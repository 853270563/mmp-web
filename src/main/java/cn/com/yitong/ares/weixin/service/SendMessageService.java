package cn.com.yitong.ares.weixin.service;

import cn.com.yitong.ares.weixin.util.WeiXinUtils;
import cn.com.yitong.consts.AppConstants;
import cn.com.yitong.framework.service.ICrudService;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by l on 2015/11/26.
 */
@Service
public class SendMessageService {

    public static final Logger logger = LoggerFactory.getLogger(SendMessageService.class);
    @Autowired
    ICrudService service;
    public void sendMessage(){
        try{
            //查询有效的消息任务
            List taskList=service.findList("ARES_WX_MSG_TASK.findWxMsgTask",null);
            for(int i=0;i< taskList.size();i++){
                Map<String, String> map=(Map<String, String>)taskList.get(i);

                List userList=service.findList("ARES_WX_USER_INFO.findUserInfoByStatus", null);
                for(int j=0;j<userList.size();j++){
                    Map<String, String> userMap=(Map<String, String>)userList.get(j);
                    map.put("MSG_ID",String.valueOf(service.load("ARES_WX_CUST_MSG.loadSeq", null).get("MSG_ID")));
                    map.put("OPEN_ID",userMap.get("OPENID"));
                    //添加消息
                    service.insert("ARES_WX_CUST_MSG.insert",map);
                }
                map.put("STATUS","1");//发送中
                service.update("ARES_WX_MSG_TASK.updateWxMsgTaskStatus",map);
            }
            //查询消息
            List msgList=service.findList("ARES_WX_CUST_MSG.findWxCustMsg",null);
            if(msgList.size()>0){
                for(int i=0;i< msgList.size();i++){
                    Map<String, String> map=(Map<String, String>)msgList.get(i);
                    String openId=map.get("OPEN_ID");
                    String msgBody=map.get("MSG_BODY");
                    //发送消息
                    this.pushMsg(openId,msgBody);
                    map.put("STATUS","1");//已发送
                    service.update("ARES_WX_CUST_MSG.updateWxCustMsgStatus",map);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            logger.error("发送微信消息失败"+e.getMessage());
        }


    }
    private void pushMsg(String openId,String message) throws Exception{
        String url = String.format(AppConstants.KeFuPostMsg, WeiXinUtils.getAccessToken());
        //String msg =print(message, openId);
        Map<String, Object> map = new HashMap<String, Object>();
        Map<String, String> map1 = new HashMap<String, String>();
        map1.put("content", message);
        map.put("touser", openId);
        map.put("msgtype", "text");
        map.put("text", map1);
        WeiXinUtils.postWeiXin(url, JSONObject.toJSONString(map));
    }




}
