package cn.com.yitong.core.session.util;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.com.yitong.common.utils.JsonUtils;
import cn.com.yitong.core.session.Session;
import cn.com.yitong.core.session.model.MhhSession;
import cn.com.yitong.core.session.vo.SimpleSession;

/**
 * @author lc3@yitong.com.cn
 */
public class SessionConverts {

    private static Logger logger = LoggerFactory.getLogger(SessionConverts.class);

    /**
     * MscSession转SimpleSession
     * @param aresSession
     * @return
     */
    public static Session aresSession2Session(MhhSession aresSession) {
        if(null == aresSession) {
            return null;
        }
        SimpleSession.Builder builder = new SimpleSession.Builder();
        builder.setId(aresSession.getSessionId());
        builder.setAuthStatus(aresSession.getAuthStatus());
        builder.setUserId(aresSession.getUserId());
        builder.setServerId(aresSession.getServerIp());
        builder.setMsgId(aresSession.getMsgId());
        builder.setSkey(aresSession.getKey());
        builder.setEventId(aresSession.getEventId());
        builder.setDeviceCode(aresSession.getDeviceId());
        try {
            builder.setDatas(JsonUtils.jsonToMap(aresSession.getData()));
        } catch (Exception e) {
            logger.error("转换Session数据失败，转换数据为：" + aresSession.getData(), e);
        }
        builder.setCreateTime(aresSession.getCreateTime());
        builder.setLastAccessTime(aresSession.getVisitTime());
        long timeout = (aresSession.getInvalidTime().getTime() - aresSession.getVisitTime().getTime()) / 1000;
        builder.setTimeout((int) timeout);
        builder.setVersion(aresSession.getVersion());
        builder.setMsgidSet(aresSession.getMsgidSet());
        return builder.builder();
    }

    /**
     * Session转MscSession
     * @param session
     * @return
     */
    public static MhhSession session2AresSession(Session session) {
        if(null == session) {
            return null;
        }
        MhhSession aresSession = new MhhSession();
        aresSession.setSessionId(session.getId());
        aresSession.setAuthStatus(session.getAuthStatus());
        aresSession.setUserId(session.getUserId());
        aresSession.setServerIp(session.getServerId());
        aresSession.setMsgId(session.getMsgId());
        aresSession.setKey(session.getSkey());
        aresSession.setEventId(session.getEventId());
        aresSession.setDeviceId(session.getDeviceCode());
        Collection<Object> keys = session.getAttributeKeys();
        if(null != keys && !keys.isEmpty()) {
            Map<String, Object> jsonObject = new HashMap<String, Object>();
            for (Object key : keys) {
                jsonObject.put((String) key, session.getAttribute(key));
            }
            aresSession.setData(JsonUtils.objectToJson(jsonObject));
        }
        aresSession.setCreateTime(session.getCreateTime());
        aresSession.setVisitTime(session.getLastAccessTime());
        aresSession.setInvalidTime(new Date(session.getLastAccessTime().getTime() + 1000 * session.getTimeout()));
        aresSession.setVersion(session.getVersion());
        aresSession.setMsgidSet(session.getMsgidSet());
        return aresSession;
    }
}
