package cn.com.yitong.core.base;

import cn.com.yitong.common.persistence.Page;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhanglong
 * @date 17/8/21
 */
public class YtPage<T> extends Page {

    private long count;// 总记录数，设置为“-1”表示不查询总数

    public YtPage(int pageNo, int pageSize) {
        this(pageNo, pageSize, 0);
    }

    /**
     * 构造方法
     * @param pageNo 当前页码
     * @param pageSize 分页大小
     * @param count 数据条数
     */
    public YtPage(int pageNo, int pageSize, long count) {
        this(pageNo, pageSize, count, new ArrayList<T>());
    }

    /**
     * 构造方法
     * @param pageNo 当前页码
     * @param pageSize 分页大小
     * @param count 数据条数
     * @param list 本页数据对象列表
     */
    public YtPage(int pageNo, int pageSize, long count, List<T> list) {
        this.setCount(count);
        this.setPageNo(pageNo);
        this.setPageSize(pageSize);
        this.setList(list);
    }

    public int getFirstResult(){
        int firstResult = (getPageNo() - 1) * getPageSize();
        return firstResult;
    }

    public int getLastResult(){
        int lastResult = getFirstResult()+getMaxResults();
        return lastResult;
    }

    @Override
    public String toString() {
        return "pageNo=" + getPageNo() + ", pageSize=" + getPageSize();
    }

    @Override
    public void setCount(long count) {
        this.count = count;
    }
}
