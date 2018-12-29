<#assign daoName = table.className?uncap_first + "Dao" />
package ${basePackageName}.service;

import cn.com.yitong.common.utils.ConfigUtils;
import cn.com.yitong.common.persistence.mybatis.MybatisBaseDao;
import cn.com.yitong.common.service.mybatis.MybatisBaseService;
import ${basePackageName}.dao.${table.className}Dao;
import ${basePackageName}.model.${table.className};
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 *
 * @author ${author}
 */
@Service
public class ${table.className}Service extends MybatisBaseService<${table.className}> {

    @Resource
    private ${table.className}Dao ${daoName};

    @Override
    public String getTableName() {
        return ConfigUtils.getValue("${table.schemaPropName}") + ".${table.name}";
    }

    @Override
    public String getIdKey() {
        return "${table.primaryField.name}";
    }

    @Override
    public MybatisBaseDao<${table.className}> getDao() {
        return ${daoName};
    }
}
