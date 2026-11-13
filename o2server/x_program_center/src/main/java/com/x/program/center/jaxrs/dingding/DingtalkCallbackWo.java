package com.x.program.center.jaxrs.dingding;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

/**
 * Created by fancyLou on 2020-10-26.
 * Copyright © 2020 O2. All rights reserved.
 */
public class DingtalkCallbackWo extends GsonPropertyObject {

    @FieldDescribe("메시지 본문 서명")
    private String msg_signature; //메시지 본문 서명
    @FieldDescribe("타임스탬프")
    private String timeStamp;
    @FieldDescribe("무작위 문자열")
    private String nonce;
    @FieldDescribe("문자열 「success」 암호화 값")
    private String encrypt;
}
