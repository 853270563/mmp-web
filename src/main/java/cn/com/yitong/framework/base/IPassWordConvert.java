package cn.com.yitong.framework.base;


/**
 * 加密设备
 * 
 * @author zhouy
 * 
 */

public interface IPassWordConvert {

	public byte[] getEncryptPinByteSecond(String encryptPassword,
										  String encryptAccount, String ENCR_LMK, String ENCR_WKEY);
}
