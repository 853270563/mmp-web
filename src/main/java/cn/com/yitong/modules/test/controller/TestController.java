package cn.com.yitong.modules.test.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.ibatis.session.SqlSession;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.com.yitong.common.utils.SpringContextUtils;
import cn.com.yitong.common.utils.StringUtils;
import cn.com.yitong.consts.AppConstants;
import cn.com.yitong.core.model.SysDict;
import cn.com.yitong.core.util.CacheUtils;
import cn.com.yitong.core.util.DictUtils;
import cn.com.yitong.framework.base.IBusinessContext;
import cn.com.yitong.framework.core.bean.BaseControl;
import cn.com.yitong.framework.core.bean.BusinessContext;
import cn.com.yitong.framework.net.IClientFactory;
import cn.com.yitong.framework.net.IEBankConfParser;
import cn.com.yitong.framework.net.IRequstBuilder;
import cn.com.yitong.framework.net.IResponseParser;
import cn.com.yitong.framework.service.ICrudService;
import cn.com.yitong.framework.service.IThirdPatryMessageService;
import cn.com.yitong.framework.servlet.ServerInit;
import cn.com.yitong.framework.util.CtxUtil;
import cn.com.yitong.modules.test.service.TestService;
import cn.com.yitong.util.MessageTools;
import cn.com.yitong.yantai.websocket.WsServer;

/**
 * @author zhuzengpeng<zzp@yitong.com.cn>
 */
@Controller("testController2")
public class TestController extends BaseControl {
	private org.slf4j.Logger logger = LoggerFactory.getLogger(TestController.class);
	@Autowired 
	ICrudService service;
	SqlSession sqlSession = SpringContextUtils.getBean(SqlSession.class);
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
	@Autowired
	private IThirdPatryMessageService thirdPatryMessageService;
	@RequestMapping(value = "/dictPut.do")
	@ResponseBody
	public Map<String, Object> dictPut() {
		List<SysDict> list = DictUtils.getDictionaries("app_extend_status");
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("aaa", list);
		return map;
	}
	
	@RequestMapping(value = "/testCache.do")
	@ResponseBody
	public Map<String, Object> testCache() {
		List<SysDict> list = DictUtils.getDictionaries("app_extend_status");
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("aaa", list);
		return map;
	}

	@RequestMapping(value = "/cachePut.do")
	@ResponseBody
	public Map<String, Object> cachePut(String key, String value) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(key, value);
		CacheUtils.put(key, value);
		return map;
	}
	
	@RequestMapping(value = "/cacheGet.do")
	@ResponseBody
	public Map<String, Object> cacheGet(String key) {
		Map<String, Object> map = new HashMap<String, Object>();
		Object value = CacheUtils.get(key);
		map.put(key, value);
		return map;
	}
	
	@RequestMapping(value = "/evict.do")
	@ResponseBody
	public Map<String, Object> evict(String key) {
		Map<String, Object> map = new HashMap<String, Object>();
		CacheUtils.evict(key);
		map.put(key, "success");
		return map;
	}
	
	@RequestMapping(value = "/sessionPut.do")
	@ResponseBody
	public Map<String, Object> sessionPut(String key, String value, long timeout) {
		CacheUtils.put(key, value, timeout);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(key, "success");
		return map;
	}
	
	@RequestMapping(value = "creditTaskList1/creditTaskList1.do")
	@ResponseBody
	public Map<String, Object> creditTaskList(HttpServletRequest request) {
		String transCode = "waittingDone";
		Map<String, Object> result = new HashMap<String, Object>();
		// 初始化数据总线
		IBusinessContext ctx = new BusinessContext(request, IBusinessContext.PARAM_TYPE_MAP);
		// 检查报文定义

		boolean ok = false;
		thirdPatryMessageService.creditLoanSystem(ctx, transCode, result);
		return result;
	}

	@RequestMapping(value = "/testCredit.do")
	@ResponseBody
	public Map<String, Object> testCredit(HttpServletRequest request) {
		String transCode = "waittingDone";
		Map<String, Object> result = new HashMap<String, Object>();
		// 初始化数据总线
		IBusinessContext ctx = new BusinessContext(request, IBusinessContext.PARAM_TYPE_MAP);
		// 检查报文定义
		// 加载参数
		if (!ctx.initParamCover(json2MapParamCover, transCode, false)) {
			MessageTools.elementToMap(ctx.getResponseContext(transCode), result);
			return result;
		}
				// 检查报文定义
		if (!requestBuilder.buildSendMessage(ctx, confParser, transCode)) {
			MessageTools.elementToMap(ctx.getResponseContext(transCode), result);
			return result;
		}
		boolean ok = false;
		try{
			ok = thirdPatryMessageService.creditLoanSystem(ctx, transCode, result);
		}catch(Exception e){
			result.put(AppConstants.STATUS, AppConstants.STATUS_OK);
			result.put(AppConstants.MSG, e.getMessage());
		}
		if(ok){
			result.put(AppConstants.STATUS, AppConstants.STATUS_FAIL);
			result.put(AppConstants.MSG, "成功");
		}else{
			result.put(AppConstants.STATUS, AppConstants.STATUS_OK);
			result.put(AppConstants.MSG, "失败");
		}
		return result;
	}

	@RequestMapping(value = "/testWebsocket.do")
	@ResponseBody //加上这个注解的话，不会跳到jsp页面
	public void testWebsocket(HttpServletRequest request) {

		try {
			WsServer.sendMessageAll("所有用户");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}

	@RequestMapping(value = "/testSystemValue.do")
	public void testSystemValue(HttpServletRequest request, HttpServletResponse respons) {
		String key = request.getParameter("key");
		String value = request.getParameter("value");
		if (StringUtils.isNotBlank(key) && StringUtils.isNotBlank(value)) {

			System.setProperty(key, value);
		}
		logger.info("key:{},value:{}", key, System.getProperty(key));
		try {
			respons.getWriter().write("213213");//不输出的话 会跳到默认的testSystemValue.jsp页面
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@RequestMapping(value = "/testApplication.do")
	public void testApplicationContext(HttpServletRequest request) throws IOException {

		logger.info("path:{}", SpringContextUtils.getApplicationContext().getResource("classpath:META-INF/debug/data.json").getFile().getPath());
		logger.info("path:{}", SpringContextUtils.getApplicationContext().getResource("WEB-INF/config/private_key.pem").getFile().getPath());
		logger.info("path:{}", SpringContextUtils.getApplicationContext().getResource("file:F:/2weima.png").getFile().getPath());
		logger.info("path:{}", SpringContextUtils.getApplicationContext().getResource("F:/2weima.png").getFile().getPath());

	}

	@RequestMapping(value = "/testBatchInsert.do")
	public void testBatchInsert(HttpServletRequest request) throws IOException {

		SqlSession sqlSession = SpringContextUtils.getBean(SqlSession.class);
		FileInputStream fileInputStream = null;
		BufferedReader bufferedReader = null;
		String users = null;
		StringBuffer bfBuffer = new StringBuffer();
		logger.debug("批量同步crm用户开始");
		try {

			File file = new File(ServerInit.getString("upload_files_path") + "/userList.txt");
			if (!file.exists()) {
				logger.debug(file.getPath() + "路径不存在");
				return;
			}
			fileInputStream = new FileInputStream(file);
			bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream, "utf-8"));
			while ((users = bufferedReader.readLine()) != null) {
				bfBuffer.append(users);
			}
			String[] split = bfBuffer.toString().split("\\|");
			sqlSession.insert("crm.inserts", split);
			logger.debug("批量同步crm用户成功");
			file.delete();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			if (fileInputStream != null) {
				try {
					fileInputStream.close();
				} catch (IOException e) {
					logger.error(e.getMessage(), e);
				}

			}
			if (bufferedReader != null) {
				try {
					bufferedReader.close();
				} catch (IOException e) {
					logger.error(e.getMessage(), e);
				}
			}
			logger.debug("批量同步crm用户结束");
		}
	}

	@RequestMapping(value = "/testTx.do")
	public void testTx(HttpServletRequest request) throws IOException {

		TestService sqlSession = SpringContextUtils.getBean(TestService.class);
		sqlSession.insert();
	}

	@RequestMapping(value = "/testnoExistinsert.do")
	public void testnoExistinsert(HttpServletRequest request, String userId) throws IOException {

		sqlSession.insert("crm.notExistinsert", userId);
	}

	/**
	 * 事务前置处理
	 * @param ctx
	 * @param transCode
	 * @param rst
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	private boolean transPrev(IBusinessContext ctx, String transCode, Map rst) {
		return CtxUtil.transPrev(ctx, transCode, json2MapParamCover, requestBuilder, confParser, rst);
	}

	/**
	 * 事务之后处理
	 * @param ctx
	 * @param transCode
	 * @param rst
	 * @param ok
	 */
	@SuppressWarnings("rawtypes")
	private void transAfter(IBusinessContext ctx, String transCode, Map rst, boolean ok) {
		CtxUtil.transAfter(ctx, transCode, rst, ok, responseParser, confParser);
	}

}
