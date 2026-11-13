package com.x.base.core.project.message;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.x.base.core.project.Context;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.i18n.KoreanMessages;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.x_message_assemble_communicate;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

public class MessageConnector {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageConnector.class);

    private MessageConnector() {
        // nothing
    }

    private static Gson gson = XGsonBuilder.instance();

    // 앱 생성
    public static final String TYPE_APPLICATION_CREATE = "application_create";

    // 앱 수정
    public static final String TYPE_APPLICATION_UPDATE = "application_update";

    // 앱 삭제
    public static final String TYPE_APPLICATION_DELETE = "application_delete";

    // 프로세스 생성
    public static final String TYPE_PROCESS_CREATE = "process_create";

    // 프로세스 수정
    public static final String TYPE_PROCESS_UPDATE = "process_update";

    // 프로세스 삭제
    public static final String TYPE_PROCESS_DELETE = "process_delete";

    // 새 업무 문서가 메시지 노드를 통과함
    public static final String TYPE_ACTIVITY_MESSAGE = "activity_message";

    // 업무 문서 완료 후 완료 업무 문서로 전환
    public static final String TYPE_WORK_TO_WORKCOMPLETED = "work_to_workCompleted";

    // 업무 문서 생성
    public static final String TYPE_WORK_CREATE = "work_create";

    // 업무 문서 삭제
    public static final String TYPE_WORK_DELETE = "work_delete";

    // 완료 업무 문서 생성
    public static final String TYPE_WORKCOMPLETED_CREATE = "workCompleted_create";

    // 완료 업무 문서 삭제
    public static final String TYPE_WORKCOMPLETED_DELETE = "workCompleted_delete";

    // 결재 대기 완료 후 처리 완료로 전환
    public static final String TYPE_TASK_TO_TASKCOMPLETED = "task_to_taskCompleted";

    // 결재 대기 생성
    public static final String TYPE_TASK_CREATE = "task_create";

    // 결재 대기 삭제
    public static final String TYPE_TASK_DELETE = "task_delete";

    // 결재 대기 만료 임박 독촉
    public static final String TYPE_TASK_URGE = "task_urge";

    // 결재 대기 만료
    public static final String TYPE_TASK_EXPIRE = "task_expire";

    // 결재 대기 알림
    public static final String TYPE_TASK_PRESS = "task_press";

    // 처리 완료 생성
    public static final String TYPE_TASKCOMPLETED_CREATE = "taskCompleted_create";

    // 처리 완료 삭제
    public static final String TYPE_TASKCOMPLETED_DELETE = "taskCompleted_delete";

    // 열람 대기를 열람 완료로 전환
    public static final String TYPE_READ_TO_READCOMPLETED = "read_to_readCompleted";

    // 열람 대기 생성
    public static final String TYPE_READ_CREATE = "read_create";

    // 열람 대기 삭제
    public static final String TYPE_READ_DELETE = "read_delete";

    // 열람 완료 생성
    public static final String TYPE_READCOMPLETED_CREATE = "readCompleted_create";

    // 열람 완료 삭제
    public static final String TYPE_READCOMPLETED_DELETE = "readCompleted_delete";

    // 참조 생성
    public static final String TYPE_REVIEW_CREATE = "review_create";

    // 참조 삭제
    public static final String TYPE_REVIEW_DELETE = "review_delete";

    // 회의 초대
    public static final String TYPE_MEETING_INVITE = "meeting_invite";

    // 회의 삭제
    public static final String TYPE_MEETING_DELETE = "meeting_delete";

    // 회의 초대 수락
    public static final String TYPE_MEETING_ACCEPT = "meeting_accept";

    // 회의 초대 거절
    public static final String TYPE_MEETING_REJECT = "meeting_reject";

    // 첨부파일 생성
    public static final String TYPE_ATTACHMENT_CREATE = "attachment_create";

    // 첨부파일 삭제
    public static final String TYPE_ATTACHMENT_DELETE = "attachment_delete";

    // 첨부파일 공유
    public static final String TYPE_ATTACHMENT_SHARE = "attachment_share";

    // 첨부파일 공유 취소
    public static final String TYPE_ATTACHMENT_SHARECANCEL = "attachment_shareCancel";

    // 첨부파일 편집 가능 설정
    public static final String TYPE_ATTACHMENT_EDITOR = "attachment_editor";

    // 첨부파일 편집 가능 취소
    public static final String TYPE_ATTACHMENT_EDITORCANCEL = "attachment_editorCancel";

    // 첨부파일 편집 가능 수정
    public static final String TYPE_ATTACHMENT_EDITORMODIFY = "attachment_editorModify";

    // 캘린더 알림
    public static final String TYPE_CALENDAR_ALARM = "calendar_alarm";

    // 사용자 정의 메시지 생성
    public static final String TYPE_CUSTOM_CREATE = "custom_create";

    // 업무 관리 작업 생성
    public static final String TYPE_TEAMWORK_TASKCREATE = "teamwork_taskCreate";

    // 업무 관리 작업 수정
    public static final String TYPE_TEAMWORK_TASKUPDATE = "teamwork_taskUpdate";

    // 업무 관리 작업 삭제
    public static final String TYPE_TEAMWORK_TASKDELETE = "teamwork_taskDelete";

    // 업무 관리 작업 시간 초과
    public static final String TYPE_TEAMWORK_TASKOVERTIME = "teamwork_taskOvertime";

    // 업무 관리 채팅
    public static final String TYPE_TEAMWORK_CHAT = "teamwork_chat";

    // 콘텐츠 관리 게시
    public static final String TYPE_CMS_PUBLISH = "cms_publish";

    // 콘텐츠 관리 게시 작성자 알림
    public static final String TYPE_CMS_PUBLISH_TO_CREATOR = "cms_publish_to_creator";

    // 포럼 게시글 작성
    public static final String TYPE_BBS_SUBJECTCREATE = "bbs_subjectCreate";

    // 포럼 답글 작성
    public static final String TYPE_BBS_REPLYCREATE = "bbs_replyCreate";

    // 마인드맵 파일 전송
    public static final String TYPE_MIND_FILESEND = "mind_fileSend";

    // 마인드맵 파일 공유
    public static final String TYPE_MIND_FILESHARE = "mind_fileShare";

    // IM 채팅 메시지 전송
    public static final String TYPE_IM_CREATE = "im_create";
    // IM 메시지 회수
    public static final String TYPE_IM_REVOKE = "im_revoke";
    // IM 대화 삭제
    public static final String TYPE_IM_CONVERSATION_DELETE = "im_conv_delete";
    // IM 대화 자체 수정
    public static final String TYPE_IM_CONVERSATION_UPDATE = "im_conv_update";

    // 근태 출퇴근 체크 알림 메시지
    public static final String TYPE_ATTENDANCE_CHECK_IN_ALERT = "attendance_checkInAlert";
    // 근태 출퇴근 체크 이상 알림 메시지
    public static final String TYPE_ATTENDANCE_CHECK_IN_EXCEPTION = "attendance_checkInException";

    // 사용자 정의 메시지
    public static final String TYPE_CUSTOM_PREFIX = "custom_";

    // 감사 로그 알림
    // public static final String TYPE_AUDIT_LOG = "audit_log";

    public static final String CONSUME_WS = "ws";

    public static final String CONSUME_PMS_INNER = "pmsinner";

    public static final String CONSUME_CALENDAR = "calendar";

    public static final String CONSUME_DINGDING = "dingding";

    public static final String CONSUME_WELINK = "welink";

    public static final String CONSUME_ANDFX = "andfx";

    public static final String CONSUME_ZHENGWUDINGDING = "zhengwudingding";

    public static final String CONSUME_QIYEWEIXIN = "qiyeweixin";

    public static final String CONSUME_MPWEIXIN = "mpweixin"; // 위챗 공식 계정

    public static final String CONSUME_KAFKA = "kafka";

    public static final String CONSUME_ACTIVEMQ = "activemq";
    // restful 유형
    public static final String CONSUME_RESTFUL = "restful";
    // 메일 유형
    public static final String CONSUME_MAIL = "mail";
    // 시스템 내부 인터페이스 호출
    public static final String CONSUME_API = "api";
    // jdbc 쓰기
    public static final String CONSUME_JDBC = "jdbc";
    // 사용자 정의 테이블
    public static final String CONSUME_TABLE = "table";
    // 사용자 정의 소비자 유형 접두사
    public static final String CONSUME_CUSTOM_PREFIX = "custom_";

    public static final Set<String> TYPES = Set.of(TYPE_APPLICATION_CREATE,
            TYPE_APPLICATION_UPDATE, TYPE_APPLICATION_DELETE, TYPE_PROCESS_CREATE, TYPE_PROCESS_UPDATE,
            TYPE_PROCESS_DELETE, TYPE_ACTIVITY_MESSAGE, TYPE_WORK_TO_WORKCOMPLETED, TYPE_WORK_CREATE, TYPE_WORK_DELETE,
            TYPE_WORKCOMPLETED_CREATE, TYPE_WORKCOMPLETED_DELETE, TYPE_TASK_TO_TASKCOMPLETED, TYPE_TASK_CREATE,
            TYPE_TASK_DELETE, TYPE_TASK_URGE, TYPE_TASK_EXPIRE, TYPE_TASK_PRESS, TYPE_TASKCOMPLETED_CREATE,
            TYPE_TASKCOMPLETED_DELETE, TYPE_READ_TO_READCOMPLETED, TYPE_READ_CREATE, TYPE_READ_DELETE,
            TYPE_READCOMPLETED_CREATE, TYPE_READCOMPLETED_DELETE, TYPE_REVIEW_CREATE, TYPE_REVIEW_DELETE,
            TYPE_MEETING_INVITE, TYPE_MEETING_DELETE, TYPE_MEETING_ACCEPT, TYPE_MEETING_REJECT, TYPE_ATTACHMENT_CREATE,
            TYPE_ATTACHMENT_DELETE, TYPE_ATTACHMENT_SHARE, TYPE_ATTACHMENT_SHARECANCEL, TYPE_ATTACHMENT_EDITOR,
            TYPE_ATTACHMENT_EDITORCANCEL, TYPE_ATTACHMENT_EDITORMODIFY, TYPE_CALENDAR_ALARM, TYPE_CUSTOM_CREATE,
            TYPE_TEAMWORK_TASKCREATE, TYPE_TEAMWORK_TASKUPDATE, TYPE_TEAMWORK_TASKDELETE, TYPE_TEAMWORK_TASKOVERTIME,
            TYPE_TEAMWORK_CHAT, TYPE_CMS_PUBLISH, TYPE_CMS_PUBLISH_TO_CREATOR, TYPE_BBS_SUBJECTCREATE,
            TYPE_BBS_REPLYCREATE, TYPE_MIND_FILESEND, TYPE_MIND_FILESHARE, TYPE_IM_CREATE, TYPE_IM_REVOKE, TYPE_IM_CONVERSATION_DELETE, TYPE_IM_CONVERSATION_UPDATE,
            TYPE_ATTENDANCE_CHECK_IN_ALERT, TYPE_ATTENDANCE_CHECK_IN_EXCEPTION);

    private static Context context;

    private static LinkedBlockingQueue<Wrap> connectQueue = new LinkedBlockingQueue<>(10000);

    public static void start(Context context) {
        MessageConnector.context = context;
        ConnectorThread thread = new ConnectorThread();
        thread.setName("MessageConnector-" + context.clazz().getSimpleName());
        thread.start();
    }

    public static void stop() {
        try {
            connectQueue.put(new StopSignal());
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
    }

    public static void send(String type, String title, String person, Object body) {
        Wrap wrap = new Wrap();
        wrap.setType(type);
        wrap.setTitle(KoreanMessages.translate(title));
        wrap.setPerson(person);
        wrap.setBody(gson.toJsonTree(body));
        try {
            connectQueue.put(wrap);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            LOGGER.error(ie);
        }
    }

    private static class ConnectorThread extends Thread {
        @Override
        public void run() {
            while (true) {
                try {
                    Wrap o = connectQueue.take();
                    if (o instanceof StopSignal) {
                        break;
                    } else {
                        context.applications().postQuery(x_message_assemble_communicate.class, "connector", o);
                    }
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    LOGGER.error(ie);
                } catch (Exception e) {
                    LOGGER.error(e);
                }
            }
        }
    }

    public static class Wrap extends GsonPropertyObject {

        private static final long serialVersionUID = 2603938363315602487L;

        @FieldDescribe("유형")
        private String type;

        @FieldDescribe("사용자")
        private String person;

        @FieldDescribe("제목")
        private String title;

        @FieldDescribe("콘텐츠 푸시")
        private JsonElement body;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getPerson() {
            return person;
        }

        public void setPerson(String person) {
            this.person = person;
        }

        public JsonElement getBody() {
            return body;
        }

        public void setBody(JsonElement body) {
            this.body = body;
        }

    }

    private static class StopSignal extends Wrap {

        private static final long serialVersionUID = -5631247237688117035L;

    }

}
