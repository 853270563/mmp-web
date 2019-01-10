package cn.com.yitong.util.common;

import java.text.DecimalFormat;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

/** 
 * com.util.AmountUtils 
 * @description  金额元分之间转换工具类 
 * @author 
 * @2014-10-20下午12:58:00 
 */  
public class AmountUtils {  
      
    /** 
     * 将分为单位的转换为元 （除100） 
     *  
     * @param amount 
     * @return 
     * @throws Exception  
     */  
    public static String changeF2Y(String amount){  
        return changeF2Y(amount,"#0.0000");  
    }  
    
    /** 
     * 将分为单位的转换为元 （除100） 
     *  
     * @param amount 
     * @return 
     * @throws Exception  
     */  
    public static String changeF2Y(String amount,String format){  
        if(StringUtil.isBlank(amount)) {  
            return "0.00";
        } 
        DecimalFormat   fmt   =   new   DecimalFormat(format);
        Double d = Double.parseDouble(amount);
        return fmt.format(d/100);  
    }  
      
      
    /**  
     * 将元为单位的转换为分 替换小数点 
     *  
     * @param amount 
     * @return 
     */  
    public static String changeY2F(String amount){  
    	if(StringUtil.isBlank(amount)) {  
            return "0";
        } 
    	 DecimalFormat   fmt   =   new   DecimalFormat("#0");
         Double d = Double.parseDouble(amount);
         return fmt.format(d*100);  
    }  
     
    /**
     * 5000.00 ==》5000
     * 
     * @param amt
     * @return
     */
    public static String transAmtFormat(Object amt) {
		String transAmt = String.valueOf(amt);
		if(StringUtils.isBlank(transAmt)){
			return "";
		}
		DecimalFormat fmt  = new DecimalFormat("#");
		
		double amtDou = NumberUtils.toDouble(transAmt, 0);
		
		return fmt.format(amtDou);
	}
    
    /**  
     * 5000 ==>5,000.00
     *  
     * @param amount 
     * @return 
     */  
    public static String formatAmount(String amount){  
    	 DecimalFormat   fmt   =   new   DecimalFormat("#,###,###.00");
         Double d = NumberUtils.toDouble(amount,0);
         return fmt.format(d);  
    } 
    
    /**  
     * 5000 ==>5000.00
     *  
     * @param amount 
     * @return 
     */  
    public static String changeY2Y(String amount){  
    	 DecimalFormat   fmt   =   new   DecimalFormat("#.00");
         Double d = NumberUtils.toDouble(amount,0);
         return fmt.format(d);  
    }
    
    public static void main(String[] args) {
		System.out.println(changeY2Y("1231231.99"));
	}
}  
