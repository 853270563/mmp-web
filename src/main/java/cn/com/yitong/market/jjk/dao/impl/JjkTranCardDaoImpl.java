package cn.com.yitong.market.jjk.dao.impl;

import java.sql.SQLException;
import java.util.List;

import org.springframework.stereotype.Repository;

import cn.com.yitong.core.base.dao.CriteriaExample;
import cn.com.yitong.core.base.dao.GenericDAOImpl;
import cn.com.yitong.market.jjk.dao.JjkTranCardDao;
import cn.com.yitong.market.jjk.model.JjkTranCard;

@Repository
public class JjkTranCardDaoImpl extends GenericDAOImpl<JjkTranCard, String> implements JjkTranCardDao {

	@Override
	public String getIbatisNamespace() {
		return "JJK_TRAN_CARD";
	}

	public long updateDecaRes1ByExample(String decaRes1, CriteriaExample<JjkTranCard> example) {
		try {
			JjkTranCard record = new JjkTranCard();
			record.setDecaRes1(decaRes1);
			return getSqlMapClient().update(getIbatisNamespace() + ".updateDecaRes1ByExample",
					new UpdateByExampleParms<JjkTranCard>(record, example));
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

    @Override
    public List<JjkTranCard> selectByExampleExt(CriteriaExample<JjkTranCard> example) {
        try {
            return getSqlMapClient().queryForList(getIbatisNamespace() + ".selectByExampleExt", example);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}