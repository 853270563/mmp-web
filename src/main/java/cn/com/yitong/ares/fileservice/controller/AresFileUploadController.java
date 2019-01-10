package cn.com.yitong.ares.fileservice.controller;

import cn.com.yitong.ares.fileservice.dao.SysFileDao;
import cn.com.yitong.ares.fileservice.support.FileUploadUtils;
import cn.com.yitong.common.utils.StringUtils;
import cn.com.yitong.common.utils.WebUtils;
import cn.com.yitong.framework.base.IBusinessContext;
import cn.com.yitong.framework.core.bean.BaseControl;
import cn.com.yitong.framework.core.bean.BusinessContext;
import cn.com.yitong.framework.net.IEBankConfParser;
import cn.com.yitong.framework.net.IRequstBuilder;
import cn.com.yitong.framework.net.IResponseParser;
import cn.com.yitong.framework.util.CtxUtil;
import cn.com.yitong.tools.crypto.RSAFactory;
import com.alibaba.fastjson.JSON;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lc3@yitong.com.cn
 */
@Controller
@RequestMapping("/ares/fileserver/")
public class AresFileUploadController extends BaseControl {

    private Logger logger = LoggerFactory.getLogger(AresFileUploadController.class);

    @Autowired
    @Qualifier("requestBuilder4db")
    IRequstBuilder requestBuilder;// 请求报文生成器
    @Autowired
    @Qualifier("responseParser4db")
    IResponseParser responseParser;// 响应报文解析器
    @Autowired
    @Qualifier("EBankConfParser4db")
    IEBankConfParser confParser;// 报文装载器

    @Autowired
    private SysFileDao fileDao;

    /**
     * 文件范围参数名称
     */
    private static final String NAME_CONTENT_RANGE = "Content-Range";
    //文件上传时在请求头传入文件的MD5
    private static final String HEADER_CHECKSUM = "Checksum";
    private static final String HEADER_CONTENT_LENGTH = "Content-Length";
    private static final String HEADER_FILENAME = "Filename";
    private static final String HEADER_FILELENGTH = "FileLength";

    /**
     * 文件上传前检查状态
     * @param request
     * @return
     */
    @RequestMapping("getFilesStatus")
    @ResponseBody
    public Map<String, Object> fileStatusCheck(HttpServletRequest request) {
        String transCode = "ares/fileservice/getFilesStatus";
        Map<String, Object> rst = new HashMap<String, Object>();
        // 初始化数据总线
        IBusinessContext ctx = new BusinessContext(request,
                IBusinessContext.PARAM_TYPE_MAP);
        // 检查报文定义
        if (!CtxUtil.transPrev(ctx, transCode, json2MapParamCover,
                requestBuilder, confParser, rst)) {
            return rst;
        }
        Map<String,Object> params = ctx.getParamMap();// 获取总线中参数Map
        try{
            String filestr = params.get("files") == null? "":params.get("files").toString();
            String willUploadFile = params.get("willUploadFile").toString();
            String[] fileList = filestr.split(",");
            List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
            for (String fileName : fileList) {
                if(org.springframework.util.StringUtils.hasText(fileName)) {
                    Map<String, Object> checkMap = FileUploadUtils.checkFileStatus(willUploadFile, fileName,
                            params.get("BUSI_ID") ==null? "":params.get("BUSI_ID").toString(), params.get("BUSI_TYPE")==null?"":params.get("BUSI_TYPE").toString());
                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put("fileName", fileName);
                    map.put("length", checkMap.get("length"));
                    map.put("modificationTime", "");
                    map.put("checksum", checkMap.get("checksum"));
                    map.put("status", checkMap.get("status"));
                    list.add(map);
                }
            }
            rst.put("files", list);
            return WebUtils.returnSuccessMsg(rst, null);
        }catch (Exception e) {
            return WebUtils.returnErrorMsg(rst, "服务端处理异常");
        }
    }


    @RequestMapping("uploadFile")
    @ResponseBody
    public Map<String, Object> uploadFile(@RequestParam MultipartFile file, HttpServletRequest request) {
         logger.info("文件开始上传,file.getSize():{}", file.getSize());
        Assert.notNull(file, "file不能为空");
        Map<String, Object> rst = new HashMap<String, Object>();
        String range = request.getHeader(NAME_CONTENT_RANGE);
        String checksum = request.getHeader(HEADER_CHECKSUM);
        String fileName = request.getHeader(HEADER_FILENAME);
        String fileLength = request.getHeader(HEADER_FILELENGTH);
        if(StringUtils.isBlank(range)) {
            range = WebUtils.findParameterValue(request, NAME_CONTENT_RANGE);
        }
        long offset = 0;
        long contentLength = 0;
        if(StringUtils.isNotBlank(range)) {
            final String offsetStr = range.split("-")[0];
            if(StringUtils.isBlank(offsetStr)) {
                return WebUtils.returnErrorMsg(rst, NAME_CONTENT_RANGE + "参数值不合法，应为已上传的进度");
            }
            offset = Long.valueOf(offsetStr);
            contentLength = Long.valueOf(fileLength);//客户端上传的时候此值减1了
        }
        if(StringUtils.isBlank(fileName) &&
                StringUtils.isBlank(fileName = WebUtils.findParameterValue(request, HEADER_FILENAME))) {
            fileName = file.getOriginalFilename();
        }
        try {
            final boolean saveFile = FileUploadUtils.saveFile(file.getInputStream(), fileName, offset, contentLength, checksum);
            String msg = null;
            if(!saveFile) {
                msg = "上传未完成，请继续上传";
            }
            return WebUtils.returnSuccessMsg(rst, msg);
        } catch (IOException e) {
            logger.debug("上传文件失败，失败原因为：" + e.getMessage(), e);
            return WebUtils.returnErrorMsg(rst, "上传文件失败，失败原因为：" + e.getMessage());
        }
    }

    /**
     * 单个文件下载
     * @return
     */
    @RequestMapping("getFile")
    @ResponseBody
    public void getFile(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String transCode = "ares/fileservice/getFile";
        Map<String, Object> rst = new HashMap<String, Object>();
        // 初始化数据总线
        IBusinessContext ctx = new BusinessContext(request,
                IBusinessContext.PARAM_TYPE_MAP);
        // 检查报文定义
        if (!CtxUtil.transPrev(ctx, transCode, json2MapParamCover,
                requestBuilder, confParser, rst)) {
            response.getWriter().write(JSON.toJSONString(rst));
            return;
        }
        Map<String,Object> params = ctx.getParamMap();// 获取总线中参数Map
        String filename = params.get("file").toString();
        String token = params.get("token").toString();
        //对TOKEN解密
        try {
            String sessionId = token.substring(16, 32);
            String token_filenamemd5 = token.substring(32).toLowerCase();
            String skey = getSkeyBySessionId(sessionId);
            skey = RSAFactory.getDefaultCodec().decrypt(skey);
            String fileNameSkeyMd5 = fileNameSkeyToMd5(filename + skey).toLowerCase();
            if(!token_filenamemd5.equals(fileNameSkeyMd5)) {
                rst.put("MSG", "不允许下载,文件名与加密后的文件名不匹配");
                response.getWriter().write(JSON.toJSONString(rst));
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
            rst.put("MSG", "TOKEN解密失败，失败原因为：" + e.getMessage());
            response.getWriter().write(JSON.toJSONString(rst));
            return;
        }
        try {
            File file = FileUploadUtils.getServerFile(filename);
            if(file != null) {
                response.setContentLength(Integer.valueOf(String.valueOf(file.length())));
                FileCopyUtils.copy(new FileInputStream(file), response.getOutputStream());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        response.getWriter().write(JSON.toJSONString(rst));
    }


    @RequestMapping("getFiles")
    @ResponseBody
    public Map<String, Object> getFiles(String[] files, String key) {
        Map<String, Object> rst = new HashMap<String, Object>();

        return rst;
    }

    /**
     * 单个文件删除
     * @return
     */
    @RequestMapping("delFile")
    @ResponseBody
    public Map<String, Object> delFile(HttpServletRequest request) {
        String transCode = "ares/fileservice/delFile";
        Map<String, Object> rst = new HashMap<String, Object>();
        // 初始化数据总线
        IBusinessContext ctx = new BusinessContext(request,
                IBusinessContext.PARAM_TYPE_MAP);
        // 检查报文定义
        if (!CtxUtil.transPrev(ctx, transCode, json2MapParamCover,
                requestBuilder, confParser, rst)) {
            return rst;
        }
        Map<String,Object> params = ctx.getParamMap();// 获取总线中参数Map
        FileUploadUtils.delFile(params.get("file").toString());
        return WebUtils.returnSuccessMsg(rst, "删除成功");
    }

    /**
     * 多个文件删除
     * @param request
     * @return
     */
    @RequestMapping("delFiles")
    @ResponseBody
    public Map<String, Object> delFiles(HttpServletRequest request) {
        String transCode = "ares/fileservice/delFiles";
        Map<String, Object> rst = new HashMap<String, Object>();
        // 初始化数据总线
        IBusinessContext ctx = new BusinessContext(request,
                IBusinessContext.PARAM_TYPE_MAP);
        // 检查报文定义
        if (!CtxUtil.transPrev(ctx, transCode, json2MapParamCover,
                requestBuilder, confParser, rst)) {
            return rst;
        }
        Map<String,Object> params = ctx.getParamMap();// 获取总线中参数Map
        String files = params.get("files").toString();
        String[] filess = files.split(",");
        for(String file:filess) {
            FileUploadUtils.delFile(file);
        }
        return WebUtils.returnSuccessMsg(rst, "删除成功");
    }

    @ExceptionHandler
    @ResponseBody
    public Map<String, Object> exceptionHandler(Throwable e, HttpServletRequest request, HttpServletResponse response) {
        logger.error("操作异常", e);
        return WebUtils.returnErrorMsg(null, "操作失败，失败原因为：" + e.getMessage());
    }

    public static void main(String[] args) {
//        Hex hex = new Hex();
//        String str = "abc123";
//        char[] enbytes = null;
//        String encodeStr = null;
//        byte[] debytes = null;
//        String decodeStr = null;
//        try {
//            enbytes = hex.encodeHex(str.getBytes());
//            encodeStr = new String(enbytes);
//            debytes = hex.decodeHex(enbytes);
//            decodeStr = new String(debytes);
//        } catch (Exception ex) {
//            ;
//        }
//        System.out.println("编码前:"+str);
//        System.out.println("编码后:"+encodeStr);
//        System.out.println("解码后:" + decodeStr);
        try {
            String a = fileNameSkeyToMd5("creditcard/201500001_1.jpge990d92542b74fb7b493677a0759238b");
            System.out.println(a);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 把字符串+当前SESSION的AESKEY通过MD5加密
     * @param encodeStr
     * @return
     * @throws org.apache.commons.codec.DecoderException
     * @throws java.security.NoSuchAlgorithmException
     */
    private static String fileNameSkeyToMd5(String encodeStr) throws DecoderException, NoSuchAlgorithmException {
        MessageDigest md5 = null;
        Hex hex = new Hex();
        md5 = MessageDigest.getInstance("MD5");
        String str = "";
        byte[] md5Bytes = md5.digest(encodeStr.getBytes());
        char[] enbytes = hex.encodeHex(md5Bytes);
        str = new String(enbytes);
        return str;
    }

    /**
     * 根据sessionId从数据库中查询取得此SESSION的SKEY
     * @param sessionId
     * @return
     */
    private String getSkeyBySessionId(String sessionId) {
        Map<String, Object> requestParams = new HashMap<String, Object>();
        requestParams.put("sessionId", sessionId);
        List<Map<String, Object>> list = fileDao.getSkeyBySessionId(requestParams);
        if(list.isEmpty()) {
            return "";
        }
        return list.get(0).get("skey").toString();
    }

}
