package cn.com.yitong.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Date;

import org.apache.log4j.Logger;

public class FileCopy {

	private static Logger log = YTLog.getLogger(FileCopy.class);
	private static int DEF_SIZE = 1048576;

	public static long forChannel(File f1, File f2) throws Exception {
		long time = new Date().getTime();
		int length = DEF_SIZE;
		FileInputStream in = new FileInputStream(f1);
		FileOutputStream out = new FileOutputStream(f2);
		FileChannel inC = in.getChannel();
		FileChannel outC = out.getChannel();
		ByteBuffer b = null;
		while (true) {
			if (inC.position() == inC.size()) {
				inC.close();
				outC.close();
				return new Date().getTime() - time;
			}
			if ((inC.size() - inC.position()) < length) {
				length = (int) (inC.size() - inC.position());
			} else
				length = DEF_SIZE;
			b = ByteBuffer.allocateDirect(length);
			inC.read(b);
			b.flip();
			outC.write(b);
			outC.force(false);
		}
	}

	public static void main(String[] args) {
		long beginTime = new Date().getTime();
		String input = "D:/1.4.3.png";
		String output = "D:/temps/imgs/1.4.3#.png";
		File inf = new File(input);
		try {
			for (int i = 0; i < 10; i++) {
				File outf = new File(
						output.replaceFirst("#", String.valueOf(i)));
				if (outf.exists())
					outf.delete();
				forChannel(inf, outf);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		long endTime = new Date().getTime();
		log.info("共耗时(ms)：" + (endTime - beginTime));
	}

}
