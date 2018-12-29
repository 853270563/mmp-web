package cn.com.yitong.modules.service.fileTab.dao;

import cn.com.yitong.common.persistence.mybatis.MybatisBaseDao;
import cn.com.yitong.modules.service.fileTab.model.AresFileTab;

import cn.com.yitong.common.persistence.annotation.MyBatisDao;

/**
 * @Description: 文件表
 * @author sunw@yitong.com.cn
 */
@MyBatisDao
public interface AresFileTabDao extends MybatisBaseDao<AresFileTab> {
	
	public boolean deleteBySerialNo(String imgSerialNo);
}
