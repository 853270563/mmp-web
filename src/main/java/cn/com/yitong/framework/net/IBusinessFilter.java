package cn.com.yitong.framework.net;

import cn.com.yitong.framework.base.IBusinessContext;

/**
 * 通用检查接口
 * 
 * @author yaoym
 * 
 */
public interface IBusinessFilter {

	boolean validate(IBusinessContext busiContext, String transCode);
}
