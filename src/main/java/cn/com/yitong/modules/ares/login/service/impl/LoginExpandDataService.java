package cn.com.yitong.modules.ares.login.service.impl;

import cn.com.yitong.framework.dao.IbatisDao;
import cn.com.yitong.modules.ares.login.service.ILoginExpandDataService;
import cn.com.yitong.util.YTLog;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class LoginExpandDataService implements ILoginExpandDataService {

	private Logger logger = YTLog.getLogger(this.getClass());

	@Autowired
	@Qualifier("ibatisDao")
	private IbatisDao dao;

	/**
	 * 文件图片下载前缀
	 */
	private static final String downLoadApiPrefix = "/download/userResource/file.do?fileName=";

	@Override
	public List<Map<String, Object>> getMenuA(Map<String, Object> params) {
		//获取A面菜单列表
		List<Map<String,Object>> menuAList = dao.findList("SYS_MOBILE_ROLE_MENU.queryMenuAtypeList", params);
		if (null == menuAList || menuAList.size() <= 0){
			return new ArrayList<Map<String,Object>>();
		}
		//初始化最大菜单坐标
		Integer maxMenuPos = menuAList.size();
		for (Map<String,Object> menuMap : menuAList){
			menuMap.put("MENU_IMG", downLoadApiPrefix + menuMap.get("FILE_ADDR"));
			/*Integer maxMenuPosTemp = Integer.valueOf((String) menuMap.get("MENU_POSITION"));
			maxMenuPos = maxMenuPosTemp > maxMenuPos ? maxMenuPosTemp : maxMenuPos;*/
		}
		//把最大坐标放在列表第一个
		if (maxMenuPos > 0){
			params.put("MAX_MENU_POSITION", maxMenuPos);
		}
		return menuAList;
	}

	@Override
	public List<Map<String, Object>> getMenuB(Map<String, Object> params) {
		//先获取一级菜单
		params.put("MENU_LEVEL", "1");
		List<Map<String,Object>> menuBList = dao.findList("SYS_MOBILE_ROLE_MENU.queryMenuUserBtypeList", params);
		//非空判断
		if (null == menuBList || menuBList.size() <= 0){
			return new ArrayList<Map<String,Object>>();
		}
		for (Map<String,Object> menuMap : menuBList){
			menuMap.put("MENU_IMG", downLoadApiPrefix + menuMap.get("FILE_ADDR"));
			menuMap.put("MENU_IMG_S", downLoadApiPrefix + menuMap.get("FILE_ADDRS"));
		}
		return menuBList;
	}
}
