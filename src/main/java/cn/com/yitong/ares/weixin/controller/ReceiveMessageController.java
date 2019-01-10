package cn.com.yitong.ares.weixin.controller;

import cn.com.yitong.ares.weixin.util.EnumMsgType;
import cn.com.yitong.ares.weixin.util.Reply;
import cn.com.yitong.ares.weixin.util.WeiXinUtils;
import cn.com.yitong.ares.weixin.vo.BaseMessage;
import cn.com.yitong.ares.weixin.vo.UserDetailInfo;
import cn.com.yitong.framework.service.ICrudService;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping(value = "/ares/dispatch/weiXinMessage.do")
public class ReceiveMessageController {
    public static final Logger logger = LoggerFactory.getLogger(ReceiveMessageController.class);

/*	@Autowired
	private Webmsg01 webmsg01;
	@Autowired
	private Webmsg02 webmsg02;
	@Autowired
	private Webmsg03 webmsg03;*/
	//@Autowired
	//private WeixinUserService weiXinUserService;
    @Autowired
    ICrudService service;

	DecimalFormat df = new DecimalFormat("###,###,###,##0.00");

	JAXBContext context;

	@RequestMapping(value = "", method = RequestMethod.GET)
	public void weiXinMessage(@RequestParam("signature") String signature,
			@RequestParam("timestamp") String timestamp,
			@RequestParam("nonce") String nonce,
			@RequestParam("echostr") String echostr,HttpServletRequest request,HttpServletResponse response) {

		logger.debug("WEIXIN_URL======1======"+signature+"<>"+timestamp+"<>"+nonce);
        String retStr = "";
		if(WeiXinUtils.checkSignature(signature, timestamp, nonce)){
            retStr=echostr;
		}else{
            retStr= "";
		}
        try {
            response.getWriter().write(retStr);
            response.getWriter().flush();
            response.getWriter().close();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
        }
	}
	
	@RequestMapping(value = "")
	@ResponseBody
	public String weiXinMessage(HttpServletRequest request,HttpServletResponse response) throws IOException {

        System.out.println("------------------------微信服务平台返回客户信息接口----------------------------");
		logger.info("WEIXIN_URL====2========" + request.getRequestURI());
		logger.info("WEIXIN_URL====3========" + request.getRemoteAddr());
		logger.info("WEIXIN_URL====4========" + request.getRemoteHost());
		logger.info("WEIXIN_URL====5========" + request.getRemotePort());

		//检测签名信息
		if(WeiXinUtils.checkSignature(request)){
			String xml = WeiXinUtils.readStreamParameter(request.getInputStream());
			//解析xml消息
			BaseMessage baseMessage = getBaseMessage(xml);
			System.out.println(baseMessage.getToUserName()+"==============>"+baseMessage.getFromUserName()+"==============>\n");
			if(baseMessage!=null){
                //保存微信用户信息
				//这里不保存用户数据--在事件中保存
                Map openIdMap= new HashMap<String,String>();
                openIdMap.put("OPEN_ID",baseMessage.getFromUserName());
                if(baseMessage.getEvent().equals("subscribe")){ //用户关注
                    getUserInfo(baseMessage.getFromUserName());
                }
                else if(baseMessage.getEvent().equals("unsubscribe")){ //取消关注

                    openIdMap.put("STATUS","0");
                    service.update("ARES_WX_USER_INFO.updateWxUserInfoStatus",openIdMap);
                }

				System.out.println("\n DateTime ==============>> "+new Date());
			}
		}
		return null;
	}
	

/*	//向请求端发送返回数据
    public void print(HttpServletResponse response,String content,String opendId,String createTime){
//      如果需要微信昵称可以把注释去了，然后weixinUser.getUsername();获取
//    	WeixinUser weixinUser = weiXinUserService.findByOpenId(opendId);
    	Map<String, String> params = new HashMap<String, String>();
    	params.put("opendId", opendId);
    	try {
			String enStr = AESOperator.getInstance().encrypt(JSON.toJSONString(params));
			content = content.replaceAll("%params%", enStr);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
        try{  
        	response.setCharacterEncoding("UTF-8");
        	response.getWriter().print(content);  
        	response.getWriter().flush();  
        	response.getWriter().close();  
        }catch(Exception e){  
              
        }  
    } */
	private void getUserInfo(String openId){
		String userInfoStr = WeiXinUtils.getUserDetailInfo(openId);
		UserDetailInfo userInfo = JSONObject.parseObject(userInfoStr, UserDetailInfo.class);

        Map openIdMap= new HashMap<String,String>();
        openIdMap.put("OPEN_ID",openId);

		UserDetailInfo findTmp =WeiXinUtils.getUserInfoConvertToMap(service.load("ARES_WX_USER_INFO.findUserInfoByOpenId", openIdMap));
		//重新设置订阅时间（当前系统时间）
		//userInfo.setSubscribe_time(new Date());
        userInfo.setStatus("1"); //注册用户

		logger.info("a当前微信用户openId======="+openId);
		logger.info("a微信接口返回信息======="+userInfoStr);
		if(findTmp==null){
			if(null != userInfo && null != userInfo.getOpenId()){
				service.insert("ARES_WX_USER_INFO.insertWxUserInfo",WeiXinUtils.getMapConvertUserInfo(userInfo) );
			}
		}else{
			if(null != userInfo && null != userInfo.getOpenId()){
                service.update("ARES_WX_USER_INFO.updateWxUserInfo",WeiXinUtils.getMapConvertUserInfo(userInfo));
			}
		}
	}

	/**
	 * 在完整解析xml消息时,先提前获取消息类型
	 * @param xmlStr
	 * @return
	 */
	private BaseMessage getBaseMessage(String xmlStr){
		if (context == null) {
				try {
					context = JAXBContext.newInstance(BaseMessage.class);
				} catch (JAXBException e) {
					e.printStackTrace();
				}
		}
		Unmarshaller um;
		try {
			um = context.createUnmarshaller();
			ByteArrayInputStream is = new ByteArrayInputStream(
					xmlStr.getBytes());
			BaseMessage baseMsg = (BaseMessage) um.unmarshal(is);
			return baseMsg;
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		return null;
	}
}
