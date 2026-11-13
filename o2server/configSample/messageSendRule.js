/**
 * 통합 메시지 푸시 실행 스크립트, messages.json 설정 파일에서 사용
 * 메서드는 boolean 타입을 반환하며, true는 발송 조건을 만족하고 body 메시지 내용 수정을 수용함을, false는 발송하지 않음을 의미
 * 변수 message는 메시지 본문으로, 원본 메시지 형식은 {type:'',person,:'',title,'',body:null}인 obj 객체
 * 변수 body는 메시지 내용으로, 메시지 유형마다 본문이 다를 수 있으며 Gson의 JsonObject 객체
 * 아래 excute 메서드는 기안 상태의 결재 대기 메시지를 보내지 않음, excute1 메서드는 body 객체의 modifyFlag 매개변수를 변경 또는 추가, excute2 메서드는 message 객체의 속성값 조회 예시
 */
function excute() {
    if(body.has("first") && body.has("workCreateType")){
        if (body.get("first").getAsBoolean() && "surface".equals(body.get("workCreateType").getAsString())){
            return false;
        }
    }
    return true;
}
function excute1() {
    body.addProperty("modifyFlag","1");
    return true;
}
function excute2() {
    print(message.getType());
    print(message.getTitle());
    return true;
}
