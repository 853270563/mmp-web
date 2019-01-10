package cn.com.yitong.ares.weixin.util;

public class Reply extends Thread {

    public static String topMsg(String toUser,String fromUser)  
    {  
    	return  "<xml>"+
				"<ToUserName><![CDATA["+toUser+"]]></ToUserName>"+
				"<FromUserName><![CDATA["+fromUser+"]]></FromUserName>"+
				"<CreateTime>"+System.currentTimeMillis()/1000+"</CreateTime>";
    }  
    public static String backMsg()  
    {  
    	return  "</xml>";
    }  
    public static String rspTextMsg(String toUser,String fromUser, String message)  
    {  
		String rspMsg = topMsg(toUser, fromUser)
						+"<MsgType><![CDATA[text]]></MsgType>"
						+"<Content><![CDATA["+message+"]]></Content>"
						+backMsg();
    	return rspMsg;
    }  

    public static String rspNewsMsg(String toUser,String fromUser, String message)  
    {  
		String rspMsg = topMsg(toUser, fromUser)
						//"<MsgType><![CDATA[news]]></MsgType>"
						+message
						+backMsg();
    	return rspMsg;
    }  
    public static String rspLocationMsg(String Title,String Description, String PicUrl, String Url)  
    {  
		String rspMsg = "<item>"
						+"<Title><![CDATA["+Title+"]]></Title>"
						+"<Description><![CDATA["+Description+"]]></Description>"
						+"<PicUrl><![CDATA["+PicUrl+"]]></PicUrl>"
						+"<Url><![CDATA["+Url+"]]></Url>"
						+"</item>";
    	return rspMsg;
    }  

    public static String rspUnknownMsg(String toUser, String fromUser, String message)  
    {  
    	//回复文本消息或者图文消息
		if (message.indexOf("[news]") == -1	&& message.indexOf("<Articles>") == -1 && message.indexOf("</Articles>") == -1){//没有上述节点的都是普通消息
    		return Reply.rspTextMsg(toUser, fromUser, message);
    	}else{//其他走图文消息
    		return Reply.rspNewsMsg(toUser, fromUser, message);
    	}
    }  

	public static String responseSubMsg(String toUser,String fromUser)
	{
		return  "<xml>"+
				"<ToUserName><![CDATA["+toUser+"]]></ToUserName>"+
				"<FromUserName><![CDATA["+fromUser+"]]></FromUserName>"+
				"<CreateTime>"+System.currentTimeMillis()/1000+"</CreateTime>"+
				"<MsgType><![CDATA[text]]></MsgType>"+
				"<Content><![CDATA[谢谢关注  敬请期待！]]></Content>"+
				"</xml>";
	}
}