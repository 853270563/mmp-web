package cn.com.yitong.ares.flow;

import cn.com.yitong.framework.base.IBusinessContext;

/**
 * 可编排原子服务
 * 
 * @author yaoym
 * 
 */
public interface IAresSerivce {
	public static int NEXT = 1;
	public static int NEXT2 = 2;
	public static int NEXT3 = 3;
	public static int EXIT = 0;

	public int execute(IBusinessContext ctx);
}
