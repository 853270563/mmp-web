package cn.com.yitong.modules.task;

import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import cn.com.yitong.ares.dao.IbatisDao;
import cn.com.yitong.ares.flow.IFlowTool;
import cn.com.yitong.core.util.DictUtils;
import cn.com.yitong.framework.base.IBusinessContext;
import cn.com.yitong.framework.core.bean.BusinessContext;
import cn.com.yitong.framework.util.security.MD5Encrypt;
import cn.com.yitong.util.YTLog;

/**
 * @author luanyu
 * @date   2018年9月6日
 */
public class TimeOutTask {
	private Logger logger = YTLog.getLogger(this.getClass());

	@Autowired
	protected IbatisDao ibatisDao;
	@Autowired
	IFlowTool flowTool;
	public void timeOutTask() {
		IBusinessContext ctx = new BusinessContext(IBusinessContext.PARAM_TYPE_MAP);
		flowTool.execute(ctx, "task/timeOutTask");

	}

	/**
	 * 解锁
	 */
	public void unlock() {
		int errTimes = Integer.parseInt(DictUtils.getDictValue("SYS_PARAM", "ERR_LGN_CNT", "5"));
		String RESET_PASS_WORD = DictUtils.getDictValue("SYS_PARAM", "RESET_PASS_WORD", "888888");
		List<String> queryForList = ibatisDao.queryForList("task.findAllUser", errTimes);
		HashMap<String, Object> hashMap = new HashMap<String, Object>();
		hashMap.put("RESET_PASS_WORD", MD5Encrypt.MD5(RESET_PASS_WORD));
		for (String string : queryForList) {
			logger.debug("自动重置错误次数和密码 userID: " + string);
			hashMap.put("ID", string);
			ibatisDao.update("task.update", hashMap);
		}
		
	}

	public void timeOutTaskDay() {
		IBusinessContext ctx = new BusinessContext(IBusinessContext.PARAM_TYPE_MAP);
		flowTool.execute(ctx, "task/timeOutTaskDay");

	}

	public void deleteXy() {
		logger.info("删除空影像");
		ibatisDao.delete("xypicture.deleteFileType", new HashMap<Object, Object>());

	}

}
