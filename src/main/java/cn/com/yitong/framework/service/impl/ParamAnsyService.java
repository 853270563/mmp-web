package cn.com.yitong.framework.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import cn.com.yitong.framework.dao.IbatisDao;
import cn.com.yitong.framework.service.IParamAnsyService;

@Service
public class ParamAnsyService implements IParamAnsyService {
	@Autowired
	@Qualifier("ibatisDao")
	private IbatisDao dao;

	@Override
	public boolean insertCommond(Map param) {
		dao.insert("P_PARAM_ANSY.insert", param);
		return true;
	}

	@Override
	public boolean updateCommondStatus(Map param) {
		dao.insert("P_PARAM_ANSY.updateStatus", param);
		return false;
	}

	@Override
	public Map loadCommond(Map param) {
		return (Map) dao.load("P_PARAM_ANSY.query", param);
	}

	@Override
	public List findCommondLog(Map param) {
		return dao.findList("P_PARAM_ANSY.findLog", param);
	}

	@Override
	public String loadSeq() {
		return dao.load("P_PARAM_ANSY.seq", null);
	}

	@Override
	public boolean saveCommRunLog(Map param) {
		dao.insert("P_PARAM_ANSY.insertAnsyLog", param);
		return true;
	}

	@Override
	public List findExeLog(Map param) {
		return null;
	}

}