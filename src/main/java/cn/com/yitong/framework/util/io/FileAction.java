package cn.com.yitong.framework.util.io;

/**
 * Created by wenin819@gmail.com on 2014-05-29.
 */
public enum FileAction {
    /**
     * 文件操作
     */
    C("新增"),  // 创建
    U("更新"),  // 更新
    D("删除"),  // 删除
    S("一致"),  // 一致
    ;
    private String title;

    private FileAction(String title) {
        this.title = title;
    }

    public String getTitle() {
        return this.title;
    }
}
