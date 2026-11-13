package com.x.meeting.core.entity;

import com.x.base.core.entity.JsonProperties;
import com.x.base.core.project.annotation.FieldDescribe;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 회의 시스템 확장 설정
 * @author sword
 */
public class MeetingConfigProperties extends JsonProperties {

	private static final long serialVersionUID = -271015360526750885L;

	public static final String ONLINE_PROJECT_HST = "好视通";
	public static final String ONLINE_PROJECT_QT = "其他";

	@FieldDescribe("회의 신청 프로세스")
	private Map<String, String> process;

	@FieldDescribe("주 시작 요일")
	private String weekBegin = "1";

	@FieldDescribe("모바일에서 회의 생성 허용 여부")
	private Boolean mobileCreateEnable = true;

	@FieldDescribe("내 회의 뷰")
	private String toMyMeetingViewName = "";

	@FieldDescribe("월 뷰")
	private String toMonthViewName = "";

	@FieldDescribe("주 뷰")
	private String toWeekViewName = "";

	@FieldDescribe("일 뷰")
	private String toDayViewName = "";

	@FieldDescribe("회의 목록 뷰")
	private String toListViewName = "";

	@FieldDescribe("회의실 뷰")
	private String toRoomViewName = "";

	@FieldDescribe("회의 상세 공개 범위")
	private List<String> meetingViewer = new ArrayList<>();

	@FieldDescribe("회의 유형")
	private List<String> typeList = new ArrayList<>();

	@FieldDescribe("")
	private List<String> disableViewList = new ArrayList<>();

	@FieldDescribe("온라인 회의 사용 여부")
	private Boolean enableOnline = false;

	@FieldDescribe("线上会议产品，如：好视通")
	private String onlineProduct = ONLINE_PROJECT_QT;

	@FieldDescribe("온라인 회의 설정(json 객체: {" +
			"'hstUrl':'好视通服务地址','hstKey':'好视通服务接口KEY','hstSecret':'好视通服务接口SECRET'," +
			"'hstAuth':'是否启用好视通会议鉴权登录，默认false','o2ToHstUid':'O2与好视通映射的唯一ID字段'}")
	private OnlineConfig onlineConfig = new OnlineConfig();

	public Map<String, String> getProcess() {
		return process;
	}

	public void setProcess(Map<String, String> process) {
		this.process = process;
	}

	public String getWeekBegin() {
		return weekBegin;
	}

	public void setWeekBegin(String weekBegin) {
		this.weekBegin = weekBegin;
	}

	public Boolean getMobileCreateEnable() {
		return mobileCreateEnable;
	}

	public void setMobileCreateEnable(Boolean mobileCreateEnable) {
		this.mobileCreateEnable = mobileCreateEnable;
	}

	public String getToMyMeetingViewName() {
		return toMyMeetingViewName;
	}

	public void setToMyMeetingViewName(String toMyMeetingViewName) {
		this.toMyMeetingViewName = toMyMeetingViewName;
	}

	public String getToMonthViewName() {
		return toMonthViewName;
	}

	public void setToMonthViewName(String toMonthViewName) {
		this.toMonthViewName = toMonthViewName;
	}

	public String getToWeekViewName() {
		return toWeekViewName;
	}

	public void setToWeekViewName(String toWeekViewName) {
		this.toWeekViewName = toWeekViewName;
	}

	public String getToDayViewName() {
		return toDayViewName;
	}

	public void setToDayViewName(String toDayViewName) {
		this.toDayViewName = toDayViewName;
	}

	public String getToListViewName() {
		return toListViewName;
	}

	public void setToListViewName(String toListViewName) {
		this.toListViewName = toListViewName;
	}

	public String getToRoomViewName() {
		return toRoomViewName;
	}

	public void setToRoomViewName(String toRoomViewName) {
		this.toRoomViewName = toRoomViewName;
	}

	public List<String> getMeetingViewer() {
		return meetingViewer;
	}

	public void setMeetingViewer(List<String> meetingViewer) {
		this.meetingViewer = meetingViewer;
	}

	public List<String> getTypeList() {
		return typeList;
	}

	public void setTypeList(List<String> typeList) {
		this.typeList = typeList;
	}

	public List<String> getDisableViewList() {
		return disableViewList;
	}

	public void setDisableViewList(List<String> disableViewList) {
		this.disableViewList = disableViewList;
	}

	public Boolean getEnableOnline() {
		return enableOnline;
	}

	public void setEnableOnline(Boolean enableOnline) {
		this.enableOnline = enableOnline;
	}

	public OnlineConfig getOnlineConfig() {
		return onlineConfig == null ? new OnlineConfig() : onlineConfig;
	}

	public void setOnlineConfig(OnlineConfig onlineConfig) {
		this.onlineConfig = onlineConfig;
	}

	public String getOnlineProduct() {
		return onlineProduct;
	}

	public void setOnlineProduct(String onlineProduct) {
		this.onlineProduct = onlineProduct;
	}

	public boolean onLineEnabled(){
		if(BooleanUtils.isTrue(enableOnline) && !ONLINE_PROJECT_QT.equals(onlineProduct)){
			return true;
		}
		return false;
	}

	public static class OnlineConfig{

		@FieldDescribe("好视通服务地址如：https://ip:8443")
		private String hstUrl = "";

		@FieldDescribe("好视通服务接口KEY")
		private String hstKey = "4QY08Kyh";

		@FieldDescribe("好视通服务接口SECRET")
		private String hstSecret = "HpQi5csSMrufkM)b&#YWVlr7o*wWUG3G";

		@FieldDescribe("是否启用好视通会议鉴权登录")
		private Boolean hstAuth = false;

		@FieldDescribe("O2与好视通映射的唯一ID字段")
		private String o2ToHstUid = "employee";

		public String getHstUrl() {
			return hstUrl;
		}

		public void setHstUrl(String hstUrl) {
			this.hstUrl = hstUrl;
		}

		public String getHstKey() {
			return hstKey;
		}

		public void setHstKey(String hstKey) {
			this.hstKey = hstKey;
		}

		public String getHstSecret() {
			return hstSecret;
		}

		public void setHstSecret(String hstSecret) {
			this.hstSecret = hstSecret;
		}

		public Boolean getHstAuth() {
			return hstAuth;
		}

		public void setHstAuth(Boolean hstAuth) {
			this.hstAuth = hstAuth;
		}

		public String getO2ToHstUid() {
			return StringUtils.isBlank(o2ToHstUid) ? "employee" : o2ToHstUid;
		}

		public void setO2ToHstUid(String o2ToHstUid) {
			this.o2ToHstUid = o2ToHstUid;
		}
	}
}
