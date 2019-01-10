package cn.com.yitong.ares.login.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BizLogFileUtil {
	/*临时存储日志的文件*/
	private static File logFile = new File(System.getProperty("user.dir")+"/behavior.log");
	private static Logger logger = LoggerFactory.getLogger(BizLogFileUtil.class);
	
	public static void close(Closeable closeable){
		try {
			closeable.close();
		} catch (IOException e) {
			logger.error("资源关闭异常",e);
		}
	}
	/**
	 * 添加一行日志
	 * 
	 * @param logStr 日志字符串
	 * @author hry@yitong.com.cn
	 * @date 2015年1月13日
	 */
	public static void writeLog(String logStr) throws IOException{
		BufferedWriter bw = null;
		try {
			if (!logFile.exists()) {
				logFile.createNewFile();
			}
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(logFile, true),"utf-8"));
			bw.write(logStr);
			bw.newLine();
		} finally {
			close(bw);
		}
	}

	/**
	 * 读取日志内容
	 * 
	 * @author hry@yitong.com.cn
	 * @date 2015年1月13日
	 */
	public static List<String> readLog() throws IOException{
		BufferedReader br = null;
		List<String> list = new ArrayList<String>();
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(logFile),"utf-8"));
			String row = null;
			while ((row = br.readLine()) != null) {
				list.add(row);
			}
		} finally {
			if(br != null){
				close(br);
			}
		}
		return list;
	}

	/**
	 * 清除日志文件内容
	 * 
	 * @author hry@yitong.com.cn
	 * @date 2015年1月13日
	 */
	public static boolean clearLog(){
		FileOutputStream os = null;
		try{
			os = new FileOutputStream(logFile);
			os.write(new String("").getBytes());
		}catch (IOException e) {
			logger.error("清除日志文件内容",e);
			return false;
		}finally {
			close(os);
		}
		return true;
	}
}
