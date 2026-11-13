package com.x.meeting.assemble.control.wrapout;

import com.x.base.core.project.annotation.FieldDescribe;
import java.util.ArrayList;
import java.util.List;

import com.x.base.core.entity.JpaObject;
import com.x.meeting.core.entity.Meeting;

public class WrapOutMeeting extends Meeting {

	private static final long serialVersionUID = -969148596991975992L;
	public static List<String> Excludes = new ArrayList<>(JpaObject.FieldsInvisible);

	@FieldDescribe("회의 상태")
	private String status;
	/* 내가 신청한 */
	private Boolean myApply;
	/* 내 확인 대기 */
	private Boolean myWaitConfirm;

	private Boolean myWaitAccept;

	private Boolean myAccept;

	private Boolean myReject;

	private Long rank;

	private WrapOutRoom woRoom;

	private List<WrapOutAttachment> attachmentList;
	/* 회의실 주소 */
	private String roomAddress;

	public String getRoomAddress() {
		return roomAddress;
	}

	public void setRoomAddress(String roomAddress) {
		this.roomAddress = roomAddress;
	}

	public Long getRank() {
		return rank;
	}

	public void setRank(Long rank) {
		this.rank = rank;
	}

	public List<WrapOutAttachment> getAttachmentList() {
		return attachmentList;
	}

	public void setAttachmentList(List<WrapOutAttachment> attachmentList) {
		this.attachmentList = attachmentList;
	}

	public Boolean getMyApply() {
		return myApply;
	}

	public void setMyApply(Boolean myApply) {
		this.myApply = myApply;
	}

	public Boolean getMyWaitConfirm() {
		return myWaitConfirm;
	}

	public void setMyWaitConfirm(Boolean myWaitConfirm) {
		this.myWaitConfirm = myWaitConfirm;
	}

	public Boolean getMyWaitAccept() {
		return myWaitAccept;
	}

	public void setMyWaitAccept(Boolean myWaitAccept) {
		this.myWaitAccept = myWaitAccept;
	}

	public Boolean getMyReject() {
		return myReject;
	}

	public void setMyReject(Boolean myReject) {
		this.myReject = myReject;
	}

	public Boolean getMyAccept() {
		return myAccept;
	}

	public void setMyAccept(Boolean myAccept) {
		this.myAccept = myAccept;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public WrapOutRoom getWoRoom() {
		return woRoom;
	}

	public void setWoRoom(WrapOutRoom woRoom) {
		this.woRoom = woRoom;
	}

}
