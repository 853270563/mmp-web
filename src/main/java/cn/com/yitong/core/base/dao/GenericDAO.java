package cn.com.yitong.core.base.dao;

import java.io.Serializable;
import java.util.List;

import cn.com.yitong.core.base.Page;

/**
 * 通用Dao
 * @author lc3@yitong.com.cn
 *
 */
public interface GenericDAO<E, PK extends Serializable> {
	
    int countByExample(CriteriaExample<E> example);

    int deleteByExample(CriteriaExample<E> example);

    int deleteByPrimaryKey(String id);

    void insert(E record);

    void insertSelective(E record);

    List<E> selectByExample(CriteriaExample<E> example);
    
    Page<E> findPageByExample(CriteriaExample<E> example, Page<E> page);

    E selectByPrimaryKey(String id);

    int updateByExampleSelective(E record, CriteriaExample<E> example);

    int updateByExample(E record, CriteriaExample<E> example);

    int updateByPrimaryKeySelective(E record);

    int updateByPrimaryKey(E record);
}