package cn.com.yitong.market.mxd.service.impl;

import java.io.File;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import cn.com.yitong.market.mxd.service.FileUploadService;
import cn.com.yitong.market.mxd.util.Utils;
import cn.com.yitong.util.YTLog;

/**
 * 文件上传服务
 * Created by QianJH on 2015-7-17.
 */
@Service
public class FileUploadServiceImpl implements FileUploadService{

    private Logger logger = YTLog.getLogger(this.getClass());
    //文件解压后路径
    private String uploadFilesPath = cn.com.yitong.consts.Properties.getString("upload_files_path");

    /**
     * 上传
     *
     * @param caseId
     * @param businessType
     * @param file
     */
    @Override
    public void upload(String caseId, String businessType, File file) throws Exception {
        //解压zip文件
        Map<String, File> files = unzip(file);
        //将解压后的文件入库
        save(caseId, files);
        //当影像类型为征信材料时，变更案件状态为02(待查询)
        if (Utils.XW_ZXCX_FILE_ATTR_TYPE.equals(businessType)) {//征信查询
           //TODO 更新状态
        }
    }

    /**
     * 保存入库
     * @param caseId
     * @param files
     */
    private void save(String caseId, Map<String, File> files) throws Exception {
        //TODO 入库
    }

    /**
     * 解压文件
     *
     * @param file
     * @return
     * @throws Exception
     */
    private Map<String, File> unzip(File file) throws Exception {
        Map<String, File> files = null;
        //TODO 解压zip文件

        return files;
    }

}
