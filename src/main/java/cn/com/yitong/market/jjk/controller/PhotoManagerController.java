package cn.com.yitong.market.jjk.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import cn.com.yitong.core.base.WebUtils;
import cn.com.yitong.core.base.web.BaseController;
import cn.com.yitong.market.jjk.service.ImageMngService;

/**
 * 照片上传
 * @author lc3@yitong.com.cn
 *
 */
@Controller
public class PhotoManagerController extends BaseController {
	
	public static final String PHOTO_VIEW_URL = "/jjk/debit/viewPhoto/";
	
	@Resource
	private ImageMngService imageMngService;
	
	/**
	 * 图片文件上传，支持图像格式和zip格式
	 * @param request
	 * @return
	 * @throws java.io.IOException
	 * @throws java.io.FileNotFoundException
	 */
	@RequestMapping(value = "/jjk/debit/photoUpload", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> photoUpload(MultipartHttpServletRequest request) throws FileNotFoundException, IOException {
		Map<String, Object> rtn = new HashMap<String, Object>();
		
		List<MultipartFile> uploadFiles = request.getFiles("uploadFiles");
		if(null == uploadFiles || uploadFiles.isEmpty()) {
			return WebUtils.returnErrorMsg(rtn, "文件不能为空！");
		}
		
		for(MultipartFile file : uploadFiles) {
			rtn.putAll(imageMngService.savePhoteMultipartFile(file));
		}
		
		return WebUtils.returnSuccessMsg(rtn, null);
	}
	
	@RequestMapping(PHOTO_VIEW_URL + "{photoName}")
	public void viewPhoto(@PathVariable String photoName, HttpServletRequest req, HttpServletResponse resp) {
		File file = imageMngService.getFileByPhotoName(photoName);
		Exception exception = null;
		try {
			FileCopyUtils.copy(new FileInputStream(file), resp.getOutputStream());
			resp.setHeader("Content-Type", "application/octet-stream");
			return;
		} catch (FileNotFoundException e) {
			exception = new FileNotFoundException("请求的文件不存在");
			if(logger.isWarnEnabled()) {
				logger.warn(String.format("请求的文件%s不存在", file), e);
			}
		} catch (IOException e) {
			exception = new IOException("输出文件出错，请联系管理员", e);
			if(logger.isErrorEnabled()) {
				logger.error(String.format("输出文件%s出错", file), e);
			}
		}
		try {
			if(null != exception) {
				org.springframework.web.util.WebUtils.exposeErrorRequestAttributes(req, 
						exception, "mbank");
			}
			req.getRequestDispatcher("/WEB-INF/page/error/500.jsp")
				.forward(req, resp);
		} catch (Exception e) {
			logger.error("跳转500网页出错", e);
		}
	}
}
