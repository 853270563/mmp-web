package cn.com.yitong.portal.thirdApp.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.UrlPathHelper;

import cn.com.yitong.portal.thirdApp.utils.CasSessionUtils;
import cn.com.yitong.common.utils.StringUtils;
import cn.com.yitong.common.utils.WebUtils;
import cn.com.yitong.core.session.Session;

/**
 * 统一会话拦截器
 * @author lc3@yitong.com.cn
 */
public class CasFilter extends OncePerRequestFilter {

    /**
     * 开放给第三方应用接入的地址，用逗号或分号分隔
     */
    private String openUrls;
    private List<String> openUrlList;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();
    private final UrlPathHelper urlPathHelper = new UrlPathHelper();

    public String getOpenUrls() {
        return openUrls;
    }

    public void setOpenUrls(String openUrls) {
        this.openUrls = openUrls;
        openUrlList = null;
        if(null != openUrls) {
            String[] split = openUrls.split("[,;\n]");
            for (String s : split) {
                if(StringUtils.isNotBlank(s)) {
                    if(null == openUrlList) {
                        openUrlList = new ArrayList<String>();
                    }
                    openUrlList.add(s);
                }
            }
        }
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 生成第三方会话
        Session session = null;
        try {
            session = CasSessionUtils.buildSessionForRequest(request);
        } catch (IllegalArgumentException e) {
            onLoginFailure(request, response, e.getMessage(), "005");
            return;
        }

        if(!CasSessionUtils.isThirdSession(session)) {
            filterChain.doFilter(request, response);
            return;
        }

        String url = urlPathHelper.getPathWithinApplication(request);
        for (String path : openUrlList) {
            if(pathMatcher.match(path, url)) {
                filterChain.doFilter(request, response);
                return;
            }
        }
        onLoginFailure(request, response, "您无权访问此接口：" + url, null);
    }

    protected boolean onLoginFailure(HttpServletRequest request, HttpServletResponse response, String errorMsg, String errCode) {
        Map<String, Object> rs = WebUtils.returnErrorMsg(null, errorMsg);
        if(null != errCode) {
            rs.put("STATUS", errCode);
        }
        WebUtils.jsonResponse(response, rs);
        return true;
    }
}
