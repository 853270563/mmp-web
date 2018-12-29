package cn.com.yitong.modules.ares.fileservice.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import cn.com.yitong.common.utils.WebUtils;
import cn.com.yitong.framework.base.IBusinessContext;
import cn.com.yitong.framework.core.bean.BusinessContext;
import cn.com.yitong.framework.net.IEBankConfParser;
import cn.com.yitong.framework.net.IParamCover;
import cn.com.yitong.framework.net.IRequstBuilder;
import cn.com.yitong.framework.util.CtxUtil;
import cn.com.yitong.modules.ares.fileBreakUpload.controller.FileUploadBreadpointController;
import cn.com.yitong.modules.ares.fileBreakUpload.util.FileUploader;
import cn.com.yitong.modules.ares.fileservice.service.FileUnZipService;
import cn.com.yitong.modules.ares.fileservice.service.ICustomFileMngService;
import cn.com.yitong.modules.service.fileTab.model.AresFileTab;
import cn.com.yitong.modules.service.fileTab.service.AresFileTabService;
import cn.com.yitong.util.CustomFileType;
import cn.com.yitong.util.StringUtil;

/**
 * @author sunw@yitong.com.cn
 */
@Controller
@RequestMapping("/ares/file/")
public class CommonFileController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	@Qualifier("json2MapParamCover")
	protected IParamCover json2MapParamCover;
	@Autowired
	@Qualifier("requestBuilder4db")
	IRequstBuilder requestBuilder;// 请求报文生成器
	@Autowired
	@Qualifier("EBankConfParser4db")
	IEBankConfParser confParser;// 报文装载器
    
    @Autowired
    private ICustomFileMngService customFileMngService;

	@Autowired
	private FileUnZipService fileUnZipService;

	@Autowired
	private AresFileTabService aresFileTabService;

	/**
	 * 文件zip上传
	 * @param request
	 * @param response
	 * @return
	 */
    @RequestMapping("upload.do")
	@ResponseBody
    public Map<String, Object> uploadFile(MultipartHttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> rst = new HashMap<String, Object>();
    	Map<String, MultipartFile> multiFileMap = request.getFileMap();
    	if (multiFileMap == null || multiFileMap.isEmpty()) {
			return WebUtils.returnErrorMsg(rst, "上传空文件异常");
    	}
    	//获取流水号
    	String serialNo = request.getHeader("serialNo");
    	serialNo = StringUtils.isEmpty(serialNo) ? request.getParameter("serialNo") : serialNo;
    	if (StringUtil.isEmpty(serialNo)){
			return WebUtils.returnErrorMsg(rst, "业务流水空异常");
		}
		Set<Entry<String, MultipartFile>> entrySet = multiFileMap.entrySet();
		// 循环保存文件
		try {
			AresFileTab aresFile = null;
			for (Entry<String, MultipartFile> entry : entrySet) {
				MultipartFile file = entry.getValue();
				String name = file.getOriginalFilename();
				String extName = name.substring(name.lastIndexOf(".") + 1).toLowerCase();
				CustomFileType fileType = CustomFileType.getType(extName);
				Map<String, String> map = customFileMngService.saveMultipartFile(file, fileType, serialNo);
				Set<Entry<String, String>> entrySet2 = map.entrySet();
				for(Entry<String, String> entry2 : entrySet2){
					map = new HashMap<String, String>();
					aresFile = new AresFileTab();
					aresFile.setId(UUID.randomUUID().toString().replaceAll("-", ""));
					aresFile.setFileName(entry2.getKey());
					aresFile.setFileSize(new BigDecimal(entry2.getValue()));
					aresFile.setSerialNo(serialNo);
					aresFile.setCreateTime(new Date());
					aresFileTabService.save(aresFile);
				}
			}
		} catch (Exception e) {
			logger.error("文件保存失败："+ e);
			return WebUtils.returnErrorMsg(rst, "文件上传失败");
		}
		return WebUtils.returnSuccessMsg(rst, "文件上传成功");
    }

	/**
	 * 文件断点续传后 的业务处理
	 */
	@RequestMapping("breakUploadTrans.do")
	@ResponseBody
	public Map<String, Object> breakUploadTrans(HttpServletRequest request) {
		Map<String, Object> rst = new HashMap<String, Object>();
		String transCode = "ares/fileBreakUpload/breakUploadTrans";
		// 初始化数据总线
		IBusinessContext ctx = new BusinessContext(request, IBusinessContext.PARAM_TYPE_MAP);
		// 检查报文定义
		if (!CtxUtil.transPrev(ctx, transCode, json2MapParamCover, requestBuilder, confParser, rst)) {
			return rst;
		}

		Map paramMap = ctx.getParamMap();
		String imageSerialNo = (String)paramMap.get("imageSerialNo");
		String fileMd5 = (String)paramMap.get("md5");
		if(StringUtil.isEmpty(imageSerialNo) || StringUtil.isEmpty(fileMd5)) {
			logger.error("文件Md5值或者影像流水号为空，请检查imageSerialNo={},md5={}", imageSerialNo, fileMd5);
			return WebUtils.returnErrorMsg(rst, "文件Md5值或者影像流水号为空，请检查");
		}

		logger.info("影像业务逻辑处理，imageSerialNo={},md5={}", imageSerialNo, fileMd5);
		FileUploader uploader = new FileUploader(FileUploadBreadpointController.SAVE_PATH + fileMd5);
		File zipFile = new File(uploader.getDestFileName());
		if(!zipFile.exists()) {
			logger.error("根据文件MD5值未找到zip文件,md5={}", fileMd5);
			return WebUtils.returnErrorMsg(rst, "zip文件未找到，请检查");
		}
		if(!fileUnZipService.zipFileSave(imageSerialNo, zipFile)) {
			return WebUtils.returnErrorMsg(rst, "业务关联影像失败");
		}
		return WebUtils.returnSuccessMsg(rst, "上传影像处理成功");
	}
	

	/**
	 * 图片base64下载
	 */
	@RequestMapping("download.do")
	@ResponseBody
	public Map<String, Object> download(HttpServletRequest request) {
		Map<String, Object> rst = new HashMap<String, Object>();
		String transCode = "ares/file/download";
		// 初始化数据总线
		IBusinessContext ctx = new BusinessContext(request, IBusinessContext.PARAM_TYPE_MAP);
		// 检查报文定义
		if (!CtxUtil.transPrev(ctx, transCode, json2MapParamCover, requestBuilder, confParser, rst)) {
			return rst;
		}

		Map paramMap = ctx.getParamMap();
		String fileName = (String) paramMap.get("FILE_NAME");
		
		InputStream in = null;
		byte[] data = null;
		try {
			CustomFileType fileType = CustomFileType.valueOf("IMG");
			// fileName = URLEncoder.encode(fileName, "UTF-8");
			File file = customFileMngService.getFileByFileName(fileName, fileType);
			if (!file.exists()) {
				logger.error("文件不存在，{}", file.getAbsolutePath());
				return WebUtils.returnErrorMsg(rst, "文件不存在");
			}
			in = new FileInputStream(file);        
		    data = new byte[in.available()];
			in.read(data);
			String base64 = new String(Base64.encodeBase64(data));
			rst.put("FILE_BASE64", base64);
		} catch (Exception e) {
			logger.error(e.getMessage());
			return WebUtils.returnErrorMsg(rst, "文件下载失败");
		} finally {
			IOUtils.closeQuietly(in);
		}
		
		return WebUtils.returnSuccessMsg(rst, "图片下载成功");
	}
}
