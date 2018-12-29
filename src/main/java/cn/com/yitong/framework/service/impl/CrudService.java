package cn.com.yitong.framework.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import cn.com.yitong.framework.dao.IbatisDao;
import cn.com.yitong.framework.service.ICrudService;


/**
 * 公共的增删改查，单步动作数据库操作
 * 
 * @author yaoym
 * 
 */
@Service
public class CrudService implements ICrudService {

	@Autowired
	@Qualifier("ibatisDao")
	private IbatisDao dao;

	/* (non-Javadoc)
	 * @see com.yitong.app.service.impl.ICrudService#insert(java.lang.String, java.util.Map)
	 */
	@Override
	public boolean insert(String statementName, Map paramMap) {
		dao.insert(statementName, paramMap);
		return true;
	}

	/* (non-Javadoc)
	 * @see com.yitong.app.service.impl.ICrudService#update(java.lang.String, java.lang.Object)
	 */
	@Override
	public boolean update(String statementName, Object paramMap) {
		return dao.update(statementName, paramMap);
	}

	/*
	 * 删除
	 */
	/* (non-Javadoc)
	 * @see com.yitong.app.service.impl.ICrudService#delete(java.lang.String, java.util.Map)
	 */
	@Override
	public boolean delete(String statementName, Map paramMap) {
		return dao.delete(statementName, paramMap);
	}

	/*
	 * 加载单条记录
	 */
	/* (non-Javadoc)
	 * @see com.yitong.app.service.impl.ICrudService#load(java.lang.String, java.util.Map)
	 */
	@Override
	public Map load(String statementName, Map paramMap) {
		return (Map) dao.load(statementName, paramMap);
	}

	/*
	 * 查询列表
	 */
	/* (non-Javadoc)
	 * @see com.yitong.app.service.impl.ICrudService#findList(java.lang.String, java.util.Map)
	 */
	@Override
	public List findList(String statementName, Map paramObj) {
		return dao.findList(statementName, paramObj);
	}

	/* (non-Javadoc)
	 * @see com.yitong.app.service.impl.ICrudService#batch4Update(java.lang.String, java.util.List)
	 */
	@Override
	public boolean batch4Update(final String statementName, final List datas) {
		return dao.batch4Update(statementName, datas);
	}

}
