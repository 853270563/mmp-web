package cn.com.yitong.ares.mp.task;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

import cn.com.yitong.ares.mp.service.MsgPushService;
import cn.com.yitong.ares.weixin.service.SendMessageService;
import cn.com.yitong.consts.AppConstants;

//@Component
public class ScanPushMessageTask extends AbstractTask {

	@Resource
	MsgPushService msgPushService;
    @Resource
    SendMessageService wxSendMsgService;

    public static final Logger L = LoggerFactory.getLogger(MsgPushService.class);

	@Override
	@Scheduled(fixedRate = 5000, initialDelay = 3000)
	public void execute() {
        //push_type 1-mqtt、2-Jpush、3-weixin
        if(AppConstants.push_type.equals("3")){
            wxSendMsgService.sendMessage();
        }else{
            msgPushService.scanAndSend();
        }

	}
}
