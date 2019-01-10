package cn.com.yitong.actions;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import cn.com.yitong.actions.atom.AbstractOp;
import cn.com.yitong.ares.flow.IAresSerivce;
import cn.com.yitong.framework.base.IBusinessContext;

/**
 * 改变公告图片和pdfURL
 */
@Component
public class ChangePropaFileUrlOp extends AbstractOp implements IAresSerivce {

	protected Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public int execute(IBusinessContext ctx) {
		logger.debug("--改变图片URLrun--");
		List<Map> rs = ctx.getParamDatas("LIST");
//		logger.debug(rs.get(0).toString());
		if (rs != null || rs.size() != 0) {
		for (Map map : rs) {
			map.put("PROPA_PIC_ADDR","download/userResource/file.do?fileName="+map.get("PROPA_PIC_ADDR"));
			map.put("FILE_ADDR","download/userResource/file.do?fileName="+map.get("FILE_ADDR"));
			}
		}
		ctx.setParam("LIST", rs);
		return NEXT;
	}
}
