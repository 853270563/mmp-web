package cn.com.yitong.actions.seq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import cn.com.yitong.ares.flow.IAresSerivce;
import cn.com.yitong.framework.base.IBusinessContext;
import cn.com.yitong.util.RandomUtil;
import cn.com.yitong.util.common.StringUtil;
import cn.com.yitong.util.common.ValidUtils;

/**
 * 生成主交易流水
 * 
 * @author yaoym
 * 
 */
@Component
public class SeqCreateOp implements IAresSerivce {

	private Logger logger = LoggerFactory.getLogger(getClass());
	
	@Override
	public int execute(IBusinessContext ctx) {
		String seqKey = ValidUtils.validEmpty("*SEQ_KEY", ctx.getParamMap());
		String[] vars = seqKey.split(",|，|\\s+");
		for (String var : vars) {
			if (StringUtil.isEmpty(var)) {
				continue;
			}
			String seqNum = RandomUtil.createTransSeq();
			ctx.setParam(var, seqNum);
		}
		return NEXT;
	}
}
