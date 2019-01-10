package cn.com.yitong.framework.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import cn.com.yitong.framework.dao.IbatisDao;
import cn.com.yitong.framework.service.IChanParaService;

@Service
public class ChanParaService implements IChanParaService {

	@Autowired
	@Qualifier("ibatisDao")
	private IbatisDao dao;
	
	@Override
	public List findSecu_Question() {
		return dao.findList("SYS_DICT.findSecu_Question", null);
	}

	@Override
	public List FINDALL_PAY_FEE_TYP(Map<String, String> map) {
		// TODO Auto-generated method stub
		return dao.findList("SYS_DICT.FINDALL_PAY_FEE_TYP", map);
	}

	@Override
	public List findGradeByIgt(String igt) {
		// TODO Auto-generated method stub
		return dao.findList("SYS_DICT.findGradeByIgt", igt);
	}

	@Override
	public List findInsurParam(Map<String, String> map) {
		// TODO Auto-generated method stub
		return dao.findList("SYS_DICT.findInsurParam", map);
	}

	@Override
	public List findParamByType(String type) {
		// TODO Auto-generated method stub
		return dao.findList("SYS_DICT.findParamByType", type);
	}
	@Override
	public List findLFHByType(String type) {
		// TODO Auto-generated method stub
		return dao.findList("SYS_DICT.findLFHByType", type);
	}
	@Override
	public List findParamByTypeHK(String type) {
		// TODO Auto-generated method stub
		return dao.findList("SYS_DICT.findParamByTypeHK", type);
	}
	@Override
	public List findParamByTypeCN(String type) {
		// TODO Auto-generated method stub
		return dao.findList("SYS_DICT.findParamByTypeCN", type);
	}
	@Override
	public List findParamByTypeEN(String type) {
		// TODO Auto-generated method stub
		return dao.findList("SYS_DICT.findParamByTypeEN", type);
	}
	
	@Override
	public List findParamDetailByTypeHK(String type) {
		// TODO Auto-generated method stub
		return dao.findList("SYS_DICT.findParamDetailByTypeHK", type);
	}
	@Override
	public List findParamDetailByTypeCN(String type) {
		// TODO Auto-generated method stub
		return dao.findList("SYS_DICT.findParamDetailByTypeCN", type);
	}
	@Override
	public List findParamDetailByTypeEN(String type) {
		// TODO Auto-generated method stub
		return dao.findList("SYS_DICT.findParamDetailByTypeEN", type);
	}

	@Override
	public List findIDTypeByType(String type) {
		// TODO Auto-generated method stub
		return dao.findList("SYS_DICT.findIDTypeByType", type);
	}

	@Override
	public List findFDTypeByType_EN(String type) {
		// TODO Auto-generated method stub
		return dao.findList("SYS_DICT.findFDTypeByType_EN", type);
	}

	@Override
	public List findFDTypeByType_CN(String type) {
		// TODO Auto-generated method stub
		return dao.findList("SYS_DICT.findFDTypeByType_CN", type);
	}

	@Override
	public List findFDTypeByType_HK(String type) {
		// TODO Auto-generated method stub
		return dao.findList("SYS_DICT.findFDTypeByType_HK", type);
	}

	@Override
	public List findFDByType_HK(String ccy_no) {
		// TODO Auto-generated method stub
		return dao.findList("SYS_DICT.findFDByType_HK", ccy_no);
	}

	@Override
	public List findFDByType_CN(String ccy_no) {
		// TODO Auto-generated method stub
		return dao.findList("SYS_DICT.findFDByType_CN", ccy_no);
	}

	@Override
	public List findFDByType_EN(String ccy_no) {
		// TODO Auto-generated method stub
		return dao.findList("SYS_DICT.findFDByType_EN", ccy_no);
	}

	@Override
	public String findStreamPwd(String pwd) {
		// TODO Auto-generated method stub
		return dao.load("SYS_DICT.findStreamPwd", pwd);
	}

	@Override
	public List findFDCcyTyp_HK(String type) {
		// TODO Auto-generated method stub
		return dao.findList("SYS_DICT.findFDCcyTyp_HK", type);
	}

	@Override
	public List findFDCcyTyp_CN(String type) {
		// TODO Auto-generated method stub
		return dao.findList("SYS_DICT.findFDCcyTyp_CN", type);
	}

	@Override
	public List findFDCcyTyp_EN(String type) {
		// TODO Auto-generated method stub
		return dao.findList("SYS_DICT.findFDCcyTyp_EN", type);
	}

	@Override
	public List findMultiByType(Map<String, String> map) {
		// TODO Auto-generated method stub
		return dao.findList("SYS_DICT.findMultiByType", map);
	}

}
