package cn.com.yitong.core.base;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author lc3@yitong.com.cn
 */
public class Page<E> implements Serializable, Iterable<E> {

	private static final long serialVersionUID = -7856776322182434667L;

	protected List<E> list;

	protected int pageSize = 10;

	protected int pageNumber = 1;

	protected int totalCount = 0;
	
	public Page() {
		
	}
	public Page(int pageNumber,int pageSize) {
		this(pageNumber,pageSize,0,new ArrayList<E>(0));
	}

	public Page(int pageNumber,int pageSize,int totalCount) {
		this(pageNumber,pageSize,totalCount,new ArrayList<E>(0));
	}

	public Page(int pageNumber,int pageSize,int totalCount,List<E> result) {
		if(pageSize <= 0) throw new IllegalArgumentException("[pageSize] must great than zero");
		this.pageSize = pageSize;
		this.pageNumber = pageNumber;
		this.totalCount = totalCount;
		setList(result);
	}

	public void setList(List<E> elements) {
		if (elements == null)
			throw new IllegalArgumentException("'result' must be not null");
		this.list = elements;
	}

	/**
     * 每一页显示的条目数
     *
     * @return 每一页显示的条目数
     */
	public int getPageSize() {
		return pageSize;
	}

	/**
	 * 当前页包含的数据
	 *
	 * @return 当前页数据源
	 */
	public List<E> getList() {
		return list;
	}

	/**
	 * 得到数据库的第一条记录号
	 * @return
	 */
	public int getFirstResult() {
		return pageNumber * pageSize;
	}

	/**
	 * 总的数据条目数量，0表示没有数据
	 *
	 * @return 总数量
	 */
	public int getTotalCount() {
		return totalCount;
	}

	@SuppressWarnings("unchecked")
	public Iterator<E> iterator() {
		return (Iterator<E>) (list == null ? Collections.emptyList().iterator() : list.iterator());
	}
	public int getPageNumber() {
		return pageNumber;
	}
	public void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
	}
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}
}
