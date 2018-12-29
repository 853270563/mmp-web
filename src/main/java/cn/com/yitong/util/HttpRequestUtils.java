package cn.com.yitong.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import cn.com.yitong.common.utils.ConfigUtils;


/**
 * Http json请求
 * @author sunwei
 *
 */
public class HttpRequestUtils {
	
	private static final Logger logger = YTLog.getLogger(HttpRequestUtils.class);
	
	private static final String address = ConfigUtils.getValue("esc_address", "");

	private static final String addressPlat = ConfigUtils.getValue("plat_address", "");

	private static final String addressInter = ConfigUtils.getValue("inter_address", "");
	/**
	 * Http json请求
	 * @param address
	 * @param json
	 * @return
	 */
	public static String send(String json) {
		
		HttpURLConnection connection = null;
		BufferedReader reader = null;
		try {
			logger.info("request json: \n" + format(json));
			logger.info("开户信息上传地址:"+address);
			// 创建连接
			URL url = new URL(address);
			connection = (HttpURLConnection) url.openConnection();
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setRequestMethod("POST");
			connection.setUseCaches(false);
			connection.setInstanceFollowRedirects(true);
			connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
			connection.setConnectTimeout(5 * 1000); // 连接超时时间5秒
			connection.setReadTimeout(5 * 1000); // 读取数据超时时间
			connection.connect();

			// POST请求
			DataOutputStream out = new DataOutputStream(connection.getOutputStream());
			json = (StringUtils.isNotBlank(json) && json.startsWith("{") && json.endsWith("}")) ? json : "{}";
			
			out.write(json.getBytes("UTF-8"));// 处理中文乱码问题
			out.flush();
			out.close();

			// 读取响应
			reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
			String lines;
			StringBuffer sb = new StringBuffer("");
			while ((lines = reader.readLine()) != null) {
				// lines = new String(lines.getBytes(), "utf-8");
				sb.append(lines);
			}
			logger.info("response json: \n" + format(sb.toString()));
			return sb.toString();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			e.printStackTrace();
		} finally {
			// 断开连接
			IOUtils.closeQuietly(reader);
			if (connection != null) {
				connection.disconnect();
			}
		}
		return "{}";
	}

	/**
	 * Http json请求
	 * @param address
	 * @param json
	 * @return
	 */
	public static String sendPlat(String json) {

		HttpURLConnection connection = null;
		BufferedReader reader = null;
		try {
			logger.info("request json: \n" + format(json));
			logger.info("影像上传地址:"+addressPlat);
			// 创建连接
			URL url = new URL(addressPlat);
			connection = (HttpURLConnection) url.openConnection();
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setRequestMethod("POST");
			connection.setUseCaches(false);
			connection.setInstanceFollowRedirects(true);
			connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
			connection.setConnectTimeout(5 * 1000); // 连接超时时间5秒
			connection.setReadTimeout(5 * 1000); // 读取数据超时时间
			connection.connect();

			// POST请求
			DataOutputStream out = new DataOutputStream(connection.getOutputStream());
			json = (StringUtils.isNotBlank(json) && json.startsWith("{") && json.endsWith("}")) ? json : "{}";

			out.write(json.getBytes("UTF-8"));// 处理中文乱码问题
			out.flush();
			out.close();

			// 读取响应
			reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
			String lines;
			StringBuffer sb = new StringBuffer("");
			while ((lines = reader.readLine()) != null) {
				// lines = new String(lines.getBytes(), "utf-8");
				sb.append(lines);
			}
			logger.info("response json: \n" + format(sb.toString()));
			return sb.toString();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			e.printStackTrace();
		} finally {
			// 断开连接
			IOUtils.closeQuietly(reader);
			if (connection != null) {
				connection.disconnect();
			}
		}
		return "{}";
	}


	/**
	 * Http json请求
	 * @param address
	 * @param json
	 * @return
	 */
	public static String sendInter(String json) {

		HttpURLConnection connection = null;
		BufferedReader reader = null;
		try {
			logger.info("request json: \n" + format(json));
			logger.info("预约网银开户上传地址:"+addressInter);
			// 创建连接
			URL url = new URL(addressInter);
			connection = (HttpURLConnection) url.openConnection();
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setRequestMethod("POST");
			connection.setUseCaches(false);
			connection.setInstanceFollowRedirects(true);
			connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
			connection.setConnectTimeout(5 * 1000); // 连接超时时间5秒
			connection.setReadTimeout(5 * 1000); // 读取数据超时时间
			connection.connect();

			// POST请求
			DataOutputStream out = new DataOutputStream(connection.getOutputStream());
			json = (StringUtils.isNotBlank(json) && json.startsWith("{") && json.endsWith("}")) ? json : "{}";

			out.write(json.getBytes("UTF-8"));// 处理中文乱码问题
			out.flush();
			out.close();

			// 读取响应
			reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
			String lines;
			StringBuffer sb = new StringBuffer("");
			while ((lines = reader.readLine()) != null) {
				// lines = new String(lines.getBytes(), "utf-8");
				sb.append(lines);
			}
			logger.info("response json: \n" + format(sb.toString()));
			return sb.toString();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			e.printStackTrace();
		} finally {
			// 断开连接
			IOUtils.closeQuietly(reader);
			if (connection != null) {
				connection.disconnect();
			}
		}
		return "{}";
	}

	/**
	 * 得到格式化json数据  退格用\t 换行用\r
	 * @param jsonStr
	 * @return
	 */
	public static String format(String jsonStr) {
        int level = 0;
        StringBuffer jsonForMatStr = new StringBuffer();
        for(int i=0;i<jsonStr.length();i++){
            char c = jsonStr.charAt(i);
            if(level>0&&'\n'==jsonForMatStr.charAt(jsonForMatStr.length()-1)){
                jsonForMatStr.append(getLevelStr(level));
            }
            switch (c) {
            case '{':
            case '[':
                jsonForMatStr.append(c+"\n");
                level++;
                break;
            case ',':
                jsonForMatStr.append(c+"\n");
                break;
            case '}':
            case ']':
                jsonForMatStr.append("\n");
                level--;
                jsonForMatStr.append(getLevelStr(level));
                jsonForMatStr.append(c);
                break;
            default:
                jsonForMatStr.append(c);
                break;
            }
        }
         
        return jsonForMatStr.toString();
 
    }
	
    private static String getLevelStr(int level){
        StringBuffer levelStr = new StringBuffer();
        for(int levelI = 0;levelI<level ; levelI++){
            levelStr.append("    ");
        }
        return levelStr.toString();
    }
}
