package cn.com.yitong.modules.ares.mock;

import cn.com.yitong.consts.AppConstants;
import cn.com.yitong.framework.base.IBusinessContext;
import cn.com.yitong.framework.core.bean.BaseControl;
import cn.com.yitong.framework.core.bean.BusinessContext;
import cn.com.yitong.framework.net.IEBankConfParser;
import cn.com.yitong.framework.net.IRequstBuilder;
import cn.com.yitong.framework.net.IResponseParser;
import cn.com.yitong.util.StringUtil;
import cn.com.yitong.util.YTLog;
import com.alibaba.fastjson.JSON;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.sql.Clob;
import java.util.HashMap;
import java.util.Map;

@Controller
public class CoreController extends BaseControl {

	private Logger logger = YTLog.getLogger(this.getClass());
	@Autowired
	@Qualifier("requestBuilder4db")
    IRequstBuilder requestBuilder;// 请求报文生成器
	@Autowired
	@Qualifier("responseParser4db")
    IResponseParser responseParser;// 响应报文解析器
	@Autowired
	@Qualifier("EBankConfParser4db")
    IEBankConfParser confParser;// 报文装载器

	@Autowired
	private CoreService coreService;

	@RequestMapping("aresMock/{module}/{transCode}.do")
	@ResponseBody
	public Map<String, Object> excute(@PathVariable String module,@PathVariable String transCode,HttpServletRequest request){
		// 初始化数据总线
		IBusinessContext ctx = new BusinessContext(request, IBusinessContext.PARAM_TYPE_MAP);
		// 加载参数
		ctx.initParamCover(json2MapParamCover, transCode, false);
		//-------------------------------mock处理-----------------------------
		Map rtnMap = new HashMap();
		try {
			//根据key关键字获取模态数据[缓存处理]
			Object transMockObj = coreService.getTransContent(module+"/"+transCode);
			if (!(transMockObj instanceof Clob)){
				logger.warn("mock data is empty!");
				rtnMap.put(AppConstants.STATUS, AppConstants.STATUS_FAIL);
				rtnMap.put(AppConstants.MSG, AppConstants.MSG_NOT_FOUND);
				return rtnMap;
			}
			//转化oracle clob ==》String
			String mockData = StringUtil.oracleClob2Str((Clob) transMockObj);
			if(StringUtil.isNotEmpty(mockData)){
				String tempJsonStr = StringEscapeUtils.unescapeHtml(mockData);
				Map map = (Map) JSON.parse(tempJsonStr);
				rtnMap.putAll(map);
			}else {
				rtnMap.put(AppConstants.STATUS, AppConstants.STATUS_FAIL);
				rtnMap.put(AppConstants.MSG, AppConstants.MSG_NOT_FOUND);
			}
		} catch (Exception e) {
			logger.error("mock exception:",e);
			rtnMap.put(AppConstants.STATUS, AppConstants.STATUS_FAIL);
			rtnMap.put(AppConstants.MSG, AppConstants.MSG_FAIL);
		}
		return rtnMap;
	}
}
