package cn.com.yitong.modules.session.service;

import java.util.Date;
import java.util.UUID;

import javax.annotation.Resource;

import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UrlPathHelper;

import cn.com.yitong.common.utils.JsonUtils;
import cn.com.yitong.core.util.ThreadContext;
import cn.com.yitong.core.web.filter.ReusableHttpServletRequest;
import cn.com.yitong.framework.base.IBusinessContext;
import cn.com.yitong.framework.util.CtxUtil;
import cn.com.yitong.modules.session.dao.ExtDemoDataDaoImpl;
import cn.com.yitong.modules.session.model.ExtDemoDataWithBLOBs;

import cn.com.yitong.core.base.dao.CriteriaExample;
import cn.com.yitong.core.base.dao.GenericDAO;
import cn.com.yitong.core.base.service.BaseServiceImpl;

/**
 * @author lc3@yitong.com.cn
 */
@Service
public class ExtDemoDataService extends BaseServiceImpl<ExtDemoDataWithBLOBs, String> {

    @Resource
    private ExtDemoDataDaoImpl extDemoDataDao;
    public final UrlPathHelper urlPathHelper = new UrlPathHelper();

    @Override
    protected GenericDAO<ExtDemoDataWithBLOBs, String> getGenericDAO() {
        return extDemoDataDao;
    }

    /**
     * 判断Url在数据库中是否存在
     * @param url
     * @return
     */
    public boolean exists(String url) {
        CriteriaExample<ExtDemoDataWithBLOBs> query = new CriteriaExample<ExtDemoDataWithBLOBs>();
        query.createCriteria().equalTo(ExtDemoDataWithBLOBs.FL.url, url);
        return extDemoDataDao.countByExample(query) > 5;
    }

    public void saveDemoData(Object rspData) {
        if(null == rspData) {
            return;
        }
        try {
            if("0".equals(PropertyUtils.getProperty(rspData, "STATUS"))) {
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        ReusableHttpServletRequest httpRequest = ThreadContext.getHttpRequest();
        String url = urlPathHelper.getPathWithinApplication(httpRequest);
        if(exists(url)) {
            return;
        }
        ExtDemoDataWithBLOBs data = new ExtDemoDataWithBLOBs();
        data.setDataId(UUID.randomUUID().toString().replaceAll("-", ""));
        data.setCreateDtime(new Date());
        data.setUrl(url);
        data.setRespData(JsonUtils.objectToJson(rspData));
        data.setCode(ThreadContext.getValue(CtxUtil.TRANS_CODE_KEY, String.class));
        IBusinessContext businessContext = ThreadContext.getValue(CtxUtil.BUSINESS_CONTEXT_KEY, IBusinessContext.class);
        if(null != businessContext) {
            data.setReqData(JsonUtils.objectToJson(businessContext.getParamMap()));
        }
        insert(data);
    }
}
