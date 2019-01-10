package cn.com.yitong.market.mm.mzj.propa;

import cn.com.yitong.consts.AppConstants;
import cn.com.yitong.consts.NS;
import cn.com.yitong.core.util.DictUtils;
import cn.com.yitong.framework.base.IBusinessContext;
import cn.com.yitong.framework.core.bean.BaseControl;
import cn.com.yitong.framework.net.IClientFactory;
import cn.com.yitong.framework.net.IEBankConfParser;
import cn.com.yitong.framework.net.IRequstBuilder;
import cn.com.yitong.framework.net.IResponseParser;
import cn.com.yitong.framework.service.IAppVersionService;
import cn.com.yitong.framework.service.ICrudService;
import cn.com.yitong.framework.util.CtxUtil;
import cn.com.yitong.market.jjk.service.CustomFileMngService;
import cn.com.yitong.util.CustomFileType;
import cn.com.yitong.util.MessageTools;
import cn.com.yitong.util.YTLog;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class PropaBannerController extends BaseControl {


	@Autowired
	IAppVersionService appVersService;
	@Resource
	private CustomFileMngService customFileMngService;
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

	final String BASE_PATH = "market/mm/mzj/";
	private final String FILE_CATA = "2";
	
	/**
	 * 列表查询
	 * 
	 * @param trans_code
	 * @param request
	 * @return
	 */
	@RequestMapping("mm/mzj/propa/list/{trans_code}.do")
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
		
		String fileCata = (String) params.get("FILE_CATA");
		List<Map<String,String>> fileCataTitle = new ArrayList<Map<String,String>>();
		if(FILE_CATA.equals(fileCata)){
			fileCataTitle = DictUtils.getValue2LabelMap2("DOC_TYP");
			if(fileCataTitle==null || fileCataTitle.isEmpty()){
				ctx.setErrorInfo(AppConstants.STATUS_FAIL, "未找到展架内容，请联系管理员添加", transCode);
				MessageTools.elementToMap(ctx.getResponseContext(transCode), rst);
				return rst;
			}
			rst.put("MENU_LIST", fileCataTitle);
		}
		
		
		
		boolean ok = false;
		try {
			// 数据库操作
			String statementName = CtxUtil.getStatement(confParser, transCode);
			List datas = service.findList(statementName, params);
			
			List resDatas= new ArrayList();
			if(datas!=null  && datas.size()>0)
			{
				for(int i =0; i<datas.size();i++)
				{
					Map temp = (HashMap)datas.get(i);
					String base64 = customFileMngService.getFileByFileName2Base64((String) temp.get("IMG_URL"), CustomFileType.FILE);
					temp.put("IMG_URL", base64);
					temp.put("FILE_URL", AppConstants.IMG_WEB_URL+temp.get("FILE_URL"));
					temp.put("FILE_TYPE",1);
					resDatas.add(temp);		
				}
			}
			rst.put(NS.LIST, resDatas);
			ok = true;
		} catch (Exception e) {
			// 输出错误的关键信息
			logger.error(ctx.getTransLogBean(transCode), e);
			ok = false;
		}
		transAfter(ctx, transCode, rst, ok);
		return rst;
	}
	
	@RequestMapping("mm/mzj/propaData/list/{trans_code}.do")
	@ResponseBody
	public Map propaDataList(@PathVariable String trans_code, HttpServletRequest request) {
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
			
			List resDatas= new ArrayList();
			if(datas!=null  && datas.size()>0)
			{
				for(int i =0; i<datas.size();i++)
				{
					Map temp = (HashMap)datas.get(i);
					if(null!=temp.get("PIC_PATH") && !"".equals(temp.get("PIC_PATH")) &&
					   null!=temp.get("FILE_PATH")  && !"".equals(temp.get("FILE_PATH") )){
						temp.put("PIC_PATH", "download/userResource/resources.do?type=resources&fileName="+temp.get("PIC_PATH"));
						temp.put("FILE_PATH", "download/userResource/resources.do?type=resources&fileName="+temp.get("FILE_PATH"));
					}else{
						temp.put("PIC_PATH", temp.get("PIC_PATH"));
						temp.put("FILE_PATH", temp.get("FILE_PATH"));
					}
//					temp.put("PIC_PATH", AppConstants.IMG_WEB_URL+temp.get("PIC_PATH"));
//					temp.put("FILE_PATH", AppConstants.IMG_WEB_URL+temp.get("FILE_PATH"));
					resDatas.add(temp);		
				}
			}
			rst.put(NS.LIST, resDatas);
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
