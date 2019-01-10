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
import cn.com.yitong.common.persistence.Page;
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
		Page<Map<String, Object>> page = new Page<Map<String, Object>>(pageNo, pageSize);
		paramMap.put("page", page);
		List<Map> rs = this.getSqlSession().selectList(statementName, paramMap);
		page.initialize();
		if (!page.isLastPage()) {

			rspMap.put(AresR.NEXT_KEY, page.getNext());
		} else {

			rspMap.put(AresR.NEXT_KEY, "");
		}

		rspMap.put("PAGE_NUM", rs.size());

		rspMap.put("TOTAL_NUM", page.getCount());

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

	@Override
	public int queryForInt(String statementName, Object paramMap) {
		// TODO Auto-generated method stub
		return getSqlSession().selectOne(statementName, paramMap);
	}
}
