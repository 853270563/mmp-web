package cn.com.yitong.actions.as;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;

import cn.com.yitong.actions.atom.AbstractOp;
import cn.com.yitong.ares.flow.IAresSerivce;
import cn.com.yitong.framework.base.IBusinessContext;
import cn.com.yitong.util.DicTransfrom;

/**
 * 新增委托预评参数组装
 */
@Component
public class BuildArgsOp extends AbstractOp implements IAresSerivce {

	protected Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public int execute(IBusinessContext ctx) {
		logger.debug("--run--");
		List<Map> pledgeList = (List) ctx.getParamMap().get("pledge");
		JSONObject businessData = new JSONObject();//业务数据
		List propertyData = new ArrayList();//物业数据
		List outCompany = new ArrayList();//评估结构列表
		Map outCompanyMap = new HashMap<String, Object>();
//		List datObject = new ArrayList();//物业列表//2018-10-15 update
		List datObject2 = new ArrayList();//物业列表2 2018-10-15 update
		Map datObjectMap = new HashMap<String, Object>();
		Map paramMap = new HashMap<String, Object>();
		businessData.put("clientName", ctx.getParamMap().get("clientName"));
		businessData.put("clientPhone", ctx.getParamMap().get("clientPhone"));
		businessData.put("projectName", pledgeList.get(0).get("house_name"));
		ctx.getParamMap().put("businessData", businessData);
		String ruleId = String.valueOf(ctx.getParamMap().get("ruleId"));
		String subRuleType = String.valueOf( ctx.getParamMap().get("subRuleType"));
		String outCompanyId = String.valueOf(ctx.getParamMap().get("outCompanyId"));
		String outCompanyName = String.valueOf(ctx.getParamMap().get("outCompanyName"));
		String propertyPattern = String.valueOf( ctx.getParamMap().get("propertyPattern"));
		outCompanyMap.put("outCompanyId", outCompanyId);
		outCompanyMap.put("outCompanyName", outCompanyName);
		outCompany.add(outCompanyMap);
		Map propertyMap = new HashMap<String, Object>();//2018-10-15 update
//		Map propertyMap2 = new HashMap<String, Object>();//2018-10-15 update
		String pledge_pro="";
		String pledge_city="";
		for(Map map : pledgeList){
//			List datObject2 = new ArrayList();//物业列表//2018-10-15 update
//			Map propertyMap = new HashMap<String, Object>();//2018-10-15 update
			
			List datObject = new ArrayList();//2018-10-15 update
			Map propertyMap2 = new HashMap<String, Object>();//2018-10-15 update
			String pro = String.valueOf(map.get("pledge_pro"));
//			String pro = "420000";
			String city = String.valueOf(map.get("pledge_city"));
			String pledge_county = String.valueOf(map.get("pledge_county"));
//			String pledge_county = "420102";
			String house_type = String.valueOf(map.get("house_type"));
//			house_type = DicTransfrom.subTypeTransfrom(house_type);
//			String house_type = "9006001";
			String house_name = String.valueOf(map.get("house_name"));
			String current_floor = String.valueOf(map.get("current_floor"));
			if(current_floor == ""){
				current_floor = "0";
			}
			String total_floor = String.valueOf(map.get("total_floor"));
			if(total_floor == ""){
				total_floor = "0";
			}
			String pledge_detail = String.valueOf(map.get("pledge_detail"));
			String house_area = String.valueOf(map.get("house_area"));
			datObjectMap.put("projectName", house_name);
			datObjectMap.put("totalFloor", total_floor);
			datObjectMap.put("floorNumber", current_floor);
			datObjectMap.put("address", pledge_detail);
			datObjectMap.put("buildingArea", house_area);
			datObjectMap.put("areaId", pledge_county);
			datObjectMap.put("typeCode", "1031001");
			datObjectMap.put("subTypeCode", "9006001");
			paramMap.put("AREA_ID", pro);
			String proName = String.valueOf(getDao(ctx).queryForMap("getAreaName.getareaname", paramMap).get("AREA_NAME"));
			paramMap.put("AREA_ID", city);
			String cityName = String.valueOf(getDao(ctx).queryForMap("getAreaName.getareaname", paramMap).get("AREA_NAME"));
			paramMap.put("AREA_ID", pledge_county);
			String countyName = String.valueOf(getDao(ctx).queryForMap("getAreaName.getareaname", paramMap).get("AREA_NAME"));
			datObjectMap.put("areaName", countyName);
			datObjectMap.put("propertyPattern", propertyPattern);
			propertyMap.put("provinceId", pro);	
			propertyMap.put("provinceName", proName);	
			propertyMap.put("cityId", city);	
			propertyMap.put("cityName", cityName);
			propertyMap.put("ruleId", ruleId);	
			propertyMap.put("subRuleType", subRuleType);	
			propertyMap.put("outCompany", outCompany);	
			if(pledge_pro != "" && pledge_city != ""){
				if(!pledge_pro.equals(pro) && !pledge_city.equals(city)){//2018-10-15 update
					datObject.add(datObjectMap);
					propertyMap2.put("provinceId", pro);	
					propertyMap2.put("provinceName", proName);	
					propertyMap2.put("cityId", city);	
					propertyMap2.put("cityName", cityName);
					propertyMap2.put("ruleId", ruleId);	
					propertyMap2.put("subRuleType", subRuleType);	
					propertyMap2.put("outCompany", outCompany);	
					
					propertyMap2.put("datObject", datObject);//2018-10-15 update
					propertyData.add(propertyMap2);//2018-10-15 update
					pledge_pro = pro;//2018-10-15 update
					pledge_city = pro;//2018-10-15 update
					continue;
				}else{
					datObject2.add(datObjectMap);
//					propertyMap.put("datObject", datObject2);//2018-10-15 update
					pledge_pro = pro;
					pledge_city = pro;
//					propertyData.add(propertyMap);//2018-10-15 update
					continue;
				}
			}
			datObject2.add(datObjectMap);
//			propertyMap.put("datObject", datObject2);	//2018-10-15 update
			pledge_pro = pro;
			pledge_city = pro;
//			propertyData.add(propertyMap);//2018-10-15 update
			
		}
		propertyMap.put("datObject", datObject2);//2018-10-15 update
		propertyData.add(propertyMap);//2018-10-15 update
//		if(null != propertyMap2 && !propertyMap2.isEmpty()){//2018-10-15 update
//			propertyMap2.put("datObject", datObject);//2018-10-15 update
//			propertyData.add(propertyMap2);//2018-10-15 update
//		}//2018-10-15 update
		ctx.getParamMap().put("propertyData", propertyData);
//		ctx.getParamMap().put("outCompany", outCompany);
//		ctx.getParamMap().put("datObject", datObject);
		logger.info("组装的参数"+ctx.getParamMap());
		return NEXT;
	}
}
