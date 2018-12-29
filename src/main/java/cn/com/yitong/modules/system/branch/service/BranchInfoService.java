package cn.com.yitong.modules.system.branch.service;

import cn.com.yitong.common.persistence.mybatis.impl.Criteria;
import cn.com.yitong.common.persistence.mybatis.impl.CriteriaQuery;
import cn.com.yitong.common.utils.JsonUtils;
import cn.com.yitong.consts.AppConstants;
import cn.com.yitong.modules.service.branch.dao.MgjBranchInfoDao;
import cn.com.yitong.modules.service.branch.model.MgjBranchInfo;
import cn.com.yitong.util.StringUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zhanglong
 * @date 17/9/25
 */
@Service
public class BranchInfoService {

    @Autowired
    private MgjBranchInfoDao mgjBranchInfoDao;

    public Map<String, Object> queryBranchInfo(Map<String, Object> params) {
        Map<String, Object> rst = new HashMap<String, Object>();
        String cityId = (String) params.get("CITY_ID");
        String townId = (String) params.get("TOWN_ID");
        String name = (String) params.get("BRANCH_NAME");
        String posX = (String) params.get("POS_X");
        String posY = (String) params.get("POS_Y");
        String radius = (String) params.get("RADIUS");
        if(StringUtil.isNotEmpty(cityId) || StringUtil.isNotEmpty(townId)) {
            rst.putAll(qryByAdminUnit(null, cityId, townId, name));
        }else if(StringUtil.isNotEmpty(posX) && StringUtil.isNotEmpty(posY)&& StringUtil.isNotEmpty(radius)) {
            rst.putAll(qryByPgs(params));
        }else {
            rst.put(AppConstants.STATUS, AppConstants.STATUS_FAIL);
            rst.put(AppConstants.MSG, "行政级别和GPS坐标都为空，请检查");
        }
        return rst;
    }

    public Map<String, Object> qryByPgs(Map<String, Object> params) {
        Map<String, Object> rst = new HashMap<String, Object>();
        List<MgjBranchInfo> list = mgjBranchInfoDao.queryBranchByGps(params);
        rst.put(AppConstants.STATUS, AppConstants.STATUS_OK);
        rst.put("LIST", getBranchMap(list));
        return rst;
    }

    public Map<String, Object> qryByAdminUnit(String prvoId, String cityId, String townId, String name) {
        Map<String, Object> rst = new HashMap<String, Object>();
        CriteriaQuery query = new CriteriaQuery();
        query.addOrder(MgjBranchInfo.TF.brchName, false);

        Criteria criteria = query.createAndCriteria();
        if(StringUtil.isNotEmpty(prvoId)) {
            criteria.equalTo(MgjBranchInfo.TF.provId, prvoId);
        }
        if(StringUtil.isNotEmpty(cityId)) {
            criteria.equalTo(MgjBranchInfo.TF.cityId, cityId);
        }
        if(StringUtil.isNotEmpty(townId)) {
            criteria.equalTo(MgjBranchInfo.TF.townId, townId);
        }
        if(StringUtil.isNotEmpty(name)) {
            criteria.like(MgjBranchInfo.TF.brchName, "%" + name + "%");
        }
        List<MgjBranchInfo> list = mgjBranchInfoDao.queryByCriteria(query);
        rst.put(AppConstants.STATUS, AppConstants.STATUS_OK);
        rst.put("LIST", getBranchMap(list));
        return rst;
    }

    private List<Map<String, Object>> getBranchMap(List<MgjBranchInfo> branchInfoList) {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        if(null != branchInfoList && branchInfoList.size() > 0) {
            for(MgjBranchInfo branch : branchInfoList ) {
                String json = JsonUtils.objectToJson(branch);
                try {
                    list.add(new ObjectMapper().readValue(json, Map.class));
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return list;
    }
}
