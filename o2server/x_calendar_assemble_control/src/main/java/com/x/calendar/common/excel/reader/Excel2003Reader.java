//package com.x.calendar.common.excel.reader;
//
//import java.io.FileInputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.util.ArrayList;
//import java.util.List;
//
//import org.apache.poi.hssf.eventusermodel.EventWorkbookBuilder.SheetRecordCollectingListener;
//import org.apache.poi.hssf.eventusermodel.FormatTrackingHSSFListener;
//import org.apache.poi.hssf.eventusermodel.HSSFEventFactory;
//import org.apache.poi.hssf.eventusermodel.HSSFListener;
//import org.apache.poi.hssf.eventusermodel.HSSFRequest;
//import org.apache.poi.hssf.eventusermodel.MissingRecordAwareHSSFListener;
//import org.apache.poi.hssf.eventusermodel.dummyrecord.LastCellOfRowDummyRecord;
//import org.apache.poi.hssf.eventusermodel.dummyrecord.MissingCellDummyRecord;
//import org.apache.poi.hssf.model.HSSFFormulaParser;
//import org.apache.poi.hssf.record.BOFRecord;
//import org.apache.poi.hssf.record.BlankRecord;
//import org.apache.poi.hssf.record.BoolErrRecord;
//import org.apache.poi.hssf.record.BoundSheetRecord;
//import org.apache.poi.hssf.record.FormulaRecord;
//import org.apache.poi.hssf.record.LabelRecord;
//import org.apache.poi.hssf.record.LabelSSTRecord;
//import org.apache.poi.hssf.record.NumberRecord;
//import org.apache.poi.hssf.record.Record;
//import org.apache.poi.hssf.record.SSTRecord;
//import org.apache.poi.hssf.record.StringRecord;
//import org.apache.poi.hssf.usermodel.HSSFWorkbook;
//import org.apache.poi.poifs.filesystem.POIFSFileSystem;
//
///**
// * 추상 Excel2003 리더, HSSFListener 리스너를 구현해 이벤트 기반 방식으로 excel2003
// * 내용을 파싱, 특정 이벤트에서만 트리거되어 메모리 사용을 크게 줄임.
// *
// */
//public class Excel2003Reader implements HSSFListener {
//	private int minColumns = -1;
//	// private POIFSFileSystem fs;
//	private int lastRowNumber;
//	private int lastColumnNumber;
//
//	/** Should we output the formula, or the value it has? */
//	private boolean outputFormulaValues = true;
//
//	/** For parsing Formulas */
//	private SheetRecordCollectingListener workbookBuildingListener;
//	// excel2003 워크북
//	private HSSFWorkbook stubWorkbook;
//
//	// Records we pick up as we process
//	private SSTRecord sstRecord;
//	private FormatTrackingHSSFListener formatListener;
//
//	// 시트 인덱스
//	private int sheetIndex = -1;
//	private BoundSheetRecord[] orderedBSRs;
//	@SuppressWarnings("rawtypes")
//	private ArrayList boundSheetRecords = new ArrayList();
//
//	// For handling formulas with string results
//	private int nextRow;
//	private int nextColumn;
//	private boolean outputNextStringRecord;
//	// 현재 행
//	private int curRow = 0;
//	// 행 레코드 저장 컨테이너
//	private List<String> rowlist = new ArrayList<String>();;
//	@SuppressWarnings("unused")
//	private String sheetName;
//
//	private String fileKey;
//	private int startRow;
//	private IRowReader rowReader;
//
//	public void setRowReader(IRowReader rowReader, String fileKey, int startRow) {
//		this.rowReader = rowReader;
//		this.fileKey = fileKey;
//		this.startRow = startRow;
//	}
//
//	/**
//	 * excel의 모든 sheet 순회
//	 * 
//	 * @param fileKey
//	 * @throws IOException
//	 */
//	public void process(String fileName) throws IOException {
//
//		try (FileInputStream fis = new FileInputStream(fileName); POIFSFileSystem fs = new POIFSFileSystem(fis)) {
//			MissingRecordAwareHSSFListener listener = new MissingRecordAwareHSSFListener(this);
//			formatListener = new FormatTrackingHSSFListener(listener);
//			HSSFEventFactory factory = new HSSFEventFactory();
//			HSSFRequest request = new HSSFRequest();
//			if (outputFormulaValues) {
//				request.addListenerForAllRecords(formatListener);
//			} else {
//				workbookBuildingListener = new SheetRecordCollectingListener(formatListener);
//				request.addListenerForAllRecords(workbookBuildingListener);
//			}
//			factory.processWorkbookEvents(request, fs);
//			// 데이터 읽기 완료
//		}
//	}
//
//	/**
//	 * excel의 모든 sheet 순회
//	 * 
//	 * @param fileKey
//	 * @throws IOException
//	 */
//	public void process(InputStream is) throws IOException {
//		try (POIFSFileSystem fs = new POIFSFileSystem(is)) {
//			MissingRecordAwareHSSFListener listener = new MissingRecordAwareHSSFListener(this);
//			formatListener = new FormatTrackingHSSFListener(listener);
//			HSSFEventFactory factory = new HSSFEventFactory();
//			HSSFRequest request = new HSSFRequest();
//			if (outputFormulaValues) {
//				request.addListenerForAllRecords(formatListener);
//			} else {
//				workbookBuildingListener = new SheetRecordCollectingListener(formatListener);
//				request.addListenerForAllRecords(workbookBuildingListener);
//			}
//			factory.processWorkbookEvents(request, fs);
//			// 데이터 읽기 완료
//		}
//	}
//
//	/**
//	 * HSSFListener 리스너 메서드, Record 처리
//	 */
//	@SuppressWarnings("unchecked")
//	public void processRecord(Record record) {
//		int thisRow = -1;
//		int thisColumn = -1;
//		String thisStr = null;
//		String value = null;
//		switch (record.getSid()) {
//		case BoundSheetRecord.sid:
//			boundSheetRecords.add(record);
//			break;
//		case BOFRecord.sid:
//			BOFRecord br = (BOFRecord) record;
//			if (br.getType() == BOFRecord.TYPE_WORKSHEET) {
//				// 필요하면 하위 워크북 생성
//				if (workbookBuildingListener != null && stubWorkbook == null) {
//					stubWorkbook = workbookBuildingListener.getStubHSSFWorkbook();
//				}
//
//				sheetIndex++;
//				if (orderedBSRs == null) {
//					orderedBSRs = BoundSheetRecord.orderByBofPosition(boundSheetRecords);
//				}
//				sheetName = orderedBSRs[sheetIndex].getSheetname();
//			}
//			break;
//
//		case SSTRecord.sid:
//			sstRecord = (SSTRecord) record;
//			break;
//
//		case BlankRecord.sid:
//			BlankRecord brec = (BlankRecord) record;
//			thisRow = brec.getRow();
//			thisColumn = brec.getColumn();
//			thisStr = "";
//			rowlist.add(thisColumn, thisStr);
//			break;
//		case BoolErrRecord.sid: // 셀이 불리언 타입
//			BoolErrRecord berec = (BoolErrRecord) record;
//			thisRow = berec.getRow();
//			thisColumn = berec.getColumn();
//			thisStr = berec.getBooleanValue() + "";
//			rowlist.add(thisColumn, thisStr);
//			break;
//
//		case FormulaRecord.sid: // 셀이 수식 타입
//			FormulaRecord frec = (FormulaRecord) record;
//			thisRow = frec.getRow();
//			thisColumn = frec.getColumn();
//			if (outputFormulaValues) {
//				if (Double.isNaN(frec.getValue())) {
//					// Formula result is a string
//					// This is stored in the next record
//					outputNextStringRecord = true;
//					nextRow = frec.getRow();
//					nextColumn = frec.getColumn();
//				} else {
//					thisStr = formatListener.formatNumberDateCell(frec);
//				}
//			} else {
//				thisStr = '"' + HSSFFormulaParser.toFormulaString(stubWorkbook, frec.getParsedExpression()) + '"';
//			}
//			rowlist.add(thisColumn, thisStr);
//			break;
//		case StringRecord.sid:// 셀 수식의 문자열
//			if (outputNextStringRecord) {
//				// String for formula
//				StringRecord srec = (StringRecord) record;
//				thisStr = srec.getString();
//				thisRow = nextRow;
//				thisColumn = nextColumn;
//				outputNextStringRecord = false;
//			}
//			break;
//		case LabelRecord.sid:
//			LabelRecord lrec = (LabelRecord) record;
//			curRow = thisRow = lrec.getRow();
//			thisColumn = lrec.getColumn();
//			value = lrec.getValue().trim();
//			value = value.equals("") ? " " : value;
//			this.rowlist.add(thisColumn, value);
//			break;
//		case LabelSSTRecord.sid: // 셀이 문자열 타입
//			LabelSSTRecord lsrec = (LabelSSTRecord) record;
//			curRow = thisRow = lsrec.getRow();
//			thisColumn = lsrec.getColumn();
//			if (sstRecord == null) {
//				rowlist.add(thisColumn, " ");
//			} else {
//				value = sstRecord.getString(lsrec.getSSTIndex()).toString().trim();
//				value = value.equals("") ? " " : value;
//				rowlist.add(thisColumn, value);
//			}
//			break;
//		case NumberRecord.sid: // 셀이 숫자 타입
//			NumberRecord numrec = (NumberRecord) record;
//			curRow = thisRow = numrec.getRow();
//			thisColumn = numrec.getColumn();
//			value = formatListener.formatNumberDateCell(numrec).trim();
//			value = value.equals("") ? " " : value;
//			// 컨테이너에 열 값 추가
//			rowlist.add(thisColumn, value);
//			break;
//		default:
//			break;
//		}
//
//		// 새 행을 만났을 때의 처리
//		if (thisRow != -1 && thisRow != lastRowNumber) {
//			lastColumnNumber = -1;
//		}
//
//		// 빈 값 처리
//		if (record instanceof MissingCellDummyRecord) {
//			MissingCellDummyRecord mc = (MissingCellDummyRecord) record;
//			curRow = thisRow = mc.getRow();
//			thisColumn = mc.getColumn();
//			rowlist.add(thisColumn, " ");
//		}
//
//		// 행과 열 값 갱신
//		if (thisRow > -1)
//			lastRowNumber = thisRow;
//		if (thisColumn > -1)
//			lastColumnNumber = thisColumn;
//
//		// 행 종료 시 처리
//		if (record instanceof LastCellOfRowDummyRecord) {
//			if (minColumns > 0) {
//				// 열 값 초기화
//				if (lastColumnNumber == -1) {
//					lastColumnNumber = 0;
//				}
//			}
//			lastColumnNumber = -1;
//			// 각 행 종료 시 getRows() 메서드 호출
//
//			rowReader.getRows(sheetIndex, curRow, rowlist, this.fileKey, this.startRow);
//
//			// 컨테이너 비우기
//			rowlist.clear();
//		}
//	}
//
//}
