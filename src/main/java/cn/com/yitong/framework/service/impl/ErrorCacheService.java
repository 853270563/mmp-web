package cn.com.yitong.framework.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import cn.com.yitong.framework.dao.IbatisDao;
import cn.com.yitong.framework.service.IErrorCacheService;


@Service
public class ErrorCacheService implements IErrorCacheService {
	@Autowired
	public IbatisDao dao;
	public List findAllErrCode(Map paramMap) {
		return dao.findList("ErrorCode.findAllErrCode", paramMap);
		
	}
	
	public List findAllErrMapping(Map paramMap) {
		return dao.findList("ErrorCode.findAllErrMapping", paramMap);
	}

}
