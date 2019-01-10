package cn.com.yitong.core.dao;

import java.util.List;

import cn.com.yitong.common.persistence.annotation.MyBatisDao;
import cn.com.yitong.core.model.SysDict;

/**
 * @author zhuzengpeng<zzp@yitong.com.cn>
 */
@MyBatisDao
public interface DictDao{

	/**
	 * 根据type查询数据字典值
	 */
	List<SysDict> getDictValuesFromDb(String dictTypeCode);
}
