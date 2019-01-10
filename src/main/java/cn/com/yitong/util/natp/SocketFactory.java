package cn.com.yitong.util.natp;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class SocketFactory {

    public static Socket createSocket(String serverName) throws NumberFormatException, UnknownHostException, IOException
    {
    	String hostPort=serverName;
    	
    	if(hostPort==null)
    	    throw new UnknownHostException("没有服务器“"+serverName+"”的配置信息！");
    	String[] segs=hostPort.split(":");
    	if(segs.length<3)
            try {
                throw new Exception("服务器“"+serverName+"”的配置错误，格式为“IP地址:端口号”！");
            } catch (Exception e) {
                e.printStackTrace();
            }
    	
    	System.out.println("afa ="+segs[0] +"  " +segs[1]);
    	Socket socket=new Socket(segs[0], Integer.parseInt(segs[1]));
    	socket.setSoTimeout(Integer.parseInt(segs[2])*1000);
        return socket;
    }
}
