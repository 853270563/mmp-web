package cn.com.yitong.framework.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import cn.com.yitong.framework.dao.IbatisDao;
import cn.com.yitong.framework.service.IPMngOrganService;


@Service
public class PMngOrganService implements IPMngOrganService {
	@Autowired
	@Qualifier("ibatisDao")
	private IbatisDao dao;

    /**
     * 通过机构ID查询对应的机构
     * @param orgId
     * @return
     */
    @Override
    public Map<String, Object> queryById(String orgId) {
        return dao.load("SYS_ORGAN.queryById", orgId);
    }
	
	@Override
	public List findList_ZH(Map<String, String> map) {
		// TODO Auto-generated method stub
		return dao.findList("SYS_ORGAN.findAll_ZH", map);
	}

	@Override
	public boolean deleteOrgan(Map<String, String> map) {
		// TODO Auto-generated method stub
		return dao.delete("SYS_ORGAN.delete", map);
	}

	@Override
	public List findList_HK(Map<String, String> map) {
		// TODO Auto-generated method stub
		return dao.findList("SYS_ORGAN.findAll_HK", map);
	}

	@Override
	public List findList_EN(Map<String, String> map) {
		// TODO Auto-generated method stub
		return dao.findList("SYS_ORGAN.findAll_EN", map);
	}

}
