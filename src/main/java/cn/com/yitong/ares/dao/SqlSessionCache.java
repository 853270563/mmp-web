package cn.com.yitong.ares.dao;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import cn.com.yitong.ares.core.AresApp;

public class SqlSessionCache {
	private Logger logger = LoggerFactory.getLogger(getClass());

	private SqlSessionFactory sqlSessionFactory;
	private Resource[] mapperLocations;
	private String packageSearchPath;
	private File root;
	private HashMap<String, Long> fileMapping = new HashMap<String, Long>();// 记录文件是否变化

	private long _lastModified = 0;

	public void refreshMapper() {
		if (this.sqlSessionFactory == null) {
			return;
		}
		boolean init = (_lastModified == 0);
		if (root != null) {
			long modified = root.lastModified();
			if (_lastModified == modified) {
				return;
			}
			_lastModified = modified;
		}
		try {
			Configuration configuration = this.sqlSessionFactory.getConfiguration();
			// step.1 扫描文件
			try {
				this.scanMapperXml();
			} catch (IOException e) {
				logger.error("packageSearchPath扫描包路径配置错误");
				return;
			}
			this.removeConfig(configuration);
			for (Resource configLocation : mapperLocations) {
				String fileName = configLocation.getFilename();
				try {
					long lastModified = configLocation.getFile().lastModified();
					/*
					 * if(now - lastModified > 1000 * 60 * 60 ){ continue;//1个小时以前的sql不做重新部署 }
					 */
					if (fileMapping.get(fileName) != null && lastModified == fileMapping.get(fileName)) {
						continue;// 未有变化
					}
					if (fileName.endsWith("public.xml")) {
						continue;// 分页函数不部署
					}
					if (!init) {
						clearMap(configuration, "mappedStatements", configLocation);
						clearMap(configuration, "sqlFragments", configLocation);

						XMLMapperBuilder xmlMapperBuilder = new XMLMapperBuilder(configLocation.getInputStream(),
								configuration, configLocation.toString(), configuration.getSqlFragments());
						xmlMapperBuilder.parse();
						logger.info("mapper文件[{}]缓存加载成功", fileName);

					}
					fileMapping.put(fileName, lastModified);
				} catch (IOException e) {
					logger.error("mapper文件[{}]不存在或内容格式不对", fileName);
					continue;
				} catch (Exception e) {
					logger.error("mapper文件[{}]文件名和namespace不一样", fileName);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setPackageSearchPath(String packageSearchPath) {
		this.packageSearchPath = packageSearchPath;
	}

	public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
		this.sqlSessionFactory = sqlSessionFactory;
	}

	/**
	 * 扫描xml文件所在的路径
	 * 
	 * @throws IOException
	 */
	private void scanMapperXml() throws IOException {
		if (this.root == null) {
			this.root = AresApp.getInstance().getFile(packageSearchPath);
			logger.debug("packageSearchPath 扫描路径:{}", packageSearchPath);
		}
		this.mapperLocations = AresApp.getInstance().getSpringCtx().getResources(packageSearchPath + "/**/*.xml");
	}

	/**
	 * 清空Configuration中几个重要的缓存
	 * 
	 * @param configuration
	 * @throws Exception
	 */
	private void removeConfig(Configuration configuration) throws Exception {
		Class<?> classConfig = configuration.getClass();
		// clearMap(classConfig, configuration, "mappedStatements");
		clearMap(classConfig, configuration, "caches");
		clearMap(classConfig, configuration, "resultMaps");
		clearMap(classConfig, configuration, "parameterMaps");
		clearMap(classConfig, configuration, "keyGenerators");
		clearMap(classConfig, configuration, "sqlFragments");
		clearSet(classConfig, configuration, "loadedResources");
	}

	/**
	 * 清空单个缓存文件
	 * 
	 * @param configuration
	 * @param fieldName
	 * @param configLocation
	 * @throws Exception
	 */
	private void clearMap(Configuration configuration, String fieldName, Resource configLocation) throws Exception {
		String name = configLocation.getFilename().replaceAll(".xml", "");
		Class<?> classConfig = configuration.getClass();
		Field field = classConfig.getDeclaredField(fieldName);
		field.setAccessible(true);
		Map mapConfig = (Map) field.get(configuration);

		List<String> deleteList = new ArrayList();
		Set set = mapConfig.keySet();
		Iterator iter = set.iterator();
		while (iter.hasNext()) {
			String key = (String) iter.next();
			if (key.toUpperCase().startsWith(name.toUpperCase() + ".")
					|| key.toUpperCase().startsWith(name.toUpperCase().substring(0, name.length() - 1) + ".")) {
				deleteList.add(key);
			}
		}

		for (String key : deleteList) {
			mapConfig.remove(key);
			;
		}
	}

	@SuppressWarnings("rawtypes")
	private void clearMap(Class<?> classConfig, Configuration configuration, String fieldName) throws Exception {
		Field field = classConfig.getDeclaredField(fieldName);
		field.setAccessible(true);
		Map mapConfig = (Map) field.get(configuration);
		mapConfig.clear();
	}

	@SuppressWarnings("rawtypes")
	private void clearSet(Class<?> classConfig, Configuration configuration, String fieldName) throws Exception {
		Field field = classConfig.getDeclaredField(fieldName);
		field.setAccessible(true);
		Set setConfig = (Set) field.get(configuration);
		setConfig.clear();
	}

}
