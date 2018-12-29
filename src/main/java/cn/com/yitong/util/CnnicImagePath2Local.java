package cn.com.yitong.util;

import cn.com.agree.eai.file.natp.NatpFileClient;
import cn.com.yitong.consts.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class CnnicImagePath2Local {

	private static Logger logger = LoggerFactory.getLogger(CnnicImagePath2Local.class);

	public static String PhotoDownloadIP = "10.2.35.32";//相片文件服务器IP
	public static int PhotoDownloadPort = 65500;//相片文件服务器端口
	public static String senderID = "";//发起方标识
	
	public static Map savePhotoPath(String photoAddr) {
		NatpFileClient client = null;
		Map map =new HashMap();
		String photoPath = Properties.getString("nciic_image");
		String img_root = Properties.getString("upload_files_path");
		String url = Properties.getString("RESOURCES_URL");
		File f = new File(photoPath);
		if (!f.exists()) {
			f.mkdirs();
		}
		File file = new File(photoAddr);
		String fileName = file.getName();
		String saveAddr = img_root +"/"+ photoPath +"/"+ fileName;
		try {
			client = new NatpFileClient(PhotoDownloadIP, PhotoDownloadPort);
			client.init(senderID, saveAddr, photoAddr,
			NatpFileClient.ACTION_DOWNLOAD_FILE);
			client.run();
			logger.debug(saveAddr + " 文件传输成功!!!");

			map.put("STATUS", "0");
			map.put("path", url+"/"+ photoPath  +"/"+ fileName);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(photoAddr + " 传输文件时出错1秒钟后重新传送：", e);
			map.put("STATUS", "1");
			map.put("path", saveAddr);
		}finally{
			if(client != null){
				client.destroy();
			}
		}
		return map;
	}
}
