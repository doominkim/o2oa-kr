/**
 * 기업 위챗이 기업 백엔드로 보내는 메시지 암복호화 예제 코드.
 * 
 * @copyright Copyright (c) 1998-2014 Tencent Inc.
 */

// ------------------------------------------------------------------------

/**
 * org.apache.commons.codec.binary.Base64 대상,
 * commons-codec-1.9(또는 commons-codec-1.8 등 다른 버전) 라이브러리 가져오기 필요
 * 공식 다운로드 주소: http://commons.apache.org/proper/commons-codec/download_codec.cgi
 */
package com.x.program.center.jaxrs.qiyeweixin;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;

/**
 * 기업 위챗 메시지 수신·전송용 암복호화 인터페이스 제공(UTF8 인코딩 문자열).
 * <ol>
 * <li>서드파티가 기업 위챗에 암호화 메시지로 응답</li>
 * <li>서드파티가 기업 위챗이 보낸 메시지를 받아 보안성을 검증하고 복호화.</li>
 * </ol>
 * 설명: java.security.InvalidKeyException:illegal Key Size 예외 해결 방법
 * <ol>
 * <li>공식 사이트에서 JCE 무제한 권한 정책 파일 다운로드(JDK7 다운로드 주소:
 * http://www.oracle.com/technetwork/java/javase/downloads/jce-7-download-432124.html</li>
 * <li>다운로드 후 압축을 풀면 local_policy.jar, US_export_policy.jar, readme.txt 확인 가능</li>
 * <li>JRE가 설치된 경우 두 jar 파일을 %JRE_HOME%\lib\security 디렉터리에 넣어 기존 파일 덮어쓰기</li>
 * <li>JDK가 설치된 경우 두 jar 파일을 %JDK_HOME%\jre\lib\security 디렉터리에 넣어 기존 파일 덮어쓰기</li>
 * </ol>
 */
public class WXBizMsgCrypt {

	private static final Random random = new SecureRandom();

	static final Charset CHARSET = StandardCharsets.UTF_8;
	Base64 base64 = new Base64();
	byte[] aesKey;
	String token;
	String receiveid;

	/**
	 * 생성자
	 * 
	 * @param token          기업 위챗 백엔드에서 개발자가 설정한 token
	 * @param encodingAesKey 기업 위챗 백엔드에서 개발자가 설정한 EncodingAESKey
	 * @param receiveid,     상황마다 의미가 다름, 문서 참고
	 * 
	 * @throws AesException 실행 실패, 해당 예외의 오류 코드와 상세 오류 정보 확인
	 */
	public WXBizMsgCrypt(String token, String encodingAesKey, String receiveid) throws AesException {
		if (encodingAesKey.length() != 43) {
			throw new AesException(AesException.IllegalAesKey);
		}

		this.token = token;
		this.receiveid = receiveid;
		aesKey = Base64.decodeBase64(encodingAesKey + "=");
	}

	// 4바이트 네트워크 바이트 순서 생성
	byte[] getNetworkBytesOrder(int sourceNumber) {
		byte[] orderBytes = new byte[4];
		orderBytes[3] = (byte) (sourceNumber & 0xFF);
		orderBytes[2] = (byte) (sourceNumber >> 8 & 0xFF);
		orderBytes[1] = (byte) (sourceNumber >> 16 & 0xFF);
		orderBytes[0] = (byte) (sourceNumber >> 24 & 0xFF);
		return orderBytes;
	}

	// 4바이트 네트워크 바이트 순서 복원
	int recoverNetworkBytesOrder(byte[] orderBytes) {
		int sourceNumber = 0;
		for (int i = 0; i < 4; i++) {
			sourceNumber <<= 8;
			sourceNumber |= orderBytes[i] & 0xff;
		}
		return sourceNumber;
	}

	// 16자리 문자열 랜덤 생성
	String getRandomStr() {
		String base = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 16; i++) {
			sb.append(base.charAt(random.nextInt(base.length())));
		}
		return sb.toString();
	}

	/**
	 * 평문 암호화.
	 * 
	 * @param text 암호화할 평문
	 * @return 암호화 후 base64로 인코딩된 문자열
	 * @throws AesException aes 암호화 실패
	 */
	String encrypt(String randomStr, String text) throws AesException {
		ByteGroup byteCollector = new ByteGroup();
		byte[] randomStrBytes = randomStr.getBytes(CHARSET);
		byte[] textBytes = text.getBytes(CHARSET);
		byte[] networkBytesOrder = getNetworkBytesOrder(textBytes.length);
		byte[] receiveidBytes = receiveid.getBytes(CHARSET);

		// randomStr + networkBytesOrder + text + receiveid
		byteCollector.addBytes(randomStrBytes);
		byteCollector.addBytes(networkBytesOrder);
		byteCollector.addBytes(textBytes);
		byteCollector.addBytes(receiveidBytes);

		// ... + pad: 사용자 정의 패딩 방식으로 평문 패딩
		byte[] padBytes = PKCS7Encoder.encode(byteCollector.size());
		byteCollector.addBytes(padBytes);

		// 최종 바이트 스트림 획득, 미암호화
		byte[] unencrypted = byteCollector.toBytes();

		try {
			// 암호화 모드를 AES의 CBC 모드로 설정
			Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
			SecretKeySpec keySpec = new SecretKeySpec(aesKey, "AES");
			IvParameterSpec iv = new IvParameterSpec(aesKey, 0, 16);
			cipher.init(Cipher.ENCRYPT_MODE, keySpec, iv);

			// 암호화
			byte[] encrypted = cipher.doFinal(unencrypted);

			// 암호화된 문자열을 BASE64로 인코딩
			String base64Encrypted = base64.encodeToString(encrypted);

			return base64Encrypted;
		} catch (Exception e) {
			e.printStackTrace();
			throw new AesException(AesException.EncryptAESError);
		}
	}

	/**
	 * 암호문 복호화.
	 * 
	 * @param text 복호화할 암호문
	 * @return 복호화된 평문
	 * @throws AesException aes 복호화 실패
	 */
	String decrypt(String text) throws AesException {
		byte[] original;
		try {
			// 복호화 모드를 AES의 CBC 모드로 설정
			Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
			SecretKeySpec key_spec = new SecretKeySpec(aesKey, "AES");
			IvParameterSpec iv = new IvParameterSpec(Arrays.copyOfRange(aesKey, 0, 16));
			cipher.init(Cipher.DECRYPT_MODE, key_spec, iv);

			// BASE64로 암호문 디코딩
			byte[] encrypted = Base64.decodeBase64(text);

			// 복호화
			original = cipher.doFinal(encrypted);
		} catch (Exception e) {
			e.printStackTrace();
			throw new AesException(AesException.DecryptAESError);
		}

		String xmlContent, from_receiveid;
		try {
			// 패딩 문자 제거
			byte[] bytes = PKCS7Encoder.decode(original);

			// 16자리 랜덤 문자열, 네트워크 바이트 순서와 receiveid 분리
			byte[] networkOrder = Arrays.copyOfRange(bytes, 16, 20);

			int xmlLength = recoverNetworkBytesOrder(networkOrder);

			xmlContent = new String(Arrays.copyOfRange(bytes, 20, 20 + xmlLength), CHARSET);
			from_receiveid = new String(Arrays.copyOfRange(bytes, 20 + xmlLength, bytes.length), CHARSET);
		} catch (Exception e) {
			e.printStackTrace();
			throw new AesException(AesException.IllegalBuffer);
		}

		// receiveid가 다른 경우
		if (!from_receiveid.equals(receiveid)) {
			throw new AesException(AesException.ValidateCorpidError);
		}
		return xmlContent;

	}

	/**
	 * 기업 위챗이 사용자에게 응답할 메시지를 암호화해 패키징.
	 * <ol>
	 * <li>전송할 메시지를 AES-CBC로 암호화</li>
	 * <li>보안 서명 생성</li>
	 * <li>메시지 암호문과 보안 서명을 xml 형식으로 패키징</li>
	 * </ol>
	 * 
	 * @param replyMsg  기업 위챗이 사용자에게 응답할 메시지, xml 형식 문자열
	 * @param timeStamp 타임스탬프, 직접 생성하거나 URL 파라미터의 timestamp 사용 가능
	 * @param nonce     랜덤 문자열, 직접 생성하거나 URL 파라미터의 nonce 사용 가능
	 * 
	 * @return 사용자에게 바로 회신 가능한 암호화된 암호문, msg_signature, timestamp, nonce, encrypt를 포함한 xml 형식 문자열
	 * @throws AesException 실행 실패, 해당 예외의 오류 코드와 상세 오류 정보 확인
	 */
	public String EncryptMsg(String replyMsg, String timeStamp, String nonce) throws AesException {
		// 암호화
		String encrypt = encrypt(getRandomStr(), replyMsg);

		// 보안 서명 생성
		if (StringUtils.isEmpty(timeStamp)) {
			timeStamp = Long.toString(System.currentTimeMillis());
		}

		String signature = SHA1.getSHA1(token, timeStamp, nonce, encrypt);

		// System.out.println("플랫폼으로 전송하는 서명: " + signature[1].toString());
		// 전송할 xml 생성
		String result = XMLParse.generate(encrypt, signature, timeStamp, nonce);
		return result;
	}

	/**
	 * 메시지 진위 검증 및 복호화된 평문 획득.
	 * <ol>
	 * <li>수신한 암호문으로 보안 서명을 생성해 서명 검증</li>
	 * <li>검증 통과 시 xml의 암호화 메시지 추출</li>
	 * <li>메시지 복호화</li>
	 * </ol>
	 * 
	 * @param msgSignature 서명 문자열, URL 파라미터 msg_signature에 대응
	 * @param timeStamp    타임스탬프, URL 파라미터 timestamp에 대응
	 * @param nonce        랜덤 문자열, URL 파라미터 nonce에 대응
	 * @param postData     암호문, POST 요청 데이터에 대응
	 * 
	 * @return 복호화된 원문
	 * @throws AesException 실행 실패, 해당 예외의 오류 코드와 상세 오류 정보 확인
	 */
	public String DecryptMsg(String msgSignature, String timeStamp, String nonce, String postData) throws AesException {

		// 키, 공식 계정의 app secret
		// 암호문 추출
		Object[] encrypt = XMLParse.extract(postData);

		// 보안 서명 검증
		String signature = SHA1.getSHA1(token, timeStamp, nonce, encrypt[1].toString());

		// URL의 서명과 일치 여부 비교
		// System.out.println("서드파티가 받은 URL의 서명: " + msg_sign);
		// System.out.println("서드파티 서명 검증: " + signature);
		if (!signature.equals(msgSignature)) {
			throw new AesException(AesException.ValidateSignatureError);
		}

		// 복호화
		String result = decrypt(encrypt[1].toString());
		return result;
	}

	/**
	 * URL 검증
	 * 
	 * @param msgSignature 서명 문자열, URL 파라미터 msg_signature에 대응
	 * @param timeStamp    타임스탬프, URL 파라미터 timestamp에 대응
	 * @param nonce        랜덤 문자열, URL 파라미터 nonce에 대응
	 * @param echoStr      랜덤 문자열, URL 파라미터 echostr에 대응
	 * 
	 * @return 복호화된 echostr
	 * @throws AesException 실행 실패, 해당 예외의 오류 코드와 상세 오류 정보 확인
	 */
	public String VerifyURL(String msgSignature, String timeStamp, String nonce, String echoStr) throws AesException {
		String signature = SHA1.getSHA1(token, timeStamp, nonce, echoStr);

		if (!signature.equals(msgSignature)) {
			throw new AesException(AesException.ValidateSignatureError);
		}

		String result = decrypt(echoStr);
		return result;
	}

}