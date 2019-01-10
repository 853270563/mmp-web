package cn.com.yitong.ares.fileservice.dao;

import cn.com.yitong.common.persistence.annotation.MyBatisDao;

import java.util.List;
import java.util.Map;

/**
 * Created by zhuzengpeng on 2015/11/24.
 */
@MyBatisDao
public interface SysFileDao {

    /**
     * 根据sessionId取得此SESSION的AES加密密钥SKEY
     * @param map
     * @return
     */
    List<Map<String, Object>> getSkeyBySessionId(Map<String, Object> map);
}
