package cn.com.yitong.modules.test.service;
/**
 * @author luanyu
 * @date   2018年6月8日
 */

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;

import cn.com.yitong.common.utils.SpringContextUtils;

@Service
public class TestService {
	SqlSession sqlSession = SpringContextUtils.getBean(SqlSession.class);

	public void insert() {
		sqlSession.insert("crm.insert", "qwer");
		sqlSession.insert("crm.insert", "qwer2");
	}

}
