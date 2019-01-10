package cn.com.yitong.market.jjk.dao.impl;

import org.springframework.stereotype.Repository;

import cn.com.yitong.core.base.dao.GenericDAOImpl;
import cn.com.yitong.framework.core.vo.UserSystem;
import cn.com.yitong.market.jjk.dao.UserSystemDao;

@Repository
public class UserSystemDaoImpl extends GenericDAOImpl<UserSystem, String> implements UserSystemDao {

	@Override
	public String getIbatisNamespace() {
		return "MMP_USER_SYSTEM";
	}
	
	
}
