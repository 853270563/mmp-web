package cn.com.yitong.modules.common.deviceCrash.dao;

import cn.com.yitong.common.persistence.mybatis.MybatisBaseDao;
import cn.com.yitong.modules.common.deviceCrash.model.DeviceCrashLog;

import cn.com.yitong.common.persistence.annotation.MyBatisDao;

/**
 * @Description: 
 * @author zhanglong@yitong.com.cn
 */
@MyBatisDao
public interface DeviceCrashLogDao extends MybatisBaseDao<DeviceCrashLog> {
}
