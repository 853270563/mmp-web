package cn.com.yitong.modules.test;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;

import cn.com.yitong.ares.core.AresApp;
import cn.com.yitong.ares.error.AresRuntimeException;
import cn.com.yitong.consts.AppConstants;
import cn.com.yitong.framework.base.IBusinessContext;
import cn.com.yitong.framework.core.bean.BaseControl;
import cn.com.yitong.framework.core.bean.BusinessContext;
import cn.com.yitong.framework.core.bean.MBTransConfBean;
import cn.com.yitong.framework.net.IEBankConfParser;
import cn.com.yitong.framework.util.CtxUtil;
import cn.com.yitong.util.MessageTools;
import cn.com.yitong.util.common.FileUtil;
import cn.com.yitong.util.common.StringUtil;

@Controller
public class TestController extends BaseControl {
	private Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	@Qualifier("EBankConfParser4Test")
	IEBankConfParser confParser;// 报文装载器

	@RequestMapping("/test/transConf.do")
	@ResponseBody
	public Map trans(HttpServletRequest request, String local) {
		long begin = System.currentTimeMillis();
		IBusinessContext ctx = new BusinessContext(request, IBusinessContext.PARAM_TYPE_MAP);
		Map result = new HashMap();
		// 初始化数据总线
		// 加载参数
		try {
			String url = request.getServletPath();
			//分割请求Url
			String fullTransCode = url.substring(1).split("\\.")[0];
			if (!ctx.initParamCover(json2MapParamCover, fullTransCode, false)) {
				MessageTools.elementToMap(ctx.getResponseContext(fullTransCode), ctx.getParamMap());
				return ctx.getParamMap();
			}
			String transPath = ctx.getParam("fullTransId");
			if (StringUtil.isEmpty(transPath)) {
				throw new AresRuntimeException("common.parameter_empty", new Object[] { "fullTransId" });
			}
			MBTransConfBean conf = confParser.findTransConfById(transPath);
			result.put("name", conf.getName());
			result.put("desc", conf.getProperty("desc"));
			// 请求字段定义
			result.put("req", conf.getSed());
			// 响应字段定义
			result.put("rsp", conf.getRcv());

			CtxUtil.showSuccessResult(result);
		} catch (AresRuntimeException e) {
			CtxUtil.showErrorResult(e, ctx);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			ctx.setParam(AppConstants.STATUS, AppConstants.STATUS_FAIL);
			ctx.setParam(AppConstants.MSG, e.getMessage());
			return ctx.getParamMap();
		} finally {
			long end = System.currentTimeMillis();
			logger.info("net total times is :{}", (end - begin));
		}
		return result;
	}

	//@Value("${TEST_FILE_PATH}")
	private String rootpath;

	//@RequestMapping("/test/qdzhSave.do")
	@ResponseBody
	public Map qdzhSave(@RequestParam String transCode, @RequestParam String fileName, @RequestBody IBusinessContext ctx) {
		transCode = transCode.replaceAll("/", "_").replaceAll(".do", "");
		if (StringUtil.isEmpty(fileName)) {
			fileName = transCode;
		}
		Map rst = new HashMap();
		// 初始化数据总线 
		String json = JSON.toJSONString(ctx.getParamMap());
		File root = AresApp.getInstance().getFile(rootpath);
		if (!root.exists()) {
			logger.warn("目录不存在{}", rootpath);
			rst.put("STATUS", "0");
			rst.put("MSG", "目录不存在!");
			return rst;
		}
		try {
			String filePath = String.format("%s/%s_%s.json", root.getAbsolutePath(), transCode, fileName);
			FileUtil.writeFileString(filePath, json);
			rst.put("STATUS", "1");
		} catch (Exception e) {
			logger.error("test qdzhSave error", e);
			rst.put("STATUS", "0");
			rst.put("MSG", "文件保存失败!");
		}
		return rst;
	}

	//@RequestMapping("/test/qdzhFind.do")
	@ResponseBody
	public Map qdzhFind(@RequestParam String transCode, HttpServletRequest request) {
		transCode = transCode.replaceAll("/", "_").replaceAll(".do", "");
		Map rst = new HashMap();
		// 初始化数据总线
		final String filePre = transCode + "_";

		File dir = AresApp.getInstance().getFile(rootpath);
		File[] files = dir.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File f, String fileName) {
				if (fileName.startsWith(filePre) && !fileName.contains("svn")) {
					return true;
				}
				return false;
			}
		});

		List list = new ArrayList();
		if (null != files) {
			for (File file : files) {
				Map map = new HashMap();
				String fileName = file.getName();
				map.put("NAME", fileName.replace(filePre, "").replace(".json", ""));
				map.put("URL", fileName);
				try {
					map.put("CONTENT", FileUtils.readFileToString(file));
				} catch (IOException e) {
					e.printStackTrace();
				}
				list.add(map);
			}
		}
		rst.put("LIST", list);
		// 加载参数
		return rst;
	}

	@RequestMapping("/test/loadTestData.do")
	@ResponseBody
	public Map loadTest(IBusinessContext ctx, HttpServletRequest request, String local) {
		long begin = new Date().getTime();
		Map result = new HashMap();
		try {
			String transCode = ctx.getParam("url");
			transCode = transCode.replaceAll("/", "_").replaceAll(".do", "");
			final String filePre = transCode + "_auto.json";

			File dir = AresApp.getInstance().getFile(rootpath);
			File[] files = dir.listFiles(new FilenameFilter() {
				@Override
				public boolean accept(File f, String fileName) {
					if (fileName.equals(filePre)) {
						return true;
					}
					return false;
				}
			});
			if (files.length > 0) {
				File file = files[0];
				result.put("STATUS", "1");
				try {
					result.put("CONTENT", FileUtils.readFileToString(file));
				} catch (IOException e) {
					e.printStackTrace();
				}

			} else {
				result.put("STATUS", "00");
				result.put("MSG", "未找到测试数据");
			}
			// rst.put("LIST", list);

			return result;
		} catch (AresRuntimeException e) {
			CtxUtil.showErrorResult(e, ctx);
		} catch (Exception e) {
			logger.error("test config error", e);
		} finally {
			long end = new Date().getTime();
			logger.info("net total times is :{}", (end - begin));
		}
		return result;
	}

}