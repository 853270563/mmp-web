package cn.com.yitong.util;

import java.util.Comparator;
import java.util.Map;

public class SortProdInfo implements Comparator<Map> {

	private Map sortMap;

	public SortProdInfo(Map sortMap) {
		this.sortMap = sortMap;
	}

	@Override
	public int compare(Map arg0, Map arg1) {
		String ord0 = ((Map) arg0).get("BUSI_TYP") + "";
		String ord1 = ((Map) arg1).get("BUSI_TYP") + "";

		int a = getOrder(ord0);
		int b = getOrder(ord1);
		System.out.println("ord0" + ord0 + ":" + a + "\tord1" + ord1 + "\t b:"
				+ b);
		return a <= b ? 1 : -1;
	}

	private int getOrder(String busiType) {
		if (StringUtil.isNotEmpty(busiType)) {
			if (sortMap.containsKey(busiType)) {
				try {
					return Integer.parseInt(sortMap.get(busiType) + "");
				} catch (Exception e) {
				}
			}
		}
		return 0;
	}
}
