package cn.com.yitong.market.mm.myw.busiPage.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.json.JSONArray;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.com.yitong.framework.dao.IbatisDao;
import cn.com.yitong.market.mm.myw.busiPage.service.IMywBusiPageService;

@Service
@Transactional
public class MywBusiPageService implements IMywBusiPageService {

	@Autowired
	@Qualifier("ibatisDao")
	private IbatisDao dao;

	@Override
	public boolean deleteBPO(Map<String, Object> params) {
		boolean isOK = dao.delete("MYW_BUSI_PACKAGE.deleteBPO", params);
		return isOK;
	}

	@Override
	public boolean deleteBPS(Map<String, Object> params) {
		boolean isOK = dao.delete("MYW_BUSI_PACKAGE.deleteBPS", params);
		return isOK;
	}

	@Override
	public boolean updateById(Map<String, Object> params) {
		boolean isOK = dao.update("MYW_BUSI_PACKAGE.updateById", params);
		return isOK;
	}

	@Override
	public boolean insertBPO(Map<String, Object> params) {
		dao.insert("MYW_BUSI_PACKAGE.insertBPO", params);
		boolean isOK = true;
		return isOK;
	}

	@Override
	public boolean insertPageBusiConf(Map<String, Object> params) {
		dao.insert("MYW_BUSI_PACKAGE.insertPageBusiConf", params);
		boolean isOK = true;
		return isOK;
	}

	@Override
	public boolean insert(Map<String, Object> params) {
		dao.insert("MYW_BUSI_PACKAGE.insert", params);
		boolean isOK = true;
		return isOK;
	}

	@Override
	public boolean delete(Map<String, Object> params) {
		boolean isOK = dao.delete("MYW_BUSI_PACKAGE.delete", params);
		return isOK;
	}

	@Override
	public JSONArray getJsonStr(String userId) {
		Map<String,Object> rst = new HashMap<String, Object>();
		rst.put("PB_OWNER", userId);
		List<Map<String,Object>> datas = dao.findList("MYW_BUSI_PACKAGE.queryBPO", rst);//查询该用户下所有的业务包信息
		Map<String,Object> menu = new HashMap<String,Object>();
		menu.put("USER_ID",userId);
		List<Map<String,Object>> menus = dao.findList("SYS_MOBILE_ROLE_MENU.queryMenuAtypeList", menu);//查询该用户下客户端A面菜单
		String[] a = new String[datas.size()];
		String[] b = new String[menus.size()];
		for(int i=0;i<datas.size();i++){
			a[i]=datas.get(i).get("PB_ID").toString();
		}
		for(int j=0;j<menus.size();j++){
			b[j]=menus.get(j).get("MENU_ID").toString();
		}
		List<Map<String,Object>> ab = isUp(a,b);
		for(Map<String,Object> ins:ab){
			Map<String,Object> insBPO = new HashMap<String,Object>();
			insBPO.put("PB_ID", ins.get("PB_ID"));
			insBPO.put("PB_TYPE", "1");
			insBPO.put("PB_OWNER", userId);
			dao.insert("MYW_BUSI_PACKAGE.insertBPO", insBPO);
		}
		List<Map<String,Object>> datass=null;
		if(ab!=null&&ab.size()!=0){
			datass = dao.findList("MYW_BUSI_PACKAGE.queryBPO", rst);
		}else{
			datass=datas;
		}
		List<Map<String,Object>> all = new ArrayList<Map<String,Object>>();
		for(Map data : datass){
			if(data.get("PB_TYPE").toString().equals("0")){//0:包
				rst.put("PKG_ID", data.get("PB_ID"));
				rst.put("PKG_OWNER", userId);
				Map pak = dao.load("MYW_BUSI_PACKAGE.queryBusiPageByOwer", rst);
				List<Map<String,Object>> busiList=dao.findList("MYW_BUSI_PACKAGE.queryBusiByPageId", rst);
				for(Map<String,Object> busi : busiList){
					busi.put("BUSI_ID", busi.get("MENU_ID"));
					if(null!=busi.get("FILE_ADDR") && !"".equals(busi.get("FILE_ADDR"))){
						if(busi.get("FILE_ADDR").toString().trim().length()>0){
							busi.put("FILE_ADDR", "/download/userResource/resources.do?type=resources&fileName="+busi.get("FILE_ADDR"));
						}else{
							busi.put("FILE_ADDR", "");
						}
					}else{
						busi.put("FILE_ADDR", "");
					}
				}
				if(pak!=null && pak.size()!=0){
					pak.put("BLIST", busiList);
					all.add(pak);
				}
			}else{//1:单独业务
				rst.put("MENU_NO", data.get("PB_ID"));
				Map<String,Object> busi = dao.load("MYW_BUSI_PACKAGE.queryMenu", rst);
				if(null!=busi){
					if(null!=busi.get("FILE_ADDR") && !"".equals(busi.get("FILE_ADDR"))){
						if(busi.get("FILE_ADDR").toString().trim().length()>0){
							busi.put("FILE_ADDR", "/download/userResource/resources.do?type=resources&fileName="+busi.get("FILE_ADDR"));
						}else{
							busi.put("FILE_ADDR", "");
						}
					}else{
						busi.put("FILE_ADDR", "");
					}
					busi.put("BUSI_ID", data.get("PB_ID"));
					all.add(busi);
				}
			}
		}
		JSONArray jsonArray = JSONArray.fromObject(all);
		return jsonArray;
	}
	
	public List<Map<String,Object>> isUp(String[] str1,String[] str2){
		//str2集合比str1集合多出的提取出来
		List<Map<String,Object>> up=new ArrayList<Map<String,Object>>();
		Set<String> set = new HashSet<String>();
		if(null!=str1 && str1.length>0){
			for(String s : str1) {
				set.add(s);
			}
		}
		
		if(null!=str2 && str2.length>0){
			for(String s : str2) {
				if(!set.contains(s)) {
					Map<String,Object> ups= new HashMap<String,Object>();
					ups.put("PB_ID", s);
					up.add(ups);
				}
			}
		}
		
		return up;
	}
	
}
