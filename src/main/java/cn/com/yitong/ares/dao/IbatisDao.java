package cn.com.yitong.ares.dao;

import java.util.List;
import java.util.Map;

import cn.com.yitong.framework.base.IBusinessContext;

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


    public Map queryForMap(String statementName, Object paramMap);


    public String queryForStr(String statementName, Object paramMap);

	public int queryForInt(String statementName, Object paramMap);

    /**
     * 分页查询
     * @param statementName
     * @param paramObj
     * @param rspMap
     * @param ctx
     * @return
     */
    public abstract List pageQuery(String statementName, Object paramObj, IBusinessContext ctx);

    

    public abstract List queryForList(String statementName, Object paramObj);
    
    
    public abstract boolean batch4Update(final String statementName, final List datas);


    /**
     * 事物中获取序列，序列值不变，调用此方法
     * @param statementName
     * @return
     */
    public  String loadSeq(String statementName);
    
    public abstract boolean batch4Insert(final String statementName, final List datas);

}