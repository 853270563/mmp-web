package cn.com.yitong.modules.ares.mock;

import cn.com.yitong.util.StringUtil;
import cn.com.yitong.util.YTLog;
import org.apache.log4j.Logger;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class CoreService  {

	private Logger logger = YTLog.getLogger(this.getClass());

	@Autowired
	@Qualifier("sqlSessionTemplate")
	private SqlSessionTemplate sqlSessionTemplate;

	/**
	 * 缓存模态数据
	 */
	private static List<Map<String, Object>> mockListCache = new ArrayList<Map<String, Object>>();

	/**
	 * 获取模态数据
	 * @param key
	 * @return
	 */
	public Object getTransContent(String key){
		//首次缓存从数据库获取一次
		if (null == mockListCache || mockListCache.size() <= 0){
			mockListCache();
		}
		return getMapValueByKey(key);
	}

	/**
	 * 0 0/10 * * * ?
	 * 每十分钟更新一次
	 */
	public List<Map<String, Object>> mockListCache(){
		try {
			mockListCache = sqlSessionTemplate.selectList("ARES_TRANS_MOCK.queryAllTransMockData");
		}catch (Exception e){
			logger.warn("自动更新模态数据异常",e);
		}
		return mockListCache;
	}

	/**
	 * 过滤内存中数据
	 * @param key
	 */
	private Object getMapValueByKey(String key){
		final List<Map<String, Object>> mockList = mockListCache;
		if (StringUtil.isNotEmpty(key)){
			for (Map<String, Object> mockCache : mockList){
				if (key.equals(mockCache.get("TRANS_CODE"))){
					return mockCache.get("TRANS_CONTENT");
				}
			}
		}
		return new Object();
	}
}
