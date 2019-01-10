package cn.com.yitong.portal.note.controller;

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
import com.alibaba.druid.proxy.jdbc.ClobProxyImpl;
import com.ibm.db2.jcc.am.he;
import oracle.sql.CLOB;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Controller
public class AresNoteController extends BaseControl {

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
	
	final String BASE_PATH = "portal/note/";
	
	final BizLogger bizLogger = BizLogger.getLogger(this.getClass());
	
	/**
	 * 获取资讯公告内容
	 * @param request
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("portal/note/loadNote.do")
	@ResponseBody
	public Map<String, Object> loadNote(HttpServletRequest request) {
		String transCode = BASE_PATH + "loadNote";
		Map<String, Object> rst = new HashMap<String, Object>();
		// 初始化数据总线
		IBusinessContext ctx = new BusinessContext(request,
				IBusinessContext.PARAM_TYPE_MAP);
		// 检查报文定义
		if (!transPrev(ctx, transCode, rst)) {
			return rst;
		}
        if (CtxUtil.debugTrans("loadNote")) {
            boolean ok = client.execute(ctx, transCode);
            transAfter(ctx, transCode, rst, ok);
            return rst;
        }
		// 数据库操作区
		Map<String, Object> params = ctx.getParamMap();
		boolean ok = false;
		try {
			Map<String, Object> aresNote = service.load("ARES_NOTE.loadAresNote", params);
			if (aresNote == null || aresNote.isEmpty()) {
				rst.put("MSG", "资讯公告：" + params.get("NOTE_ID") + "不存在！");
				rst.put("STATUS", "0");
				return rst;
			}
			Object object = aresNote.get("NOTE_CONTENT");
			rst.putAll(aresNote);
			rst.put("NOTE_CONTENT", this.clobToString(object));
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
			if(clob instanceof ClobProxyImpl) {
				ClobProxyImpl impl = (ClobProxyImpl)clob;
				Clob tempClob = impl.getRawClob(); // 获取原生的这个 Clob
				characterStream = tempClob.getCharacterStream();
			}else if (clob instanceof CLOB) {
				CLOB local = (CLOB)clob;
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
				sb.append(line);
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
	 * 获取资讯公告内容
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("portal/note/noteList.do")
	@ResponseBody
	public Map<String, Object> loadNoteList(HttpServletRequest request) {
		String transCode = BASE_PATH + "noteList";
		Map<String, Object> rst = new HashMap<String, Object>();
		// 初始化数据总线
		IBusinessContext ctx = new BusinessContext(request,
				IBusinessContext.PARAM_TYPE_MAP);
		// 检查报文定义
		if (!transPrev(ctx, transCode, rst)) {
			return rst;
		}
        if (CtxUtil.debugTrans("noteList")) {
            boolean ok = client.execute(ctx, transCode);
            transAfter(ctx, transCode, rst, ok);
            return rst;
        }
		// 数据库操作区
		Map<String, Object> params = ctx.getParamMap();
		boolean ok = false;
		try {
			int pageNo = Integer.parseInt((String)params.get("PAGE_NO"));
			int pageSize = Integer.parseInt((String)params.get("PAGE_SIZE"));
			
			int firstIndex = (pageNo - 1) * pageSize;
			int lastIndex = pageNo * pageSize;
			
			params.put("firstIndex", firstIndex);
			params.put("lastIndex", lastIndex);
			
			List<Map<String, Object>> aresNoteList = service.findList("ARES_NOTE.loadAresNoteList", params);
			List<Map<String, Object>> noteList = new ArrayList<Map<String, Object>>();
			if(null != aresNoteList && aresNoteList.size() > 0) {
				for(Map<String, Object> map : aresNoteList) {
					map.put("NOTE_CONTENT", clobToString(map.get("NOTE_CONTENT")));
					map.put("CRT_DATE", map.get("CREATE_TIME"));
					noteList.add(map);
				}
			}
			rst.put("LIST", noteList);
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