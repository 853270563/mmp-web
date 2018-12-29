package cn.com.yitong.framework.util.io;

import java.io.File;
import java.io.FileFilter;
import java.util.List;
import java.util.regex.Pattern;

public class RegexListFilter implements FileFilter {

	private Pattern[] includePatterns;
	private Pattern[] excludePatterns;
	private int baseDirLength;

	public RegexListFilter(File baseDir, List<String> includeFiles, List<String> excludeFiles) {
		if (null != includeFiles && !includeFiles.isEmpty()) {
			includePatterns = new Pattern[includeFiles.size()];
			for (int i = 0; i < includeFiles.size(); i++) {
				includePatterns[i] = Pattern.compile(includeFiles.get(i));
			}
		}
		if (null != excludeFiles && !excludeFiles.isEmpty()) {
			excludePatterns = new Pattern[excludeFiles.size()];
			for (int i = 0; i < excludeFiles.size(); i++) {
				excludePatterns[i] = Pattern.compile(excludeFiles.get(i));
			}
		}
		baseDirLength = baseDir.getAbsolutePath().length();
	}

	@Override
	public boolean accept(File dir) {
		boolean result = false;
		String path = dir.getAbsolutePath();
		if(null != includePatterns) {
			for(Pattern p : includePatterns) {
				if(p.matcher(path.substring(baseDirLength)).matches()) {
					result = true;
					break;
				}
			}
			if(!result) {
				return false;
			}
		}
		if(null != excludePatterns) {
			result = true;
			for(Pattern p : excludePatterns) {
				if(p.matcher(path.substring(baseDirLength)).matches()) {
					result = false;
					break;
				}
			}
			if(!result) {
				return false;
			}
		}
		return true;
	}
}
