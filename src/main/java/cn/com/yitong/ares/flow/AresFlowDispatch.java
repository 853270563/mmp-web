package cn.com.yitong.ares.flow;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import cn.com.yitong.ares.error.AresRuntimeException;
import cn.com.yitong.ares.flow.plugin.ITransationBeginOp;
import cn.com.yitong.ares.flow.plugin.ITransationEndOp;
import cn.com.yitong.ares.flow.plugin.ITransationOp;
import cn.com.yitong.common.utils.SpringContextUtils;
import cn.com.yitong.framework.base.IBusinessContext;
import cn.com.yitong.util.common.StringUtil;

/**
 * 服务编排组件
 * 
 * @author yjl
 * @author yaoym
 */
public class AresFlowDispatch extends AresFlowData implements IAresFlowDispatch {

	private Logger logger = LoggerFactory.getLogger(getClass());
	private List<AresServiceStep> steps = new ArrayList<AresServiceStep>();// 流程节点

	private Map<Integer, AresServiceStep> mapping = new HashMap<Integer, AresServiceStep>();// 节点序列

	private String defineName;
	private IAresFlowDefine flowDefine;
	private int finallyStartIndex = IAresSerivce.EXIT;
	ThreadLocal<Map<String, TransactionStatus>> threadLocal = new ThreadLocal<Map<String, TransactionStatus>>();

	/**
	 * 初始化定义
	 */
	@Override
	public void init() {
		// 加载定义
		flowDefine.parserDefine(defineName, this);
		int stepIndex = 1;
		for (AresServiceStep step : steps) {
			if (finallyStartIndex == IAresSerivce.EXIT) {
				// 初始化Finally 起始节点序号
				if (step.isFinallyStep()) {
					finallyStartIndex = stepIndex;
				}
			}
			mapping.put(stepIndex++, step);
		}
	}

	@Override
	public int execute(IBusinessContext ctx) {
		try {
			// 执行预定义代码
			evalPrevDatas(ctx);
			// 依次执行服务
			auto(ctx, 1);
		} catch (AresRuntimeException e) {
			throw e;
		} catch (Exception e2) {
			logger.error("FlowDispatch.Finally.Error{}", e2);
			throw new AresRuntimeException("FlowDispatch.Execute.Error", e2.toString());
		} finally {
			try {
				autoFinallyFlow(ctx, finallyStartIndex);
			} catch (Exception e) {
				logger.error("FlowDispatch.Finally.Error{}", e);
			} finally {
				// 事务回滚--补刀步骤
				transationRollback(ctx);
			}
		}
		return NEXT;
	}

	/**
	 * 事务回滚
	 * 
	 * @param ctx
	 */
	private void transationRollback(IBusinessContext ctx) {
		Map<String, TransactionStatus> maps = threadLocal.get();
		if (null == maps) {
			return;
		}
		Collection<String> keys = maps.keySet();
		for (String key : keys) {
			TransactionStatus status = maps.get(key);
			if (status != null && !status.isCompleted()) {
				logger.info("事务未正常结束，强制结束事务，{}", key);
				try {
					DataSourceTransactionManager transactionManager = getTransManagerBean(key);
					transactionManager.rollback(status);
				} catch (Exception e) {
					logger.warn("rollback datasource {}", key, e);
				}
			}
		}
		threadLocal.remove();
	}

	/**
	 * 依次执行Finally节点,需捕获所有异常，不能抛出
	 * 
	 * @param ctx
	 * @param startIndex
	 */
	private void autoFinallyFlow(IBusinessContext ctx, int stepIndex) {
		try {
			if (stepIndex <= IAresSerivce.EXIT)
				return;
			AresServiceStep step = mapping.get(stepIndex);
			if (step == null) {
				return;
			}
			stepIndex = step.run(ctx);
			autoFinallyFlow(ctx, stepIndex);
		} catch (Exception e) {
			logger.error("InnerFinallyFlow.error", e);
		}
	}

	private DataSourceTransactionManager getTransManagerBean(String key) {
		return SpringContextUtils.getBean(key, DataSourceTransactionManager.class);
	}

	private Map<String, TransactionStatus> getTransMap(boolean create) {
		Map<String, TransactionStatus> maps = threadLocal.get();
		if (null == maps) {
			if (create) {
				maps = new HashMap<String, TransactionStatus>();
				threadLocal.set(maps);
			}
		}
		return maps;
	}

	/**
	 * 事务提交
	 * 
	 * @param ctx
	 */
	private void transationCommit(IBusinessContext ctx) {
		String beanId = getTransmanagerBeanName(ctx);
		Map<String, TransactionStatus> maps = getTransMap(false);
		if (null == maps) {
			return;
		}
		TransactionStatus status = maps.get(beanId);
		DataSourceTransactionManager transactionManager = getTransManagerBean(beanId);
		try {
			transactionManager.commit(status);
		} catch (Exception e) {
			transactionManager.rollback(status);
		} finally {
			maps.remove(beanId);
			ctx.removeParam(PRARAM_DB_NAME);
		}
	}

	/**
	 * 事务开启
	 * 
	 * @param ctx
	 */
	private void transationBegin(IBusinessContext ctx) {
		String beanId = getTransmanagerBeanName(ctx);
		try {
			DataSourceTransactionManager transactionManager = getTransManagerBean(beanId);
			// 创建事务
			Map<String, TransactionStatus> maps = getTransMap(true);
			DefaultTransactionDefinition def = new DefaultTransactionDefinition();
			def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED); // 事物隔离级别，开启新事务
			TransactionStatus status = transactionManager.getTransaction(def); // 获得事务状态
			maps.put(beanId, status);
		} finally {
			ctx.removeParam(PRARAM_DB_NAME);
		}
	}

	private final String PRARAM_DB_NAME = "*dbName";

	private String getTransmanagerBeanName(IBusinessContext ctx) {
		String dbName = ctx.getParam(PRARAM_DB_NAME);
		return String.format("transactionManager%s", StringUtil.isEmpty(dbName) ? "" : dbName);
	}

	/**
	 * 内部流转
	 * 
	 * @param ctx
	 * @param stepIndex
	 * @param mapping
	 * @return
	 * @throws Exception
	 */
	private void auto(IBusinessContext ctx, int stepIndex) {
		if (stepIndex <= IAresSerivce.EXIT)
			return;
		AresServiceStep step = mapping.get(stepIndex);
		if (step == null) {
			return;
		}
		if (stepIndex == finallyStartIndex) {
			// 普通节点结束，进入Finally 流程
			return;
		}
		if (step.getAresService() instanceof ITransationOp) {
			step.evalPrevDatas(ctx);
			if (step.getAresService() instanceof ITransationBeginOp) {
				transationBegin(ctx);
			} else if (step.getAresService() instanceof ITransationEndOp) {
				transationCommit(ctx);
			}
		}
		stepIndex = step.run(ctx);
		auto(ctx, stepIndex);
	}

	@Override
	public List<AresServiceStep> getSteps() {
		return steps;
	}

	@Override
	public void setSteps(List<AresServiceStep> steps) {
		this.steps = steps;
	}

	public String getDefineName() {
		return defineName;
	}

	@Override
	public void setDefineName(String defineName) {
		this.defineName = defineName;
	}

	public IAresFlowDefine getFlowDefine() {
		return flowDefine;
	}

	@Override
	public void setFlowDefine(IAresFlowDefine flowDefine) {
		this.flowDefine = flowDefine;
	}

}