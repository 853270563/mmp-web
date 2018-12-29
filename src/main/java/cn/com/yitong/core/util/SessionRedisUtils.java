package cn.com.yitong.core.util;

import cn.com.yitong.common.utils.SpringContextUtils;
import cn.com.yitong.core.cache.CacheNames;
import cn.com.yitong.core.session.Session;
import cn.com.yitong.util.StringUtil;
import org.springframework.cache.CacheManager;

/**
 * @author zhanglong@yitong.com.cn
 * @date 15/10/21
 */
public class SessionRedisUtils {

    private static CacheManager cacheManager = SpringContextUtils.getBean(CacheManager.class);

    public static Session getSession(String id) {
        if(StringUtil.isEmpty(id)) {
            return null;
        }
        return (Session)cacheManager.getCache(CacheNames.SESSION_CACHE_NAME).get(id);
    }

    public static void saveSession(Session session) {
        if(null == session || StringUtil.isEmpty(session.getId())) {
            return;
        }
        cacheManager.getCache(CacheNames.SESSION_CACHE_NAME).put(session.getId(), session);
    }

    public static void deleteSession(String id) {
        if(StringUtil.isEmpty(id)) {
            return;
        }
        cacheManager.getCache(CacheNames.SESSION_CACHE_NAME).evict(id);
    }

}
