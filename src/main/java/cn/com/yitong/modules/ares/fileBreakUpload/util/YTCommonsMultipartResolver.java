package cn.com.yitong.modules.ares.fileBreakUpload.util;

import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUpload;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

public class YTCommonsMultipartResolver extends CommonsMultipartResolver {

	protected FileUpload newFileUpload(FileItemFactory fileItemFactory) {
		return new YTServletFileUpload(fileItemFactory);
	}
}
