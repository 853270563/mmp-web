package cn.com.yitong.framework.net;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Map;

/**
 * jsonp服务对应的filter
 * @author lc3@yitong.com.cn
 */
public class JsonpCallbackFilter implements Filter {

    public static class MultiOutputHttpServletResponse extends HttpServletResponseWrapper {

        public MultiOutputHttpServletResponse(HttpServletResponse response) {
            super(response);
        }

        @Override
        public PrintWriter getWriter() throws IOException {
            return new PrintWriter(getOutputStream());
        }
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = new MultiOutputHttpServletResponse((HttpServletResponse) response);

        Map<String, String[]> parms = httpRequest.getParameterMap();

        String callback = null;
        if(parms.containsKey("callback") && null != (callback = parms.get("callback")[0])) {
            OutputStream out = httpResponse.getOutputStream();
            out.write(new String(callback + "(").getBytes());
            chain.doFilter(request, httpResponse);
            out.write(new String(");").getBytes());
            httpResponse.setContentType("text/javascript;charset=UTF-8");
            httpResponse.setHeader("Access-Control-Allow-Origin", "*");
        } else {
            chain.doFilter(request, response);
        }
    }

	@Override
	public void destroy() {
		
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

}
