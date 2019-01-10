package cn.com.yitong.core.session.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.util.StringUtils;

import cn.com.yitong.common.persistence.mybatis.impl.CriteriaQuery;
import cn.com.yitong.common.utils.ConfigUtils;
import cn.com.yitong.common.utils.ServerUtils;
import cn.com.yitong.core.session.Session;
import cn.com.yitong.core.session.model.MhhSession;
import cn.com.yitong.core.session.util.SessionConverts;
import cn.com.yitong.core.util.ConfigName;

/**
 * @author lc3@yitong.com.cn
 */
public class DbSessionDao extends AbstractSessionDao {
    @Resource
    private MhhSessionDao mhhSessionDao;

    @Override
    public void create(Session session) {
        if(null == session) {
            return;
        }
        mhhSessionDao.insert(SessionConverts.session2AresSession(session));
    }

    @Override
    public void delete(Session session) {
        if(null == session) {
            return;
        }
		//mhhSessionDao.deleteSessionToBack(session.getId());
        mhhSessionDao.deleteById(session.getId());
    }

    @Override
    public void update(Session session) {
        if(null == session) {
            return;
        }
        mhhSessionDao.updateById(SessionConverts.session2AresSession(session));
        session.setVersion(session.getVersion() + 1);
    }

    @Override
    public Session get(String id) {
        if(null == id || !StringUtils.hasText(id)) {
            return null;
        }
        MhhSession mhhSession = mhhSessionDao.queryById(id);
        return SessionConverts.aresSession2Session(mhhSession);
    }

    @Override
    public Collection<Session> getAllSession() {
        CriteriaQuery query = new CriteriaQuery();
        query.createAndCriteria().equalTo(MhhSession.TF.serverIp, ServerUtils.getServerIp());
        List<MhhSession> mhhSessions = mhhSessionDao.queryByCriteria(query);
        if(null != mhhSessions && !mhhSessions.isEmpty()) {
            List<Session> list = new ArrayList<Session>(mhhSessions.size());
            for (MhhSession mhhSession : mhhSessions) {
                list.add(SessionConverts.aresSession2Session(mhhSession));
            }
            return list;
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public Collection<Session> getActiveSessions() {
        CriteriaQuery query = new CriteriaQuery();
        query.createAndCriteria().equalTo(MhhSession.TF.serverIp, ServerUtils.getServerIp());
        query.createAndCriteria().sql(MhhSession.TF.invalidTime + " >= ?", new Date());
        List<MhhSession> mhhSessions = mhhSessionDao.queryByCriteria(query);
        if(null != mhhSessions && !mhhSessions.isEmpty()) {
            List<Session> list = new ArrayList<Session>(mhhSessions.size());
            for (MhhSession mhhSession : mhhSessions) {
                list.add(SessionConverts.aresSession2Session(mhhSession));
            }
            return list;
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public Collection<Session> getInvalidSessions() {
        int timeOut = ConfigUtils.getValue(ConfigName.SESSION_TIMEOUT_SECOND,
                ConfigName.SESSION_TIMEOUT_SECOND_DEFVAL);
        CriteriaQuery query = new CriteriaQuery();
		query.createAndCriteria().equalTo(MhhSession.TF.serverIp, ServerUtils.getServerIp());
		query.createAndCriteria().sql(MhhSession.TF.invalidTime + " < ?", new Date());
        List<MhhSession> mhhSessions = mhhSessionDao.queryByCriteria(query);
        if(null != mhhSessions && !mhhSessions.isEmpty()) {
            List<Session> list = new ArrayList<Session>(mhhSessions.size());
            for (MhhSession mhhSession : mhhSessions) {
                list.add(SessionConverts.aresSession2Session(mhhSession));
            }
            return list;
        } else {
            return Collections.emptyList();
        }
    }
}
