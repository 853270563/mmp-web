package cn.com.yitong.modules.ares.servCtl.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import cn.com.yitong.common.persistence.mybatis.impl.CriteriaQuery;
import cn.com.yitong.modules.ares.servCtl.dao.AbstractCacheServCtlConfigDao;
import cn.com.yitong.modules.ares.servCtl.model.MhhEventConfig;

/**
 * 服务并发控制配置读取类
 * @author lc3@yitong.com.cn
 */
public class ServCtlConfigDBDao extends AbstractCacheServCtlConfigDao {

    @Resource
    private MhhEventConfigDao mhhEventConfigDao;

    @Override
    protected Map<String, Integer> getAllConfig() {
        CriteriaQuery query = new CriteriaQuery();
        query.setDistinct(true);
        query.addSelect(MhhEventConfig.TF.eventId).addSelect(MhhEventConfig.TF.limitCount);
        List<MhhEventConfig> mhhEventConfigs = mhhEventConfigDao.queryByCriteria(query);

        Map<String, Integer> map = new HashMap<String, Integer>();
        if(null != mhhEventConfigs) {
            for (MhhEventConfig mhhEventConfig : mhhEventConfigs) {
                map.put(mhhEventConfig.getEventId(), mhhEventConfig.getLimitCount());
            }
        }
        return map;
    }
}
