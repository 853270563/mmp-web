package cn.com.yitong.framework.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import cn.com.yitong.framework.dao.IbatisDao;
import cn.com.yitong.framework.service.ILoginSessionService;

@Service
public class LoginSessionService implements ILoginSessionService {

	@Autowired
	@Qualifier("ibatisDao")
	private IbatisDao dao;

	@Override
	public Object saveLgnSessLog(Map<String, String> map) {
		return dao.insert("P_LGN_SESS_LOG.saveLgnSessLog", map);
	}

	@Override
	public void saveSessionOutInfo(Map<String, String> map) {
		dao.update("P_LGN_SESS_LOG.sessionOut", map);
	}

	@Override
	public boolean updateSessionSuccessMsg(Map<String, String> map) {

		return dao.update("P_LGN_SESS_LOG.updateSessionSuccessMsg", map);
	}

	@Override
	public boolean sessionStatusByLgn_id(Map<String, String> map) {
		return dao.update("P_LGN_SESS_LOG.sessionStatusByLgn_id", map);

	}

	@Override
	public String getSessSeqByCifNo(Map<String, String> map) {
		return dao.load("P_LGN_SESS_LOG.queryByCifNo", map);
	}

	@Override
	public boolean exitStatusByLgn_id(Map<String, String> map) {
		return dao.update("P_LGN_SESS_LOG.exitStatusByLgn_id", map);
	}

	@Override
	public String findLastExitStat(Map<String, String> map) {
		return (String) dao.load("P_LGN_SESS_LOG.findLastExitStat", map);
	}

	@Override
	public boolean updateLgnStat_ExitStat(Map<String, String> map) {
		return dao.update("P_LGN_SESS_LOG.updateLgnStat_ExitStat", map);
	}

	@Override
	public boolean updateSess_SeqStat(Map<String, String> map) {
		return dao.update("P_LGN_SESS_LOG.updateLgnStat_ExitStat", map);
	}

	@Override
	public List pageQuery4Login(Map<String, String> map) {
		return dao.findList("P_LGN_SESS_LOG.pageQueryForLogin", map);
	}

	@Override
	public List findBySessID(String sessId) {
		return dao.findList("P_LGN_SESS_LOG.queryById", sessId);
	}

}
