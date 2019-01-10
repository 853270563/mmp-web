package cn.com.yitong.framework.core.bean;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.dom4j.Element;

import cn.com.yitong.framework.base.IBusinessContext;
import cn.com.yitong.framework.net.IParamCover;

/**
 * JSP请求获取 ，将页面请求存储到数据总线
 * 
 * @author yaoym
 * 
 */
public class JspParamCover implements IParamCover {

	public boolean cover(IBusinessContext ctx, HttpServletRequest request,
			boolean signed, String transCode) throws Exception {
		if (null == request || ctx == null)
			return false;
		Element context = ctx.getParamContext();

		Map<String, String[]> params = request.getParameterMap();
		if (null == params || params.isEmpty()) {
			return true;
		}
		for (String key : params.keySet()) {
			Object value = params.get(key);
			if (value instanceof String[]) {
				System.out.println("key:" + key);
				String[] values = (String[]) value;
				for (String text : values) {
					System.out.println("values: " + text);
					try {
						context.addElement(key).setText(text);
					} catch (Exception e) {
						System.out
								.println("-----jsp param cover error---------");
					}
				}
			} else {
				System.out.println("key:" + key + "\t value:" + value);
				context.addElement(key).setText((String) value);
			}
		}
		return true;
	}
}
