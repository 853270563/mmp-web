package cn.com.yitong.ares.weixin.task;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.com.yitong.ares.mp.task.AbstractTask;
import cn.com.yitong.ares.weixin.service.SendMessageService;

/**
 * Created by l on 2015/11/26.
 */
//@Component
public class PushWeiXinMessageTask extends AbstractTask {
    @Resource
    SendMessageService sendMsgService;
    public static final Logger logger = LoggerFactory.getLogger(PushWeiXinMessageTask.class);
    @Override
    //@Scheduled(fixedRate = 5000, initialDelay = 3000)
    public void execute() {
        logger.info("PushWeiXinMessageTask has executed................");
        sendMsgService.sendMessage();
    }
}
