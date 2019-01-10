package cn.com.yitong.ares.flow;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.com.yitong.ares.jstl.JstlUtil;
import cn.com.yitong.framework.base.IBusinessContext;

public class AresFlowData {

	private Pattern pattern = Pattern.compile("\\$\\{[^\\$}]+\\}");

	/**
	 * 简单的赋值及转译代码
	 */
	private Map<String, String> datas = new HashMap();

	/**
	 * 执行简单的赋值及转译
	 * 
	 * @param ctx
	 */
	public void evalPrevDatas(IBusinessContext ctx) {
		evalPrevDatas(ctx, null);
	}

	/**
	 * 执行简单的赋值及转译
	 * 
	 * @param ctx
	 */
	public void evalPrevDatas(IBusinessContext ctx, List<String> tmpKeys) {
		if (null == datas)
			return;
		for (String key : datas.keySet()) {
			String expr = datas.get(key);
			Object value = this.evalExpress(expr, ctx);

			if (key.startsWith("*SESS_")) {
				// 注意：会话赋值, 只能从内部赋值，不能从由请求报文字段直接创建同名的会话；
				ctx.saveSessionText(key.substring(1), value);
			} else {
				// 参数赋值
				ctx.setParam(key, value);
			}
			if (key.startsWith("*") && null != tmpKeys) {
				tmpKeys.add(key);
			}
		}
	}

	public Map getDatas() {
		return datas;
	}

	public void setDatas(Map datas) {
		this.datas = datas;
	}

	private Object evalExpress(String exp, IBusinessContext ctx) {
		Matcher matcher = pattern.matcher(exp);
		while (matcher.find()) {
			String value = matcher.group();
			Object o = JstlUtil.eval(ctx, value);
			if (o instanceof List || o instanceof Map) {
				return JstlUtil.eval(ctx, value);
			} else {
				String convertValue = JstlUtil.evalString(ctx, value);
				exp = exp.replace(value, convertValue);
			}
		}
		return exp;
	}

	public static void main(String[] args) {
		Pattern pattern = Pattern.compile("\\$\\{[^\\$}]+\\}");
		Matcher matcher = pattern.matcher("${sdas}");
		System.out.println(matcher.find());
		System.out.println(matcher.group());
		System.out.println("${sdas}".replace("${sdas}", "12"));
	}
}
