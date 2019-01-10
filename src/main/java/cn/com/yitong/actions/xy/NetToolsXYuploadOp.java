package cn.com.yitong.actions.xy;

import java.io.File;
import java.sql.Date;
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
import cn.com.yitong.ares.error.AresCoreException;
import cn.com.yitong.ares.error.OtherRuntimeException;
import cn.com.yitong.ares.flow.IAresSerivce;
import cn.com.yitong.common.utils.ConfigUtils;
import cn.com.yitong.core.util.SecurityUtils;
import cn.com.yitong.framework.base.IBusinessContext;
import cn.com.yitong.util.StringUtil;
import cn.com.yitong.util.SunEcmClient;
import cn.com.yitong.util.common.ValidUtils;

/**
 * @author luanyu
 * @date   2018年8月14日
 */
/**
*影像平台上传
*@author
*/
@Service
public class NetToolsXYuploadOp extends AbstractOp implements IAresSerivce {
	@Autowired
	private SunEcmClient sunEcmClient;
	private Logger logger = LoggerFactory.getLogger(getClass());
	Lock lock = new ReentrantLock();
	@Override
	public int execute(IBusinessContext ctx) {
		// TODO Auto-generated method stub
		logger.debug("-影像上传-run--");
		List<String> filePaths = ctx.getParamDatas("FILE_PATHS");
		String taskId = ctx.getParam("TASK_ID");
		String busiFileType = ctx.getParam("BUSI_FILE_TYPE");
		String busiFileScanuser = "admin";
		if (SecurityUtils.getSession() != null && ConfigUtils.getValue("debug_model").equals("0")) {
			busiFileScanuser = SecurityUtils.getSession().getUserId();
		}
		String busiStartDate = ctx.getParam("BUSI_START_DATE");
		if (!busiStartDate.matches("\\d{4}(0[0-9]|1[0-2])([0-2][0-9]|3[0-1])")) {
			ctx.setParam("*msgKey", "common.parameter_valueError");
			ctx.setParam("*msgArgs", "BUSI_START_DATE");
			throwAresRuntimeException(ctx);
		}
		String contentID = null;
		try {
			lock.lock();
		List<Map<String, Object>> queryForList = getDao(ctx).queryForList("xypicture.qry", ctx.getParamMap());
			ctx.setParam("UPDATE_TIME", new Date(System.currentTimeMillis()));

		if (queryForList != null && !queryForList.isEmpty()) {
				contentID = (String) queryForList.get(0).get("BATCH_ID");
			String AMOUNT = (String) queryForList.get(0).get("AMOUNT");
				String BUSI_FILE_PAGENUM = (String) queryForList.get(0).get("BUSI_FILE_PAGENUM");
			sunEcmClient.checkOut(busiStartDate, contentID);
				String updateExample = sunEcmClient.updateExample(filePaths, taskId, busiFileType, busiFileScanuser, busiStartDate, contentID, AMOUNT,
						BUSI_FILE_PAGENUM);

				if (!"SUCCESS".equals(updateExample)) {
					throw new OtherRuntimeException("yx_upload_failure", "影像更新失败");
				}
				ctx.setParam("AMOUNT", filePaths.size() + Integer.parseInt(AMOUNT));
				ctx.setParam("BUSI_FILE_PAGENUM", filePaths.size() + (StringUtil.isEmpty(BUSI_FILE_PAGENUM) ? 0 : Integer.parseInt(BUSI_FILE_PAGENUM)));
				getDao(ctx).update("xypicture.update", ctx.getParamMap());
				Map queryForMap = getDao(ctx).queryForMap("xypicture.qryFileType", ctx.getParamMap());
				if (queryForMap == null) {

					ctx.setParam("CREATE_TIME", new Date(System.currentTimeMillis()));
					ctx.setParam("AMOUNT", filePaths.size());
					getDao(ctx).insert("xypicture.insertFileType", ctx.getParamMap());
				} else {
					Object object = queryForMap.get("SIZE");
					ctx.setParam("AMOUNT", filePaths.size() + Integer.parseInt(object.toString()));
					getDao(ctx).update("xypicture.updateFileType", ctx.getParamMap());
				}
		} else {

				contentID = sunEcmClient.upload(filePaths, taskId, busiFileType, busiFileScanuser, busiStartDate);
				if (StringUtil.isNotEmpty(contentID)) {
					ctx.setParam("BATCH_ID", contentID);
					ctx.setParam("CREATE_TIME", new Date(System.currentTimeMillis()));
					String statementName = ValidUtils.validEmpty("*sqlId", ctx.getParamMap());
					ctx.setParam("AMOUNT", filePaths.size());
					ctx.setParam("BUSI_FILE_SCANUSER", busiFileScanuser);
					ctx.setParam("BUSI_FILE_PAGENUM", filePaths.size());
					getDao(ctx).insert(statementName, ctx.getParamMap());
					getDao(ctx).insert("xypicture.insertFileType", ctx.getParamMap());

				} else {
					throw new AresCoreException("yx_upload_failure", "影像新增失败");
				}
		}
			ArrayList<Map<String, Object>> temp = new ArrayList<Map<String, Object>>();//返回报文
		for (String path : filePaths) {
			if (new File(path).delete()) {
					logger.info("影像上传成功，删除本地图片{}", path);
			}
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("FILE_CN_NAME", new File(path).getName());
				map.put("BUSI_FILE_TYPE", busiFileType);
				map.put("BATCH_ID", contentID);
				map.put("BUSI_START_DATE", busiStartDate);
				temp.add(map);
		}
			ctx.setParam("LIST", temp);
		} finally {
			if (contentID != null) {

				sunEcmClient.checkIn(busiStartDate, contentID);
			}
			lock.unlock();
		}


		return NEXT;
	}

}
