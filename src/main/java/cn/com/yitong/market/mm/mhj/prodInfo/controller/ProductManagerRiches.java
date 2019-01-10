package cn.com.yitong.market.mm.mhj.prodInfo.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.com.yitong.framework.base.IBusinessContext;
import cn.com.yitong.framework.core.bean.BaseControl;
import cn.com.yitong.framework.service.ICrudService;
import cn.com.yitong.framework.util.CtxUtil;

@Controller
public class ProductManagerRiches extends BaseControl{
	private Logger logger = Logger.getLogger(ProductManagerRiches.class);

	@Autowired
	ICrudService service;

	@RequestMapping("mm/mhj/productManagerRiches/productManagerRiches.do")
	@ResponseBody
	public Map exeute(HttpServletRequest req){
		
		IBusinessContext ctx = CtxUtil.createMapContext(req);
		ctx.initParamCover(json2MapParamCover, false);
		
		Map rst = new HashMap();
		Map NewRst = new HashMap();
		String USER_ID = ctx.getParam("LGN_ID");
		NewRst.put("USER_ID", USER_ID);
		
		try{
			List list = service.findList("INVEST_INFO.PRODUCT_MANAGE_RICHES", NewRst);
			if(null != list && !list.isEmpty()){
				rst.put("LIST",list);
				rst.put("STATUS","1");
				rst.put("MSG", "查询成功！");
			}else{
				rst.put("LIST", "");
				rst.put("STATUS","1");
				rst.put("MSG", "查询成功！");
			}
			return rst;
		}catch(Exception e){
			rst.put("STATUS","999");
			rst.put("MSG", e);
			e.printStackTrace();
			logger.debug("操作数据库异常！ ");
			return rst;
		}
	}
}
