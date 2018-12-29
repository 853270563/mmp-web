package cn.com.yitong.modules.service.fileTab.service;

import cn.com.yitong.common.utils.ConfigUtils;
import cn.com.yitong.common.persistence.mybatis.MybatisBaseDao;
import cn.com.yitong.common.service.mybatis.MybatisBaseService;
import cn.com.yitong.modules.service.fileTab.dao.AresFileTabDao;
import cn.com.yitong.modules.service.fileTab.model.AresFileTab;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 *
 * @author sunw@yitong.com.cn
 */
@Service
public class AresFileTabService extends MybatisBaseService<AresFileTab> {

    @Resource
    private AresFileTabDao aresFileTabDao;

    @Override
    public String getTableName() {
        return ConfigUtils.getValue("schema.configPlat") + ".ARES_FILE_TAB";
    }

    @Override
    public String getIdKey() {
        return "id";
    }

    @Override
    public MybatisBaseDao<AresFileTab> getDao() {
        return aresFileTabDao;
    }
}
