package ${basePackageName}.dao;

import cn.com.yitong.common.persistence.mybatis.MybatisBaseDao;
import ${basePackageName}.model.${table.className};

import cn.com.yitong.common.persistence.annotation.MyBatisDao;

/**
 * @Description: ${table.remarks}
 * @author ${author}
 */
@MyBatisDao
public interface ${table.className}Dao extends MybatisBaseDao<${table.className}> {
}
