package cn.com.yitong.framework.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import cn.com.yitong.framework.core.vo.FunVistLog;
import cn.com.yitong.framework.dao.IbatisDao;
import cn.com.yitong.framework.service.IFunVistLogService;


@Service
public class FunVistLogService implements IFunVistLogService {

	@Autowired
	@Qualifier("ibatisDao")
	private IbatisDao dao;

	@Override
	public String save(FunVistLog funVistLog) {
		return (String) dao.insert("P_FUN_VIST_LOG.insert", funVistLog);
	}

	@Override
	public void updateOutTime(FunVistLog funVistLog) {
		dao.update("P_FUN_VIST_LOG.updateInfo", funVistLog);

	}

	@Override
	public String getTranSeq() {
		return (String) dao.load("P_FUN_VIST_LOG.findTranSeq", null);

	}

	@Override
	public List pageQuery(Map param) {
		return dao.findList("P_FUN_VIST_LOG.pageQuery", param);
	}

	@Override
	public int pageCount(Map param) {
		return Integer.valueOf(String.valueOf(dao.load("", param)));
	}

}
