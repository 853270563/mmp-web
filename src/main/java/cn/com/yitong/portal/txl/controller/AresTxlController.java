package cn.com.yitong.portal.txl.controller;

import cn.com.yitong.common.utils.StringUtils;
import cn.com.yitong.framework.base.IBusinessContext;
import cn.com.yitong.framework.core.bean.BaseControl;
import cn.com.yitong.framework.core.bean.BusinessContext;
import cn.com.yitong.framework.net.IClientFactory;
import cn.com.yitong.framework.net.IEBankConfParser;
import cn.com.yitong.framework.net.IRequstBuilder;
import cn.com.yitong.framework.net.IResponseParser;
import cn.com.yitong.framework.service.ICrudService;
import cn.com.yitong.framework.util.CtxUtil;
import cn.com.yitong.util.YTLog;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author 孙伟(sunw@yitong.com.cn)
 */
@Controller
public class AresTxlController extends BaseControl {

    private Logger logger = YTLog.getLogger(this.getClass());
    @Autowired
    ICrudService service;
    @Autowired
    @Qualifier("requestBuilder4db")
    IRequstBuilder requestBuilder;// 请求报文生成器
    @Autowired
    @Qualifier("responseParser4db")
    IResponseParser responseParser;// 响应报文解析器
    @Autowired
    @Qualifier("urlClient4db")
    IClientFactory client;// 响应报文解析器
    @Autowired
    @Qualifier("EBankConfParser4db")
    IEBankConfParser confParser;// 报文装载器
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");

    final String BASE_PATH = "portal/txl/";

    
    /**
     * 同步通讯录
     * @param request
     * @return
     */
    @SuppressWarnings({ "rawtypes", "unchecked"})
	@RequestMapping("portal/txl/allTxlList.do")
    @ResponseBody
    public Map allTxlList(HttpServletRequest request) {
        String trans_code = "allTxlList";
        String transCode = BASE_PATH + trans_code;
        Map rst = new HashMap();
        // 初始化数据总线
        IBusinessContext ctx = new BusinessContext(request,
                IBusinessContext.PARAM_TYPE_MAP);
        // 检查报文定义
        if (!transPrev(ctx, transCode, rst)) {
            return rst;
        }
        if (CtxUtil.debugTrans(trans_code)) {
            boolean ok = client.execute(ctx, transCode);
            transAfter(ctx, transCode, rst, ok);
            return rst;
        }
        // 数据库操作区
        Map params = ctx.getParamMap();
        boolean ok = false;
        try {
        	HttpSession session = request.getSession();
        	if (session.getAttribute("LGN_ID") == null) {
        		//TODO 测试用
            	params.put("loginName", "sw");
        	} else {
            	params.put("loginName", (String)session.getAttribute("LGN_ID"));
        	}
        	String synchroTime = (String)params.get("synchroTime");
        	if (StringUtils.isBlank(synchroTime)) {
        		synchroTime = sdf.format(new Date());
        		// 机构列表
        		List<Map<String, Object>> orgNewList = service.findList("ARES_TXL_LIST.queryAllOrgnList", null);
        		// 职员列表
        		List<Map<String, Object>> empNewList = service.findList("ARES_TXL_LIST.queryAllEmployeeList", null);
        		// 职员所属群组列表
        		List<Map<String, Object>> grpNewList = service.findList("ARES_TXL_LIST.queryGroupListByLoginName", params);
        		List<Map<String, Object>> grpList = service.findList("ARES_TXL_LIST.queryAllEmptyGroup", params);
        		
        		// 下面两个for循环，将空的Group添加到结果列表中，
        		// 因为queryGroupListByLoginName只能检索出不为空的group
        		Set<String> grpNewSet = new HashSet<String>();
        		for (Map<String, Object> map : grpNewList) {
        			grpNewSet.add((String)map.get("GROUP_ID"));
        		}
        		for (Map<String, Object> map : grpList) {
        			if (!grpNewSet.contains(map.get("GROUP_ID"))) {
        				grpNewList.add(map);
        			}
        		}
        		
        		grpNewList = this.formatGrpList(grpNewList);
        		rst.put("orgNewList", orgNewList);
        		rst.put("empNewList", empNewList);
        		rst.put("grpNewList", grpNewList);
        		rst.put("updateTime", synchroTime);
        	} else {
            	Date date = sdf.parse(synchroTime);
            	params.put("synchroTime", date);
            	// 机构列表
        		List<Map<String, Object>> iudOrgnList = service.findList("ARES_TXL_LIST.queryIudOrgnList", params);
        		if (iudOrgnList != null && !iudOrgnList.isEmpty()) {
        			List<Map<String, Object>> orgNewList = new ArrayList<Map<String, Object>>();
        			List<Map<String, Object>> orgUpdateList = new ArrayList<Map<String, Object>>();
        			List<Map<String, Object>> orgDeleteList = new ArrayList<Map<String, Object>>();
        			for (Map<String, Object> map : iudOrgnList) {
        				String iudFlag = (String)map.get("IUD_FLAG");
        				if ("I".equals(iudFlag)) {
        					orgNewList.add(map);
        				} else if ("U".equals(iudFlag)) {
        					orgUpdateList.add(map);
        				} else {
        					orgDeleteList.add(map);
        				}
        			}
        			rst.put("orgNewList", orgNewList);
        			rst.put("orgUpdateList", orgUpdateList);
        			rst.put("orgDeleteList", orgDeleteList);
        		}
        		// 职员列表
        		List<Map<String, Object>> iudEmployeeList = service.findList("ARES_TXL_LIST.queryIudEmployeeList", params);
        		if (iudEmployeeList != null && !iudEmployeeList.isEmpty()) {
        			List<Map<String, Object>> empNewList = new ArrayList<Map<String, Object>>();
        			List<Map<String, Object>> empUpdateList = new ArrayList<Map<String, Object>>();
        			List<Map<String, Object>> empDeleteList = new ArrayList<Map<String, Object>>();
        			for (Map<String, Object> map : iudEmployeeList) {
        				String iudFlag = (String)map.get("IUD_FLAG");
        				if ("I".equals(iudFlag)) {
        					empNewList.add(map);
        				} else if ("U".equals(iudFlag)) {
        					empUpdateList.add(map);
        				} else {
        					empDeleteList.add(map);
        				}
        			}
        			rst.put("empNewList", empNewList);
        			rst.put("empUpdateList", empUpdateList);
        			rst.put("empDeleteList", empDeleteList);
        		}
        		// 职员所属群组列表
        		List<Map<String, Object>> iudGroupListByLoginName = service.findList("ARES_TXL_LIST.queryIudGroupListByLoginName", params);
        		List<Map<String, Object>> iudEmptyGroup = service.findList("ARES_TXL_LIST.queryIudEmptyGroup", params);
        		
        		// 下面两个for循环，将空的Group添加到结果列表中，
        		// 因为queryIudGroupListByLoginName只能检索出不为空的group
        		Set<String> grpNewSet = new HashSet<String>();
        		for (Map<String, Object> map : iudGroupListByLoginName) {
        			grpNewSet.add((String)map.get("GROUP_ID"));
        		}
        		for (Map<String, Object> map : iudEmptyGroup) {
        			if (!grpNewSet.contains(map.get("GROUP_ID"))) {
        				iudGroupListByLoginName.add(map);
        			}
        		}
        		
        		if (iudGroupListByLoginName != null && !iudGroupListByLoginName.isEmpty()) {
        			List<Map<String, Object>> grpNewList = new ArrayList<Map<String, Object>>();
        			List<Map<String, Object>> grpUpdateList = new ArrayList<Map<String, Object>>();
        			for (Map<String, Object> map : iudGroupListByLoginName) {
        				String iudFlag = (String)map.get("IUD_FLAG");
        				if ("I".equals(iudFlag)) {
        					grpNewList.add(map);
        				} else {
        					grpUpdateList.add(map);
        				}
        			}
        			grpNewList = this.formatGrpList(grpNewList);
        			grpUpdateList = this.formatGrpList(grpUpdateList);
        			rst.put("grpNewList", grpNewList);
        			rst.put("grpUpdateList", grpUpdateList);
        		}
            	List<Map<String, Object>> grpDeleteList = service.findList("ARES_TXL_LIST.queryDelGroupListByLoginName", params);
    			rst.put("grpDeleteList", grpDeleteList);
        		rst.put("updateTime", synchroTime);
        	}
        	
        	ok = true;
        } catch (Exception e) {
        	logger.error(e.getMessage());
        }
        transAfter(ctx, transCode, rst, ok);
        return rst;
    }
    
    /**
     * 群组列表格式化
     * @param list
     */
	@SuppressWarnings("unchecked")
	private List<Map<String, Object>> formatGrpList(List<Map<String, Object>> list) {
		List<Map<String, Object>> newList = new ArrayList<Map<String, Object>>();;
		if (list == null || list.isEmpty()) {
			return newList;
		}
		Set<String> grpIdSet = new HashSet<String>();
		Map<String, Map<String, Object>> grpMap = new HashMap<String, Map<String, Object>>();
		// 对查询结果进行循环
		for (Map<String, Object> map : list) {
			String groupId = (String)map.get("GROUP_ID");
			Object empID = map.get("EMP_ID");
			// 群组联系人为空的情况下
			if (empID == null) {
				grpMap.put(groupId, map);
				grpIdSet.add(groupId);
				continue;
			}
			Map<String, Object> empIdMap = new HashMap<String, Object>();
			empIdMap.put("EMP_ID", empID);
			// 新Group，Set中不包含GroupID，讲该条记录放到grpMap中
			if (!grpIdSet.contains(groupId)) {
				List<Map<String, Object>> empList = new ArrayList<Map<String, Object>>();
				empList.add(empIdMap);
				map.put("empList", empList);
				map.remove("EMP_ID");
				grpMap.put(groupId, map);
				grpIdSet.add(groupId);
			// 旧Group，Set中不包含GroupID，讲该条记录放到grpMap中
			} else {
				Map<String, Object> map2 = grpMap.get(groupId);
				List<Map<String, Object>> empList = (List<Map<String, Object>>)map2.get("empList");
				empList.add(empIdMap);
			}
		}
		if (!grpIdSet.isEmpty()) {
			for (String groupId : grpIdSet) {
				newList.add(grpMap.get(groupId));
			}
		}
		return newList;
	}
	

    /**
     * 个人群组修改同步接口
     * @param request
     * @return
     */
    @SuppressWarnings({ "rawtypes", "unchecked"})
	@RequestMapping("portal/txl/groupUpdate.do")
    @ResponseBody
    public Map groupUpdate(HttpServletRequest request) {
        String trans_code = "groupUpdate";
        String transCode = BASE_PATH + trans_code;
        Map rst = new HashMap();
        // 初始化数据总线
        IBusinessContext ctx = new BusinessContext(request,
                IBusinessContext.PARAM_TYPE_MAP);
        // 检查报文定义
        if (!transPrev(ctx, transCode, rst)) {
            return rst;
        }
        if (CtxUtil.debugTrans(trans_code)) {
            boolean ok = client.execute(ctx, transCode);
            transAfter(ctx, transCode, rst, ok);
            return rst;
        }
        // 数据库操作区
        Map params = ctx.getParamMap();
        boolean ok = false;
        try {
        	String loginName = null;
        	HttpSession session = request.getSession();
        	if (session.getAttribute("LGN_ID") == null) {
        		//TODO 测试用
        		loginName = "sw";
        	} else {
        		loginName = (String) session.getAttribute("LGN_ID");
        	}
        	List<Map> groupList = (List<Map>) params.get("groupList");
        	Date date = new Date();
        	for (Map groupMap : groupList) {
        		Map paramCheck = this.paramCheck(groupMap, false);
        		if (!paramCheck.isEmpty()) {
        			return paramCheck;
        		}
        		groupMap.put("UPDATE_BY", loginName);
        		groupMap.put("UPDATE_DATE", date);
        		boolean updateReuslt = service.update("ARES_TXL_LIST.updateGroup", groupMap);
        		// 更新成功，删除群组成员
        		if (updateReuslt) {
        			// 更新群组
        			if ("0".equals(groupMap.get("DEL_FLAG"))) {
        				// 删除群组成员
            			if ("1".equals(groupMap.get("EMP_DEL_FLAG"))) {
            				String GROUP_EMPLOYEES = (String) groupMap.get("GROUP_EMPLOYEES");
                    		if (StringUtils.isBlank(GROUP_EMPLOYEES)) {
                    			continue;
                    		}
            				String[] emploueeIdses = GROUP_EMPLOYEES.split(",");
            				for (String employeeId : emploueeIdses) {
            					if (StringUtils.isNotBlank(employeeId)) {
            						groupMap.put("EMP_ID", employeeId);
            						service.insert("ARES_TXL_LIST.deleteGroupEmployees", groupMap);
            					}
            				}
            			}
        				// 删除群组
        			} else {
        				service.delete("ARES_TXL_LIST.deleteGroupEmployees", groupMap);
        			}
        		} else if ("0".equals(groupMap.get("DEL_FLAG"))){
        			paramCheck = this.paramCheck(groupMap, true);
            		if (!paramCheck.isEmpty()) {
            			return paramCheck;
            		}
        			// 更新未成功，插入新的群组
            		groupMap.put("CREATE_BY", loginName);
            		groupMap.put("CREATE_DATE", date);
            		groupMap.put("OWNER_BY", loginName);
            		service.insert("ARES_TXL_LIST.insertGroup", groupMap);
        		}
        		// 新建或更新群组，插入群组成员
        		if ("0".equals(groupMap.get("DEL_FLAG")) && !"1".equals(groupMap.get("EMP_DEL_FLAG"))){
            		String GROUP_EMPLOYEES = (String) groupMap.get("GROUP_EMPLOYEES");
            		if (StringUtils.isBlank(GROUP_EMPLOYEES)) {
            			continue;
            		}
    				String[] emploueeIdses = GROUP_EMPLOYEES.split(",");
    				for (String employeeId : emploueeIdses) {
    					if (StringUtils.isNotBlank(employeeId)) {
    						groupMap.put("EMP_ID", employeeId);
    						service.insert("ARES_TXL_LIST.insertGroupEmployees", groupMap);
    					}
    				}
        		}
        	}
        	ok = true;
        } catch (Exception e) {
        	logger.error(e.getMessage());
        }
        transAfter(ctx, transCode, rst, ok);
        return rst;
    }
    
    /**
     * 群组参数check
     * @param map
     * @return
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
	private Map paramCheck(Map map, boolean insertFlag){
    	Map result = new HashMap();
    	if (StringUtils.isBlank((String)map.get("GROUP_ID"))) {
    		result.put("MSG", "群组编号不能为空！");
    		result.put("STATUS", "0");
        	return result;
    	}
    	if (!insertFlag) {
        	return result;
    	}
    	if (StringUtils.isBlank((String)map.get("GROUP_NAME"))) {
    		result.put("MSG", "新建群组，名称不能为空！群组编号：" +(String)map.get("GROUP_ID"));
    		result.put("STATUS", "0");
        	return result;
    	}
    	if (StringUtils.isBlank((String)map.get("GROUP_TYPE"))) {
    		result.put("MSG", "新建群组，类型不能为空！群组编号：" +(String)map.get("GROUP_ID"));
    		result.put("STATUS", "0");
        	return result;
    	}
    	return result;
    }

    /**
     * 事务前置处理
     *
     * @param ctx
     * @param transCode
     * @param rst
     * @return
     */
    @SuppressWarnings("rawtypes")
    private boolean transPrev(IBusinessContext ctx, String transCode, Map rst) {
        // 交易开始，设置交易流水
        commonService.generyTransLogSeq(ctx, transCode);
        return CtxUtil.transPrev(ctx, transCode, json2MapParamCover,
                requestBuilder, confParser, rst);
    }
    
    /**
     * 事务之后处理
     *
     * @param ctx
     * @param transCode
     * @param rst
     * @param ok
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
	private void transAfter(IBusinessContext ctx, String transCode, Map rst,
                            boolean ok) {
        // 生成交易结果
        CtxUtil.transAfter(ctx, transCode, rst, ok, responseParser, confParser);
        // 保存交易日志
        commonService.saveJsonTransLog(ctx, transCode, rst);
    }
}