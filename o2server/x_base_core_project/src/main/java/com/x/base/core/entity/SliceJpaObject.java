package com.x.base.core.entity;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import com.x.base.core.project.annotation.FieldDescribe;

@MappedSuperclass
public abstract class SliceJpaObject extends JpaObject {

	private static final long serialVersionUID = 805690971791595604L;

	@FieldDescribe("분산 스토리지 식별 비트.")
	@Column(name = ColumnNamePrefix + distributeFactor_FIELDNAME)
	protected Integer distributeFactor;

	public Integer getDistributeFactor() {
		return distributeFactor;
	}

	public void setDistributeFactor(Integer distributeFactor) {
		this.distributeFactor = distributeFactor;
	}

}