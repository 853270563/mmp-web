package cn.com.yitong.market.mm.mhj.prodInfo.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
public class ChangeIntroduce extends BaseControl{
	
	private Logger logger = Logger.getLogger(ChangeIntroduce.class);

	@Autowired
	ICrudService service;
	
	@RequestMapping("mm/mhj/ChangeIntroduce/ChangeIntroduce.do")
	@ResponseBody
	public Map exeute(HttpServletRequest req){
		Map rst = new HashMap();
		Map<String, Comparable> rstNew = new HashMap();
		List list = new ArrayList();
		List newList = new ArrayList();
		List newList2 = new ArrayList();
		// 初始化数据总线
		IBusinessContext ctx = CtxUtil.createMapContext(req);
		ctx.initParamCover(json2MapParamCover, false);
		String LoninId =  ctx.getParam("LGN_ID");
		String CUST_NAME = ctx.getParam("CUST_NAME");
		String CUST_PHONE = ctx.getParam("CUST_PHONE");
		String CUST_OFFI_PHONE = ctx.getParam("CUST_OFFI_PHONE");
		String CUST_CARD_TYPE = ctx.getParam("CUST_CARD_TYPE");
		String CUST_CARD_ID = ctx.getParam("CUST_CARD_ID");
		String PRODUCT_MANAGE_ID = ctx.getParam("PRODUCT_MANAGE_ID");
		String PRODUCT_MANAGE_RICHES = ctx.getParam("PRODUCT_MANAGE_RICHES");
		String PRODUCT_CODE = ctx.getParam("PRODUCT_CODE");
		String INTE_DATE = ctx.getParam("INTE_DATE");
		INTE_DATE = INTE_DATE.substring(0,4)+"-"+INTE_DATE.substring(5,7)+"-"+INTE_DATE.substring(8,10);
		String INTE_MONEY = ctx.getParam("INTE_MONEY");
		String INTE_STATE = ctx.getParam("INTE_STATE");
		String INVEST_REMARK = ctx.getParam("INVEST_REMARK");
		String QUEUE_NUM = ctx.getParam("QUEUE_NUM");
		String TURN_PRODUCT_NAME = ctx.getParam("TURN_PRODUCT_NAME");
		String TRUN_TYPE_NUM = ctx.getParam("TRUN_TYPE_NUM");
		
		rstNew.put("TURN_USER_ID",LoninId);   //一次登录用户名
		rstNew.put("TURN_CUST_NAME",CUST_NAME);   //客户名
		rstNew.put("TURN_CUST_PHONE",CUST_PHONE);   //联系方式
		rstNew.put("TURN_CUST_OFFI_PHONE",CUST_OFFI_PHONE);   // 固定电话
		rstNew.put("TURN_CUST_CARD_TYPE",CUST_CARD_TYPE);   //证件类型
		rstNew.put("TURN_CUST_CARD_ID",CUST_CARD_ID);   //证件号码
		rstNew.put("TURN_PRODUCT_MANAGE_ID",PRODUCT_MANAGE_ID);   //理财经理一次登录名
		rstNew.put("TURN_PRODUCT_MANAGE_RICHES",PRODUCT_MANAGE_RICHES);   //理财经理
		rstNew.put("TURN_PRODUCT_CODE",PRODUCT_CODE);   //产品代码 
		rstNew.put("TURN_DATE",INTE_DATE);   //意向时间
		rstNew.put("TURN_MONEY",INTE_MONEY);   //购买金额
		rstNew.put("TURN_STATE",INTE_STATE);   //意向状态
		rstNew.put("TURN_REMARK",INVEST_REMARK);   //备注     
		rstNew.put("TURN_STATUS",0);      //推送状态  0为推送，其他已推送
		rstNew.put("TURN_DIRE",TRUN_TYPE_NUM); //产品类型
		rstNew.put("TURN_PRODUCT_NAME",TURN_PRODUCT_NAME); //产品名称
		logger.info("插入‘客户意向’与理财==》"+rstNew);
		try{
			Map tempMap= new HashMap();
			//获取序列主键PROD_FAVO_ID
			tempMap=service.load("MOBILE_CUST_INFO.seqMOBILE_CUST_INFO", null);
			String SYS_CUST_ID =  tempMap.get("SYS_CUST_ID").toString();
			String date = new SimpleDateFormat("yyyyMMdd").format(new Date());
			SYS_CUST_ID= date+SYS_CUST_ID;
			String TURN_ID = date+QUEUE_NUM;
			String TURN_INTERSET_DATE =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
			rstNew.put("TURN_INTERSET_DATE",TURN_INTERSET_DATE);
			rstNew.put("AUTO_ID",SYS_CUST_ID);
			rstNew.put("TURN_ID",TURN_ID);
			service.insert("TURN_INTRODUCE.insert", rstNew);
			logger.info("++++++");
			logger.info("ChangeIntroduce.do rst：=========》" + rst.toString());
			rst.put("STATUS","1");
			rst.put("MSG", "转介成功！");
			return rst;
		}catch(Exception e){
			rst.put("STATUS","999");
			rst.put("MSG", "网络异常，插入失败！");
//			logger.info("rst：=========》" + rst.toString());
			e.printStackTrace();
			logger.debug("操作数据库异常！ ");
			return rst;
		}

	}
	
	public static void main(String[] args){
		String INTE_DATE="2014年05月20日";
		INTE_DATE = INTE_DATE.substring(0,4)+"-"+INTE_DATE.substring(5,7)+"-"+INTE_DATE.substring(8,10);
		System.out.println(INTE_DATE);
	}	
}
