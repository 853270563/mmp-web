package cn.com.yitong.market.mm.mgj;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.com.yitong.consts.NS;
import cn.com.yitong.framework.base.IBusinessContext;
import cn.com.yitong.framework.core.bean.BaseControl;
import cn.com.yitong.framework.core.bean.BusinessContext;
import cn.com.yitong.framework.dao.IbatisDao;
import cn.com.yitong.framework.net.IEBankConfParser;
import cn.com.yitong.framework.net.IRequstBuilder;
import cn.com.yitong.framework.net.IResponseParser;
import cn.com.yitong.framework.util.CtxUtil;
import cn.com.yitong.util.YTLog;

@Controller
public class NetPointQueryController extends BaseControl {
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
	@Qualifier("ibatisDao")
	IbatisDao dao;
	
	@SuppressWarnings("rawtypes")
	@RequestMapping("mm/mgj/core/AreaList.do")
	@ResponseBody
	public Map areaInfoQuery(HttpServletRequest request){
		String transCode = "market/core/AreaList";
		Map rst = new HashMap();
		// 初始化数据总线
		IBusinessContext ctx = new BusinessContext(request,
				IBusinessContext.PARAM_TYPE_MAP);
		// 检查报文定义
		if (!CtxUtil.transPrev(ctx, transCode, json2MapParamCover,
				requestBuilder, confParser, rst)) {
			logger.info("报文格式异常...");
			return rst;
		}
		Map params = ctx.getParamMap();
		boolean status = false;
		try {
			// 获取省份信息
			@SuppressWarnings("rawtypes")
			List prvInfoList = dao.findList("AREA_INFO.provInfoQuery", null);
			//获取城市信息
			List cityInfoList = dao.findList("AREA_INFO.cityInfoQuery", params);
			//获取区镇信息
			List townInfoList = dao.findList("AREA_INFO.townInfoQuery", params);
			// 省、市、区关联处理
			List datas = formatAreaInfo(prvInfoList, cityInfoList, townInfoList, rst);
			status = true;//查询成功，更新状态
			CtxUtil.transAfter(ctx, transCode, rst, status, responseParser,
					confParser);
			rst.put(NS.LIST, datas);//放入list中
		} catch (Exception e) {
			logger.error(ctx.getTransLogBean(transCode), e);
			CtxUtil.transError(ctx, transCode, rst);
		}
		return rst;
	}

	/**
	 * 格式区域信息列表
	 * @param prvInfoList
	 * @param cityInfoList
	 * @param townInfoList
	 * @param rst
	 * @return
	 */
	private List formatAreaInfo(List<Map> prvInfoList, List<Map> cityInfoList,
			List<Map> townInfoList, Map rst) {
		logger.info("start format area info list...");
		List list = new ArrayList();
		for(Map mp : prvInfoList){//遍历省份list
			String prvId = (String) mp.get("PROV_ID");//获取省ID
			List cityList = new ArrayList();
			for(Map mc : cityInfoList){//遍历城市list
				String pId = (String) mc.get("PROV_ID");
				String cityId = (String) mc.get("CITY_ID");//获取市ID
				List townList = new ArrayList();
				for(Map mt :  townInfoList){//遍历区镇list
					String cId = (String) mt.get("CITY_ID");
					if(cityId.equals(cId)){
						townList.add(mt);
					}
				}
				mc.put("TOWN_LIST", townList);//将城镇list添加城市list中
				if(prvId.equals(pId)){
					cityList.add(mc);
				}
			}
			mp.put("CITY_LIST", cityList);//将城市list添加到省份list中
			list.add(mp);
		}
		return list;
	};
}
