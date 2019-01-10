package cn.com.yitong.modules.hbbProvider.support;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;



import cn.com.yitong.core.util.DictUtils;
import cn.com.yitong.framework.base.IBusinessContext;
import cn.com.yitong.framework.service.ICrudService;
import cn.com.yitong.framework.util.security.MD5Encrypt;
import cn.com.yitong.util.RandomUtil;
import cn.com.yitong.util.StringUtil;
import cn.com.yitong.util.YTLog;

/**
 * 
 * @author shyt_huangqiang
 *
 */
@Component
public class UserSynchroSupport extends AbstractMsgSupport{
	
	private Logger logger = YTLog.getLogger(this.getClass());

	@Autowired
	ICrudService service;
	
	
	@Override
	public void execute(IBusinessContext ctx) {
		JSONObject json = (JSONObject) ctx.getRequestEntry();
		JSONObject jhead = (JSONObject) json.get("head");
		JSONObject jbody = (JSONObject) json.get("body");
		String loginName = jbody.getString("loginName");
		if(null == loginName || loginName == ""){
			parseResponse(ctx, jhead, "loginName不能为空", "0");
			return;
		}
		String officeId = jbody.getString("officeId");
		if(null == officeId || officeId == ""){
			parseResponse(ctx, jhead, "officeId不能为空", "0");
			return;
		}
		String name = jbody.getString("name");
		if(null == name || name == ""){
			parseResponse(ctx, jhead, "name不能为空", "0");
			return;
		}
		String email = jbody.getString("email");
		String mobile = jbody.getString("mobile");
		String id = RandomUtil.randomString16(32);
		String password = MD5Encrypt.MD5(DictUtils.getDictValue("SYS_PARAM", "RESET_PASS_WORD", "888888"));
		Date creatDate = new Date(System.currentTimeMillis()); 
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("ID", id);
		paramMap.put("OFFICE_ID", officeId);
		paramMap.put("LOGIN_NAME", loginName);
		paramMap.put("PASSWORD", password);
		paramMap.put("NO", loginName);
		paramMap.put("NAME", name);
		paramMap.put("EMAIL", email);
		paramMap.put("MOBILE", mobile);
		paramMap.put("USER_TYPE", "1");
		paramMap.put("CREATE_DATE", creatDate);
		paramMap.put("DEL_FLAG", "0");
		boolean flag = service.insert("SYS_USER.insertUserInfo", paramMap);
		if(!flag){
			parseResponse(ctx, jhead, "入库失败！", "0");
		}
		parseResponse(ctx, jhead, "交易成功", "1");
	}
	
	public void parseResponse(IBusinessContext ctx,JSONObject jhead,String msg,String status){
		JSONObject json = new JSONObject();
		JSONObject head = new JSONObject();
		JSONObject body = new JSONObject();
		head.put("transNo", jhead.getString("transNo"));
		head.put("transTime", jhead.getString("transTime"));
		head.put("transCode", jhead.getString("transCode"));
		body.put("status", status);
		body.put("msg", msg);
		json.put("head", head);
		json.put("body", body);
		try {
			int length = json.toString().getBytes("UTF-8").length;
			ctx.setResponseEntry(StringUtil.lpadString(String.valueOf(length), 8, "0") + json.toString());
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			
		}
	}
	

}
