package cn.com.yitong.market.jjk.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URLEncoder;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import cn.com.yitong.core.base.WebUtils;
import cn.com.yitong.core.base.web.BaseController;
import cn.com.yitong.market.jjk.service.CustomFileMngService;
import cn.com.yitong.util.CustomFileType;

@Controller
@RequestMapping("/download/userResource")
public class CustomFileMngController extends BaseController {
	
	public static final String IMG_PARENT_URL = "/download/userResource/img.do?fileName=";
	public static final String FILE_PARENT_URL = "/download/userResource/file.do?fileName=";
	
	@Resource
	private CustomFileMngService customFileMngService;
	
	@RequestMapping("/{type}")
	public void download(@PathVariable String type, String fileName,
			HttpServletRequest req, HttpServletResponse resp) throws IOException {
		Assert.hasText(type, "请求路径不正确，请指定文件类型不支持");
		CustomFileType fileType = CustomFileType.valueOf(type.toUpperCase());
		fileName = URLEncoder.encode(fileName, "UTF-8");
		Object obj = req.getParameterValues("number");
		File file;
		if(null==obj){
			file = customFileMngService.getFileByFileName(fileName, fileType);
		}else{
			String number = req.getParameterValues("number")[0];
			file = customFileMngService.getFileByFileName(fileName, fileType,number);
		}
		resp.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
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
	public void exceptionHandler(HttpServletRequest request, HttpServletResponse response,
			Exception e) {
		WebUtils.htmlExceptionHandler(request, response, e);
	}

}
