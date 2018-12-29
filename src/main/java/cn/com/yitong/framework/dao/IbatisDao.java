package cn.com.yitong.framework.dao;

import java.util.List;
import java.util.Map;

public interface IbatisDao {

	/*
	 * 新增
	 */
	public abstract Object insert(String statementName, Object paramMap);

	/*
	 * 更新
	 */
	public abstract boolean update(String statementName, Object paramMap);

	/*
	 * 删除
	 */
	public abstract boolean delete(String statementName, Object paramMap);

	/*
	 * 加载单条记录
	 */
	public abstract <T> T load(String statementName, Object paramMap);

	/*
	 * 查询列表
	 */
	@SuppressWarnings("rawtypes")
	public abstract List findList(String statementName, Object paramObj);

	/**
	 * 批量更新
	 * 
	 * @param statementName
	 * @param datas
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public abstract boolean batch4Update(final String statementName,
										 final List datas);

	/**
	 * 简单的批量更新
	 * 
	 * @param statementNames
	 * @param map
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public abstract boolean batch4Update(final List<String> statementNames,
										 final Map map);

	/**
	 * 批量更新 可指定提交记录数
	 * 
	 * @param statementName
	 * @param datas
	 * @param batchSize
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public abstract boolean batch4Update(final String statementName,
										 final List datas, final int batchSize);

	/**
	 * 加载Map
	 * 
	 * @param statementName
	 * @param paramMap
	 * @return
	 */
	Map queryForMap(String statementName, Object paramMap);

}