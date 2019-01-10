package cn.com.yitong.core.base.dao;

import com.ibatis.sqlmap.client.SqlMapClient;
import cn.com.yitong.core.base.Page;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.List;

public abstract class GenericDAOImpl<E, PK extends Serializable> implements GenericDAO<E, PK> {

	@Resource
    protected SqlMapClient sqlMapClient;

	public GenericDAOImpl() {
	}

	public GenericDAOImpl(SqlMapClient sqlMapClient) {
		super();
		Assert.notNull(sqlMapClient, "sqlMapClient不能为空");
		this.sqlMapClient = sqlMapClient;
	}

	public abstract String getIbatisNamespace();

    @Override
    public int countByExample(CriteriaExample<E> example) {
        Integer count;
		try {
			count = (Integer)  sqlMapClient.queryForObject(getIbatisNamespace() + ".countByExample", example);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
        return count.intValue();
    }

    @Override
    public int deleteByExample(CriteriaExample<E> example) {
        int rows;
		try {
			rows = sqlMapClient.delete(getIbatisNamespace() + ".deleteByExample", example);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
        return rows;
    }

    @Override
    public int deleteByPrimaryKey(String id) {
    	Assert.hasText(id, "id 不能为空");
        int rows;
		try {
			rows = sqlMapClient.delete(getIbatisNamespace() + ".deleteByPrimaryKey", id);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
        return rows;
    }

    @Override
    public void insert(E record) {
    	Assert.notNull(record, "record 不能为空");
        try {
			sqlMapClient.insert(getIbatisNamespace() + ".insert", record);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
    }

    @Override
    public void insertSelective(E record) {
    	Assert.notNull(record, "record 不能为空");
        try {
			sqlMapClient.insert(getIbatisNamespace() + ".insertSelective", record);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
    }

    @SuppressWarnings("unchecked")
    @Override
	public List<E> selectByExample(CriteriaExample<E> example) {
		List<E> list;
		try {
			list = sqlMapClient.queryForList(getIbatisNamespace() + ".selectByExample", example);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
        return list;
    }

	@SuppressWarnings("unchecked")
	@Override
	public Page<E> findPageByExample(CriteriaExample<E> example, Page<E> page) {
		Assert.notNull(page, "page 不能为空");
		List<E> list;
		try {
			list = sqlMapClient.queryForList(getIbatisNamespace() + ".findPageByExample", 
					new PageByExampleParms<E>(page, example));
			page.setList(list);
			page.setTotalCount(countByExample(example));
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
        return page;
	}

	@SuppressWarnings("unchecked")
	@Override
    public E selectByPrimaryKey(String id) {
		Assert.hasText(id, "id 不能为空");
		E record;
		try {
			record = (E) sqlMapClient.queryForObject(getIbatisNamespace() + ".selectByPrimaryKey", id);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
        return record;
    }

	@Override
    public int updateByExampleSelective(E record, CriteriaExample<E> example) {
		Assert.notNull(record, "record 不能为空");
        UpdateByExampleParms<E> parms = new UpdateByExampleParms<E>(record, example);
        int rows;
		try {
			rows = sqlMapClient.update(getIbatisNamespace() + ".updateByExampleSelective", parms);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
        return rows;
    }

	@Override
    public int updateByExample(E record, CriteriaExample<E> example) {
		Assert.notNull(record, "record 不能为空");
        UpdateByExampleParms<E> parms = new UpdateByExampleParms<E>(record, example);
        int rows;
		try {
			rows = sqlMapClient.update(getIbatisNamespace() + ".updateByExample", parms);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
        return rows;
    }

	@Override
    public int updateByPrimaryKeySelective(E record) {
		Assert.notNull(record, "record 不能为空");
        int rows;
		try {
			rows = sqlMapClient.update(getIbatisNamespace() + ".updateByPrimaryKeySelective", record);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
        return rows;
    }

	@Override
    public int updateByPrimaryKey(E record) {
		Assert.notNull(record, "record 不能为空");
        int rows;
		try {
			rows = sqlMapClient.update(getIbatisNamespace() + ".updateByPrimaryKey", record);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
        return rows;
    }

    public static class UpdateByExampleParms<E> extends CriteriaExample<E> {
        private Object record;

        public UpdateByExampleParms(Object record, CriteriaExample<E> example) {
            super(example);
            this.record = record;
        }

        public Object getRecord() {
            return record;
        }
    }
    
    public static class PageByExampleParms<E> extends CriteriaExample<E> {
        private int startRow;
        private int endRow;

        public PageByExampleParms(Page<E> page, CriteriaExample<E> example) {
            super(example);
            startRow = (page.getPageNumber() - 1) * page.getPageSize();
            endRow = page.getPageNumber() * page.getPageSize();
        }

		public int getStartRow() {
			return startRow;
		}

		public int getEndRow() {
			return endRow;
		}

    }

	public SqlMapClient getSqlMapClient() {
		return sqlMapClient;
	}

}