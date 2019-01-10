package cn.com.yitong.util;

public class DicTransfrom {
	public static String subTypeTransfrom(String code){
		String [] typeCode = {"1031001","1031002","1031003","1031004","1031005"};
		String [] subTypeCode = {"9006001","9006002","9006003","9006004","9006006","9006007"};
		if(code.equals(typeCode[0])){
			return subTypeCode[0];
		}else if(code.equals(typeCode[1])){
			return subTypeCode[4];
		}else if(code.equals(typeCode[2])){
			return subTypeCode[2];
		}else if(code.equals(typeCode[3])){
			return subTypeCode[3];
		}else if(code.equals(typeCode[4])){
			return subTypeCode[5];
		}else{
			return subTypeCode[0];
		}
	}
}
