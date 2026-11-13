package com.x.query.core.express.plan;

import java.util.List;

import org.apache.commons.collections4.list.TreeList;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.tools.ListTools;

public class Calculate extends GsonPropertyObject {

	public Calculate() {
		this.isGroup = false;
		this.calculateList = new TreeList<CalculateEntry>();
	}

	public static final String GROUPMERGETYPE_SPECIFIED = "specified";
	public static final String GROUPMERGETYPE_ITEM = "item";
	public static final String GROUPMERGETYPE_INTERSECTION = "intersection";
	public static final String GROUPMERGETYPE_SUM = "sum";

	@FieldDescribe("분류 뷰 여부")
	public Boolean isGroup;

	@FieldDescribe("합계 여부")
	public Boolean isAmount;

	@FieldDescribe("정렬 유형")
	public String orderType;

	@FieldDescribe("정렬 열")
	public String orderColumn;

	@FieldDescribe("분류 사용자 정의 표시")
	public List<String> groupSpecifiedList;

	@FieldDescribe("분류 뷰 제목")
	public String title;

	@FieldDescribe("specified,item,intersection,sum")
	public String groupMergeType;

	public List<CalculateEntry> calculateList;
	
	@FieldDescribe("차트")
	public List<String> chart;
	
	public Boolean available() {
		for (CalculateEntry o : ListTools.nullToEmpty(this.calculateList)) {
			if (o.available()) {
				return true;
			}
		}
		return false;
	}

	public CalculateEntry find(String id) {
		for (CalculateEntry o : this.calculateList) {
			if (StringUtils.equals(o.id, id)) {
				return o;
			}
		}
		return null;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	};

}