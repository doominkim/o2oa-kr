package com.x.bbs.assemble.control.jaxrs.subjectinfo;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.bbs.entity.BBSVoteOptionGroup;

public class WiVoteOptionGroup extends BBSVoteOptionGroup{
	
	private static final long serialVersionUID = 1L;

	public static List<String> Excludes = new ArrayList<String>();
	
	@FieldDescribe( "투표 선택지 집합" )
	private List<WiVoteOption> voteOptions = null;
	
	@FieldDescribe( "사용자가 투표 결과 제출 시 선택한 선택지 ID 집합" )
	private List<String> selectedVoteOptionIds = null;

	public void setVoteOptions(List<WiVoteOption> voteOptions) {
		this.voteOptions = voteOptions;
	}

	public List<WiVoteOption> getVoteOptions() {
		return voteOptions;
	}

	public List<String> getSelectedVoteOptionIds() {
		return selectedVoteOptionIds;
	}

	public void setSelectedVoteOptionIds(List<String> selectedVoteOptionIds) {
		this.selectedVoteOptionIds = selectedVoteOptionIds;
	}

	
	
	
}
