package cn.com.yitong.framework.interceptor;

import java.io.Serializable;
import java.util.Properties;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import org.apache.log4j.Logger;
import cn.com.yitong.util.YTLog;

/**
 * 
 * @author sunwei
 *
 */
@Intercepts({
        @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class })
})
public class MybatisTimeLogInterceptor implements Interceptor, Serializable {

	private static final long serialVersionUID = 1L;
	
	private Logger logger = YTLog.getLogger(this.getClass());
    
    private Object excute(Invocation invocation) throws Exception {
    	long before = System.currentTimeMillis();
    	Object object = invocation.proceed();
    	long after = System.currentTimeMillis();
    	long cost = after - before;
    	
    	Object[] args = invocation.getArgs();
    	String id = ((MappedStatement)args[0]).getId();
    	
    	logger.info(id + ", 耗时: " + cost + "毫秒；");
    	
    	return object;
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
    	return this.excute(invocation);
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
    }
}
