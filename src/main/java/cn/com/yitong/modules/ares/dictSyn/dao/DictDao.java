package cn.com.yitong.modules.ares.dictSyn.dao;

import java.util.List;
import java.util.Map;

import cn.com.yitong.common.persistence.annotation.MyBatisDao;
import cn.com.yitong.modules.ares.dictSyn.model.SysDict;

/**
 * @author zhuzengpeng<zzp@yitong.com.cn>
 */
@MyBatisDao
public interface DictDao{

	/**
	 * 根据type查询数据字典值
	 */
	List<SysDict> getDictValuesFromDb(String dictTypeCode);
	List<Map<String,Object>> getDictByType(String dictTypeCode);
}
