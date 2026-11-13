package com.x.processplatform.core.entity.content;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.x.base.core.entity.JsonProperties;
import com.x.base.core.project.annotation.FieldDescribe;

public class SnapProperties extends JsonProperties {

	private static final long serialVersionUID = -2320401657143206452L;

	@FieldDescribe("업무 데이터")
	private Data data = new Data();

	@FieldDescribe("제목")
	private String title;

	@FieldDescribe("식별자")
	private String job;

	@FieldDescribe("문서")
	private List<Work> workList = new ArrayList<Work>();

	@FieldDescribe("완료된 업무 문서")
	private WorkCompleted workCompleted;

	@FieldDescribe("결재 대기")
	private List<Task> taskList = new ArrayList<Task>();

	@FieldDescribe("처리 완료")
	private List<TaskCompleted> taskCompletedList = new ArrayList<TaskCompleted>();

	@FieldDescribe("열람 대기")
	private List<Read> readList = new ArrayList<Read>();

	@FieldDescribe("열람 완료")
	private List<ReadCompleted> readCompletedList = new ArrayList<ReadCompleted>();

	@FieldDescribe("참조 열람")
	private List<Review> reviewList = new ArrayList<Review>();

	@FieldDescribe("첨부파일")
	private List<Attachment> attachmentList = new ArrayList<Attachment>();

	@FieldDescribe("기록")
	private List<Record> recordList = new ArrayList<Record>();

	@FieldDescribe("업무 로그")
	private List<WorkLog> workLogList = new ArrayList<WorkLog>();

	@FieldDescribe("서식 파일 버전")
	private List<DocumentVersion> documentVersionList = new ArrayList<DocumentVersion>();

	@FieldDescribe("문서 서명 결재 정보")
	private List<DocSign> docSignList = new ArrayList<>();

	@FieldDescribe("문서 서명 결재 첨부파일 정보")
	private List<DocSignScrawl> docSignScrawlList = new ArrayList<>();

	private Map<String, String> attachmentContentMap = new HashMap<>();

	public Data getData() {
		return data;
	}

	public void setData(Data data) {
		this.data = data;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getJob() {
		return job;
	}

	public List<Work> getWorkList() {
		if (null == this.workList) {
			this.workList = new ArrayList<Work>();
		}
		return workList;
	}

	public List<Task> getTaskList() {
		if (null == this.taskList) {
			this.taskList = new ArrayList<Task>();
		}
		return taskList;
	}

	public List<TaskCompleted> getTaskCompletedList() {
		if (null == this.taskCompletedList) {
			this.taskCompletedList = new ArrayList<TaskCompleted>();
		}
		return taskCompletedList;
	}

	public List<Read> getReadList() {
		if (null == this.readList) {
			this.readList = new ArrayList<Read>();
		}
		return readList;
	}

	public List<ReadCompleted> getReadCompletedList() {
		if (null == this.readCompletedList) {
			this.readCompletedList = new ArrayList<ReadCompleted>();
		}
		return readCompletedList;
	}

	public List<Review> getReviewList() {
		if (null == this.reviewList) {
			this.reviewList = new ArrayList<Review>();
		}
		return reviewList;
	}

	public List<Attachment> getAttachmentList() {
		if (null == this.attachmentList) {
			this.attachmentList = new ArrayList<Attachment>();
		}
		return attachmentList;
	}

	public List<Record> getRecordList() {
		if (null == this.recordList) {
			this.recordList = new ArrayList<Record>();
		}
		return recordList;
	}

	public List<WorkLog> getWorkLogList() {
		if (null == this.workLogList) {
			this.workLogList = new ArrayList<WorkLog>();
		}
		return workLogList;
	}

	public List<DocumentVersion> getDocumentVersionList() {
		if (null == this.documentVersionList) {
			this.documentVersionList = new ArrayList<DocumentVersion>();
		}
		return documentVersionList;
	}

	public void setJob(String job) {
		this.job = job;
	}

	public void setWorkList(List<Work> workList) {
		this.workList = workList;
	}

	public void setTaskList(List<Task> taskList) {
		this.taskList = taskList;
	}

	public void setTaskCompletedList(List<TaskCompleted> taskCompletedList) {
		this.taskCompletedList = taskCompletedList;
	}

	public void setReadList(List<Read> readList) {
		this.readList = readList;
	}

	public void setReadCompletedList(List<ReadCompleted> readCompletedList) {
		this.readCompletedList = readCompletedList;
	}

	public void setReviewList(List<Review> reviewList) {
		this.reviewList = reviewList;
	}

	public void setAttachmentList(List<Attachment> attachmentList) {
		this.attachmentList = attachmentList;
	}

	public void setRecordList(List<Record> recordList) {
		this.recordList = recordList;
	}

	public void setWorkLogList(List<WorkLog> workLogList) {
		this.workLogList = workLogList;
	}

	public void setDocumentVersionList(List<DocumentVersion> documentVersionList) {
		this.documentVersionList = documentVersionList;
	}

	public WorkCompleted getWorkCompleted() {
		return workCompleted;
	}

	public void setWorkCompleted(WorkCompleted workCompleted) {
		this.workCompleted = workCompleted;
	}

	public Map<String, String> getAttachmentContentMap() {
		return attachmentContentMap;
	}

	public void setAttachmentContentMap(Map<String, String> attachmentContentMap) {
		this.attachmentContentMap = attachmentContentMap;
	}

	public List<DocSign> getDocSignList() {
		if(docSignList == null){
			docSignList = new ArrayList<>();
		}
		return docSignList;
	}

	public void setDocSignList(List<DocSign> docSignList) {
		this.docSignList = docSignList;
	}

	public List<DocSignScrawl> getDocSignScrawlList() {
		if(docSignScrawlList == null){
			this.docSignScrawlList = new ArrayList<>();
		}
		return docSignScrawlList;
	}

	public void setDocSignScrawlList(List<DocSignScrawl> docSignScrawlList) {
		this.docSignScrawlList = docSignScrawlList;
	}
}
