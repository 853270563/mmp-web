package cn.com.yitong.modules.service.branch.dao;

import cn.com.yitong.common.persistence.annotation.MyBatisDao;
import cn.com.yitong.common.persistence.mybatis.MybatisBaseDao;
import cn.com.yitong.modules.service.branch.model.MgjBranchInfo;

import java.util.List;
import java.util.Map;

/**
 * @Description: 网点信息表
 * @author zhanglong@yitong.com.cn
 */
@MyBatisDao
public interface MgjBranchInfoDao extends MybatisBaseDao<MgjBranchInfo> {

    public List<MgjBranchInfo> queryBranchByGps(Map<String, Object> params);
}
