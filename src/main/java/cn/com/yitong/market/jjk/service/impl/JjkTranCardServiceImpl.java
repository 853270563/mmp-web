package cn.com.yitong.market.jjk.service.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import cn.com.yitong.core.base.dao.Criteria;
import cn.com.yitong.core.base.dao.CriteriaExample;
import cn.com.yitong.core.base.dao.GenericDAO;
import cn.com.yitong.core.base.service.BaseServiceImpl;
import cn.com.yitong.market.jjk.dao.JjkTranCardDao;
import cn.com.yitong.market.jjk.model.JjkTranCard;
import cn.com.yitong.market.jjk.service.JjkTranCardService;
import cn.com.yitong.market.jjk.service.JjkTranLogService;
import cn.com.yitong.util.ConfigEnum;

/**
 * 
 * @author lc3@yitong.com.cn
 *
 */
@Service
public class JjkTranCardServiceImpl extends BaseServiceImpl<JjkTranCard, String> implements JjkTranCardService {
	
	@Resource
	private JjkTranCardDao jjkTranCardDao;

    @Resource
    private JjkTranLogService jjkTranLogService;

	@Override
	protected GenericDAO<JjkTranCard, String> getGenericDAO() {
		return jjkTranCardDao;
	}

    @Override
    public boolean insert(JjkTranCard record) {
        Exception exp = null;
        Assert.notNull(record, "开卡信息不能为空");
        try {
            record.setSignTime(new Date());
            super.insert(record);
        } catch (Exception e) {
            exp = e;
            return false;
        }
        boolean operStatus = (null == exp);
        String operDesc = null == exp ? null : "交易失败，失败原因为：" + exp.getMessage();
        jjkTranLogService.insert4TranDecard(record, operStatus, operDesc);
        return true;
    }
	
	public long queryCount(String userId, boolean isChk) {
		CriteriaExample<JjkTranCard> example = new CriteriaExample<JjkTranCard>();
		Criteria crit = example.createCriteria();
		if(StringUtils.hasText(userId)) {
			crit.equalTo(JjkTranCard.FL.signUser, userId);
		}
		crit.equalTo(JjkTranCard.FL.decaRes1, ConfigEnum.DICT_TRAN_DECARD_READ_STATE_NO.strVal());
		if(isChk) {
			crit.equalTo(JjkTranCard.FL.signState, ConfigEnum.DICT_TRAN_DECARD_SIGN_STATE_SUCCESS.strVal());
		} else {
			crit.equalTo(JjkTranCard.FL.signState, ConfigEnum.DICT_TRAN_DECARD_SIGN_STATE_FAILURE.strVal());
		}
		return jjkTranCardDao.updateDecaRes1ByExample(ConfigEnum.DICT_TRAN_DECARD_READ_STATE_HAS.strVal(), example);
	}

    @Override
    public List<JjkTranCard> queryByExampleExt(CriteriaExample<JjkTranCard> example) {
        return jjkTranCardDao.selectByExampleExt(example);
    }

}
