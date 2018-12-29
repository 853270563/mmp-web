package cn.com.yitong.modules.common.thirdServer.builder;

/**
 * @author zhanglong
 * @date 17/8/29
 */
public interface BuilderParamService<T, E> {

    /**
     * 构建请求参数接口 可有多个实现
     */
    public T builder(E param);
}
