package cn.com.yitong.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.sunyard.client.SunEcmClientApi;
import com.sunyard.client.bean.ClientAnnotationBean;
import com.sunyard.client.bean.ClientBatchBean;
import com.sunyard.client.bean.ClientBatchFileBean;
import com.sunyard.client.bean.ClientBatchIndexBean;
import com.sunyard.client.bean.ClientFileBean;
import com.sunyard.client.bean.ClientHeightQuery;
import com.sunyard.client.bean.OperPermission;
import com.sunyard.client.impl.SunEcmClientSocketApiImpl;
import com.sunyard.ecm.server.bean.MigrateBatchBean;
import com.sunyard.util.OptionKey;
import com.sunyard.util.TransOptionKey;

import cn.com.yitong.framework.servlet.ServerInit;

/**
 * 客户端使用示例
 * 
 * @author Warren
 * 
 */
@Lazy
@Component
public class SunEcmClient {
	private static Logger log = LoggerFactory.getLogger(SunEcmClient.class);
	public final static String SUCCESS = "SUCCESS";
	public final static String FAIL = "FAIL";
	public static String ip = ServerInit.getString("YX_IP");; //为SunECMDM的ip地址
	public static int socketPort= ServerInit.getInt("YX_SOCKETPORT");;//为SunECMDM的socket端口
	public static int httpPort= ServerInit.getInt("YX_HTTPPORT");; //为SunECMDM的http端口
	String serverName= ServerInit.getString("YX_SERVERNAME"); ; // 连接的服务工程名称
	// String serverName = "UnityAccess"; // 连接的服务工程名称
	String groupName= ServerInit.getString("YX_GROUPNAME"); ; // 内容存储服务器组名
	public static SunEcmClientApi clientApi = new SunEcmClientSocketApiImpl(ip, socketPort);
	String STARTCOLUMN = "BUSI_START_DATE";//业务开始时间的属性英文名称
	String STARTDATE = "20141212"; //业务开始时间的值
	public final static String MODEL_CODE = "XDKHZL"; // 索引对象内容模型代码
	String filePartName = "XDKHZL_P"; // 文档对象模型代码
	String userName = "admin"; //登录SunECMDM的用户名
	String passWord = "111"; //登录SunECMDM的密码
	String contentID = "2014_1_8FA39BD6-43BD-F384-F3D3-1783228FC800-12"; // 8位日期+随机路径+36位GUID+内容存储服务器ID
	String fileNO1 = "FF1C004F-08FD-88C2-6C8D-2706EC878EAF";
	String fileNO2 = "";
	String fileNO3 = "381CA442-A7DC-1D11-59C0-9B493C997167";

	String annoID = "92E0A6BC-94CA-2F47-3EF3-A57FB34A69B5";

	String checkToken = "caea764b9f8307687edc"; // 检入检出随机数

	String token_check_value = "1234567890";
	String tokenCode = "caea764b9f8307687edc";
	String insNo = "aag";

	// =========================批次信息设定=========================

	public String getContentID() {
		return contentID;
	}

	public void setContentID(String contentID) {
		this.contentID = contentID;
	}

	/**
	 * 登陆
	 * 
	 * @param userName
	 *            用户名
	 * @param password
	 *            密码
	 * @return
	 * @throws Exception
	 */
	@PostConstruct
	public void login() {
		try {
			String resultMsg = clientApi.login(userName, passWord);
			log.debug("#######登陆返回的信息[" + resultMsg + "]#######");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 登出
	 * 
	 * @param userName
	 *            用户名密码
	 * @return
	 * @throws Exception
	 */
	@PreDestroy
	public void logout() {
		try {
			String resultMsg = clientApi.logout(userName);
			log.debug("#######登出返回的信息[" + resultMsg + "]#######");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 检出----------------------------------------------------------------------
	 * -------
	 * 
	 * @return
	 * @throws Exception
	 */
	public void checkOut() {
		ClientBatchBean clientBatchBean = new ClientBatchBean();
		clientBatchBean.setModelCode(MODEL_CODE);
		clientBatchBean.setUser(userName);
		clientBatchBean.setPassWord(passWord);
		clientBatchBean.getIndex_Object().addCustomMap(STARTCOLUMN, STARTDATE);
		clientBatchBean.getIndex_Object().setContentID(contentID);

		try {
			checkToken = clientApi.checkOut(clientBatchBean, groupName);
			String[] result = checkToken.split("<<::>>");
			checkToken = result[1];
			log.debug("#######检出返回的信息[" + checkToken + "]#######");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 检入----------------------------------------------------------------------
	 * --
	 * 
	 * @return
	 * @throws Exception
	 */
	public void checkIn() {
		ClientBatchBean clientBatchBean = new ClientBatchBean();
		clientBatchBean.setModelCode(MODEL_CODE);
		clientBatchBean.setUser(userName);
		clientBatchBean.setPassWord(passWord);
		clientBatchBean.getIndex_Object().addCustomMap(STARTCOLUMN, STARTDATE);
		clientBatchBean.getIndex_Object().setContentID(contentID);
		clientBatchBean.setCheckToken(checkToken);

		try {
			String resultMsg = clientApi.checkIn(clientBatchBean, groupName);
			log.debug("#######检入返回的信息[" + resultMsg + "]#######");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param filePaths 文件路径
	 * @param busiStartDate 开始日期8位数字
	 */
	public void upload(List<String> filePaths, String busiStartDate) {
		if (filePaths == null || filePaths.isEmpty()) {
			log.info("上传的文件不能为空");
			return;
		}
		ClientBatchBean clientBatchBean = new ClientBatchBean();
		clientBatchBean.setModelCode(MODEL_CODE);
		clientBatchBean.setUser(userName);
		clientBatchBean.setPassWord(passWord);
		clientBatchBean.setBreakPoint(false); // 是否作为断点续传上传
		clientBatchBean.setOwnMD5(false); // 是否为批次下的文件添加MD5码
		// 若内容模型配置有安全校验
		// clientBatchBean.setToken_check_value(token_check_value);
//		clientBatchBean.setToken_code(tokenCode);
		// =========================设置索引对象信息开始=========================
		ClientBatchIndexBean clientBatchIndexBean = new ClientBatchIndexBean();
		clientBatchIndexBean.setAmount(filePaths.size() + "");//数量和文件数量一样
		// 索引自定义属性
		clientBatchIndexBean.addCustomMap(STARTCOLUMN, busiStartDate);
		// =========================设置索引对象信息结束=========================
		// =========================设置文档部件信息开始=========================
		ClientBatchFileBean clientBatchFileBean = new ClientBatchFileBean();
		clientBatchFileBean.setFilePartName(filePartName);
		// =========================设置文档部件信息结束=========================
		// =========================添加文件=========================
		for (String path : filePaths) {
			ClientFileBean fileBean = new ClientFileBean();
			fileBean.setFileName(path);
			clientBatchFileBean.addFile(fileBean);
		}
		clientBatchBean.setIndex_Object(clientBatchIndexBean);
		clientBatchBean.addDocument_Object(clientBatchFileBean);
		try {
			String resultMsg = clientApi.upload(clientBatchBean, groupName);
			if (resultMsg.substring(0, 7).contains(SUCCESS)) {
				log.debug(resultMsg.substring(13));//影像批次号
			}
			log.debug("#######上传批次返回的信息[" + resultMsg + "]#######");
		} catch (Exception e) {
			log.error(e.getMessage());
		}
	}

	/**
	 * 根据批次号查询
	 * @param batchId
	 * @param busiStartDate 开始日期
	 * @return
	 */
	public String query(String batchId, String modelCode, String busiStartDate) {
		ClientBatchBean clientBatchBean = new ClientBatchBean();
		//clientBatchBean.setModelCode("XDKHZL");
		clientBatchBean.setModelCode(modelCode);
		clientBatchBean.setUser("admin");
		clientBatchBean.setPassWord("111");
		clientBatchBean.setDownLoad(false);
		 clientBatchBean.getIndex_Object().setVersion("0");
		clientBatchBean.getIndex_Object().setContentID(batchId);
		clientBatchBean.getIndex_Object().addCustomMap("BUSI_START_DATE", busiStartDate);// 必要信息,自定义属性中必须有一个8位数字字段,用以分表,从内容模型模板中获取字段名
		// 若内容模型配置有安全校验
		// clientBatchBean.setToken_check_value(token_check_value);
		// clientBatchBean.setToken_code(tokenCode);
		String resultMsg;
		try {
			resultMsg = clientApi.queryBatch(clientBatchBean, groupName);
			Writer writer = new StringWriter();
			if (resultMsg.substring(0, 4).equals("0001")) {
				Document document = DocumentHelper.parseText(resultMsg.substring(resultMsg.indexOf("<?xml")));
				StringUtil.formateXMLStr(writer, document);
				log.debug("#######查询批次返回的信息[" + writer + "]#######");
			}
		} catch (Exception e) {
			e.printStackTrace();
			resultMsg = e.getMessage();
		}
		return resultMsg;
	}

	/**
	 * 高级搜索调用示例 -------------------------------------------------------
	 * 最后结果为组上最大版本的批次号
	 */
	public String heightQuery(String objectName, String busiStartDate, String busiSerialNo) {
		ClientHeightQuery heightQuery = new ClientHeightQuery();
		heightQuery.setUserName(userName);//admin
		heightQuery.setPassWord(passWord);//111
		heightQuery.setLimit(10);
		heightQuery.setPage(1);
		//heightQuery.setModelCode("XDKHZL");
		heightQuery.setModelCode(objectName);
		//heightQuery.addCustomAtt("BUSI_START_DATE", "20160126");
		heightQuery.addCustomAtt("BUSI_START_DATE", busiStartDate);
		//heightQuery.addCustomAtt("BUSI_SERIAL_NO", "2015121410");
		heightQuery.addCustomAtt("BUSI_SERIAL_NO", busiSerialNo);
		// heightQuery.addfilters("VARCHARTYPE='varchartype'");
		String resultMsg;
		try {
			resultMsg = clientApi.heightQuery(heightQuery, groupName);
			Writer writer = new StringWriter();
			Document document = DocumentHelper.parseText(resultMsg.substring(resultMsg.indexOf("<?xml")));
			StringUtil.formateXMLStr(writer, document);
			log.debug("#######调用高级搜索返回的信息[" + resultMsg + "]#######");
		} catch (Exception e) {
			log.warn(e.getMessage());
			resultMsg =e.getMessage();
		}
		return resultMsg;
	}

	/**
	 * 删除
	 * @param batchId 批次id
	 * @param busiStartDate 8位日期数字
	 */
	public void delete(String batchId, String busiStartDate) {
		ClientBatchBean clientBatchBean = new ClientBatchBean();
		clientBatchBean.setModelCode(MODEL_CODE);
		clientBatchBean.setPassWord(passWord);
		clientBatchBean.setUser(userName);
		clientBatchBean.getIndex_Object().setContentID(batchId);
		clientBatchBean.getIndex_Object().addCustomMap("BUSI_START_DATE",
				busiStartDate);
		// 若内容模型配置有安全校验
		// clientBatchBean.setToken_check_value(token_check_value);
		// clientBatchBean.setToken_code(tokenCode);

		try {
			String resultMsg = clientApi.delete(clientBatchBean, groupName);
			log.debug("#######删除批次返回的信息[" + resultMsg + "]#######");
		} catch (Exception e) {
			log.warn(e.getMessage());
		}

	}

	/**
	 * 更新时需要注明版本号则表示自第几个版本更新,
	 * ------------------------------------------------------- 没有版本控制则无需注明
	 * 
	 */
	public void updateExample() {
		ClientBatchBean clientBatchBean = new ClientBatchBean();
		clientBatchBean.setModelCode(MODEL_CODE);
		clientBatchBean.setUser(userName);
		clientBatchBean.setPassWord(passWord);
		clientBatchBean.getIndex_Object().setContentID(contentID);
		clientBatchBean.getIndex_Object().addCustomMap(STARTCOLUMN, STARTDATE);
		// clientBatchBean.getIndex_Object().addCustomMap("BUSI_START_TIME",
		// STARTDATE);
		// clientBatchBean.getIndex_Object().addCustomMap("UPDATETIME",
		// "20130828114548");

		// 若内容模型配置有安全校验
		clientBatchBean.setToken_check_value(token_check_value);
		clientBatchBean.setToken_code(tokenCode);
		clientBatchBean.setCheckToken(checkToken);

		ClientBatchFileBean batchFileBean = new ClientBatchFileBean();
		batchFileBean.setFilePartName(filePartName);

		// // 新增一个文件
		ClientFileBean fileBean1 = new ClientFileBean();
		fileBean1.setOptionType(OptionKey.U_ADD);
		fileBean1.setFileName("E:/image/2.jpg");
		fileBean1.setFileFormat("jpg");
		batchFileBean.addFile(fileBean1);
		// //
		//		
		// // 替换一个文件
		// ClientFileBean clientFileBean2 = new ClientFileBean();
		// clientFileBean2.setOptionType(OptionKey.U_REPLACE);
		// clientFileBean2.setFileNO("B73A7B76-96A8-1094-806F-7730DCFFC024");
		// clientFileBean2.setFileName("D:\\1.jpg");
		// clientFileBean2.setFileFormat("jpg");
		// batchFileBean.addFile(clientFileBean2);

		// 删除一个文件
		// ClientFileBean clientFileBean3 = new ClientFileBean();
		// clientFileBean3.setOptionType(OptionKey.U_DEL);
		// clientFileBean3.setFileNO("8186E0C6-FEC5-1CD5-7559-B8B6F687674F");
		// batchFileBean.addFile(clientFileBean3);
		//		 
		// ClientFileBean clientFileBean4 = new ClientFileBean();
		// clientFileBean4.setOptionType(OptionKey.U_DEL);
		// clientFileBean4.setFileNO("E114898E-9DFE-1E39-8ED4-5C36807455B4");
		// batchFileBean.addFile(clientFileBean4);

		// // 修改文档部件字段
		// ClientFileBean clientFileBean = new ClientFileBean();
		// clientFileBean.setOptionType(OptionKey.U_MODIFY);
		// clientFileBean.setFileNO("B7F0E665-EB2E-A68C-0443-333C2BC80DC4");
		// clientFileBean.addOtherAtt("IMAGEPAGEID", "1");
		// batchFileBean.addFile(clientFileBean);
		// //
		clientBatchBean.addDocument_Object(batchFileBean);
		try {
			String resultMsg = clientApi.update(clientBatchBean, groupName,
					false);
			log.debug("#######更新批次返回的信息[" + resultMsg + "]#######");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 新增批注
	 * 
	 * @throws Exception
	 */
	public void operAnnotation() {
		// 设定批次信息
		ClientBatchBean clientBatchBean = new ClientBatchBean();
		clientBatchBean.setModelCode(MODEL_CODE);
		clientBatchBean.setUser(userName);
		clientBatchBean.getIndex_Object().addCustomMap("BUSI_START_TIME",
				"20120929");
		clientBatchBean.getIndex_Object().setContentID(contentID);
		// 若内容模型配置有安全校验
		clientBatchBean.setToken_check_value(token_check_value);
		clientBatchBean.setToken_code(tokenCode);
		// 设定文档部件信息
		ClientBatchFileBean batchFileBean = new ClientBatchFileBean();
		batchFileBean.setFilePartName(filePartName);

		// 设定文件信息
		ClientFileBean clientFileBean = new ClientFileBean();
		clientFileBean.setFileNO("1BF8004E-957F-EE07-20E3-D6066AF85B99");

		// 追加批注信息
		ClientAnnotationBean annotationBean = new ClientAnnotationBean();
		annotationBean.setAnnotation_id(annoID);
		annotationBean.setAnnotation_flag(OptionKey.U_ADD);
		annotationBean.setAnnotation_x1pos("1");
		annotationBean.setAnnotation_y1pos("1");
		annotationBean.setAnnotation_x2pos("100");
		annotationBean.setAnnotation_y2pos("5");
		annotationBean.setAnnotation_content("内容模型批注");
		annotationBean.setAnnotation_color("red");

		clientFileBean.addAnnoList(annotationBean); // 批注关联文件
		batchFileBean.addFile(clientFileBean); // 文件关联文档部件
		clientBatchBean.addDocument_Object(batchFileBean); // 文档部件关联批次

		try {
			String resultMsg = clientApi.operAnnotation(clientBatchBean,
					groupName);
			log.debug("#######批注操作返回的信息[" + resultMsg + "]#######");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 查询批注
	 * 
	 * @throws Exception
	 */
	public void queryAnnotation() {
		ClientBatchBean clientBatchBean = new ClientBatchBean();
		clientBatchBean.setModelCode(MODEL_CODE);
		clientBatchBean.setUser(userName);
		clientBatchBean.getIndex_Object().setVersion("1");
		clientBatchBean.getIndex_Object()
				.addCustomMap("START_TIME", "20120612");
		clientBatchBean.getIndex_Object().setContentID(contentID);

		// 若内容模型配置有安全校验
		clientBatchBean.setToken_check_value(token_check_value);
		clientBatchBean.setToken_code(tokenCode);

		// 设定文档部件信息
		ClientBatchFileBean batchFileBean = new ClientBatchFileBean();
		batchFileBean.setFilePartName(filePartName);

		// 设定文件信息
		ClientFileBean clientFileBean = new ClientFileBean();
		clientFileBean.setFileNO(fileNO1);

		batchFileBean.addFile(clientFileBean); // 文件关联文档部件
		clientBatchBean.addDocument_Object(batchFileBean); // 文档部件关联批次

		try {
			String resultMsg = clientApi.queryAnnotation(clientBatchBean,
					groupName);
			log.debug("#######查询批注返回的信息[" + resultMsg + "]#######");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 客户端获得内容模型权限获取 -------------------------------------------------------
	 * */
	public void getPermissions_Client() {
		try {
			String resultMsg = clientApi.getPermissions_Client(userName,
					passWord);
			log.debug("#######客户端获得内容模型权限获取返回的信息[" + resultMsg + "]#######");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 客户端获取所有服务器信息
	 * 
	 * */
	public void getContentServerInfo_Client() {
		try {
			String resultMsg = clientApi.getContentServerInfo_Client();
			log.debug("#######客户端获取所有服务器返回的信息[" + resultMsg + "]#######");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 客户端获得获取内容模型列表信息
	 * */
	public void getAllModelMsg_Client() {
		try {
			String resultMsg = clientApi.getAllModelMsg_Client();
			log.debug("#######客户端获得获取内容模型列表返回的信息[" + resultMsg + "]#######");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 客户端获取内容模型模版
	 * */
	public void getModelTemplate_Client() {

		String[] modeCodes = { MODEL_CODE };
		try {
			String resultMsg = clientApi.getModelTemplate_Client(modeCodes);
			log.debug("#######客户端获取内容模型模版返回的信息[" + resultMsg + "]#######");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 客户端获取内容存储服务器信息
	 * */
	public void inquireDMByGroup() {
		try {
			String resultMsg = clientApi.inquireDMByGroup(userName, groupName);
			log.debug("#######客户端获取内容存储服务器信息[" + resultMsg + "]#######");
		} catch (Exception e) {
			log.error(e.getMessage());
		}
	}

	/**
	 * 客户端获取令牌，调用WEBSERVICE
	 * */
	public void getToken() {
		try {
			String oper = OperPermission.ADD + "," + OperPermission.UPD + ","
					+ OperPermission.DEL + "," + OperPermission.QUY + ","
					+ OperPermission.MAK;

			String resultMsg = clientApi.getToken(
					"http://172.16.3.32:9080/SunECMConsole", token_check_value,
					userName, oper);
			log.debug("#######客户端获取令牌返回的信息[" + resultMsg + "]#######");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void queryAndDownload() {
		ClientBatchBean clientBatchBean = new ClientBatchBean();
		clientBatchBean.setModelCode(MODEL_CODE);
		clientBatchBean.setUser(userName);
		// clientBatchBean.getIndex_Object().setVersion("2");
		clientBatchBean.getIndex_Object().setContentID(contentID);
		clientBatchBean.getIndex_Object()
				.addCustomMap("START_TIME", "20130821");
		clientBatchBean.setDownLoad(true);
		// 若内容模型配置有安全校验
		clientBatchBean.setToken_check_value(token_check_value);
		clientBatchBean.setToken_code(tokenCode);

		try {
			String resultMsg = clientApi.queryBatch(clientBatchBean, groupName);
			log.debug("#######查询批次返回的信息[" + resultMsg + "]#######");
			String batchStr = resultMsg.split(TransOptionKey.SPLITSYM)[1];

			// List<BatchBean> batchBeans =
			// XMLUtil.xml2list(XMLUtil.removeHeadRoot(batchStr),
			// BatchBean.class);
			// for (BatchBean batchBean : batchBeans) {
			// List<BatchFileBean> fileBeans = batchBean.getDocument_Objects();
			// for (BatchFileBean batchFileBean : fileBeans) {
			// List<FileBean> files = batchFileBean.getFiles();
			// for (FileBean fileBean : files) {
			// String urlStr = fileBean.getUrl();
			// String fileName = fileBean.getFileNO() + "-" +
			// Thread.currentThread().getId() + "." + fileBean.getFileFormat();
			// log.debug("#######文件访问链接为[" + urlStr + "], 文件名为["+ fileName
			// +"]#######");
			// receiveFileByURL(urlStr, fileName);
			// }
			// }
			// }

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void receiveFileByURL(String urlStr, String fileName) {
		File file = new File("E:\\testfiles\\urlFile\\" + fileName);
		URL url;
		InputStream in = null;
		FileOutputStream fos = null;
		try {
			url = new URL(urlStr);
			in = url.openStream();
			fos = new FileOutputStream(file);
			if (in != null) {
				byte[] b = new byte[1024];
				int len = 0;
				while ((len = in.read(b)) != -1) {
					fos.write(b, 0, len);
				}
			}
		} catch (FileNotFoundException e) {
			log.error("unitedaccess http -- GetFileServer: " + e.toString());
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				log
						.error("unitedaccess http -- GetFileServer: "
								+ e.toString());
			}
			try {
				fos.close();
			} catch (IOException e) {
				log
				.error("unitedaccess http -- GetFileServer: "
						+ e.toString());
			}
		}
	}

	/**
	 * 客户端调用立即迁移接口 -------------------------------------------------------
	 */
	public void immedMigrate(String batchId) {
		MigrateBatchBean m = new MigrateBatchBean();
		m.setMigrate(true);
		m.setModelCode(MODEL_CODE);
		// m.setUser(userName);
		m.setUserName(userName);
		m.setPassWord(passWord);
		m.getIndex_Object().setContentID(batchId);
		m.getIndex_Object().addCustomMap(STARTCOLUMN, STARTDATE);
		try {
			String resultMsg = clientApi.immedMigrate(m, groupName);
			log.debug("#######返回的信息[" + resultMsg + "]#######");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void queryNodeInfoByGroupIdAndInsNo() {
		try {
			String resultMsg = clientApi.queryNodeInfoByGroupIdAndInsNo(
					"FTC_IDX", "jigou");
			log.debug("#######返回的信息[" + resultMsg + "]#######");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	// 多线程客户端
	private Runnable createTask(final int taskID) {
		return new Runnable() {
			@Override
			public void run() {
				System.out.println("现在进行第[" + taskID + "]个任务");
				// getToken();
				// checkOut(); //88d29fee522dbf173337
//				login();
				for (int i = 0; i < 1; i++) {
//					 updateExample();
//					 uploadExample();
				}
				// checkIn();

				// queryAndDownload();

				// immedMigrate(contentID);
				// inquireDMByGroup();
//				 heightQueryExample();
			login();
//				 queryExample(contentID);
				// operAnnotation();
				// queryAnnotation();
				// deleteExample();
				// getAllModelMsg_Client();
				// getContentServerInfo_Client();
				// getModelTemplate_Client();
				// getPermissions_Client();
				// logout();

				// queryNodeInfoByGroupIdAndInsNo();
			}
		};
	}


	public static byte[] getLength(int source,String length){
		String dataLength = String.format("%1$0"+length+"d",source);
		return dataLength.getBytes();
	}

	public static void main(String[] args) {
		SunEcmClient client = new SunEcmClient();
		//client.getAllModelMsg_Client();
		//client.getPermissions_Client();
		client.login();
		//client.uploadExample();
		//client.test();
		//System.out.println(client.heightQueryExample("XDKHZL","20081015","10001"));
		ArrayList<String> arrayList = new ArrayList<String>();
		arrayList.add("D:/qr.png");
		//client.upload(arrayList);
		//client.query("20180323_12_639_8808B7A0-88DA-EA7C-9813-A2C213879481-1", MODEL_CODE, "20180323");
		//client.delete();
		client.logout();
	}
}
