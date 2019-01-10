package cn.com.yitong.modules.session.dao;

import cn.com.yitong.modules.session.model.AresSession;
import cn.com.yitong.core.base.dao.CriteriaExample;
import cn.com.yitong.core.base.dao.GenericDAOImpl;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author lc3@yitong.com.cn
 */
@Repository
public class AresSessionDaoImpl extends GenericDAOImpl<AresSession, String> {

    @Override
    public String getIbatisNamespace() {
        return "ARES_SESSION";
    }

    @Override
    public int updateByPrimaryKey(AresSession record) {
        int i = super.updateByPrimaryKey(record);
        record.setVersion(record.getVersion() + 1);
        return i;
    }

    @Override
    public int updateByPrimaryKeySelective(AresSession record) {
        int i = super.updateByPrimaryKeySelective(record);
        record.setVersion(record.getVersion() + 1);
        return i;
    }

    /**
     * 通过用户Id查询
     * @param userId
     * @return
     */
    public List<AresSession> queryByUserId(String userId) {
        CriteriaExample<AresSession> query = new CriteriaExample<AresSession>();
        query.createCriteria().equalTo(AresSession.FL.userId, userId);
        return selectByExample(query);
    }

    /**
     * 通过用户Id查询第一条记录
     * @param userId
     * @return
     */
    public AresSession queryOneByUserId(String userId) {
        List<AresSession> aresSessions = queryByUserId(userId);
        if(null != aresSessions && !aresSessions.isEmpty()) {
            return aresSessions.get(0);
        } else {
            return null;
        }
    }

    /**
     * 注销其他用户
     * @param userId
     */
    public void logoutAllByUserId(String userId) {
        CriteriaExample<AresSession> query = new CriteriaExample<AresSession>();
        query.createCriteria().equalTo(AresSession.FL.userId, userId);
        AresSession aresSession = new AresSession();
        aresSession.setAuthStatus("0");
        updateByExampleSelective(aresSession, query);
    }
}
