package cn.com.yitong.util;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import cn.com.agree.eai.file.natp.NatpFileClient;
import cn.com.yitong.consts.Properties;

public class CnnicImagePath2Local {
	public static void main(String[] args) throws Exception {
		savePhotoPath(null);
	}
	
	public static   Map savePhotoPath(String photoAddr){
		
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
		client = new NatpFileClient(GlobalStaticVar.PhotoDownloadIP,
				GlobalStaticVar.PhotoDownloadPort);
		client.init(GlobalStaticVar.senderID, saveAddr, photoAddr, 
		NatpFileClient.ACTION_DOWNLOAD_FILE);
		client.run();
			System.out.println(saveAddr+" 文件传输成功!!!");

			map.put("STATUS", "0");
			map.put("path", url+"/"+ photoPath  +"/"+ fileName);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(photoAddr+" 传输文件时出错1秒钟后重新传送："+e.getMessage());
			map.put("STATUS", "1");
			map.put("path", saveAddr);
		}finally{
		if(client!=null){
			client.destroy();
		}
		}
		return map;
	
	}
}
