package cn.com.yitong.framework.core.bean;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import cn.com.yitong.framework.net.IEBankConfCaches;

/**
 * 交易处理类的读取类
 * 
 * @author yym
 * 
 */
@Component
public class EBankConfCaches implements IEBankConfCaches {
	private Map<String, MBTransConfBean> ebankConfList = new HashMap();

	public EBankConfCaches() {
	}

	
	@Override
	public synchronized void addTransConf(String transCode,
			MBTransConfBean trans) {
		ebankConfList.put(transCode, trans);
	}

	
	@Override
	public MBTransConfBean getTransConfById(String transCode) {
		return ebankConfList.get(transCode);
	}

	
	@Override
	public boolean hasTransConfById(String transCode) {
		return ebankConfList.containsKey(transCode);
	}

	@Override
	public void removeCache(String key) {
		ebankConfList.remove(key);

	}
}