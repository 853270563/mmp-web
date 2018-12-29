package cn.com.yitong.framework.service;

import java.util.List;
import java.util.Map;

/**
 * 公共的增删改查，单步动作数据库操作
 * 
 * @author yaoym
 * 
 */
public interface ICrudService {

	/**
	 * 增加
	 * 
	 * @param statementName
	 * @param paramMap
	 * @return
	 */
	public abstract boolean insert(String statementName, Map paramMap);

	/**
	 * 删除
	 * 
	 * @param statementName
	 * @param paramMap
	 * @return
	 */
	public abstract boolean update(String statementName, Object paramMap);

	/*
	 * 删除
	 */
	public abstract boolean delete(String statementName, Map paramMap);

	/*
	 * 加载单条记录
	 */
	public abstract Map load(String statementName, Map paramMap);

	/*
	 * 查询列表
	 */
	public abstract List findList(String statementName, Map paramObj);

	/**
	 * 单一批量更新
	 * 
	 * @param statementName
	 * @param datas
	 * @return
	 */
	public abstract boolean batch4Update(final String statementName,
										 final List datas);

}