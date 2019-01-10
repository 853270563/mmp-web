package cn.com.yitong.core.web.filter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import cn.com.yitong.common.utils.ConfigUtils;
import cn.com.yitong.core.session.Session;
import cn.com.yitong.core.session.vo.HttpSessionWrapper;
import cn.com.yitong.core.util.ConfigName;
import cn.com.yitong.core.util.SecurityUtils;

/**
 * 可重用流的Request，
 * @author lc3@yitong.com.cn
 */
public class ReusableHttpServletRequest extends HttpServletRequestWrapper {

    protected Logger logger = LoggerFactory.getLogger(getClass());
    public static final String TOKEN_HEADER_KEY = "ARES_TOKEN";
    public static final String REQUEST_MESSAGE_ID = "requestHeadMsgId";
    private ReusableServletInputStream inputStream;
    /**
     * 空串代表无效SessionId
     */
    private String requestedSessionId = "";
    private HttpSession httpSession;
    private Charset characterEncoding;
    private String requestMessageId = "";

    /**
     * Constructs a request object wrapping the given request.
     *
     * @param request
     * @throws IllegalArgumentException if the request is null
     */
    public ReusableHttpServletRequest(HttpServletRequest request) {
        super(request);
        if(null != request.getCharacterEncoding()) {
            try {
                characterEncoding = Charset.forName(request.getCharacterEncoding());
            } catch (Exception e) {
                logger.warn("不能识别的编码类型:{}", request.getCharacterEncoding());
            }
        }
        if(null == characterEncoding) {
            characterEncoding = Charset.forName("UTF-8");
        }
    }

    private ReusableServletInputStream getReusableServletInputStream() throws IOException {
        if(null == inputStream) {
            inputStream = new ReusableServletInputStream(_getHttpServletRequest().getInputStream());
        }
        return inputStream;
    }

    private HttpServletRequest _getHttpServletRequest() {
        return (HttpServletRequest) super.getRequest();
    }

    /**
     * 得到系统的请求SessionId
     * @return
     */
    @Override
    public String getRequestedSessionId() {
        if(!"".equals(requestedSessionId)) {
            return requestedSessionId;
        }
        if(SecurityUtils.canCodec()) {
            if(SecurityUtils.isLogining()) {
                requestedSessionId = null;
            } else {    // 取ARES token，默认取请求头中，如果没有再进一步取报文体中的
                String tokenHeader = _getHttpServletRequest().getHeader(TOKEN_HEADER_KEY);
                if(StringUtils.hasText(tokenHeader)) {
                    requestedSessionId = tokenHeader;
                } else {
                    try {
						if ("1".equals(ConfigUtils.getValue("debug_model"))) {
							requestedSessionId = super.getSession().getId();
						} else {

							requestedSessionId = getReusableServletInputStream().readSubString(16, 16);
						}
                    } catch (IOException e) {
                        requestedSessionId = null;
                    }
                }
            }
        } else {
			requestedSessionId = super.getSession().getId();
        }
        return requestedSessionId;
    }

    /**
     * 得到原始的J2EE的请求SessionId
     * @return
     */
    public String getOriginalRequestedSessionId() {
        return super.getRequestedSessionId();
    }

    @Override
    public HttpSession getSession() {
        return getSession(true);
    }

    @Override
    public HttpSession getSession(boolean create) {
        if(null == httpSession) {
            Session session = create ? SecurityUtils.getSessionRequired() :
                    SecurityUtils.getSession();
            httpSession = null == session ? null : new HttpSessionWrapper(session);
        }
        return httpSession;
    }

    public HttpSession getOriginalSession() {
        return super.getSession();
    }

    public HttpSession getOriginalSession(boolean create) {
        return super.getSession(create);
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        if(_getHttpServletRequest() instanceof MultipartHttpServletRequest) {
            return super.getInputStream();
        }
        return getReusableServletInputStream();
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(getReusableServletInputStream(), characterEncoding));
    }

    /**
     * 当前请求是否加密
     * @return
     */
    public boolean isCodec() {
        ServletInputStream inputStream = null;
        try {
            inputStream = getInputStream();
        } catch (IOException e) {
            return false;
        }
        if(!(inputStream instanceof ReusableServletInputStream)) {
            return false;
        }
        String firstStr = ((ReusableServletInputStream) inputStream).readSubString(0, 1);
        return null != firstStr && !"".equals(firstStr) && !"{".equals(firstStr);
    }

    /**
     * 获取请求的msgId
     * @return
     */
    public String getRequestMessageId() {
        if(!"".equals(requestMessageId)) {
            return requestMessageId;
        }
        //先从请求头中获取请求的messageId,取不到
        requestMessageId = _getHttpServletRequest().getHeader(REQUEST_MESSAGE_ID);
        if(!StringUtils.hasText(requestMessageId)) {
            try {
                requestMessageId = getReusableServletInputStream().readSubString(0, 16);
            } catch (IOException e) {
                requestMessageId = null;
            }
        }
        return requestMessageId;
    }

    public class ReusableServletInputStream extends ServletInputStream {
        public final int REUSABLE_MAX_LENGTH = ConfigUtils.getValue(ConfigName.SESSION_REUSABLE_MAX_LENGTH,
                ConfigName.SESSION_REUSABLE_MAX_LENGTH_DEFVAL);    // 可重复读取的位数
        private ServletInputStream inputStream; // 输入流
        private int cur = 0;    // 当前读取进度
        private int maxLength = -1;    // 当前环境的可重复读的最大长度
        private byte[] bufByteArr = new byte[REUSABLE_MAX_LENGTH];  // 重复读的缓存数据

        public ReusableServletInputStream(ServletInputStream inputStream) {
            this.inputStream = inputStream;
        }

        /**
         * 检查是否预读
         * @throws java.io.IOException
         */
        private void checkRead() throws IOException {
            if(-1 == maxLength) {
                maxLength = inputStream.read(bufByteArr);
            }
        }

        @Override
        public int read() throws IOException {
            checkRead();
            if(cur < maxLength) {
                return bufByteArr[cur++];
            } else {
                return inputStream.read();
            }
        }

        /**
         * 直接读取指定位置的字符串，不影响流的读取
         * @param offset 偏移量
         * @param length 读取长度
         * @return
         */
        public String readSubString(int offset, int length) {
            try {
                checkRead();
            } catch (IOException e) {
                return null;
            }
            if(offset >= maxLength) {
                return null;
            }
            if(length > maxLength - offset) {
                length = maxLength - offset;
            }
            return new String(bufByteArr, offset, length, characterEncoding);
        }
    }
}
