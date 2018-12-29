package cn.com.yitong.modules.common.thirdServer.parser;

/**
 * @author zhanglong
 * @date 17/8/29
 */
public interface ParserResultService<T, E> {

    /**
     * 解析返回结果 接口
     */
    public T parser(E result);
}
