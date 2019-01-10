package cn.com.yitong.market.mm.mhj.prodInfo.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.com.yitong.consts.AppConstants;
import cn.com.yitong.framework.base.IBusinessContext;
import cn.com.yitong.framework.core.bean.BaseControl;
import cn.com.yitong.framework.net.impl.temp.ClientFactory4Test;
import cn.com.yitong.framework.service.ICrudService;
import cn.com.yitong.framework.util.CtxUtil;
import cn.com.yitong.util.MessageTools;

@Controller
public class ProductRiches extends BaseControl{
	private Logger logger = Logger.getLogger(ProductRiches.class);

	@Autowired
	ICrudService service;

	@RequestMapping("mm/mhj/productRiches/productRiches.do")
	@ResponseBody
	public Map exeute(HttpServletRequest req){
		
		final String transCode ="manageMoneyJson/queryMoneyListJson";
		Map rst = new HashMap();
		Map NewRst = new HashMap();
		boolean isok = false;
		boolean isok1 = false;
		IBusinessContext ctx = CtxUtil.createMapContext(req);
		ctx.initParamCover(json2MapParamCover,transCode, false);
		ctx.setParam("SERVICE_CODE", "09003000004");
		ctx.setParam("SERVICE_SCENE", "02");
		ctx.setParam("SOURCE_TYPE", "G");
		ctx.setParam("ORDER_FLAG", "0");
		ctx.setParam("QUERY_KEY", "1");
		ctx.setParam("QUERY_CNT", "100");
		ctx.setParam("TRAN_FLAG", "0");
		ctx.setParam("RISK_LEVEL", "-1");
		
		
		for(int j=0;j<2;j++){
			
			if(j==0){
				ctx.setParam("PRODUCT_TYPE","0");
				ctx.setParam("PRODUCT_STATUS","0");
			}
			else if(j==1){
				ctx.setParam("PRODUCT_TYPE","1");
				ctx.setParam("PRODUCT_STATUS","1");
			}	
			ctx.setParam("QUERY_KEY", "1");
//	    	this.execute(ctx, req, transCode);
//			MessageTools.elementToMap(ctx.getResponseContext(transCode), rst);
			{
				rst = ClientFactory4Test.getResultMap(transCode, "esb");
			}
	
			if (!rst.get(AppConstants.STATUS).equals(AppConstants.STATUS_OK)) {
				return rst;
			}
			List prods = (List) rst.get("PRODUCTS_INFO");
			logger.info("基金返回数据数量：" + prods.size());
			//String PRODUCT_TYPE = ctx.getParam("PRODUCT_TYPE");// 产品类别0-基金 1-国内理财 2-境外理财产品
			int TOTAL_NUM = Integer.valueOf(String.valueOf(rst.get("TOTAL_NUM")));
		
			List newList= new ArrayList();
			for (int i = 0; i < prods.size(); i++) {
				Map newRst = new HashMap();
				Map obj = (Map) prods.get(i);
				String TA_CODE = (String) obj.get("PRODUCT_CODE");
				String PRODUCT_NAME = (String) obj.get("PRODUCT_NAME");
				newRst.put("PRODUCT_CODE", TA_CODE);
				newRst.put("PRODUCT_NAME", PRODUCT_NAME);
				newList.add(newRst);
			}
			
			int key = 0;
			key = key+20;
			while(TOTAL_NUM>key){	
				Map rstt = new HashMap();
				ctx.setParam("QUERY_KEY",String.valueOf(key));
				this.execute(ctx, req, transCode);
				MessageTools.elementToMap(ctx.getResponseContext(transCode), rstt);
				List prod = (List) rstt.get("PRODUCTS_INFO");
				key = key+20;
				for (int i = 0; i < prod.size(); i++) {
					Map newRst = new HashMap();
					Map obj = (Map) prod.get(i);
					String PRODUCT_CODE = (String) obj.get("PRODUCT_CODE");
					String PRODUCT_NAME = (String) obj.get("PRODUCT_NAME");
					newRst.put("PRODUCT_CODE", PRODUCT_CODE);
					newRst.put("PRODUCT_NAME", PRODUCT_NAME);
					newList.add(newRst);
				}
			}
			if(j==0){
				NewRst.put("LIST",newList);
				isok =true;
			}
			else if(j==1){
				NewRst.put("LIST1",newList);
				isok1 = true;
			}
		}
		if(isok && isok1){
			NewRst.put("STATUS","1");
			NewRst.put("MSG","查询理财、基金成功！");
		}else if(isok){
			NewRst.put("STATUS","2");
			NewRst.put("MSG","查询基金成功，理财失败！");
		}else if(isok1){
			NewRst.put("STATUS","3");
			NewRst.put("MSG","查询理财成功，基金失败！");
		}else{
			NewRst.put("STATUS","0");
			NewRst.put("MSG","查询理财、基金失败！");
		}
		return NewRst;
	}
}
