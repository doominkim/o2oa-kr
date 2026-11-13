package com.x.file.assemble.control;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.HashMap;

/**
 * 파일 업로드·다운로드
 * @author sword
 */
public class FileUtil {

	private static final Integer BYTE_LENGTH = 1024;
	/**
	 * 파일 헤더 정보 캐시 - 파일 헤더 정보
	 */
	public static final HashMap<String, String> mFileTypes = new HashMap<>();
	static {
		// images
		mFileTypes.put("FFD8FF", "jpg");
		mFileTypes.put("89504E47", "png");
		mFileTypes.put("47494638", "gif");
		mFileTypes.put("49492A00", "tif");
		mFileTypes.put("424D", "bmp");
		mFileTypes.put("41433130", "dwg");
		mFileTypes.put("38425053", "psd");
		mFileTypes.put("7B5C727466", "rtf");
		mFileTypes.put("3C3F786D6C", "xml");
		mFileTypes.put("68746D6C3E", "html");
		mFileTypes.put("44656C69766572792D646174653A", "eml");
		mFileTypes.put("D0CF11E0", "doc");
		mFileTypes.put("D0CF11E0", "xls");
		mFileTypes.put("5374616E64617264204A", "mdb");
		mFileTypes.put("252150532D41646F6265", "ps");
		mFileTypes.put("255044462D312E", "pdf");
		mFileTypes.put("504B0304", "docx");
		mFileTypes.put("504B0304", "xlsx");
		mFileTypes.put("52617221", "rar");
		mFileTypes.put("57415645", "wav");
		mFileTypes.put("41564920", "avi");
		mFileTypes.put("2E524D46", "rm");
		mFileTypes.put("000001BA", "mpg");
		mFileTypes.put("000001B3", "mpg");
		mFileTypes.put("6D6F6F76", "mov");
		mFileTypes.put("3026B2758E66CF11", "asf");
		mFileTypes.put("4D546864", "mid");
		mFileTypes.put("1F8B08", "gz");
	}

	/**
	 * <p>
	 * Title:getFileType
	 * </p>
	 * <p>
	 * Description: 파일 경로로 파일 헤더 정보 조회
	 * </p>
	 *
	 * @param filePath 파일 경로
	 * @return 파일 헤더 정보
	 */
	public static String getFileType(String filePath) {
		return mFileTypes.get(getFileHeader(filePath));
	}

	/**
	 * <p>
	 * Title:getFileTypeByFileInputStream
	 * </p>
	 * <p>
	 * Description: 파일 스트림으로 파일 헤더 정보 조회
	 * </p>
	 *
	 * @param is 파일 스트림
	 * @return 파일 헤더 정보
	 */
	public static String getFileTypeByFileInputStream(FileInputStream is) {
		return mFileTypes.get(getFileHeaderByFileInputStream(is));
	}

	/**
	 * <p>
	 * Title:getFileHeader
	 * </p>
	 * <p>
	 * Description: 파일 경로로 파일 헤더 정보 조회
	 * </p>
	 *
	 * @param filePath 파일 경로
	 * @return 16진수 파일 헤더 정보
	 */
	public static String getFileHeader(String filePath) {
		FileInputStream is = null;
		String value = null;
		try {
			is = new FileInputStream(filePath);
			byte[] b = new byte[7];
			/*
			 * int read() 이 입력 스트림에서 데이터 바이트 하나를 읽음. int read(byte[] b) 이 입력 스트림에서 최대 b.length 바이트의 데이터를
			 * byte 배열로 읽음. int read(byte[] b, int off, int len) 이 입력 스트림에서 최대 len 바이트의 데이터를 byte
			 * 배열로 읽음.
			 */
			is.read(b, 0, b.length);
			value = bytesToHexString(b);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != is) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return value;
	}

	/**
	 * <p>
	 * Title:getFileHeaderByFileInputStream
	 * </p>
	 * <p>
	 * Description: 파일 스트림으로 파일 헤더 정보 조회
	 * </p>
	 *
	 * @param is 파일 스트림
	 * @return 16진수 파일 헤더 정보
	 */
	public static String getFileHeaderByFileInputStream(FileInputStream is) {
		String value = null;
		try {
			byte[] b = new byte[7];
			/*
			 * int read() 이 입력 스트림에서 데이터 바이트 하나를 읽음. int read(byte[] b) 이 입력 스트림에서 최대 b.length 바이트의 데이터를
			 * byte 배열로 읽음. int read(byte[] b, int off, int len) 이 입력 스트림에서 최대 len 바이트의 데이터를 byte
			 * 배열로 읽음.
			 */
			is.read(b, 0, b.length);
			value = bytesToHexString(b);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != is) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return value;
	}

	/**
	 * <p>
	 * Title:bytesToHexString
	 * </p>
	 * <p>
	 * Description: 파일 헤더 정보를 읽을 파일의 byte 배열을 string 형태로 변환
	 * </p>
	 *
	 * @param src 파일 헤더 정보를 읽을 파일의 byte 배열
	 * @return 파일 헤더 정보
	 */
	private static String bytesToHexString(byte[] src) {
		StringBuilder builder = new StringBuilder();
		if (src == null || src.length <= 0) {
			return null;
		}
		String hv;
		for (int i = 0; i < src.length; i++) {
			// 정수 인수를 16진수(기수 16) 부호 없는 정수 형태의 문자열로 반환하고 대문자로 변환
			hv = Integer.toHexString(src[i] & 0xFF).toUpperCase();
			if (hv.length() < 2) {
				builder.append(0);
			}
			builder.append(hv);
		}
		return builder.toString();
	}

	/**
	 * 지정한 파일 크기 조회
	 */
	public static long getFileSize(File file) throws Exception {
		long size = 0;
		if (file.exists()) {
			try (FileInputStream fis = new FileInputStream(file)) {
				size = fis.available();
			}
		} else {
			file.createNewFile();
		}
		return size;
	}

	/**
	 * 압축 후 파일 이름
	 */
	private static String zipName(String name) {
		String prefix = "";
		if (name.indexOf(".") != -1) {
			prefix = name.substring(0, name.lastIndexOf("."));
		} else {
			prefix = name;
		}
		return prefix + ".zip";
	}

	/**
	 * 파일 MD5 조회
	 */
	public static String getFileMD5(byte[] bytes) {
		if (bytes == null) {
			return null;
		}
		MessageDigest digest = null;
		InputStream in = null;
		byte[] buffer = new byte[BYTE_LENGTH];
		int len;
		try {
			digest = MessageDigest.getInstance("MD5");
			in = new ByteArrayInputStream(bytes);
			while ((len = in.read(buffer, 0, BYTE_LENGTH)) != -1) {
				digest.update(buffer, 0, len);
			}
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		BigInteger bigInt = new BigInteger(1, digest.digest());
		return bigInt.toString(16);
	}

	/**
	 * 파일 MD5 조회
	 */
	public static String getFileMD5(InputStream in) {
		if (in == null) {
			return null;
		}
		MessageDigest digest = null;
		byte[] buffer = new byte[BYTE_LENGTH];
		int len;
		try {
			digest = MessageDigest.getInstance("MD5");
			while ((len = in.read(buffer, 0, BYTE_LENGTH)) != -1) {
				digest.update(buffer, 0, len);
			}
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		BigInteger bigInt = new BigInteger(1, digest.digest());
		return bigInt.toString(16);
	}
}
