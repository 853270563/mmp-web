package cn.com.yitong.portal.thirdApp.service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import cn.com.yitong.portal.thirdApp.dao.AresAuthorityDao;
import cn.com.yitong.portal.thirdApp.model.AresAuthority;
import cn.com.yitong.portal.thirdApp.utils.CasSessionUtils;
import cn.com.yitong.common.persistence.mybatis.MybatisBaseDao;
import cn.com.yitong.common.persistence.mybatis.impl.CriteriaQuery;
import cn.com.yitong.common.service.mybatis.MybatisBaseService;
import cn.com.yitong.common.utils.ConfigUtils;
import cn.com.yitong.core.session.Session;
import cn.com.yitong.core.util.SecurityUtils;
import cn.com.yitong.util.StringUtil;

/**
 *
 * @author lc3@yitong.com.cn
 */
@Service
public class AresAuthorityService extends MybatisBaseService<AresAuthority> {

    @Resource
    private AresAuthorityDao aresAuthorityDao;

    @Override
    public String getTableName() {
        return ConfigUtils.getValue("schema.configPlat") + ".ARES_AUTHORITY";
    }

    @Override
    public String getIdKey() {
        return "authCode";
    }

    @Override
    public MybatisBaseDao<AresAuthority> getDao() {
        return aresAuthorityDao;
    }

    /**
     * 基于当前会话创建新的第三方会话
     * @param appId 应用ID
     * @return
     */
    public AresAuthority create(String appId) {
        Session session = SecurityUtils.getSessionRequired();
        Date now = new Date();
        AresAuthority authority = new AresAuthority();
        authority.setAuthCode(genAuthCode());
        authority.setCodeCreateTime(now);
        authority.setCodeStatus("0");

        // 门户信息设置
        authority.setPortalSessionId(session.getId());
        authority.setSecretKeyWithNoCodec(SecurityUtils.genSecurityKey());

        // 用户应用信息设置
        authority.setUserId(session.getUserId());
        authority.setAppId(appId);

        return authority;
    }

    /**
     * 通过授权码查询
     * @param authCode 授权码
     * @return
     */
    public AresAuthority queryByAuthCode(String authCode) {
        CriteriaQuery query = new CriteriaQuery();
        query.createAndCriteria().equalTo(AresAuthority.TF.authCode, authCode);
        List<AresAuthority> aresAuthorities = getDao().queryByCriteria(query);
        return aresAuthorities.isEmpty() ? null : aresAuthorities.get(0);
    }

    /**
     * 通过访问令牌查询
     * @param accessToken 访问令牌
     * @return
     */
    public AresAuthority queryByAccessToken(String accessToken) {
        CriteriaQuery query = new CriteriaQuery();
        query.createAndCriteria().equalTo(AresAuthority.TF.accessToken, accessToken);
        List<AresAuthority> aresAuthorities = getDao().queryByCriteria(query);
        return aresAuthorities.isEmpty() ? null : aresAuthorities.get(0);
    }

    /**
     * 通过刷新码查询
     * @param freshToken 刷新码
     * @return
     */
    public AresAuthority queryByFreshToken(String freshToken) {
        CriteriaQuery query = new CriteriaQuery();
        query.createAndCriteria().equalTo(AresAuthority.TF.freshToken, freshToken);
        List<AresAuthority> aresAuthorities = getDao().queryByCriteria(query);
        return aresAuthorities.isEmpty() ? null : aresAuthorities.get(0);
    }

    /**
     * 检查第三方会话状态并生成访问令牌与刷新令牌
     * @param authority 第三方会话
     * @return
     */
    public String genToken(AresAuthority authority) {
        Date now = new Date();

        authority.setCreateTime(now);
        authority.setCodeStatus("1");   // 设置授权码状态为已被使用
        authority.setAccessToken(genAccessToken()); // 生成访问令牌
        authority.setFreshToken(genFreshToken());  // 生成刷新令牌
        authority.setLastAccessTime(now);    // 最后访问时间
        authority.setFreshLastAccessTime(now);

        getDao().updateById(authority);

        return null;
    }

    /**
     * 刷新访问令牌
     */
    public AresAuthority refreshToken() {
        AresAuthority authority = CasSessionUtils.getAresAuthority(null);
        Assert.notNull(authority, "无法获取第三方会话信息");
        authority.setAccessToken(genAccessToken()); // 生成访问令牌
        authority.setLastAccessTime(new Date());    // 最后访问时间

        getDao().updateById(authority);

        return authority;
    }

    /**
     * 生成授权码
     * @return
     */
    public String genAuthCode() {
        String token;
        do  {
            token = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 16);
        } while (null != queryByAuthCode(token));
        return token;
    }

    /**
     * 生成访问令牌
     * @return
     */
    public String genAccessToken() {
        String token;
        do  {
            token = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 16);
        } while (null != queryByAccessToken(token));
        return token;
    }

    /**
     * 生成刷新码
     * @return
     */
    public String genFreshToken() {
        String token;
        do  {
            token = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 16);
        } while (null != queryByFreshToken(token));
        return token;
    }

    /**
     * 根据应用标识 查询应用信息
     * @param appId 应用标识
     * @return
     */
    public Map queryAppInfoByAppId(String appId) {
        if(StringUtil.isEmpty(appId)) {
            return null;
        }
        return aresAuthorityDao.queryAppInfoByAppId(appId);
    }
}
