package com.x.message.assemble.communicate.message;


public class QiyeweixinTextMessage extends QiyeweixinBaseMessage {

	private static final long serialVersionUID = 8600141025638957869L;
	/**
	 * { "touser" : "UserID1|UserID2|UserID3", "toparty" : "PartyID1|PartyID2",
	 * "totag" : "TagID1 | TagID2", "msgtype" : "text", "agentid" : 1, "text" : {
	 * "content" : "택배가 도착했습니다. 사원증을 지참하고 우편 센터에서 수령하세요.\n출발 전 <a
	 * href=\"http://work.weixin.qq.com\">우편 센터 실시간 영상</a>을 확인하여 대기 줄을 피하세요." }, "safe":0 }
	 */

	public QiyeweixinTextMessage() {
		this.setMsgtype("text");
	}

	private Text text = new Text();
	private Long safe = 0L;


	public Text getText() {
		return text;
	}

	public void setText(Text text) {
		this.text = text;
	}

	public Long getSafe() {
		return safe;
	}

	public void setSafe(Long safe) {
		this.safe = safe;
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

}
