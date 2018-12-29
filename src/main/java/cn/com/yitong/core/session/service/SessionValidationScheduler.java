package cn.com.yitong.core.session.service;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.com.yitong.common.utils.ConfigUtils;
import cn.com.yitong.common.utils.config.ConfigListener;
import cn.com.yitong.core.session.SessionManager;
import cn.com.yitong.core.util.ConfigName;

/**
 *
 * @author lc3@yitong.com.cn
 */
public class SessionValidationScheduler implements Runnable, ConfigListener {

    private static Logger logger = LoggerFactory.getLogger(SessionValidationScheduler.class);
    private boolean enable;
    private SessionManager sessionManager;
    private ScheduledExecutorService scheduledExecutorService = createScheduledExecutorService();

    public SessionValidationScheduler() {
    }

    public SessionValidationScheduler(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    @Override
    public void onChange(String key, String oldVal, String newVal) {
        if(isEnable()) {
            disableSessionValidation();
            enableSessionValidation();
        }
    }

    /**
     * 开始Session定时验证
     */
    public void enableSessionValidation() {
        long interval = ConfigUtils.getValue(ConfigName.SESSION_VALIDATION_INTERVAL,
                ConfigName.SESSION_VALIDATION_INTERVAL_DEFVAL);
        if(interval > 1L) {
            if(null == scheduledExecutorService) {
                scheduledExecutorService = createScheduledExecutorService();
            }
            scheduledExecutorService.scheduleWithFixedDelay(this, interval, interval, TimeUnit.SECONDS);
            ConfigUtils.registerConfigListener(ConfigName.SESSION_VALIDATION_INTERVAL, this);
            enable = true;
            logger.debug("成功开启Session自动清理定时器，清理间隔为{}秒", interval);
            return;
        }
        logger.debug("开启Session自动清理定时器失败，清理时间设置不合理，应大于1秒");
    }

    /**
     * 结束Session定时验证
     */
    public void disableSessionValidation() {
        if(null != scheduledExecutorService) {
            scheduledExecutorService.shutdown();
            scheduledExecutorService = null;
        }
        enable = false;
    }

    public ScheduledExecutorService createScheduledExecutorService() {
        return Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r);
                thread.setDaemon(true);
                return thread;
            }
        });
    }

    public ScheduledExecutorService getScheduledExecutorService() {
        return scheduledExecutorService;
    }

    public void setScheduledExecutorService(ScheduledExecutorService scheduledExecutorService) {
        this.scheduledExecutorService = scheduledExecutorService;
    }

    public SessionManager getSessionManager() {
        return sessionManager;
    }

    public void setSessionManager(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @Override
    public void run() {
        if(null == sessionManager) {
            if(logger.isWarnEnabled()) {
                logger.warn("执行会话验证定时器失败，失败原因为sessionManager没有配置");
            }
            return;
        }
        if(logger.isDebugEnabled()) {
            logger.debug("开始执行会话验证定时器…");
        }
        long startTime = System.currentTimeMillis();
        try {
            sessionManager.validateSessions();
        } catch (Exception e) {
            logger.error("执行会话验证定时器失败，执行出现异常", e);
        }
        long endTime = System.currentTimeMillis();
        if(logger.isDebugEnabled()) {
            logger.debug("会话验证定时器验证完成，用时{}毫秒", (endTime - startTime));
        }
    }
}
