package cn.com.yitong.util;

import java.util.Comparator;
import java.util.Map;

public class SortAcctNo implements Comparator<Map> {

	@Override
	public int compare(Map arg0, Map arg1) {
		// TODO Auto-generated method stub
		String ord0 = ((Map)arg0).get("ORD")+"";
		String ord1 = ((Map)arg1).get("ORD")+"";
		
		int flag = ord0.compareTo(ord1);
		return flag;
	}

}
