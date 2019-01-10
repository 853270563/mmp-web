package cn.com.yitong.ares.login.service;

import cn.com.yitong.core.util.SecurityUtils;
import cn.com.yitong.framework.dao.IbatisDao;
import cn.com.yitong.framework.service.IPMngOrganService;
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
    private IPMngOrganService organService;

	/**
	 * 通过用户号查询用户信息
	 * 
	 * @param <T>
	 * @param userId
	 * @return
	 */
	public <T> T loadUserById(String userId) {
		return (T) dao.load("SYS_USER.loadById", userId);
	}

	/**
	 * 更新用户登录错误的日志
	 * 
	 * @param param
	 * @return
	 */
	public boolean updateErrorLoginCnt(Map param) {
		return dao.update("SYS_USER.updateErrLoginCnt", param);
	}
//	public boolean updateErrorLoginCnt(Map param) {
//		return dao.update("SYS_USER.updateErrLgnCnt", param);
//	}
	
	/**
	 * 更新用户最近成功登录的时间
	 * @param param
	 * @return
	 */
	public boolean updateLastLgnTime(Map param){
		return dao.update("SYS_USER.updateLastLoginTime", param);
	}
//	public boolean updateLastLgnTime(Map param){
//		return dao.update("SYS_USER.updateLastLgnTime", param);
//	}
	
	/**
	 * 通过UserId查对应的角色ID列表
	 * @param userId
	 * @return
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
     * @param user
     * @return
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
            Map<String, Object> organ = organService.queryById(orgId);
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
}
