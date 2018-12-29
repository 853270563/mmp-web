package cn.com.yitong.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import cn.com.yitong.common.persistence.BaseEntity;

/**
 * Map 相关的工具类 提供简单的xpath功能遍历json map;
 * 
 * @author yaoym
 * 
 */
public class MapUtil {

	private static final String[] EMPTY_ARY = {};
	private static final Map EMPTY_MAP = new HashMap();
	private static final List EMPTY_LIST = new ArrayList();

	private static final String SPIT_CHAR = "/";

	/**
	 * 节点遍历<br>
	 * 相当于dom4j.selectSingleNode(xpath)<br>
	 */
	public static Map singleNode(Map map, String xpath) {
		String[] paths = splitPath(xpath);
		return singleNode(map, paths, 0, paths.length - 1);
	}

	/**
	 * 深度遍历节点
	 */
	private static Map singleNode(Map map, String[] paths, int index,
			int endIndex) {
		if (index > endIndex) {
			return map;
		}
		String curPath = paths[index];
		if (StringUtil.isEmpty(curPath)) {
			if (index < endIndex) {
				return singleNode(map, paths, index + 1, endIndex);
			}
			return EMPTY_MAP;
		}
		Object node = map.get(paths[index]);
		if (null == node) {
			return EMPTY_MAP;
		}
		// 继续深入
		if (node instanceof Map) {
			return singleNode((Map) node, paths, index + 1, endIndex);
		} else if (node instanceof List) {
			System.out.println("current list ");
			List<Map> nodes = (List) node;
			if (nodes != null) {
				return singleNode(nodes.get(0), paths, index + 1, endIndex);
			}
			// System.out.println("list ies empty");
		}
		return EMPTY_MAP;
	}

	private static String[] splitPath(String xpath) {
		if (StringUtil.isEmpty(xpath)) {
			return EMPTY_ARY;
		}
		return xpath.split(SPIT_CHAR);
	}

	/**
	 * 获取单节点文本 <br>
	 * 相当于 dom4j.selectSingleNode(xpath).getText()<br>
	 */
	public static String singleNodeText(Map map, String xpath) {
		String[] paths = splitPath(xpath);
		if (paths.length > 1) {
			String last = paths[paths.length - 1];
			Map node = singleNode(map, paths, 0, paths.length - 2);
			if (null != node) {
				return getMapString(node, last, "");
			}
			return "";
		}
		return getMapString(map, xpath, "");
	}

	public static String getMapString(Map node, String key, String def) {
		if (node.containsKey(key)) {
			return node.get(key).toString();
		}
		return def;
	}

	/**
	 * 获取列表 相当于 dom4j.selectNodes(xpath) <br>
	 */
	public static List selectNodes(Map map, String xpath) {
		String[] paths = splitPath(xpath);
		if (paths.length > 1) {
			Object obj = singleObject(map, paths, 0, paths.length - 1);
			if (obj instanceof List) {
				return (List) obj;
			}
			return EMPTY_LIST;
		}
		Object obj = map.get(xpath);
		if (obj instanceof List) {
			return (List) obj;
		}
		return EMPTY_LIST;
	}

	private static Object singleObject(Map map, String[] paths, int index,
			int endIndex) {
		if (index > endIndex) {
			return map;
		}
		String curPath = paths[index];
		if (StringUtil.isEmpty(curPath)) {
			if (index < endIndex) {
				return singleObject(map, paths, index + 1, endIndex);
			}
			return EMPTY_MAP;
		}
		Object node = map.get(paths[index]);
		if (null == node) {
			return null;
		}
		// System.out.println("current index: " + index + ":" + node);
		// 继续深入
		if (node instanceof Map) {
			return singleObject((Map) node, paths, index + 1, endIndex);
		} else if (node instanceof List) {
			// System.out.println("current list ");
			List<Map> nodes = (List) node;
			if (nodes != null) {
				if (index == endIndex)
					return nodes;
				return singleObject(nodes.get(0), paths, index + 1, endIndex);
			}
			// System.out.println("list ies empty");
		}
		return null;
	}

	public static String toListPath(String xpath) {
		if (xpath != null) {
			return xpath.replaceAll("\\/e\\/", "/");
		}
		return null;
	}

	/**
	 * 任意位置节点 <br>
	 * 任意节点查找以//开头，不支持两层连续列表<br>
	 * <span>真实路径：list/map/AcctNo </span><br>
	 * <span>任意节点 写法：//map/AcctNo </span><br>
	 * <span>任意节点 写法：//AcctNo </span>
	 * 
	 * @param map
	 * @param xpath
	 * @return
	 */
	public static String singleAnsyNodeText(Map map, String xpath) {
		if (xpath == null || !xpath.startsWith("//")) {
			return "";
		}
		xpath = xpath.substring(2);
		Object obj = singleAnsyNode(map, xpath.split("/"), 0, false);
		return obj == null ? null : obj.toString();
	}

	/**
	 * 任意节点查找以//开头，不支持两层连续列表<br>
	 * <span>list/map/AcctNo </span><br>
	 * <span>//map/AcctNo </span>
	 * 
	 * @param map
	 * @param xpaths
	 * @param index
	 * @param needJoin
	 *            是否已开始
	 * @return
	 */
	private static Object singleAnsyNode(Map map, String[] xpaths, int index,
			boolean needJoin) {
		needJoin = needJoin ? true : map.containsKey(xpaths[index]);
		if (needJoin) {
			Object obj = map.get(xpaths[index]);
			if (obj == null) {
				return null;
			}
			if (index == (xpaths.length - 1)) {
				// 已适配完成
				return obj;
			}
			// System.out.println("-------" + xpaths[index]);
			if (obj instanceof Map) {
				return singleAnsyNode((Map) obj, xpaths, index + 1, true);
			} else if (obj instanceof List) {
				// 进入列表遍历:最多两层连续的列表
				List datas = (List) obj;
				Object next = datas.get(0);
				if (next instanceof Map) {
					return singleAnsyNode((Map) next, xpaths, index + 1, true);
				} else if (next instanceof List) {
					// 进入第二层列表递归
					return null;
				}
			}
		} else {
			Set<String> keys = map.keySet();
			for (String key : keys) {
				Object item = map.get(key);
				if (item instanceof List) {
					// 进入列表遍历:不支持连续的列表
					List datas = (List) item;
					Object next = datas.get(0);
					if (next instanceof Map) {
						return singleAnsyNode((Map) next, xpaths, 0, false);
					}
				} else if (item instanceof Map) {
					return singleAnsyNode((Map) item, xpaths, 0, false);
				}
			}
		}
		return null;
	}

	public static <T> T getMapValue(Map map, String key, Object def) {
		if (null != map && null != key && map.containsKey(key)) {
			try {
				return (T) map.get(key);
			} catch (Exception e) {
			}
		}
		return (T) def;
	}
	
	public static List<Map<String, Object>> parseJSON2List(String jsonStr){  
        JSONArray jsonArr = JSONArray.fromObject(jsonStr);  
        List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();  
        Iterator<JSONObject> it = jsonArr.iterator();  
        while(it.hasNext()){  
            JSONObject json2 = it.next();  
            list.add(parseJSON2Map(json2.toString()));  
        }  
        return list;  
    }  
      
     
    public static Map<String, Object> parseJSON2Map(String jsonStr){  
        Map<String, Object> map = new HashMap<String, Object>();  
        //最外层解析  
        JSONObject json = JSONObject.fromObject(jsonStr);  
        for(Object k : json.keySet()){  
            Object v = json.get(k);   
            //如果内层还是数组的话，继续解析  
            if(v instanceof JSONArray){  
                List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();  
                Iterator<JSONObject> it = ((JSONArray)v).iterator();  
                while(it.hasNext()){  
                    JSONObject json2 = it.next();  
                    list.add(parseJSON2Map(json2.toString()));  
                }  
                map.put(k.toString(), list);  
            } else {  
                map.put(k.toString(), v);  
            }  
        }  
        return map;  
    }  
      
     
    public static List<Map<String, Object>> getListByUrl(String url){  
        try {  
            //通过HTTP获取JSON数据  
            InputStream in = new URL(url).openStream();  
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));  
            StringBuilder sb = new StringBuilder();  
            String line;  
            while((line=reader.readLine())!=null){  
                sb.append(line);  
            }  
            return parseJSON2List(sb.toString());  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
        return null;  
    }  
      
     
    public static Map<String, Object> getMapByUrl(String url){  
        try {  
            //通过HTTP获取JSON数据  
            InputStream in = new URL(url).openStream();  
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));  
            StringBuilder sb = new StringBuilder();  
            String line;  
            while((line=reader.readLine())!=null){  
                sb.append(line);  
            }  
            return parseJSON2Map(sb.toString());  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
        return null;  
    }

	/**
	 * map to entity
	 * @param map
	 * @param entityClass
	 * @return
	 * @throws Exception
	 */
	public static <T extends BaseEntity> T map2EntityHandler(Map<String, Object> map, Class<T> entityClass) throws Exception{
		if (entityClass == null) {
			return null;
		}
		T instance = entityClass.newInstance();
		if (map == null || map.isEmpty()) {
			return instance;
		}

		Class<?>[] classes = entityClass.getDeclaredClasses();
		Class<?> tfClass = classes[0];
		Field[] fields = tfClass.getDeclaredFields();
		if (fields.length == 0) {
			return instance;
		}
		for(Field field : fields) {
			String columnName = (String)field.get(null);
			if (map.containsKey(columnName)) {
				String fieldName = field.getName();
				Field entityField = entityClass.getDeclaredField(fieldName);
				entityField.setAccessible(true);
				Class<?> type = entityField.getType();
				entityField.set(instance, map.get(columnName));
			}
		}

		return instance;
	}
	

	/**
	 * entity to map
	 * @param map
	 * @param entityClass
	 * @return
	 * @throws Exception
	 */
	public static Map<String, Object> entity2MapHandler(BaseEntity entity) throws Exception{
		Map<String, Object> map = new HashMap<String, Object>();
		if (entity == null) {
			return map;
		}

		Class<?>[] classes = entity.getClass().getDeclaredClasses();
		Class<?> tfClass = classes[0];
		Field[] tfFields = tfClass.getDeclaredFields();
		if (tfFields.length == 0) {
			return map;
		}
		for(Field tfField : tfFields) {
			String columnName = (String)tfField.get(null);
			String tfFieldName = tfField.getName();
			try {
				Field entityFiled = entity.getClass().getDeclaredField(tfFieldName);
				entityFiled.setAccessible(true);
				Object object = entityFiled.get(entity);
				map.put(columnName, object == null ? "" : object);
			} catch (Exception e) {
			}
		}
		return map;
	}
}