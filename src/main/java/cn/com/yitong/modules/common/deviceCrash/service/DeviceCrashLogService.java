package cn.com.yitong.modules.common.deviceCrash.service;

import cn.com.yitong.common.utils.ConfigUtils;
import cn.com.yitong.common.persistence.mybatis.MybatisBaseDao;
import cn.com.yitong.common.service.mybatis.MybatisBaseService;
import cn.com.yitong.modules.common.deviceCrash.dao.DeviceCrashLogDao;
import cn.com.yitong.modules.common.deviceCrash.model.DeviceCrashLog;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 *
 * @author zhanglong@yitong.com.cn
 */
@Service
public class DeviceCrashLogService extends MybatisBaseService<DeviceCrashLog> {

    @Resource
    private DeviceCrashLogDao deviceCrashLogDao;

    @Override
    public String getTableName() {
        return ConfigUtils.getValue("schema.interPlat") + ".DEVICE_CRASH_LOG";
    }

    @Override
    public String getIdKey() {
        return "logId";
    }

    @Override
    public MybatisBaseDao<DeviceCrashLog> getDao() {
        return deviceCrashLogDao;
    }
}
