package cn.com.yitong.modules.session.dao;

import cn.com.yitong.modules.session.model.ExtDemoDataWithBLOBs;
import cn.com.yitong.core.base.dao.CriteriaExample;
import cn.com.yitong.core.base.dao.GenericDAOImpl;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.sql.SQLException;

/**
 * @author lc3@yitong.com.cn
 */
@Repository
public class ExtDemoDataDaoImpl extends GenericDAOImpl<ExtDemoDataWithBLOBs, String> {
    @Override
    public String getIbatisNamespace() {
        return "MMP_EXT_DEMO_DATA";
    }

    public ExtDemoDataWithBLOBs selectByExampleWithBLOBs(String id) {
        Assert.hasText(id, "id 不能为空");
        ExtDemoDataWithBLOBs record;
        try {
            record = (ExtDemoDataWithBLOBs) sqlMapClient.queryForObject(getIbatisNamespace()
                    + ".selectByExampleWithBLOBs", id);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return record;
    }

    public int updateByExampleWithBLOBs(ExtDemoDataWithBLOBs record, CriteriaExample<ExtDemoDataWithBLOBs> example) {
        Assert.notNull(record, "record 不能为空");
        UpdateByExampleParms<ExtDemoDataWithBLOBs> parms = new UpdateByExampleParms<ExtDemoDataWithBLOBs>(record, example);
        int rows;
        try {
            rows = sqlMapClient.update(getIbatisNamespace() + ".updateByExampleWithBLOBs", parms);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return rows;
    }

    public int updateByPrimaryKeyWithBLOBs(ExtDemoDataWithBLOBs record) {
        Assert.notNull(record, "record 不能为空");
        int rows;
        try {
            rows = sqlMapClient.update(getIbatisNamespace() + ".updateByPrimaryKeyWithBLOBs", record);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return rows;
    }
}
