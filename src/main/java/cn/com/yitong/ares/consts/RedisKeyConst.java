/**   
* Copyright (c) 2016 Shanghai P&C Information Technology Co.,Ltd. All rights reserved.
* @title: RedisKeyR.java 
* @package cn.com.yitong.ares.consts 
* @description: TODO(用一句话描述该文件做什么) 
* @author 章文兵
* @mail zwb@yitong.com.cn 
* @date 2016年6月16日 上午11:22:52 
* @version V1.0.0   
*/ 
package cn.com.yitong.ares.consts;

/** 
 * @className: RedisKeyConst 
 * @description: Redis key存储常量 
 * @author 章文兵
 * @mail zwb@yitong.com.cn 
 * @date 2016年6月16日 上午11:22:52 
 * @version V1.0.0   
 */
public interface RedisKeyConst {
	
	/**
	 * 客户号键值前缀
	 */
	public final static String CUST_NO_PREFIX="CUST_NO_";
	/**
	 * OA系统登录客户唯一标识
	 */
	public final static String OPER_ID_PREFIX="OPER_ID_";
	
	/**
	 * session 属性数据存储前缀
	 */
	public final static String SESSION_ATTR_PREFIX = "sessionAttr:";
	
	/**
	 * The default prefix for each key and channel in Redis used by Spring Session.
	 */
	public static final String DEFAULT_SPRING_SESSION_REDIS_PREFIX = "spring:session:sessions:";
}
