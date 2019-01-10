package cn.com.yitong.ares.deviceInfo.service;

import cn.com.yitong.consts.NS;
import cn.com.yitong.framework.service.ICrudService;
import cn.com.yitong.util.YTLog;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by winkie on 15/6/24.
 */
@Service
public class DeviceAppService {

    private Logger logger = YTLog.getLogger(this.getClass());

    private static String DEVICE_APP_SYSTEM_IOS = "1";
    private static String DEVICE_APP_SYSTEM_ANDROID = "2";
    private static String DEVICE_APP_SYSTEM_ALL = "3";

    @Autowired
    ICrudService service;

    /**
     * 查询设备上 黑名单-白名单 列表
     * @param appType 入参
     * @return
     */
    public Map queryBlackWhiteApps(String appType, String appSystem) {
        Map paramMap = new HashMap();
        List<String> systems = new ArrayList<String>();
        if(!DEVICE_APP_SYSTEM_ALL.equals(appSystem)) {
            systems.add(appSystem.trim());
            systems.add(DEVICE_APP_SYSTEM_ALL);
        }else {
            systems.add(DEVICE_APP_SYSTEM_IOS);
            systems.add(DEVICE_APP_SYSTEM_ANDROID);
            systems.add(DEVICE_APP_SYSTEM_ALL);
        }
        paramMap.put("appSystem", systems);

        List<Map> blackApps = null;
        List<Map> whiteApps = null;
        if(NS.DEVICE_APP_TYPE_ALL.equals(appType)) {
            //查询黑名单
            paramMap.put("appType", NS.DEVICE_APP_TYPE_BLACK);
            blackApps = service.findList("ARES_DEVICE_APP.queryByAppType", paramMap);
            //查询白名单
            paramMap.put("appType", NS.DEVICE_APP_TYPE_WHITE);
            whiteApps = service.findList("ARES_DEVICE_APP.queryByAppType", paramMap);
        }else if(NS.DEVICE_APP_TYPE_BLACK.equals(appType)){
            paramMap.put("appType", NS.DEVICE_APP_TYPE_BLACK);
            blackApps = service.findList("ARES_DEVICE_APP.queryByAppType", paramMap);
        }else if(NS.DEVICE_APP_TYPE_WHITE.equals(appType)) {
            paramMap.put("appType", NS.DEVICE_APP_TYPE_WHITE);
            whiteApps = service.findList("ARES_DEVICE_APP.queryByAppType", paramMap);
        }
        Map returnMap = new HashMap();
        //处理黑名单
        if(null != blackApps && blackApps.size() > 0) {
            returnMap.put("black", blackApps);
        }
        //处理白名单
        if(null != whiteApps && whiteApps.size() > 0) {
            returnMap.put("white", whiteApps);
        }
        return returnMap;
    }

}
