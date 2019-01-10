 /*
 * Copyright(C) 2006 Agree Tech, All rights reserved.
 * 
 * Created on 2006-8-11   by Xu Haibo
 */

package cn.com.yitong.util.natp;

 /**
 * <DL><DT><B>
 * natp 通讯
 * </B></DT><p><DD>
 * 供交易调用
 * </DD></DL><p>
 * 
 * <DL><DT><B>使用范例</B></DT><p><DD>
 * 使用范例说明
 * </DD></DL><p>
 * 
 * @author 徐海波
 * @author agree
 * @version $Revision: 1.1 $date 
 */
public interface INatp {
	 /**
	 * <DL><DT><B>
	 * 初始化NATP通讯区
	 * </B></DT><p><DD>
	 * 第一次用方法Pack_NATP之前用
	 * </DD></DL><p>
	 * @param transCode 交易代码
	 * @param templateCode 模板代码
	 * @param reservedCode 保留代码
	 * @return 成功返回0, 失败返回<0
	 */
	int init(int natpVersion, String transCode, String templateCode, String reservedCode);
	
	 /**
	 * <DL><DT><B>
	 * 将交易信息打成NATP格式数据包
	 * </B></DT><p><DD>
	 * 详细介绍
	 * </DD></DL><p>
	 * @param fieldName 字段名  如："账号"
	 * @param value 字段值
	 * @throws Exception 
	 */
	void  pack(String fieldName, String value) throws Exception;
	
	 /**
	 * <DL><DT><B>
	 * 将交易信息打成NATP格式数据包
	 * </B></DT><p><DD>
	 * 按数组名一次将数组所有有值的元素打成NATP格式数据包
	 * </DD></DL><p>
	 * @param fieldNames
	 * @param values
	 * @throws Exception 
	 */
	
	void pack(String[] fieldNames, String[] values) throws Exception;
	
	 /**
	 * <DL><DT><B>
	 * 将NATP格式数据包中的指定节点解析到变量或数组
	 * </B></DT><p><DD>
	 * 详细介绍
	 * </DD></DL><p>
	 * @param fieldName 节点名  如："账号"
	 * @param iPos 该字段在包中第几次出现
	 * @return 成功返回字符串数组 元素1为节点名，元素2为值，失败返回null
	 * @throws Exception 
	 */
	String unpack(String fieldName, int iPos) throws Exception;
	
	
	 /**
	 * <DL><DT><B>
	 * 将NATP格式数据包中的指定节点解析到变量或数组
	 * </B></DT><p><DD>
	 * 详细介绍
	 * </DD></DL><p>
	 * @param fieldName 节点名  如："账号"
	 * @return 成功返回数组 1为节点名数组，2为值数组，失败返回null
	 * @throws Exception 
	 */
	String[] unpack(String fieldName) throws Exception;
	
	 /**
	 * <DL><DT><B>
	 * 获取字段名在一个数据包中出现的次数
	 * </B></DT><p><DD>
	 * 详细介绍
	 * </DD></DL><p>
	 * @param fieldName 字段名  如："账号"
	 * @return  成功返回字段个数，失败返回<0
	 */
	int getRecordCount(String fieldName);
	
	 /**
	 * <DL><DT><B>
	 * 通讯
	 * </B></DT><p><DD>
	 * 详细介绍
	 * </DD></DL><p>
	 * @param serverName
	 * @return TODO
	 * @throws Exception
	 */
	void exchange(String serverName) throws Exception;
}
