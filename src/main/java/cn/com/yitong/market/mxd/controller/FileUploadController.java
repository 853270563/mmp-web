package cn.com.yitong.market.mxd.controller;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import cn.com.yitong.market.mxd.service.FileUploadService;
import cn.com.yitong.market.mxd.util.FileUploader;
import cn.com.yitong.util.YTLog;

/**
 * 文件断点续传Controller
 *
 * @author hry@yitong.com.cn
 * @date 2015年4月24日
 */
@Controller
@RequestMapping("mxd")
public class FileUploadController {

    private Logger logger = YTLog.getLogger(this.getClass());
    private static final String PARAM_NAME_MD5 = "md5";
    private static final String PARAM_NAME_CONTENT_RANGE = "contentRange";

    @Autowired
    private FileUploadService fileUploadService;

    /**
     * 查询文件已上传字节数
     *
     * @param request
     * @return
     */
    @RequestMapping("/uploadReq.do")
    @ResponseBody
    public Map<String, Object> uploadReq(HttpServletRequest request) {
        String fileMd5 = request.getParameter(PARAM_NAME_MD5);//文件MD5值
        if (StringUtils.isEmpty(fileMd5)) {
            logger.error("请求文件MD5值为空");
            return createResult(-1, "请求失败，md5值为空");
        }
        FileUploader fileUploader = new FileUploader(fileMd5);
        Map<String, Object> resulstMap = createResult(1);
        resulstMap.put("contentRange", fileUploader.getNodePosition());
        logger.info("文件[" + fileMd5 + "]已上传字节数[" + fileUploader.getNodePosition() + "]");
        return resulstMap;
    }

    /**
     * 文件上传
     * 请求类型multipart/form-data
     * 响应参数格式{"MSG":"交易成功!","STATUS":"1"}OR{"MSG":"交易失败!","STATUS":"0"}
     */
    @RequestMapping("/uploadFile.do")
    @ResponseBody
    public Map<String, Object> uploadFile(@RequestParam("file") MultipartFile upload,
                                          HttpServletRequest request) {
        final String fileMd5 = request.getParameter(PARAM_NAME_MD5);//md5值验证
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("clientNo", request.getParameter("clientNo"));
        map.put("imageNo", request.getParameter("imageNo"));
        map.put("transId", request.getParameter("transId"));
        map.put("xwFileType", request.getParameter("xwFileType"));
        map.put("fileUrl", FileUploader.SAVE_PATH);

        if (StringUtils.isEmpty(fileMd5)) {
            logger.error("上传文件MD5值为空");
            return createResult(-1, "上传失败，md5值为空");
        }
        String msgFile = "文件[" + fileMd5 + "]";
        logger.info("开始上传" + msgFile);

        FileUploader fileUploader = new FileUploader(fileMd5, upload.getOriginalFilename());
        //contentRange验证
        if (!validateContentRange(request.getParameter(PARAM_NAME_CONTENT_RANGE), fileUploader)) {
            logger.error("上传失败，Content-Range值非法");
            return createResult(-1, "上传失败，Content-Range值非法");
        }

        //保存文件至临时文件
        InputStream inputStream = null;
        try {
            //保存文件流
            inputStream = upload.getInputStream();
            fileUploader.saveFile(inputStream);

            //true：新文件，移动成功。false:文件已存在，无需移动
            boolean result = fileUploader.moveFileToUpLoaded();
            if (!result) {//重复上传，直接返回上传者成功，无需进行后续业务操作。
                return createResult(1);
            }
            logger.info("上传成功:" + msgFile);

            //上传成功后续处理
            afterOperation(fileMd5, map);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return createResult(-1, e.getMessage());
        }

        return createResult(1);
    }

    /**
     * 上传完成后续业务处理
     *
     * @throws Exception
     */
    private void afterOperation(String fileMd5, Map<String, Object> map) throws Exception {
        String transId = String.valueOf(map.get("transId"));
        String xwFileType = String.valueOf(map.get("xwFileType"));
        File file = new File(String.valueOf(map.get("fileUrl")), fileMd5 + ".zip");

        fileUploadService.upload(transId, xwFileType, file);
    }

    /**
     * 验证请求参数contentRange值跟服务器临时文件是否一致
     */
    private boolean validateContentRange(String contentRangeStr, FileUploader fileUploader) {
        long contentRange = 0;
        try {
            contentRange = Long.parseLong(contentRangeStr);
        } catch (Exception e) {
        }
        if (fileUploader.getNodePosition() != contentRange) {
            return false;
        }
        return true;
    }


    private final static String MSG_SUCC = "交易成功!";
    private final static String MSG_FAIL = "交易失败!";
    private final static String STATUS_OK = "1";
    private final static String STATUS_FAIL = "0";

    /**
     *
     * @param resultCode
     * @return
     */
    private Map<String, Object> createResult(int resultCode) {
        Map<String, Object> rst = new HashMap<String, Object>();
        if (resultCode == 1) {
            rst.put("MSG", MSG_SUCC);
            rst.put("STATUS", STATUS_OK);
        } else {
            rst.put("MSG", MSG_FAIL);
            rst.put("STATUS", STATUS_FAIL);
        }
        return rst;
    }

    /**
     *
     * @param resultCode
     * @param resultMessage
     * @return
     */
    private Map<String, Object> createResult(int resultCode, String resultMessage) {
        Map<String, Object> rst = new HashMap<String, Object>();
        if (resultCode == 1) {
            rst.put("MSG", resultMessage);
            rst.put("STATUS", resultMessage);
        } else {
            rst.put("MSG", resultMessage);
            rst.put("STATUS", resultMessage);
        }
        return rst;
    }

}
