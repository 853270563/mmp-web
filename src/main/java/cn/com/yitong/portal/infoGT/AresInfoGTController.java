package cn.com.yitong.portal.infoGT;

import cn.com.yitong.ares.login.BizLogger;
import cn.com.yitong.framework.base.IBusinessContext;
import cn.com.yitong.framework.core.bean.BaseControl;
import cn.com.yitong.framework.core.bean.BusinessContext;
import cn.com.yitong.framework.net.IClientFactory;
import cn.com.yitong.framework.net.IEBankConfParser;
import cn.com.yitong.framework.net.IRequstBuilder;
import cn.com.yitong.framework.net.IResponseParser;
import cn.com.yitong.framework.service.ICrudService;
import cn.com.yitong.framework.util.CtxUtil;
import cn.com.yitong.util.YTLog;
import com.ibm.db2.jcc.am.he;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.Reader;
import java.sql.Clob;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
public class AresInfoGTController extends BaseControl {

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
	
	final String BASE_PATH = "portal/infoGT/";
	
	final BizLogger bizLogger = BizLogger.getLogger(this.getClass());
	
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
	
	/**
	 * 获取聚和信息内容
	 * @param request
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping("portal/infoGT/loadInfoGT.do")
	@ResponseBody
	public Map<String, Object> loadInfoGT(HttpServletRequest request) {
		String transCode = BASE_PATH + "loadInfoGT";
		Map<String, Object> rst = new HashMap<String, Object>();
		// 初始化数据总线
		IBusinessContext ctx = new BusinessContext(request,
				IBusinessContext.PARAM_TYPE_MAP);
		// 检查报文定义
		if (!transPrev(ctx, transCode, rst)) {
			return rst;
		}
        if (CtxUtil.debugTrans("loadInfoGT")) {
            boolean ok = client.execute(ctx, transCode);
            transAfter(ctx, transCode, rst, ok);
            return rst;
        }
		// 数据库操作区
		Map<String, Object> params = ctx.getParamMap();
		boolean ok = false;
		try {
			Map map = service.load("ARES_INFO_GT.loadInfoGT", params);
			if (map != null && !map.isEmpty()) {
				Object object = map.get("CONTENT");
				map.put("CONTENT", this.clobToString(object));
				rst.putAll(map);
			}
			ok = true;
		} catch (Exception e) {
			// 输出错误的关键信息
			ok=false;
			logger.error(ctx.getTransLogBean(transCode), e);
		}
		transAfter(ctx, transCode, rst, ok);
		return rst;
	}
	

	/**
	 * 将clob类型转成string
	 * @param clob
	 * @return
	 * @throws Exception
	 */
	private String clobToString(Object clob) throws Exception{
		if (clob == null) {
			return "";
		}
		Reader characterStream = null;
		BufferedReader br = null;
		try {
			if (clob instanceof Clob) {
				Clob local = (Clob)clob;
				characterStream = local.getCharacterStream();
			} else if (clob instanceof he) {
				he local = (he)clob;
				characterStream = local.getCharacterStream();
			} else {
				return clob.toString();
			}
			br = new BufferedReader(characterStream);
			StringBuffer sb = new StringBuffer();
			String line = null;
			while ((line = br.readLine()) != null) {
				sb.append(line).append("\n");
			}
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (characterStream != null) {
				characterStream.close();
			}
			if (br != null) {
				br.close();
			}
		}
	}
	

	/**
	 * 获取聚合信息列表
	 * @param request
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping("portal/infoGT/infoGTlist.do")
	@ResponseBody
	public Map<String, Object> infoGTlist(HttpServletRequest request) {
		String transCode = BASE_PATH + "infoGTlist";
		Map<String, Object> rst = new HashMap<String, Object>();
		// 初始化数据总线
		IBusinessContext ctx = new BusinessContext(request,
				IBusinessContext.PARAM_TYPE_MAP);
		// 检查报文定义
		if (!transPrev(ctx, transCode, rst)) {
			return rst;
		}
        if (CtxUtil.debugTrans("infoGTlist")) {
            boolean ok = client.execute(ctx, transCode);
            transAfter(ctx, transCode, rst, ok);
            return rst;
        }
		// 数据库操作区
		Map<String, Object> params = ctx.getParamMap();
		boolean ok = false;
		try {
			String synchroTime = (String)params.get("synchroTime");
			Date date = sdf.parse(synchroTime);
			params.put("synchroTime", date);
			Date updateTime = new Date();
			params.put("updateTime", updateTime);
			List<Map> infoGTList = service.findList("ARES_INFO_GT.selectInfoGTList", params);
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			for (Map map : infoGTList) {
				String createDate = dateFormat.format((Date)map.get("CREATE_DATE"));
				map.put("CREATE_DATE", createDate);
			}
			rst.put("UPDATETIME", sdf.format(updateTime));
			rst.put("LIST", infoGTList);
			ok = true;
		} catch (Exception e) {
			// 输出错误的关键信息
			ok=false;
			logger.error(ctx.getTransLogBean(transCode), e);
		}
		transAfter(ctx, transCode, rst, ok);
		return rst;
	}
	

	/**
	 * 创建聚合信息
	 * @param request
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping("portal/infoGT/addInfoGT.do")
	@ResponseBody
	public Map<String, Object> addInfoGT(HttpServletRequest request) {
		String transCode = BASE_PATH + "addInfoGT";
		Map<String, Object> rst = new HashMap<String, Object>();
		// 初始化数据总线
		IBusinessContext ctx = new BusinessContext(request,
				IBusinessContext.PARAM_TYPE_MAP);
		// 检查报文定义
		if (!transPrev(ctx, transCode, rst)) {
			return rst;
		}
        if (CtxUtil.debugTrans("addInfoGT")) {
            boolean ok = client.execute(ctx, transCode);
            transAfter(ctx, transCode, rst, ok);
            return rst;
        }
		// 数据库操作区
		Map<String, Object> params = ctx.getParamMap();
		boolean ok = false;
		try {
			String id = UUID.randomUUID().toString();
			params.put("ID", id);
			params.put("CREATE_DATE", new Date());
			service.insert("ARES_INFO_GT.insertInfoGT", params);
			ok = true;
		} catch (Exception e) {
			// 输出错误的关键信息
			ok=false;
			logger.error(ctx.getTransLogBean(transCode), e);
		}
		transAfter(ctx, transCode, rst, ok);
		return rst;
	}
	
	/**
	 * 事务前置处理
	 * @param ctx
	 * @param transCode
	 * @param rst
	 * @return
	 */
	private boolean transPrev(IBusinessContext ctx, String transCode, Map<String, Object> rst) {
		return CtxUtil.transPrev(ctx, transCode, json2MapParamCover,
				requestBuilder, confParser, rst);
	}

	/**
	 * 事务之后处理
	 * @param ctx
	 * @param transCode
	 * @param rst
	 * @param ok
	 */
	private void transAfter(IBusinessContext ctx, String transCode, Map<String, Object> rst,
			boolean ok) {
		CtxUtil.transAfter(ctx, transCode, rst, ok, responseParser, confParser);
	}
}