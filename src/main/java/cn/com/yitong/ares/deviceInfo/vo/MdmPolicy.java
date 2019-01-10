package cn.com.yitong.ares.deviceInfo.vo;

import java.sql.Timestamp;

/**
 * @author zhanglong@yitong.com.cn
 * @date 15/9/18
 */
public class MdmPolicy{

    private String policyId;

    private String policyName;

    private String policyType;

    private String priority;

    private String status;

    private String isDefault;

    private Timestamp createTime;

    public String getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(String isDefault) {
        this.isDefault = isDefault;
    }

    public String getPolicyId() {
        return policyId;
    }

    public void setPolicyId(String policyId) {
        this.policyId = policyId;
    }

    public String getPolicyName() {
        return policyName;
    }

    public void setPolicyName(String policyName) {
        this.policyName = policyName;
    }

    public String getPolicyType() {
        return policyType;
    }

    public void setPolicyType(String policyType) {
        this.policyType = policyType;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MdmPolicy mdmPolicy = (MdmPolicy) o;

        if (policyId != null ? !policyId.equals(mdmPolicy.policyId) : mdmPolicy.policyId != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return policyId != null ? policyId.hashCode() : 0;
    }
}
