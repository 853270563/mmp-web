package cn.com.yitong.core.web.fileupload;

import org.apache.commons.fileupload.*;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.NamedInheritableThreadLocal;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.util.UrlPathHelper;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;

/**
 * 可中断的文件上传工具
 * @author lc3@yitong.com.cn
 */
public class InterruptibleServletFileUpload extends ServletFileUpload {

    private static final Logger logger = LoggerFactory.getLogger(InterruptibleServletFileUpload.class);
    private final static AntPathMatcher antPathMatcher = new AntPathMatcher();
    private final static UrlPathHelper urlPathHelper = new UrlPathHelper();
    private static final ThreadLocal<String> inheritableRequestUrl =
            new NamedInheritableThreadLocal<String>("InterruptibleServletFileUpload Request url");
    /**
     * 断点请求Url列表
     */
    private String[] inheritableUrls;

    public InterruptibleServletFileUpload() {
        super();
    }

    public InterruptibleServletFileUpload(FileItemFactory fileItemFactory) {
        super(fileItemFactory);
    }

    @Override
    public List<FileItem> parseRequest(HttpServletRequest request) throws FileUploadException {
        inheritableRequestUrl.set(urlPathHelper.getPathWithinApplication(request));
        return super.parseRequest(request);
    }

    @Override
    public List<FileItem> parseRequest(RequestContext ctx) throws FileUploadException {
        List<FileItem> items = new ArrayList<FileItem>();
        boolean successful = false;
        try {
            FileItemIterator iter = getItemIterator(ctx);
            FileItemFactory fac = getFileItemFactory();
            if (fac == null) {
                throw new NullPointerException("No FileItemFactory has been set.");
            }
            while (iter.hasNext()) {
                final FileItemStream item = iter.next();
                final String fileName = item.getName();
                FileItem fileItem = fac.createItem(item.getFieldName(), item.getContentType(),
                        item.isFormField(), fileName);
                items.add(fileItem);
                try {
                    Streams.copy(item.openStream(), fileItem.getOutputStream(), true);
                } catch (FileUploadIOException e) {
                    throw (FileUploadException) e.getCause();
                } catch (IOException e) {
                    final String msg = String.format("Processing of %s request failed. %s",
                            FileUploadBase.MULTIPART_FORM_DATA, e.getMessage());
                    if(logger.isDebugEnabled()) {
                        logger.debug(msg);
                    }
                    if(!isInterruptibleRequest()) {
                        throw new IOFileUploadException(msg, e);
                    }
                    logger.debug("文件上传中断，进行断点保存……");
                }
                final FileItemHeaders fih = item.getHeaders();
                fileItem.setHeaders(fih);
            }
            successful = true;
            return items;
        } catch (FileUploadIOException e) {
            throw (FileUploadException) e.getCause();
        } catch (IOException e) {
            if(!isInterruptibleRequest()) {
                throw new FileUploadException(e.getMessage(), e);
            }
            successful = true;
        } finally {
            if (!successful) {
                for (FileItem fileItem : items) {
                    try {
                        fileItem.delete();
                    } catch (Throwable e) {
                        // ignore it
                    }
                }
            }
        }
        return items;
    }

    /**
     * 是否断点续传请求
     * @return 是否
     */
    private boolean isInterruptibleRequest() {
        if(null != getInheritableUrls()) {
            String reqUrl = inheritableRequestUrl.get();
            for (String url : getInheritableUrls()) {
                if(antPathMatcher.match(url, reqUrl)) {
                    return true;
                }
            }
        }
        return false;
    }

    public String[] getInheritableUrls() {
        return inheritableUrls;
    }

    public void setInheritableUrls(String[] inheritableUrls) {
        this.inheritableUrls = inheritableUrls;
    }
}
