package cn.com.yitong.core.session.dao;

import cn.com.yitong.common.persistence.annotation.MyBatisDao;
import cn.com.yitong.common.persistence.mybatis.MybatisBaseDao;
import cn.com.yitong.core.session.model.MhhSession;

/**
 * @Description: 会话数据表
 * @author kwang@yitong.com.cn
 */
@MyBatisDao
public interface MhhSessionDao extends MybatisBaseDao<MhhSession> {

    public void deleteSessionToBack(String sessionId);
}
