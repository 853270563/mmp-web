package cn.com.yitong.modules.common;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.com.yitong.common.utils.ConfigEnum;
import cn.com.yitong.common.utils.SpringContextUtils;
import cn.com.yitong.core.session.Session;
import cn.com.yitong.core.util.DictUtils;
import cn.com.yitong.core.util.SecurityUtils;
import cn.com.yitong.framework.base.IBusinessContext;
import cn.com.yitong.framework.core.bean.BaseControl;
import cn.com.yitong.framework.core.bean.BusinessContext;
import cn.com.yitong.framework.net.IClientFactory;
import cn.com.yitong.framework.net.IEBankConfParser;
import cn.com.yitong.framework.net.IRequstBuilder;
import cn.com.yitong.framework.net.IResponseParser;
import cn.com.yitong.framework.util.CtxUtil;

/**
 * @author luanyu
 * @date   2018年6月4日
 */
@Controller
public class CommonController extends BaseControl {

	@Autowired
	@Qualifier("requestBuilder4db")
	IRequstBuilder requestBuilder;// 请求报文生成器
	@Autowired
	@Qualifier("responseParser4db")
	IResponseParser responseParser;// 响应报文解析器
	@Autowired
	@Qualifier("urlClient4db")
	IClientFactory client;// 响应报文解析器
	@Autowired
	@Qualifier("EBankConfParser4db")
	IEBankConfParser confParser;// 报文装载器
	private static SqlSession sqlSession = SpringContextUtils.getBean(SqlSession.class);

	@RequestMapping("ares/getMenus.do")
	@ResponseBody
	public Map<String, Object> getMeau(HttpServletRequest request) {
		Map<String, Object> result = new HashMap<String, Object>();
		// 初始化数据总线
		IBusinessContext ctx = new BusinessContext(request, IBusinessContext.PARAM_TYPE_MAP);
		Session session = SecurityUtils.getSession();
		boolean ok = false;
		try {

			List<Map<String, Object>> selectList = sqlSession.selectList("menu.queryCurrentMenu", session.getUserId());
			List<Map<String, Object>> selectOAList = sqlSession.selectList("menu.queryOAUser", session.getUserId());
			List<Map<String, Object>> selectCRMList = sqlSession.selectList("menu.queryCRMUser", session.getUserId());
			List<Map<String, Object>> selectCOCKPITList = sqlSession.selectList("menu.queryCOCKPITUser", session.getUserId());
			if (!selectCRMList.isEmpty()) {
				HashMap<String, Object> hashMap = new HashMap<String, Object>();
				hashMap.put("MENU_NAME", "移动CRM");
				hashMap.put("MENU_URL", DictUtils.getDictValue(ConfigEnum.DICT_SYS_PARAMS, "crm_url", ""));
				hashMap.put("MENU_NO", "crm");
				hashMap.put("MENU_SORT", DictUtils.getDictSort(ConfigEnum.DICT_SYS_PARAMS, "crm_url", ""));
				selectList.add(hashMap);
			}
			if (!selectOAList.isEmpty()) {
				HashMap<String, Object> hashMap = new HashMap<String, Object>();
				hashMap.put("MENU_NAME", "移动OA");
				hashMap.put("MENU_URL", DictUtils.getDictValue(ConfigEnum.DICT_SYS_PARAMS, "OA_url", ""));
				hashMap.put("MENU_NO", "oa");
				hashMap.put("MENU_SORT", DictUtils.getDictSort(ConfigEnum.DICT_SYS_PARAMS, "OA_url", ""));
				selectList.add(hashMap);
			}
			if (!selectCOCKPITList.isEmpty()) {
				HashMap<String, Object> hashMap = new HashMap<String, Object>();
				hashMap.put("MENU_NAME", "移动驾驶舱");
				hashMap.put("MENU_URL", DictUtils.getDictValue(ConfigEnum.DICT_SYS_PARAMS, "cockpit_url", ""));
				hashMap.put("MENU_NO", "cockpit");
				hashMap.put("MENU_SORT", DictUtils.getDictSort(ConfigEnum.DICT_SYS_PARAMS, "cockpit_url", ""));
				selectList.add(hashMap);
			}
			result.put("LIST", selectList);
			ok = true;
		} catch (Exception e) {

		}
		transAfter(ctx, "menu/menu", result, ok);

		return result;
	}
	/**
	 * 事务前置处理
	 * 
	 * @param ctx
	 * @param transCode
	 * @param rst
	 * @return
	 */
	private boolean transPrev(IBusinessContext ctx, String transCode, Map rst) {
		return CtxUtil.transPrev(ctx, transCode, json2MapParamCover, requestBuilder, confParser, rst);
	}

	/**
	 * 事务之后处理
	 * 
	 * @param ctx
	 * @param transCode
	 * @param rst
	 * @param ok
	 */
	private void transAfter(IBusinessContext ctx, String transCode, Map rst, boolean ok) {
		CtxUtil.transAfter(ctx, transCode, rst, ok, responseParser, confParser);
	}
}
