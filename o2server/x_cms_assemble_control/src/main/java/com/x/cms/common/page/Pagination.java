package com.x.cms.common.page;

import java.util.List;

public class Pagination<T> {
	private int currentPage; // 현재 페이지
	private int pageSize; // 페이지당 표시 건수
	private int totalPage; // 총 페이지 수
	private int totalRecord; // 총 레코드 수
	private List<T> dataList; // 페이지별 반환 데이터

	private Pagination() {
	}

	/*
	 * PageModel 인스턴스 초기화
	 */
	private Pagination(final String pageSize, final String page, final String totalRecord) {
		// 페이지당 표시 건수 초기화
		setPageSize(pageSize);
		// 총 레코드 수 설정
		setTotalRecord(totalRecord);
		// 총 페이지 수 초기화
		setTotalPage();
		// 현재 페이지 초기화
		setCurrentPage(page);
	}

	/*
	 * 외부에서 PageModel 인스턴스 조회
	 */
	@SuppressWarnings("rawtypes")
	public static Pagination newPagination(final String pageSize, final String page, final String totalRecord) {
		return new Pagination(pageSize, page, totalRecord);
	}

	// 현재 요청 페이지 설정
	private void setPageSize(String pagesize) {
		try {
			pageSize = Integer.parseInt(pagesize);
		} catch (java.lang.NumberFormatException e) {
			// 여기서는 예외를 처리하지 않으며 현재 페이지 기본값은 1
			pageSize = 12;
		}
		// 현재 페이지가 첫 페이지보다 작으면 현재 페이지를 첫 페이지로 지정
		if (pageSize <= 0) {
			pageSize = 12;
		}
	}

	// 현재 요청 페이지 설정
	private void setTotalRecord(String total) {
		try {
			totalRecord = Integer.parseInt(total);
		} catch (java.lang.NumberFormatException e) {
			// 여기서는 예외를 처리하지 않으며 현재 페이지 기본값은 1
			totalRecord = 0;
		}
	}

	// 현재 요청 페이지 설정
	private void setCurrentPage(String page) {
		try {
			currentPage = Integer.parseInt(page);

		} catch (java.lang.NumberFormatException e) {
			// 여기서는 예외를 처리하지 않으며 현재 페이지 기본값은 1
			currentPage = 1;
		}
		// 현재 페이지가 첫 페이지보다 작으면 현재 페이지를 첫 페이지로 지정
		if (currentPage < 1) {

			currentPage = 1;
		}

		if (currentPage > totalPage) {

			currentPage = totalPage;

		}

	}

	private void setTotalPage() {
		if (totalRecord % pageSize == 0) {

			totalPage = totalRecord / pageSize;
		} else {
			totalPage = totalRecord / pageSize + 1;
		}
	}

	/*
	 * 현재 페이지 조회
	 */
	public int getCurrentPage() {
		return currentPage;
	}

	/*
	 * 총 페이지 수 조회
	 */
	public int getTotalPage() {
		return totalPage;

	}

	/*
	 * 시작 행 조회, 0부터 시작
	 */
	public int getStartRow() {
		return (currentPage - 1) * pageSize;
	}

	/*
	 * 끝 행 조회
	 */
	public int getEndRow() {
		return currentPage * pageSize - 1;
	}

	/*
	 * 페이지 데이터 조회
	 */
	public List<T> getDataList() {
		return dataList;
	}

	/*
	 * 페이지 데이터 설정
	 */
	public void setDataList(List<T> dataList) {
		this.dataList = dataList;
	}

	// 첫 페이지
	public int getFirst() {
		return 1;
	}

	//
	// 이전 페이지

	public int getPrevious() {

		return currentPage - 1;
	}

	//
	// // 다음 페이지
	public int getNext() {
		return currentPage + 1;
	}

	//
	// // 마지막 페이지
	//
	public int getLast() {
		return totalPage;
	}

	public int getTotalRecord() {
		return totalRecord;
	}

	public int getPageSize() {
		return pageSize;
	}

}
