package cn.com.yitong.actions.xy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.com.yitong.ares.flow.IAresSerivce;
import cn.com.yitong.framework.base.IBusinessContext;
import cn.com.yitong.util.SunEcmClient;

/**
 * @author luanyu
 * @date   2018年8月30日
 */
/**
*查询影像列表
*@author
*/
@Service
public class H5ImgQryOp implements IAresSerivce {

	private Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	private SunEcmClient sunEcmClient;
	@Override
	public int execute(IBusinessContext ctx) {
		// TODO Auto-generated method stub
		logger.debug("-查询影像列表-run--");
		List<Map<String, Object>> list = ctx.getParamDatas("LIST");
		String busiSerialNo = ctx.getParam("TASK_ID");
		String busiFileType = ctx.getParam("BUSI_FILE_TYPE");
		List<Map<String, String>> query = new ArrayList<Map<String, String>>();
		for (Map<String, Object> map : list) {
			String busiStartDate = (String) map.get("BUSI_START_DATE");
			String batchId = (String) map.get("BATCH_ID");
			String BUSI_FILE_SCANUSER = (String) map.get("BUSI_FILE_SCANUSER");
			List<Map<String, String>> query2 = sunEcmClient.query(batchId, busiStartDate, busiSerialNo, busiFileType, BUSI_FILE_SCANUSER, null, null);
			if (query2 != null) {

				for (Map<String, String> map3 : query2) {
					query.add(map3);
				}
			}
		}
		ctx.setParam("LIST", query);
		return NEXT;
	}

}
