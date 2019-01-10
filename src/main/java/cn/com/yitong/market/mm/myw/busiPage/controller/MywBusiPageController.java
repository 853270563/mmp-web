package cn.com.yitong.market.mm.myw.busiPage.controller;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.com.yitong.consts.NS;
import cn.com.yitong.framework.base.IBusinessContext;
import cn.com.yitong.framework.core.bean.BaseControl;
import cn.com.yitong.framework.core.bean.BusinessContext;
import cn.com.yitong.framework.net.IClientFactory;
import cn.com.yitong.framework.net.IEBankConfParser;
import cn.com.yitong.framework.net.IRequstBuilder;
import cn.com.yitong.framework.net.IResponseParser;
import cn.com.yitong.framework.service.ICrudService;
import cn.com.yitong.framework.util.CtxUtil;
import cn.com.yitong.market.mm.myw.busiPage.service.impl.MywBusiPageService;
import cn.com.yitong.util.YTLog;

@Controller
@RequestMapping("mm/myw/")
public class MywBusiPageController extends BaseControl {

	private Logger logger = YTLog.getLogger(this.getClass());
	@Autowired
	ICrudService service;
	@Autowired
	MywBusiPageService bps;
	@Autowired
	@Qualifier("requestBuilder4db")
	IRequstBuilder requestBuilder;// 请求报文生成器
	@Autowired
	@Qualifier("responseParser4db")
	IResponseParser responseParser;// 响应报文解析器
	@Autowired
	@Qualifier("urlClient4db")
	IClientFactory client;// 响应报文解析器
	@Autowired
	@Qualifier("EBankConfParser4db")
	IEBankConfParser confParser;// 报文装载器

	private static final String BASE_PATH = "market/mm/myw/busiPage/";
	
	
	
	/**
	 * 包列表查询
	 * 
	 * @param trans_code
	 * @param request
	 * @return
	 */
	@RequestMapping("pageList/{trans_code}.do")
	@ResponseBody
	public Map pageList(@PathVariable String trans_code, HttpServletRequest request) {
		String transCode = BASE_PATH + trans_code;
		Map<String,Object> rst = new HashMap<String,Object>();
		Map<String,Object> rsts = new HashMap<String,Object>();
		// 初始化数据总线
		IBusinessContext ctx = CtxUtil.createMapContext(request);
//		 检查报文定义
		if (!transPrev(ctx, transCode, rst)) {
			return rst;
		}
		if (CtxUtil.debugTrans(trans_code)) {
			boolean ok = client.execute(ctx, transCode);
			transAfter(ctx, transCode, rst, ok);
			return rst;
		}
		// 数据库操作区
		Map<String,Object> params = ctx.getParamMap();
		boolean ok = false;
		try {
			// 数据库操作
//			String statementName = CtxUtil.getStatement(confParser, transCode);
			String user_id = params.get("PB_OWNER").toString();
			JSONArray jsonArray = bps.getJsonStr(user_id);
			String jsonStr = jsonArray!=null?jsonArray.toString():"";
			rsts.put("jsonStr", jsonStr);
			
			//bug调试代码，后期删除
			HashSet<Object> set=new HashSet<Object>();
			int nums = 0;
			if(jsonArray!=null){
				List<Map<String,Object>> aa = (List)jsonArray;
				nums= aa.size();
				for(Map<String,Object> tmpMap : aa){
					if(tmpMap.get("BUSI_ID")!=null){
						set.add(tmpMap.get("BUSI_ID"));
					}else{
						nums--;
					}
				}
			}
			
			if(!(set.size()==nums)){
				rsts.put("ERROR","0");//测试，数据是否重复
				rsts.put("MSG", "业务重复，请马上联系后台管理人员...");
			}else{
				rsts.put("ERROR","1");//测试，数据是否重复
				rsts.put("MSG", "交易成功！");
			}
			rsts.put("STATUS", "1");
		} catch (Exception e) {
			// 输出错误的关键信息
			logger.error(ctx.getTransLogBean(transCode), e);
			ok = false;
		}
		return rsts;
	}
	
	/**
	 * 包新增
	 * 
	 * @param trans_code
	 * @param request
	 * @return
	 */
	@RequestMapping("pageAdd/{trans_code}.do")
	@ResponseBody
	public Map<String,Object> pageAdd(@PathVariable String trans_code, HttpServletRequest request) {
		String transCode = BASE_PATH + trans_code;
		Map<String,Object> rst = new HashMap<String,Object>();
		// 初始化数据总线
		IBusinessContext ctx = new BusinessContext(request,
				IBusinessContext.PARAM_TYPE_MAP);
		// 检查报文定义
		if (!transPrev(ctx, transCode, rst)) {
			return rst;
		}
		if (CtxUtil.debugTrans(trans_code)) {
			boolean ok = client.execute(ctx, transCode);
			transAfter(ctx, transCode, rst, ok);
			return rst;
		}
//		// 数据库操作区
		Map params = ctx.getParamMap();
		boolean ok = false;
		try {
			// 数据库操作
			String str = params.get("jsonStr").toString();
			JSONArray json = JSONArray.fromObject(str);
			
			//调试代码开始，后期删除
			List<Map<String,Object>> aa = (List)json;
			HashSet set=new HashSet();
			int nums = aa.size();
			for(Map<String,Object> tmpMap : aa){
				if(tmpMap.get("BUSI_ID")!=null){
					set.add(tmpMap.get("BUSI_ID"));
				}else{
					nums--;
				}
			}
			if(!(set.size()==nums)){
				rst.put("ERROR","0");//测试，数据是否重复
				rst.put("MSG", "业务重复提交，请马上联系后台管理人员...");
				rst.put("STATUS", "0");
				return rst;
			}
			//调试代码结束
			Iterator it = json.iterator();
			int num = 0;
			Map delBpo= new HashMap();
			Map delBp= new HashMap();
			String userId=params.get("userId").toString();
			if(null==userId || userId.equals("")){
				ok=false;
				rst.put("MSG", "交易失败，请先登录！");
				rst.put("STATUS", "0");
				return rst;
			}
			delBpo.put("PB_OWNER",userId);
			bps.deleteBPO(delBpo);//增加之前执行删除包业务排序表操作。
			//先删除包+业务关联表内的信息
			delBpo.put("BUSI_OWNER", userId);
			bps.deleteBPS(delBpo);//增加之前执行删除包业务关联表操作。
			List list = new ArrayList<String>();
			while(it.hasNext()){
				num+=1;
				JSONObject jb = (JSONObject)it.next();
				if(jb.containsKey("BLIST")){//判断是否含有BLIST，以确认是包还是单独业务，若有，则是包，无则相反
					JSONArray ja = jb.getJSONArray("BLIST");
					//判断是否含有PKG_ID，以确认是新建包还是原来已有包,若不为空，则是已有的包，空则相反
					if(null!=jb.get("PKG_ID")){//老包
						list.add(jb.get("PKG_ID"));
						boolean op = OldPak(jb,ja,num);
						if(!op){
							rst.put("MSG", "更新旧包交易失败");
							rst.put("STATUS", "0");
							return rst;
						}
					}else{//新包
						String pkg_id = NewPak(jb,ja,num);
						list.add(pkg_id);
						if(pkg_id==null||pkg_id.equals("")){
							rst.put("MSG", "添加新包交易失败");
							rst.put("STATUS", "0");
							return rst;
						}
					}
				}else{//单独业务
					boolean b = Busi(jb,num,userId);
					if(!b){
						rst.put("MSG", "添加业务交易失败");
						rst.put("STATUS", "0");
						return rst;
					}
				}
			}
			Map<String,Object> Obj= new HashMap<String,Object>();
			Obj.put("PKG_IDS",list);
			Obj.put("PKG_OWNER", userId);
			bps.delete(Obj);//增加之后执行删除包操作，删除坏数据
			
			rst.put("ERROR","1");//调试代码，数据是否重复，后期可删除
			ok=true;
		} catch (Exception e) {
			// 输出错误的关键信息
			logger.error(ctx.getTransLogBean(transCode), e);
		}
		transAfter(ctx, transCode, rst, ok);
		return rst;
	}
	/*
	 * 已有包操作
	 */
	public boolean OldPak(JSONObject jb,JSONArray ja,int num){
		boolean isOK=true;
		try {
			//对原有老包进行更新操作
			Map<String,Object> pags = new HashMap<String,Object>();
			pags.put("PKG_ID", jb.get("PKG_ID"));
			pags.put("PKG_NAME", jb.get("PKG_NAME"));
			pags.put("PKG_DESC", jb.get("PKG_DESC"));
			pags.put("PKG_OWNER", jb.get("PKG_OWNER"));
			pags.put("PKG_ADD_DATE", jb.get("PKG_ADD_DATE"));
			pags.put("PKG_UPDATE_DATE", jb.get("PKG_UPDATE_DATE"));
			pags.put("PKG_UPDATE_OWNER", jb.get("PKG_UPDATE_OWNER"));
			pags.put("PKG_ORDER", jb.get("PKG_ORDER"));
			pags.put("PKG_TYPE", jb.get("PKG_TYPE"));
			pags.put("PKG_PUB", jb.get("PKG_PUB"));
			bps.updateById(pags);
			//向排序表插入排序关联信息
			Map<String,Object> pb = new HashMap<String,Object>();
			pb.put("PB_ID", jb.get("PKG_ID"));
			pb.put("PB_ORDER", num);
			pb.put("PB_TYPE", "0");
			pb.put("PB_OWNER", pags.get("PKG_OWNER"));
			bps.insertBPO(pb);
			//向包业务关联表内添加关联信息
			Iterator it = ja.iterator();
			int a =0;
			while(it.hasNext()){
				a+=1;
				JSONObject json = (JSONObject)it.next();
				pags.put("BUSI_ID", json.get("BUSI_ID"));
				pags.put("BUSI_ORDER", a);
				pags.put("BUSI_OWNER", pags.get("PKG_OWNER"));
				bps.insertPageBusiConf(pags);
			}
		} catch (Exception e) {
			logger.error("错误信息为："+e.getMessage());
			isOK=false;
		}
		return isOK;
	}
	/*
	 * 新建包操作
	 */
	public String NewPak(JSONObject jb,JSONArray ja,int num){
		boolean isOK=true;
		Map<String,Object> pags = new HashMap<String,Object>();
		try{
			pags.put("PKG_NAME", jb.get("PKG_NAME"));
			pags.put("PKG_DESC", jb.get("PKG_DESC"));
			pags.put("PKG_OWNER", jb.get("PKG_OWNER"));
			pags.put("PKG_ADD_DATE", jb.get("PKG_ADD_DATE"));
			pags.put("PKG_UPDATE_DATE", jb.get("PKG_UPDATE_DATE"));
			pags.put("PKG_UPDATE_OWNER", jb.get("PKG_UPDATE_OWNER"));
			pags.put("PKG_ORDER", jb.get("PKG_ORDER"));
			pags.put("PKG_TYPE", jb.get("PKG_TYPE"));
			pags.put("PKG_PUB", jb.get("PKG_PUB"));
			bps.insert(pags);
			//向排序表插入排序关联信息
			Map<String,Object> pb = new HashMap<String,Object>();
			pb.put("PB_ID", pags.get("PKG_ID"));
			pb.put("PB_ORDER", num);
			pb.put("PB_TYPE", "0");
			pb.put("PB_OWNER", pags.get("PKG_OWNER"));
			bps.insertBPO(pb);
			//向包业务关联表内添加关联信息
			Iterator it = ja.iterator();
			int a =0;
			while(it.hasNext()){
				a+=1;
				JSONObject json = (JSONObject)it.next();
				pags.put("BUSI_ID", json.get("BUSI_ID"));
				pags.put("BUSI_ORDER", a);
				pags.put("BUSI_OWNER", pags.get("PKG_OWNER"));
				bps.insertPageBusiConf(pags);
			}
		} catch (Exception e) {
			logger.error("错误信息为："+e.getMessage());
			isOK=false;
		}
		return pags.get("PKG_ID").toString();
	}
	/*
	 * 单独业务
	 */
	public boolean Busi(JSONObject o,int num,String userId){
		boolean isOK=true;
		try{
			Map<String,Object> pb = new HashMap<String,Object>();
			pb.put("PB_ID", o.get("BUSI_ID"));
			//向排序表插入排序关联信息
			pb.put("PB_ORDER", num);
			pb.put("PB_TYPE", "1");
			pb.put("PB_OWNER", userId);
			bps.insertBPO(pb);
		} catch (Exception e) {
			logger.error("错误信息为："+e.getMessage());
			isOK=false;
		}
		return isOK;
	}
	/**
	 * 功能业务新增（常用工具+业务功能）
	 * 
	 * @param trans_code
	 * @param request
	 * @return
	 */
	@RequestMapping("busiSelAdd/{trans_code}.do")
	@ResponseBody
	public Map busiSelAdd(@PathVariable String trans_code, HttpServletRequest request) {
		String transCode = BASE_PATH + trans_code;
		Map rst = new HashMap();
		// 初始化数据总线
		IBusinessContext ctx = new BusinessContext(request,
				IBusinessContext.PARAM_TYPE_MAP);
		// 检查报文定义
		if (!transPrev(ctx, transCode, rst)) {
			return rst;
		}
		if (CtxUtil.debugTrans(trans_code)) {
			boolean ok = client.execute(ctx, transCode);
			transAfter(ctx, transCode, rst, ok);
			return rst;
		}
		// 数据库操作区
		Map<String,Object> params = ctx.getParamMap();
		boolean ok = false;
		try {
			// 数据库操作
			String statementName = CtxUtil.getStatement(confParser, transCode);
			service.delete("BUSI_PACKAGE.deletePS", params);//先将该包下所有数据删除，然后再次插入关联数据
			List<Map<String,Object>> BusiSels = (List<Map<String,Object>>) params.get("BusiSels");
			for(Map<String,Object> BusiSel : BusiSels){
				params.put("BUSI_ID", BusiSel.get("BUSI_ID"));
				params.put("BUSI_ORDER", BusiSel.get("BUSI_ORDER"));
				params.put("BUSI_TYPE", BusiSel.get("BUSI_TYPE"));
				params.put("BUSI_OWNER", BusiSel.get("BUSI_OWNER"));
				service.insert(statementName, params);
			}
			ok=true;
		} catch (Exception e) {
			// 输出错误的关键信息
			logger.error(ctx.getTransLogBean(transCode), e);
		}
		transAfter(ctx, transCode, rst, ok);
		return rst;
	}
	
	
	
	/**
	 * 业务列表查询
	 * 
	 * @param trans_code
	 * @param request
	 * @return
	 */
	@RequestMapping("busiSelList/{trans_code}.do")
	@ResponseBody
	public Map busiList(@PathVariable String trans_code, HttpServletRequest request) {
		String transCode = BASE_PATH + trans_code;
		Map rst = new HashMap();
		// 初始化数据总线
		IBusinessContext ctx = CtxUtil.createMapContext(request);
		// 检查报文定义
		if (!transPrev(ctx, transCode, rst)) {
			return rst;
		}
		if (CtxUtil.debugTrans(trans_code)) {
			boolean ok = client.execute(ctx, transCode);
			transAfter(ctx, transCode, rst, ok);
			return rst;
		}
		// 数据库操作区
		Map params = ctx.getParamMap();
		boolean ok = false;
		try {
			// 数据库操作
			String statementName = CtxUtil.getStatement(confParser, transCode);
			List datas = service.findList(statementName, params);
			rst.put(NS.LIST, datas);
			ok = true;
		} catch (Exception e) {
			// 输出错误的关键信息
			logger.error(ctx.getTransLogBean(transCode), e);
			ok = false;
		}
		transAfter(ctx, transCode, rst, ok);
		return rst;
	}
	/**
	 * 事务前置处理
	 * 
	 * @param ctx
	 * @param transCode
	 * @param rst
	 * @return
	 */
	private boolean transPrev(IBusinessContext ctx, String transCode, Map rst) {
		// 交易开始，设置交易流水
		commonService.generyTransLogSeq(ctx, transCode);
		return CtxUtil.transPrev(ctx, transCode, json2MapParamCover,
				requestBuilder, confParser, rst);
	}

	/**
	 * 事务之后处理
	 * 
	 * @param ctx
	 * @param transCode
	 * @param rst
	 * @param ok
	 */
	private void transAfter(IBusinessContext ctx, String transCode, Map rst,
			boolean ok) {
		// 生成交易结果
		CtxUtil.transAfter(ctx, transCode, rst, ok, responseParser, confParser);
		// 保存交易日志
		commonService.saveJsonTransLog(ctx, transCode, rst);
	}
}
