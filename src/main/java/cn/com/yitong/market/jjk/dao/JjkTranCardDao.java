package cn.com.yitong.market.jjk.dao;

import java.util.List;

import cn.com.yitong.core.base.dao.CriteriaExample;
import cn.com.yitong.core.base.dao.GenericDAO;
import cn.com.yitong.market.jjk.model.JjkTranCard;

public interface JjkTranCardDao extends GenericDAO<JjkTranCard, String> {
	
	long updateDecaRes1ByExample(String decaRes1, CriteriaExample<JjkTranCard> example);

    List<JjkTranCard> selectByExampleExt(CriteriaExample<JjkTranCard> example);
}