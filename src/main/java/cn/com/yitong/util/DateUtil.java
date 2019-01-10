package cn.com.yitong.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;

import cn.com.yitong.consts.Properties;

/**
 * 日期格式化
 * 
 */
public class DateUtil {

	public static final String DATE_FORMATTER = Properties
			.getString("DATE_FORMATTER");

	public static final SimpleDateFormat sdf = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss.SSS");
	public static final SimpleDateFormat ymd = new SimpleDateFormat(
			"yyyy-MM-dd");

	public static Date parseDate(String dateStr, String format) {
		if (StringUtil.isEmpty(dateStr))
			return null;
		Date date = null;
		try {
			java.text.DateFormat df = new SimpleDateFormat(format);
			String dt = dateStr;// .replaceAll("-", "/");
			if ((!dt.equals("")) && (dt.length() < format.length())) {
				dt += format.substring(dt.length()).replaceAll("[YyMmDdHhSs]",
						"0");
			}
			date = (Date) df.parse(dt);
		} catch (Exception e) {
		}
		return date;
	}

	public static Date parseDate(String dateStr) {
		return parseDate(dateStr, DATE_FORMATTER);
	}

	public static Date parseDate(String dateStr, SimpleDateFormat format) {
		if (StringUtil.isEmpty(dateStr))
			return null;
		Date date = null;
		try {
			date = format.parse(dateStr);
		} catch (Exception e) {
			System.out.println(dateStr);
			e.printStackTrace();
		}
		return date;
	}

	public static String todayStr(String format) {
		return formatDateToStr(new Date(), format);
	}

	public static Date today(String format) {
		return parseDate(todayStr(format), format);
	}

	public static String todayStr() {
		return formatDateToStr(new Date(), DATE_FORMATTER);
	}

	public static String todayfulldata() {
		SimpleDateFormat dateformat1 = new SimpleDateFormat("yyyyMMddHHmmss");
		String a1 = dateformat1.format(new Date());
		return a1;
	}

	public static Date today() {
		return parseDate(todayStr(), DATE_FORMATTER);
	}

	/**
	 * @param date
	 *            需要格式化的日期對像
	 * @param formatter
	 *            格式化的字符串形式
	 * @return 按照formatter指定的格式的日期字符串
	 * @throws java.text.ParseException
	 *             無法解析的字符串格式時拋出
	 */
	public static String formatDateToStr(Date date, String formatter) {
		if (date == null)
			return "";
		try {
			return new SimpleDateFormat(formatter).format(date);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 生成默认格式的日期
	 * 
	 * @param date
	 * @return
	 */
	public static String formatDateToStr(Date date) {
		return formatDateToStr(date, DATE_FORMATTER);
	}

	/**
	 * 將日期按照指定的模式格式化
	 * 
	 * @param date
	 *            {@link java.util.Date}
	 * @param format
	 *            如：yyyy/MM/dd
	 * @return 返回空表示格式化產生異常
	 */
	public static String format(Date date, String format) {
		String result = "";
		try {
			if (date != null) {
				java.text.DateFormat df = new SimpleDateFormat(format);
				result = df.format(date);
			}
		} catch (Exception e) {
		}
		return result;
	}

	/**
	 * 将一种字符日期转化成另外一种日期格式
	 * 
	 * @param date
	 * @param fmtSrc
	 * @param fmtTag
	 * @return
	 */
	public static String format(String date, String fmtSrc, String fmtTag) {
		if (null == fmtSrc)
			return null;
		if (fmtSrc.equals(fmtTag)) {
			return date;
		}
		String year, month, daty;
		int Y, M, D;
		fmtSrc = fmtSrc.toUpperCase();
		Y = fmtSrc.indexOf("YYYY");
		M = fmtSrc.indexOf("MM");
		D = fmtSrc.indexOf("DD");
		if (Y < 0 || M < 0 || D < 0) {
			return date;
		}
		year = date.substring(Y, Y + 4);
		month = date.substring(M, M + 2);
		daty = date.substring(D, D + 2);
		fmtTag = fmtTag.toUpperCase();
		fmtTag = fmtTag.replaceAll("YYYY", year);
		fmtTag = fmtTag.replaceAll("MM", month);
		fmtTag = fmtTag.replaceAll("DD", daty);
		return fmtTag;
	}

	/**
	 * 計算指定年月的日期數
	 * 
	 * @param year
	 * @param month
	 * @return
	 */
	public static int maxDayOfMonth(int year, int month) {
		switch (month) {
		case 1:
		case 3:
		case 5:
		case 7:
		case 8:
		case 10:
		case 12:
			return 31;
		case 4:
		case 6:
		case 9:
		case 11:
			return 30;
		case 2:
			boolean isRunYear = (year % 400 == 0)
					|| (year % 4 == 0 && year % 100 != 0);
			return isRunYear ? 29 : 28;
		default:
			return 31;
		}
	}

	/**
	 * 获取指定年月的日期數
	 * 
	 * @param year
	 * @param month
	 * @return
	 */
	public static int maxDayOfMonth(String year, String month) {
		return maxDayOfMonth(Integer.parseInt(year), Integer.parseInt(month));
	}

	/**
	 * 获取指定年月上月月末日期
	 * 
	 * @param year
	 * @param month
	 * @return
	 */
	public static String getLastMonthDate(String year, String month) {
		return getLastMonthDate(Integer.parseInt(year), Integer.parseInt(month));
	}

	/**
	 * 获取指定年月上月月末日期
	 * 
	 * @param year
	 * @param month
	 * @return
	 */
	public static String getLastMonthDate(int year, int month) {
		if (month <= 1) {
			year -= 1;
			month = 12;
		} else {
			month -= 1;
		}
		StringBuffer bfDate = new StringBuffer();
		bfDate.append(year);
		if (month < 10)
			bfDate.append("0");
		bfDate.append(month);
		bfDate.append(maxDayOfMonth(year, month));
		return bfDate.toString();
	}

	/**
	 * 提前N天的日期
	 * 
	 * @param date
	 * @param days
	 * @return
	 */
	public static Date beforeDate(Date date, int days) {
		Calendar c = new GregorianCalendar();
		c.setTime(date);
		c.add(Calendar.DAY_OF_YEAR, -days);
		return c.getTime();

	}

	/**
	 * @param date
	 * @param days
	 * @return
	 */
	public static Date addHour(Date date, int hour) {
		Calendar c = new GregorianCalendar();
		c.setTime(date);
		c.add(Calendar.HOUR_OF_DAY, hour);
		return c.getTime();
	}

	/**
	 * 一周前的日期
	 * 
	 * @return
	 */
	public static Date getLastWeek() {
		return getNextDay(-7);
	}

	/**
	 * 取相对天数，正数为向后，负数为向前
	 * 
	 * @param day
	 * @return
	 */
	public static Date getNextDay(int day) {
		Calendar c = new GregorianCalendar();
		c.add(Calendar.DAY_OF_YEAR, day);
		return c.getTime();
	}

	public static int curHour(Calendar cal) {
		return cal.get(Calendar.HOUR_OF_DAY);
	}

	public static int curMinute(Calendar cal) {
		return cal.get(Calendar.MINUTE);
	}

	public static int curSecond(Calendar cal) {
		return cal.get(Calendar.SECOND);
	}

	public static String curTimeStr() {
		Calendar cal = new GregorianCalendar();
		// 分别取得当前日期的年、月、日
		StringBuffer bf = new StringBuffer(10);
		int hour = cal.get(Calendar.HOUR_OF_DAY);
		if (hour < 10)
			bf.append("0");
		bf.append(hour);
		bf.append(":");
		int minite = cal.get(Calendar.MINUTE);
		if (minite < 10)
			bf.append("0");
		bf.append(minite);
		bf.append(":");
		int second = cal.get(Calendar.SECOND);
		if (second < 10)
			bf.append("0");
		bf.append(second);
		return bf.toString();
	}

	/***************************************************************************
	 * @功能 计算当前日期某年的第几周
	 * @return interger
	 * @throws java.text.ParseException
	 **************************************************************************/
	public static int getWeekNumOfYear() {
		Calendar calendar = new GregorianCalendar();
		int iWeekNum = calendar.get(Calendar.WEEK_OF_YEAR);
		return iWeekNum;
	}

	/***************************************************************************
	 * @功能 计算指定日期某年的第几周
	 * @return interger
	 * @throws java.text.ParseException
	 **************************************************************************/
	public static int getWeekNumOfYearDay(String strDate) {
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(parseDate(strDate));
		int iWeekNum = calendar.get(Calendar.WEEK_OF_YEAR);
		return iWeekNum;
	}

	/***************************************************************************
	 * @功能 计算某年某周的开始日期
	 * @return interger
	 * @throws java.text.ParseException
	 **************************************************************************/
	public static String getWeekFirstDay(int yearNum, int weekNum) {
		Calendar cal = new GregorianCalendar();
		cal.set(Calendar.YEAR, yearNum);
		cal.set(Calendar.WEEK_OF_YEAR, weekNum);
		cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		// 分别取得当前日期的年、月、日
		String tempYear = Integer.toString(yearNum);
		String tempMonth = Integer.toString(cal.get(Calendar.MONTH) + 1);
		String tempDay = Integer.toString(cal.get(Calendar.DATE));
		return tempYear + "-" + tempMonth + "-" + tempDay;
	}

	public static String getWeekFirstDay(int weekNum) {
		Calendar cal = new GregorianCalendar();
		cal.set(Calendar.WEEK_OF_YEAR, weekNum);
		cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		// 分别取得当前日期的年、月、日
		String tempMonth = Integer.toString(cal.get(Calendar.MONTH) + 1);
		String tempDay = Integer.toString(cal.get(Calendar.DATE));
		return cal.get(Calendar.YEAR) + "-" + tempMonth + "-" + tempDay;
	}

	/***************************************************************************
	 * @功能 计算某年某周的结束日期
	 * @return interger
	 * @throws java.text.ParseException
	 **************************************************************************/
	public static String getWeekEndDay(int yearNum, int weekNum) {
		Calendar cal = new GregorianCalendar();
		cal.set(Calendar.YEAR, yearNum);
		cal.set(Calendar.WEEK_OF_YEAR, weekNum + 1);
		cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
		// 分别取得当前日期的年、月、日
		String tempMonth = Integer.toString(cal.get(Calendar.MONTH) + 1);
		String tempDay = Integer.toString(cal.get(Calendar.DATE));
		return cal.get(Calendar.YEAR) + "-" + tempMonth + "-" + tempDay;
	}

	public static String getWeekEndDay(int weekNum) {
		Calendar cal = new GregorianCalendar();
		cal.set(Calendar.WEEK_OF_YEAR, weekNum + 1);
		cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
		// 分别取得当前日期的年、月、日
		String tempMonth = Integer.toString(cal.get(Calendar.MONTH) + 1);
		String tempDay = Integer.toString(cal.get(Calendar.DATE));
		return cal.get(Calendar.YEAR) + "-" + tempMonth + "-" + tempDay;
	}

	public static String getDatePreHours(int preHours) {
		// 当前日期
		Date date = new Date();
		// 格式化对象
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMATTER
				+ " HH:mm:ss");
		// 日历对象
		Calendar calendar = new GregorianCalendar();
		// 设置当前日期
		calendar.setTime(date);
		// 小时增减
		calendar.add(Calendar.HOUR_OF_DAY, preHours);

		return sdf.format(calendar.getTime());
	}

	public static String getDate() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return sdf.format(new Date());
	}

	public static String getTime() {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		return sdf.format(new Date());
	}

	/**
	 * 获取指定日期的下一周日期
	 * 
	 * @param date
	 * @return
	 */
	public static String getNextWeekDay(String dateStr, int weekday) {
		Calendar cal = new GregorianCalendar();
		cal.setTime(DateUtil.parseDate(dateStr, "yyyy-MM-dd"));
		int weekNum = cal.get(Calendar.WEEK_OF_YEAR);
		cal.set(Calendar.WEEK_OF_YEAR, weekNum + 1);
		cal.set(Calendar.DAY_OF_WEEK, weekday + 1);
		return format(cal.getTime(), "yyyy-MM-dd");
	}

	/**
	 * 获取指定日期的当前一周日期
	 * 
	 * @param date
	 * @return
	 */
	public static String getCurrWeekDay(String dateStr, int weekday) {
		Calendar cal = new GregorianCalendar();
		cal.setTime(DateUtil.parseDate(dateStr, "yyyy-MM-dd"));
		cal.set(Calendar.DAY_OF_WEEK, weekday + 1);
		return format(cal.getTime(), "yyyy-MM-dd");
	}

	/**
	 * 获取指定日期的下个月日期
	 * 
	 * @param date
	 * @return
	 */
	public static String getCurrMonthDay(String dateStr, int day) {
		Calendar cal = new GregorianCalendar();
		cal.setTime(DateUtil.parseDate(dateStr, "yyyy-MM-dd"));
		int month = cal.get(Calendar.MONTH) + 1;
		int year = cal.get(Calendar.YEAR);
		int endDay = maxDayOfMonth(year, month);
		int valDay = day;
		if (day > endDay) {
			valDay = endDay;
		}
		cal.set(Calendar.DATE, valDay);
		return format(cal.getTime(), "yyyy-MM-dd");
	}

	/**
	 * 获取指定日期的下个月日期
	 * 
	 * @param date
	 * @return
	 */
	public static String getNextMonthDay(String dateStr, int day) {
		Calendar cal = new GregorianCalendar();
		cal.setTime(DateUtil.parseDate(dateStr, "yyyy-MM-dd"));
		int month = cal.get(Calendar.MONTH) + 2;
		cal.set(Calendar.MONTH, month - 1);
		int year = cal.get(Calendar.YEAR);
		int endDay = maxDayOfMonth(year, month);
		int valDay = day;
		if (day > endDay) {
			valDay = endDay;
		}
		cal.set(Calendar.DATE, valDay);
		return format(cal.getTime(), "yyyy-MM-dd");
	}

	/**
	 * 前几个月日期
	 * 
	 * @param dateStr
	 * @param number
	 * @return
	 */
	public static String beforeMonthDate(String dateStr, int number) {
		Calendar cal = new GregorianCalendar();
		cal.setTime(DateUtil.parseDate(dateStr, DATE_FORMATTER));
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH);
		int day = cal.get(Calendar.DATE);
		int endDay = maxDayOfMonth(year, month);
		day = Math.min(endDay, day);
		cal.set(Calendar.MONTH, month - number);
		cal.set(Calendar.DATE, day);

		return format(cal.getTime(), DATE_FORMATTER);
	}

	/**
	 * 得到二个日期间的间隔天数
	 */
	public static String getTwoDay(String sj1, String sj2) {
		SimpleDateFormat myFormatter = new SimpleDateFormat("yyyy-MM-dd");
		long day = 0;
		try {
			Date date = myFormatter.parse(sj1);
			Date mydate = myFormatter.parse(sj2);
			day = (date.getTime() - mydate.getTime()) / (24 * 60 * 60 * 1000);
		} catch (Exception e) {
			return "";
		}
		return day + "";
	}

	/**
	 * 获取指定日期的预约日期 weekday
	 * 
	 * @param date
	 * @return
	 */
	public static String getPlanWeekDay(String startDate, String endDate,
			int day) {
		String dt = getCurrWeekDay(startDate, day);
		if (dt.compareTo(startDate) < 0) {
			dt = getNextWeekDay(startDate, day);
		}
		if (dt.compareTo(endDate) > 0) {
			dt = "";
		}
		return dt;
	}

	/**
	 * 获取指定日期的预约日期day
	 * 
	 * @param date
	 * @return
	 */
	public static String getPlanMonthDay(String startDate, String endDate,
			int day) {
		String dt = getCurrMonthDay(startDate, day);
		if (dt.compareTo(startDate) < 0) {
			dt = getNextMonthDay(startDate, day);
		}
		if (dt.compareTo(endDate) > 0) {
			dt = "";
		}
		return dt;
	}

	/**
	 * 将字符串yyyyMMdd 格式成 字符串yyyy-MM-dd
	 * 
	 * @param date
	 * @return
	 */
	public static String fmtmat(String date) {
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
		try {
			Date newDate = df.parse(date);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			return sdf.format(newDate);
		} catch (Exception ex) {
			return date;
		}
	}

	/**
	 * 将字符串yyyyMMddHHmmss 格式成 字符串yyyy-MM-dd HH:mm:ss
	 * 
	 * @param date
	 * @return
	 */
	public static String fmtmatfulldate(String date) {
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
		try {
			Date newDate = df.parse(date);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			return sdf.format(newDate);
		} catch (Exception ex) {
			return date;
		}
	}
	
	/**
     * 通用格式转换
     * @param dateStr 输入日期字符串
     * @return boolean :true,可正常转换；false，不可正常转换或者异常
     */
    public static boolean dateFormat(String dateStr,String formatter) {

        try {
            Date date = DateUtils.parseDate(dateStr, DateFormatter.DATE_PATTERNS);
            SimpleDateFormat format = new SimpleDateFormat(formatter);
            return StringUtils.isNotBlank(format.format(date));
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }

	public static void main(String[] args) {
		/*
		 * int threadSize = 20; for (int i = 0; i < threadSize; i++) { new
		 * Thread(new Runnable() { int max = 100; int index = 0;
		 * 
		 * @Override public void run() { while (index < max) { index++; String
		 * rst = DateUtil.beforeMonthDate( DateUtil.todayStr(), 3);
		 * System.out.println("thread is " + index + " rst is " + rst); } }
		 * }).start(); }
		 */
	}

}
