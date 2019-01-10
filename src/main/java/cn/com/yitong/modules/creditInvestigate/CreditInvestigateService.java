package cn.com.yitong.modules.creditInvestigate;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.com.yitong.common.utils.SpringContextUtils;
import cn.com.yitong.framework.base.IBusinessContext;
import cn.com.yitong.framework.servlet.ServerInit;
import cn.com.yitong.framework.util.CtxUtil;

public class CreditInvestigateService {
	private static final Logger logger = LoggerFactory
			.getLogger(CreditInvestigateService.class);
	// 缓存模板
	private static Map<String, String> CACHE = new HashMap<String, String>();
	public static final String BANKCS_SERVER_IP = ServerInit
			.getString("ESB_SERVER_IP");
	public static final int BANKCS_SERVER_PORT = ServerInit
			.getInt("ESB_SERVER_PORT");
	public static final String waittingDoneOut = "waittingDoneOut";
	public static final String custInfoOut = "custInfoOut";
	public static final String custLegalRepOut = "custLegalRepOut";
	public static final String custStockHolder = "custStockHolder";
	public static final String custExecutives = "custExecutives";
	public static final String custFinSta = "custFinSta";
	public static final String baseInfoOut = "baseInfoOut";
	public static final String amountAllocateOut = "amountAllocateOut";
	public static final String amountAssuranceOut = "amountAssuranceOut";
	public static final String bussDocumentOut = "bussDocumentOut";
	public static final String allOpitionOut = "allOpitionOut";
	public static final String currentOpitionsOut = "currentOpitionsOut";
	public static final String alreadyDone = "alreadyDone";
	public static final String nextInfoOut = "nextInfoOut";
	public static final String yxPicInfoOut = "yxPicInfoOut";
	public static final String invReport = "invReport";
	public static final String showSign = "showSign";
	public static final String creditShowJudgeSign = "creditShowJudgeSign";
	public static final String keepAdivice = "keepAdivice";
	public static final String submitActionOut = "submitActionOut";
	public static final String RET_SUCCESS = "0000";

	/**
	 * 信贷代办任务
	 * 
	 * @param apCard
	 * @param apCustom
	 * @param reserve
	 * @param ctx
	 * @return
	 * @throws Exception
	 */
	public static Map<String, Object> creditTaskList(IBusinessContext ctx,
			Map map) throws Exception {
		Map<String, Object> rst = new HashMap<String, Object>();
		String inParamXml = "";
		if (CACHE.containsKey(waittingDoneOut)) {
			inParamXml = CACHE.get(waittingDoneOut);
		} else {
			File file = SpringContextUtils.getApplicationContext().getResource("WEB-INF/conf/webservice/creditInvestigate/waittingDoneOut.xml").getFile();
			inParamXml = FileUtils.readFileToString(file, "UTF-8");
			CACHE.put(waittingDoneOut, inParamXml);
		}
		// 发送报文
		String inParamXmlFinal = CtxUtil.makesoapRequestData(map, inParamXml);
		byte[] byet = inParamXmlFinal.getBytes("GBK");
		String baoWeiLength = String.format("%07d", byet.length);
		String fsqBapWei = baoWeiLength + inParamXmlFinal;
		
		logger.info("信贷代办任务列表 发送前报文\n" + fsqBapWei);
		YtClient ytClient = new YtClient(BANKCS_SERVER_IP, BANKCS_SERVER_PORT);
		String reviceMsg = ytClient.sendMassage(fsqBapWei);
		// 处理解析返回报文
		YtParseReviceMsg.parseMessege(rst, reviceMsg, waittingDoneOut);
		return rst;
	}

	/**
	 * 客户信息查询
	 * 
	 * @param apCard
	 * @param apCustom
	 * @param reserve
	 * @param ctx
	 * @return
	 * @throws Exception
	 */
	public static Map<String, Object> creditCustInfo(IBusinessContext ctx,
			Map map) throws Exception {
		Map<String, Object> rst = new HashMap<String, Object>();
		String inParamXml = "";
		if (CACHE.containsKey(custInfoOut)) {
			inParamXml = CACHE.get(custInfoOut);
		} else {
			File file = SpringContextUtils.getApplicationContext().getResource("WEB-INF/conf/webservice/creditInvestigate/custInfoOut.xml").getFile();
			inParamXml = FileUtils.readFileToString(file, "UTF-8");
			CACHE.put(custInfoOut, inParamXml);
		}
		// 发送报文
		String inParamXmlFinal = CtxUtil.makesoapRequestData(map, inParamXml);
		byte[] byet = inParamXmlFinal.getBytes("GBK");
		String baoWeiLength = String.format("%07d", byet.length);
		String fsqBapWei = baoWeiLength + inParamXmlFinal;
		
		logger.info("客户信息查询 发送前报文\n" + fsqBapWei);
		YtClient ytClient = new YtClient(BANKCS_SERVER_IP, BANKCS_SERVER_PORT);
		String reviceMsg = ytClient.sendMassage(fsqBapWei);
		// 处理解析返回报文
		YtParseReviceMsg.parseMessege(rst, reviceMsg, custInfoOut);
		return rst;
	}

	/**
	 * 客户法人
	 * 
	 * @param apCard
	 * @param apCustom
	 * @param reserve
	 * @param ctx
	 * @return
	 * @throws Exception
	 */
	public static Map<String, Object> creditCustLegal(IBusinessContext ctx,
			Map map) throws Exception {
		Map<String, Object> rst = new HashMap<String, Object>();
		String inParamXml = "";
		if (CACHE.containsKey(custLegalRepOut)) {
			inParamXml = CACHE.get(custLegalRepOut);
		} else {
			File file = SpringContextUtils.getApplicationContext().getResource("WEB-INF/conf/webservice/creditInvestigate/custLegalRepOut.xml").getFile();
			inParamXml = FileUtils.readFileToString(file, "UTF-8");
			CACHE.put(custLegalRepOut, inParamXml);
		}
		// 发送报文
		String inParamXmlFinal = CtxUtil.makesoapRequestData(map, inParamXml);
		byte[] byet = inParamXmlFinal.getBytes("GBK");
		String baoWeiLength = String.format("%07d", byet.length);
		String fsqBapWei = baoWeiLength + inParamXmlFinal;
		
		logger.info("客户法人信息查询 发送前报文\n" + fsqBapWei);
		YtClient ytClient = new YtClient(BANKCS_SERVER_IP, BANKCS_SERVER_PORT);
		String reviceMsg = ytClient.sendMassage(fsqBapWei);
		// 处理解析返回报文
		YtParseReviceMsg.parseMessege(rst, reviceMsg, custLegalRepOut);
		return rst;
	}

	/**
	 * 
	 * 
	 * 股东
	 * 
	 * @param ctx
	 * @param map
	 * @return
	 * @throws Exception
	 */
	public static Map<String, Object> creditCustStockHolder(
			IBusinessContext ctx, Map map) throws Exception {
		Map<String, Object> rst = new HashMap<String, Object>();
		String inParamXml = "";
		if (CACHE.containsKey(custStockHolder)) {
			inParamXml = CACHE.get(custStockHolder);
		} else {
			File file = SpringContextUtils.getApplicationContext().getResource("WEB-INF/conf/webservice/creditInvestigate/custStockHolderOut.xml").getFile();

			inParamXml = FileUtils.readFileToString(file, "UTF-8");
			CACHE.put(custStockHolder, inParamXml);
		}
		// 发送报文
		String inParamXmlFinal = CtxUtil.makesoapRequestData(map, inParamXml);
		byte[] byet = inParamXmlFinal.getBytes("GBK");
		String baoWeiLength = String.format("%07d", byet.length);
		String fsqBapWei = baoWeiLength + inParamXmlFinal;
		
		logger.info("客户股东信息查询 发送前报文\n" + fsqBapWei);
		YtClient ytClient = new YtClient(BANKCS_SERVER_IP, BANKCS_SERVER_PORT);
		String reviceMsg = ytClient.sendMassage(fsqBapWei);
		// 处理解析返回报文
		YtParseReviceMsg.parseMessege(rst, reviceMsg, custStockHolder);
		return rst;
	}

	/**
	 * 
	 * 
	 * 客户高管信息
	 * 
	 * @param ctx
	 * @param map
	 * @return
	 * @throws Exception
	 */
	public static Map<String, Object> creditCustExecutives(
			IBusinessContext ctx, Map map) throws Exception {
		Map<String, Object> rst = new HashMap<String, Object>();
		String inParamXml = "";
		if (CACHE.containsKey(custExecutives)) {
			inParamXml = CACHE.get(custExecutives);
		} else {
			File file = SpringContextUtils.getApplicationContext().getResource("WEB-INF/conf/webservice/creditInvestigate/custExecutivesOut.xml").getFile();

			inParamXml = FileUtils.readFileToString(file, "UTF-8");
			CACHE.put(custExecutives, inParamXml);
		}
		// 发送报文
		String inParamXmlFinal = CtxUtil.makesoapRequestData(map, inParamXml);
		byte[] byet = inParamXmlFinal.getBytes("GBK");
		String baoWeiLength = String.format("%07d", byet.length);
		String fsqBapWei = baoWeiLength + inParamXmlFinal;
		
		logger.info("客户高管信息查询 发送前报文\n" + fsqBapWei);
		YtClient ytClient = new YtClient(BANKCS_SERVER_IP, BANKCS_SERVER_PORT);
		String reviceMsg = ytClient.sendMassage(fsqBapWei);
		// 处理解析返回报文
		YtParseReviceMsg.parseMessege(rst, reviceMsg, custExecutives);
		return rst;
	}

	/**
	 * 
	 * 财务报表
	 * 
	 * @param ctx
	 * @param map
	 * @return
	 * @throws Exception
	 */
	public static Map<String, Object> creditCustFinSta(IBusinessContext ctx,
			Map map) throws Exception {
		Map<String, Object> rst = new HashMap<String, Object>();
		String inParamXml = "";
		if (CACHE.containsKey(custFinSta)) {
			inParamXml = CACHE.get(custFinSta);
		} else {
			File file = SpringContextUtils.getApplicationContext().getResource("WEB-INF/conf/webservice/creditInvestigate/custExecutivesOut.xml").getFile();

			inParamXml = FileUtils.readFileToString(file, "UTF-8");
			CACHE.put(custFinSta, inParamXml);
		}
		// 发送报文
		String inParamXmlFinal = CtxUtil.makesoapRequestData(map, inParamXml);
		byte[] byet = inParamXmlFinal.getBytes("GBK");
		String baoWeiLength = String.format("%07d", byet.length);
		String fsqBapWei = baoWeiLength + inParamXmlFinal;
		
		logger.info("客户财务报表信息查询 发送前报文\n" + fsqBapWei);
		YtClient ytClient = new YtClient(BANKCS_SERVER_IP, BANKCS_SERVER_PORT);
		String reviceMsg = ytClient.sendMassage(fsqBapWei);
		// 处理解析返回报文
		YtParseReviceMsg.parseMessege(rst, reviceMsg, custFinSta);
		return rst;
	}

	/**
	 * 基本信息
	 * 
	 * @param apCard
	 * @param apCustom
	 * @param reserve
	 * @param ctx
	 * @return
	 * @throws Exception
	 */
	public static Map<String, Object> creditBaseInfo(IBusinessContext ctx,
			Map map) throws Exception {
		Map<String, Object> rst = new HashMap<String, Object>();
		String inParamXml = "";
		if (CACHE.containsKey(baseInfoOut)) {
			inParamXml = CACHE.get(baseInfoOut);
		} else {
			File file = SpringContextUtils.getApplicationContext().getResource("WEB-INF/conf/webservice/creditInvestigate/baseInfoOut.xml").getFile();

			inParamXml = FileUtils.readFileToString(file, "UTF-8");
			CACHE.put(baseInfoOut, inParamXml);
		}
		// 发送报文
		String inParamXmlFinal = CtxUtil.makesoapRequestData(map, inParamXml);
		byte[] byet = inParamXmlFinal.getBytes("GBK");
		String baoWeiLength = String.format("%07d", byet.length);
		String fsqBapWei = baoWeiLength + inParamXmlFinal;
		
		logger.info("基本信息查询 发送前报文\n" + fsqBapWei);
		YtClient ytClient = new YtClient(BANKCS_SERVER_IP, BANKCS_SERVER_PORT);
		String reviceMsg = ytClient.sendMassage(fsqBapWei);
		// 处理解析返回报文
		YtParseReviceMsg.parseMessege(rst, reviceMsg, baseInfoOut);
		return rst;
	}

	/**
	 * 额度分配信息
	 * 
	 * @param apCard
	 * @param apCustom
	 * @param reserve
	 * @param ctx
	 * @return
	 * @throws Exception
	 */
	public static Map<String, Object> amountAllocate(IBusinessContext ctx,
			Map map) throws Exception {
		Map<String, Object> rst = new HashMap<String, Object>();
		String inParamXml = "";
		if (CACHE.containsKey(amountAllocateOut)) {
			inParamXml = CACHE.get(amountAllocateOut);
		} else {
			File file = SpringContextUtils.getApplicationContext().getResource("WEB-INF/conf/webservice/creditInvestigate/amountAllocateOut.xml").getFile();

			inParamXml = FileUtils.readFileToString(file, "UTF-8");
			CACHE.put(amountAllocateOut, inParamXml);
		}
		// 发送报文
		String inParamXmlFinal = CtxUtil.makesoapRequestData(map, inParamXml);
		byte[] byet = inParamXmlFinal.getBytes("GBK");
		String baoWeiLength = String.format("%07d", byet.length);
		String fsqBapWei = baoWeiLength + inParamXmlFinal;
		
		logger.info("基本信息查询 发送前报文\n" + fsqBapWei);
		YtClient ytClient = new YtClient(BANKCS_SERVER_IP, BANKCS_SERVER_PORT);
		String reviceMsg = ytClient.sendMassage(fsqBapWei);
		// 处理解析返回报文
		YtParseReviceMsg.parseMessege(rst, reviceMsg, amountAllocateOut);
		return rst;
	}

	/**
	 * 额度担保信息 bussDocumentOut
	 * 
	 * @param apCard
	 * @param apCustom
	 * @param reserve
	 * @param ctx
	 * @return
	 * @throws Exception
	 */
	public static Map<String, Object> amountAssurance(IBusinessContext ctx,
			Map map) throws Exception {
		Map<String, Object> rst = new HashMap<String, Object>();
		String inParamXml = "";
		if (CACHE.containsKey(amountAssuranceOut)) {
			inParamXml = CACHE.get(amountAssuranceOut);
		} else {
			File file = SpringContextUtils.getApplicationContext().getResource("WEB-INF/conf/webservice/creditInvestigate/amountAssuranceOut.xml").getFile();

			inParamXml = FileUtils.readFileToString(file, "UTF-8");
			CACHE.put(amountAssuranceOut, inParamXml);
		}
		// 发送报文
		String inParamXmlFinal = CtxUtil.makesoapRequestData(map, inParamXml);
		byte[] byet = inParamXmlFinal.getBytes("GBK");
		String baoWeiLength = String.format("%07d", byet.length);
		String fsqBapWei = baoWeiLength + inParamXmlFinal;
		
		logger.info("基本信息查询 发送前报文\n" + fsqBapWei);
		YtClient ytClient = new YtClient(BANKCS_SERVER_IP, BANKCS_SERVER_PORT);
		String reviceMsg = ytClient.sendMassage(fsqBapWei);
		// 处理解析返回报文
		YtParseReviceMsg.parseMessege(rst, reviceMsg, amountAssuranceOut);
		return rst;
	}

	/**
	 * 业务文档信息
	 * 
	 * @param apCard
	 * @param apCustom
	 * @param reserve
	 * @param ctx
	 * @return
	 * @throws Exception
	 */
	public static Map<String, Object> bussDocument(IBusinessContext ctx, Map map)
			throws Exception {
		Map<String, Object> rst = new HashMap<String, Object>();
		String inParamXml = "";
		if (CACHE.containsKey(bussDocumentOut)) {
			inParamXml = CACHE.get(bussDocumentOut);
		} else {
			File file = SpringContextUtils.getApplicationContext().getResource("WEB-INF/conf/webservice/creditInvestigate/bussDocumentOut.xml").getFile();

			inParamXml = FileUtils.readFileToString(file, "UTF-8");
			CACHE.put(bussDocumentOut, inParamXml);
		}
		// 发送报文
		String inParamXmlFinal = CtxUtil.makesoapRequestData(map, inParamXml);
		byte[] byet = inParamXmlFinal.getBytes("UTF-8");
		String baoWeiLength = String.format("%07d", byet.length);
		String fsqBapWei = baoWeiLength + inParamXmlFinal;
		
		logger.info("基本信息查询 发送前报文\n" + fsqBapWei);
		YtClient ytClient = new YtClient(BANKCS_SERVER_IP, BANKCS_SERVER_PORT);
		String reviceMsg = ytClient.sendMassage(fsqBapWei);
		// 处理解析返回报文
		YtParseReviceMsg.parseMessege(rst, reviceMsg, bussDocumentOut);
		return rst;
	}

	/**
	 * 查看意见信息
	 * 
	 * @param apCard
	 * @param apCustom
	 * @param reserve
	 * @param ctx
	 * @return
	 * @throws Exception
	 */
	public static Map<String, Object> allOpition(IBusinessContext ctx, Map map)
			throws Exception {
		Map<String, Object> rst = new HashMap<String, Object>();
		String inParamXml = "";
		if (CACHE.containsKey(allOpitionOut)) {
			inParamXml = CACHE.get(allOpitionOut);
		} else {
			File file = SpringContextUtils.getApplicationContext().getResource("WEB-INF/conf/webservice/creditInvestigate/allOpitionOut.xml").getFile();

			inParamXml = FileUtils.readFileToString(file, "UTF-8");
			CACHE.put(allOpitionOut, inParamXml);
		}
		// 发送报文
		String inParamXmlFinal = CtxUtil.makesoapRequestData(map, inParamXml);
		byte[] byet = inParamXmlFinal.getBytes("GBK");
		String baoWeiLength = String.format("%07d", byet.length);
		String fsqBapWei = baoWeiLength + inParamXmlFinal;
		
		logger.info("基本信息查询 发送前报文\n" + fsqBapWei);
		YtClient ytClient = new YtClient(BANKCS_SERVER_IP, BANKCS_SERVER_PORT);
		String reviceMsg = ytClient.sendMassage(fsqBapWei);
		// 处理解析返回报文
		YtParseReviceMsg.parseMessege(rst, reviceMsg, allOpitionOut);
		return rst;
	}

	/**
	 * 审批人意见选择
	 * 
	 * @param apCard
	 * @param apCustom
	 * @param reserve
	 * @param ctx
	 * @return
	 * @throws Exception
	 */
	public static Map<String, Object> currentOpitions(IBusinessContext ctx,
			Map map) throws Exception {
		Map<String, Object> rst = new HashMap<String, Object>();
		String inParamXml = "";
		if (CACHE.containsKey(currentOpitionsOut)) {
			inParamXml = CACHE.get(currentOpitionsOut);
		} else {
			File file = SpringContextUtils.getApplicationContext().getResource("WEB-INF/conf/webservice/creditInvestigate/currentOpitionsOut.xml").getFile();

			inParamXml = FileUtils.readFileToString(file, "UTF-8");
			CACHE.put(currentOpitionsOut, inParamXml);
		}
		// 发送报文
		String inParamXmlFinal = CtxUtil.makesoapRequestData(map, inParamXml);
		byte[] byet = inParamXmlFinal.getBytes("GBK");
		String baoWeiLength = String.format("%07d", byet.length);
		String fsqBapWei = baoWeiLength + inParamXmlFinal;
		
		logger.info("审批人意见选择信息查询 发送前报文\n" + fsqBapWei);
		YtClient ytClient = new YtClient(BANKCS_SERVER_IP, BANKCS_SERVER_PORT);
		String reviceMsg = ytClient.sendMassage(fsqBapWei);
		// 处理解析返回报文
		YtParseReviceMsg.parseMessege(rst, reviceMsg, currentOpitionsOut);
		return rst;
	}

	/**
	 * 下一步信息
	 * 
	 * @param apCard
	 * @param apCustom
	 * @param reserve
	 * @param ctx
	 * @return
	 * @throws Exception
	 */
	public static Map<String, Object> nextInfo(IBusinessContext ctx, Map map)
			throws Exception {
		Map<String, Object> rst = new HashMap<String, Object>();
		String inParamXml = "";
		if (CACHE.containsKey(nextInfoOut)) {
			inParamXml = CACHE.get(nextInfoOut);
		} else {
			File file = SpringContextUtils.getApplicationContext().getResource("WEB-INF/conf/webservice/creditInvestigate/nextInfoOut.xml").getFile();

			inParamXml = FileUtils.readFileToString(file, "UTF-8");
			CACHE.put(nextInfoOut, inParamXml);
		}
		// 发送报文
		String inParamXmlFinal = CtxUtil.makesoapRequestData(map, inParamXml);
		byte[] byet = inParamXmlFinal.getBytes("GBK");
		String baoWeiLength = String.format("%07d", byet.length);
		String fsqBapWei = baoWeiLength + inParamXmlFinal;
		
		logger.info("下一步信息查询 发送前报文\n" + fsqBapWei);
		YtClient ytClient = new YtClient(BANKCS_SERVER_IP, BANKCS_SERVER_PORT);
		String reviceMsg = ytClient.sendMassage(fsqBapWei);
		// 处理解析返回报文
		YtParseReviceMsg.parseMessege(rst, reviceMsg, nextInfoOut);
		return rst;
	}

	/**
	 * 影像信息
	 * 
	 * @param apCard
	 * @param apCustom
	 * @param reserve
	 * @param ctx
	 * @return
	 * @throws Exception
	 */
	public static Map<String, Object> yxPicInfo(IBusinessContext ctx, Map map)
			throws Exception {
		Map<String, Object> rst = new HashMap<String, Object>();
		String inParamXml = "";
		if (CACHE.containsKey(yxPicInfoOut)) {
			inParamXml = CACHE.get(yxPicInfoOut);
		} else {
			File file = SpringContextUtils.getApplicationContext().getResource("WEB-INF/conf/webservice/creditInvestigate/yxPicInfoOut.xml").getFile();

			inParamXml = FileUtils.readFileToString(file, "UTF-8");
			CACHE.put(yxPicInfoOut, inParamXml);
		}
		// 发送报文
		String inParamXmlFinal = CtxUtil.makesoapRequestData(map, inParamXml);
		byte[] byet = inParamXmlFinal.getBytes("GBK");
		String baoWeiLength = String.format("%07d", byet.length);
		String fsqBapWei = baoWeiLength + inParamXmlFinal;
		
		logger.info("影像信息查询 发送前报文\n" + fsqBapWei);
		YtClient ytClient = new YtClient(BANKCS_SERVER_IP, BANKCS_SERVER_PORT);
		String reviceMsg = ytClient.sendMassage(fsqBapWei);
		// 处理解析返回报文
		YtParseReviceMsg.parseMessege(rst, reviceMsg, yxPicInfoOut);
		return rst;
	}

	/**
	 * 已办任务
	 * 
	 * @param ctx
	 * @param map
	 * @return
	 * @throws Exception
	 */
	public static Map<String, Object> alreadyDoneList(IBusinessContext ctx,
			Map map) throws Exception {
		Map<String, Object> rst = new HashMap<String, Object>();
		String inParamXml = "";
		if (CACHE.containsKey(alreadyDone)) {
			inParamXml = CACHE.get(alreadyDone);
		} else {
			File file = SpringContextUtils.getApplicationContext().getResource("WEB-INF/conf/webservice/creditInvestigate/alreadyDoneOut.xml").getFile();

			inParamXml = FileUtils.readFileToString(file, "UTF-8");
			CACHE.put(alreadyDone, inParamXml);
		}
		// 发送报文
		String inParamXmlFinal = CtxUtil.makesoapRequestData(map, inParamXml);
		byte[] byet = inParamXmlFinal.getBytes("GBK");
		String baoWeiLength = String.format("%07d", byet.length);
		String fsqBapWei = baoWeiLength + inParamXmlFinal;
		
		logger.info("已办任务列表 发送前报文\n" + fsqBapWei);
		YtClient ytClient = new YtClient(BANKCS_SERVER_IP, BANKCS_SERVER_PORT);
		String reviceMsg = ytClient.sendMassage(fsqBapWei);
		// 处理解析返回报文
		YtParseReviceMsg.parseMessege(rst, reviceMsg, alreadyDone);
		return rst;
	}

	/**
	 * 查询是否显示签名
	 * 
	 * @param ctx
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public static Map<String, Object> creditShowSign(IBusinessContext ctx,
			Map map) throws Exception {
		// TODO Auto-generated method stub
		Map<String, Object> rst = new HashMap<String, Object>();
		String inParamXml = "";
		if (CACHE.containsKey(showSign)) {
			inParamXml = CACHE.get(showSign);
		} else {
			File file = SpringContextUtils.getApplicationContext().getResource("WEB-INF/conf/webservice/creditInvestigate/isShowSignOut.xml").getFile();

			inParamXml = FileUtils.readFileToString(file, "UTF-8");
			CACHE.put(showSign, inParamXml);
		}
		// 发送报文
		String inParamXmlFinal = CtxUtil.makesoapRequestData(map, inParamXml);
		byte[] byet = inParamXmlFinal.getBytes("GBK");
		String baoWeiLength = String.format("%07d", byet.length);
		String fsqBapWei = baoWeiLength + inParamXmlFinal;
		
		logger.info("任务列表 发送前报文\n" + fsqBapWei);
		YtClient ytClient = new YtClient(BANKCS_SERVER_IP, BANKCS_SERVER_PORT);
		String reviceMsg = ytClient.sendMassage(fsqBapWei);
		// 处理解析返回报文
		YtParseReviceMsg.parseMessege(rst, reviceMsg, showSign);
		return rst;
	}

	/**
	 * 是否显示贷审会意见
	 * 
	 * @param ctx
	 * @param map
	 * @return
	 * @throws Exception
	 */
	public static Map<String, Object> creditShowJudgeSign(IBusinessContext ctx,
			Map map) throws Exception {
		// TODO Auto-generated method stub
		Map<String, Object> rst = new HashMap<String, Object>();
		String inParamXml = "";
		if (CACHE.containsKey(creditShowJudgeSign)) {
			inParamXml = CACHE.get(creditShowJudgeSign);
		} else {
			File file = SpringContextUtils.getApplicationContext().getResource("WEB-INF/conf/webservice/creditInvestigate/showJudgeSignOut.xml").getFile();

			inParamXml = FileUtils.readFileToString(file, "UTF-8");
			CACHE.put(creditShowJudgeSign, inParamXml);
		}
		// 发送报文
		String inParamXmlFinal = CtxUtil.makesoapRequestData(map, inParamXml);
		byte[] byet = inParamXmlFinal.getBytes("GBK");
		String baoWeiLength = String.format("%07d", byet.length);
		String fsqBapWei = baoWeiLength + inParamXmlFinal;
		
		logger.info("任务列表 发送前报文\n" + fsqBapWei);
		YtClient ytClient = new YtClient(BANKCS_SERVER_IP, BANKCS_SERVER_PORT);
		String reviceMsg = ytClient.sendMassage(fsqBapWei);
		// 处理解析返回报文
		YtParseReviceMsg.parseMessege(rst, reviceMsg, creditShowJudgeSign);
		return rst;
	}

	/**
	 * 调查报告
	 * 
	 * @param ctx
	 * @param map
	 * @return
	 * @throws Exception
	 */
	public static Map<String, Object> invReportList(IBusinessContext ctx,
			Map map) throws Exception {
		Map<String, Object> rst = new HashMap<String, Object>();
		String inParamXml = "";
		if (CACHE.containsKey(invReport)) {
			inParamXml = CACHE.get(invReport);
		} else {
			File file = SpringContextUtils.getApplicationContext().getResource("WEB-INF/conf/webservice/creditInvestigate/invReportOut.xml").getFile();

			inParamXml = FileUtils.readFileToString(file, "UTF-8");
			CACHE.put(invReport, inParamXml);
		}
		// 发送报文
		String inParamXmlFinal = CtxUtil.makesoapRequestData(map, inParamXml);
		byte[] byet = inParamXmlFinal.getBytes("GBK");
		String baoWeiLength = String.format("%07d", byet.length);
		String fsqBapWei = baoWeiLength + inParamXmlFinal;
		
		logger.info("调查报告查询 发送前报文\n" + fsqBapWei);
		YtClient ytClient = new YtClient(BANKCS_SERVER_IP, BANKCS_SERVER_PORT);
		String reviceMsg = ytClient.sendMassage(fsqBapWei);
		// 处理解析返回报文
		YtParseReviceMsg.parseMessege(rst, reviceMsg, invReport);
		return rst;
	}

	/**
	 * 保存意见
	 * 
	 * @param ctx
	 * @param map
	 * @return
	 * @throws Exception
	 */

	public static Map<String, Object> keepAdiviceList(IBusinessContext ctx,
			Map map) throws Exception {
		Map<String, Object> rst = new HashMap<String, Object>();
		String inParamXml = "";
		if (CACHE.containsKey(keepAdivice)) {
			inParamXml = CACHE.get(keepAdivice);
		} else {
			File file = SpringContextUtils.getApplicationContext().getResource("WEB-INF/conf/webservice/creditInvestigate/keepAdiviceOut.xml").getFile();

			inParamXml = FileUtils.readFileToString(file, "UTF-8");
			CACHE.put(keepAdivice, inParamXml);
		}
		// 发送报文
		String inParamXmlFinal = CtxUtil.makesoapRequestData(map, inParamXml);
		byte[] byet = inParamXmlFinal.getBytes("GBK");
		String baoWeiLength = String.format("%07d", byet.length);
		String fsqBapWei = baoWeiLength + inParamXmlFinal;
		
		logger.info("保存按钮 发送前报文\n" + fsqBapWei);
		YtClient ytClient = new YtClient(BANKCS_SERVER_IP, BANKCS_SERVER_PORT);
		String reviceMsg = ytClient.sendMassage(fsqBapWei);
		// 处理解析返回报文
		YtParseReviceMsg.parseMessege(rst, reviceMsg, keepAdivice);
		return rst;
	}

	/**
	 * 提交接口
	 * 
	 * @param ctx
	 * @param map
	 * @return
	 * @throws Exception
	 */

	public static Map<String, Object> submitAction(IBusinessContext ctx, Map map)
			throws Exception {
		Map<String, Object> rst = new HashMap<String, Object>();
		String inParamXml = "";
		if (CACHE.containsKey(submitActionOut)) {
			inParamXml = CACHE.get(submitActionOut);
		} else {
			File file = SpringContextUtils.getApplicationContext().getResource("WEB-INF/conf/webservice/creditInvestigate/submitActionOut.xml").getFile();

			inParamXml = FileUtils.readFileToString(file, "UTF-8");
			CACHE.put(submitActionOut, inParamXml);
		}
		// 发送报文
		String inParamXmlFinal = CtxUtil.makesoapRequestData(map, inParamXml);
		byte[] byet = inParamXmlFinal.getBytes("GBK");
		String baoWeiLength = String.format("%07d", byet.length);
		String fsqBapWei = baoWeiLength + inParamXmlFinal;
		
		logger.info("保存按钮 发送前报文\n" + fsqBapWei);
		YtClient ytClient = new YtClient(BANKCS_SERVER_IP, BANKCS_SERVER_PORT);
		String reviceMsg = ytClient.sendMassage(fsqBapWei);
		// 处理解析返回报文
		YtParseReviceMsg.parseMessege(rst, reviceMsg, submitActionOut);
		return rst;
	}
}
