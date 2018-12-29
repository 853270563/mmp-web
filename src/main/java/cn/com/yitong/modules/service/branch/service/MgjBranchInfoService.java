package cn.com.yitong.modules.service.branch.service;

import cn.com.yitong.common.utils.ConfigUtils;
import cn.com.yitong.common.persistence.mybatis.MybatisBaseDao;
import cn.com.yitong.common.service.mybatis.MybatisBaseService;
import cn.com.yitong.modules.service.branch.dao.MgjBranchInfoDao;
import cn.com.yitong.modules.service.branch.model.MgjBranchInfo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 *
 * @author zhanglong@yitong.com.cn
 */
@Service
public class MgjBranchInfoService extends MybatisBaseService<MgjBranchInfo> {

    @Resource
    private MgjBranchInfoDao mgjBranchInfoDao;

    @Override
    public String getTableName() {
        return ConfigUtils.getValue("schema.interPlat") + ".MGJ_BRANCH_INFO";
    }

    @Override
    public String getIdKey() {
        return "brchId";
    }

    @Override
    public MybatisBaseDao<MgjBranchInfo> getDao() {
        return mgjBranchInfoDao;
    }
}
