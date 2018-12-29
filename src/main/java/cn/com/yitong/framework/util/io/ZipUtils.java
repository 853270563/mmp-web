package cn.com.yitong.framework.util.io;

import java.io.File;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Expand;
import org.apache.tools.ant.taskdefs.Zip;
import org.apache.tools.ant.types.FileSet;

public class ZipUtils {

	
	public static final String ZIP_FILE_ENCODING = "GBK";
	/**
	 * @param destDir
	 *            目标目录
	 * @param zipName
	 *            解压缩的zip文件名
	 */
	public static void unzip(String filePath, String zipName) {
		Project project = new Project();
		Expand expand = new Expand();
		expand.setProject(project);
		expand.setSrc(new File(zipName));
		// 是否覆盖
		expand.setOverwrite(true);
		File file = new File(filePath);
		if (file.isDirectory()) {
			expand.setDest(file);
			expand.execute();
		}
	}

	/**
	 * @param filePath
	 * @param zipName
	 *            压缩文件名
	 */
	public static void zip(String filePath, String zipName) {
		Project project = new Project();
		Zip zip = new Zip();
		zip.setProject(project);
		zip.setDestFile(new File(zipName));
		FileSet fileSet = new FileSet();
		fileSet.setProject(project);
		File file = new File(filePath);
		if (file.isDirectory()) {
			fileSet.setDir(file);
			// fileSet.setIncludes("**/*.java");//包括哪些文件或文件夹，只压缩目录中的所以java文件
			// fileSet.setExcludes("**/*.java");//排序哪些文件或文件夹，压缩所有的文件，排除java文件
			zip.addFileset(fileSet);
			zip.execute();
		}
	}
	
    /**
     * 压缩文件夹
     * @param srcDir
     * @param zipFile
     */
    public static void zip(File srcDir, File zipFile) {
        Zip zip = new Zip();

        Project p = new Project();
        p.setBaseDir(srcDir);
        zip.setProject(p);

        zip.setBasedir(srcDir);
        zip.setDestFile(zipFile);

        zip.setEncoding(ZIP_FILE_ENCODING);
        zip.execute();
    }

    /**
     * 解压zip文件
     * @param zipFile
     * @param destDir
     */
    public static void unzip(File zipFile, File destDir) {
        Expand expand = new Expand();

        Project p = new Project();
        p.setBaseDir(destDir);
        expand.setProject(p);

        expand.setEncoding(ZIP_FILE_ENCODING);
        expand.setSrc(zipFile);
        expand.setDest(destDir);
        expand.execute();
    }

}
