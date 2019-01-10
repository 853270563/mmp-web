package cn.com.yitong.yantai.task;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import cn.com.yitong.common.utils.ConfigEnum;
import cn.com.yitong.core.util.DictUtils;
import cn.com.yitong.framework.servlet.ServerInit;


/**
 * @author luanyu
 * @date   2018年6月7日
 */
@Service
@Lazy(false)
public class UserSynchronization {
	private static Logger logger = Logger.getLogger(SocketTask.class);
	@Autowired
	private SqlSession sqlSession;


	@Scheduled(cron = "0 0 6 * * ?")
	public void getCrmUser() {

		logger.info("每天6点从crm获取用户");
		CloseableHttpClient httpclient = null;
		CloseableHttpResponse response = null;
		try {
			httpclient = HttpClients.createDefault();
			String url = DictUtils.getDictValue(ConfigEnum.DICT_SYS_PARAMS, "getCrmUserFile", "");
			// 创建httpget.      
			HttpGet httpget = new HttpGet(url);
			// 执行get请求.      
			response = httpclient.execute(httpget);
			// 获取响应实体      
			HttpEntity entity = response.getEntity();
			// 打印响应状态      
			if (entity != null && response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				entity.writeTo(new FileOutputStream(ServerInit.getString("upload_files_path") + "/userList.txt"));
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			try {
				if (httpclient != null) {

					httpclient.close();
				}
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
			}
			try {
				if (response != null) {

					response.close();
				}
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
			}
		}

	}

	@Scheduled(cron = "0 10 6 * * ?")
	public void insertToDb() {
		FileInputStream fileInputStream = null;
		BufferedReader bufferedReader = null;
		String users=null;
		StringBuffer bfBuffer = new StringBuffer();
		logger.debug("批量同步crm用户开始");
		try {

			File file = new File(ServerInit.getString("upload_files_path") + "/userList.txt");
			if (!file.exists()) {
				logger.debug(file.getPath() + "路径不存在");
				return;
			}
			fileInputStream = new FileInputStream(file);
			bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream, "utf-8"));
			while ((users=bufferedReader.readLine())!=null) {
				bfBuffer.append(users);
			}
			String[] split = bfBuffer.toString().split("\\|");
			logger.debug("本次同步用户数为: " + split.length);
			for (String string : split) {
				sqlSession.insert("crm.notExistinsert", string);
				Thread.sleep(2000);
			}
			logger.debug("批量同步crm用户成功");
			file.delete();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			if (fileInputStream != null) {
				try {
					fileInputStream.close();
				} catch (IOException e) {
					logger.error(e.getMessage(), e);
				}

			}
			if (bufferedReader != null) {
				try {
					bufferedReader.close();
				} catch (IOException e) {
					logger.error(e.getMessage(), e);
				}
			}
			logger.debug("批量同步crm用户结束");
		}
	}

}
