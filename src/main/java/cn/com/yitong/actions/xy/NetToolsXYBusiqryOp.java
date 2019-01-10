package cn.com.yitong.actions.xy;

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
 * @date   2018年8月16日
 */
/**
*业务影像查询
*@author
*/
@Service
public class NetToolsXYBusiqryOp extends AbstractOp implements IAresSerivce {

	private Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	private SunEcmClient sunEcmClient;
	@Override
	public int execute(IBusinessContext ctx) {
		// TODO Auto-generated method stub
		logger.debug("-业务影像查询-run--");
		String busiSerialNo = ctx.getParam("TASK_ID");
		String busiFileType = ctx.getParam("BUSI_FILE_TYPE");
		String busiFileScanuser = ctx.getParam("BUSI_FILE_SCANUSER");
		String busiFilePageNum = ctx.getParam("BUSI_FILE_PAGENUM");
		String busiStartDate = ctx.getParam("BUSI_START_DATE");
		String batchId = ctx.getParam("BATCH_ID");
		String fileCnName = ctx.getParam("FILE_CN_NAME");
		List<Map<String, String>> urls = sunEcmClient.query(batchId, busiStartDate, busiSerialNo, busiFileType, busiFileScanuser, busiFilePageNum, fileCnName);

		ctx.setParam("LIST", urls);

		return NEXT;
	}

}
