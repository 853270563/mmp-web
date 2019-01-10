/**
 * ibatis 基本操作方法
 */
package cn.com.yitong.ares.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

import cn.com.yitong.ares.consts.AresR;
import cn.com.yitong.framework.base.IBusinessContext;
import cn.com.yitong.framework.servlet.ServerInit;
import cn.com.yitong.util.common.StringUtil;

@Repository
@Lazy(false)
public class IbatisDaoImpl implements IbatisDao {
	@Autowired
	SqlSession sqlSession;
	@Override
	public Object insert(String statementName, Object paramMap) {
		return getSqlSession().insert(statementName, paramMap);
	}

	@Override
	public boolean update(String statementName, Object paramMap) {
		return this.getSqlSession().update(statementName, paramMap) > 0;
	}

	@Override
	public boolean delete(String statementName, Object paramMap) {
		return getSqlSession().delete(statementName, paramMap) > 0;
	}

	@Override
	public Map load(String statementName, Object paramMap) {
		return (Map) getSqlSession().selectOne(statementName, paramMap);
	}

	// 获取数据库序列：ibatis事物中序列值不变.需清空缓存
	@Override
	public String loadSeq(String statementName) {
		this.getSqlSession().clearCache();
		return (String) getSqlSession().selectOne(statementName, null);
	}

	@Override
	public Map queryForMap(String statementName, Object paramMap) {
		return load(statementName, paramMap);
	}

	@Override
	public String queryForStr(String statementName, Object paramMap) {
		return (String) getSqlSession().selectOne(statementName, paramMap);
	}

	/**
	 * list查询
	 */
	@Override
	public List queryForList(String statementName, Object paramObj) {
		return this.getSqlSession().selectList(statementName, paramObj);
	}

	/**
	 * 分页查询
	 */
	@Override
	public List pageQuery(String statementName, Object paramObj, IBusinessContext ctx) {

		String sql = SqlHelper.getNamespaceSql(getSqlSession(), statementName, paramObj);

		Map rspMap = ctx.getParamMap();
		Map paramMap = (Map) paramObj;
		int pageNo = 1;
		int pageSize = ServerInit.getInt(AresR.PAGE_SIZE);// 默认分页大小
		if (StringUtil.isNotEmpty(ctx.getParam(AresR.NEXT_KEY))) {
			pageNo = StringUtil.parseInt(ctx.getParam(AresR.NEXT_KEY));
		}
		if (StringUtil.isNotEmpty(ctx.getParam(AresR.PAGE_SIZE))) {
			pageSize = StringUtil.parseInt(ctx.getParam(AresR.PAGE_SIZE));
			if (pageSize <= 0) {
				pageNo = ServerInit.getInt(AresR.PAGE_SIZE);
			}
		}

		paramMap.put(AresR.START_ROW, (pageNo - 1) * pageSize);
		if (!"1".equals(ctx.getParam("IS_EXPORT"))) {
			paramMap.put(AresR.END_ROW, pageNo * pageSize + 1);
		}
		paramMap.put("SQL", sql);
		List<Map> rs = this.getSqlSession().selectList("public.pageQuery", paramMap);

		if (rs.size() == pageSize + 1) {
			rs.remove(rs.size() - 1);
			rspMap.put(AresR.NEXT_KEY, pageNo + 1);
		} else {
			rspMap.put(AresR.NEXT_KEY, "");
		}
		// 将该页的上页标识返回
		rspMap.put("PAGE_NUM", rs.size());
		if (!rs.isEmpty()) {
			rspMap.put("TOTAL_NUM", rs.get(0).get("TOTAL_NUM"));
		} else {
			rspMap.put("TOTAL_NUM", "0");
		}
		return rs;
	}

	@Override
	public boolean batch4Update(String statementName, List datas) {
		this.update(statementName, datas);
		return true;
	}

	@Override
	public boolean batch4Insert(String statementName, List datas) {
		this.insert(statementName, datas);
		return true;
	}

	private SqlSession getSqlSession() {
		return sqlSession;
	}
}
