package cn.com.yitong.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 
 * @author lc3@yitong.com.cn
 *
 */
public enum CustomFileType {
	PDF(Arrays.asList("pdf"), "pdf", 30000000L),
	ZIP(Arrays.asList("zip"), "zip", 200000000L),
	APK(Arrays.asList("apk"), "apk", 300000000L),
	IPA(Arrays.asList("ipa"), "ipa", 300000000L),
	IMG(Arrays.asList("jpeg", "bmp", "png", "gif", "jpg", "ico"), "img", 100000000L),
	VIDEO(Arrays.asList("mp4", "avi", "mkv", "rm", "mov", "rmvb"), "video", 100000000L),
	HEAD(Arrays.asList(""), "head", 100000000L),
    FILE(Arrays.asList("jpeg", "bmp", "png", "gif", "jpg", "ico", "pdf", "mp4", "avi", "mkv", "rm", "mov", "rmvb"), "file", 50000000L),
	MOA(Arrays.asList("jpeg", "bmp", "png", "gif", "jpg", "ico", "pdf", "mp4", "avi", "mkv", "rm", "mov", "rmvb"), "MOA", 50000000L)
    ;
	private String filePath;
	private List<String> list;
	private long size;
	private CustomFileType(List<String> list, String filePath, long size) {
		this.size = size;
		this.list = Collections.unmodifiableList(list);
		this.filePath = filePath;
	}
	public String getFilePath() {
		return filePath;
	}
	public List<String> getFileTypeList() {
		return list;
	}
	public long getSize() {
		return size;
	}
	public static CustomFileType getType(String type) {
		if("pdf".equals(type)){
			return PDF;
		}else if("zip".equals(type)){
			return ZIP;
		}else if("apk".equals(type)){
			return PDF;
		}else if("ipa".equals(type)){
			return PDF;
		}else if("jpg".equals(type)||"jpeg".equals(type)||"bmp".equals(type)||"png".equals(type)||"gif".equals(type)||"ico".equals(type)){
			return IMG;
		}else if("mp4".equals(type)||"avi".equals(type)||"mkv".equals(type)||"rm".equals(type)||"mov".equals(type)||"rmvb".equals(type)) {
			return VIDEO;
		}else {
			return FILE;
		}
	}
}
