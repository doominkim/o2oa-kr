package com.x.base.core.project.tools;

import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

public class Host {
	public static final Logger logger = LoggerFactory.getLogger(Host.class);
	public static String ROLLBACK_IPV4 = "127.0.0.1";
	public static String ROLLBACK_LOCALHOST = "localhost";
	public static String ROLLBACK_IPV6 = "[::1]";
	// public static Integer APPLICATION_PORT = 20020;
	// public static Integer CENTER_PORT = 20030;
	// public static Integer WEB_PORT = 80;
	// public static Integer DATA_PORT = 20040;
	// public static Integer STORAGE_PORT = 20050;

	public static String httpHost(String host, Integer port, String defaultHost, Integer defaultPort) {
		String str = "http://";
		str += StringUtils.isNotEmpty(host) ? host : defaultHost;
		Integer p = (null != port) ? port : defaultPort;
		str += (p == 80) ? "" : (":" + p);
		return str;
	}

	public static Boolean isRollback(String host) {
		if (StringUtils.equalsIgnoreCase(host, ROLLBACK_IPV4)) {
			return true;
		}
		if (StringUtils.equalsIgnoreCase(host, ROLLBACK_LOCALHOST)) {
			return true;
		}
		return false;
	}

	public static boolean innerIp(String ip) {
		// // 10.0.0.0 - 10.255.255.255 대역 일치
		// String pattern_10 =
		// "^(\\D)*10(\\.([2][0-4]\\d|[2][5][0-5]|[01]?\\d?\\d)){3}";
		//
		// // 172.16.0.0 - 172.31.255.255 대역 일치
		// String pattern_172 =
		// "172\\.([1][6-9]|[2]\\d|3[01])(\\.([2][0-4]\\d|[2][5][0-5]|[01]?\\d?\\d)){2}";
		//
		// // 192.168.0.0 - 192.168.255.255 대역 일치
		// String pattern_192 =
		// "192\\.168(\\.([2][0-4]\\d|[2][5][0-5]|[01]?\\d?\\d)){2}";
		String pattern = "((192\\.168|172\\.([1][6-9]|[2]\\d|3[01]))"
				+ "(\\.([2][0-4]\\d|[2][5][0-5]|[01]?\\d?\\d)){2}|"
				+ "^(\\D)*10(\\.([2][0-4]\\d|[2][5][0-5]|[01]?\\d?\\d)){3})";
		Pattern reg = Pattern.compile(pattern);
		Matcher match = reg.matcher(ip);
		return match.find();
	}

	public static boolean ip(String addr) {
		if (addr.length() < 7 || addr.length() > 15 || "".equals(addr)) {
			return false;
		}
		/**
		 * IP 형식과 범위 판단
		 */
		String rexp = "([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}";

		Pattern pat = Pattern.compile(rexp);

		Matcher mat = pat.matcher(addr);

		return mat.find();
	}

	/**
	 * 머신 코드 생성(MAC 주소와 하드웨어 정보 기반)
	 */
	public static String generateMachineCode() {
		try {
			StringBuilder sb = new StringBuilder();

			// 1. MAC 주소 조회
			String macAddress = getMacAddress();
			sb.append(macAddress);

			// 2. 기타 시스템 정보 조회
			String osArch = System.getProperty("os.arch");
			String userName = System.getProperty("user.name");

			sb.append("#").append(osArch).append("#").append(userName);

			// 3. MD5 해시를 머신 코드로 생성
			logger.debug("머신 코드 정보: {}", sb.toString());
			return MD5Tool.getMD5Str(sb.toString());

		} catch (Exception e) {
			logger.warn("머신 코드 생성 실패: {}", e.getMessage());
		}
		return "";
	}

	/**
	 * 기본 MAC 주소 조회
	 */
	private static String getMacAddress() {
		try {
			Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
			while (networkInterfaces.hasMoreElements()) {
				NetworkInterface networkInterface = networkInterfaces.nextElement();
				if (networkInterface.isLoopback() || !networkInterface.isUp()) {
					continue;
				}

				byte[] mac = networkInterface.getHardwareAddress();
				if (mac != null) {
					StringBuilder sb = new StringBuilder();
                    for (byte b : mac) {
                        sb.append(String.format("%02X", b));
                    }
					return sb.toString();
				}
			}
		} catch (Exception e) {
			logger.warn("getMacAddress error: {}", e.getMessage());
		}
		return "00:00:00:00:00:00";
	}

}
