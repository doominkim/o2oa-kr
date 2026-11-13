package com.x.program.center.jaxrs.qiyeweixin;

@SuppressWarnings("serial")
public class AesException extends Exception {

	public final static int OK = 0;
	public final static int ValidateSignatureError = -40001;
	public final static int ParseXmlError = -40002;
	public final static int ComputeSignatureError = -40003;
	public final static int IllegalAesKey = -40004;
	public final static int ValidateCorpidError = -40005;
	public final static int EncryptAESError = -40006;
	public final static int DecryptAESError = -40007;
	public final static int IllegalBuffer = -40008;
	//public final static int EncodeBase64Error = -40009;
	//public final static int DecodeBase64Error = -40010;
	//public final static int GenReturnXmlError = -40011;

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
//		case EncodeBase64Error:
//			return "base64 암호화 오류";
//		case DecodeBase64Error:
//			return "base64 복호화 오류";
//		case GenReturnXmlError:
//			return "xml 생성 실패";
		default:
			return null; // cannot be
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
