package cn.com.yitong.modules.creditInvestigate.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jws.WebParam;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;

import cn.com.yitong.framework.service.ICrudService;
import cn.com.yitong.modules.creditInvestigate.IAddUser;

public class AddUserImpl implements IAddUser {
	@Autowired
	ICrudService iCrudService;

	/**
	 * 操作同步用户信息
	 * 
	 * @param text
	 * @return
	 */
	@Override
	public String editUser(@WebParam(name = "arg0") String text) {
		List<Map<String, String>> resultMapList = new ArrayList<Map<String, String>>();
		Map<String, String> resultMap = new HashMap<String, String>();
		try {
			Document document = null;
			try {
				document = DocumentHelper.parseText(text);
			} catch (DocumentException e1) {
				e1.printStackTrace();
			}
			Element rootElm = document.getRootElement();
			Element item = rootElm.element("item");
			Map<String, String> map = new HashMap<String, String>();
			transMap(item,map);
			if (map.get("STATUS").equals("1")) {
				map.put("PASSWORD", "e10adc3949ba59abbe56e057f20f883e");
				resultMap.put("loginName", map.get("LOGIN_NAME"));
				try {
					iCrudService.insert("SYS_USER.insertUser", map);
					resultMap.put("status", "0");
				} catch (Exception e) {
					resultMap.put("status", "1");
				}
			}
			if (map.get("STATUS").equals("2")) {//修改
				/*map.put("UPDATE_DATE", "yes");
				resultMap.put("loginName", map.get("LOGIN_NAME"));*/
				try {
					/*Map<String, Object> mapParent = iCrudService.load(
							"SYS_USER.getDelFlag", map);
					map.put("REV_DEL_FLAG", mapParent.get("DEL_FLAG")
							.toString());*/
					iCrudService.update("SYS_USER.updateUser", map);
					resultMap.put("status", "0");
				} catch (Exception e) {
					resultMap.put("status", "1");
				}
			}
			if (map.get("STATUS").equals("3")) {//修改密碼
				try {
					iCrudService.update("SYS_USER.updateUser", map);
					resultMap.put("status", "0");
				} catch (Exception e) {
					System.out.println(e.getMessage());
					resultMap.put("status", "1");
				}
			}if(map.get("STATUS").equals("4")) {//鎖定
				try {
					iCrudService.update("SYS_USER.updateUser", map);
					resultMap.put("status", "0");
				} catch (Exception e) {
					System.out.println(e.getMessage());
					resultMap.put("status", "1");
				}
			}if(map.get("STATUS").equals("5")) {//修改個人資料
				try {
					iCrudService.update("SYS_USER.updateUser", map);
					resultMap.put("status", "0");
				} catch (Exception e) {
					System.out.println(e.getMessage());
					resultMap.put("status", "1");
				}
			}
			resultMapList.add(resultMap);
			resultMap = new HashMap<String, String>();
			map = new HashMap<String, String>();
			StringBuffer resultMapStr = new StringBuffer(
					"<?xml version='1.0' encoding='UTF-8'?>");
			resultMapStr.append("<Result>");
			for (int i = 0; i < resultMapList.size(); i++) {
				resultMapStr.append("<item>");
				resultMapStr.append("<loginName>"
						+ resultMapList.get(i).get("loginName")
						+ "</loginName>");
				resultMapStr.append("<status>"
						+ resultMapList.get(i).get("status") + "</status>");
				resultMapStr.append("</item>");
			}
			resultMapStr.append("</Result>");
			return resultMapStr.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return e.getMessage();
		}
	}
	
	public void transMap(Element item,Map<String, String> map) {
		String officeId = item.element("officeId").getText().trim();
		String status = item.element("status").getText().trim();
		String passWord = item.element("passWord").getText().trim();
		String delFlag = item.element("delFlag").getText().trim();
		String name = item.element("name").getText().trim();
		String companyId = item.element("companyId").getText().trim();
		String loginName = item.element("loginName").getText().trim();
		String userType = item.element("userType").getText().trim();
		String userSex = item.element("userSex").getText().trim();
		String useFirstMark = item.element("useFirstMark").getText().trim();
		String userStaus = item.element("userStaus").getText().trim();
		String mobile = item.element("mobile").getText().trim();
		String phone = item.element("phone").getText().trim();
		String email = item.element("email").getText().trim();
		String restPswMark = item.element("restPswMark").getText().trim();
		if(status.equals("1")) {
			map.put("RSET_PSW_MARK", restPswMark);
			map.put("OFFICE_ID", officeId);
			map.put("STATUS", status);
			map.put("DEL_FLAG", delFlag);
			map.put("NAME", name);
			map.put("PASSWORD", passWord);
			map.put("COMPANY_ID", companyId);
			map.put("LOGIN_NAME", loginName);
			map.put("ID", loginName);
			map.put("NO", loginName);
			map.put("USER_TYPE", userType);
			map.put("USER_SEX", userSex);
			map.put("USE_FIRST_MARK", useFirstMark);
			map.put("USER_STAUS", userStaus);
			map.put("MOBILE", mobile);
			map.put("PHONE", phone);
			map.put("EMAIL", email);
		}
		if(status.equals("2")) {
			map.put("RSET_PSW_MARK", restPswMark);
			map.put("OFFICE_ID", officeId);
			map.put("STATUS", status);
			map.put("DEL_FLAG", delFlag);
			map.put("NAME", name);
			map.put("PASSWORD", passWord);
			map.put("COMPANY_ID", companyId);
			map.put("LOGIN_NAME", loginName);
			map.put("ID", loginName);
			map.put("NO", loginName);
			map.put("USER_TYPE", userType);
			map.put("USER_SEX", userSex);
			map.put("USE_FIRST_MARK", useFirstMark);
			map.put("USER_STAUS", userStaus);
			map.put("MOBILE", mobile);
			map.put("PHONE", phone);
			map.put("EMAIL", email);
		}
		if(status.equals("3")) {
			map.put("STATUS", status);
			map.put("LOGIN_NAME", loginName);
			map.put("PASSWORD", passWord);
		}
		if(status.equals("4")) {
			map.put("STATUS", status);
			map.put("LOGIN_NAME", loginName);
			map.put("RSET_PSW_MARK", passWord);
		}
		if(status.equals("5")) {
			map.put("STATUS", status);
			map.put("LOGIN_NAME", loginName);
			map.put("NAME", name);
			map.put("USER_SEX", userSex);
			map.put("EMAIL", email);
			map.put("PHONE", phone);
			map.put("MOBILE", mobile);
		}
		
	}
}
