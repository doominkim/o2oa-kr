package com.x.message.assemble.communicate.message;

import com.x.base.core.project.gson.GsonPropertyObject;

public class ZhengwuDingdingMessage extends GsonPropertyObject {

	private static final long serialVersionUID = -1369430177900592062L;
	/**
	 * agent_id Number 필수 1234 기업 개발자는 앱 설정 페이지에서 확인 가능. ISV 개발자는 기업 기본 정보 인터페이스로 조회 가능 userid_list
	 * String 선택(userid_list,dept_id_list, to_all_user 중 하나는 비어 있으면 안 됨) zhangsan,lisi
	 * 수신자 userid 목록, 최대 목록 길이: 20 dept_id_list String 선택 123,456 수신자 부서 id 목록, 최대 목록 길이: 20,
	 * 수신자는 부서 id 하위(하위 부서의 모든 사용자 포함) to_all_user Boolean 선택 false 기업 전체 사용자에게 발송 여부(ISV는 true 설정 불가)
	 * msg Json 필수 {"msgtype":"text","text":{"content":"메시지 내용"}}
	 * 메시지 내용. 자세한 내용은 「메시지 유형과 데이터 형식」 참조. 최대 2048바이트. 동일한 메시지 내용은 당일 한 번만 수신 가능.
	 * 
	 * @author ray
	 *
	 */

	private Long agentId = 0L;
	private String touser = "";
	private Msg msg = new Msg();

	public static class Msg {
		/**
		 * { "msgtype": "text", "text": { "content": "홍길동의 휴가 신청" } }
		 */
		private String msgtype = "text";
		private Text text = new Text();

		public String getMsgtype() {
			return msgtype;
		}

		public void setMsgtype(String msgtype) {
			this.msgtype = msgtype;
		}

		public Text getText() {
			return text;
		}

		public void setText(Text text) {
			this.text = text;
		}

	}

	public static class Text {

		private String content = "";

		public String getContent() {
			return content;
		}

		public void setContent(String content) {
			this.content = content;
		}

	}

	public Msg getMsg() {
		return msg;
	}

	public void setMsg(Msg msg) {
		this.msg = msg;
	}

	public Long getAgentId() {
		return agentId;
	}

	public void setAgentId(Long agentId) {
		this.agentId = agentId;
	}

	public String getTouser() {
		return touser;
	}

	public void setTouser(String touser) {
		this.touser = touser;
	}

}