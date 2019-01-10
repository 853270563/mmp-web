package cn.com.yitong.actions.xy;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import cn.com.yitong.actions.atom.AbstractOp;
import cn.com.yitong.ares.flow.IAresSerivce;
import cn.com.yitong.framework.base.IBusinessContext;

/**
 * @author luanyu
 * @date   2018年10月15日
 */
/**
*判断是否存在
*@author
*/
@Service
public class IsExistxyOp extends AbstractOp implements IAresSerivce {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public int execute(IBusinessContext ctx) {
		// TODO Auto-generated method stub
		logger.debug("-判断是否存在-run--");
		Map queryForMap = getDao(ctx).queryForMap("xypicture.qryFileType", ctx.getParamMap());
		if (queryForMap == null) {
			ctx.setParam("IS_EXIST", false);
		} else {
			Object object = queryForMap.get("SIZE");
			if (Integer.parseInt(object.toString())==0) {
				ctx.setParam("IS_EXIST", false);
			} else {

				ctx.setParam("IS_EXIST", true);
			}
		}
		return NEXT;
	}

}
