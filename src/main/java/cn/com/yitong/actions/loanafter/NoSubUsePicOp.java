package cn.com.yitong.actions.loanafter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import cn.com.yitong.actions.atom.AbstractOp;
import cn.com.yitong.ares.flow.IAresSerivce;
import cn.com.yitong.framework.base.IBusinessContext;

/**
 * 未上传影像的用户
 */
@Component
public class NoSubUsePicOp extends AbstractOp implements IAresSerivce {

	protected Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public int execute(IBusinessContext ctx) {
		logger.debug("--未上传影像的用户run--");
		List<Map> receiptdetaillist = (List) ctx.getParamMap().get("RECEIPTDETALIST");
		List<Map> nousepiclist = null;
		if (receiptdetaillist!=null) {
			nousepiclist = new ArrayList<Map>();
			for (int i = 0, k = receiptdetaillist.size(); i < k; i++) {
				Map map = receiptdetaillist.get(i);
				List<Map> imagelist = (List) map.get("LIST");
				if (imagelist == null||imagelist.size() == 0) {
					nousepiclist.add(map);
				}
			}
		}
		ctx.getParamMap().put("RECEIPTDETALIST", nousepiclist);
		return NEXT;
	}
}