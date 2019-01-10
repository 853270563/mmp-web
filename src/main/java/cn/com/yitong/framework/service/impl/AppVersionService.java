package cn.com.yitong.framework.service.impl;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import cn.com.yitong.framework.dao.IbatisDao;
import cn.com.yitong.framework.service.IAppVersionService;
import cn.com.yitong.util.YTLog;

@Service
public class AppVersionService implements IAppVersionService {

	private Logger logger = YTLog.getLogger(this.getClass());

	@Autowired
	@Qualifier("ibatisDao")
	private IbatisDao dao;

	@Override
	public Map loadCurrent(Map params) {
		return (Map)dao.load("APP_VERS.load", params);
	}

	@Override
	public List findHistory(Map params) {
		return dao.findList("APP_VERS_HIS.query", params);
	}
}
