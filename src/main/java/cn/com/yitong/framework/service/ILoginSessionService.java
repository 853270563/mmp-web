package cn.com.yitong.framework.service;

import java.util.List;
import java.util.Map;

/**
 * 登录日志
 * 
 * @author yaoym
 * 
 */
public interface ILoginSessionService {

	/**
	 * 用户登录时插入操作日志
	 * 
	 * @return
	 */
	public Object saveLgnSessLog(Map<String, String> map);

	/**
	 * 用戶登錄失敗時，修改用戶登錄登錄日誌中的 SESS_SEQ状态(失效)登錄狀態(错误类型)、退出狀態(登录失败)、退出日期、退出时间
	 * 
	 * @param map
	 * @return
	 */
	public boolean updateLgnStat_ExitStat(Map<String, String> map);

	/**
	 * 登录成功后<br>
	 * 给当次登录日志中的CIF_NO、TFB_CIF_NO赋值<br>
	 * 修改当次登录日志中的sess_seq狀態為可用、登錄狀態為登錄成功
	 * 
	 * @param map
	 * @return
	 */
	public boolean updateSessionSuccessMsg(Map<String, String> map);

	/**
	 * 登录成功后<br>
	 * 將用戶非本次登录日志中的退出結果为0的（未設置）全部改成2（会话过期退出）<br>
	 * 
	 * @param map
	 * @return
	 */
	public boolean exitStatusByLgn_id(Map<String, String> map);

	/**
	 * 登录成功后<br>
	 * 將用戶非本次登录日志中的SESS_SEQ狀態3不可用<br>
	 * 
	 * @param map
	 * @return
	 */
	public boolean sessionStatusByLgn_id(Map<String, String> map);

	/**
	 * 保存会话过期信息
	 * 
	 * @param map
	 */
	void saveSessionOutInfo(Map<String, String> map);

	/**
	 * 查询用户的会话信息
	 * 
	 * @param cifNo
	 * @return
	 */
	public String getSessSeqByCifNo(Map<String, String> map);

	/**
	 * 獲取用戶上次退出狀態
	 * 
	 * @param map
	 * @return
	 */
	public String findLastExitStat(Map<String, String> map);

	/**
	 * 安全退出时，修改用户的SESS_SEQ状态为不可用、退出状态为正常退出、退出日期、退出时间
	 * 
	 * @param map
	 * @return
	 */
	public boolean updateSess_SeqStat(Map<String, String> map);

	/**
	 * 登录查询
	 * 
	 * @param map
	 * @return
	 */
	public List pageQuery4Login(Map<String, String> map);
	
	/**
	 * 根据主键sessId查询
	 * @param sessId
	 * @return
	 */
	public List findBySessID(String sessId);
}
