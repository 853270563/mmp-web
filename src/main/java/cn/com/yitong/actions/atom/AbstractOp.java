package cn.com.yitong.actions.atom;

import org.springframework.beans.factory.annotation.Autowired;

import cn.com.yitong.ares.dao.IbatisDao;
import cn.com.yitong.ares.error.AresRuntimeException;
import cn.com.yitong.ares.flow.IAresSerivce;
import cn.com.yitong.framework.base.IBusinessContext;
import cn.com.yitong.framework.core.bean.BusinessContext;
import cn.com.yitong.util.common.StringUtil;

/**
 * 支持多数据源适配 op抽象类
 */
public abstract class AbstractOp implements IAresSerivce {

	@Autowired
	protected IbatisDao ibatisDao;

	/**
	 * 获得多数据源操作类
	 * 
	 * @param ctx
	 * @return
	 */
	protected IbatisDao getDao(IBusinessContext ctx) {
		// 数据库名称
		String dbName = ctx.getParam("*dbName");
		if (StringUtil.isEmpty(dbName)) {
			return ibatisDao;
		} else if ("DB_X".equalsIgnoreCase(dbName)) {
			// 根据数据名称来适配对应的操作类
			return ibatisDao;
		}
		return ibatisDao;
	}

	/*
	 * 方法作用：1：方法执行时不影响当前的总线 2：需要通过总线的错误码判断交易是否成功、失败
	 * 
	 * @param ctx
	 * 
	 * @return
	 */
	public IBusinessContext call(IBusinessContext ctx) {
		IBusinessContext tmpCtx = new BusinessContext();
		tmpCtx.getParamMap().putAll(ctx.getParamMap());
		//	tmpCtx.getHeadMap().putAll(ctx.getHeadMap());
		try {
			execute(tmpCtx);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tmpCtx;
	}

	protected void throwAresRuntimeException(IBusinessContext ctx) {
		String msgKey = ctx.getParam("*msgKey", "common.error.undefined");
		String msgArgs = ctx.getParam("*msgArgs", "");
		throw new AresRuntimeException(msgKey, msgArgs);
	}

}
