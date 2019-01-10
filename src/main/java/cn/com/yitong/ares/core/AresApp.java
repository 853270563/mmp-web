package cn.com.yitong.ares.core;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.springframework.context.MessageSource;
import org.springframework.core.io.Resource;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

import cn.com.yitong.ares.error.AresRuntimeException;
import cn.com.yitong.ares.flow.AresFlowDispatch;
import cn.com.yitong.ares.flow.IAresFlowDispatch;
import cn.com.yitong.ares.flow.define.AresFlowDefineByFlw;
import cn.com.yitong.util.common.FileUtil;

/**
 * 应用工具
 * 
 * @author yaoym
 * 
 */
public class AresApp {
	private MessageSource messageSource;
	private WebApplicationContext springCtx;
	private Locale locale = Locale.CHINA;
	private Map<String, IAresFlowDispatch> flowMap = new HashMap();

	// public static WebApplicationContext mvcCtx;

	private AresApp() {
	}

	private static AresApp instance = new AresApp();

	public static AresApp getInstance() {
		return instance;
	}

	public void init(ContextLoader contextLoader) {
		springCtx = ContextLoader.getCurrentWebApplicationContext();
		messageSource = getBean("messageSource");
	}

	public String getMessage(String code, Object[] params, Locale local) {
		return messageSource.getMessage(code, params, locale);
	}

	public String getMessage(String code, Object... params) {
		return getMessage(code, params, locale);
	}

	/**
	 * 获取bean
	 * 
	 * @param beanId
	 * @return
	 */
	public <T> T getBean(String beanId) {
		return (T) springCtx.getBean(beanId);
	}

	public IAresFlowDispatch getFlow(String name, String rootPath) {
		if (flowMap.containsKey(name)) {
			return flowMap.get(name);
		}
		IAresFlowDispatch dispatch = new AresFlowDispatch();
		String flwFilePath = String.format("%s/%s.flw", rootPath, name);
		dispatch.setDefineName(flwFilePath);
		dispatch.setFlowDefine(new AresFlowDefineByFlw());
		dispatch.init();
		flowMap.put(name, dispatch);
		return dispatch;
	}

	public void removeFlow(String name, String rootPath) {
		//add by zwb 修复window路径是"\"问题导致无法正常移除问题。
		name=name.replaceAll("\\\\", "/");
		flowMap.remove(name);
	}


	/**
	 * 获取文件
	 * 
	 * @param location
	 * @return
	 */
	public File getFile(String location) {
		try {
			Resource res = springCtx.getResource(location);
			return res.getFile();
		} catch (Exception e) {
			e.printStackTrace();
			throw new AresRuntimeException("common.system_error", e);
		}
	}

	/**
	 * 获取系统文件
	 * 
	 * @param location
	 * @return
	 */
	public String loadUtf8Resouce(String location) {
		InputStream ioStream = null;
		try {
			Resource res = springCtx.getResource(location);
			File file = res.getFile();
			if (!file.exists()) {
				throw new AresRuntimeException("common.file_not_exists", location);
			}
			ioStream = res.getInputStream();
			return FileUtil.readFileAsString(ioStream);
		} catch (Exception e) {
			e.printStackTrace();
			throw new AresRuntimeException(e.getMessage());
		} finally {
			try {
				if (ioStream != null)
					ioStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		//return "";
	}

	public WebApplicationContext getSpringCtx() {
		return springCtx;
	}

}
