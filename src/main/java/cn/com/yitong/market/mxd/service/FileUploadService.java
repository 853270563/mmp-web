package cn.com.yitong.market.mxd.service;

import java.io.File;

/**
 * Created by QianJH on 2015-7-17.
 */
public interface FileUploadService {

    /**
     * 上传
     * @param caseId 案件编号
     * @param businessType 业务类型
     * @param file zip文件
     * @throws Exception
     */
    void upload(String caseId, String businessType, File file) throws Exception;
}
