package cn.com.yitong.actions.apply;

import java.sql.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import cn.com.yitong.ares.flow.IAresSerivce;
import cn.com.yitong.framework.base.IBusinessContext;

/**
 * @author luanyu
 * @date   2018年8月29日
 */
/**
*完善数据
*@author
*/
@Service
public class FinishDataOp implements IAresSerivce {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public int execute(IBusinessContext ctx) {
		// TODO Auto-generated method stub
		logger.debug("-完善数据-run--");
		ctx.setParam("RUSH_TIME", new Date(System.currentTimeMillis()));
		return NEXT;
	}

}
