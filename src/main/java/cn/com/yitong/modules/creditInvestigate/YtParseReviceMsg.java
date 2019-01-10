package cn.com.yitong.modules.creditInvestigate;

import java.io.File;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpStatus;
import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.xml.sax.InputSource;

import com.jcraft.jsch.JSchException;

import cn.com.yitong.common.utils.SpringContextUtils;
import cn.com.yitong.consts.AppConstants;
import cn.com.yitong.framework.net.impl.credit.CreditConst;
import cn.com.yitong.framework.service.ICrudService;
import cn.com.yitong.framework.servlet.ServerInit;
import cn.com.yitong.util.SunEcmClient;
import cn.com.yitong.util.YTLog;
public class YtParseReviceMsg {
	//本地存放文件信息的路径
	public static final String upload_files_path_yxInfo = ServerInit.getString("upload_files_path_yxInfo");
	public static final String upload_files_path_financeReport = ServerInit.getString("upload_files_path_financeReport");
	public static final String upload_files_path_bussDocumentInfo = ServerInit.getString("upload_files_path_bussDocumentInfo");
	public static final String upload_files_path_surveyReport = ServerInit.getString("upload_files_path_surveyReport");
	public static final String FTP_DOWN_HOST = ServerInit.getString("FTP_DOWN_HOST");
	public static final int FTP_DOWN_PORT = ServerInit.getInt("FTP_DOWN_PORT");
	public static final String FTP_DOWN_USER_NAME = ServerInit.getString("FTP_DOWN_USER_NAME");
	public static final String FTP_DOWN_USER_PW = ServerInit.getString("FTP_DOWN_USER_PW");
	private static ICrudService service = SpringContextUtils.getBean(ICrudService.class);
	private static SunEcmClient client = SpringContextUtils.getBean(SunEcmClient.class);
	private static Logger logger = YTLog.getLogger(YtParseReviceMsg.class);
	private static SqlSession sqlSession = SpringContextUtils.getBean(SqlSession.class);
	/**
	 * 处理返回报文的方法
	 * @param flag区别接口类型	
	 * @param rst
	 * @param reviceMsg 返回报文
	 * @throws UnsupportedEncodingException 
	 */
	public static void parseMessege(Map<String, Object> rst, String reviceMsg, String flag) throws UnsupportedEncodingException {
		logger.info("response message is:\n" + reviceMsg);
		StringReader read = new StringReader(reviceMsg);
		InputSource source = new InputSource(read);
		SAXReader saxReader = new SAXReader();
		Document document = null;
		try {
			document =saxReader.read(source);
		} catch (DocumentException e1) {
			e1.printStackTrace();
		}
		Element rootElm =  document.getRootElement();
		Element serviceHeader = rootElm.element("Service_Header");
		Element serviceResponse =  serviceHeader.element("service_response");
		String status    = serviceResponse.element("status").getText();
		String code    = serviceResponse.element("code").getText();
		String desc    = serviceResponse.element("desc").getText();
		if (!CreditConst.RS_COMPLETE.equalsIgnoreCase(status)) {
			rst.put(AppConstants.STATUS, AppConstants.STATUS_FAIL);
			rst.put(AppConstants.MSG, desc);
			return;
		}
		rst.put(AppConstants.STATUS, AppConstants.STATUS_OK);
		rst.put(AppConstants.MSG, AppConstants.MSG_SUCC);
		Element total = (Element) rootElm.selectSingleNode("Service_Body/response/total");
		if (total != null) {
			rst.put("total", total.getText());
		}
		List<Map<String,Object>> resultList = new ArrayList<Map<String,Object>>();
		Element serviceBody = rootElm.element("Service_Body");
		if(code.equals("0000")&&flag.equals("waittingDoneOut")){//代办
			List<Element> responseList = serviceBody.element("response").element("list").elements("item");
			for(Element e : responseList){
				Map<String,Object> newMap = new HashMap<String,Object>();
				newMap.put("serialNo",e.element("serialNo").getText());
				newMap.put("applySerialNo",e.element("applySerialNo").getText());
				newMap.put("customerID",e.element("customerID").getText());
				newMap.put("customerName",e.element("customerName").getText());
				newMap.put("inputUserName",e.element("inputUserName").getText());
				newMap.put("inputOrgName",e.element("inputOrgName").getText());
				newMap.put("businessName",e.element("businessName").getText());
				newMap.put("beginTime",e.element("beginTime").getText());
				newMap.put("updateDate",e.element("updateDate").getText());
				newMap.put("flowNo",e.element("flowNo").getText());
				newMap.put("phaseNo",e.element("phaseNo").getText());
				resultList.add(newMap);
			}
		}else if(code.equals("0000")&&flag.equals("alreadyDone")){//已办
			List<Element> responseList = serviceBody.element("response").element("list").elements("item");
			for(Element e : responseList){
				Map<String,Object> newMap = new HashMap<String,Object>();
				newMap.put("serialNo",e.element("serialNo").getText());
				newMap.put("applySerialNo",e.element("applySerialNo").getText());
				newMap.put("customerID",e.element("customerID").getText());
				newMap.put("customerName",e.element("customerName").getText());
				newMap.put("inputUserName",e.element("inputUserName").getText());
				newMap.put("inputOrgName",e.element("inputOrgName").getText());
				newMap.put("businessName",e.element("businessName").getText());
				newMap.put("beginTime",e.element("beginTime").getText());
				newMap.put("updateDate",e.element("updateDate").getText());
				newMap.put("flowNo",e.element("flowNo").getText());
				newMap.put("phaseNo",e.element("phaseNo").getText());
				resultList.add(newMap);
			}
		}else if(code.equals("0000")&&flag.equals("showSign")){//是否显示签署意见
			List<Element> responseList = serviceBody.element("response").element("list").elements("item");
			for(Element e : responseList){
				Map<String,Object> newMap = new HashMap<String,Object>();
				newMap.put("displaySignOpinion",e.element("displaySignOpinion").getText());
				resultList.add(newMap);
			}
		}else if(code.equals("0000")&&flag.equals("creditShowJudgeSign")){
			List<Element> responseList = serviceBody.element("response").element("list").elements("item");
				for(Element e : responseList){
					Map<String,Object> newMap = new HashMap<String,Object>();
					newMap.put("displaySpecialOpinion",e.element("displaySpecialOpinion").getText());
					newMap.put("specialOpinionHeader",e.element("specialOpinionHeader").getText());
					newMap.put("phaseOpinionValue",e.element("phaseOpinionValue").getText());
					newMap.put("specialOpinionValue",e.element("specialOpinionValue").getText());
					List<Element> specialOpinion=e.element("specialOpinion").elements("string");
					int u=specialOpinion.size();
					for(int i=0;i<specialOpinion.size();i++){
						String opinion=specialOpinion.get(i).getText();
						if(opinion.equalsIgnoreCase("1"))
							newMap.put("1","同意");
						if(opinion.equalsIgnoreCase("2"))
							newMap.put("2","有条件同意");
						if(opinion.equalsIgnoreCase("3"))
							newMap.put("3","续议");
						if(opinion.equalsIgnoreCase("4"))
							newMap.put("4","否决");
					}
	                resultList.add(newMap);
				}
		}
		else if(code.equals("0000")&&flag.equals("invReport")){
			List<Element> responseList = serviceBody.element("response").elements("item");
			String applySerialNo = serviceBody.element("request").element("apply_serialno").getTextTrim();
			for(Element e : responseList){
				Map<String,Object> newMap = new HashMap<String,Object>();
				//String localPath ="D:"+File.separator+"opt"+File.separator+"tomcat7"+File.separator+"upload-files"+File.separator+"yxInfo"+File.separator+serialNo+File.separator;
				File parentPath = new File(upload_files_path_surveyReport+applySerialNo+File.separator);
				if(!parentPath.exists()) {
					parentPath.mkdirs();
				}
				newMap.put("formatdoc__url",e.element("formatdoc__url").getText());
				//String ceshiStr =  "/home/oracle/0A0DC8CFF64887C7CDAAD0E9166C6F53.html";
				String  ceshiStr=e.element("formatdoc__url").getText();
				if(ceshiStr!=null&&!ceshiStr.equals("")){
					String fullPathName = ceshiStr.substring(ceshiStr.lastIndexOf("/")+1);
					String fullPath="";
					try {
						String localPath = parentPath.getAbsolutePath() + File.separator + fullPathName;
						if (!new File(localPath).exists()) {
							SftpUtil.downloadSftpFile(FTP_DOWN_HOST, FTP_DOWN_USER_NAME, FTP_DOWN_USER_PW, FTP_DOWN_PORT, ceshiStr,
									localPath, newMap);
						}
						//fullPath = "http://198.198.200.115:7000/mmp/download/userResource/surveyReport.do?fileName="+fullPathName+"&number="+applySerialNo;
						fullPath = "http://36.32.192.100:8080/mmp/download/userResource/surveyReport.do?fileName="+fullPathName+"&number="+applySerialNo;
						//fullPath = "http://36.32.192.100:8080/mmp/download/userResource/surveyReport.do?fileName=0A0DC8CFF64887C7CDAAD0E9166C6F53.html&number=2016122700000026";
						if(newMap.get("fullPath")!=null &&newMap.get("fullPath").equals("error")){
							newMap.put("fullPath", "");
						}else{
							newMap.put("fullPath", fullPath);
						}
					} catch (JSchException e1) {
						newMap.put("fullPath", "");
						e1.printStackTrace();
					}
				}else{
					newMap.put("fullPath", "");
				}
				resultList.add(newMap);
			}
		}
		else if(code.equals("0000")&&flag.equals("custInfoOut")){//客户信息概况
			List<Element> responseList = serviceBody.element("response").element("list").elements("item");
			for(Element e : responseList){
				Map<String,Object> newMap = new HashMap<String,Object>();
				newMap.put("serialNo",e.element("serialNo").getText());
				newMap.put("enterpriseName",e.element("enterpriseName").getText());
				newMap.put("industryType",e.element("industryType").getText());
				newMap.put("customerID",e.element("customerID").getText());
				newMap.put("corpID",e.element("corpID").getText());
				newMap.put("certDate",e.element("certDate").getText());
				newMap.put("licenseNo",e.element("licenseNo").getText());
				newMap.put("orgNature",e.element("orgNature").getText());
				newMap.put("licenseDate",e.element("licenseDate").getText());
				newMap.put("licenseMaturity",e.element("licenseMaturity").getText());
				newMap.put("registerAdd",e.element("registerAdd").getText());
				newMap.put("mostBusiness",e.element("mostBusiness").getText());
				newMap.put("registerCapital",e.element("registerCapital").getText());
				newMap.put("paiclUpCapital",e.element("paiclUpCapital").getText());
				newMap.put("orgType",e.element("orgType").getText());
				newMap.put("listingCorpOrNot",e.element("listingCorpOrNot").getText());
				newMap.put("captType",e.element("captType").getText());
				newMap.put("isGroup",e.element("isGroup").getText());
				newMap.put("hasIERight",e.element("hasIERight").getText());
				newMap.put("isHghTec",e.element("isHghTec").getText());
				newMap.put("isGFRZ",e.element("isGFRZ").getText());
				resultList.add(newMap);
			}	
		}else if(code.equals("0000")&&flag.equals("custLegalRepOut")){//客户法人 
			List<Element> responseList = serviceBody.element("response").element("list").elements("item");
			for(Element e : responseList){
				Map<String,Object> newMap = new HashMap<String,Object>();
				newMap.put("customerName",e.element("customerName").getText());
				resultList.add(newMap);
			}	
		}else if(code.equals("0000")&&flag.equals("custStockHolder")){//客户股东
			List<Element> responseList = serviceBody.element("response").element("list").elements("item");
			for(Element e : responseList){
				Map<String,Object> newMap = new HashMap<String,Object>();
				//newMap.put("serialNo",e.element("serialNo").getText());
				newMap.put("customerName",e.element("customerName").getText());
				newMap.put("relationShip",e.element("relationShip").getText());
				newMap.put("investmentProp",e.element("investmentProp").getText());
				resultList.add(newMap);
			}	
		}else if(code.equals("0000")&&flag.equals("custExecutives")){//客户高管
			List<Element> responseList = serviceBody.element("response").element("list").elements("item");
			for(Element e : responseList){
				Map<String,Object> newMap = new HashMap<String,Object>();
				newMap.put("customerName",e.element("customerName").getText());
				newMap.put("relationShip",e.element("relationShip").getText());
				newMap.put("certType",e.element("certType").getText());			
				resultList.add(newMap);
			}	
		}else if(code.equals("0000")&&flag.equals("baseInfoOut")){//申请基本信息
			List<Element> responseList = serviceBody.element("response").element("list").elements("item");
			for(Element e : responseList){
				Map<String,Object> newMap = new HashMap<String,Object>();
				newMap.put("serialNo",e.element("serialNo").getText());
				newMap.put("occurDate",e.element("occurDate").getText());
				newMap.put("customerID",e.element("customerID").getText());
				newMap.put("customerName",e.element("customerName").getText());
				newMap.put("businessName",e.element("businessName").getText());
				newMap.put("businessCurrency",e.element("businessCurrency").getText());
				newMap.put("businessSum",e.element("businessSum").getText());
				newMap.put("termMonth",e.element("termMonth").getText());
				newMap.put("isVouchCompany",e.element("isVouchCompany").getText());
				newMap.put("vouchCompany",e.element("vouchCompany").getText());
				newMap.put("vouchTreatyNo",e.element("vouchTreatyNo").getText());
				newMap.put("vouchType",e.element("vouchType").getText());
				newMap.put("vouchFlag",e.element("vouchFlag").getText());
				newMap.put("vouchType1",e.element("vouchType1").getText());
				newMap.put("operateUserName",e.element("operateUserName").getText());
				newMap.put("operateOrgName",e.element("operateOrgName").getText());
				newMap.put("inputUserName",e.element("inputUserName").getText());
				newMap.put("inputOrgName",e.element("inputOrgName").getText());
				newMap.put("inputDate",e.element("inputDate").getText());
				newMap.put("updateDate",e.element("updateDate").getText());
				resultList.add(newMap);
			}
		}else if(code.equals("0000")&&flag.equals("amountAllocateOut")){
			List<Element> responseList = serviceBody.element("response").element("list").elements("item");
			for(Element e : responseList){
				Map<String,Object> newMap = new HashMap<String,Object>();
				newMap.put("serialNo",e.element("serialNo").getText());
				newMap.put("divideType",e.element("divideType").getText());
				newMap.put("divideName",e.element("divideName").getText());
				newMap.put("currency",e.element("currency").getText());
				newMap.put("businessSum",e.element("businessSum").getText());
				newMap.put("cycleFlag",e.element("cycleFlag").getText());
				newMap.put("maxTermDate",e.element("maxTermDate").getText());
				newMap.put("gjRate",e.element("gjRate").getText());
				newMap.put("minBusinessRate",e.element("minBusinessRate").getText());
				newMap.put("minBailSum",e.element("minBailSum").getText());
				newMap.put("otherDescribe",e.element("otherDescribe").getText());
				newMap.put("inputDate",e.element("inputDate").getText());
				newMap.put("inputUserName",e.element("inputUserName").getText());
				newMap.put("inputOrgName",e.element("inputOrgName").getText());
				newMap.put("updateDate",e.element("updateDate").getText());
				resultList.add(newMap);
			}
		}else if(code.equals("0000")&&flag.equals("amountAssuranceOut")){
			List<Element> responseList = serviceBody.element("response").element("list").elements("item");
			for(Element e : responseList){
				Map<String,Object> newMap = new HashMap<String,Object>();
				newMap.put("serialNo",e.element("serialNo").getText());
				newMap.put("guarantyType",e.element("guarantyType").getText());
				newMap.put("guarantorName",e.element("guarantorName").getText());
				newMap.put("guarantyCurrency",e.element("guarantyCurrency").getText());
				newMap.put("guarantyValue",e.element("guarantyValue").getText());
				newMap.put("inputUserName",e.element("inputUserName").getText());
				newMap.put("inputOrgName",e.element("inputOrgName").getText());
				resultList.add(newMap);
			}
		}else if(code.equals("0000")&&flag.equals("bussDocumentOut")){//业务文档
			//String serialNo = serviceBody.element("request").element("serial_no").getTextTrim();
			Element ee = serviceBody.element("request");
			Element ee3 =ee.element("serial_no");
			String serialNo =ee3.getTextTrim();
			List<Element> responseList = serviceBody.element("response").elements("item");
			for(Element e : responseList){
				Map<String,Object> newMap = new HashMap<String,Object>();
				newMap.put("docTitle",e.element("docTitle").getText());
				newMap.put("docType",e.element("docType").getText());
				newMap.put("docDate",e.element("docDate").getText());
				newMap.put("userName",e.element("userName").getText());
				newMap.put("orgName",e.element("orgName").getText());
				newMap.put("inputTime",e.element("inputTime").getText());
				newMap.put("updateTime",e.element("updateTime").getText());
				newMap.put("serialNo", serialNo);
				String fullPath = e.element("fullPath").getText();
				String docNo = e.element("docNo").getText();//文档编号
				newMap.put("docNo", docNo);
				String attachmentNo = e.element("attachmentNo").getText();//文档序号
				newMap.put("attachmentNo", attachmentNo);
				if(fullPath!=null&&!fullPath.equals("")){
					String fullPathName = docNo + attachmentNo + fullPath.substring(fullPath.lastIndexOf("."));
					//信贷系统给的文件全路径
					//String ftpPath = "/opt/tomcat7/upload-files/yxInfo/222222.pdf";
					//本地存放文件的路径
					//String localPath ="D:"+File.separator+"opt"+File.separator+"tomcat7"+File.separator+"upload-files"+File.separator+"yxInfo"+File.separator+serialNo+File.separator;
					File parentPath = new File(upload_files_path_bussDocumentInfo+serialNo+File.separator);
					if(!parentPath.exists()) {
						parentPath.mkdirs();
					}
					newMap.put("absolutePath", parentPath.getPath() + File.separator + fullPathName);
					//	SftpUtil.downloadSftpFile(FTP_DOWN_HOST, FTP_DOWN_USER_NAME,FTP_DOWN_USER_PW,FTP_DOWN_PORT,   fullPath, parentPath.getAbsolutePath()+File.separator+fullPathName, "11",newMap);
					Map<String, Object> selectOne = sqlSession.selectOne("credit.queryById", newMap);
					if (selectOne != null && !selectOne.get("UPDATE_TIME").equals(newMap.get("updateTime"))) {
						sqlSession.update("credit.updateById", newMap);
						int statusCode = SftpUtil.downloadFileTolocal(parentPath.getPath() + File.separator + fullPathName, docNo, attachmentNo);
						if (statusCode != HttpStatus.SC_OK) {
							continue;
						}
					} else if (selectOne == null) {
						int statusCode = SftpUtil.downloadFileTolocal(parentPath.getPath() + File.separator + fullPathName, docNo, attachmentNo);
						if (statusCode != HttpStatus.SC_OK) {
							continue;
						}
					}
					//客户端会从mmp截取
					fullPath = "/mmp/download/userResource/bussDocumentInfo.do?fileName=" + fullPathName + "&number=" + serialNo;
						newMap.put("fullPath", fullPath);
					if (selectOne == null) {
						sqlSession.insert("credit.insert", newMap);
					}
				}else{
					newMap.put("fullPath", "");
				}

				resultList.add(newMap);
			}
		}else if(code.equals("0000")&&flag.equals("allOpitionOut")){
			List<Element> responseList = serviceBody.element("response").element("list").elements("item");
			for(Element e : responseList){
				Map<String,Object> newMap = new HashMap<String,Object>();
				newMap.put("customerName",e.element("customerName").getText());
				newMap.put("businessCurrency",e.element("businessCurrency").getText());
				newMap.put("businessSum",e.element("businessSum").getText());
				newMap.put("termMonth",e.element("termMonth").getText());
				newMap.put("phaseName",e.element("phaseName").getText());
				newMap.put("userName",e.element("userName").getText());
				newMap.put("orgName",e.element("orgName").getText());
				newMap.put("beginTime",e.element("beginTime").getText());
				newMap.put("endTime",e.element("endTime").getText());
				newMap.put("mrPhaseOpinion",e.element("mrPhaseOpinion").getText());
				newMap.put("phaseOpinion",e.element("phaseOpinion").getText());
				resultList.add(newMap);
			}
		}else if(code.equals("0000")&&flag.equals("currentOpitionsOut")){
			List<Element> responseList = serviceBody.element("response").element("list").elements("item");
			for(Element e : responseList){
				Map<String,Object> newMap = new HashMap<String,Object>();
				newMap.put("serialNo",e.element("serialNo").getText());
				newMap.put("phaseOpinion1",e.element("phaseOpinion1").getText());
				resultList.add(newMap);
			}
		}else if(code.equals("0000")&&flag.equals("nextInfoOut")){
			String serialNo = serviceBody.element("response").element("list").element("Action").element("serialNo").getTextTrim();
			rst.put("serialNo", serialNo);
			String nextPhaseNo = serviceBody.element("response").element("list").element("Action").element("nextPhaseNo").getTextTrim();
			rst.put("nextPhaseNo", nextPhaseNo);
			String nextPhaseName = serviceBody.element("response").element("list").element("Action").element("nextPhaseName").getTextTrim();
			rst.put("nextPhaseName", nextPhaseName);
			String isMultiSelect = serviceBody.element("response").element("list").element("Action").element("isMultiSelect").getTextTrim();
			String roleIDs = serviceBody.element("response").element("list").element("Action").element("roleIDs").getTextTrim();
			rst.put("roleIDs", roleIDs);
			rst.put("isMultiSelect", isMultiSelect);//true 为多选
			List<Element> responseList = serviceBody.element("response").element("list").elements("item");
			for(Element e : responseList){
				Map<String,Object> newMap = new HashMap<String,Object>();
				newMap.put("phaseAction",e.element("phaseAction").getText());
				resultList.add(newMap);
			}
		}else if(code.equals("0000")&&flag.equals("keepAdivice")){//保存意见
			List<Element> responseList = serviceBody.element("response").element("list").elements("item");
			String saveResult = responseList.get(0).element("saveResult").getTextTrim();
			Map<String,Object> newMap = new HashMap<String,Object>();
			if("Success".equals(saveResult)){
				newMap.put("saveResult", "Success");
			}else{
				newMap.put("saveResult", "Fault");
			}
			resultList.add(newMap);
		}else if(code.equals("0000")&&flag.equals("yxPicInfoOut")){//影像接口
			String serialNo = serviceBody.element("request").element("serial_no").getTextTrim();
			List<Element> responseList = serviceBody.element("response").element("item").element("infoList").elements("string");
 			for(Element e : responseList){
				Map<String,Object> newMap = new HashMap<String,Object>();
				String[] strList = e.getText().split(";");
				String busiSerialNo=strList[0].split(":")[1];
				String objectName= strList[1].split(":")[1];
				String busiStartDate=strList[2].split(":")[1];
				String objectRemark ="noRemark";
				for(String str :strList){
					if("OBJECT_REMARK".equals(str.split(":")[0])){
						try {
							objectRemark = URLDecoder.decode(str.split(":")[1],"UTF-8");
						} catch (UnsupportedEncodingException e1) {
							e1.printStackTrace();
						}
					}
				}
				Map<String,String> findListMap =new HashMap<String,String>();
				findListMap.put("OBJECT_NAME_EN", objectName);
				List<Map<String,String>> listMap = service.findList("TRANS_DATA.findList", findListMap);
				if(null!=listMap&&listMap.size()>0){
					String fristNodeName = listMap.get(0).get("MODEL_NAME");
					if(!"noRemark".equals(objectRemark)){
						fristNodeName+="("+objectRemark+")";
					}else{
						fristNodeName+="("+busiSerialNo+")";
					}
					newMap.put("fristNodeName",fristNodeName);
				}else{
					newMap.put("fristNodeName", "");
				}
				newMap.put("busiSerialNo",busiSerialNo);
				newMap.put("busiStartDate",busiStartDate);
				newMap.put("objectName",objectName);
				//有数据第一行放开，第二行注释
				String fristNodeStr = client.heightQuery(objectName, busiStartDate, busiSerialNo);
				//String fristNodeStr = client.heightQueryExample("XDKHZL","20081015","10001");
				fristNodeStr = fristNodeStr.substring(fristNodeStr.indexOf("<?xml"));
				Map<String,String> heightQueryExampleMap = parseYxFangHuiMess(fristNodeStr,objectName,serialNo);
				boolean flag1 =true;
				if(null ==heightQueryExampleMap){ 
					newMap.put("contentId", "nothing");
					resultList.add(newMap);
					continue;
				}else{
					newMap.put("contentId", heightQueryExampleMap.get("contentId"));
				}
				//有数据第一行放开，第二行注释
				String queryExampleStr = client.query(heightQueryExampleMap.get("contentId").toString(), objectName, busiStartDate);
				//String queryExampleStr = client.queryExample("20170223_12_913_D0E347EA-0BEB-3017-11AB-039F4902845A-1","XDKHZL","20081015");
				queryExampleStr = queryExampleStr.substring(queryExampleStr.indexOf("<?xml"));
				//有数据第一行放开，第二行注释
				Map<String,Object> queryExampleMap = parseYxFangHuiMess1(queryExampleStr,objectName,serialNo,heightQueryExampleMap.get("contentId").toString(),busiStartDate);
				//Map<String,Object> queryExampleMap = parseYxFangHuiMess1(queryExampleStr,"XDKHZL","10001","20170223_12_913_D0E347EA-0BEB-3017-11AB-039F4902845A-1","20081015");
				List<Map<String,Object>> lastList = new ArrayList<Map<String,Object>>();
				Iterator<Entry<String, Object>> it = queryExampleMap.entrySet().iterator();
				while(it.hasNext()){ 
					Entry<String, Object> itEntry = it.next(); 
					lastList.addAll((List<Map<String, Object>>)itEntry.getValue());
				}
				newMap.put("objectCode", lastList);
				resultList.add(newMap);
			}
		}else if(code.equals("0000")&&flag.equals("submitActionOut")){
			String submitResult = serviceBody.element("response").element("list").element("item").element("submitReturnValue").getText();
			Map<String,Object> newMap = new HashMap<String,Object>();
			if("Working".equals(submitResult)){
				newMap.put("resultMsg", "已经提交过，无需再提交");
			}else if("Success".equals(submitResult)){
				newMap.put("resultMsg", "提交成功");
			}else if("Failure".equals(submitResult)){
				newMap.put("resultMsg", "提交失败,请从新提交");
			}
			resultList.add(newMap);
		}
		rst.put("LIST", resultList); 
	}

	/*
	 * 解析影像报文t
	 */
	public static Map parseYxFangHuiMess(String str,String objectName,String serialNo){
		Map<String,String> map = new HashMap<String,String>();
		StringReader read = new StringReader(str);
		InputSource source = new InputSource(read);
		SAXReader saxReader = new SAXReader();
		Document document = null;
		try {
			document =saxReader.read(source);
		} catch (DocumentException e1) {
			e1.printStackTrace();
		}
		Element rootElm =  document.getRootElement();
		Element heightQueryElm = rootElm.element("HeightQuery");
		Element customAttElm = heightQueryElm.element("customAtt");
		map.put("busiStartDate",customAttElm.element("BUSI_START_DATE").element("string").getText());
		map.put("busiSerialNo",customAttElm.element("BUSI_SERIAL_NO").element("string").getText());
		map.put("groupId",heightQueryElm.element("groupId").getText());
		Element indexBeansElm =  heightQueryElm.element("indexBeans");
		if(null ==indexBeansElm){
			 return null;
		}
		Element batchIndexBeanElm =  indexBeansElm.element("BatchIndexBean");
		String contentId = batchIndexBeanElm.attribute("CONTENT_ID").getText();
		map.put("contentId", contentId);
		Element customMapElm = batchIndexBeanElm.element("customMap");
		map.put("amount",customMapElm.element("AMOUNT").element("string").getText());	
		return map;
	}
	/*
	 * 解析影像报文t
	 */
	@SuppressWarnings("unchecked")
	public static Map<String,Object> parseYxFangHuiMess1(String str,String objectName,String serialNo,String contentId,String busiStartDate){
		StringReader read = new StringReader(str);
		InputSource source = new InputSource(read);
		SAXReader saxReader = new SAXReader();
		Document document = null;
		try {
			document =saxReader.read(source);
		} catch (DocumentException e1) {
			e1.printStackTrace();
		}
		Element rootElm =  document.getRootElement();
		Element filesElm = rootElm.element("BatchBean").element("document_Objects").element("BatchFileBean").element("files");
		List<Element> fileBean = filesElm.elements("FileBean");
		//List<Map<String,Object>> fullPaths = new ArrayList<Map<String,Object>>();
		Map<String,Object> fileBeanMap = new HashMap<String,Object>();
		int len=fileBean.size();
		for(int y=0;y<fileBean.size();y++){
			Map<String,Object> fileBeanMap1 = new HashMap<String,Object>();
			String codeString = fileBean.get(y).element("otherAtt").element("BUSI_FILE_TYPE").element("string").getText();
			Map<String,Object> map1 = new HashMap<String,Object>();
			map1.put("OBJECT_NAME_EN", objectName);
			map1.put("FILE_TYPE_BARCODE", codeString);
			List<Map<String,Object>> listMap = service.findList("TRANS_DATA.findList", map1);
			String fileTypeName ="";
			if(null !=listMap&&listMap.size()>0){
				fileTypeName = listMap.get(0).get("FILE_TYPE_NAME").toString();
			}else{
				fileTypeName = "其他资料";
			}
			fileBeanMap1.put("fileTypeName", fileTypeName);
			fileBeanMap1.put("objectName", objectName);
			fileBeanMap1.put("serialNo", serialNo);
			fileBeanMap1.put("codeString", codeString);
			fileBeanMap1.put("contentId", contentId);
			fileBeanMap1.put("busiStartDate", busiStartDate);
			//fileBeanMap1.put("URL",picURL);

			List<Map<String,Object>> urlList = new ArrayList<Map<String,Object>>();
			if(y==0){
				urlList.add(fileBeanMap1);
				fileBeanMap.put(codeString,urlList);
			}else{
				Iterator<Entry<String, Object>> it = fileBeanMap.entrySet().iterator();
				boolean flag =true;
				while(it.hasNext()){ 
					Entry<String, Object> itEntry = it.next(); 
					if(itEntry.getKey().equals(codeString)){
						flag =false;
//						List<Map<String,Object>> newList =  (List<Map<String,Object>>)fileBeanMap.get(codeString);
//						newList.add(fileBeanMap1);
//						fileBeanMap.put(codeString,newList);
					}
				}
				if(flag){
					urlList.add(fileBeanMap1);
					fileBeanMap.put(codeString,urlList);
				}
			}
		}
		return fileBeanMap;
	}
}
