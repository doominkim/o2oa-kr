package com.x.cms.core.express.tools;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DateOperation {
	
	/**
	 * 패턴에 따라 SimpleDateFormat 포맷 객체 반환
	 * @param date
	 * @param style
	 * @return
	 */
	public static String getDate(Date date, String style) {
		SimpleDateFormat format = new SimpleDateFormat(style);
		return format.format(date);
	}

	/**
	 * 특정 날짜의 날짜 부분 반환
	 * 
	 * @param date
	 * @return yyyy-MM-dd
	 */
	public static String getDate(Date date) {
		return format1.format(date);
	}

	/**
	 * 특정 날짜의 시간 부분 반환
	 * 
	 * @param date
	 * @return HH:mm:ss
	 */
	public static  String getTime(Date date) {
		return format2.format(date);
	}

	public static  Date getDateFromString(String dateString, String style) throws Exception{
		Date date = null;
		if(style==null||"".equals(style.trim())){
			date = format1.parse(dateString);
		}else if( "yyyy-MM-dd HH:mm:ss".equalsIgnoreCase(style.trim())){
			date = format3.parse(dateString);
		}else if( "yyyy-MM-dd HH:mm".equalsIgnoreCase(style.trim())){
			date = format3_2.parse(dateString);
		}else if( "yyyy/MM/dd HH:mm:ss".equalsIgnoreCase(style.trim())){
			date = format3_1.parse(dateString);
		}else if( "yyyy/MM/dd".equalsIgnoreCase(style.trim())){
			date = format4.parse(dateString);
		}else if( "yyyy-MM-dd".equalsIgnoreCase(style.trim())){
			date = format1.parse(dateString);
		}else if( "yyyyMMdd".equalsIgnoreCase(style.trim())){
			date = format5.parse(dateString);
		}else if( "yyyyMMddHHmmss".equalsIgnoreCase(style.trim())){
			date = format7.parse(dateString);
		}else if( "HH:mm:ss".equalsIgnoreCase(style.trim())){
			date = format2.parse(dateString);
		}else if( "HH:mm".equalsIgnoreCase(style.trim())){
			date = format2_1.parse(dateString);
		}else{
			throw new Exception( "죄송합니다. 입력하신 날짜 style을 인식할 수 없습니다. 매개변수를 확인하십시오!" );
		}
		return date;
	}
	
	/**
	 * 문자열을 날짜 형식으로 변환
	 * 여러 형식으로 변환을 시도하고, 성공하면 결과 반환
	 * @param dateString
	 * @return
	 * @throws Exception
	 */
	public static  Date getDateFromString( String dateString ) throws Exception{
		Date date = null;
		try{
			date = getDateFromString( dateString, "yyyy-MM-dd HH:mm:ss" );
			return date;
		}catch(Exception e){}

		try{
			date = getDateFromString( dateString, "yyyy-MM-dd HH:mm" );
			return date;
		}catch(Exception e){}
		
		try{
			date = getDateFromString( dateString, "yyyy/MM/dd HH:mm:ss" );
			return date;
		}catch(Exception e){}
		
		try{
			date = getDateFromString( dateString, "yyyy-MM-dd" );
			return date;
		}catch(Exception e){}
		
		try{
			date = getDateFromString( dateString, "yyyy/MM/dd" );
			return date;
		}catch(Exception e){}
		
		try{
			date = getDateFromString( dateString, "yyyyMMdd" );
			return date;
		}catch(Exception e){}
		
		try{
			date = getDateFromString( dateString, "yyyyMMddHHmmss" );
			return date;
		}catch(Exception e){}
		
		try{
			date = getDateFromString( dateString, "HH:mm:ss" );
			return date;
		}catch(Exception e){}
		
		try{
			date = getDateFromString( dateString, "HH:mm" );
			return date;
		}catch(Exception e){}
		
		throw new Exception( "the style of date has error, date:" + dateString );
	}
	
	
	public static  String getDateStringFromDate(Date date, String style) throws Exception{
		String dateString = null;
		if(style==null||"".equals(style.trim())){
			dateString = format1.format(date);
		}else if( "yyyy-MM-dd HH:mm:ss".equalsIgnoreCase(style.trim())){
			dateString = format3.format(date);
		}else if( "yyyy/MM/dd HH:mm:ss".equalsIgnoreCase(style.trim())){
			dateString = format3_1.format(date);
		}else if( "yyyy/MM/dd".equalsIgnoreCase(style.trim())){
			dateString = format4.format(date);
		}else if( "yyyy-MM-dd".equalsIgnoreCase(style.trim())){
			dateString = format1.format(date);
		}else if( "yyyyMMdd".equalsIgnoreCase(style.trim())){
			dateString = format5.format(date);
		}else if( "yyyyMMddHHmmss".equalsIgnoreCase(style.trim())){
			dateString = format7.format(date);
		}else if( "HH:mm:ss".equalsIgnoreCase(style.trim())){
			dateString = format2.format(date);
		}else{
			throw new Exception( "죄송합니다. 입력하신 날짜 style을 인식할 수 없습니다. 매개변수를 확인하십시오! style=" + style);
		}
		return dateString;
	}
	
	/**
	 * 특정 날짜에 일수를 더하거나 뺀 날짜 반환, day가 음수이면 뺌
	 * 
	 * @param date
	 * @param month
	 * @return "yyyy-MM-dd"
	 * @throws Exception 
	 */
	public static  String getDayAdd(String dateString, int day, String style) throws Exception {
		Date date = getDateFromString(dateString, style);
		return getDayAdd(date, day);
	}
	
	/**
	 * 특정 날짜에 일수를 더하거나 뺀 날짜 반환, day가 음수이면 뺌
	 * 
	 * @param date
	 * @param month
	 * @return "yyyy-MM-dd"
	 */
	public static  String getDayAdd(Date date, int day) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DATE, day);
		return format1.format(calendar.getTime());
	}

	/**
	 * 특정 날짜에 개월 수를 더하거나 뺀 날짜 반환, month가 음수이면 뺌
	 * 
	 * @param date
	 * @param month
	 * @return "yyyy-MM-dd"
	 */
	public static  String getMonthAdd(Date date, int month) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.MONTH, month);
		return format1.format(calendar.getTime());
	}
	
	/**
	 * 특정 날짜에 분을 더하거나 뺀 날짜 반환, min이 음수이면 뺌
	 * 
	 * @param date
	 * @param month
	 * @return "yyyy-MM-dd HH:mi:ss"
	 */
	public static  String getMinutesAdd(Date date, int min) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.MINUTE, min);
		return format3.format(calendar.getTime());
	}
	/**
	 * 특정 날짜의 일 반환
	 * 
	 * @param Date
	 *            date
	 * @return
	 */
	public static  String getDay(Date date) {
		try{
			return format10.format(date);
		}catch(Exception e){
			return "0";
		}
	}
	public static int getDayNumber(Date date) {
		String result = null;
		try{
			result = format10.format(date);
		}catch(Exception e){
			result = "0";
		}
		return Integer.parseInt(result);
	}
	/**
	 * 특정 날짜의 월 반환
	 * 
	 * @param Date
	 *            date
	 * @return
	 */
	public static  String getMonth(Date date) {
		try{
			return format9.format(date);
		}catch(Exception e){
			return "0";
		}
	}
	public static int getMonthNumber(Date date) {
		String result = null;
		try{
			result = format9.format(date);
		}catch(Exception e){
			result = "0";
		}
		return Integer.parseInt(result);
	}

	/**
	 * 특정 날짜의 연도 반환
	 * 
	 * @param Date
	 *            date
	 * @return
	 */
	public static  String getYear(Date date) {
		try{
			return format8.format(date);
		}catch(Exception e){
			return "0";
		}
	}
	public static int getYearNumber(Date date) {
		String result = null;
		try{
			result = format8.format(date);
		}catch(Exception e){
			result = "0";
		}
		return Integer.parseInt(result);
	}
	/**
	 * 특정 날짜의 시 반환
	 * 
	 * @param Date
	 *            date
	 * @return
	 */
	public static  String getHour(Date date) {
		try{
			return format11.format(date);
		}catch(Exception e){
			return "0";
		}
	}
	/**
	 * 특정 날짜의 분 반환
	 * 
	 * @param Date
	 *            date
	 * @return
	 */
	public static  String getMinites(Date date) {
		return format12.format(date);
	}
	/**
	 * 특정 날짜의 초 반환
	 * 
	 * @param Date
	 *            date
	 * @return
	 */
	public static  String getSeconds(Date date) {
		return format13.format(date);
	}
	/**
	 * 특정 연도의 일수 반환
	 * 
	 * @param String
	 *            date "yyyy-MM-dd"
	 * @return
	 * @throws ParseException
	 */
	@SuppressWarnings("static-access")
	public static  int getDaysForYear(String date) throws ParseException {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(format1.parse(date));
		return calendar.get(calendar.DAY_OF_YEAR);
	}

	/**
	 * 특정 연도의 일수 반환
	 * 
	 * @param Date
	 *            date
	 * @return
	 * @throws ParseException
	 */
	@SuppressWarnings("static-access")
	public static  int getDaysForYear(Date date) throws ParseException {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar.get(calendar.DAY_OF_YEAR);
	}

	/**
	 * 특정 연도의 일수 반환
	 * 
	 * @param String
	 *            year "yyyy"
	 * @return
	 * @throws ParseException
	 */
	@SuppressWarnings("static-access")
	public static  int getDaysForYear_YYYY(String year) throws ParseException {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(format8.parse(year));
		return calendar.get(calendar.DAY_OF_YEAR);
	}

	/**
	 * 특정 월의 일수 반환
	 * 
	 * @param String
	 *            date "yyyy-MM-dd"
	 * @return
	 * @throws ParseException
	 */
	public static  int getDaysForMonth(String date) throws ParseException {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(format1.parse(date));
		return calendar.getActualMaximum( Calendar.DAY_OF_MONTH);
	}

	/**
	 * 특정 월의 일수 반환
	 * 
	 * @param String
	 *            date "yyyy-MM"
	 * @return
	 * @throws ParseException
	 */
	@SuppressWarnings("static-access")
	public static  int getDaysForMonth_MM(String date) throws ParseException {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(format14.parse(date));
		return calendar.get(calendar.DAY_OF_MONTH);
	}

	/**
	 * 특정 월의 일수 반환
	 * 
	 * @param Date
	 *            date
	 * @return
	 * @throws ParseException
	 */
	public static  int getDaysForMonth(Date date) throws ParseException {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar.get(Calendar.DAY_OF_MONTH);
	}

	/**
	 * 현재 날짜 반환
	 * 
	 * @return
	 */
	public static  String getNowDate() {
		return format1.format(new Date());
	}

	/**
	 * 현재 시간 반환
	 * 
	 * @return
	 */
	public static  String getNowTime() {
		return format2.format(new Date());
	}
	/**
	 * 현재 시간 반환
	 * yyyyMMddHHmmss
	 * @return
	 */
	public static  String getNowTimeChar() {
		return format7.format(new Date());
	}
	/**
	 * 현재 시간 반환
	 * yyyy-MM-dd HH:mm:ss
	 * @return
	 */
	public static  String getNowDateTime() {
		return format3.format(new Date());
	}
	/**
	 * 두 시간 사이의 분 차이 반환
	 * @param date1 yyyy-MM-dd HH:mm:ss
	 * @param date2 yyyy-MM-dd HH:mm:ss
	 * @return
	 * @throws Exception 
	 */
	public static  long getDeff(String date1,String date2) throws Exception {
		long dayNumber = 0;
		// 1시간=60분=3600초=3600000
		long mins = 60L * 1000L;
		// long day= 24L * 60L * 60L * 1000L;일수 차이 계산
		SimpleDateFormat df = null;
		if(date1.length() == 19){
			df = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
		}else if(date1.length() == 10){
			df = new SimpleDateFormat( "yyyy-MM-dd" );
		}else if(date1.length() == 8){
			df = new SimpleDateFormat( "HH:mm:ss" );
		}else{
			df = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
		}
		try {
			Date d1 = df.parse(date1);
			Date d2 = df.parse(date2);
			dayNumber = (d2.getTime() - d1.getTime()) / mins;
		} catch (Exception e) {
			throw e;
		}
		return dayNumber;
	}
	/**
	 * 두 시간 사이의 분 차이 반환
	 * @param date1 yyyy-MM-dd HH:mm:ss
	 * @param date2 yyyy-MM-dd HH:mm:ss
	 * @return
	 */
	public static long getDeff(Date date1,Date date2) {
		long dayNumber = 0;
		// 1시간=60분=3600초=3600000
		long mins = 60L * 1000L;
		// long day= 24L * 60L * 60L * 1000L;일수 차이 계산
		try {
			dayNumber = (date2.getTime() - date1.getTime()) / mins;
		} catch (Exception e) {
			throw e;
		}
		return dayNumber;
	}
	/**
	 * 
	 * 날짜 형식 변환
	 * YYYY-MM-DD에서 YYYYMMDD로 변환
	 * @param date
	 * @throws ParseException 
	 */
	public static  String changeDateFormat(String dateString) throws ParseException{
		Date date;
		String reslut = null;
		try {
			if( "".equals(dateString)){
				dateString="0000-00-00";
			}
			date = format1.parse(dateString);
			reslut = format5.format(date);
		} catch (ParseException e) {
			try {
				date = format1.parse( "0000-00-00" );
			} catch (ParseException e1) {
				throw e1;
			}
			throw e;
		}
		
		return reslut;
	}
	
	/** 형식 yyyy-MM-dd */
	public static  final SimpleDateFormat format1 = new SimpleDateFormat( "yyyy-MM-dd" );
	/** 형식 HH:mm:ss */
	public static  final SimpleDateFormat format2 = new SimpleDateFormat( "HH:mm:ss" );
	public static  final SimpleDateFormat format2_1 = new SimpleDateFormat( "HH:mm" );
	/** 형식 yyyy-MM-dd HH:mm:ss */
	public static  final SimpleDateFormat format3 = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
	public static  final SimpleDateFormat format3_2 = new SimpleDateFormat( "yyyy-MM-dd HH:mm" );
	public static  final SimpleDateFormat format3_1 = new SimpleDateFormat( "yyyy/MM/dd HH:mm:ss" );
	/** 형식 yyyy/MM/dd */
	public static  final SimpleDateFormat format4 = new SimpleDateFormat( "yyyy/MM/dd" );
	/** 형식 yyyyMMdd */
	public static  final SimpleDateFormat format5 = new SimpleDateFormat( "yyyyMMdd" );
	/** 형식 HHmmss */
	public static  final SimpleDateFormat format6 = new SimpleDateFormat( "HHmmss" );
	/** 형식 yyyyMMddHHmmss */
	public static  final SimpleDateFormat format7 = new SimpleDateFormat( "yyyyMMddHHmmss" );
	/** 형식 yyyy */
	public static  final SimpleDateFormat format8 = new SimpleDateFormat( "yyyy" );
	/** 형식 MM */
	public static  final SimpleDateFormat format9 = new SimpleDateFormat( "MM" );
	/** 형식 dd */
	public static  final SimpleDateFormat format10 = new SimpleDateFormat( "dd" );
	/** 형식 HH */
	public static  final SimpleDateFormat format11 = new SimpleDateFormat( "HH" );
	/** 형식 mm */
	public static  final SimpleDateFormat format12 = new SimpleDateFormat( "mm" );
	/** 형식 ss */
	public static  final SimpleDateFormat format13 = new SimpleDateFormat( "ss" );
	/** 형식 ss */
	public static  final SimpleDateFormat format14 = new SimpleDateFormat( "yyyy-MM" );
	
	
	public static  String getStartOfWeek(String dateString) throws Exception{
		SimpleDateFormat format = new SimpleDateFormat( "yyyy-MM-dd" );
		try {
			Date date = format.parse(dateString);
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			cal.setFirstDayOfWeek(Calendar.MONDAY);
			int tmp = cal.get(Calendar.DAY_OF_WEEK) - 1;
			if (0 == tmp) {
				tmp = 7;
			}
			cal.add(Calendar.DATE, -(tmp-1));
			return getDateStringFromDate(cal.getTime(), "yyyy-MM-dd" ) + " 00:00:00";
		} catch (ParseException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
	}
	
	public static  String getEndOfWeek(String dateString) throws Exception{
		try {
			Date date = getDateFromString(getStartOfWeek(dateString), "yyyy-MM-dd HH:mm:ss" );
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			cal.setFirstDayOfWeek(Calendar.MONDAY);
			cal.add(Calendar.DATE, 6);
			return getDateStringFromDate(cal.getTime(), "yyyy-MM-dd" ) + " 23:59:59";
		} catch (ParseException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
	}
	
	
	
	/**
	 * 시간 형식을 **월**일**시**분 형식으로 변환
	 * @param dateString
	 * @param style
	 * @return
	 * @throws Exception
	 */
	public static String getDateCNString( String dateString, String style ) throws Exception{
		StringBuffer ch_date_string = new StringBuffer();
		Date _date = null;
		try{
			_date = getDateFromString( dateString, style );
		}catch(Exception e){
			throw e;
		}
		if(_date == null ){
			_date = new Date();
		}
		String year = getYear( _date );
		String month = getMonth( _date );
		String day = getDay( _date );
		String hour = getHour( _date );
		String min = getMinites( _date );
		ch_date_string.append( year );
		ch_date_string.append( "년 " );
		ch_date_string.append( month );
		ch_date_string.append( "월 " );
		ch_date_string.append( day );
		ch_date_string.append( "일 " );
		ch_date_string.append( hour );
		ch_date_string.append( "시 " );
		ch_date_string.append( min );
		ch_date_string.append( "분" );		
		return ch_date_string.toString();
	}
	
	/**
	 * 시간 형식을 **월**일**시**분**초 형식으로 변환
	 * @param dateString
	 * @param style
	 * @return
	 * @throws Exception
	 */
	public static String getDateCNString2( String dateString, String style ) throws Exception{
		StringBuffer ch_date_string = new StringBuffer();
		Date _date = null;
		try{
			_date = getDateFromString( dateString, style );
		}catch(Exception e){
			throw e;
		}
		if(_date == null ){
			_date = new Date();
		}
		String year = getYear( _date );
		String month = getMonth( _date );
		String day = getDay( _date );
		String hour = getHour( _date );
		String min = getMinites( _date );
		String sec = getSeconds( _date );
		ch_date_string.append( year );
		ch_date_string.append( "년 " );
		ch_date_string.append( month );
		ch_date_string.append( "월 " );
		ch_date_string.append( day );
		ch_date_string.append( "일 " );
		ch_date_string.append( hour );
		ch_date_string.append( "시 " );
		ch_date_string.append( min );
		ch_date_string.append( "분" );
		ch_date_string.append( sec );
		ch_date_string.append( "초" );
		return ch_date_string.toString();
	}
	
	/**
	 * 날짜의 연중 주차 조회
	 * 결과는 1부터 시작
	 * @param dateString yyyy-mm-dd
	 * @return
	 * @throws Exception 
	 */
	public static  int getWeekNumOfYear( String dateString ) throws Exception{
		try {
			Date date = getDateFromString( dateString , "yyyy-MM-dd" );
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			cal.setFirstDayOfWeek(2);//매주 첫째 날을 월요일로 설정
			return cal.get( Calendar.WEEK_OF_YEAR );
		} catch (ParseException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 * 날짜의 연중 주차 조회
	 * 결과는 1부터 시작
	 * @param dateString yyyy-mm-dd
	 * @return
	 */
	public static  int getWeekNumOfYear( Date date ){
		try {
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			cal.setFirstDayOfWeek(2);//매주 첫째 날을 월요일로 설정
			return cal.get( Calendar.WEEK_OF_YEAR );
		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 * 주말 여부 판단
	 * @param recordDate
	 * @return
	 */
	public static boolean isWeekend(Date date) {
		Calendar cal = Calendar.getInstance();
	    cal.setTime( date );
	    if(cal.get(Calendar.DAY_OF_WEEK)==Calendar.SATURDAY||cal.get(Calendar.DAY_OF_WEEK)==Calendar.SUNDAY){
	       return true;
	    }
		return false;
	}
	
	public static long getMinutes( Date date1, Date data2){
		 long l=data2.getTime()-date1.getTime();
		 long min=((l/(60*1000)));
		 return min;
	}

	/**
	 * 주어진 연도·월로 해당 월의 모든 날짜 문자열 조회: yyyy-mm-dd
	 * @param s_year
	 * @param _month
	 * @return
	 */
	public static List<String> getDateStringFormMonth(String s_year, String _month) {
		List<String> result = new ArrayList<String>();
		Calendar cal = Calendar.getInstance();
		int year = 0;
		int month = 0;
		int days = 0;
		try{
			year = Integer.parseInt( s_year );
		}catch(Exception e){
			e.printStackTrace();
		}
		try{
			month = Integer.parseInt( _month );
		}catch(Exception e){
			e.printStackTrace();
		}
		cal.set(year, month-1, 1);
		days = cal.getActualMaximum(Calendar.DATE);
		for( int i =1; i<= days ; i++){
			result.add( s_year + "-" + (month<10?"0"+month:month) + "-" + (i<10?"0"+i:i));
		}
		return result;
	}
	
	/**
	 * 주어진 두 시간 사이의 모든 날짜 문자열 조회: yyyy-mm-dd
	 * @param startDate
	 * @param endDate
	 * @return
	 * @throws Exception 
	 */
	public static List<String> listDateStringBetweenDate( Date startDate, Date endDate ) throws Exception {		
		List<String> result = new ArrayList<String>();
		Calendar cal = Calendar.getInstance();
		startDate = getDateFromString( getDateStringFromDate( startDate, "yyyy-MM-dd" ) );
		endDate = getDateFromString( getDateStringFromDate( endDate, "yyyy-MM-dd" ) + " 23:59:59" );	
		cal.setTime( startDate );
		while( cal.getTime().before(endDate)){
			//logger.debug( getDateStringFromDate( cal.getTime(), "yyyy-MM-dd" ) );
			result.add( getDateStringFromDate( cal.getTime(), "yyyy-MM-dd" ));
			cal.add(Calendar.DATE, 1);
		}
		return result;
	}
	
	/**
	 * 날짜가 속한 월의 첫째 날 조회
	 * @param recordDate
	 * @return
	 * @throws Exception 
	 */
	public static Date getFirstDateInMonth(Date recordDate) throws Exception {
		String year = format8.format(recordDate);
		String month = format9.format(recordDate);
		return getDateFromString( year + "-" + month + "-01" );
	}
	
	/**
	 * 날짜가 속한 월의 첫째 날 조회
	 * @param recordDate yyyy-MM-dd
	 * @return
	 * @throws Exception 
	 */
	public static String getFirstDateStringInMonth(Date recordDate) throws Exception {
		String year = format8.format(recordDate);
		String month = format9.format(recordDate);
		return year + "-" + month + "-01";
	}
	
	/**
	 * 해당 월의 마지막 날 조회
	 * @param date
	 * @return
	 */
	public static Date getLastDayOfMonth(Date date) {  
        Calendar calendar = convert(date);  
        calendar.set(Calendar.DATE, calendar.getMaximum(Calendar.DATE));  
        return calendar.getTime();  
    }  
	/** 
     * 날짜를 Calendar로 변환 
     * @param date 날짜 
     * @return Calendar 
     */  
    private static Calendar convert(Date date) {  
        Calendar calendar = Calendar.getInstance();  
        calendar.setTime(date);  
        return calendar;  
    }  
	/**
	 * 날짜가 속한 월의 첫째 날 조회
	 * @param recordDate
	 * @return
	 * @throws Exception 
	 */
	public static Date getLastDateInMonth(Date recordDate) throws Exception {
		Date lastDate = getLastDayOfMonth(recordDate);
		return getDateFromString( getDateStringFromDate( lastDate, "yyyy-MM-dd" ) );
	}
	
	/**
	 * 날짜가 속한 월의 첫째 날 조회
	 * @param recordDate yyyy-MM-dd
	 * @return
	 * @throws Exception 
	 */
	public static String getLastDateStringInMonth(Date recordDate) throws Exception {
		Date lastDate = getLastDayOfMonth(recordDate);
		return getDateStringFromDate( lastDate, "yyyy-MM-dd" );
	}

	/**
	 * 오늘 시작 시간 조회
	 * @return
	 */
	public static Date getTodayStartTime() {
		String todayDateTime = getNowDate() + " 00:00:00";
		try {
			return getDateFromString( todayDateTime );
		} catch (Exception e) {
			return new Date();
		}
	}
}
