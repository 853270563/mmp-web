package cn.com.yitong.market.jjk.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import cn.com.yitong.framework.dao.IbatisDao;
import cn.com.yitong.market.jjk.model.JjkTranCard;
import cn.com.yitong.util.ConfigEnum;

/**
 * JJK_TRAN_LOG--数据库操作
 * @author yaoym
 */
@Service
public class JjkTranLogService {

	final String TABLE_NAME = "JJK_TRAN_LOG";

    public static final String OPER_RET_STATUS_SUCCESS = "1";
    public static final String OPER_RET_STATUS_FAILURE= "0";

	protected String getTableName() {
		return TABLE_NAME;
	}
    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    protected IbatisDao ibatisDao;

    public Object insert(Map<String, Object> paramMap) {
        return ibatisDao.insert(getTableName() + ".insert", paramMap);
    }

    public void insert4TranDecard(JjkTranCard decard, boolean operStatus, String operDesc) {
        //TRAN_ID,TRAN_TYPE,TRAN_SUB_TYPE,OPER_TIME,OPER_RET_STATUS,OPER_RET_DESC,
        // SIGN_DEVI_TYPE,SIGN_ORGAN,SIGN_USER,SIGN_DEVI_NO,CERT_NO,CARD_TYPE,
        // CARD_NO,EXTEND1,EXTEND2,EXTEND3,CUST_NAME
        if(null == decard || null == decard.getTransId()) {
            if (logger.isWarnEnabled()) {
                logger.warn("记录开卡业务日志失败，传入的参数为空");
            }
            return;
        }
        Map<String, Object> logMap = new HashMap<String, Object>();
        logMap.put("TRAN_ID", decard.getTransId());
        logMap.put("TRAN_TYPE", ConfigEnum.DICT_TRAN_OPERLOG_TRAN_TYPE_OPENCARD.strVal());
        logMap.put("TRAN_SUB_TYPE", null);
        logMap.put("OPER_TIME", new Date());
        logMap.put("OPER_RET_STATUS", operStatus ? OPER_RET_STATUS_SUCCESS : OPER_RET_STATUS_FAILURE);
        logMap.put("OPER_RET_DESC", operDesc);
        logMap.put("SIGN_DEVI_TYPE", decard.getSignDeviType());
        logMap.put("SIGN_ORGAN", decard.getSignOrgan());
        logMap.put("SIGN_USER", decard.getSignUser());
        logMap.put("SIGN_DEVI_NO", decard.getSignDeviNo());
        logMap.put("CERT_NO", decard.getCertNo());
        logMap.put("CARD_TYPE", decard.getCardType());
        logMap.put("CARD_NO", decard.getCardNo());
        logMap.put("CUST_NAME", decard.getCustName());
        insert(logMap);
    }

}