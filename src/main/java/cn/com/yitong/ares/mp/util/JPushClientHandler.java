package cn.com.yitong.ares.mp.util;

import cn.com.yitong.common.utils.ConfigUtils;
import cn.jpush.api.JPushClient;
import cn.jpush.api.common.resp.APIConnectionException;
import cn.jpush.api.common.resp.APIRequestException;
import cn.jpush.api.push.PushResult;
import cn.jpush.api.push.model.Message;
import cn.jpush.api.push.model.Options;
import cn.jpush.api.push.model.Platform;
import cn.jpush.api.push.model.PushPayload;
import cn.jpush.api.push.model.audience.Audience;
import cn.jpush.api.push.model.audience.AudienceTarget;
import cn.jpush.api.push.model.notification.Notification;
import cn.jpush.api.push.model.notification.PlatformNotification;
import com.google.gson.JsonObject;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * @author zhanglong@yitong.com.cn
 * @date 15/11/18
 */
public class JPushClientHandler {

    private static Logger logger = LoggerFactory.getLogger(JPushClientHandler.class);

    public static JPushClient jpushClient = null;

    private static int RESULT_RETORY = 0;   //发送失败，可以重试
    private static int RESULT_SUCCESS = 1;  //发送成功
    private static int RESULT_FAIL = 2;     //发送失败，不可重试

    private static String masterSecret = ConfigUtils.getValue("pushMasterSecret");
    private static String appKey =  ConfigUtils.getValue("pushAppKey");

    static void init() {
        if(null == jpushClient) {
            jpushClient = new JPushClient(masterSecret, appKey, 3);
            logger.info(" - - - - - JPushClientHandler... init......" + "masterSecret==" +
                    masterSecret + ", appKey==" + appKey);
        }
    }

    public static int sendMessageAll(String title, String context, String extra) {
        if(null == jpushClient) {
            init();
        }
        PushResult result = null;
        try{
            PushPayload payload = PushPayload.newBuilder()
                    .setPlatform(Platform.all())
                    .setAudience(Audience.all())
                    .setNotification(Notification.newBuilder().setAlert(context).build())
                    .setMessage(Message.newBuilder()
                            .setTitle(title)
                            .setMsgContent(context)
                            .addExtra("EXTRA", extra)
                            .build())
                    .build();
            result = jpushClient.sendPush(payload);
        }catch (APIConnectionException e) {
            e.printStackTrace();
        }catch (APIRequestException e) {
            if(e.getStatus() >= 500) {
                return RESULT_RETORY; // 需要重试
            }else if(e.getStatus() == 400 && e.getErrorCode() == 1011){
                return RESULT_SUCCESS; //成功
            }else {
                return RESULT_FAIL; //失败
            }
        }
        return result.isResultOK()?RESULT_SUCCESS:RESULT_FAIL;
    }

    public static int sendMessageWithRegistrationID(String title, String context, String extra, String... registrationID) {
        if(null == jpushClient) {
            init();
        }
        PushResult result = null;
        try{
            PushPayload payload = PushPayload.newBuilder()
                    .setPlatform(Platform.all())
                    .setAudience(Audience.registrationId(registrationID))
                    .setNotification(Notification.newBuilder().setAlert(context).build())
                    .setMessage(Message.newBuilder()
                            .setTitle(title)
                            .setMsgContent(context)
                            .addExtra("EXTRA", extra)
                            .build())
                    .build();
            result = jpushClient.sendPush(payload);
        }catch (APIConnectionException e) {
            e.printStackTrace();
        }catch (APIRequestException e) {
            if(e.getStatus() >= 500) {
                return RESULT_RETORY; // 需要重试
            }else if(e.getStatus() == 400 && e.getErrorCode() == 1011){
                return RESULT_SUCCESS; //成功
            }else {
                return RESULT_FAIL; //失败
            }
        }
        return result.isResultOK()?RESULT_SUCCESS:RESULT_FAIL;
    }

    public static int sendMessageWithAlias(String title, String context, String extra, String... alias) {
        if(null == jpushClient) {
            init();
        }
        PushResult result = null;
        try{
            PushPayload payload = PushPayload.newBuilder()
                    .setPlatform(Platform.all())
                    .setAudience(Audience.alias(alias))
                    .setNotification(Notification.newBuilder().setAlert(context).build())
                    .setMessage(Message.newBuilder()
                            .setTitle(title)
                            .setMsgContent(context)
                            .addExtra("EXTRA", extra)
                            .build())
                    .build();
            result = jpushClient.sendPush(payload);
        }catch (APIConnectionException e) {
            e.printStackTrace();
        }catch (APIRequestException e) {
            if(e.getStatus() >= 500) {
                return RESULT_RETORY; // 需要重试
            }else if(e.getStatus() == 400 && e.getErrorCode() == 1011){
                return RESULT_SUCCESS; //成功
            }else {
                return RESULT_FAIL; //失败
            }
        }
        return result.isResultOK()?RESULT_SUCCESS:RESULT_FAIL;
    }

    public static int sendMessageWithTags(String title, String context, String extar, String... tags) {
        if(null == jpushClient) {
            init();
        }
        PushResult result = null;
        try{
            PushPayload payload = PushPayload.newBuilder()
                    .setPlatform(Platform.all())
                    .setAudience(Audience.tag(tags))
                    .setNotification(Notification.newBuilder().setAlert(title).build())
                    .setMessage(Message.newBuilder()
                            .setTitle(title)
                            .setMsgContent(context)
                            .addExtra("EXTRA", extar)
                            .build())
                    .build();
            result = jpushClient.sendPush(payload);
        }catch (APIConnectionException e) {
            e.printStackTrace();
        }catch (APIRequestException e) {
            if(e.getStatus() >= 500) {
                return RESULT_RETORY; // 需要重试
            }else if(e.getStatus() == 400 && e.getErrorCode() == 1011){
                return RESULT_SUCCESS; //成功
            }else {
                return RESULT_FAIL; //失败
            }
        }
        return result.isResultOK()?RESULT_SUCCESS:RESULT_FAIL;
    }
}
