package cn.com.yitong.ares.servCtl.service;

import cn.com.yitong.modules.session.service.SessionService;
import org.springframework.util.Assert;

import cn.com.yitong.core.util.SecurityUtils;
import cn.com.yitong.ares.servCtl.dao.ServCtlConfigDao;
import cn.com.yitong.ares.servCtl.dao.ServCtlDao;
import cn.com.yitong.tools.vo.SimpleResult;

/**
 * 服务控制服务
 * @author lc3@yitong.com.cn
 */
public class ServCtlService {
    private ServCtlConfigDao servCtlConfigDao;
    private ServCtlDao servCtlDao;

    /**
     * 开始一个事件处理，如果当前服务访问数大于配置数，就返回失败
     * @param eventId 事件ID
     * @return 处理结果
     */
    public SimpleResult startEventId(String eventId) {
        Assert.hasText(eventId, "eventId不能为空");
        int limitConfig = servCtlConfigDao.getLimitConfig(eventId);
        if(-1 < limitConfig) {
            int accessCount = servCtlDao.getAccessCount(eventId);
            if(accessCount >= limitConfig) {
                return new SimpleResult(false, "当前服务访问数已满，请稍后再试", SessionService.SESSION_STATUS_EVENT_BUSY);
            }
        }
        try {
            servCtlDao.startEvent(SecurityUtils.getSessionRequired().getId(), eventId);
        } catch (Exception e) {
            return new SimpleResult(false, e.getMessage(), e);
        }
        return new SimpleResult();
    }

    /**
     * 结束一个事件处理
     * @param eventId 事件ID
     * @return 处理结果
     */
    public SimpleResult stopEventId(String eventId) {
        Assert.hasText(eventId, "eventId不能为空");
        servCtlDao.stopEvent(SecurityUtils.getSessionRequired().getId(), eventId);
        return new SimpleResult();
    }

    public ServCtlConfigDao getServCtlConfigDao() {
        return servCtlConfigDao;
    }

    public void setServCtlConfigDao(ServCtlConfigDao servCtlConfigDao) {
        this.servCtlConfigDao = servCtlConfigDao;
    }

    public ServCtlDao getServCtlDao() {
        return servCtlDao;
    }

    public void setServCtlDao(ServCtlDao servCtlDao) {
        this.servCtlDao = servCtlDao;
    }
}
