package cn.com.yitong.actions.atom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.com.yitong.ares.flow.IAresSerivce;
import cn.com.yitong.framework.base.IBusinessContext;
import cn.com.yitong.framework.net.impl.common.EBankConfParser;
import cn.com.yitong.framework.net.impl.common.RequestBuilder;
import cn.com.yitong.util.common.ValidUtils;

/**
 * @author luanyu
 * @date   2018年8月15日
 */
/**
*请求参数检查
*@author
*/
@Service
public class RequestCheckOp implements IAresSerivce {
	@Autowired
	private RequestBuilder requestBuilder;
	@Autowired
	private EBankConfParser eBankConfParser;
	private Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public int execute(IBusinessContext ctx) {
		// TODO Auto-generated method stub
		logger.debug("-请求参数检查-run--");
		String transCode = ValidUtils.validEmpty("*transCode", ctx.getParamMap());
		requestBuilder.buildSendMessage(ctx, eBankConfParser, transCode);
		return NEXT;
	}

}
