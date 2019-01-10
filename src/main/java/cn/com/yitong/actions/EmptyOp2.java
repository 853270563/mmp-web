package cn.com.yitong.actions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import cn.com.yitong.ares.flow.IAresSerivce;
import cn.com.yitong.framework.base.IBusinessContext;

/**
 * @author luanyu
 * @date   2018年6月16日
 */
/**
*简单任务
*@author
*/
@Service
public class EmptyOp2 implements IAresSerivce {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public int execute(IBusinessContext ctx) {
		// TODO Auto-generated method stub
		logger.debug("-简单任务2-run--");
		ctx.setParam("key", "測試flow");
		return NEXT;
	}

}
