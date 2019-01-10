package cn.com.yitong.framework.listener;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;

import cn.com.yitong.framework.servlet.ServerInit;
import cn.com.yitong.yantai.task.SocketTask;
/**
 * 外围推送监听服务
 * @author YGH
 */
public class YTYDYXServerListener implements Runnable{
	private static Logger logger = Logger.getLogger(YTYDYXServerListener.class);
	public static final String YDYX_PORT =ServerInit.getString(("YDYX_PORT"));
	@Autowired
	@Qualifier("myExecutor")
	private TaskExecutor taskExecutor; 
	private ServerSocket serverSocket=null;
	private Socket socket = null;
	// 本服务启动标志
	private  boolean state = true;
	public void init(){
		logger.info("++++++  移动营销服务,消息推送监听程序启动....   ++++++");
		if(state){
			taskExecutor.execute(this);
		}else{
			logger.info("++++++   移动营销服务,消息推送监听服务,端口：【" + YDYX_PORT + "】已经被禁用！++++++");
		}
	}
	
	
	/**
	 * 
	 */
	@Override
	public void run() {
		try {
			serverSocket = new ServerSocket(Integer.parseInt(YDYX_PORT));
			logger.info(" 初始化移动营销服务,微贷审批消息推送监听服务ServerSocket,端口：【" + YDYX_PORT + "】！服务成功！");
			while (state) {
				try {
					socket = serverSocket.accept();
					SocketTask socketTask = new SocketTask();
					socketTask.init(socket);
					taskExecutor.execute(socketTask);
					logger.info("++++++ 移动营销-消息推送：接收一个请求服务并处理......  ++++++");
				} catch (IOException e) {
					logger.info("工作线程出现异常：", e);
				}
			}

		} catch (Exception e) {
			logger.error("初始化移动营销服务,消息推送监听服务ServerSocket,端口：【" + YDYX_PORT + "】出现异常！", e);
			//System.exit(1);
		} finally {
			if (serverSocket != null) {
				try {
					serverSocket.close();
				} catch (IOException e) {
					logger.error(e.getMessage());
				}

			}
		}
	}
	
	public void setState(boolean state) {
		this.state = state;
	}
	
	public void destory() {
				setState(false);
			logger.info("++++++ 关闭移动营销-微贷审批消息推送:YDYXServerListener,端口：【" + YDYX_PORT + "】++++++");

	}
}
