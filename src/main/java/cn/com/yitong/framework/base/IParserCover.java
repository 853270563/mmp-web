package cn.com.yitong.framework.base;

public interface IParserCover {

	/**
	 * 生成反馈信息
	 * 
	 * @param busiCtx
	 * @param transCode
	 * @return
	 */
	public boolean parserCover(IBusinessContext busiCtx, String transCode);
}
