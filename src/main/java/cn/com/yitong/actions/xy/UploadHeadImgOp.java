package cn.com.yitong.actions.xy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import cn.com.yitong.ares.flow.IAresSerivce;
import cn.com.yitong.framework.base.IBusinessContext;
import cn.com.yitong.util.ImageUtil;

/**
 * @author luanyu
 * @date   2018年9月14日
 */
/**
*上传头像
*@author
*/
@Service
public class UploadHeadImgOp implements IAresSerivce {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public int execute(IBusinessContext ctx) {
		// TODO Auto-generated method stub
		logger.debug("-上传头像-run--");
		ImageUtil.GenerateHeadImage(ctx.getParam("USER_ID"), ctx.getParam("IMG_BASE64"));
		return NEXT;
	}

}
