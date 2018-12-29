package cn.com.yitong.modules.ares.servCtl.dao.impl;

import java.util.Date;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import cn.com.yitong.common.utils.ConfigName;
import cn.com.yitong.common.utils.ConfigUtils;
import cn.com.yitong.core.session.Session;
import cn.com.yitong.core.session.SessionListener;
import cn.com.yitong.core.session.util.SessionManagerUtils;
import cn.com.yitong.modules.ares.servCtl.dao.ServCtlDao;

/**
 * 用数据库表方式实现事件并发控制
 * @author zhuzengpeng<zzp@yitong.com.cn>
 */
public class ServCtlDatabaseDaoImpl implements ServCtlDao, SessionListener{
	
    private static Logger logger = LoggerFactory.getLogger(ServCtlDatabaseDaoImpl.class);
    private final String aresEventLog = ConfigUtils.getValue(ConfigName.SCHAME_CONFIGPLAT,
            ConfigName.SCHAME_CONFIGPLAT_DEFVAL)
            + ".ARES_EVENT_LOG";
    //使用SPRING JdbcTemplate操作数据库
    @Resource
    private JdbcTemplate jdbcTemplate; 
    //事件执行中
    public static final Integer EVENT_RUNNING = 1;
    //事件正常结束
    public static final Integer EVENT_NORMAL_FINISH = 2;
    //事件通过SESSION过期触发结束
    public static final Integer EVENT_SESSION_FINISH = 3;
    //事件可能因为各种原因无法结束,通过定时器定时找到已经执行超过一小时仍然没有结束的事件并强制结束
    public static final Integer EVENT_FORCE_FINISH = 4;
    
    @PostConstruct
    public void init() {
        SessionManagerUtils.resigerListener(this);
    }
    
	@Override
	public int getAccessCount(String eventId) {
		/**String sql = "select count(*) as \"totalEvent\" from " + aresEventLog + " where EVENT_ID = ? and STATUS = ?";
		Map<String, Object> map = jdbcTemplate.queryForMap(sql, eventId, ServCtlDatabaseDaoImpl.EVENT_RUNNING);
		Object totalEvent = map.get("totalEvent");
		if(totalEvent != null) {
			return Integer.parseInt(totalEvent.toString());
		}*/
		return 0;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
	/**
	 * 开始事件并发控制
	 * @param sessionId
	 * @param eventId
	 * @return
	 */
	@Override
	public void startEvent(String sessionId, String eventId) {
		logger.info("开始事件并发控制, eventId:{}, sessionId:{}", eventId, sessionId);
		/**String deleteSql = "delete from " + aresEventLog + " where SESSION_ID = ?";
		String insertSql = "insert into " + aresEventLog + "(SESSION_ID, EVENT_ID, USER_ID, START_TIME, STATUS) " +
				"values(?, ?, ?, ?, ?)";
		jdbcTemplate.update(deleteSql, sessionId);
		jdbcTemplate.update(insertSql, sessionId, eventId, "", new Date(), ServCtlDatabaseDaoImpl.EVENT_RUNNING);*/
	}

	/**
	 * 结束事件并发控制
	 * @param sessionId
	 * @param eventId
	 */
	@Override
	public void stopEvent(String sessionId, String eventId) {
		logger.info("结束事件并发控制, eventId:{}, sessionId:{}", eventId, sessionId);
		//String sql = "update " + aresEventLog + " set END_TIME = ?, STATUS = ? where SESSION_ID = ?";
		//jdbcTemplate.update(sql, new Date(), ServCtlDatabaseDaoImpl.EVENT_NORMAL_FINISH, sessionId);
	}

	@Override
	public void onStart(Session session) {
	}

	@Override
	public void onStop(Session session) {
		stopEvent(session.getId(), null);
	}

	@Override
	public void onExpiration(Session session) {
		stopEvent(session.getId(), null);
	}	
}
