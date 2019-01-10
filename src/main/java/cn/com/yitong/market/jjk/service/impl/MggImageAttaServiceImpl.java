package cn.com.yitong.market.jjk.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import cn.com.yitong.core.base.dao.GenericDAO;
import cn.com.yitong.core.base.service.BaseServiceImpl;
import cn.com.yitong.market.jjk.dao.MggImageAttaDao;
import cn.com.yitong.market.jjk.model.MggImageAtta;
import cn.com.yitong.market.jjk.service.MggImageAttaService;

/**
 * 
 * @author lc3@yitong.com.cn
 *
 */
@Service
public class MggImageAttaServiceImpl extends BaseServiceImpl<MggImageAtta, String> implements MggImageAttaService {
	
	@Resource
	private MggImageAttaDao tranAttaDAO;

	@Override
	protected GenericDAO<MggImageAtta, String> getGenericDAO() {
		return tranAttaDAO;
	}

}
