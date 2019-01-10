package cn.com.yitong.util;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class GetIpAddress {
	/*
	 * 获取本机ip地址
	 */
	public static String getIP(){
		String ip="";
		try {
			ip = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ip;
	}
	
}
