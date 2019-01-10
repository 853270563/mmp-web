package cn.com.yitong.core.base.service;

import java.io.Serializable;
import java.util.List;

import cn.com.yitong.core.base.Page;
import cn.com.yitong.core.base.dao.CriteriaExample;
import cn.com.yitong.core.base.dao.GenericDAO;

/**
 * 基础Service实现
 * 
 * @author lc3@yitong.com.cn
 *
 * @param <E>
 * @param <PK>
 */
public abstract class BaseServiceImpl<E, PK extends Serializable> implements BaseService<E, PK> {
	
	protected abstract GenericDAO<E, PK> getGenericDAO();

	@Override
	public int deleteByPrimaryKey(String id) {
		return getGenericDAO().deleteByPrimaryKey(id);
	}

	@Override
	public boolean insert(E record) {
		getGenericDAO().insert(record);
		return true;
	}

	@Override
	public List<E> queryByExample(CriteriaExample<E> example) {
		return getGenericDAO().selectByExample(example);
	}

	@Override
	public Page<E> queryPageByExample(CriteriaExample<E> example, Page<E> page) {
		return getGenericDAO().findPageByExample(example, page);
	}

	@Override
	public E findByPrimaryKey(String id) {
		return getGenericDAO().selectByPrimaryKey(id);
	}

	@Override
	public int update(E record) {
		return getGenericDAO().updateByPrimaryKey(record);
	}

}
