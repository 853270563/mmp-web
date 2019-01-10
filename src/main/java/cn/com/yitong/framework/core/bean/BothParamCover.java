package cn.com.yitong.framework.core.bean;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.com.yitong.framework.base.IBusinessContext;
import cn.com.yitong.framework.net.IParamCover;
import cn.com.yitong.util.YTLog;


public class BothParamCover implements IParamCover {
	@SuppressWarnings("unused")
	private Logger logger = YTLog.getLogger(this.getClass());

	@Autowired
	private IParamCover jsonParamCover;
	@Autowired
	private IParamCover jspParamCover;

	
	public boolean cover(IBusinessContext context, HttpServletRequest request,
			boolean signed, String transCode) throws Exception {
		if (null == request || context == null)
			return false;

		jsonParamCover.cover(context, request, signed, transCode);
		jspParamCover.cover(context, request, signed, transCode);

		return true;
	}

}
