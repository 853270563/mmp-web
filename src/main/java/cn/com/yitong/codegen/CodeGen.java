package cn.com.yitong.codegen;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Collection;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import cn.com.yitong.codegen.util.CodegenConfigUtils;
import cn.com.yitong.codegen.util.DbUtils;
import cn.com.yitong.codegen.util.FreemarkerUtils;
import cn.com.yitong.codegen.vo.TableEntity;
import freemarker.template.Configuration;
import freemarker.template.Template;

/**
* 代码生成主类.
* @author  lc3@yitong.com.cn
*/
public class CodeGen {

    private static final Logger logger = LoggerFactory.getLogger(CodeGen.class);

    private static final String separator = File.separator;

    private static final String DB2_TYPE = "db2";

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

    public static void main(String[] args) throws IOException {

        // 代码模板配置
        Configuration cfg = new Configuration();
        cfg.setDirectoryForTemplateLoading(CodegenConfigUtils.getTplPath());
        cfg.setDefaultEncoding("UTF-8");

        // 定义模板变量
        Properties model = CodegenConfigUtils.getConfigs();


        // 生成 Entity
		// Template entityTpl = cfg.getTemplate("Entity.ftl");
        Template daoTpl = cfg.getTemplate("EntityDao.ftl");
        Template serviceTpl = cfg.getTemplate("EntityService.ftl");
        Template controllerTpl = cfg.getTemplate("EntityController.ftl");
        Template formTpl = cfg.getTemplate("viewForm.ftl");
        Template listTpl = cfg.getTemplate("viewList.ftl");
        Template sqlMapperTpl = cfg.getTemplate("SqlMapper.ftl");
		// Template sqlMapperDB2Tpl = cfg.getTemplate("SqlMapperForDB2.ftl");
		//Template sqlMapperMysqlTpl = cfg.getTemplate("SqlMapperForMysql.ftl");
        Template sqlMapperExtTpl = cfg.getTemplate("SqlMapperExt.ftl");

        Collection<TableEntity> tables = DbUtils.getTables(CodegenConfigUtils.getTableSchema(),
                CodegenConfigUtils.getTablePattern());
        if (logger.isTraceEnabled()) {
            logger.trace("tables:[{}]", tables.toString());
        }

        String genFileType = model.getProperty("genFileType", null);
        if(null != genFileType) {
            genFileType = genFileType.toUpperCase();
        }
        for (TableEntity table : tables) {
            model.put("table", table);
            String filePath = null;
            String content = null;

            if(null == genFileType || genFileType.contains("M")) {
				/* filePath = CodegenConfigUtils.getJavaBasePath() + separator + "model"
				        + separator + table.getClassName() + ".java";
				content = FreemarkerUtils.process2String(entityTpl, model);
				writeFile(content, filePath, false);
				logger.info("Entity: {}", filePath);*/

                filePath = CodegenConfigUtils.getMapperBasePath() + separator
                        + table.getName() + ".xml";
                content = FreemarkerUtils.process2String(sqlMapperTpl, model);
                writeFile(content, filePath, true);
                logger.info("SqlMapper: {}", filePath);

                filePath = CodegenConfigUtils.getMapperBasePath() + separator
                        + table.getName() + "_ext.xml";
                content = FreemarkerUtils.process2String(sqlMapperExtTpl, model);
                writeFile(content, filePath, false);
                logger.info("SqlMapperExt: {}", filePath);

				/* filePath = CodegenConfigUtils.getMapperDB2Path() + separator
				        + table.getName() + ".xml";
				content = FreemarkerUtils.process2String(sqlMapperDB2Tpl, model);
				writeFile(content, filePath, true);
				logger.info("SqlMapperDB2: {}", filePath);
				
				filePath = CodegenConfigUtils.getMapperDB2Path() + separator
				        + table.getName() + "_ext.xml";
				content = FreemarkerUtils.process2String(sqlMapperExtTpl, model);
				writeFile(content, filePath, false);
				logger.info("SqlMapperDB2Ext: {}", filePath);
				
				filePath = CodegenConfigUtils.getMapperMysqlPath() + separator
				        + table.getName() + ".xml";
				content = FreemarkerUtils.process2String(sqlMapperMysqlTpl, model);
				writeFile(content, filePath, true);
				logger.info("SqlMapperMysql: {}", filePath);
				
				filePath = CodegenConfigUtils.getMapperMysqlPath() + separator
				        + table.getName() + "_ext.xml";
				content = FreemarkerUtils.process2String(sqlMapperExtTpl, model);
				writeFile(content, filePath, false);
				logger.info("SqlMapperMysqlExt: {}", filePath);*/
            }

            if(null == genFileType || genFileType.contains("D")) {
                filePath = CodegenConfigUtils.getJavaBasePath() + separator + "dao"
                        + separator + table.getClassName() + "Dao.java";
                content = FreemarkerUtils.process2String(daoTpl, model);
                writeFile(content, filePath, false);
                logger.info("Dao: {}", filePath);
            }

            if(null == genFileType || genFileType.contains("S")) {
                filePath = CodegenConfigUtils.getJavaBasePath() + separator + "service"
                        + separator + table.getClassName() + "Service.java";
                content = FreemarkerUtils.process2String(serviceTpl, model);
                writeFile(content, filePath, false);
                logger.info("Service: {}", filePath);
            }

            if(null == genFileType || genFileType.contains("C")) {
                filePath = CodegenConfigUtils.getJavaBasePath() + separator + "controller"
                        + separator + table.getClassName() + "Controller.java";
                content = FreemarkerUtils.process2String(controllerTpl, model);
                writeFile(content, filePath, false);
                logger.info("Controller: {}", filePath);
            }

            if(null == genFileType || genFileType.contains("J")) {
                filePath = CodegenConfigUtils.getViewBasePath() + separator
                        + table.getClassName() + "List.jsp";
                content = FreemarkerUtils.process2String(listTpl, model);
                writeFile(content, filePath, false);
                logger.info("viewList: {}", filePath);

                filePath = CodegenConfigUtils.getViewBasePath() + separator
                        + table.getClassName() + "Form.jsp";
                content = FreemarkerUtils.process2String(formTpl, model);
                writeFile(content, filePath, false);
                logger.info("viewForm: {}", filePath);
            }
        }

        logger.info("Generate Success.");
    }

    /**
     * 将内容写入文件
     *
     * @param content  文件内容
     * @param filePath 路径
     * @param cover 是否覆盖已有文件
     */
    public static void writeFile(String content, String filePath, boolean cover) {
        if(null == content || null == filePath) {
            return;
        }
        BufferedWriter bufferedWriter = null;
        try {
            File file = new File(filePath);
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            if (cover || !file.exists()) {
                bufferedWriter = new BufferedWriter(
                        new OutputStreamWriter(new FileOutputStream(file, false), "UTF-8"));
                bufferedWriter.write(content);
            } else {
                logger.info("文件[{}]已存在，直接跳过！", filePath);
            }
        } catch (Exception e) {
            logger.error("写到文件时出错", e);
        } finally {
            if(null != bufferedWriter) {
                try {
                    bufferedWriter.flush();
                    bufferedWriter.close();
                } catch (IOException e) {
                    logger.error("关闭文件时出错", e);
                }
            }
        }
    }


}
