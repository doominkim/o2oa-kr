/**
 * 공식 플랫폼이 공식 계정에 보내는 메시지 암복호화 예제 코드.
 * 
 * @copyright Copyright (c) 1998-2014 Tencent Inc.
 */

// ------------------------------------------------------------------------

package com.x.organization.assemble.personal.jaxrs.exmail;

import java.security.MessageDigest;
import java.util.Arrays;

/**
 * @author ray
 * SHA1 class
 *
 * 공중 플랫폼 메시지 서명 계산 인터페이스.
 */
class SHA1 {

	/**
	 * SHA1 알고리즘으로 보안 서명 생성
	 * @param token 티켓
	 * @param timestamp 타임스탬프
	 * @param nonce 랜덤 문자열
	 * @param encrypt 암호문
	 * @return 보안 서명
	 * @throws AesException 
	 */
	public static String getSHA1(String token, String timestamp, String nonce, String encrypt) throws AesException
			  {
		try {
			String[] array = new String[] { token, timestamp, nonce, encrypt };
			StringBuffer sb = new StringBuffer();
			// 문자열 정렬
			Arrays.sort(array);
			for (int i = 0; i < 4; i++) {
				sb.append(array[i]);
			}
			String str = sb.toString();
			// SHA1 서명 생성
			MessageDigest md = MessageDigest.getInstance("SHA-1");
			md.update(str.getBytes());
			byte[] digest = md.digest();

			StringBuffer hexstr = new StringBuffer();
			String shaHex = "";
			for (int i = 0; i < digest.length; i++) {
				shaHex = Integer.toHexString(digest[i] & 0xFF);
				if (shaHex.length() < 2) {
					hexstr.append(0);
				}
				hexstr.append(shaHex);
			}
			return hexstr.toString();
		} catch (Exception e) {
			e.printStackTrace();
			throw new AesException(AesException.ComputeSignatureError);
		}
	}
}
