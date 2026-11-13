package com.x.base.core.project.tools;

import org.apache.commons.lang3.StringUtils;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Random;

/**
 * @author sword
 */
public class URLTools {

	private static final String[] chars = new String[] { "a" , "b" , "c" , "d" , "e" , "f" , "g" , "h" ,
			"i" , "j" , "k" , "l" , "m" , "n" , "o" , "p" , "q" , "r" , "s" , "t" ,
			"u" , "v" , "w" , "x" , "y" , "z" , "0" , "1" , "2" , "3" , "4" , "5" ,
			"6" , "7" , "8" , "9" , "A" , "B" , "C" , "D" , "E" , "F" , "G" , "H" ,
			"I" , "J" , "K" , "L" , "M" , "N" , "O" , "P" , "Q" , "R" , "S" , "T" ,
			"U" , "V" , "W" , "X" , "Y" , "Z"
	};

	private static final Random random = new SecureRandom();

	public static String getQueryStringParameter(String queryString, String name) {
		String value = StringUtils.substringAfter(queryString, name + "=");
		if (StringUtils.contains(value, "&")) {
			return StringUtils.substringBefore(value, "&");
		} else if (StringUtils.contains(value, "!")) {
			return StringUtils.substringBefore(value, "!");
		} else {
			return value;
		}
	}

	/**
	 * Java8 URLEncoder.encode throw Exception,Java11 URLEncoder.encode 는 오류를 던지지 않음
	 * 두 메서드를 통일.
	 * @param str
	 * @return
	 */
	public static String encode(String str) {
		try {
			return URLEncoder.encode(str, StandardCharsets.UTF_8.name());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 지정한 길이의 단축 링크 생성
	 * @param url
	 * @param length 단축 링크 길이, 최소 4자리
	 * @return
	 */
	public static String shortUrl(String url, int length) {
		if(StringUtils.isBlank(url)){
			return url;
		}
		int defaultLength = 4;
		if(length < defaultLength){
			length = defaultLength;
		}
		String hex = MD5Tool.getMD5Str(url);
		// 암호화 문자를 8자리씩 16진수로 0x3FFFFFFF와 비트 AND 연산
		int i = random.nextInt(25);
		String sTempSubString = hex.substring(i, i + 8);
		long lHexLong = 0x3FFFFFFF & Long.parseLong(sTempSubString, 16);
		String outChars = "" ;
		for ( int j = 0; j < length; j++) {
			// 얻은 값을 0x0000003D와 비트 AND 연산하여 문자 배열 chars 인덱스 얻기
			long index = 0x0000003D & lHexLong;
			outChars += chars[( int ) index];
			// 반복마다 오른쪽으로 5비트 시프트
			lHexLong = lHexLong >> 5;
		}
		return outChars;
	}

	/**
	 * 6자리 단축 링크 생성
	 * @param url
	 * @return
	 */
	public static String shortUrl(String url) {
		return shortUrl(url, 6);
	}

	public static void main(String[] args) {
		System.out.println(shortUrl("http://dev.o2oa.net/x_desktop/index.html"));
	}

}
