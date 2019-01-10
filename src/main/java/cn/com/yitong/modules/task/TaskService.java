package cn.com.yitong.modules.task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class TaskService {
	public static List buildRecMap(Map recMap){
		List<Map> cityObjList = (List) recMap.get("cityObjList");
		String clientName = (String) recMap.get("clientName");
		String clientPhone = (String) recMap.get("clientPhone");
		String projectName = (String) recMap.get("projectName");
		List list = new ArrayList();
		if(null != cityObjList && !cityObjList.isEmpty()){
			for(Map busiMap : cityObjList){
				busiMap.get("cityName");//城市名称
				List<Map> objectList = (List) busiMap.get("objectList");//委估对象列表
				List<Map> outCompany = (List) busiMap.get("outCompany");//评估机构列表
				if(null != objectList && !objectList.isEmpty()){
					for(Map objectMap : objectList){//遍历委估对象列表
						Map busiPram = new HashMap();//业务详情入库参数
						busiPram.put("CLIENT_NAME", clientName);
						busiPram.put("CLIENT_PHONE", clientPhone);
						busiPram.put("PRO_NAME", projectName);
						
						String objectId = (String) objectMap.get("objectId");//委估对象id
						String stateCode = (String) objectMap.get("statecode");//业务状态1
						String address = (String) objectMap.get("address");//委估对象全称
						busiPram.put("OBJECTID", objectId);
						busiPram.put("stateCode", stateCode);
						busiPram.put("OBJECT_NAME", address);
						for(Map outCompanyMap : outCompany){//遍历评估机构列表
							String pstateCode = (String) outCompanyMap.get("stateCode");//业务状态2
							busiPram.put("pstateCode", pstateCode);
							List<Map> objectPriceList = (List) outCompanyMap.get("objectPriceList");//委估对象价格列表
							for(Map objectPriceMap : objectPriceList){//遍历委估对象价格列表
								String pobjectId = (String) objectPriceMap.get("objectId");//委估对象id
								String unitPrice = (String) objectPriceMap.get("unitPrice");//委估对象id
								String buildingArea = (String) objectPriceMap.get("buildingArea");//委估对象id
								String totalPrice = String.format("%.4f", Double.parseDouble(unitPrice) * Double.parseDouble(buildingArea)/10000);
								if(pobjectId.equals(objectId)){
									busiPram.put("TOTAL_PRICE", totalPrice);
									busiPram.put("UNIT_PRICE", unitPrice);
									busiPram.put("OBJECT_AREA", buildingArea);
								}
							}
						}
						list.add(busiPram);
					}
				}
				
			}
			return list;
		}
		return null;
	}
}
