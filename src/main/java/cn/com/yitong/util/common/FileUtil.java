package cn.com.yitong.util.common;

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
	/**
	 * 解析文件的扩展名
	 * 
	 * @param oldName
	 * @return
	 */
	public static String getFileExtName(String oldName) {
		String ext = "";
		int lastIndex = oldName.lastIndexOf(".");
		if (lastIndex > 0) {
			ext = oldName.substring(lastIndex);
		}
		return ext;
	}

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
		return new String(readFileBinary(fileName), encoding);
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
		return new String(readFileBinary(in), "utf-8");
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
		try {
			FileInputStream fin = new FileInputStream(fileName);
			rst = readFileBinary(fin);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return rst;

	}

	/**
	 * 
	 * 从输入流读取数据为二进制字节数组.
	 * 
	 * @param streamIn
	 * @return
	 * @throws IOException
	 */
	public static byte[] readFileBinary(InputStream streamIn) {
		BufferedInputStream in = null;
		ByteArrayOutputStream out = null;
		try {
			in = new BufferedInputStream(streamIn);
			out = new ByteArrayOutputStream(10240);
			int len;
			byte buf[] = new byte[1024];
			while ((len = in.read(buf)) >= 0)
				out.write(buf, 0, len);
			return out.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
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
	 * 通过类路径来取工程路径 
	 *  
	 * @return 
	 * @throws Exception 
	 */  
	public static String getAbsolutePathByClass(){  
	    String webPath = FileUtil.class.getResource("/").getPath().replaceAll("^\\/", "");  
	    webPath = webPath.replaceAll("[\\\\\\/]WEB-INF[\\\\\\/]classes[\\\\\\/]?", "/");  
	    webPath = webPath.replaceAll("[\\\\\\/]+", "/");  
	    webPath = webPath.replaceAll("%20", " ");  
	  
	    if (webPath.matches("^[a-zA-Z]:.*?$")) {  
	  
	    } else {  
	        webPath = "/" + webPath;  
	    }  
	  
	    webPath += "/";  
	    webPath = webPath.replaceAll("[\\\\\\/]+", "/");  
	  
	    return webPath;  
	} 


	public static void main(String[] args) {
		System.out.println(replaceInvalidFileChars("http://www.abc.com/"));
	}

}