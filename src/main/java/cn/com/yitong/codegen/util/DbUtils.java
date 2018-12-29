package cn.com.yitong.codegen.util;

import cn.com.yitong.codegen.vo.TableEntity;
import cn.com.yitong.codegen.vo.TableField;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.io.IOException;
import java.sql.*;
import java.util.*;


/**
 * 代码生成方面的数据库工具类.
 */
public class DbUtils {

    private static final Logger logger = LoggerFactory.getLogger(DbUtils.class);
    private static final Properties prop = genConfigProperties();

    private static Properties genConfigProperties() {
        try {
            Properties sysProps = new Properties(System.getProperties());
            PropertiesLoaderUtils.fillProperties(sysProps,
                    new EncodedResource(new ClassPathResource(
                            "META-INF/res/ares.properties"), "UTF-8"));
            return sysProps;
        } catch (IOException e) {
            logger.error("加载数据库配置文件失败失败", e);
            throw new RuntimeException(e);
        }
    }

    public static Connection getConnect() {
        try {
            Class.forName(prop.getProperty("jdbc.driverClassName"));
            Properties tmpProp = System.getProperties();
            tmpProp.setProperty("user", prop.getProperty("jdbc.username"));
            tmpProp.setProperty("password", prop.getProperty("jdbc.password"));
            tmpProp.setProperty("remarksReporting", "true");
            tmpProp.setProperty("useInformationSchema", "true");
            return DriverManager.getConnection(prop.getProperty("jdbc.url"), tmpProp);
        } catch (Exception e) {
            logger.error("获取和数据库的连接失败", e);
            throw new RuntimeException("获取和数据库的连接失败", e);
        }
    }

    public static Collection<TableEntity> getTables(String schema, String tablePattern) {
        Set<TableEntity> tableCollection = new HashSet<TableEntity>();
        if (StringUtils.isBlank(tablePattern)) {
            return tableCollection;
        }
        String[] tablePatterns = tablePattern.split(",");
        ResultSet rs = null;
        try {
            DatabaseMetaData metaData = getConnect().getMetaData();
            for (String tablePatternStr : tablePatterns) {
                rs = metaData.getTables(null, schema, tablePatternStr, new String[]{"TABLE"});
                tableCollection.addAll(parseTableEntity4ResultSet(rs));
                closeResultSet(rs);
            }
            for (TableEntity entity : tableCollection) {
                rs = metaData.getColumns(null, entity.getSchema(), entity.getName(), null);
                entity.setFieldList(parseTableField4ResultSet(rs));
                closeResultSet(rs);
                rs = metaData.getPrimaryKeys(null, entity.getSchema(), entity.getName());
                while (rs.next()) {
                    String columnName = rs.getString("COLUMN_NAME"); //$NON-NLS-1$
                    entity.addPrimaryKeyColumn(columnName);
                }
                closeResultSet(rs);
            }
        } catch (SQLException e) {
            logger.error("获取数据库表信息失败", e);
            throw new RuntimeException("获取数据库信息失败", e);
        } finally {
            closeResultSet(rs);
        }
        return tableCollection;
    }

    private static void closeResultSet(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                // ignore
            }
        }
    }

    private static List<TableField> parseTableField4ResultSet(ResultSet rs) throws SQLException {
        List<TableField> list = new ArrayList<TableField>();
        if (null == rs) {
            return null;
        }
        while (rs.next()) {
            TableField field = new TableField();
            field.setCollumnName(rs.getString("COLUMN_NAME"));
            field.setJdbcType(rs.getInt("DATA_TYPE"));
            field.setLength(rs.getInt("COLUMN_SIZE"));
            field.setNullable(rs.getInt("NULLABLE") > 0);
            field.setRemarks(rs.getString("REMARKS"));
            field.setScale(rs.getInt("DECIMAL_DIGITS"));
            list.add(field);
        }
        return list;
    }

    private static List<TableEntity> parseTableEntity4ResultSet(ResultSet rs) throws SQLException {
        List<TableEntity> list = new ArrayList<TableEntity>();
        if (null == rs) {
            return null;
        }
        while (rs.next()) {
            TableEntity table = new TableEntity();
            table.setSchema(rs.getString("TABLE_SCHEM"));
            if (null == table.getSchema()) {
                table.setSchema(rs.getString("TABLE_CAT"));
            }
            table.setName(rs.getString("TABLE_NAME"));
            table.setRemarks(rs.getString("REMARKS"));
            list.add(table);
        }
        return list;
    }
}
