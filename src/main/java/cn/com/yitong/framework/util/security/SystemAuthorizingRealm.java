/**
 * Copyright &copy; 2012-2013 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package cn.com.yitong.framework.util.security;


import java.util.ArrayList;
import java.util.List;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;

import cn.com.yitong.ares.dao.IbatisDao;
import cn.com.yitong.util.StringUtil;

/**
 * 系统安全认证实现类
 * @author ThinkGem
 * @version 2013-5-29
 */
//@Service
public class SystemAuthorizingRealm extends AuthorizingRealm {


	@Autowired
	protected IbatisDao ibatisDao;
	/**
	 * 认证回调函数, 登录时调用
	 */
	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authcToken) throws AuthenticationException {
		UsernamePasswordToken token = (UsernamePasswordToken) authcToken;
		Object principal = token.getPrincipal();


		return new SimpleAuthenticationInfo(principal, "111111", getName());

	}

	/**
	 * 授权查询回调函数, 进行鉴权但缓存中无用户的授权信息时调用
	 */
	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
		String userId = (String) principals.getPrimaryPrincipal();
		List<String> permissions = new ArrayList<String>();
		List<String> queryForList = ibatisDao.queryForList("mobileMenu.findByUserId", userId);
		for (String string : queryForList) {
			if (StringUtil.isNotEmpty(string)) {

				permissions.add(string);
			}
		}
		if (queryForList.isEmpty()) {
			queryForList.add("noPermission");
		}
		SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
		simpleAuthorizationInfo.addStringPermissions(permissions);
		return simpleAuthorizationInfo;

	}

	

	/**
	 * 清空用户关联权限认证，待下次使用时重新加载
	 */
	public void clearCachedAuthorizationInfo(String principal) {
		SimplePrincipalCollection principals = new SimplePrincipalCollection(principal, getName());
		clearCachedAuthorizationInfo(principals);
	}

	/**
	 * 清空所有关联认证
	 */
	public void clearAllCachedAuthorizationInfo() {
		Cache<Object, AuthorizationInfo> cache = getAuthorizationCache();
		if (cache != null) {
			for (Object key : cache.keys()) {
				cache.remove(key);
			}
		}
	}

	

}
