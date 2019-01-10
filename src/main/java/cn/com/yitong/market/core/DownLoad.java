package cn.com.yitong.market.core;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import cn.com.yitong.consts.SessConsts;
import cn.com.yitong.util.StringUtil;
import cn.com.yitong.util.YTLog;

/**
 * 文件下载
 * 
 * @author yaoym
 * 
 */
@Controller
public class DownLoad {
	private Logger logger = YTLog.getLogger(this.getClass());

	private final String CSV = "csv";

	/**
	 * 文本文件下载
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping("/common/download/text.do")
	public String downText(HttpServletRequest request, String type,
			String content) {
		logger.debug("---DownLoad---");
		if (!checkTimeout(request)) {
			return "error/downloadError";
		}
		if (CSV.equals(type)) {
			if(StringUtil.isNotEmpty(content)){
				content = content.replaceAll("\t", ",");
				content = content.replaceAll("(,)+", ",");  
			}
			request.setAttribute("content", content);
			return "common/download_csv";
		}
		return "common/download_text";
	}

	private final long WAIT_TIME = 10000;// 10秒

	/**
	 * 检查下载操作的间隔时间，小于最小时间间隔，不做处理，正常则更新下载最新时间
	 * 
	 * @param request
	 * @return
	 */
	private boolean checkTimeout(HttpServletRequest request) {
		HttpSession session = request.getSession();
		Date time = (Date) session.getAttribute(SessConsts.TIME_DOWNLOAD);
		if (null == time) {
			// 更新下载最新时间
			session.setAttribute(SessConsts.TIME_DOWNLOAD, new Date());
			return true;
		}
		long lstime = time.getTime();
		Date curDate = new Date();
		long curtime = curDate.getTime();
		if (curtime < (lstime + WAIT_TIME)) {
			return false;
		}
		// 更新下载最新时间
		session.setAttribute(SessConsts.TIME_DOWNLOAD, new Date());
		return true;
	}
}
