package cn.com.yitong.ares.jstl;

import java.util.Map;

import cn.com.yitong.ares.jstl.function.JstlFnUtil;
import cn.com.yitong.ares.jstl.lang.Evaluator;

/**
 * 数据总线的表达式运算辅助工具类
 * 
 * @作者：yym
 * @邮箱：yym@yitong.com.cn
 * @创建时间：2016年9月11日 上午9:49:56
 * @版本信息：
 */
public class JstlUtil {

	private static Evaluator eval = new Evaluator();
	private static final String PLAT = "ares";

	/**
	 * 执行条件表达式
	 * 
	 * @param ctx
	 * @param expr
	 * @return
	 */
	public static boolean evalBoolean(PageContext ctx, String expr) {
		Map m = JstlFnUtil.getInstance().initFunction();
		return (Boolean) eval.evaluate(PLAT, expr, Boolean.class, null, ctx, m, "fn");
	}

	/**
	 * 数值表达式
	 * 
	 * @return
	 */
	public static double evalDubble(PageContext ctx, String expr) {
		return (Double) eval.evaluate(PLAT, expr, Double.TYPE, null, ctx);
	}

	/**
	 * 取整表达式
	 * 
	 * @param ctx
	 * @param expr
	 * @return
	 */
	public static int evalInt(PageContext ctx, String expr) {
		return (Integer) eval.evaluate(PLAT, expr, Integer.TYPE, null, ctx);
	}

	/**
	 * 取值表达式
	 * 
	 * @return
	 */
	public static String evalString(PageContext ctx, String expr) {
		Map m = JstlFnUtil.getInstance().initFunction();
		return eval.evaluate(PLAT, expr, String.class, null, ctx, m, "fn").toString();
	}

	/**
	 * 取值表达式
	 * 
	 * @return
	 */
	public static Object eval(PageContext ctx, String expr) {
		return eval.evaluate(PLAT, expr, Object.class, null, ctx);
	}
}
