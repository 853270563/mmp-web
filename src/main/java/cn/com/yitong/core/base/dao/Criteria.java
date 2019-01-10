package cn.com.yitong.core.base.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.util.Assert;

/**
 * Ibatis 通用查询条件
 * @author lc3@yitong.com.cn
 *
 */
public class Criteria {
	
	/**
	 * 不带值的查询条件
	 */
	protected List<String> criteriaWithoutValue;

	/**
	 * 单个值的查询条件
	 */
    protected List<Map<String, Object>> criteriaWithSingleValue;

    /**
     * 值为集合的查询条件
     */
    protected List<Map<String, Object>> criteriaWithListValue;

    /**
     * between查询条件
     */
    protected List<Map<String, Object>> criteriaWithBetweenValue;

    protected Criteria() {
        super();
        criteriaWithoutValue = new ArrayList<String>();
        criteriaWithSingleValue = new ArrayList<Map<String, Object>>();
        criteriaWithListValue = new ArrayList<Map<String, Object>>();
        criteriaWithBetweenValue = new ArrayList<Map<String,Object>>();
    }

    public boolean isValid() {
        return criteriaWithoutValue.size() > 0
            || criteriaWithSingleValue.size() > 0
            || criteriaWithListValue.size() > 0
            || criteriaWithBetweenValue.size() > 0;
    }

    public List<String> getCriteriaWithoutValue() {
        return criteriaWithoutValue;
    }

    public List<Map<String, Object>> getCriteriaWithSingleValue() {
        return criteriaWithSingleValue;
    }

    public List<Map<String, Object>> getCriteriaWithListValue() {
        return criteriaWithListValue;
    }

    public List<Map<String, Object>> getCriteriaWithBetweenValue() {
        return criteriaWithBetweenValue;
    }
    
    public Criteria isNull(String condition) {
        criteriaWithoutValue.add(condition + " is null");
        return (Criteria) this;
    }
    
    public Criteria isNotNull(String condition) {
        criteriaWithoutValue.add(condition + " is not null");
        return (Criteria) this;
    }
    
    public Criteria equalTo(String condition, Object value) {
		addCriterion(condition, "=", value);
		return (Criteria) this;
	}

	public Criteria notEqualTo(String condition, Object value) {
		addCriterion(condition, "<>", value);
		return (Criteria) this;
	}

	public Criteria greaterThan(String condition, Object value) {
		addCriterion(condition, ">", value);
		return (Criteria) this;
	}

	public Criteria greaterThanOrEqualTo(String condition, Object value) {
		addCriterion(condition, ">=", value);
		return (Criteria) this;
	}

	public Criteria lessThan(String condition, Object value) {
		addCriterion(condition, "<", value);
		return (Criteria) this;
	}

	public Criteria lessThanOrEqualTo(String condition, Object value) {
		addCriterion(condition, "<=", value);
		return (Criteria) this;
	}

	public Criteria like(String condition, Object value) {
		addCriterion(condition, "like", value);
		return (Criteria) this;
	}

	public Criteria notLike(String condition, Object value) {
		addCriterion(condition, "not like", value);
		return (Criteria) this;
	}

	public Criteria in(String condition, List<Object> values) {
		addCriterion(condition, "in", values);
		return (Criteria) this;
	}

	public Criteria notIn(String condition, List<Object> values) {
		addCriterion(condition, "not in", values);
		return (Criteria) this;
	}

	public Criteria between(String condition, Object value1, Object value2) {
		addCriterion(condition, "between", value1, value2);
		return (Criteria) this;
	}

	public Criteria andApprIdNotBetween(String condition, Object value1, Object value2) {
		addCriterion(condition, "not between", value1, value2);
		return (Criteria) this;
	}
	
    protected void addCriterion(String condition, String op, Object value) {
    	Assert.notNull(condition, "condition 不能为空");
    	Assert.notNull(op, "op 不能为空");
    	Assert.notNull(value, "value 不能为空");
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("condition", condition + ' ' + op);
        map.put("value", value);
        criteriaWithSingleValue.add(map);
    }

    protected void addCriterion(String condition, String op, List<Object> values) {
    	Assert.notNull(condition, "condition 不能为空");
    	Assert.notNull(op, "op 不能为空");
        Assert.notEmpty(values, "values 不能为空");
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("condition", condition + ' ' + op);
        map.put("values", values);
        criteriaWithListValue.add(map);
    }

    protected void addCriterion(String condition, String op, Object value1, Object value2) {
    	Assert.notNull(condition, "condition 不能为空");
    	Assert.notNull(op, "op 不能为空");
        Assert.notNull(value1, "value1 不能为空");
        Assert.notNull(value2, "value2 不能为空");
        List<Object> list = new ArrayList<Object>();
        list.add(value1);
        list.add(value2);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("condition", condition + ' ' + op);
        map.put("values", list);
        criteriaWithBetweenValue.add(map);
    }

}
