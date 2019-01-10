package cn.com.yitong.codegen.vo;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.ObjectUtils;

import cn.com.yitong.common.utils.ConfigUtils;
import cn.com.yitong.util.StringUtil;

/**
 * 表对应的代码生成辅助类.
 */
public class TableEntity {

    private String schema;
    private String name;
    private String remarks;
    private String className;
    private List<TableField> fieldList;
    private List<String> primaryKeyList = null;

    public List<TableField> getFieldList() {
        return fieldList;
    }

    public void setFieldList(List<TableField> fieldList) {
        this.fieldList = fieldList;
    }

    public String getTableId() {
        return schema + '.' + name;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public String getSchemaPropName() {
        if(null == schema) {
            return "";
        }
        Set<String> keys = ConfigUtils.keySetStartWith("schema.");
        for (String key : keys) {
            if(schema.equalsIgnoreCase(ConfigUtils.getValue(key))) {
                return key;
            }
        }
        return "";
    }

    public String getName() {
        return name.toUpperCase();
    }

    public void setName(String name) {
        this.name = name;
        this.className = StringUtil.capitalizeAll(name);
    }

    public String getClassName() {
        return className;
    }

    public String getRemarks() {
        return ObjectUtils.toString(remarks, "");
    }

    public void setRemarks(String remarks) {
        if(null != remarks) {
            remarks = remarks.replaceAll("\\n|\\r", "  ");
        }
        this.remarks = remarks;
    }

    public void addPrimaryKeyColumn(String collumnName) {
        if (null == collumnName) {
            return;
        }
        if (null == primaryKeyList) {
            primaryKeyList = new ArrayList<String>();
        }
        if (!primaryKeyList.contains(collumnName)) {
            primaryKeyList.add(collumnName);
        }
    }

    public TableField getPrimaryField() {
        if (primaryKeyList.isEmpty() || primaryKeyList.size() > 1) {
            return null;
        }
        for (TableField tableField : fieldList) {
            if (primaryKeyList.contains(tableField.getCollumnName())) {
                return tableField;
            }
        }
        return null;
    }

    public List<TableField> getFieldListWithoutKey() {
        List<TableField> list = new ArrayList<TableField>(fieldList.size());
        TableField primaryField = getPrimaryField();
        for (TableField tableField : fieldList) {
            if (tableField != primaryField) {
                list.add(tableField);
            }
        }
        return list;
    }

    public Set<String> getImportTypeList() {
        if (null != this.fieldList) {
            Set<String> importTypeSet = new HashSet<String>();
            for (TableField tableField : fieldList) {
                Class javaType = tableField.getJavaType();
                if (null != javaType && !javaType.getName().startsWith("java.lang")) {
                    importTypeSet.add(javaType.getName());
                }
            }
            return importTypeSet;
        }
        return null;
    }

    @Override
    public int hashCode() {
        return getTableId().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof TableEntity && null != getTableId() &&
                getTableId().equals(obj);
    }

    @Override
    public String toString() {
        return "TableEntity{" +
               "schema='" + schema + '\'' +
               ", name='" + name + '\'' +
               ", remarks='" + remarks + '\'' +
               ", className='" + className + '\'' +
               ", fieldList=" + fieldList +
               ", primaryKeyList=" + primaryKeyList +
               '}';
    }
}
