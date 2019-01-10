package cn.com.yitong.framework.core.vo;

/**
 * 错误码转译
 * 
 * @author yaoym
 * 
 */
public class ErrorInfo {
	
	private String ERR_CODE;
	private String ERR_INFO_CN;
	private String ERR_INFO_HK;
	private String ERR_INFO_EN;
	private String APP_SYS_TYP;
	private String RELE_ERR_CODE;

	public String getERR_CODE() {
		return ERR_CODE;
	}

	public void setERR_CODE(String eRR_CODE) {
		ERR_CODE = eRR_CODE;
	}

	public String getERR_INFO_CN() {
		return ERR_INFO_CN;
	}

	public void setERR_INFO_CN(String eRR_INFO_CN) {
		ERR_INFO_CN = eRR_INFO_CN;
	}

	public String getERR_INFO_HK() {
		return ERR_INFO_HK;
	}

	public void setERR_INFO_HK(String eRR_INFO_HK) {
		ERR_INFO_HK = eRR_INFO_HK;
	}

	public String getERR_INFO_EN() {
		return ERR_INFO_EN;
	}

	public void setERR_INFO_EN(String eRR_INFO_EN) {
		ERR_INFO_EN = eRR_INFO_EN;
	}

	public String getAPP_SYS_TYP() {
		return APP_SYS_TYP;
	}

	public void setAPP_SYS_TYP(String aPP_SYS_TYP) {
		APP_SYS_TYP = aPP_SYS_TYP;
	}

	public String getRELE_ERR_CODE() {
		return RELE_ERR_CODE;
	}

	public void setRELE_ERR_CODE(String rELE_ERR_CODE) {
		RELE_ERR_CODE = rELE_ERR_CODE;
	}
}
