package com.han.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {

	public static String date2String(Date date, String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		String dateStr = sdf.format(date);
		return dateStr;
	}

	public static String date2String(Date date) {
		return date2String(date, "yyyy-MM-dd HH:mm:ss");
	}

	public static String date2String() {
		return date2String(new Date());
	}

	public static Date string2Date(String date) {

		return string2Date(date, "yyyy-MM-dd");
	}

	public static Date string2Date(String date, String format) {
		Date d = null;
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		try {
			d = sdf.parse(date);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return d;
	}

	public static Date getMonthBegin(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int month = calendar.get(Calendar.MONTH);
		int year = calendar.get(Calendar.YEAR);
		calendar.set(year, month, 1, 0, 0, 0);
		Date bDate = calendar.getTime();
		return bDate;
	}

	public static Date getMonthBegin() {
		return getMonthBegin(new Date());
	}

	public static Date getMonthEnd() {
		return getMonthEnd(new Date());
	}

	public static Date getMonthEnd(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int month = calendar.get(Calendar.MONTH);
		int year = calendar.get(Calendar.YEAR);
		calendar.set(year, month + 1, 1, 0, 0, 0);
		calendar.add(Calendar.SECOND, -1);
		Date eDate = calendar.getTime();
		return eDate;
	}
	
	
	public static Date changeDateTime2Date(Date date){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTime();
	}

	public static void main(String[] args) {
		System.out.println(string2Date("2016-11-14"));
		System.out.println(getMonthBegin());
		System.out.println(getMonthEnd().getTime());
		System.out.println(changeDateTime2Date(new Date()).getTime());
	}
}
