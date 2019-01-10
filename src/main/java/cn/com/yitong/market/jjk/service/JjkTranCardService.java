package cn.com.yitong.market.jjk.service;

import java.util.List;

import cn.com.yitong.core.base.dao.CriteriaExample;
import cn.com.yitong.core.base.service.BaseService;
import cn.com.yitong.market.jjk.model.JjkTranCard;

/**
 * 
 * @author lc3@yitong.com.cn
 *
 */
public interface JjkTranCardService extends BaseService<JjkTranCard, String> {

	/**
	 * 查看业务审核通知数
	 * @param userId
	 * @param isChk
	 * @return
	 */
	long queryCount(String userId, boolean isChk);

    /**
     * 按条件查询，并附带审核结果信息
     * @param example
     * @return
     */
    List<JjkTranCard> queryByExampleExt(CriteriaExample<JjkTranCard> example);
}
