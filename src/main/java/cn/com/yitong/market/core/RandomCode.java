package cn.com.yitong.market.core;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.com.yitong.consts.AppConstants;
import cn.com.yitong.consts.SessConsts;
import cn.com.yitong.framework.base.IBusinessContext;
import cn.com.yitong.framework.core.bean.BaseControl;
import cn.com.yitong.util.YTLog;

/**
 * 一次性会话
 * 
 * @author yaoym
 * 
 */
@Controller
@RequestMapping("/common/RandomCode.do")
public class RandomCode extends BaseControl {
	private Logger logger = YTLog.getLogger(this.getClass());

	/**
	 * 一次性會話创建
	 * 
	 * @param trans_code
	 * @param request
	 * @return
	 */
	@RequestMapping(params = "method=singleSequnce")
	@ResponseBody
	public Map singleSequence(HttpServletRequest request) {
		Map<String, String> rst = new HashMap<String, String>();

		IBusinessContext busiCtx = busiCtxFactory.createBusiContext(request);
		StringBuffer bf = new StringBuffer();
		// 随机数 + 时间标记
		bf.append(randomString(2)).append(randomInt(4))
				.append(AppConstants.SPIT_CODE).append(new Date().getTime());
		String singleSeq = bf.toString();
		rst.put(SessConsts.SIGNLE_SEQ, singleSeq);
		busiCtx.saveSessionText(SessConsts.SIGNLE_SEQ, singleSeq);
		responseSuccess(rst, "success");
		if (logger.isDebugEnabled()) {
			logger.debug("singleSequence result:\t" + rst);
		}
		return rst;
	}
 

	final char[] codeSequences = { '0', '1', '2', '3', '4', '5', '6', '7', '8',
			'9' };

	/**
	 * 
	 * 随机生成6位数新密码
	 */
	private String randomInt(int length) {
		StringBuffer randomCode = new StringBuffer();
		Random random = new Random();
		for (int i = 0; i < length; i++) {
			String strRand = String.valueOf(codeSequences[random.nextInt(10)]);
			randomCode.append(strRand);
		}
		return randomCode.toString();
	}

	final char[] charSequences = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I',
			'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V',
			'W', 'X', 'Y', 'Z' };

	/**
	 * 
	 * 随机生成6位短信前缀
	 */
	public String randomString(int length) {
		StringBuffer randomCode = new StringBuffer();
		Random random = new Random();
		for (int i = 0; i < length; i++) {
			String strRand = String.valueOf(charSequences[random.nextInt(20)]);
			randomCode.append(strRand);
		}
		return randomCode.toString();
	}
}
