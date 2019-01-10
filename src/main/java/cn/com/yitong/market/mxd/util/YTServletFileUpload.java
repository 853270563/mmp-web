package cn.com.yitong.market.mxd.util;

import org.apache.commons.fileupload.*;
import org.apache.commons.fileupload.MultipartStream.MalformedStreamException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;

//import org.apache.commons.fileupload.FileItemHeaders;

/**
 * 
 * 重写org.apache.commons.fileupload.FileUploadBase的parseRequest方法
 * 支持断点续传：当文件中断即出现MalformedStreamException异常时，保存当前输入流中的字节至临时文件。
 *
 * @author hry@yitong.com.cn
 * @date 2015年4月28日
 */
public class YTServletFileUpload extends ServletFileUpload {
	private Logger logger = Logger.getLogger(this.getClass());
	public YTServletFileUpload() {
		super();
	}
	public YTServletFileUpload(FileItemFactory fileItemFactory) {
		super(fileItemFactory);
	}
	@Override
	public List<FileItem> parseRequest(RequestContext ctx)throws FileUploadException {
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
                FileItem fileItem = fac.createItem(item.getFieldName(), item.getContentType(),item.isFormField(), fileName);
                items.add(fileItem);
                try {
                    Streams.copy(item.openStream(), fileItem.getOutputStream(), true);
                } catch (FileUploadIOException e) {
                    throw (FileUploadException) e.getCause();
                } catch (IOException e) {
                	if(e instanceof MalformedStreamException){
                		logger.info("文件上传中断："+e.getMessage());
                		//文件名即md5值
                		String md5 = fileItem.getName().substring(0, fileItem.getName().length()-4);
                		FileUploader fileUploader = new FileUploader(md5);
            			InputStream inputStream = fileItem.getInputStream();//已在FileUploader里面关闭，此处可以不处理
            			//保存文件至临时文件
            			fileUploader.saveFile(inputStream);
                		logger.info("当前文件指针（已上传字节）:"+fileUploader.getNodePosition());
                	}else{
                		throw new IOFileUploadException(format("Processing of %s request failed. %s",MULTIPART_FORM_DATA, e.getMessage()), e);
                	}
                }
                //final FileItemHeaders fih = item.getHeaders();
                //fileItem.setHeaders(fih);
            }
            successful = true;
            return items;
        } catch (FileUploadIOException e) {
            throw (FileUploadException) e.getCause();
        } catch (IOException e) {
            throw new FileUploadException(e.getMessage(), e);
        } finally {
            clearFileItem(items, successful);
        }
	}
	private void clearFileItem(List<FileItem> items, boolean successful) {
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
	
}
