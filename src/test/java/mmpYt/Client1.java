package mmpYt;

import java.io.BufferedInputStream;
import java.io.BufferedReader;  
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;  
import java.io.InputStream;
import java.io.InputStreamReader;  
import java.io.OutputStream;
import java.io.PrintStream;  
import java.net.Socket;  
import java.net.SocketTimeoutException;  

import javax.swing.InputMap;

import org.apache.tools.ant.taskdefs.Input;
  
public class Client1 {  
    public static void main(String[] args) throws IOException {  
        //客户端请求与本机在20006端口建立TCP连接   
        Socket client = new Socket("198.198.200.107", 9090);  
       // client.setSoTimeout(1000000);  
        //获取键盘输入   
        //BufferedReader input = new BufferedReader(new InputStreamReader(System.in));  
        StringBuffer strffer = new  StringBuffer();
        strffer.append("1234567<?xml version='1.0' encoding='UTF-8'?>");
        strffer.append("<Service>");
        strffer.append("<Service_Header>");
        strffer.append("<service_id>00080001000001</service_id>");
        strffer.append("<requester_id>0018</requester_id>");
        strffer.append("<branch_id>802777777</branch_id>");
        strffer.append("<channel_id>01</channel_id>");
        strffer.append("<version_id>01</version_id>");
        strffer.append("<service_time>20101104111710李及</service_time>");
        strffer.append("<service_sn>8027777770209011171</service_sn>");
        strffer.append("</Service_Header>");
        strffer.append("<Service_Body>");
        strffer.append("<request>");
        strffer.append("<customer_id>4006</customer_id>");
        strffer.append("</request> ");
        strffer.append("<response>");
        strffer.append("</response> ");
        strffer.append("</Service_Body> </Service>");
        System.out.println(strffer.toString());
        OutputStream output = client.getOutputStream();
        InputStream inputStream = client.getInputStream();
        int fileSize = 1090;
        boolean transFlag = true;
        int transBufferSize =1024;
        byte[] byt = new byte[1024];
        int off=0;	
        int i=0;
        output.write(strffer.toString().getBytes("GBK"));
        output.flush();
        StringBuffer sb = new StringBuffer();
        StringBuffer sb1 = new StringBuffer();
        while((i=inputStream.read(byt))>-1){
	    	sb1.append(new String(byt,"GBK"));
        }
        System.out.println(sb1.substring(0, 7));
//        while((i=inputStream.read(byt))>-1){
//        	//output.write(byt, 0, i);
////        	System.out.println(new String(byt,"UTF-8"));
//        	sb.append(new String(byt,"UTF-8"));
//        }
        while (transFlag) {
            int bufSize = transBufferSize;
            if (fileSize < transBufferSize) {
              bufSize = Integer.parseInt(Long.toString(fileSize));
              transFlag = false;
            }
            byte[] buf = new byte[bufSize];
            inputStream.read(buf);
            sb.append(new String(buf));
//            this.outStream.write(buf);
            fileSize -= transBufferSize;
            off += transBufferSize;
          
        }
//        this.outStream.flush();
        System.out.println(sb.toString());
        inputStream.close();
        output.close();
        if(client != null){  
            //如果构造函数建立起了连接，则关闭套接字，如果没有建立起连接，自然不用关闭  
            client.close(); //只关闭socket，其关联的输入输出流也会被关闭  
        }  
    }  
}  