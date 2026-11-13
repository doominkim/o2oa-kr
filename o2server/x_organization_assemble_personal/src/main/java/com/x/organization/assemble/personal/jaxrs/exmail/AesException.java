package com.x.organization.assemble.personal.jaxrs.exmail;

/**
 * 
 * @author ray
 *
 */
public class AesException extends Exception {
	public static final int OK = 0;
	public static final int ValidateSignatureError = -40001;
	public static final int ParseXmlError = -40002;
	public static final int ComputeSignatureError = -40003;
	public static final int IllegalAesKey = -40004;
	public static final int ValidateCorpidError = -40005;
	public static final int EncryptAESError = -40006;
	public static final int DecryptAESError = -40007;
	public static final int IllegalBuffer = -40008;
	private int code;

	private static String getMessage(int code) {
		switch (code) {
		case ValidateSignatureError:
			return "서명 검증 오류";
		case ParseXmlError:
			return "xml 파싱 실패";
		case ComputeSignatureError:
			return "sha 암호화로 서명 생성 실패";
		case IllegalAesKey:
			return "SymmetricKey가 올바르지 않습니다";
		case ValidateCorpidError:
			return "corpid 검증 실패";
		case EncryptAESError:
			return "aes 암호화 실패";
		case DecryptAESError:
			return "aes 복호화 실패";
		case IllegalBuffer:
			return "복호화한 buffer가 올바르지 않습니다";
		default:
			return null;
		}
	}

	public int getCode() {
		return code;
	}

	AesException(int code) {
		super(getMessage(code));
		this.code = code;
	}
}
