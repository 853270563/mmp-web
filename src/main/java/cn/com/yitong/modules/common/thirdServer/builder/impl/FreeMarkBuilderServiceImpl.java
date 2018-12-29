package cn.com.yitong.modules.common.thirdServer.builder.impl;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import cn.com.yitong.modules.common.thirdServer.builder.BuilderParamService;
import cn.com.yitong.modules.common.thirdServer.utils.FreeMarkTplCacheUtils;
import cn.com.yitong.tools.vo.SimpleResult;
import cn.com.yitong.util.FreemarkerUtils;
import cn.com.yitong.util.StringUtil;
import freemarker.template.Template;

/**
 * 使用dom4j构造xml，通过配置模板，然后填充查询，其中模板的加载在resources/template/freemarktpl下
 * 使用时需要额外传入参数，transModel：在freemarktpl下创建的子模块，transCode：模板名称
 * @author zhanglong
 * @date 17/8/29
 */
@Service
public class FreeMarkBuilderServiceImpl implements BuilderParamService<SimpleResult, Map> {

    private static final Logger logger = LoggerFactory.getLogger(FreeMarkBuilderServiceImpl.class);

    @Override
    public SimpleResult builder(Map param) {
        String transModel = (String) param.get("transModel");
        String transCode = (String) param.get("transCode");
        Template tpl = null;
        try {
            tpl = FreeMarkTplCacheUtils.getTpl(transModel, transCode);
        }catch (Exception e) {
            logger.error("获取交易参数模块失败，错误信息：", e);
            return new SimpleResult(false, e.getMessage());
        }
        if(null == tpl) {
            logger.error("获取模板失败");
            return new SimpleResult(false, "获取模板失败");
        }
        String content = FreemarkerUtils.process2String(tpl, param);
        if(StringUtil.isEmpty(content)) {
            logger.error("根据模板生成参数失败");
            return new SimpleResult(false, "根据模板生成参数失败");
        }
        return new SimpleResult(true, "处理成功", content);
    }
}
