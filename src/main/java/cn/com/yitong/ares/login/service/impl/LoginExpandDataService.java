package cn.com.yitong.ares.login.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.com.yitong.ares.login.service.ILoginExpandDataService;
import cn.com.yitong.consts.AppConstants;
import cn.com.yitong.consts.NS;
import cn.com.yitong.framework.dao.IbatisDao;
import cn.com.yitong.market.jjk.service.CustomFileMngService;
import cn.com.yitong.util.CustomFileType;
import cn.com.yitong.util.StringUtil;
import cn.com.yitong.util.YTLog;

@Service
@Transactional
public class LoginExpandDataService implements ILoginExpandDataService {
    private Logger logger = YTLog.getLogger(this.getClass());
    @Resource
    private CustomFileMngService customFileMngService;
    @Autowired
    @Qualifier("ibatisDao")
    private IbatisDao dao;

    @Override
    public List<Map<String, Object>> getRate(Map<String, Object> params) {
        List<Map<String, Object>> rate = null;
        try {
            rate = dao.findList("RATE_INTE.queryRate", params);
        } catch (Exception e) {
            if (logger.isDebugEnabled()) {
                logger.debug("===================错误信息：" + e.getMessage());
            }
        }
        return rate;
    }

    @Override
    public List<Map<String, Object>> getExRate(Map<String, Object> params) {
        List<Map<String, Object>> exchanger = null;
        try {
            exchanger = dao.findList("EXCHANGE_RATE.query", params);
        } catch (Exception e) {
            if (logger.isDebugEnabled()) {
                logger.debug("===================错误信息：" + e.getMessage());
            }
        }
        return exchanger;
    }

    @Override
    public List<Map<String, Object>> getCharge(Map<String, Object> params) {
        List<Map<String, Object>> charge = new ArrayList<Map<String, Object>>();
        try {
            List<Map<String, Object>> proTyp = dao.findList("CHARGE.queryProductType", params);
            for (Map<String, Object> typ : proTyp) {
                Map<String, Object> chargeMap = new HashMap<String, Object>();
                chargeMap.put("PRODUCT_TYPE_ID", typ.get("PRODUCT_TYPE_ID"));
                chargeMap.put("PRODUCT_TYPE_NAME", typ.get("PRODUCT_TYPE_NAME"));
                chargeMap.put("PRODUCT_TYPE_DESC", typ.get("PRODUCT_TYPE_DESC"));
                List<Map<String, Object>> productInfo = dao.findList("CHARGE.queryProductInfo", chargeMap);
                List<Map<String, Object>> productList = new ArrayList<Map<String, Object>>();
                for (Map<String, Object> pro : productInfo) {
                    Map<String, Object> proDet = new HashMap<String, Object>();
                    proDet.put("PRODUCT_ID", pro.get("PRODUCT_ID"));
                    proDet.put("PRODUCT_NAME", pro.get("PRODUCT_NAME"));
                    proDet.put("PRODUCT_DESC", pro.get("PRODUCT_DESC"));
                    List<Map<String, Object>> chargeInfo = dao.findList("CHARGE.queryChargeInfo", proDet);
                    List<Map<String, Object>> chargeList = new ArrayList<Map<String, Object>>();
                    for (Map<String, Object> charges : chargeInfo) {
                        Map<String, Object> chargeDet = new HashMap<String, Object>();
                        chargeDet.put("CHARGE_NAME", charges.get("CHARGE_NAME"));
                        chargeDet.put("CHARGE_COST", charges.get("CHARGE_COST"));
                        chargeList.add(chargeDet);
                    }
                    proDet.put("CHARGELIST", chargeList);
                    productList.add(proDet);
                }
                chargeMap.put("PRODUCTLIST", productList);
                charge.add(chargeMap);
            }
        } catch (Exception e) {
            if (logger.isDebugEnabled()) {
                logger.debug("===================错误信息：" + e.getMessage());
            }
        }
        return charge;
    }

    @Override
    public List<Map<String, Object>> getResFiles(Map<String, Object> params) {
        List<Map<String, Object>> resDatas = new ArrayList<Map<String, Object>>();
        try {
            List<Map<String, Object>> datas = dao.findList("MZJ_RES_DATA.pageQuery", params);
            if (datas != null && datas.size() > 0) {
                for (int i = 0; i < datas.size(); i++) {
                    Map<String, Object> temp = datas.get(i);
                    if (null != temp.get("PROPA_IMG") && !"".equals(temp.get("PROPA_IMG")) &&
                            null != temp.get("FILE_ADDR") && !"".equals(temp.get("FILE_ADDR"))) {
                        temp.put("PROPA_IMG", AppConstants.IMG_WEB_URL + temp.get("PROPA_IMG"));
                        temp.put("FILE_ADDR", AppConstants.IMG_WEB_URL + temp.get("FILE_ADDR"));
                    }
                    if (("null").equals(String.valueOf(temp.get("PRD_TYPE")))) {
                        temp.put("PRD_TYPE", "");
                    }
                    resDatas.add(temp);
                }
            }
        } catch (Exception e) {
            if (logger.isDebugEnabled()) {
                logger.debug("===================错误信息：" + e.getMessage());
            }
        }
        return resDatas;
    }

    @Override
    public List<Map<String, Object>> isUpdate(String Udate) {
        Map<String, Object> rateObj = new HashMap<String, Object>();
        List<String> list = new ArrayList<String>();
        list.add(NS.RATE_INTE);
        list.add(NS.EXCHANGE_RATE);
        list.add(NS.CHARGE);
        rateObj.put("OPERATE_IDS", list);
        rateObj.put("OPERATE_DATE", Udate);
        List<Map<String, Object>> isUpdate = dao.findList("MGG_TRIGGER_RELATION.query", rateObj);
        return isUpdate;
    }

	/**
	 * 文件图片下载前缀
	 */
	private static final String downLoadApiPrefix = "/download/userResource/file.do?fileName=";
    @Override
    public List<Map<String, Object>> getMenuA(Map<String, Object> params) {
        List<Map<String, Object>> menuA = new ArrayList<Map<String, Object>>();
        //获取一级菜单
        List<Map<String, Object>> menuList1 = new ArrayList<Map<String, Object>>();
		if (params.get("MENU_PAR_ID") == null) {

			params.put("MENU_PAR_ID", "00000");
		}
        menuList1 = dao.findList("SYS_MOBILE_ROLE_MENU.queryMenuAtypeList", params);

        for (Map<String, Object> menuMap : menuList1) {
			params.put("MENU_PAR_ID", menuMap.get("MENU_ID"));
			menuMap.put("MENU_IMG", downLoadApiPrefix + menuMap.get("FILE_ADDR"));
			List<Map<String, Object>> menuA2 = getMenuA(params);
			if (!menuA2.isEmpty()) {

				menuMap.put("SUB_MENU", menuA2);
			}
			menuA.add(menuMap);

        }
        return menuA;
    }

    @Override
    public List<Map<String, Object>> getMenuB(Map<String, Object> params) {
        List<Map<String, Object>> menuB = new ArrayList<Map<String, Object>>();
        //菜单查询
        //先获取一级菜单
        List<Map<String, Object>> menuList1 = new ArrayList<Map<String, Object>>();
        params.put("MENU_LEVEL", "1");
        menuList1 = dao.findList("SYS_MOBILE_ROLE_MENU.queryMenuUserBtypeList", params);
        for (int i = 0; menuList1.size() > i; i++) {
            Map<String, Object> content = new HashMap<String, Object>();
            //添加一级菜单
            content.put("MENU_ID", menuList1.get(i).get("MENU_ID"));
            content.put("MENU_NAME", menuList1.get(i).get("MENU_NAME"));
            content.put("MENU_URL", menuList1.get(i).get("MENU_URL"));
            if (!StringUtil.isEmpty((String) content.get("FILE_ADDR"))) {
                content.put("MENU_IMG", customFileMngService.getFileByFileName2Base64(
                        (String) content.get("FILE_ADDR"), CustomFileType.FILE));
            } else {
                content.put("MENU_IMG", content.get("FILE_ADDR"));
            }
            content.put("MENU_TYP", menuList1.get(i).get("MENU_TYP"));
            content.put("MENU_PAR_ID", menuList1.get(i).get("MENU_PAR_ID"));
            //查询二级菜单
            params.put("MENU_LEVEL", "2");
            params.put("MENU_PAR_ID", menuList1.get(i).get("MENU_ID"));
            List<Map<String, Object>> mentList2 = dao.findList("SYS_MOBILE_ROLE_MENU.queryMenuUserBtypeList", params);
            List<Map<String, Object>> menuList2Temp = new ArrayList<Map<String, Object>>();
            for (int j = 0; j < mentList2.size(); j++) {
                Map<String, Object> tempList2 = mentList2.get(j);
                if (!StringUtil.isEmpty((String) tempList2.get("FILE_ADDR"))) {
                    tempList2.put("MENU_IMG", customFileMngService.getFileByFileName2Base64(
                            (String) tempList2.get("FILE_ADDR"), CustomFileType.FILE));
                } else {
                    tempList2.put("MENU_IMG", tempList2.get("FILE_ADDR"));
                }
                menuList2Temp.add(tempList2);
            }
            //添加二级菜单
            content.put("MENU_LIST2", menuList2Temp);
            menuB.add(content);
        }
        return menuB;
    }

}
