package com.x.message.assemble.communicate.message;

import com.x.base.core.project.gson.GsonPropertyObject;

public   class DingdingBaseMessage extends GsonPropertyObject {

	/**
	 * agent_id Number 필수 1234 기업 개발자는 앱 설정 페이지에서 확인 가능. ISV 개발자는 기업 기본 정보 인터페이스로 조회 가능 userid_list
	 * String 선택(userid_list,dept_id_list, to_all_user 중 하나는 비어 있으면 안 됨) zhangsan,lisi
	 * 수신자 userid 목록, 최대 목록 길이: 20 dept_id_list String 선택 123,456 수신자 부서 id 목록, 최대 목록 길이: 20,
	 * 수신자는 부서 id 하위(하위 부서의 모든 사용자 포함) to_all_user Boolean 선택 false 기업 전체 사용자에게 발송 여부(ISV는 true 설정 불가)
	 * msg Json 필수 {"msgtype":"text","text":{"content":"메시지 내용"}} msg markdown 메시지 형식
	 * {"msgtype":"markdown","markdown":{"title":"메시지 내용",
	 * "text":"[이것은 링크입니다](http://o2oa.net)"}}
	 * 메시지 내용. 자세한 내용은 「메시지 유형과 데이터 형식」 참조. 최대 2048바이트. 동일한 메시지 내용은 당일 한 번만 수신 가능.
	 */

	private Long agent_id = 0L;
	private String userid_list = "";
	private Msg msg = new Msg();

	public DingdingBaseMessage() {

	}

	public static class Msg {
		// {
		// "msgtype": "text",
		// "text": {
		// "content": "홍길동의 휴가 신청"
		// }
		// }
		private String msgtype = "text";
		private Text text = new Text();
		private Markdown markdown = new Markdown();

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

		public Markdown getMarkdown() {
			return markdown;
		}

		public void setMarkdown(Markdown markdown) {
			this.markdown = markdown;
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

	public static class Markdown {
		private String title = "";// 메시지 헤더 텍스트
		private String text = "";// markdown 형식 메시지

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getText() {
			return text;
		}

		public void setText(String text) {
			this.text = text;
		}
	}

	public Long getAgent_id() {
		return agent_id;
	}

	public void setAgent_id(Long agent_id) {
		this.agent_id = agent_id;
	}

	public String getUserid_list() {
		return userid_list;
	}

	public void setUserid_list(String userid_list) {
		this.userid_list = userid_list;
	}

	public Msg getMsg() {
		return msg;
	}

	public void setMsg(Msg msg) {
		this.msg = msg;
	}

}
