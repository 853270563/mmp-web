package cn.com.yitong.framework.base;

import java.util.Map;

/**
 * 数据库模拟操作接口
 * 
 * @author yaoym
 * 
 */
public interface IDbTransation {

	public Map getResultMap();

	public boolean execute(IBusinessContext ctx, Map rst);

}
