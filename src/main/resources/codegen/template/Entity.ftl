package ${basePackageName}.model;

import cn.com.yitong.common.utils.ConfigUtils;
import cn.com.yitong.common.persistence.BaseEntity;
<#list table.importTypeList as importType >
import ${importType};
</#list>

/**
 * ${table.remarks}
 *
 * @author ${author}
 */
public class ${table.className} extends BaseEntity {
<#macro field classType name remarks>
    /**
     * ${remarks}
     */
    private ${classType} ${name};
</#macro>
<#macro getSetMethod classType name upperName remarks>
    public ${classType} get${upperName}() {
        return ${name};
    }

    public void set${upperName}(${classType} ${name}) {
        this.${name} = ${name};
    }
</#macro>

<#list table.fieldList as tf>
        <@field classType="${tf.javaTypeName}" name="${tf.name}" remarks="${tf.remarks}" />
</#list>

<#list table.fieldList as tf>
    <@getSetMethod classType="${tf.javaTypeName}" name="${tf.name}"
        upperName="${tf.methodName}" remarks="${tf.remarks}" />
</#list>

    public static class TF {

<#macro enumField name collumnName remarks>
        public static String ${name} = "${collumnName}";  // ${remarks}
</#macro>
<#list table.fieldList as field>
    <@enumField name="${field.name}" collumnName="${field.collumnName}" remarks="${field.remarks}" />
</#list>

    }
}
