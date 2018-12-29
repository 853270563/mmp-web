package cn.com.yitong.modules.system.rate.service;

import cn.com.yitong.consts.AppConstants;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zhanglong
 * @date 17/9/25
 */
@Service
public class RateService {

    public Map<String, Object> queryRateInfo(Map<String, Object> params) {
        Map<String, Object> rst = new HashMap<String, Object>();
        rst.put(AppConstants.STATUS, AppConstants.STATUS_OK);
        // 存款利率 贷款利率 分别通过接口从外系统查询获取
        return rst;
    }
}
