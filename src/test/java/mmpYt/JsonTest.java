package mmpYt;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import cn.com.yitong.common.utils.JsonUtils;

/**
 * @author luanyu
 * @date   2018年1月28日
 */
public class JsonTest {
	protected final static ObjectMapper mapper = new ObjectMapper();

	public static void main(String[] args) throws JsonParseException, JsonMappingException, IOException {

		JSONObject jsonObject = new JSONObject();
		jsonObject.put("1", "111");
		System.out.println(jsonObject.toString());
		System.out.println(jsonObject.toJSONString());
		JSONObject parse = JSON.parseObject("{k:1,h:\"luanyu\"}");
		Object object = parse.get("j");
		Object object2 = parse.get("f");
		Object object3 = parse.get("k");
		Map<String, String> jsonToMap = mapper.readValue("{\"k\":1,\"h\":\"luanyu\"}", Map.class);

		Map<String, String> javaObject = JSONObject.toJavaObject(parse, Map.class);
		HashMap<String, Object> hashMap = new HashMap<String, Object>();
		HashMap<String, Object> hashMap2 = new HashMap<String, Object>();
		hashMap.put("ni", 1);
		hashMap2.put("ni", 1);
		hashMap.put("wo", "2");
		hashMap.put("q", hashMap2);
		String jsonString = JSON.toJSONString(hashMap);
		String objectToJson = JsonUtils.objectToJson(hashMap);
		Object object4 = javaObject.get("h");
		Object object5 = javaObject.get("k");
	}

}
