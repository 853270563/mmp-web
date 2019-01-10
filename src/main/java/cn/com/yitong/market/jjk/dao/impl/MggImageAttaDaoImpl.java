package cn.com.yitong.market.jjk.dao.impl;

import org.springframework.stereotype.Repository;

import cn.com.yitong.core.base.dao.GenericDAOImpl;
import cn.com.yitong.market.jjk.dao.MggImageAttaDao;
import cn.com.yitong.market.jjk.model.MggImageAtta;

@Repository
public class MggImageAttaDaoImpl extends GenericDAOImpl<MggImageAtta, String> implements MggImageAttaDao {

	@Override
	public String getIbatisNamespace() {
		return "MGG_IMAGE_ATTA";
	}
	
}