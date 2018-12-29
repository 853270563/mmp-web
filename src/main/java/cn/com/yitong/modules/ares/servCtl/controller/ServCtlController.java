package cn.com.yitong.modules.ares.servCtl.controller;

import cn.com.yitong.core.base.WebUtils;
import cn.com.yitong.framework.core.bean.BaseControl;
import cn.com.yitong.modules.ares.servCtl.service.ServCtlService;
import cn.com.yitong.tools.vo.SimpleResult;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @author lc3@yitong.com.cn
 */
@Controller
@RequestMapping("/servCtl/")
public class ServCtlController extends BaseControl {

    @Resource
    private ServCtlService servCtlService;

    @RequestMapping("eventServDemo")
    @ResponseBody
    public Map<String, Object> eventServDemo() {
        SimpleResult result = servCtlService.startEventId("000001");
        if(!result.isSeccess()) {
            return WebUtils.returnErrorMsg(null, result.getMsg());
        }
        try {
            Thread.sleep(12 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        servCtlService.stopEventId("000001");
        return WebUtils.returnSuccessMsg(null, "成功访问事件接口");
    }
}
