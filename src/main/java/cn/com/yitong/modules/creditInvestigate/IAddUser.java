package cn.com.yitong.modules.creditInvestigate;

import javax.jws.WebParam;
import javax.jws.WebService;


@WebService
public interface IAddUser {
    //@WebParam(name="arg0")可有可无，为了增强可读性  
  //  public String insertUser(@WebParam(name="arg0")String text);  
    
    /**
     * 修改用户信息
     * @param text
     * @return
     */
	public String editUser(@WebParam(name = "text") String text);
}
