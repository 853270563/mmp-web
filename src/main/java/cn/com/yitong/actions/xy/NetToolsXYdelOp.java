package cn.com.yitong.actions.xy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.com.yitong.ares.flow.IAresSerivce;
import cn.com.yitong.framework.base.IBusinessContext;
import cn.com.yitong.util.SunEcmClient;

/**
 * @author luanyu
 * @date   2018年8月14日
 */
/**
*房产影像删除
*@author
*/
@Service
public class NetToolsXYdelOp implements IAresSerivce {
	@Autowired
	private SunEcmClient sunEcmClient;
	private Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public int execute(IBusinessContext ctx) {
		// TODO Auto-generated method stub
		logger.debug("-房产影像删除-run--");
		String busiSerialNo = ctx.getParam("BUSI_SERIAL_NO");
		String busiFileType = ctx.getParam("BUSI_FILE_TYPE");
		String busiFileScanuser = ctx.getParam("BUSI_FILE_SCANUSER");
		String busiFilePageNum = ctx.getParam("BUSI_FILE_PAGENUM");
		String busiStartDate = ctx.getParam("BUSI_START_DATE");
		String batchId = ctx.getParam("BATCH_ID");
		String fileCnName = ctx.getParam("FILE_CN_NAME");
		sunEcmClient.checkOut(busiStartDate, batchId);
		sunEcmClient.delete(batchId, busiStartDate, ctx.getParam("FILE_NO"), busiSerialNo, "");
		sunEcmClient.checkIn(busiStartDate, batchId);
		return NEXT;
	}

}
