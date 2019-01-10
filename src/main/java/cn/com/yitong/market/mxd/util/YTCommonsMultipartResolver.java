package cn.com.yitong.market.mxd.util;

import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUpload;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

/**
 * 
 * 重写父类CommonsMultipartResolver的newFileUpload方法
 * 支持断点续传
 * @author hry@yitong.com.cn
 * @date 2015年4月28日
 */
public class YTCommonsMultipartResolver extends CommonsMultipartResolver {
	@Override
	protected FileUpload newFileUpload(FileItemFactory fileItemFactory) {
		return new YTServletFileUpload(fileItemFactory);
	}
}
