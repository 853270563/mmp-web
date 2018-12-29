package cn.com.yitong.framework.dao.support;

import java.util.HashMap;
import java.util.Map;

/**
 * Oracle 对应的 ibatis类型自动转换基类
 * @author lc3@yitong.com.cn
 */
public class OracleObjectTypeHandler extends AbstractObjectTypeHandler {
    @Override
    protected Map<String, String> getTypeHandler() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("oracle.sql.TIMESTAMP", "timestampValue");
        return map;
    }
}
