package cn.com.yitong.ares.deviceInfo.service;

import cn.com.yitong.ares.deviceInfo.vo.MdmPolicy;
import cn.com.yitong.ares.deviceInfo.vo.MdmPolicyItem;
import cn.com.yitong.ares.login.service.LoginService;
import cn.com.yitong.common.utils.JsonUtils;
import cn.com.yitong.core.util.SecurityUtils;
import cn.com.yitong.framework.service.ICrudService;
import cn.com.yitong.util.StringUtil;
import cn.com.yitong.util.YTLog;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.sql.Timestamp;
import java.util.*;

/**
 * @author zhanglong@yitong.com.cn
 * @date 15/9/18
 */
@Service
public class MdmPolicyItemService {

    private Logger logger = YTLog.getLogger(this.getClass());

    private static String LABEL_RELATION_OBJECR_DEVICE_TYPE = "2"; //设备
    private static String LABEL_RELATION_OBJECR_ORGAN_TYPE = "1"; //机构
    private static String LABEL_RELATION_OBJECR_USER_TYPE = "0"; //用户

    private static String LABEL_OPTION_TYPE_ORGAN = "1"; //机构
    private static String LABEL_OPTION_TYPE_USER = "2";  //账号
    private static String LABEL_OPTION_TYPE_DEVICE = "3"; //设备

    private static String POLICY_IS_DEFAULT_NO = "0"; //策略是否默认-否
    private static String POLICY_IS_DEFAULT_YES = "1"; //策略是否默认-是

    private static String POLICY_ITEM_TYPE_ONETOMANY = "4"; //策略项类型--一对多情况
    private static String POLICY_ITEM_TYPE_VIOLATE = "5";   //策略项类型--关联违规策略

    private static String POLICY_ITEM_CONTRO_TYPE = "1"; //关联应用列表

    private static String POLICY_ITEM_SYSTEM_IOS = "1";
    private static String POLICY_ITEM_SYSTEM_ANDROID = "2";
    private static String POLICY_ITEM_SYSTEM_ALL = "3";

    private static String POLICY_TYPE_SYSTEM = "2";     //策略类型--系统策略
    private static String POLICY_TYPE_DEVICE = "3";     //策略类型--设备策略

    @Autowired
    ICrudService service;

    @Autowired
    LoginService loginService;

    /**
     * 应用启动时，查询策略项
     * @return
     */
    public Map queryPolicyItemAppStart(String deviceUuid, String osSystem) {
        //查询终端设备系统和设备Id关联的标签
        List<MdmPolicy> policyList = queryPolicysByObjId(osSystem, deviceUuid, LABEL_RELATION_OBJECR_DEVICE_TYPE, LABEL_OPTION_TYPE_DEVICE, false);
        //计算查询的策略列表，按照优先级 获取系统和设备策略，不足是使用默认策略补充
        List<MdmPolicy> policys = fillUpAndHighPriPolicyByDefault(policyList);
        return queryItemValueByPolicys(policys, osSystem);
    }

    /**
     * 用户登录时，查询策略项
     * @return
     */
    public Map queryPolicyItemLogin(String deviceUuid, String osSystem, String userId) {
        //查询标签
        List<MdmPolicy> policyList = queryPolicyLogin(osSystem, deviceUuid, userId);
        return queryItemValueByPolicys(policyList, osSystem);
    }

    /**
     * 根据策略集合计算策略项的值 并构造返回参数
     * @param policyList 策略集合
     * @param osSystem 系统类型
     * @return
     */
    public Map queryItemValueByPolicys(List<MdmPolicy> policyList, String osSystem) {
        Map rst = new HashMap();
        //分别计算策略项
        List<MdmPolicyItem> policyItemList = analysisPolicyItem(policyList, osSystem);
        //填补特殊处理的策略项值
        specialPolicyItemValue(policyItemList, osSystem);
        //构造返回参数
        structPolicyItemReturnMap(policyItemList, rst);
        return rst;
    }

    /**
     * 根据操作系统类型、设备UUID、应用范围 查询关联的标签 再 关联出策略信息
     * @return
     */
    private List<MdmPolicy> queryPolicysByObjId(String osSystem, String objId, String objType, String optionType, boolean isOrgan) {
        List<MdmPolicy> policys = new ArrayList<MdmPolicy>();
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("osSystem", osSystem);
        param.put("objId", objId);
        param.put("objType", objType);
        param.put("optionType", optionType);
        List<Map<String, Object>> policyMapList = null;
        if(isOrgan) {
            policyMapList = service.findList("MDM_POLICY_ITEM.queryPolicyByOrganId", param);
        }else {
            policyMapList = service.findList("MDM_POLICY_ITEM.queryPolicyBySystemTypeAndObj", param);
        }

        if(null != policyMapList && policyMapList.size() > 0) {
            MdmPolicy policy = null;
            for(Map<String, Object> map : policyMapList) {
                policy = converToPolicy(map);
                if(null != policy && !policys.contains(policy)) {
                    policys.add(policy);
                }
            }
        }
        return policys;
    }

    /**
     * 若策略集合不满足至少一个系统策略 和 一个设备策略 使用默认策略补充 并计算优先级最高的两个策略
     * @param policyList 策略集合
     * @return
     */
    private List<MdmPolicy> fillUpAndHighPriPolicyByDefault(List<MdmPolicy> policyList) {
        if(null == policyList) {
            policyList = new ArrayList<MdmPolicy>();
        }
        MdmPolicy devicePolicy = getPolicyPriorityByType(policyList, POLICY_TYPE_DEVICE);
        MdmPolicy systemPolicy = getPolicyPriorityByType(policyList, POLICY_TYPE_SYSTEM);
        if(null == devicePolicy || null == systemPolicy) {
            //默认策略集合
            List<MdmPolicy> defaulPolicyList = defaultPolicy();
            if(null != defaulPolicyList && defaulPolicyList.size() > 0) {
                String type = "";
                for(MdmPolicy policy : defaulPolicyList) {
                    type = policy.getPolicyType();
                    if(StringUtil.isEmpty(type)) {
                        continue;
                    }
                    if(null == devicePolicy && POLICY_TYPE_DEVICE.equals(type)) {
                        devicePolicy = policy;
                    }
                    if(null == systemPolicy && POLICY_TYPE_SYSTEM.equals(type)) {
                        systemPolicy = policy;
                    }
                }
            }
        }
        List<MdmPolicy> policys = new ArrayList<MdmPolicy>();
        policys.add(devicePolicy);
        policys.add(systemPolicy);
        return policys;
    }

    /**
     * 用户登陆时 查询策略信息
     * @return
     */
    private List<MdmPolicy> queryPolicyLogin(String osSystem, String deviceUuid, String userId) {
        //获取设备关联策略
        List<MdmPolicy> deivicePolicyList = queryPolicysByObjId(osSystem, deviceUuid, LABEL_RELATION_OBJECR_DEVICE_TYPE, LABEL_OPTION_TYPE_DEVICE, false);
        MdmPolicy devicePolicy = getPolicyPriorityByType(deivicePolicyList, POLICY_TYPE_DEVICE);
        MdmPolicy systemPolicy = getPolicyPriorityByType(deivicePolicyList, POLICY_TYPE_SYSTEM);

        //没有配置设备关联策略 使用用户查询
        if(null == devicePolicy || null == systemPolicy) {
            List<MdmPolicy> userPolicyList = queryPolicysByObjId(osSystem, userId, LABEL_RELATION_OBJECR_USER_TYPE, LABEL_OPTION_TYPE_USER, false);
            if(null == devicePolicy) {
                devicePolicy = getPolicyPriorityByType(userPolicyList, POLICY_TYPE_DEVICE);
            }
            if(null == systemPolicy) {
                systemPolicy = getPolicyPriorityByType(userPolicyList, POLICY_TYPE_SYSTEM);
            }
        }

        //没有配置设备、用户关联策略 使用机构查询
        if(null == devicePolicy || null == systemPolicy) {
            String organId = getOrgByUser(userId);
            List<MdmPolicy> organPolicyList = queryPolicysByObjId(osSystem, organId, LABEL_RELATION_OBJECR_ORGAN_TYPE, LABEL_OPTION_TYPE_ORGAN, true);
            if(null == devicePolicy) {
                devicePolicy = getPolicyPriorityByType(organPolicyList, POLICY_TYPE_DEVICE);
            }
            if(null == systemPolicy) {
                systemPolicy = getPolicyPriorityByType(organPolicyList, POLICY_TYPE_SYSTEM);
            }
        }

        //没有配置设备、用户、机构关联策略 使用默认策略
        if(null == devicePolicy || null == systemPolicy) {
            //默认策略集合
            List<MdmPolicy> defaulPolicyList = defaultPolicy();
            if(null != defaulPolicyList && defaulPolicyList.size() > 0) {
                String type = "";
                for(MdmPolicy policy : defaulPolicyList) {
                    type = policy.getPolicyType();
                    if(StringUtil.isEmpty(type)) {
                        continue;
                    }
                    if(null == devicePolicy && POLICY_TYPE_DEVICE.equals(type)) {
                        devicePolicy = policy;
                    }
                    if(null == systemPolicy && POLICY_TYPE_SYSTEM.equals(type)) {
                        systemPolicy = policy;
                    }
                }
            }
        }

        List<MdmPolicy> policys = new ArrayList<MdmPolicy>();
        policys.add(devicePolicy);
        policys.add(systemPolicy);
        return policys;
    }

    /**
     * map 映射 VO
     * @param policyMap
     * @return
     */
    private MdmPolicy converToPolicy(Map<String, Object> policyMap) {
        MdmPolicy policy = new MdmPolicy();
        policy.setPolicyId(getValue(policyMap.get("POLICY_ID")));
        policy.setPolicyName(getValue(policyMap.get("NAME")));
        policy.setPolicyType(getValue(policyMap.get("POLICY_TYPE")));
        policy.setPriority(getValue(policyMap.get("PRIORITY")));
        policy.setIsDefault(getValue(policyMap.get("IS_DEFAULT")));
        policy.setStatus(getValue(policyMap.get("STATUS")));
        policy.setCreateTime((Timestamp)policyMap.get("CREATE_TIME"));
        return policy;
    }

    private String getValue(Object obj) {
        return null==obj?"":obj.toString();
    }

    /**
     * 查询默认策略列表
     */
    private List<MdmPolicy> defaultPolicy() {
        //查询默认的策略
        List<MdmPolicy> list = new ArrayList<MdmPolicy>();
        Map paramMap = new HashMap();
        paramMap.put("isDefault", POLICY_IS_DEFAULT_YES);
        List<Map<String, Object>> defaultPolicys = service.findList("MDM_POLICY_ITEM.queryDefaultPolicy", paramMap);
        if(null != defaultPolicys && defaultPolicys.size() > 0) {
            MdmPolicy policy = null;
            for(Map<String, Object> map : defaultPolicys) {
                policy = converToPolicy(map);
                if(null != policy && !list.contains(policy)) {
                    list.add(policy);
                }
            }
        }
        return list;
    }

    /**
     * 根据策略计算下发的策略项 集合中不能有相同类型的策略（目前实现没有去重，请先按照优先级处理后，再调用次方法）
     * @param policyList 策略集合
     * @return
     */
    private List<MdmPolicyItem> analysisPolicyItem(List<MdmPolicy> policyList, String osSystem) {
        List<MdmPolicyItem> policyItemList = new ArrayList<MdmPolicyItem>();
        if(null != policyList && policyList.size() > 0) {
            String policyId = null;
            List<MdmPolicyItem> tempPolicyItem = null;
            for(MdmPolicy policy : policyList) {
                policyId = policy.getPolicyId();
                if(StringUtil.isEmpty(policyId)) {
                    continue;
                }
                tempPolicyItem = getPolicyItemsByPolicyId(policy, osSystem);
                if(null != tempPolicyItem && tempPolicyItem.size() > 0) {
                    String itemCode = null;
                    for(MdmPolicyItem item : tempPolicyItem) {
                        itemCode = item.getCode();
                        if(StringUtil.isEmpty(itemCode)) {
                            continue;
                        }
                        if(!policyItemList.contains(item)) {
                            policyItemList.add(item);
                        }
                    }
                }
            }
        }
        return policyItemList;
    }

    /**
     * 根据策略查询策略项集合 过滤操作系统
     * @param policy
     * @return
     */
    private List<MdmPolicyItem> getPolicyItemsByPolicyId(MdmPolicy policy, String osSystem) {
        List<MdmPolicyItem> policyItemList = new ArrayList<MdmPolicyItem>();
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("policyId", policy.getPolicyId());
        List<String> osSystemList = new ArrayList<String>();
        osSystemList.add(osSystem);
        osSystemList.add(POLICY_ITEM_SYSTEM_ALL);
        paramMap.put("osSystemList", osSystemList);
        List<Map<String, Object>> policyItems = service.findList("MDM_POLICY_ITEM.queryPolicyItemByPolicyId", paramMap);
        if(null != policyItems && policyItems.size() > 0) {
            MdmPolicyItem policyItem = null;
            for(Map<String, Object> item : policyItems) {
                policyItem = converToPolicyItem(item, policy);
                policyItemList.add(policyItem);
            }
        }
        return policyItemList;
    }

    /**
     * 构造策略项
     * @param item 策略项map
     * @param policy 关联策略
     * @return
     */
    private MdmPolicyItem converToPolicyItem(Map<String, Object> item, MdmPolicy policy) {
        MdmPolicyItem policyItem = new MdmPolicyItem();
        policyItem.setPolicyType(getValue(item.get("POLICY_TYPE")));
        policyItem.setPolicyId(getValue(item.get("POLICY_ID")));
        policyItem.setCode(getValue(item.get("CODE")));
        policyItem.setName(getValue(item.get("NAME")));
        policyItem.setPolicyItemId(getValue(item.get("POLICY_ITEM_ID")));
        policyItem.setPolicyItemType(getValue(item.get("POLICY_ITEM_TYPE")));
        policyItem.setPolicyPriority(policy.getPriority());
        policyItem.setPolicyItemValue(getValue(item.get("POLICY_ITEM_VALUE")));
        policyItem.setItemSystem(getValue(item.get("ITEM_SYSTEM")));
        return policyItem;
    }

    /**
     * 策略项的值 关联其他实体 补充策略项的值
     * @param policyItemList 策略项集合
     * @return
     */
    private List<MdmPolicyItem> specialPolicyItemValue(List<MdmPolicyItem> policyItemList, String osSystem) {
        List<MdmPolicyItem> itemList = new ArrayList<MdmPolicyItem>();
        String policyItemType = "";
        String policyItemId = "";
        String policyItemValue = "";
        for(MdmPolicyItem item : policyItemList) {
            policyItemId = item.getPolicyItemId();
            policyItemType = item.getPolicyItemType();
            if(StringUtil.isEmpty(policyItemId) || StringUtil.isEmpty(policyItemType)) {
                continue;
            }
            policyItemValue = item.getPolicyItemValue();
            if(POLICY_ITEM_TYPE_ONETOMANY.equals(policyItemType)) {
                Map value = new HashMap();
                value.put("policyItemId", policyItemId);
                List<Map<String, Object>> valueList = service.findList("MDM_POLICY_ITEM.queryPolicyItemValueByPolicyItemId", value);
                List<String> objIdList = new ArrayList<String>();
                if(null != valueList && valueList.size() > 0) {
                    String itemValue = null;
                    String itemType = null;
                    for(Map<String, Object> map : valueList) {
                        itemValue = (String)map.get("POLICY_ITEM_VALUE");
                        itemType = (String)map.get("CONTRO_TYPE");
                        if(StringUtil.isNotEmpty(itemValue) && !objIdList.contains(itemValue)) {
                            objIdList.add(itemValue);
                        }
                    }
                    if(StringUtil.isNotEmpty(itemType) && objIdList.size() > 0) {
                        //当前策略项-控制类型为 关联应用
                        if(POLICY_ITEM_CONTRO_TYPE.equals(itemType)) {
                            item.setPolicyItemValue(StringUtils.collectionToDelimitedString(objIdList, ","));
                        }
                    }
                }
            }else if(POLICY_ITEM_TYPE_VIOLATE.equals(policyItemType)){
                if(StringUtil.isNotEmpty(policyItemValue)) {
                    MdmPolicy violatePolicy = getPolicyByPolicyId(policyItemValue);
                    if(null != violatePolicy) {
                        List<MdmPolicyItem> items = getPolicyItemsByPolicyId(violatePolicy, osSystem);
                        Map violateMap = new HashMap();
                        structPolicyItemReturnMap(items, violateMap);
                        item.setPolicyItemValue(JsonUtils.objectToJson(violateMap));
                    }
                }
            }
            itemList.add(item);
        }
        return itemList;
    }

    /**
     * 构造返回Map信息
     * @param policyItemList 策略项集合
     * @param rst 返回Map
     */
    private void structPolicyItemReturnMap(List<MdmPolicyItem> policyItemList, Map rst) {
        if(null != policyItemList && policyItemList.size() > 0 && null != rst) {
            for(MdmPolicyItem item : policyItemList) {
                rst.put(item.getCode(), item.getPolicyItemValue());
            }
        }
    }

    /**
     * 根据用户查询机构
     * @param userId
     * @return
     */
    private String getOrgByUser(String userId) {
        Map<String, Object> userInfo = loginService.loadUserById(userId);
        return getValue(userInfo.get("ORG_ID"));
    }

    /**
     * 根据策略项Id 获取策略信息
     * @param policyId 策略项Id
     * @return
     */
    private MdmPolicy getPolicyByPolicyId(String policyId) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("policyId", policyId);
        Map policyMap = service.load("MDM_POLICY_ITEM.queryPolicyByPolicyId", paramMap);
        return converToPolicy(policyMap);
    }

    /**
     * 保存策略执行日志信息
     * @param rst
     * @return
     */
    public Boolean violatePolicyLog(Map<String, Object> rst) {
        rst.put("LOG_ID", SecurityUtils.genSecurityKey());
        rst.put("CREATE_DTIME", new Date());
        return service.insert("MDM_POLICY_ITEM.insertViolatePolicyLog", rst);
    }

    /**
     * 从当前策略集合中 分离出设备策略和系统策略 并获取优先级最高的策略
     * @param policyList 策略集合
     */
    private MdmPolicy getPolicyPriorityByType(List<MdmPolicy> policyList, String policyType) {
        if(null == policyList || policyList.isEmpty()) {
            return null;
        }
        List<MdmPolicy> policyTypeList = new ArrayList<MdmPolicy>();
        String tempType = "";
        for(MdmPolicy policy : policyList) {
            tempType = policy.getPolicyType();
            if(StringUtil.isEmpty(tempType)) {
                continue;
            }
            if(policyType.equals(tempType)) {
                policyTypeList.add(policy);
            }
        }
        return getPolicyByPriority(policyTypeList);
    }

    /**
     * 根据优先级获取最高的策略 若优先级相同 取时间最新的
     * @param policyList 待选策略集合
     * @return
     */
    private MdmPolicy getPolicyByPriority(List<MdmPolicy> policyList) {
        MdmPolicy policy = null;
        if(null != policyList && policyList.size() > 0) {
            String priority;
            String highPriority = "";
            for(MdmPolicy tempPolicy : policyList) {
                if(null == policy || StringUtil.isEmpty(highPriority)) {
                    highPriority = tempPolicy.getPriority();
                    if(StringUtil.isEmpty(highPriority)) {
                        continue;
                    }
                    policy = tempPolicy;
                    continue;
                }
                priority = tempPolicy.getPriority();
                if(StringUtil.isEmpty(priority)) {
                    continue;
                }
                Long lprity = Long.valueOf(priority);
                Long hprity = Long.valueOf(highPriority);
                if(lprity < hprity) {
                    policy = tempPolicy;
                    highPriority = priority;
                }else if(lprity == hprity) {
                    Timestamp highTime = policy.getCreateTime();
                    Timestamp tempTime = tempPolicy.getCreateTime();
                    if(tempTime.after(highTime)) {
                        policy = tempPolicy;
                        highPriority = priority;
                    }
                }
            }
        }
        return policy;
    }
}
