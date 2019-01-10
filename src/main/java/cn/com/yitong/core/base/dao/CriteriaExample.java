package cn.com.yitong.core.base.dao;

import java.util.ArrayList;
import java.util.List;

/**
 * Ibatis 通用查询类
 * @author lc3@yitong.com.cn
 * @param <E> 要进行查询的类
 */
public class CriteriaExample<E> {

	/**
	 * order by
	 */
    protected String orderByClause;
    /**
     * distinct
     */
    protected boolean distinct;
    /**
     * or查询条件列表
     */
    protected List<Criteria> oredCriteria;

    public CriteriaExample() {
        oredCriteria = new ArrayList<Criteria>();
    }

    protected CriteriaExample(CriteriaExample<E> example) {
        this.orderByClause = example.orderByClause;
        this.oredCriteria = example.oredCriteria;
    }

    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    public String getOrderByClause() {
        return orderByClause;
    }
    
    public void setDistinct(boolean distinct) {
		this.distinct = distinct;
	}

	public boolean isDistinct() {
		return distinct;
	}

    public List<Criteria> getOredCriteria() {
        return oredCriteria;
    }

    public void or(Criteria criteria) {
        oredCriteria.add(criteria);
    }

    public Criteria createCriteria() {
        Criteria criteria = createCriteriaInternal();
        if (oredCriteria.size() == 0) {
            oredCriteria.add(criteria);
        }
        return criteria;
    }

    protected Criteria createCriteriaInternal() {
        Criteria criteria = new Criteria();
        return criteria;
    }

    /**
     * 清空查询条件
     */
    public void clear() {
        oredCriteria.clear();
    }

}
