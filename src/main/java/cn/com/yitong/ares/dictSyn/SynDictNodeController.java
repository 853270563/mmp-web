package cn.com.yitong.ares.dictSyn;

import cn.com.yitong.common.utils.WebUtils;
import cn.com.yitong.core.cache.CacheNames;
import cn.com.yitong.core.util.DictUtils;
import cn.com.yitong.util.YTLog;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zhanglong@yitong.com.cn
 * @date 15/12/10
 */
@Controller
@RequestMapping("/ares/synDict/")
public class SynDictNodeController {

    private Logger logger = YTLog.getLogger(this.getClass());

    @RequestMapping("cleanSynDict")
    @ResponseBody
    public Map cleanSynDict(HttpServletRequest request) {
        Map<String, Object> map = new HashMap<String, Object>();
        if(logger.isInfoEnabled()) {
            logger.info("字典缓存同步====接收到请求");
        }
        DictUtils.cleanDictByCacheName(CacheNames.DICT_CACHE_NAME);
        return WebUtils.returnSuccessMsg(map, null);
    }
}
