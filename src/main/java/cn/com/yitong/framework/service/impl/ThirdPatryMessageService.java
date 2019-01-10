package cn.com.yitong.framework.service.impl;

import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import cn.com.yitong.consts.AppConstants;
import cn.com.yitong.framework.base.IBusinessContext;
import cn.com.yitong.framework.net.INetTools;
import cn.com.yitong.framework.net.impl.credit.CreditConst;
import cn.com.yitong.framework.net.impl.esb.ESBConst;
import cn.com.yitong.framework.service.IThirdPatryMessageService;
import cn.com.yitong.util.MessageTools;
import cn.com.yitong.util.YTLog;
/**
 * 接口定义的实现
 * @author ygh@yitong.com.cn
 */
@Service
public class ThirdPatryMessageService implements IThirdPatryMessageService {
	private Logger logger = YTLog.getLogger(ThirdPatryMessageService.class);
	//信贷系统
	@Autowired
	@Qualifier("netTools4credit")
	private INetTools netTools4credit;
	//esb联盟
	@Autowired
	@Qualifier("netTools4esb")
	private INetTools netTools4esb;
	
	@Override
	public boolean creditLoanSystem(IBusinessContext busiCtx, String transCode, Map rst) {
		if (netTools4credit == null) {
			// 通讯组件加载未设置
			busiCtx.setErrorInfo(AppConstants.STATUS_FAIL, "NetTool通讯组件未设置！",
					transCode);
			return false;
		}
		boolean result = netTools4credit.execute(busiCtx, transCode);
		MessageTools.elementToMap(busiCtx.getResponseContext(transCode), rst);
		if (result && !CreditConst.RS_COMPLETE.equalsIgnoreCase((String) rst.get(CreditConst.RS_STATUS))) {
			busiCtx.setErrorInfo(AppConstants.STATUS_FAIL, (String) rst.get(ESBConst.RS_DESC), transCode);
			rst.put(AppConstants.MSG, rst.get(ESBConst.RS_DESC));
			rst.put(AppConstants.STATUS, AppConstants.STATUS_FAIL);
			return false;
		} else if (!result) {
			rst.put(AppConstants.STATUS, AppConstants.STATUS_FAIL);
			rst.put(AppConstants.MSG, rst.get(AppConstants.MSG));

		} else {
			rst.put(AppConstants.STATUS, AppConstants.STATUS_OK);
			rst.put(AppConstants.MSG, AppConstants.MSG_SUCC);
		}
		return result;
	}

	@Override
	public boolean esbUnionSystem(IBusinessContext busiCtx, String transCode, Map<String, Object> rst) {
		if (netTools4esb == null) {
			// 通讯组件加载未设置
			busiCtx.setErrorInfo(AppConstants.STATUS_FAIL, "NetTool通讯组件未设置！", transCode);
			return false;
		}
		boolean result = netTools4esb.execute(busiCtx, transCode);
		MessageTools.elementToMap(busiCtx.getResponseContext(transCode), rst);
		if (result && !ESBConst.RS_COMPLETE.equalsIgnoreCase((String) rst.get(ESBConst.RS_STATUS))) {
			busiCtx.setErrorInfo(AppConstants.STATUS_FAIL, (String) rst.get(ESBConst.RS_DESC), transCode);
			return false;
		}
		return result;
	}



	
	
	

}
