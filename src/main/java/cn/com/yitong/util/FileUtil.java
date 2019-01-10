package cn.com.yitong.util;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

public class FileUtil {

	public static String readFileAsString(String fileName) {
		String content = "";
		try {
			content = new String(readFileBinary(fileName), "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return content;

	}

	/**
	 * 
	 * 读取文件并返回为给定字符集的字符串.
	 * 
	 * @param fileName
	 * @param encoding
	 * @return
	 * @throws Exception
	 */
	public static String readFileAsString(String fileName, String encoding)
			throws Exception {
		String content = new String(readFileBinary(fileName), encoding);
		return content;

	}

	/**
	 * 
	 * 读取文件并返回为给定字符集的字符串.
	 * 
	 * @param fileName
	 * @param encoding
	 * @return
	 * @throws Exception
	 */
	public static String readFileAsString(InputStream in) throws Exception {
		return new String(readFileBinary(in));
	}

	/**
	 * Read content from local file to binary byte array.
	 * 
	 * @param fileName
	 *            local file name to read
	 * @return
	 * @throws Exception
	 */
	public static byte[] readFileBinary(String fileName) {
		byte[] rst = null;
		FileInputStream fin = null;
		try {
			fin = new FileInputStream(fileName);
			rst = readFileBinary(fin);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fin != null) {
				try {
					fin.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return rst;

	}

	/**
	 * 
	 * 从输入流读取数据为二进制字节数组.
	 * 
	 * @param streamIn
	 * @return
	 * @throws java.io.IOException
	 */
	private static byte[] readFileBinary(InputStream streamIn)
			throws IOException {
		BufferedInputStream in = new BufferedInputStream(streamIn);
		ByteArrayOutputStream out = new ByteArrayOutputStream(10240);
		int len;
		byte buf[] = new byte[1024];
		while ((len = in.read(buf)) >= 0)
			out.write(buf, 0, len);
		in.close();
		return out.toByteArray();
	}

	public static boolean writeFileString(String fileName, String content) {
		FileWriter fout = null;
		try {
			fout = new FileWriter(fileName);
			fout.write(content);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fout != null) {
				try {
					fout.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return true;

	}

	public static boolean writeFileString(String fileName, String content,
			String encoding) {
		OutputStreamWriter fout = null;
		try {
			fout = new OutputStreamWriter(new FileOutputStream(fileName),
					encoding);
			fout.write(content);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fout != null) {
				try {
					fout.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return true;
	}

	private static boolean writeFileBinary(String fileName, byte[] content) {
		FileOutputStream fout = null;
		try {
			fout = new FileOutputStream(fileName);
			fout.write(content);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fout != null) {
				try {
					fout.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return true;

	}

	/**
	 * 
	 * 检查文件名是否合法.文件名字不能包含字符\/:*?"<>|
	 * 
	 * 
	 * 
	 * @param fileName文件名
	 *            ,不包含路径
	 * 
	 * @return boolean is valid file name
	 */

	public static boolean isValidFileName(String fileName) {
		boolean isValid = true;
		String errChar = "\\/:*?\"<>|"; //
		if (fileName == null || fileName.length() == 0) {
			isValid = false;
		} else {
			for (int i = 0; i < errChar.length(); i++) {
				if (fileName.indexOf(errChar.charAt(i)) != -1) {
					isValid = false;
					break;
				}
			}
		}
		return isValid;

	}

	/**
	 * 
	 * 把非法文件名转换为合法文件名.
	 * 
	 * 
	 * 
	 * @param fileName
	 * 
	 * @return
	 */

	public static String replaceInvalidFileChars(String fileName) {
		StringBuffer out = new StringBuffer();
		for (int i = 0; i < fileName.length(); i++) {
			char ch = fileName.charAt(i);
			// Replace invlid chars: \\/:*?\"<>|
			switch (ch) {
			case '\\':
			case '/':
			case ':':
			case '*':
			case '?':
			case '\"':
			case '<':
			case '>':
			case '|':
				out.append('_');
				break;
			default:
				out.append(ch);
			}
		}
		return out.toString();
	}

	public static String filePathToURL(String fileName) {
		return new File(fileName).toURI().toString();
	}

	public static boolean appendFileString(String fileName, String content) {
		OutputStreamWriter fout = null;
		try {
			fout = new OutputStreamWriter(new FileOutputStream(fileName, true),
					"GBK");
			fout.write(content);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fout != null) {
				try {
					fout.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return true;

	}
	


	/**
	 *以文件流的方式复制文件
	 * @param src
	 * @param dest
	 * @throws java.io.IOException
	 */
	public static void copyFile(String src,String dest) throws IOException{
	    FileInputStream in=new FileInputStream(src);
	    File file=new File(dest);
	    if(!file.exists())
	        file.createNewFile();
	    FileOutputStream out=new FileOutputStream(file);
	    int c;
	    byte buffer[]=new byte[1024];
	    while((c=in.read(buffer))!=-1){
	        for(int i=0;i<c;i++)
	            out.write(buffer[i]);        
	    }
	    in.close();
	    out.close();
	}
	public static void copyFile1(String oldPath,String newPath){
		InputStream inStream = null;
		FileOutputStream fs = null;
		try{
			int bytesum = 0;
			int byteread = 0;
			File oldfile = new File(oldPath);
			if(oldfile.exists()){
				inStream = new FileInputStream(oldPath);
				fs = new FileOutputStream(newPath);
				byte[] buffer = new byte[1444];
				int length;
				while((byteread=inStream.read(buffer))!=-1){
					bytesum +=byteread;
					fs.write(buffer,0,byteread);
				}
				inStream.close();
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(inStream!=null){
				try {
					inStream.close();
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
			
			if(fs!=null){
				try {
					fs.close();
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
		}
	}
	public static void main(String[] args) {
		System.out.println(replaceInvalidFileChars("http://www.abc.com/"));
	}

}