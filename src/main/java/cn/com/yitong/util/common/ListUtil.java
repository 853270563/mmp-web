package cn.com.yitong.util.common;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

/**
 * List 相关的工具类 
 * 
 * @author fxb
 * 
 */
public class ListUtil {


	/**
	 * 判断list的map数据中，是否含有对应的key，与value
	 * 
	 * @param list  
	 * 			list数据格式List&lt;Map&gt;
	 * 
	 * @param key
	 * 			map中的key
	 * 
	 * @param value
	 * 			map中key对应的value
	 * 
	 */
	@SuppressWarnings("rawtypes")
	public static boolean isHasKeyValue(List list,String key,Object value) {
		
		if(null == list || list.isEmpty()) {
			return false;
		}
		boolean flag = false;
		//遍历list
		for (Object object : list) {
			Map map  = (Map) object;
			Object mapValue = map.get(key);
			if(mapValue == value) {
				flag = true;
				break;
			} else if(mapValue instanceof String && value instanceof String){
				String mapStr  = (String) mapValue;
				String valueStr = (String) value;
				if(StringUtils.equals(mapStr, valueStr)){
					flag = true;
					break;
				}
			}  else if(mapValue instanceof Integer && value instanceof Integer){
				int mapStr  = (Integer) mapValue;
				int valueStr = (Integer) value;
				if(mapStr == valueStr){
					flag = true;
					break;
				}
			} else if(mapValue instanceof Double && value instanceof Double){
				double mapStr  = (Double) mapValue;
				double valueStr = (Double) value;
				if(mapStr == valueStr){
					flag = true;
					break;
				}
			} else if(mapValue instanceof Float && value instanceof Float){
				float mapStr  = (Float) mapValue;
				float valueStr = (Float) value;
				if(mapStr == valueStr){
					flag = true;
					break;
				}
			} else if(mapValue instanceof Long && value instanceof Long){
				long mapStr  = (Long) mapValue;
				long valueStr = (Long) value;
				if(mapStr == valueStr){
					flag = true;
					break;
				}
			} else if(mapValue instanceof Byte && value instanceof Byte){
				byte mapStr  = (Byte) mapValue;
				byte valueStr = (Byte) value;
				if(mapStr == valueStr){
					flag = true;
					break;
				}
			} else if(mapValue instanceof Character && value instanceof Character){
				char mapStr  = (Character) mapValue;
				char valueStr = (Character) value;
				if(mapStr == valueStr){
					flag = true;
					break;
				}
			} else if(mapValue instanceof Short && value instanceof Short){
				short mapStr  = (Short) mapValue;
				short valueStr = (Short) value;
				if(mapStr == valueStr){
					flag = true;
					break;
				}
			} else if(mapValue instanceof Boolean && value instanceof Boolean){
				boolean mapStr  = (Boolean) mapValue;
				boolean valueStr = (Boolean) value;
				if(mapStr == valueStr){
					flag = true;
					break;
				}
			}
		}
		return flag;
	}
	
	
	/**
	 * 判断list的map数据中，对应的key的值是否全部与value一直
	 * 
	 * @param list  
	 * 			list数据格式List&lt;Map&gt;
	 * 
	 * @param key
	 * 			map中的key
	 * 
	 * @param value
	 * 			需判断的value
	 * 
	 */
	@SuppressWarnings("rawtypes")
	public static boolean isAllEqualsValue(List list,String key,Object value) {
		
		if(null == list || list.isEmpty()) {
			return false;
		}
		boolean flag = true;
		//遍历list
		for (Object object : list) {
			Map map  = (Map) object;
			Object mapValue = map.get(key);
			if(mapValue instanceof String && value instanceof String){
				String mapStr  = (String) mapValue;
				String valueStr = (String) value;
				if(!StringUtils.equals(mapStr, valueStr)){
					flag = false;
					break;
				}
			}  else if(mapValue instanceof Integer && value instanceof Integer){
				int mapStr  = (Integer) mapValue;
				int valueStr = (Integer) value;
				if(mapStr != valueStr){
					flag = false;
					break;
				}
			} else if(mapValue instanceof Double && value instanceof Double){
				double mapStr  = (Double) mapValue;
				double valueStr = (Double) value;
				if(mapStr != valueStr){
					flag = false;
					break;
				}
			} else if(mapValue instanceof Float && value instanceof Float){
				float mapStr  = (Float) mapValue;
				float valueStr = (Float) value;
				if(mapStr != valueStr){
					flag = false;
					break;
				}
			} else if(mapValue instanceof Long && value instanceof Long){
				long mapStr  = (Long) mapValue;
				long valueStr = (Long) value;
				if(mapStr != valueStr){
					flag = false;
					break;
				}
			} else if(mapValue instanceof Byte && value instanceof Byte){
				byte mapStr  = (Byte) mapValue;
				byte valueStr = (Byte) value;
				if(mapStr != valueStr){
					flag = false;
					break;
				}
			} else if(mapValue instanceof Character && value instanceof Character){
				char mapStr  = (Character) mapValue;
				char valueStr = (Character) value;
				if(mapStr != valueStr){
					flag = false;
					break;
				}
			} else if(mapValue instanceof Short && value instanceof Short){
				short mapStr  = (Short) mapValue;
				short valueStr = (Short) value;
				if(mapStr != valueStr){
					flag = false;
					break;
				}
			} else if(mapValue instanceof Boolean && value instanceof Boolean){
				boolean mapStr  = (Boolean) mapValue;
				boolean valueStr = (Boolean) value;
				if(mapStr != valueStr){
					flag = false;
					break;
				}
			} else if(mapValue != value) {
				flag = false;
				break;
			} 
		}
		return flag;
	}
	
	
	/**
	 * 判断list的map数据中，对应的key的值是否全部与value不一致
	 * 
	 * @param list  
	 * 			list数据格式List&lt;Map&gt;
	 * 
	 * @param key
	 * 			map中的key
	 * 
	 * @param value
	 * 			需判断的value
	 * 
	 */
	@SuppressWarnings("rawtypes")
	public static boolean isAllNotEqualsValue(List list,String key,Object value) {
		if(null == list || list.isEmpty()) {
			return false;
		}
		boolean flag = true;
		//遍历list
		for (Object object : list) {
			Map map  = (Map) object;
			Object mapValue = map.get(key);
			if(mapValue == value) {
				flag = false;
				break;
			} else if(mapValue instanceof String && value instanceof String){
				String mapStr  = (String) mapValue;
				String valueStr = (String) value;
				if(StringUtils.equals(mapStr, valueStr)){
					flag = false;
					break;
				}
			}  else if(mapValue instanceof Integer && value instanceof Integer){
				int mapStr  = (Integer) mapValue;
				int valueStr = (Integer) value;
				if(mapStr == valueStr){
					flag = false;
					break;
				}
			} else if(mapValue instanceof Double && value instanceof Double){
				double mapStr  = (Double) mapValue;
				double valueStr = (Double) value;
				if(mapStr == valueStr){
					flag = false;
					break;
				}
			} else if(mapValue instanceof Float && value instanceof Float){
				float mapStr  = (Float) mapValue;
				float valueStr = (Float) value;
				if(mapStr == valueStr){
					flag = false;
					break;
				}
			} else if(mapValue instanceof Long && value instanceof Long){
				long mapStr  = (Long) mapValue;
				long valueStr = (Long) value;
				if(mapStr == valueStr){
					flag = false;
					break;
				}
			} else if(mapValue instanceof Byte && value instanceof Byte){
				byte mapStr  = (Byte) mapValue;
				byte valueStr = (Byte) value;
				if(mapStr == valueStr){
					flag = false;
					break;
				}
			} else if(mapValue instanceof Character && value instanceof Character){
				char mapStr  = (Character) mapValue;
				char valueStr = (Character) value;
				if(mapStr == valueStr){
					flag = false;
					break;
				}
			} else if(mapValue instanceof Short && value instanceof Short){
				short mapStr  = (Short) mapValue;
				short valueStr = (Short) value;
				if(mapStr == valueStr){
					flag = false;
					break;
				}
			} else if(mapValue instanceof Boolean && value instanceof Boolean){
				boolean mapStr  = (Boolean) mapValue;
				boolean valueStr = (Boolean) value;
				if(mapStr == valueStr){
					flag = false;
					break;
				}
			}
		}
		return flag;
	}
	
	/**
	 * 
	 * 判断list是否为空<br>
	 * 
	 * list == null || list.isEmpty  ==》 true
	 * 
	 * @param list
	 * 
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static boolean isEmpty (List list) {
		if(list == null || list.isEmpty()) {
			return true;
		} 
		return false;
	}
	
	/**
	 * 
	 * 判断Map是否为空<br>
	 * 
	 * map == null || map.isEmpty  ==》 true
	 * 
	 * @param list
	 * 
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static boolean isEmpty (Map map) {
		if(map == null || map.isEmpty()) {
			return true;
		} 
		return false;
	}
	
	
	/**
	 * 
	 * 判断list是否不为空<br>
	 * 
	 * list == null || list.isEmpty  ==》 true
	 * 
	 * @param list
	 * 
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static boolean isNotEmpty (List list) {
		return !isEmpty(list);
	}
	
	/**
	 * 
	 * 判断Map是否不为空<br>
	 * 
	 * map != null && !map.isEmpty  ==》 true
	 * 
	 * @param list
	 * 
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static boolean isNotEmpty (Map map) {
		return !isEmpty(map);
	}
	
	/**
	 * 先根据序列 获取list中的map对象
	 * 再根据map中的key获取对应的value值
	 * 
	 * @param list
	 * @param index
	 * 
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static <T> T getValueByIndex(List list,int index,String key) {
		if(isEmpty(list)) {
			return (T) "";
		}
		Map map = (Map) list.get(index);
		if(isEmpty(map)) {
			return (T) "";
		}
		if(map.containsKey(key)) {
			return (T) map.get(key);
		}
		return (T) "";
		
	}
	
	/**
	 * 先根据序列 获取list中的map对象
	 * 再根据map中的key获取对应的value值
	 * 
	 * @param list
	 * @param index
	 * @param key
	 * 			map中的key
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static <T> T getValueByIndex(List list,int index,String key,Object defl) {
		if(isEmpty(list)) {
			return (T) "";
		}
		Map map = (Map) list.get(index);
		if(isEmpty(map)) {
			return (T) "";
		}
		if(map.containsKey(key)) {
			return (T) map.get(key);
		}
		return (T) defl;
		
	}
	
	 /**
	 * 根据key取得map List中的值
	 * @param paraList 数据列表
	 * @param keyName1 属性名1
	 * @param keyValue1 属性值1
	 * @param keyName2 属性名2
	 * @param keyName3 属性名3
	 * @param Values[]
	 * @return Map
	 */
	public static Map getMapValue(List paraList,String keyName1,String keyValue1){
		if(paraList == null || paraList.size() == 0){			
			return null;
		}
		
		if(StringUtil.isEmpty(keyValue1)){			
			return null;
		}
		Map map;
		try{
			for(int i=0;i<paraList.size();i++){
				map=(Map)paraList.get(i);
				if(map == null || map.isEmpty()){
					continue;
				}
				if(map.get(keyName1)!=null&&map.get(keyName1).toString().equals(keyValue1)){
					return map;				
				}			
			}
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
		return null;
	}
	
}