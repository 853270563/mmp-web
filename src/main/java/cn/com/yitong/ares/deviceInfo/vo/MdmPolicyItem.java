package cn.com.yitong.ares.deviceInfo.vo;

/**
 * @author zhanglong@yitong.com.cn
 * @date 15/9/18
 */
public class MdmPolicyItem {

    private String policyItemId;

    private String name;

    private String code;

    private String policyType;

    private String policyId;

    private String policyPriority;

    private String configId;

    private String policyItemValue;

    private String policyItemType;

    private String itemSystem;

    public String getPolicyItemId() {
        return policyItemId;
    }

    public void setPolicyItemId(String policyItemId) {
        this.policyItemId = policyItemId;
    }

    public String getPolicyItemType() {
        return policyItemType;
    }

    public void setPolicyItemType(String policyItemType) {
        this.policyItemType = policyItemType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getPolicyType() {
        return policyType;
    }

    public void setPolicyType(String policyType) {
        this.policyType = policyType;
    }

    public String getPolicyId() {
        return policyId;
    }

    public void setPolicyId(String policyId) {
        this.policyId = policyId;
    }

    public String getPolicyPriority() {
        return policyPriority;
    }

    public void setPolicyPriority(String policyPriority) {
        this.policyPriority = policyPriority;
    }

    public String getConfigId() {
        return configId;
    }

    public void setConfigId(String configId) {
        this.configId = configId;
    }

    public String getPolicyItemValue() {
        return policyItemValue;
    }

    public void setPolicyItemValue(String policyItemValue) {
        this.policyItemValue = policyItemValue;
    }

    public String getItemSystem() {
        return itemSystem;
    }

    public void setItemSystem(String itemSystem) {
        this.itemSystem = itemSystem;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MdmPolicyItem that = (MdmPolicyItem) o;

        if (code != null ? !code.equals(that.code) : that.code != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return code != null ? code.hashCode() : 0;
    }
}
