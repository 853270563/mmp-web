package cn.com.yitong.portal.thirdApp.dao;

import cn.com.yitong.common.persistence.mybatis.MybatisBaseDao;
import cn.com.yitong.portal.thirdApp.model.AresAuthority;

import cn.com.yitong.common.persistence.annotation.MyBatisDao;

import java.util.Map;

/**
 * @Description: 授权许可表
 * @author lc3@yitong.com.cn
 */
@MyBatisDao
public interface AresAuthorityDao extends MybatisBaseDao<AresAuthority> {

    /**
     * 查询应用
     * @param appId
     * @return
     */
    public Map queryAppInfoByAppId(String appId);
}
