package cn.com.yitong.actions.xy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.com.yitong.actions.atom.AbstractOp;
import cn.com.yitong.ares.flow.IAresSerivce;
import cn.com.yitong.framework.base.IBusinessContext;
import cn.com.yitong.util.SunEcmClient;

/**
 * @author luanyu
 * @date   2018年8月14日
 */
/**
*房产影像查询
*@author
*/
@Service
public class NetToolsXYqryOp extends AbstractOp implements IAresSerivce {
	@Autowired
	private SunEcmClient sunEcmClient;
	private Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public int execute(IBusinessContext ctx) {
		// TODO Auto-generated method stub
		logger.debug("-影像查询-run--");
		String busiSerialNo = ctx.getParam("TASK_ID");
		String busiFileType = ctx.getParam("BUSI_FILE_TYPE");
		String busiFileScanuser = ctx.getParam("BUSI_FILE_SCANUSER");
		String busiFilePageNum = ctx.getParam("BUSI_FILE_PAGENUM");
		String busiStartDate = ctx.getParam("BUSI_START_DATE");
		String batchId = ctx.getParam("BATCH_ID");
		String fileCnName = ctx.getParam("FILE_CN_NAME");
		List<Map<String, String>> query = new ArrayList<Map<String, String>>();
		List<Map<String, String>> query2 = sunEcmClient.query(batchId, busiStartDate, busiSerialNo, busiFileType, busiFileScanuser, busiFilePageNum, fileCnName);
		if (query2 != null) {

			for (Map<String, String> map3 : query2) {
				query.add(map3);
			}
		}
		ctx.setParam("LIST", query);
		return NEXT;

	}
}
