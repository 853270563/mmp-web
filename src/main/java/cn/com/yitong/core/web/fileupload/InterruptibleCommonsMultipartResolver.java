package cn.com.yitong.core.web.fileupload;

import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUpload;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

/**
 * @author lc3@yitong.com.cn
 */
public class InterruptibleCommonsMultipartResolver extends CommonsMultipartResolver {

    /**
     * 断点请求Url列表
     */
    private String[] inheritableUrls;

    @Override
    protected FileUpload newFileUpload(FileItemFactory fileItemFactory) {
        final InterruptibleServletFileUpload fileUpload = new InterruptibleServletFileUpload(fileItemFactory);
        fileUpload.setInheritableUrls(getInheritableUrls());
        return fileUpload;
    }

    public String[] getInheritableUrls() {
        return inheritableUrls;
    }

    public void setInheritableUrls(String[] inheritableUrls) {
        this.inheritableUrls = inheritableUrls;
    }
}
