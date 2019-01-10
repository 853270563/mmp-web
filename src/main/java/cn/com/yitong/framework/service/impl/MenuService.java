package cn.com.yitong.framework.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import cn.com.yitong.framework.core.vo.MenuInfo;
import cn.com.yitong.framework.dao.IbatisDao;
import cn.com.yitong.framework.service.IMenuService;


/**
 * 网银菜单数据服务
 * 
 * @author yaoym
 * 
 */
@Service
public class MenuService implements IMenuService {

	@Autowired
	@Qualifier("ibatisDao")
	private IbatisDao dao;

	@Override
	public List<MenuInfo> findAllMenusByAppId(String appId) {
		// TODO Auto-generated method stub
		return dao.findList("P_MENU_INFO.findAllByAppId", appId);
	}

	@Override
	public List findMenu4Map(Map params) {
		return dao.findList("P_MENU_INFO.findMenu4Map", params);
	}

}
