package cn.com.yitong.ares.jcl;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;

public class JclSqlTemplate extends SqlSessionTemplate{

	public JclSqlTemplate(SqlSessionFactory sqlSessionFactory) {
		super(sqlSessionFactory);
	}

}
