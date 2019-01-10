package cn.com.yitong.actions.xy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import cn.com.yitong.ares.flow.IAresSerivce;
import cn.com.yitong.framework.base.IBusinessContext;
import cn.com.yitong.util.ImageUtil;

/**
 * @author luanyu
 * @date   2018年9月17日
 */
/**
*查询客户端头像
*@author
*/
@Service
public class QryHeadImgOp implements IAresSerivce {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public int execute(IBusinessContext ctx) {
		// TODO Auto-generated method stub
		logger.debug("-查询客户端头像-run--");
		String qryHeadImage = ImageUtil.qryHeadImage(ctx.getParam("USER_ID"));
		ctx.setParam("IMG_BASE64", qryHeadImage);
		return NEXT;
	}

}
