package com.x.general.core.entity;

import com.x.base.core.entity.JsonProperties;
import com.x.base.core.project.annotation.FieldDescribe;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 전자 세금계산서 확장 정보
 * @author sword
 */
public class InvoiceProperties extends JsonProperties {

	private static final long serialVersionUID = -1259157593040432239L;

	@FieldDescribe("상세 기록")
	private List<InvoiceDetail> detailList = new ArrayList<>();

	@FieldDescribe("원본 세금계산서 내용")
	private String content;

	public List<InvoiceDetail> getDetailList() {
		return detailList;
	}

	public void setDetailList(List<InvoiceDetail> detailList) {
		this.detailList = detailList;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
}
