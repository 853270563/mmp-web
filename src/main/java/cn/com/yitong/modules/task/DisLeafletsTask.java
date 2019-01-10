package cn.com.yitong.modules.task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import cn.com.yitong.actions.xy.NetToolsXYuploadOp;
import cn.com.yitong.framework.base.IBusinessContext;
import cn.com.yitong.framework.core.bean.BusinessContext;
import cn.com.yitong.framework.net.INetTools;
import cn.com.yitong.framework.service.ICrudService;
import cn.com.yitong.util.DateUtil;
import cn.com.yitong.util.ImageUtil;

@Service
public class DisLeafletsTask {

	protected Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	@Qualifier("netToolsAs")
	INetTools netTools;

	@Autowired
	NetToolsXYuploadOp xyUploadOp;

	@Autowired
	ICrudService service;

	public void ptask() {
		logger.debug("----派单业务详情定时查询开始----");
		String pathid;// 任务id，或者房产id
		// Map paraMap = new HashMap();
		IBusinessContext ctx = new BusinessContext(
				IBusinessContext.PARAM_TYPE_MAP);

		// 查询任务id对应的业务id
		List<Map> busidList = service.findList(
				"TASK_BUSI_RELATION.queryBusidList", null);
		logger.info("业务id列表：" + busidList.toString());

		// 调用业务详情查询接口
		String transCode = "as/getentrustdetail";
		if (null != busidList && !busidList.isEmpty()) {
			for (Map map : busidList) {
				 String applyTime = (String) map.get("CREATE_TIME");
				 applyTime = DateUtil.fmtmatdate(applyTime);
//				String applyTime = "20180905";
				String taskId = (String) map.get("TASKID");
				String busiId = (String) map.get("BUSIID");
				ctx.getParamMap().put("entrustId", map.get("BUSIID"));
				// paraMap.put("entrustId", map.get("BUSIID"));
				// ctx.setParamMap(paraMap);
				netTools.execute(ctx, transCode);
				boolean updateFlag = false;// 业务详情查询标识更新标志
				// 组装业务详情返回参数
				List<Map> recList = TaskService.buildRecMap(ctx.getParamMap());
				logger.info("业务详情返回参数："+recList);
				for (Map recMap : recList) {
					recMap.put("BUSIID", busiId);
					// 判断业务是否已经完成
					if (recMap.get("stateCode").equals("5016004") || recMap.get("stateCode").equals("5016016")) {
						// 获取查勘图片
						String busiCode = "as/getSurveyPhotos";
						// 清空数据总线
						ctx.getParamMap().clear();
						ctx.getParamMap().put("objectid",
								recMap.get("OBJECTID"));
						netTools.execute(ctx, busiCode);
						List<Map> photoList = (List<Map>) ctx.getParamMap()
								.get("surveyReturnList");
						for (Map photoMap : photoList) {
							List<String> filePath = new ArrayList<String>();
							String imageCode = (String) photoMap
									.get("imageCode");
							if (!imageCode.isEmpty() && imageCode != "") {
								String objId = (String) recMap.get("OBJECTID");
								String path = (String) photoMap.get("path");
								if (imageCode.substring(0,4).equals("1011")
										|| imageCode.substring(0,4)
												.equals("1022")
										|| imageCode.substring(0,4)
												.equals("1033")
										|| imageCode.substring(0,4)
												.equals("1044")
										|| imageCode.substring(0,4)
												.equals("1055")) {
									pathid = objId;
								} else {
									pathid = taskId;
								}
								// 下载图片
								String imgTransCode = "as/image";
								ctx.getParamMap().put("imgUrl", path);
								ctx.getParamMap().put("isurl", "1");
								logger.info("调下载图片前的数据总线===="
										+ ctx.getParamMap());
								netTools.execute(ctx, imgTransCode);
								ctx.getParamMap().put("isurl", "0");
								String imgFilePath = ImageUtil.GenerateImage(
										ctx.getParam("base64"), pathid,
										imageCode);
								// 上传图片到影像平台
								ctx.getParamMap().put("TASK_ID", taskId);
								ctx.getParamMap().put("BUSI_SERIAL_NO", pathid);
								ctx.getParamMap().put("BUSI_START_DATE",
										applyTime);
								ctx.getParamMap().put("BUSI_FILE_TYPE",
										imageCode);
								filePath.add(imgFilePath);
								ctx.getParamMap().put("FILE_PATHS", filePath);
								ctx.getParamMap().put("*sqlId",
										"xypicture.insert");
								xyUploadOp.execute(ctx);
							}
						}
						// 业务详情入库
						service.insert("BUSI_DETAILS.insertBusiDetails", recMap);
						updateFlag = true;
						// 更新任务业务关联表状态
						// recMap.put("FLAG", "1");
						// service.update("TASK_BUSI_RELATION.updateBusidState",
						// recMap);
					}
				}
				if (updateFlag) {
					Map prams = new HashMap<String, Object>();
					// 更新任务业务关联表状态
					prams.put("FLAG", "1");
					prams.put("BUSIID", busiId);
					service.update("TASK_BUSI_RELATION.updateBusidState", prams);
					//更新派单状态
					prams.put("BAR_CODE", taskId);
					prams.put("STATE", "02");
					service.update("MY_DISPACH.updateState", prams);
				}
			}
		}

	}

}
