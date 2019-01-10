package cn.com.yitong.ares.mp.service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.com.yitong.framework.service.ICrudService;

@Service
public class AresMpMsgTaskService{
    @Autowired
    ICrudService service;
    
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final SimpleDateFormat yyyyMMdd = new SimpleDateFormat("yyyyMMdd");
    private static final String dateStr = yyyyMMdd.format(new Date());
    /**
     * 插入任务列表
     * @param params
     * @throws Exception 
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Transactional
    public Boolean insertAresMpMsgTask(Map params) throws Exception {
        //校验参数是否为空，为空 返回失败
        String taskId = dateStr + service.load("ARES_MP_MSG_TASK.loadSeq", null).get("TASK_ID").toString();
        String clientType=(String)params.get("PUSHCLIENTTYPE");
        params.put("TASKID",taskId);
        if (params.get("ISTMPL") == null) {
            params.put("ISTMPL", "0");
        }
        params.put("STATUS", "3");
        params.put("SENDBEGINTIME", sdf.parse((String) params.get("SENDBEGINTIME")));
        if (params.get("SENDENDTIME") != null) {
            params.put("SENDENDTIME", sdf.parse((String) params.get("SENDENDTIME")));
        }
        params.put("CREATETIME",new Date());
        params.put("UPDATETIME",new Date());
        service.insert("ARES_MP_MSG_TASK.insertAresMpMsgTask", params);
        
        // 订阅用户
    	List<Map<String, Object>> custClientList = service.findList("ARES_MP_MSG_TASK.selectCustClient", params);
    	// 接受客户号来源
        String recCustFrom = (String)params.get("RECCUSTFROM");
        List<String> custes = new ArrayList<String>();
        String recCust = (String)params.get("RECCUST");
        boolean allFlag = false;
        // 全部
        if ("0".equals(recCustFrom)) {
        	allFlag = true;
        } else {
        	custes = Arrays.asList(recCust.split(","));
        }
        for (Map<String, Object> custClient : custClientList) {
        	if (allFlag || custes.contains((String)custClient.get("CUST_ID"))) {
                String taskCustId = dateStr + service.load("ARES_MP_MSG_TASK_CUST.loadSeq", null).get("CUST_ID").toString();
        		Map<String, Object> msgCust = new HashMap<String, Object>();
        		msgCust.put("TASKCUSTID", taskCustId);
        		msgCust.put("TASKID", taskId);
        		msgCust.put("CUSTID", custClient.get("CUST_ID"));
        		msgCust.put("CLIENTTYPE", clientType);
        		msgCust.put("CREATETIME",new Date());
        		msgCust.put("UPDATETIME",new Date());
        		new MsgCustIns(service, msgCust).run();
        	}
        }
        
        return Boolean.TRUE;
    }

    private class MsgCustIns implements Runnable {
    	private ICrudService service;
    	private Map<String, Object> map;
    	public MsgCustIns(ICrudService service, Map<String, Object> map){
    		this.service = service;
    		this.map = map;
    	}
		@Override
		public void run() {
			service.insert("ARES_MP_MSG_TASK_CUST.insertAresMpMsgTaskCust", map);
		}
    }
}