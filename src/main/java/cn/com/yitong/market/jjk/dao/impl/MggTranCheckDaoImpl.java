package cn.com.yitong.market.jjk.dao.impl;

import org.springframework.stereotype.Repository;

import cn.com.yitong.core.base.dao.GenericDAOImpl;
import cn.com.yitong.market.jjk.dao.MggTranCheckDao;
import cn.com.yitong.market.jjk.model.MggTranCheck;

@Repository
public class MggTranCheckDaoImpl extends GenericDAOImpl<MggTranCheck, String> implements MggTranCheckDao {
	
	@Override
	public String getIbatisNamespace() {
		return "MGG_TRAN_CHECK";
	}

}
