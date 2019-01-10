package cn.com.yitong.market.mxd.controller;

import cn.com.yitong.common.utils.key.KeyFactory;
import cn.com.yitong.consts.NS;
import cn.com.yitong.framework.base.IBusinessContext;
import cn.com.yitong.framework.core.bean.BaseControl;
import cn.com.yitong.framework.core.bean.BusinessContext;
import cn.com.yitong.framework.net.IEBankConfParser;
import cn.com.yitong.framework.net.IRequstBuilder;
import cn.com.yitong.framework.net.IResponseParser;
import cn.com.yitong.framework.service.ICrudService;
import cn.com.yitong.ares.login.service.LoginService;
import cn.com.yitong.framework.util.CtxUtil;
import cn.com.yitong.util.DateUtil;
import cn.com.yitong.util.StringUtil;
import cn.com.yitong.util.YTLog;
import cn.com.yitong.core.base.WebUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * Created by QianJH on 2015-7-10.
 */
@Controller
@RequestMapping("mxd")
public class XDController extends BaseControl {
    private Logger logger = YTLog.getLogger(this.getClass());

    @Autowired
    @Qualifier("requestBuilder4db")
    IRequstBuilder requestBuilder;// 请求报文生成器
    @Autowired
    @Qualifier("responseParser4db")
    IResponseParser responseParser;// 响应报文解析器
    @Autowired
    @Qualifier("EBankConfParser4db")
    IEBankConfParser confParser;// 报文装载器

    @Autowired
    ICrudService service;
    private static final String BASE_PATH = "market/mxd";
    private static final String BASE_PATH_SUFFIX = ".do";

    private static final String PATH_LOAN_LIST = "loanList";//贷款查询
    private static final String PATH_PRODUCTS = "products";//产品查询
    private static final String PATH_USERS = "users";//用户查询
    private static final String PATH_CASE_APPLY = "caseApply";//贷款案件申请
    private static final String PATH_CASES = "cases";//案件列表
    private static final String PATH_CUSTOMER_VISIT = "customerVisit";//客户拜访
    private static final String PATH_VISITS = "visits";//拜访查询


    private static final String EXIST = "EXIST";
    private static final String EXIST_FALSE = "0";
    private static final String EXIST_TRUE = "1";

    private static final String PRODUCT_NAME = "PRODUCT_NAME";
    private static final String PRODUCT_ID = "PRODUCT_ID";
    private static final String CUSTOMER_ID = "CUSTOMER_ID";

    @Autowired
    private LoginService loginService;

    /**
     * 用户贷款记录查询
     *
     * @param request
     * @return
     */
    @RequestMapping("/" + PATH_LOAN_LIST + BASE_PATH_SUFFIX)
    public
    @ResponseBody
    Map<String, Object> loanList(HttpServletRequest request) {
        String transCode = BASE_PATH + PATH_LOAN_LIST;
        Map<String, Object> rst = new HashMap<String, Object>();
        // 初始化数据总线
        IBusinessContext ctx = new BusinessContext(request, IBusinessContext.PARAM_TYPE_MAP);
        // 检查报文定义
        if (!transPrev(ctx, transCode, rst)) {
            return rst;
        }
        Map params = ctx.getParamMap();
        try {
            List<Map<String, Object>> customerCertificates = service.findList("CUSTOMER_CERTIFICATE.query", params);
            if (customerCertificates == null || customerCertificates.isEmpty()) {
                rst.put(EXIST, EXIST_FALSE);
            } else {
                Map<String, Object> customer = service.load("CUSTOMER.query", customerCertificates.get(0));
                if (customer == null || customer.isEmpty()) {
                    rst.put(EXIST, EXIST_FALSE);
                } else {
                    rst.putAll(customer);
                    rst.put(EXIST, EXIST_TRUE);
                    //查询贷款记录
                    Map<String, Object> loanQueryParam = new HashMap<String, Object>();
                    loanQueryParam.put(CUSTOMER_ID, customer.get(CUSTOMER_ID));
                    List<Map<String, Object>> loans = service.findList("LOAN.queryFull", loanQueryParam);

                    rst.put(NS.LIST, loans);
                }
            }

        } catch (Exception e) {
            transAfter(ctx, transCode, rst, false);
            logger.error(e.getMessage(), e);
        }
        transAfter(ctx, transCode, rst, true);
        return rst;
    }

    /**
     * 查询贷款产品
     *
     * @param request
     * @return
     */
    @RequestMapping("/" + PATH_PRODUCTS + BASE_PATH_SUFFIX)
    public
    @ResponseBody
    Map<String, Object> products(HttpServletRequest request) {
        String transCode = BASE_PATH + PATH_PRODUCTS;
        Map<String, Object> rst = new HashMap<String, Object>();
        // 初始化数据总线
        IBusinessContext ctx = new BusinessContext(request, IBusinessContext.PARAM_TYPE_MAP);
        // 检查报文定义
        if (!transPrev(ctx, transCode, rst)) {
            return rst;
        }
        Map params = ctx.getParamMap();
        try {
            List<Map<String, Object>> products = service.findList("PRODUCT.query", params);
            rst.put(NS.LIST, products);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            transAfter(ctx, transCode, rst, false);
        }
        transAfter(ctx, transCode, rst, true);
        return rst;
    }

    /**
     * 查询人员列表
     *
     * @param request
     * @return
     */
    @RequestMapping("/" + PATH_USERS + BASE_PATH_SUFFIX)
    public
    @ResponseBody
    Map<String, Object> users(HttpServletRequest request) {
        String transCode = BASE_PATH + PATH_USERS;
        Map<String, Object> rst = new HashMap<String, Object>();
        // 初始化数据总线
        IBusinessContext ctx = new BusinessContext(request, IBusinessContext.PARAM_TYPE_MAP);
        // 检查报文定义
        if (!transPrev(ctx, transCode, rst)) {
            return rst;
        }
        Map params = ctx.getParamMap();
        try {
            Map<String, Object> user = loginService.loadUserById(String.valueOf(params.get("LOGIN_NAME")));
            if (user == null || user.isEmpty()) {
                throw new Exception("上送登录账号不存在");
            }
            Map<String, Object> srMap = service.load("SYS_ROLE_MAP.query", params);//MAP_CODE
            if (srMap == null || srMap.isEmpty()) {
                throw new Exception("上送角色映射编号异常");
            }

            Map<String, Object> userParam = new HashMap<String, Object>();
            userParam.put("ROLE_ID", srMap.get("SYS_ROLE_ID"));
            userParam.put("OFFICE_ID", user.get("ORG_ID"));
            List<Map<String, Object>> userMaps = service.findList("SYS_USER.queryByRoleAndOffice", userParam);
            List<Map<String, Object>> rstUsers = new ArrayList<Map<String, Object>>();
            for (Map<String, Object> userMap : userMaps) {
                Map<String, Object> rstUser = new HashMap<String, Object>();
                rstUser.put("USER_ID", userMap.get("USER_ID"));
                rstUser.put("LOGIN_NAME", userMap.get("LOGIN_NAME"));
                rstUser.put("USER_NAME", userMap.get("USER_NAME"));
                rstUsers.add(rstUser);
            }
            rst.put(NS.LIST, rstUsers);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            transAfter(ctx, transCode, rst, false);
        }
        transAfter(ctx, transCode, rst, true);
        return rst;
    }

    /**
     * 贷款申请
     *
     * @param request
     * @return
     */
    @RequestMapping("/" + PATH_CASE_APPLY + BASE_PATH_SUFFIX)
    public
    @ResponseBody
    Map<String, Object> caseApply(HttpServletRequest request) {
        String transCode = BASE_PATH + PATH_CASE_APPLY;
        Map<String, Object> rst = new HashMap<String, Object>();
        // 初始化数据总线
        IBusinessContext ctx = new BusinessContext(request, IBusinessContext.PARAM_TYPE_MAP);
        // 检查报文定义
        if (!transPrev(ctx, transCode, rst)) {
            return rst;
        }
        Map params = ctx.getParamMap();
        try {
            Map<String, Object> userManager = loginService.loadUserById(String.valueOf(params.get("LOGIN_NAME")));
            if (userManager == null || userManager.isEmpty()) {
                throw new Exception("上送登录账号不存在");
            }

            //主管编号
            String userAuditId = StringUtil.getString(params, "USER_AUDIT_ID", null);

            List<Map<String, Object>> customerCertificates = service.findList("CUSTOMER_CERTIFICATE.query", params);
            Map<String, Object> customer = null;
            if (customerCertificates == null || customerCertificates.isEmpty()) {
                //创建客户
                String customerId = KeyFactory.getKeyGenerator("CUSTOMER").genNextKey();
                Map<String, Object> customerBean = new HashMap<String, Object>();
                customerBean.put("CUSTOMER_ID", customerId);
                customerBean.put("CUSTOMER_NAME", params.get("CUSTOMER_NAME"));
                customerBean.put("CREATE_TIME", DateUtil.todayfulldata());
                service.insert("CUSTOMER.insert", customerBean);
                //创建证件记录
                customer = new HashMap<String, Object>();
                customer.put("CUSTOMER_ID", customerId);
                String customerCertId = KeyFactory.getKeyGenerator("CUSTOMER_CERTIFICATE").genNextKey();
                customer.put("CUSTOMER_CERTIFICATE_ID", customerCertId);
                customer.put("CUSTOMER_CERTIFICATE_TYPE", params.get("CUSTOMER_CERTIFICATE_TYPE"));
                customer.put("CUSTOMER_CERTIFICATE_NO", params.get("CUSTOMER_CERTIFICATE_NO"));
                service.insert("CUSTOMER_CERTIFICATE.insert", customer);
            } else {
                customer = service.load("CUSTOMER.query", customerCertificates.get(0));
                if (customer == null || customer.isEmpty()) {
                    rst.put(EXIST, EXIST_FALSE);
                }
            }

            Date now = new Date();
            Map<String, Object> caseBean = new HashMap<String, Object>();
            caseBean.put("CASE_ID", KeyFactory.getKeyGenerator("CASE").genNextKey());
            caseBean.put("CUSTOMER_ID", customer.get("CUSTOMER_ID"));
            caseBean.put("PRODUCT_ID", params.get("PRODUCT_ID"));
            caseBean.put("APPLY_AMOUNT", params.get("APPLY_AMOUNT"));
            caseBean.put("CASE_STATUS", "01");
            caseBean.put("USER_MANAGER_ID", userManager.get("ID"));
            caseBean.put("USER_AUDIT_ID", userAuditId);
            caseBean.put("CASE_DATE", DateUtil.ymd.format(now));
            caseBean.put("CREATE_TIME", DateUtil.sdf.format(now));
            service.insert("CASE.insert", caseBean);

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            transAfter(ctx, transCode, rst, false);
        }
        transAfter(ctx, transCode, rst, true);
        return rst;
    }

    /**
     * 案件查询
     * @param request
     * @return
     */
    @RequestMapping("/" + PATH_CASES + BASE_PATH_SUFFIX)
    public
    @ResponseBody
    Map<String, Object> cases(HttpServletRequest request) {
        String transCode = BASE_PATH + PATH_CASES;
        Map<String, Object> rst = new HashMap<String, Object>();
        // 初始化数据总线
        IBusinessContext ctx = new BusinessContext(request, IBusinessContext.PARAM_TYPE_MAP);
        // 检查报文定义
        if (!transPrev(ctx, transCode, rst)) {
            return rst;
        }
        Map params = ctx.getParamMap();
        try {
            Object caseStatuss = params.get("CASE_STATUSS");
            if (caseStatuss != null) {
                params.put("CASE_STATUSS", String.valueOf(caseStatuss).replace(",", "','"));
            }
            List<Map<String, Object>> cases = service.findList("CASE.queryFull", params);
            rst.put(NS.LIST, cases);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            transAfter(ctx, transCode, rst, false);
        }
        transAfter(ctx, transCode, rst, true);
        return rst;
    }

    /**
     * 客户拜访
     * @param request
     * @return
     */
    @RequestMapping("/" + PATH_CUSTOMER_VISIT + BASE_PATH_SUFFIX)
    public @ResponseBody Map<String, Object> customerVisit(HttpServletRequest request) {
        String transCode = BASE_PATH + PATH_CUSTOMER_VISIT;
        Map<String, Object> rst = new HashMap<String, Object>();
        // 初始化数据总线
        IBusinessContext ctx = new BusinessContext(request, IBusinessContext.PARAM_TYPE_MAP);
        // 检查报文定义
        if (!transPrev(ctx, transCode, rst)) {
            return rst;
        }
        Map params = ctx.getParamMap();
        Date now = new Date();
        try {
            params.put("CUSTOMER_VISIT_ID", KeyFactory.getKeyGenerator("CUSTOMER_VISIT").genNextKey());
            params.put("VISIT_TIME", DateUtil.sdf.format(now));
            params.put("VISIT_DATE", DateUtil.ymd.format(now));
            service.insert("CUSTOMER_VISIT.insert", params);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            transAfter(ctx, transCode, rst, false);
        }
        transAfter(ctx, transCode, rst, true);

        return rst;
    }


    /**
     * 拜访查询
     * @param request
     * @return
     */
    @RequestMapping("/" + PATH_VISITS + BASE_PATH_SUFFIX)
    public @ResponseBody Map<String, Object> visits(HttpServletRequest request) {
        String transCode = BASE_PATH + PATH_VISITS;
        Map<String, Object> rst = new HashMap<String, Object>();
        // 初始化数据总线
        IBusinessContext ctx = new BusinessContext(request, IBusinessContext.PARAM_TYPE_MAP);
        // 检查报文定义
        if (!transPrev(ctx, transCode, rst)) {
            return rst;
        }
        Map params = ctx.getParamMap();
        try {
            List<Map<String, Object>> visits = service.findList("CUSTOMER_VISIT.queryFull", params);
            rst.put(NS.LIST, visits);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            transAfter(ctx, transCode, rst, false);
        }
        transAfter(ctx, transCode, rst, true);

        return rst;
    }


    /**
     * 获取当前登录的用户
     *
     * @param request
     * @param ctx
     * @return
     */
    private Map<String, Object> getCurrentUserId(HttpServletRequest request, IBusinessContext ctx) {
        //获取当前登录的用户
        String userId = WebUtils.getCurrentUserId(request, ctx);
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("ID", userId);
        Map<String, Object> user = service.load("SYS_USER.query", param);
        return user;
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
        return CtxUtil.transPrev(ctx, transCode, json2MapParamCover,
                requestBuilder, confParser, rst);
    }

    /**
     * 事务之后处理
     *
     * @param ctx
     * @param transCode
     * @param rst
     * @param ok
     */
    private void transAfter(IBusinessContext ctx, String transCode, Map<String, Object> rst,
                            boolean ok) {
        CtxUtil.transAfter(ctx, transCode, rst, ok, responseParser, confParser);
    }
}
