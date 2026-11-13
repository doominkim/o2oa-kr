package com.x.message.assemble.communicate.message;

import java.util.List;

import com.x.base.core.project.gson.GsonPropertyObject;

public class WeLinkMessage extends GsonPropertyObject {

	private static final long serialVersionUID = -5678682987495247923L;
	/**
	 * { "msgRange": 0,0: 사용자별 푸시 필수 "toUserList": ["john@welink", "john@1234"], 사용자 목록
	 * 필수 "msgTitle": "{"EN": "hello world", "CN": "안녕하세요"}", //제목 필수
	 * 중국어만 있으면 제목 문자열을 직접 쓰면 되며 json 불필요 "msgContent": "{"CN": "환영합니다", "EN":"Welcome"}",
	 * //메시지 내용 필수. 중국어만 있으면 제목 문자열을 직접 쓰면 되며 json 불필요 "urlType": "html", //이 두 파라미터는 메시지 하이퍼링크용
	 * 단, 현재는 미니 프로그램만 지원하고 경량 앱은 미지원 "urlPath": "h5://demo.com", "msgOwner": "프로세스 플랫폼", 메시지 소유자 필수
	 * "createTime": "1487289600000" 생성 시간 }
	 */

	private String msgRange;
	private List<String> toUserList;
	private String msgTitle;
	private String msgContent;
	private String urlType;
	private String urlPath;
	private String msgOwner;
	private String createTime;

	public String getMsgRange() {
		return msgRange;
	}

	public void setMsgRange(String msgRange) {
		this.msgRange = msgRange;
	}

	public List<String> getToUserList() {
		return toUserList;
	}

	public void setToUserList(List<String> toUserList) {
		this.toUserList = toUserList;
	}

	public String getMsgTitle() {
		return msgTitle;
	}

	public void setMsgTitle(String msgTitle) {
		this.msgTitle = msgTitle;
	}

	public String getMsgContent() {
		return msgContent;
	}

	public void setMsgContent(String msgContent) {
		this.msgContent = msgContent;
	}

	public String getUrlType() {
		return urlType;
	}

	public void setUrlType(String urlType) {
		this.urlType = urlType;
	}

	public String getUrlPath() {
		return urlPath;
	}

	public void setUrlPath(String urlPath) {
		this.urlPath = urlPath;
	}

	public String getMsgOwner() {
		return msgOwner;
	}

	public void setMsgOwner(String msgOwner) {
		this.msgOwner = msgOwner;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
}