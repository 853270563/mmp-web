package cn.com.yitong.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Zip;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;
import org.apache.tools.zip.ZipOutputStream;

/**
 * 压缩文件工具类
 * @author panqingqing
 * 
 */
public class ZipUtil {
	/**
	 * @param srcFile	源文件
	 * @param destFile	压缩文件
	 */
	public static void zip(String srcFile, String destFile) {
		Project prj = new Project();
		Zip zip = new Zip();
		zip.setProject(prj);
		zip.setDestFile(new File(destFile));
		zip.setEncoding("UTF-8");
		FileSet fileSet = new FileSet();
		fileSet.setProject(prj);
		fileSet.setDir(new File(srcFile));
		zip.addFileset(fileSet);
		zip.execute();
	}
	 /** 
     * 解压文件到指定目录 
     * @param zipFile 
     * @param descDir 
     */  
    public static void unZipFiles(File zipFile,String descDir)throws IOException{  
        File pathFile = new File(descDir);  
        if(!pathFile.exists()){  
            pathFile.mkdirs();  
        }  
        ZipFile zip = new ZipFile(zipFile);  
        for(Enumeration entries = zip.getEntries();entries.hasMoreElements();){  
            ZipEntry entry = (ZipEntry)entries.nextElement();  
            String zipEntryName = entry.getName();  
            InputStream in = zip.getInputStream(entry);  
            String outPath = (descDir+zipEntryName).replaceAll("\\*", "//");;  
            //判断路径是否存在,不存在则创建文件路径  
            File file = new File(outPath.substring(0, outPath.lastIndexOf('/')));  
            if(!file.exists()){  
                file.mkdirs();  
            }  
            //判断文件全路径是否为文件夹,如果是上面已经上传,不需要解压  
            if(new File(outPath).isDirectory()){  
                continue;  
            }  
            //输出文件路径信息  
            System.out.println(outPath);  
              
            OutputStream out = new FileOutputStream(outPath);  
            byte[] buf1 = new byte[1024];  
            int len;  
            while((len=in.read(buf1))>0){  
                out.write(buf1,0,len);  
            }  
            in.close();  
            out.close();  
            }  
        System.out.println("******************解压完毕********************");  
    }
    
	 /** 
     * 解压文件到指定目录 
     * @param zipFile 
     * @param descDir 
     * @param true 生成zip名字命名的文件夹，并将压缩包的内容放入此文件夹中，false 直接将内容解压到当前压缩包所在的目录下
     */  
    public static void unZipFiles(File zipFile,boolean flag)throws IOException{  
    	String parentPath=zipFile.getParent();
    	String zipName=zipFile.getName();
    	String descDir=parentPath+"/";
    	if(flag){
    		descDir=descDir+zipName.substring(0,zipName.lastIndexOf("."))+"/";
    	}
    	
        File pathFile = new File(descDir);
        if(!pathFile.exists()){  
            pathFile.mkdirs();  
        }  
        
        ZipFile zip = new ZipFile(zipFile);  
        for(Enumeration entries = zip.getEntries();entries.hasMoreElements();){  
            ZipEntry entry = (ZipEntry)entries.nextElement();  
            String zipEntryName = entry.getName();  
            InputStream in = zip.getInputStream(entry);  
            String outPath = (descDir+zipEntryName).replaceAll("\\*", "//");;  
            //判断路径是否存在,不存在则创建文件路径  
            File file = new File(outPath.substring(0, outPath.lastIndexOf('/')));  
            if(!file.exists()){  
                file.mkdirs();  
            }  
            //判断文件全路径是否为文件夹,如果是上面已经上传,不需要解压  
            if(new File(outPath).isDirectory()){  
                continue;  
            }  
            //输出文件路径信息  
            System.out.println(outPath);  
              
            OutputStream out = new FileOutputStream(outPath);  
            byte[] buf1 = new byte[1024];  
            int len;  
            while((len=in.read(buf1))>0){  
                out.write(buf1,0,len);  
            }  
            in.close();  
            out.close();  
            }  
        System.out.println("******************解压完毕********************");  
    }
    
    public static void main(String[] args) {
    	File zipFile=new File("E:/home/wasadmin/resources/crdt_image/client/admin_1411372858904.zip");
    	try {
			ZipUtil.unZipFiles(zipFile, true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
