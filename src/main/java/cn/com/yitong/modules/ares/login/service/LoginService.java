package cn.com.yitong.modules.ares.login.service;

import cn.com.yitong.core.session.dao.MhhSessionDao;
import cn.com.yitong.core.session.model.MhhSession;
import cn.com.yitong.core.util.SecurityUtils;
import cn.com.yitong.framework.dao.IbatisDao;
import cn.com.yitong.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class LoginService {

	@Autowired
	@Qualifier("ibatisDao")
	private IbatisDao dao;

	@Resource
	private MhhSessionDao mhhSessionDao;

	/**
	 * 通过用户号查询用户信息
	 */
	public <T> T loadUserById(String userId) {
		return (T) dao.load("SYS_USER.loadById", userId);
	}

	public void saveUserHeadImg(Map<String, Object> param) {
		dao.update("SYS_USER.saveUserHeadImg", param);
	}

	public <T> T findUserByPhone(String userId) {
		return (T) dao.findList("SYS_USER.loadUserByPhone", userId);
	}

	/**
	 * 更新用户登录错误的日志
	 */
	public boolean updateErrorLoginCnt(Map param) {
		return dao.update("SYS_USER.updateErrLoginCnt", param);
	}

	/**
	 * 更新用户最近成功登录的时间
	 */
	public boolean updateLastLgnTime(Map param){
		return dao.update("SYS_USER.updateLastLoginTime", param);
	}

	/**
	 * 通过UserId查对应的角色ID列表
	 */
	@SuppressWarnings("unchecked")
	public List<String> queryRoleIdsByUserId(String userId) {
		List<String> rs = null;
		if(StringUtils.hasText(userId)) {
			rs = dao.findList("SYS_ROLE.queryRoleIdsByUserId", userId);
		}
		if(null == rs || rs.isEmpty()) {
			return null;
		} else {
			return rs;
		}
	}

    /**
     * 获取用户所属的分行机构ID，如所属机构为支行，就返回支行的父机构ID
     */
    public String queryExtOrgId4User(Map<String, Object> user) {
        if(null == user) {
            return null;
        }
        String orgId = StringUtil.getString(user, "OFFICE_ID", null);
        // 尝试取5次，假定机构层级不大于8
        for (int i = 0; i < 8; i++) {
            if(null == orgId) {
                return null;
            }
            Map<String, Object> organ = dao.load("SYS_ORGAN.queryById", orgId);
            if(null == organ) {
                return null;
            }
            int orgLvl = StringUtil.getInt(organ, "GRADE", 3);
            // 分行的机构号为2，总行的为1
            if(3 > orgLvl) {
                return orgId;
            }
            orgId = StringUtil.getString(organ, "ID", null);
        }
        return null;
    }

    /**
     * 登陆成功后把同一设备号其它有效SESSION置为无效
     */
    public void setSessionInvalidByDeviceId(String deviceUuid) {
    	Map<String, Object> params = new HashMap<String, Object>();
    	params.put("deviceUuid", deviceUuid);
		params.put("sessionId", SecurityUtils.getSessionRequired().getId());
    	dao.update("ARES_SESSION.setSessionInvalidByDeviceId", params);
    }

	/**
	 * 生成 loginToken 保存到用户表中 并返回
	 * @param deviceUuid 设备Id
	 * @param userId 用户Id
	 * @return
	 */
	public String setLoginTokenToUser(String deviceUuid, String userId) {
		String loginToken = deviceUuid + SecurityUtils.formatToken(SecurityUtils.genSecurityKey());
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("LOGINTOKEN", loginToken);
		params.put("USER_ID", userId);
		dao.update("SYS_USER.updateLoginTokenByUserId", params);
		return loginToken;
	}

	public MhhSession getSessionById(String token) {
		return mhhSessionDao.queryById(token);
	}
}
