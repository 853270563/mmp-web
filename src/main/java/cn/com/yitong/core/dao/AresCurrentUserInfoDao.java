package cn.com.yitong.core.dao;

import cn.com.yitong.common.dao.CurrentUserInfoDao;
import cn.com.yitong.core.util.SecurityUtils;

/**
 * Ares通用当前用户查询接口
 * @author lc3@yitong.com.cn
 */
public class AresCurrentUserInfoDao implements CurrentUserInfoDao {

    @Override
    public String getId() {
        return SecurityUtils.getSessionRequired().getId();
    }

    @Override
    public String getName() {
        return getId();
    }

    @Override
    public String getLoginName() {
        return getId();
    }
}
