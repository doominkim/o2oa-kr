package com.x.base.core.entity;

/**
 * 첨부파일 스토리지 유형
 * @author sword
 */

public enum StorageType {
	file, processPlatform, mind, meeting, calendar, cms, bbs, teamwork, structure, im, general, custom;

	public static final int length = JpaObject.length_32B;
}
