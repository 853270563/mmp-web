package cn.com.yitong.framework.datediff;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DateTool {
	
	public static int MON = 1;
	static int DAY = 2;
	
	static SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd");
	
	/**
	 * 获取几个月前或者几天后的日期
	 * @param today 指定日期
	 * @param days  几天前
	 * @return
	 */
	public static String getDate(String today,int number,int type){
		Calendar c = Calendar.getInstance(); 
		Date date= null;
		try { 
			date = new SimpleDateFormat("yy-MM-dd").parse(today); 
		} catch (ParseException e) { 
			e.printStackTrace(); 
		} 
		c.setTime(date); 
		
		if(type == MON){
			c.add(Calendar.MONTH, -number); //月份减N
		}else{
			c.add(Calendar.DATE, number);	//天数加N
		}
		
		String dayBefore=new SimpleDateFormat("yyyy-MM-dd").format(c.getTime()); 
		return dayBefore; 
	}
	
	/**
	 * 根据起始和结束时间计算等分后的日期,用于界面X轴的展现
	 * @param startDate 起始日期
	 * @param endDate	结束日期
	 * @param str 		根据getFLabel计算出的长度
	 * @return
	 */
	public static String[] getXLable(String startDate ,String endDate,String[] oldStr){
		
		int days = getDaysBetweenDate(startDate,endDate);
		int incr = days/5;
		int len = oldStr.length;
		String[] newStr = new String[len];
		String beginDate = newStr[0] = oldStr[0];
		String nextDate = getDate(beginDate,incr,DAY);
		for(int i=1;i<len;i++){
			if(nextDate.equals(oldStr[i])){ //如果下一个计划日期等于原始数组的日期
				newStr[i] = nextDate;
				nextDate = getDate(nextDate,incr,DAY);
			}else{		//否则,将当前数据置空
				newStr[i] = "";
			}
		}
		return newStr;
	}
	
	/**
	 * 根据起始和结束时间计算全日期,用于提供界面数据模块
	 * @param startDate	起始日期
	 * @param endDate	结束日期
	 * @return
	 */
	public static String[] getFLabel(String startDate ,String endDate){
		int days = getDaysBetweenDate(startDate,endDate);
		String[] str = new String[days];
		for(int i=0;i<days;i++){
			str[i] = getDate(startDate,1,DAY);
			startDate = str[i];
		}
		return str;
	}
	
	/**
	 * 将日期型转化为字符串
	 * @param date 日期型
	 * @return
	 */
	public static String toDateString(Date date){
		
		return df.format(date);
	}
	
	/**
	 * 获取两个日期之间的天数
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public static int getDaysBetweenDate(String startDate , String endDate){
	    try {
			long to = df.parse(endDate).getTime();
			long from = df.parse(startDate).getTime();
			return (int) ((to - from) / (1000 * 60 * 60 * 24));
		} catch (ParseException e) {
			e.printStackTrace();
			return -1;
		}
	}
	
	/**
	 * 将输出字段转化成数组
	 * @条件 segIn的类型与srcDate内元素类型必须一致
	 * @参数 srcList	数据库查询列表
	 * @参数 segIn		数据库输入字段
	 * @参数 segOut	数据库输出字段
	 * @return
	 */
	public static String[] getSegOutArr(String[] srcDate ,List srcList, String segIn , String segOut){
		
		Map srcMap = new HashMap();
		String [] str = new String[srcDate.length];
		int len = srcList.size();
		for(int i=0;i<len;i++){
			Map item = (Map) srcList.get(i);
			String key = item.get(segIn).toString();
			srcMap.put(key, item);
		}
		
		len = srcDate.length;
		for(int x=0;x<len;x++ ){
			Map item = (Map) srcMap.get(srcDate[x]);
			if(item!=null){
				str[x] = item.get(segOut).toString(); //为了保持整洁,未考虑数据库字段的其他情况,默认varchar2
			}else{
				str[x] = "0";
			}
		}
		return str;
	}

}
