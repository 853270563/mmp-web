package cn.com.yitong.market.mm.sys.menu.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.com.yitong.consts.AppConstants;
import cn.com.yitong.consts.NS;
import cn.com.yitong.consts.SessConsts;
import cn.com.yitong.framework.base.IBusinessContext;
import cn.com.yitong.framework.core.bean.BaseControl;
import cn.com.yitong.framework.net.IClientFactory;
import cn.com.yitong.framework.net.IEBankConfParser;
import cn.com.yitong.framework.net.IRequstBuilder;
import cn.com.yitong.framework.net.IResponseParser;
import cn.com.yitong.framework.service.IAppVersionService;
import cn.com.yitong.framework.service.ICrudService;
import cn.com.yitong.framework.util.CtxUtil;
import cn.com.yitong.util.YTLog;

/**
 * 
 * 菜单查询
 * 
 * @author yaoym
 * 
 */
@Controller
public class MenuListController extends BaseControl {


	@Autowired
	IAppVersionService appVersService;

	private Logger logger = YTLog.getLogger(this.getClass());
	@Autowired
	ICrudService service;
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

	final String BASE_PATH = "market/mm/sys/menu/";
	
	/**
	 * 列表查询
	 * 
	 * @param trans_code
	 * @param request
	 * @return
	 */
	@RequestMapping("mm/sys/menu/list/{trans_code}.do")
	@ResponseBody
	public Map list(@PathVariable String trans_code, HttpServletRequest request) {
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
				//List datas = service.findList(statementName, params);
				
				
				List datas = new ArrayList();
				
				//先获取一级菜单
				List<Map>  menuList1 = new ArrayList();
				params.put("MENU_LEVEL", "1");
				menuList1=service.findList(statementName, params);
			
				for(int i=0; menuList1.size()>i ;i++)
				{
					Map listMap = new HashMap();
					params.put("MENU_LEVEL", "2");
					params.put("MENU_PAR_ID", menuList1.get(i).get("MENU_ID") );		
					//添加一级菜单
					listMap.put("MENU_ID", menuList1.get(i).get("MENU_ID"));
					listMap.put("MENU_NAME", menuList1.get(i).get("MENU_NAME"));
					listMap.put("MENU_URL", menuList1.get(i).get("MENU_URL"));
					listMap.put("MENU_IMG", AppConstants.IMG_WEB_URL+listMap.get("FILE_ADDR"));
					listMap.put("MENU_TYP", menuList1.get(i).get("MENU_TYP"));
					listMap.put("MENU_PAR_ID", menuList1.get(i).get("MENU_PAR_ID"));
	
					List mentList2= service.findList(statementName, params);
					
					List menuList2Temp= new ArrayList();
					for(int j =0;j<mentList2.size();j++)
					{
						Map  tempList2=(HashMap)mentList2.get(j);
						tempList2.put("MENU_IMG",  AppConstants.IMG_WEB_URL+tempList2.get("FILE_ADDR"));
						menuList2Temp.add(tempList2);
					}
					
					//添加二级菜单
					listMap.put("MENU_LIST2", menuList2Temp);					
					datas.add(listMap);
				}
				Map timeMap =new HashMap();
				timeMap=service.load("P_MOBI_ROL_MENU.QueryMenuUpdateTime", params);
				rst.put("MENU_UPDATE_TIME", timeMap.get("MENU_UPDATE_TIME"));
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
		CtxUtil.transAfter(ctx, transCode, rst, ok, responseParser, confParser);
	}
	
	
}
