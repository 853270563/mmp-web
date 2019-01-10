package cn.com.yitong.core.base.service;

import java.io.Serializable;
import java.util.List;

import cn.com.yitong.core.base.Page;
import cn.com.yitong.core.base.dao.CriteriaExample;

/**
 * 基础Service
 * 
 * @author lc3@yitong.com.cn
 *
 * @param <E> 实体类型
 * @param <PK> 主键类型
 */
public interface BaseService<E, PK extends Serializable> {
	
    int deleteByPrimaryKey(String id);

    boolean insert(E record);

    List<E> queryByExample(CriteriaExample<E> example);
    
    Page<E> queryPageByExample(CriteriaExample<E> example, Page<E> page);

    E findByPrimaryKey(String id);

    int update(E record);
}
