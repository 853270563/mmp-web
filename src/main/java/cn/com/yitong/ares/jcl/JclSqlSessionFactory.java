package cn.com.yitong.ares.jcl;

import java.io.IOException;

import org.mybatis.spring.SqlSessionFactoryBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import cn.com.yitong.ares.core.AresConf;

public class JclSqlSessionFactory extends SqlSessionFactoryBean {
	private Logger logger = LoggerFactory.getLogger(getClass());

	JclSqlSessionFactory(String mappingLocation){
		// 相对路径
		String locations = String.format("file:%s%s", AresConf.jclModuleRootPath, mappingLocation);
		logger.info("sql mapping locations:{}", locations);
		ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
		try {
			Resource[] metaInfResources = resourcePatternResolver.getResources(locations);

			this.setMapperLocations(metaInfResources);
		} catch (IOException e) {
			logger.error("ibatis mapping file not found!", e);
		}
	}

}
