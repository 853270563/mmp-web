package cn.com.yitong.actions.xy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import cn.com.yitong.ares.flow.IAresSerivce;
import cn.com.yitong.framework.base.IBusinessContext;
import cn.com.yitong.util.ImageUtil;

/**
 * @author luanyu
 * @date   2018年8月30日
 */
/**
*上传到本地
*@author
*/
@Service
public class YxLocaluploadOp2 implements IAresSerivce {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public int execute(IBusinessContext ctx) {
		// TODO Auto-generated method stub
		logger.debug("-上传到本地-run--");
		List<Map<String, Object>> paramDatas = ctx.getParamDatas("LIST");
		String BUSI_SERIAL_NO = ctx.getParam("BATCH_ID");
		String BUSI_FILE_TYPE = ctx.getParam("BUSI_FILE_TYPE");
		List<String> imagePaths = new ArrayList<String>();
		for (Map<String, Object> map : paramDatas) {

			String path = ImageUtil.GenerateImage((String) map.get("IMG_BASE64"), BUSI_SERIAL_NO, BUSI_FILE_TYPE);
			imagePaths.add(path);
		}
		ctx.removeParam("LIST");
		logger.debug("上传到本地成功：{}", imagePaths);
		ctx.setParam("FILE_PATHS", imagePaths);
		return NEXT;
	}

}
