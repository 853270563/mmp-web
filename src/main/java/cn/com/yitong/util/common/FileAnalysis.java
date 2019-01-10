package cn.com.yitong.util.common;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 前置文件解析
 * 
 * @author yaoym
 * 
 */
public class FileAnalysis {

	
	private static Logger logger = LoggerFactory.getLogger(FileAnalysis.class);
	
	private static final int BUF_SIZE = 1024;

	/**
	 * 加载文件数据
	 * 
	 * @param fileName
	 * @param beginIndex
	 *            起始位置
	 * @param pageSize
	 * @param datas
	 * @param cols
	 *            列数
	 * @return
	 */
	public static int loadDatas(String fileName, int beginIndex, int pageSize,
			List datas, int cols) {
		RandomAccessFile randomFile = null;
		try {
			// 打开一个随机访问文件流，按只读方式, 需要留意文件系统权限
			randomFile = new RandomAccessFile(fileName, "r");
			// 文件长度，字节数
			long fileLength = randomFile.length();
			// 读文件的起始位置
			int tmpPos = Math.max(beginIndex, 0);
			// 将读文件的开始位置移到beginIndex位置。
			randomFile.seek(tmpPos);
			byte[] rst = new byte[0];
			byte[] bytes = new byte[BUF_SIZE];
			int byteread = 0;
			PageQZ qz = new PageQZ(0, 0);
			while ((byteread = randomFile.read(bytes)) != -1) {
				// System.out.println("------byteread-------" + byteread);
				// 提取内容直到文件结束或指定页面数据
				int end = getEndPosition(bytes, byteread, qz, pageSize);
				tmpPos += end;
				if (end < BUF_SIZE) {
					byte[] tmp = new byte[end];
					System.arraycopy(bytes, 0, tmp, 0, end);
					rst = mergeDatas(rst, tmp);
				} else {
					rst = mergeDatas(rst, bytes);
				}
				if (qz.curNum >= pageSize) {
					break;
				}
			}
			String content = new String(rst, "UTF-8");
			parseData(content, datas, cols);
			// 修正下次取值位置
			if (qz.curNum == pageSize && (fileLength > tmpPos + 5)) {
				// System.out.println("nextBeginIndex:" + tmpPos);
				return tmpPos;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (randomFile != null) {
				try {
					randomFile.close();
				} catch (IOException e1) {
				}
			}
		}
		return 0;
	}

	private static byte[] mergeDatas(byte[] data1, byte[] data2) {
		byte[] rst = new byte[data1.length + data2.length];
		if (data1.length > 0) {
			System.arraycopy(data1, 0, rst, 0, data1.length);
		}
		System.arraycopy(data2, 0, rst, data1.length, data2.length);
		return rst;
	}

	private static void parseData(String content, List datas, int cols) {
		String[] rows = content.split("\n");
		for (String row : rows) {
			if (StringUtil.isEmpty(row) || row.length() < 2) {
				continue;
			}
			//2014-09-28  数据后为多个 ||| 无法正常拆分
			String[] tempS = row.split("\\|",-1);
			String[] tds = new String[tempS.length-1];
			System.arraycopy(tempS, 0, tds, 0, tds.length);
			//2014-09-26  数据里面含有空格
			for (int i=0;i<tds.length;i++) {
				tds[i] = tds[i].trim();
			}
			logger.debug("tds:{}========cols:{}",tds.length,cols);
			System.out.println(tds.length >= cols);
			if (tds.length >= cols) {
				// System.out.println("row:" + tds[0]);
				datas.add(tds);// 数据安全保护
			}
		}
	}

	public static int getEndPosition(byte[] bytes, int maxSize, PageQZ qz,
			int pageSize) {
		int i = 2;// 安全字节
		for (; i < maxSize; i++) {
			byte b = bytes[i];
			if (b == '\n' || b == 13 || b == 10) {
				i++;
				qz.curNum++;
				if (qz.curNum >= pageSize) {
					return i;
				}
			}
		}
		return i;
	}

	public static void main(String[] args) {
		String filePath = "f:/TEMP/tranlist_191_781011010002918859.txt";
		int pageNo = 1;
		List datas = new ArrayList();
		int endPos = loadDatas(filePath, 0, 10, datas, 2);
		while (endPos > 0) {
			pageNo++;
			System.out.println("=========>pageNo:" + pageNo + "");
			datas = new ArrayList();
			endPos = loadDatas(filePath, endPos, 10, datas, 2);
		}
	}
}

class PageQZ {
	int tmpIndex;
	int curNum;

	public PageQZ(int tmpIndex, int curNum) {
		this.tmpIndex = tmpIndex;
		this.curNum = curNum;
	}
}
