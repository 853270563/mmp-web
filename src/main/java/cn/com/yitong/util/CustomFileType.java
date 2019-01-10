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
	IMG(Arrays.asList("jpeg", "bmp", "png", "gif", "jpg", "ico"), "img", 10000000L),
	ZIP(Arrays.asList("zip"), "zip", 100000000L),
	RESOURCES(Arrays.asList("jpeg", "bmp", "png", "gif", "jpg", "ico","pdf"), "resources", 50000000L),
    APK(Arrays.asList("apk"), "apk", 30000000L),
    IPA(Arrays.asList("ipa"), "ipa", 30000000L),
    WORK_LOG_IMG(Arrays.asList("jpeg", "bmp", "png", "gif", "jpg", "ico"), "work_log_img", 50000000L),
    FILE(Arrays.asList("jpeg", "bmp", "png", "gif", "jpg", "ico", "pdf", "mp4", "avi"), "file", 50000000L),
    BUSSDOCUMENTINFO(Arrays.asList("jpeg", "bmp", "png", "gif", "jpg", "ico", "pdf", "docx", "xls","docm","dotx","xlsx","xlsm","xltx"), "bussDocumentInfo", 50000000L),
    FINANCEREPORT(Arrays.asList("jpeg", "bmp", "png", "gif", "jpg", "ico", "pdf", "docx", "xls","docm","dotx","xlsx","xlsm","xltx"), "financeReport", 50000000L),
    YXINFO(Arrays.asList("jpeg", "bmp", "png", "gif", "jpg", "ico", "pdf", "docx", "xls","docm","dotx","xlsx","xlsm","xltx"), "yxInfo", 50000000L),
    SURVEYREPORT(Arrays.asList("jpeg", "bmp", "png", "gif", "jpg", "ico", "pdf", "docx", "xls","docm","dotx","xlsx","xlsm","xltx","html"), "surveyReport", 50000000L)
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
	public static CustomFileType getType(String type){
		if("zip".equals(type)){
			return ZIP;
		}
		else if("pdf".equals(type)){
			return PDF;
		}
		else if("jpg".equals(type)||"jpeg".equals(type)||"bmp".equals(type)||"png".equals(type)||"gif".equals(type)||"ico".equals(type)){
			return IMG;
		}
		return null;
	}
}
