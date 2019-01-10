package cn.com.yitong.actions.xy;

import java.sql.Date;
import java.util.ArrayList;
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
import cn.com.yitong.framework.base.IBusinessContext;
import cn.com.yitong.util.SunEcmClient;

/**
 * @author luanyu
 * @date   2018年8月31日
 */
/**
*h5删除影像
*@author
*/
@Service
public class H5delpictureOp extends AbstractOp implements IAresSerivce {

	private Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	private SunEcmClient sunEcmClient;
	Lock lock = new ReentrantLock();
	@Override
	public int execute(IBusinessContext ctx) {
		// TODO Auto-generated method stub
		logger.debug("-h5删除影像-run--");
		List<Map<String, Object>> list = ctx.getParamDatas("LIST");
		String busiSerialNo = ctx.getParam("TASK_ID");
		List<Map<String, Object>> query = new ArrayList<Map<String, Object>>();
		ctx.setParam("UPDATE_TIME", new Date(System.currentTimeMillis()));
		for (Map<String, Object> map : list) {
			String busiStartDate = (String) map.get("BUSI_START_DATE");
			String batchId = (String) map.get("BATCH_ID");
			String AMOUNT = (String) map.get("AMOUNT");
			try {

				lock.lock();
			sunEcmClient.checkOut(busiStartDate, batchId);
			String delete = sunEcmClient.delete(batchId, busiStartDate, ctx.getParam("FILE_NO"), busiSerialNo, AMOUNT);

			if (!"SUCCESS".equals(delete)) {
				throw new OtherRuntimeException("yx_delete_failure", "影像删除失败");
			}
			ctx.setParam("AMOUNT", Integer.parseInt(AMOUNT) - 1);
			getDao(ctx).update("xypicture.update", ctx.getParamMap());
				Map queryForMap = getDao(ctx).queryForMap("xypicture.qryFileType", ctx.getParamMap());
				if (queryForMap != null) {

					Object object = queryForMap.get("SIZE");
						ctx.setParam("AMOUNT", Integer.parseInt(object.toString()) - 1);
						getDao(ctx).update("xypicture.updateFileType", ctx.getParamMap());
				}
			} finally {
				sunEcmClient.checkIn(busiStartDate, batchId);
				lock.unlock();
			}
		}
		return NEXT;
	}

}
