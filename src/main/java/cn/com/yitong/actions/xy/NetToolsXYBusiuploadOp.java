package cn.com.yitong.actions.xy;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.com.yitong.actions.atom.AbstractOp;
import cn.com.yitong.ares.error.OtherRuntimeException;
import cn.com.yitong.ares.flow.IAresSerivce;
import cn.com.yitong.common.utils.ConfigUtils;
import cn.com.yitong.core.util.SecurityUtils;
import cn.com.yitong.framework.base.IBusinessContext;
import cn.com.yitong.util.SunEcmClient;

/**
 * @author luanyu
 * @date   2018年8月16日
 */
/**
*贷后影像上传
*@author
*/
@Service
public class NetToolsXYBusiuploadOp extends AbstractOp implements IAresSerivce {
	@Autowired
	private SunEcmClient sunEcmClient;
	private Logger logger = LoggerFactory.getLogger(getClass());
	Lock lock = new ReentrantLock();
	@Override
	public int execute(IBusinessContext ctx) {
		// TODO Auto-generated method stub
		logger.debug("-贷后影像上传-run--");
		List<String> filePaths = ctx.getParamDatas("FILE_PATHS");
		String busiFileScanuser = "admin";
		if (SecurityUtils.getSession() != null && ConfigUtils.getValue("debug_model").equals("0")) {
			busiFileScanuser = SecurityUtils.getSession().getUserId();
		}
		String contentID = null;
		String busiStartDate = null;
		try {
			lock.lock();
			contentID = ctx.getParam("BATCH_ID");
			busiStartDate = ctx.getParam("BUSI_START_DATE");
			String AMOUNT = ctx.getParam("AMOUNT");
			String busiFileType = ctx.getParam("BUSI_FILE_TYPE");
				sunEcmClient.checkOut(busiStartDate, contentID);
			String updateExample = sunEcmClient.updateExample(filePaths, null, busiFileType, busiFileScanuser, busiStartDate, contentID, AMOUNT, AMOUNT);

				if (!"SUCCESS".equals(updateExample)) {
					throw new OtherRuntimeException("yx_upload_failure", "影像更新失败");
				}
			ArrayList<Map<String, Object>> arrayList = new ArrayList<Map<String, Object>>();
			for (String path : filePaths) {
				HashMap<String, Object> hashMap = new HashMap<String, Object>();
				if (new File(path).delete()) {
					logger.info("影像上传成功，删除本地图片{}", path);
				}
				hashMap.put("FILE_CN_NAME", new File(path).getName());
				hashMap.put("BUSI_FILE_TYPE", busiFileType);
				hashMap.put("BATCH_ID", contentID);
				hashMap.put("BUSI_START_DATE", busiStartDate);
				arrayList.add(hashMap);

			}
			ctx.setParam("LIST", arrayList);
		} finally {
			if (contentID != null) {

				sunEcmClient.checkIn(busiStartDate, contentID);
			}
			lock.unlock();
		}

		return NEXT;
	}

}
