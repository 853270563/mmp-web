package cn.com.yitong.modules.ares.fileBreakUpload.controller;

import cn.com.yitong.consts.AppConstants;
import cn.com.yitong.consts.Properties;
import cn.com.yitong.framework.base.IBusinessContext;
import cn.com.yitong.framework.core.bean.BaseControl;
import cn.com.yitong.framework.util.CtxUtil;
import cn.com.yitong.modules.ares.fileBreakUpload.util.FileUploader;
import cn.com.yitong.modules.ares.fileBreakUpload.util.MD5BigFileUtil;
import cn.com.yitong.util.YTLog;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * 断点续传影像zip文件
 * @author zhanglong
 * @since 2017-08-04
 */
@Controller
public class FileUploadBreadpointController extends BaseControl{

	private Logger logger = YTLog.getLogger(this.getClass());
	/**
	 * MD5变量名md5
	 */
	private static final String PARAM_NAME_MD5 = "md5";
	/**
	 * 返回给客户端的变量名contentRange
	 */
	private static final String PARAM_NAME_CONTENT_RANGE = "contentRange";
	/**
	 * 临时MD5文件存放目录
	 */
	public static final String TEMP_PATH = Properties.getString("file_break_upload_path");

	/**
	 * 生成zip包文件存放目录
	 */
	public static final String SAVE_PATH = TEMP_PATH;

	/**
	 * 检测上传ZIP包的大小
	 */
	@RequestMapping("/fileUpload/breakpoint/checkFile.do")
	@ResponseBody
	public Map<String, Object> checkFile(HttpServletRequest request) {
		logger.info("enter checkFile 文件上传：查询已上传文件字节开始");
		// 初始化数据总线
		IBusinessContext ctx = CtxUtil.createMapContext(request);
		ctx.initParamCover(json2MapParamCover, false);

		String fileMd5 = ctx.getParam(PARAM_NAME_MD5);
		if (StringUtils.isEmpty(fileMd5)) {
			return createResult(-1, "请求失败，md5值为空");
		}
		logger.info("inter checkFile 文件上传：查询已上传文件字节结束,fileMd5：" + fileMd5);
		FileUploader fileUploader = new FileUploader(TEMP_PATH + fileMd5, SAVE_PATH + fileMd5);

		Map<String, Object> resulstMap = createResult(1);
		long fileLength = fileUploader.getNodePosition();
		File file = new File(fileUploader.getDestFileName());
		if(file.exists()) {
			fileLength = file.length();
		}
		resulstMap.put(PARAM_NAME_CONTENT_RANGE, fileLength + "");
		logger.info("exit checkFile 文件上传：查询已上传文件字节结束。字节数：" + fileLength);
		return resulstMap;
	}

	/**
	 * 上传ZIP文件
	 */
	@RequestMapping("/fileUpload/breakpoint/zipUpload.do")
	@ResponseBody
	public Map<String, Object> zipUpload(@RequestParam("file") MultipartFile upload, HttpServletRequest request)
			throws UnsupportedEncodingException {
		Map<String, Object> resulstMap = createResult(1);
		logger.info("enter zipUpload 文件上传开始");
		String fileMd5 = request.getParameter(PARAM_NAME_MD5);
		if (StringUtils.isEmpty(fileMd5)) {
			return createResult(-1, "上传失败，md5值为空");
		}

		String contentRange = request.getParameter(PARAM_NAME_CONTENT_RANGE);
		if (StringUtils.isEmpty(contentRange)) {
			return createResult(-1, "上传失败，contentRange值为空");
		}
		FileUploader fileUploader = new FileUploader(TEMP_PATH + fileMd5, SAVE_PATH + fileMd5);
		// contentRange验证
		if (!validateContentRange(contentRange, fileUploader)) {
			return createResult(-1, "上传失败，Content-Range值非法");
		}

		// 保存文件至临时文件
		InputStream inputStream = null;
		try {
			inputStream = upload.getInputStream();
			fileUploader.saveFile(inputStream);
		} catch (IOException e) {
			return createResult(-1, "文件上传失败，获取输入流异常：" + e.getMessage());
		}

		String filePath = TEMP_PATH + fileMd5;
		File fileZip = new File(filePath + ".tem");
		if (!fileZip.exists()) {
			logger.error("临时文件不存在");
			return createResult(-1, "文件上传失败，临时文件生成失败");
		}
		String servZipFileMd5 = MD5BigFileUtil.fileMd5(fileZip);

		long nodePosition = 0L;
		// 判断客户端传送数据与原始数据的大小 若是相同-已经传完
		if (servZipFileMd5.equals(fileMd5)) {
			logger.info("inter zipUpload zip文件上传完成，文件名-MD5:" + fileMd5);
			try {
				// 上传完成 把临时文件zip 转移到正式文件夹下 并删除临时文件
				if (fileUploader.moveFileToUpLoaded()) {
					logger.info("临时文件移到正式文件完成。。。。MD5:" + fileMd5);
					File file = new File(SAVE_PATH + fileMd5 + ".zip");
					if (!file.exists()) {
						return createResult(-1, "移动正式文件不存在，请检查");
					}
					nodePosition = file.length();
				} else {
					return createResult(-1, "移动文件异常");
				}
			} catch (Exception e) {
				logger.error("---Exception-----" + e);
				return createResult(-1, "移动文件异常:" + e.getMessage());
			}
		}else {
			//没有传完
			logger.info("inter zipUpload zip文件未全部上传，文件名-MD5:" + fileMd5);
			nodePosition = fileUploader.getNodePosition();
		}
		logger.info("exit zipUpload 文件上传：已经上传字节数：" + nodePosition);
		resulstMap.put(PARAM_NAME_CONTENT_RANGE, nodePosition + "");
		return resulstMap;
	}

	/**
	 * 验证请求参数contentRange值跟服务器临时文件是否一致
	 */
	private boolean validateContentRange(String contentRangeStr, FileUploader fileUploader) {
		long contentRange = 0;
		try {
			contentRange = Long.parseLong(contentRangeStr);
		} catch (Exception e) {
			logger.error("上传Content-Range值非法：{}", e);
			return false;
		}
		if (fileUploader.getNodePosition() != contentRange) {
			return false;
		}
		return true;
	}

	private Map<String, Object> createResult(int resultCode) {
		Map<String, Object> rst = new HashMap<String, Object>();
		if(resultCode == 1){
			rst.put(AppConstants.MSG, AppConstants.MSG_SUCC);
			rst.put(AppConstants.STATUS, AppConstants.STATUS_OK);
		}else{
			rst.put(AppConstants.MSG, AppConstants.MSG_FAIL);
			rst.put(AppConstants.STATUS, AppConstants.STATUS_FAIL);
		}
		return rst;
	}

	private Map<String, Object> createResult(int resultCode, String resultMessage) {
		Map<String, Object> rst = new HashMap<String, Object>();
		if(resultCode == 1){
			rst.put(AppConstants.MSG, resultMessage);
			rst.put(AppConstants.STATUS, AppConstants.STATUS_OK);
		}else{
			rst.put(AppConstants.MSG, resultMessage);
			rst.put(AppConstants.STATUS, AppConstants.STATUS_FAIL);
		}
		return rst;
	}
}
