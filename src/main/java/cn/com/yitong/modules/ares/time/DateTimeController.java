package cn.com.yitong.modules.ares.time;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.com.yitong.framework.core.bean.BaseControl;
import cn.com.yitong.util.DateUtil;
import cn.com.yitong.util.YTLog;

@Controller
public class DateTimeController extends BaseControl {

	private Logger logger = YTLog.getLogger(this.getClass());
	/**
	 * 获取原生资源更新信息
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping("ares/core/time.do")
	@ResponseBody
	public Map time(HttpServletRequest request) {
		Map map = new HashMap<String, String>();
		map.put("DATE_TIME", DateUtil.format(new Date(), "yyyyMMddHHmmssSSS"));
		logger.info(map);
		return map;
	}
}