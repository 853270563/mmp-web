package cn.com.yitong.modules.hbbProvider;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import cn.com.yitong.consts.Properties;
import cn.com.yitong.util.YTLog;

/**
 * 
 * @author shyt_huangqiang
 *
 */
public class  ProviderListener implements Runnable{

    private Logger logger = YTLog.getLogger(this.getClass());

    private static int port = Properties.getInt("HBB_PROVIDER_PORT");//默认端口

    @Autowired
    private ThreadPoolTaskExecutor taskExecutor;

    private ServerSocket serverSocket=null;

    private boolean state = true; // 本服务启动标志

    private boolean over = false;

    @PostConstruct
    public void start(){
        if(state){
            new Thread(this).start();
        }else{
            logger.info("客户经理app平台,端口：【" + port + "】已经被禁用！");
        }
    }

    @Override
    public void run() {
        try {
            //监听服务器端口，一旦有数据发送过来，那么就将数据封装成socket对象;
            //如果没有数据发送过来，那么这时处于线程阻塞状态，不会向下继续执行
            serverSocket = new ServerSocket(port);
            logger.info(" 初始化ServerSocket,端口：【"+port+"】！服务成功！");
            while(!over){
                try {
                    Socket socket = serverSocket.accept();//侦听并接受到此套接字的连接
                    taskExecutor.execute(new ProviderHandler(socket));
                }catch (IOException e) {
                }
            }
        } catch (IOException e) {
            logger.error("初始化ServerSocket,端口：【"+port+"】出现异常！",e);
        } finally {
            if(null != serverSocket) {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                }
            }
        }
    }

    public void destory() {
        try {
            over = true;
            if(null != serverSocket) {
                serverSocket.close();
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setTaskExecutor(ThreadPoolTaskExecutor taskExecutor) {
        this.taskExecutor = taskExecutor;
    }

    public void setState(boolean state) {
        this.state = state;
    }

}
