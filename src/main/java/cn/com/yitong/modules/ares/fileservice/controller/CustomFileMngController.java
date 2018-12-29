package cn.com.yitong.modules.ares.fileservice.controller;

import cn.com.yitong.core.base.WebUtils;
import cn.com.yitong.framework.core.bean.BaseControl;
import cn.com.yitong.modules.ares.fileservice.service.ICustomFileMngService;
import cn.com.yitong.util.CustomFileType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

@Controller
@RequestMapping("/download/userResource")
public class CustomFileMngController extends BaseControl {

	protected Logger logger = LoggerFactory.getLogger(getClass());
	
	public static final String IMG_PARENT_URL = "/download/userResource/img.do?fileName=";
	public static final String FILE_PARENT_URL = "/download/userResource/file.do?fileName=";
	
	@Resource
	private ICustomFileMngService customFileMngService;
	
	@RequestMapping("/{type}")
	public void download(@PathVariable String type, String fileName,
			HttpServletRequest req, HttpServletResponse resp) throws IOException {
		Assert.hasText(type, "请求路径不正确，请指定文件类型不支持");
		Assert.hasText(fileName, "文件名称不能为空");
		CustomFileType fileType = CustomFileType.valueOf(type.toUpperCase());
		// fileName = URLEncoder.encode(fileName, "UTF-8");
		fileName = fileName.replaceAll(".do", "");
		File file = customFileMngService.getFileByFileName(fileName, fileType);
		resp.setHeader("Content-Disposition", "attachment; filename=\"" + fileName.substring(fileName.indexOf("/") + 1) + "\"");
		resp.setHeader("Content-Type", "application/octet-stream");

		resp.setContentLength((int) file.length());
		try {
			FileCopyUtils.copy(new FileInputStream(file), resp.getOutputStream());
			resp.getOutputStream().close();
		} catch (FileNotFoundException e) {
            logger.warn("请求的文件不存在:{}", file.getAbsolutePath());
			throw new FileNotFoundException("请求的文件不存在:{}" + fileName);
		} catch (IOException e) {
			throw new IOException("输出文件出错，请联系管理员", e);
		}
	}
	
	@ExceptionHandler({Exception.class})
	public void exceptionHandler(HttpServletRequest request, HttpServletResponse response, Exception e) {
		WebUtils.htmlExceptionHandler(request, response, e);
	}
}
