package cn.com.yitong.modules.downloadPicture;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import cn.com.yitong.framework.servlet.ServerInit;

/**
 * @author luanyu
 * @date   2018年10月16日
 */
@Controller
public class DownLoadPicture {
	private static Logger logger = LoggerFactory.getLogger(DownLoadPicture.class);
	public static String ip = ServerInit.getString("YX_IP");; //为SunECMDM的ip地址
	String YX_HTTPURL = ServerInit.getString("YX_HTTPURL");; // 下载图片的url
	String YX_HTTPPORT = ServerInit.getString("YX_HTTPPORT");; // 下载图片的端口

	@RequestMapping("SunECMDM/servlet/getFile")
	public void downloadPicture(HttpServletRequest request, HttpServletResponse resp) {
		CloseableHttpClient httpclient = null;
		CloseableHttpResponse response = null;
		try {
			httpclient = HttpClients.createDefault();
			// 创建httpget.      
			HttpGet httpget = new HttpGet(
					"http://" + ip + ":" + YX_HTTPPORT + YX_HTTPURL + "?" + request.getQueryString());
			// 执行get请求.      
			response = httpclient.execute(httpget);
			// 获取响应实体      
			HttpEntity entity = response.getEntity();
			// 打印响应状态      
			if (entity != null) {
				// 打印响应内容      
				entity.writeTo(resp.getOutputStream());

			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			try {
				httpclient.close();
				response.close();
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
			}
		}
	}
}
