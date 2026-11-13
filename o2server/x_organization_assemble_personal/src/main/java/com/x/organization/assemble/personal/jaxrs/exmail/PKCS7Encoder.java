/**
 * 공식 플랫폼이 공식 계정에 보내는 메시지 암복호화 예제 코드.
 * 
 * @copyright Copyright (c) 1998-2014 Tencent Inc.
 */

// ------------------------------------------------------------------------

package com.x.organization.assemble.personal.jaxrs.exmail;

import java.nio.charset.Charset;
import java.util.Arrays;

/**
 * @author ray PKCS7 알고리즘 기반 암복호화 API 제공.
 */
class PKCS7Encoder {
	static Charset CHARSET = Charset.forName("utf-8");
	static int BLOCK_SIZE = 32;

	/**
	 * 평문 패딩용 바이트 조회.
	 * 
	 * @param count 패딩이 필요한 평문 바이트 수
	 * @return 패딩용 바이트 배열
	 */
	static byte[] encode(int count) {
		// 패딩할 자릿수 계산
		int amountToPad = BLOCK_SIZE - (count % BLOCK_SIZE);
		if (amountToPad == 0) {
			amountToPad = BLOCK_SIZE;
		}
		// 패딩용 문자 조회
		char padChr = chr(amountToPad);
		String tmp = new String();
		for (int index = 0; index < amountToPad; index++) {
			tmp += padChr;
		}
		return tmp.getBytes(CHARSET);
	}

	/**
	 * 복호화된 평문의 패딩 문자 제거
	 * 
	 * @param decrypted 복호화된 평문
	 * @return 패딩 문자를 제거한 평문
	 */
	static byte[] decode(byte[] decrypted) {
		int pad = (int) decrypted[decrypted.length - 1];
		if (pad < 1 || pad > 32) {
			pad = 0;
		}
		return Arrays.copyOfRange(decrypted, 0, decrypted.length - pad);
	}

	/**
	 * 숫자를 ASCII 코드 문자로 변환, 평문 패딩에 사용
	 * 
	 * @param a 변환할 숫자
	 * @return 변환된 문자
	 */
	static char chr(int a) {
		byte target = (byte) (a & 0xFF);
		return (char) target;
	}

}
