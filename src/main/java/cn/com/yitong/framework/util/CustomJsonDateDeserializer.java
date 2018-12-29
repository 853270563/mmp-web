package cn.com.yitong.framework.util;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;

/**
 * json格式化的时间格式定制类
 * @author lc3@yitong.com.cn
 *
 */
public class CustomJsonDateDeserializer extends JsonSerializer<Date> {

	@Override
	public void serialize(Date value, JsonGenerator jgen, SerializerProvider provider) throws IOException,
			JsonProcessingException {
		String str = "";
		if(null != value) {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			str = format.format(value);
		}
		jgen.writeString(str);
	}

}
