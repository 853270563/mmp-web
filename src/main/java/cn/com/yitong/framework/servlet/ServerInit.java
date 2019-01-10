package cn.com.yitong.framework.servlet;

import cn.com.yitong.common.utils.ConfigUtils;
import cn.com.yitong.core.util.SecurityUtils;
import cn.com.yitong.framework.base.IErrorCaches;

import org.apache.log4j.Logger;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.http.HttpServlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.Properties;


/**
 * 
 * 系统配置初始化
 * <p>
 * 初始化系统上下文
 * </p>
 * 
 * @author yaoym
 */
public class ServerInit extends HttpServlet {

	static Logger logger =Logger.getLogger(ServerInit.class);
	
	public static IErrorCaches errorCaches;

	// 静态数据标识
	public static final boolean STATIC_DATA = false;

	public void init() {
		Resource resource = new ClassPathResource("META-INF/res/ares.properties");
		try {
			Properties props = PropertiesLoaderUtils.loadProperties(resource);
			String codecType = props.getProperty("codec_type");
			if(StringUtils.hasText(codecType)) {
				SecurityUtils.setCodecType(Integer.parseInt(codecType));
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//打印工程版本信息
		printProjectVersion();
	}

	/**
	 * 外围环境参数
	 * 
	 * @param key
	 * @return
	 */
	public static String getString(String key) {
		return ConfigUtils.getValue(key);
	}

	public static String getConfig(String key) {
		return getString(key);
	}

	/**
	 * 外围环境参数
	 * 
	 * @param key
	 * @return
	 */
	public static int getInt(String key) {
		return ConfigUtils.getValue(key, 0);
	}
	
	/**
	 * 打印工程版本信息
	 */
	private void printProjectVersion() {
		StringBuilder sBuilder = new StringBuilder("\n");
		try {
            Enumeration<java.net.URL> urls;
            ClassLoader classLoader = findClassLoader();
            if (classLoader != null) {
                urls = classLoader.getResources("version.properties");
            } else {
                urls = ClassLoader.getSystemResources("version.properties");
            }
            
            if (urls != null) {
                while (urls.hasMoreElements()) {
                    java.net.URL url = urls.nextElement();
                    BufferedReader reader = null;
                    try {
                        reader = new BufferedReader(new InputStreamReader(url.openStream(), "utf-8"));
                        Properties properties = new Properties();
                        properties.load(reader);
                        
                        sBuilder.append("项目名称：").append(properties.getProperty("project.name")).append(", ");
                        sBuilder.append("项目版本：").append(properties.getProperty("build.version")).append(", ");
                        sBuilder.append("构建时间：").append(properties.getProperty("build.time")).append(".\n");
                    } catch (Throwable t) {
                        logger.error(t.getMessage(), t);
                    }finally{
                    	if(reader!=null){
                    		try {
                    			reader.close();
							} catch (Exception e) {
								logger.error(e.getMessage(), e);
							}
                    	}
                    }
                }
            }
		} catch (Throwable t) {
            logger.error(t.getMessage(), t);
        }
		
		String info = sBuilder.toString();
		System.out.println("===========================================================================================================================");
		System.out.println(info);
		System.out.println("===========================================================================================================================");
	}

	private static ClassLoader findClassLoader() {
		return  ServerInit.class.getClassLoader();
    }
	
}
