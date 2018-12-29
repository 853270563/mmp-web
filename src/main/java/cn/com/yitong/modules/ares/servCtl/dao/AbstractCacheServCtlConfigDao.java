package cn.com.yitong.modules.ares.servCtl.dao;

import cn.com.yitong.common.utils.ConfigName;
import cn.com.yitong.common.utils.ConfigUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 服务控制的配置访问接口抽象实现，支持缓存
 * @author lc3@yitong.com.cn
 */
public abstract class AbstractCacheServCtlConfigDao implements ServCtlConfigDao {
    private static long cacheTime = 0;
    private static Map<String, Integer> caches = null;

    @Override
    public int getLimitConfig(String eventId) {
        flushCaches();
        Integer limit = caches.get(eventId);
        return null == limit ? 0 : limit;
    }

    /**
     * 是否需要刷新缓存，如果没有自定义判断方法isUpdated，则按配置时间去自动刷新
     * @return 是否需要刷新
     */
    private boolean needFlush() {
        Boolean updated = isUpdated();
        if(null != updated) {
            return updated;
        }
        return System.currentTimeMillis() - cacheTime >
                1000 * ConfigUtils.getValue(ConfigName.SYSTEM_CACHE_REFRESH_SECOND,
                        ConfigName.SYSTEM_CACHE_REFRESH_SECOND_DEFVAL);
    }

    /**
     * 刷新缓存，会自动判断是否需要刷新
     */
    private void flushCaches() {
        if(!needFlush()) {
            return;
        }
        synchronized (AbstractCacheServCtlConfigDao.class) {
            if(!needFlush()) {
                return;
            }
            caches = getAllConfig();
            if(null == caches) {
                caches = new HashMap<String, Integer>(0);
            }
            cacheTime = System.currentTimeMillis();
            hasUpdated();
        }
    }

    /**
     * 自定义是否更新的判断回调钩子
     * @return 是否更新
     */
    protected Boolean isUpdated() {
        return null;
    }

    /**
     * 更新完成后的回调钩子
     */
    protected void hasUpdated() {

    }

    /**
     * 得到所有的配置信息
     * @return 配置信息
     */
    protected abstract Map<String, Integer> getAllConfig();
}
