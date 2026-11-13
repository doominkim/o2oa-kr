package com.x.program.center.jaxrs.dingding.encrypt;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by fancyLou on 2020-10-26.
 * Copyright © 2020 O2. All rights reserved.
 */
public class DingTalkEncryptException extends Exception {
    public static final int SUCCESS = 0;
    public static final int ENCRYPTION_PLAINTEXT_ILLEGAL = 900001;
    public static final int ENCRYPTION_TIMESTAMP_ILLEGAL = 900002;
    public static final int ENCRYPTION_NONCE_ILLEGAL = 900003;
    public static final int AES_KEY_ILLEGAL = 900004;
    public static final int SIGNATURE_NOT_MATCH = 900005;
    public static final int COMPUTE_SIGNATURE_ERROR = 900006;
    public static final int COMPUTE_ENCRYPT_TEXT_ERROR = 900007;
    public static final int COMPUTE_DECRYPT_TEXT_ERROR = 900008;
    public static final int COMPUTE_DECRYPT_TEXT_LENGTH_ERROR = 900009;
    public static final int COMPUTE_DECRYPT_TEXT_CORPID_ERROR = 900010;
    private static Map<Integer, String> msgMap = new HashMap();
    private Integer code;

    public Integer getCode() {
        return this.code;
    }

    public DingTalkEncryptException(Integer exceptionCode) {
        super((String)msgMap.get(exceptionCode));
        this.code = exceptionCode;
    }

    static {
        msgMap.put(0, "성공");
        msgMap.put(900001, "암호화할 평문이 올바르지 않습니다");
        msgMap.put(900002, "암호화 타임스탬프 매개변수가 올바르지 않습니다");
        msgMap.put(900003, "암호화 난수 문자열 매개변수가 올바르지 않습니다");
        msgMap.put(900005, "서명이 일치하지 않습니다");
        msgMap.put(900006, "서명 계산에 실패했습니다");
        msgMap.put(900004, "올바르지 않은 aes key");
        msgMap.put(900007, "암호문 계산 오류");
        msgMap.put(900008, "복호문 계산 오류");
        msgMap.put(900009, "복호문 길이가 일치하지 않습니다");
        msgMap.put(900010, "복호문의 corpid가 일치하지 않습니다");
    }
}
