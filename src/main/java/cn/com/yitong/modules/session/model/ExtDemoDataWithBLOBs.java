package cn.com.yitong.modules.session.model;

public class ExtDemoDataWithBLOBs extends ExtDemoData {
    /**
     * REQ_DATA
     */
    private String reqData;

    /**
     * RESP_DATA
     */
    private String respData;

    /**
     * 
     *
     * @return 
     */
    public String getReqData() {
        return reqData;
    }

    /**
     * 
     *
     * @param 
     */
    public void setReqData(String reqData) {
        this.reqData = reqData;
    }

    /**
     * 
     *
     * @return 
     */
    public String getRespData() {
        return respData;
    }

    /**
     * 
     *
     * @param 
     */
    public void setRespData(String respData) {
        this.respData = respData;
    }
}