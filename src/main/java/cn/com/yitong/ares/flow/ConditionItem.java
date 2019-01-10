/**
 * 
 */
package cn.com.yitong.ares.flow;

/**
 * 条件判断
 * 
 * @作者：yaoyimin
 * @邮箱：yym@yitong.com.cn
 * @创建时间：2016年9月10日 上午10:56:04
 * @版本信息：
 */
public class ConditionItem {

	private String condition;
	private int index;
	private int nextStepIndex;

	/**
	 * @param condition
	 * @param index
	 * @param nextStepIndex
	 */
	public ConditionItem(String condition, int index, int nextStepIndex) {
		super();
		this.condition = condition;
		this.index = index;
		this.nextStepIndex = nextStepIndex;
	}

	/**
	 * @return the condition
	 */
	public String getCondition() {
		return condition;
	}

	/**
	 * @param condition
	 *            the condition to set
	 */
	public void setCondition(String condition) {
		this.condition = condition;
	}

	/**
	 * @return the index
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * @param index
	 *            the index to set
	 */
	public void setIndex(int index) {
		this.index = index;
	}

	/**
	 * @return the nextStepIndex
	 */
	public int getNextStepIndex() {
		return nextStepIndex;
	}

	/**
	 * @param nextStepIndex
	 *            the nextStepIndex to set
	 */
	public void setNextStepIndex(int nextStepIndex) {
		this.nextStepIndex = nextStepIndex;
	}

	@Override
	public String toString() {
		return String.format("{index:%d,condition:%s,nextStepIndex:%d}", this.index, this.condition, this.nextStepIndex);
	}
}
