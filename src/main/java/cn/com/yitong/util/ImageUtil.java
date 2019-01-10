package cn.com.yitong.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.FileCopyUtils;

import Decoder.BASE64Decoder;
import cn.com.yitong.consts.Properties;
import cn.com.yitong.tools.codec.Base64;

public class ImageUtil {
	
	private static final String imgSaveFilePath = Properties.getString("upload_files_path");
	private static Logger logger = LoggerFactory.getLogger(ImageUtil.class);

	/**
	 *  base64字符串转化成图片
	 * @param imgStr base64字符串
	 * @param objId 房产id或者任务id
	 * @param code 文件类型
	 * @return
	 */
	public  static String GenerateImage(String imgStr, String objId, String code){ // 对字节数组字符串进行Base64解码并生成图片
		if (imgStr == null){
			// 图像数据为空
			return "";
		}
		BASE64Decoder decoder = new BASE64Decoder();
		try {
			// Base64解码
			byte[] b = decoder.decodeBuffer(imgStr);
			for (int i = 0; i < b.length; ++i) {
				if (b[i] < 0) {// 调整异常数据
					b[i] += 256;
				}
			}
			String picName = DateUtil.todayStr("yyyyMMddHHmmss")+RandomUtil.getBaseNum();//防止图片名字重复
            //根据分类生成目录
			String imgFilePath = imgSaveFilePath + File.separator + objId + File.separator + code;
			File file = new File(imgFilePath);
			if(!file.exists()){
				file.mkdirs();
            }
			// 新生成的图片
			imgFilePath = imgFilePath + File.separator+ picName+ ".jpg";
			File fileJpg = new File(imgFilePath);
			OutputStream out = new FileOutputStream(fileJpg);
			out.write(b);
			out.flush();
			out.close();
			return imgFilePath;
		} catch (Exception e) {
		    e.printStackTrace();
			return "";
		}
	}

	public static void GenerateHeadImage(String userId, String base64) {
		String path = imgSaveFilePath + "/userHead";
		if (!new File(path).exists()) {
			new File(path).mkdirs();
		}
	
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(new File(path, userId + ".png"));
			out.write(Base64.decode(base64));
		} catch (Exception e) {
			logger.warn(e.getMessage(), e);
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					logger.warn(e.getMessage(), e);
				}

			}
		}

	}

	public static String qryHeadImage(String userId) {
		String path = imgSaveFilePath + "/userHead";
		if (!new File(path).exists()) {
			new File(path).mkdirs();
		}
	
		FileInputStream in = null;
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		try {
			in = new FileInputStream((new File(path, userId + ".png")));
			FileCopyUtils.copy(in, output);
			String encodeToString = Base64.encodeToString(output.toByteArray());
			return encodeToString;
		} catch (Exception e) {
			logger.warn(e.getMessage(), e);
		}finally{
			if(null != output){
				try {
					output.close();
				} catch (IOException e) {
					logger.warn(e.getMessage(), e);
				}
			}
			if(null != in){
				try {
					in.close();
				} catch (IOException e) {
					logger.warn(e.getMessage(), e);
				}
			}
		}
		return "";

	}
}
