//package com.x.calendar.common.excel.reader;
//
//import java.io.InputStream;
//import java.math.BigDecimal;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.Iterator;
//import java.util.List;
//
//import org.apache.poi.openxml4j.opc.OPCPackage;
//import org.apache.poi.ss.usermodel.DateUtil;
//import org.apache.poi.xssf.eventusermodel.XSSFReader;
//import org.apache.poi.xssf.model.SharedStringsTable;
//import org.apache.poi.xssf.usermodel.XSSFRichTextString;
//import org.xml.sax.Attributes;
//import org.xml.sax.InputSource;
//import org.xml.sax.SAXException;
//import org.xml.sax.XMLReader;
//import org.xml.sax.helpers.DefaultHandler;
//import org.xml.sax.helpers.XMLReaderFactory;
//
///**
// * 추상 Excel2007 리더, excel2007의 하위 데이터 구조는 xml 파일이며 SAX 이벤트 기반 방식으로 파싱
// * xml, DefaultHandler를 상속해야 하며 파일 내용을 만나면 이벤트가 트리거됨, 이 방식은 메모리 소모를 크게 줄일 수 있어 대용량 파일에 특히 적합.
// *
// */
//public class Excel2007Reader extends DefaultHandler {
//	// 공유 문자열 테이블
//	private SharedStringsTable sst;
//	// 이전 내용
//	private String lastContents;
//
//	private boolean nextIsString;
//
//	private int sheetIndex = -1;
//
//	private List<String> rowlist = new ArrayList<String>();
//	// 현재 행
//	private int curRow = 0;
//	// 현재 열
//	private int curCol = 0;
//	// 날짜 플래그
//	private boolean dateFlag;
//	// 숫자 플래그
//	private boolean numberFlag;
//
//	private boolean isTElement;
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
//	 * 스프레드시트 하나만 순회, sheetId는 순회할 sheet 인덱스이며 1부터 시작, 1-3
//	 * 
//	 * @param filename
//	 * @param sheetId
//	 * @throws Exception
//	 */
//	public void processOneSheet(String filename, int sheetId) throws Exception {
//		OPCPackage pkg = OPCPackage.open(filename);
//		XSSFReader r = new XSSFReader(pkg);
//		SharedStringsTable sst = r.getSharedStringsTable();
//		XMLReader parser = fetchSheetParser(sst);
//
//		// rId# 또는 rSheet#으로 sheet 찾기
//		InputStream sheet2 = r.getSheet("rId" + sheetId);
//		sheetIndex++;
//		InputSource sheetSource = new InputSource(sheet2);
//		parser.parse(sheetSource);
//		sheet2.close();
//	}
//
//	/**
//	 * 워크북의 모든 스프레드시트 순회
//	 * 
//	 * @param filename
//	 * @param fileKey
//	 * @throws Exception
//	 */
//	public void process(String filename) throws Exception {
//		OPCPackage pkg = OPCPackage.open(filename);
//		XSSFReader r = new XSSFReader(pkg);
//		SharedStringsTable sst = r.getSharedStringsTable();
//		XMLReader parser = fetchSheetParser(sst);
//		Iterator<InputStream> sheets = r.getSheetsData();
//		while (sheets.hasNext()) {
//			curRow = 0;
//			sheetIndex++;
//			InputStream sheet = sheets.next();
//			InputSource sheetSource = new InputSource(sheet);
//			parser.parse(sheetSource);
//			sheet.close();
//		}
//		// 데이터 읽기 완료
//	}
//
//	/**
//	 * 워크북의 모든 스프레드시트 순회
//	 * 
//	 * @param is
//	 * @param fileKey
//	 * @throws Exception
//	 */
//	public void process(InputStream is) throws Exception {
//		OPCPackage pkg = OPCPackage.open(is);
//		XSSFReader r = new XSSFReader(pkg);
//		SharedStringsTable sst = r.getSharedStringsTable();
//		XMLReader parser = fetchSheetParser(sst);
//		Iterator<InputStream> sheets = r.getSheetsData();
//		while (sheets.hasNext()) {
//			curRow = 0;
//			sheetIndex++;
//			InputStream sheet = sheets.next();
//			InputSource sheetSource = new InputSource(sheet);
//			parser.parse(sheetSource);
//			sheet.close();
//		}
//		// 데이터 읽기 완료
//	}
//
//	public XMLReader fetchSheetParser(SharedStringsTable sst) throws SAXException {
//		XMLReader parser = XMLReaderFactory.createXMLReader("org.apache.xerces.parsers.SAXParser");
//		this.sst = sst;
//		parser.setContentHandler(this);
//		return parser;
//	}
//
//	public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException {
//
//		// c => 셀
//		if ("c".equals(name)) {
//			// 다음 요소가 SST 인덱스이면 nextIsString을 true로 표시
//			String cellType = attributes.getValue("t");
//			if ("s".equals(cellType)) {
//				nextIsString = true;
//			} else {
//				nextIsString = false;
//			}
//			// 날짜 형식
//			String cellDateType = attributes.getValue("s");
//			if ("1".equals(cellDateType)) {
//				dateFlag = true;
//			} else {
//				dateFlag = false;
//			}
//			String cellNumberType = attributes.getValue("s");
//			if ("2".equals(cellNumberType)) {
//				numberFlag = true;
//			} else {
//				numberFlag = false;
//			}
//
//		}
//		// 요소가 t일 때
//		if ("t".equals(name)) {
//			isTElement = true;
//		} else {
//			isTElement = false;
//		}
//
//		// 초기화
//		lastContents = "";
//	}
//
//	public void endElement(String uri, String localName, String name) throws SAXException {
//
//		// SST 인덱스 값으로 셀에 실제 저장할 문자열 구하기
//		// 이때 characters() 메서드가 여러 번 호출될 수 있음
//		if (nextIsString) {
//			try {
//				int idx = Integer.parseInt(lastContents);
//				lastContents = new XSSFRichTextString(sst.getEntryAt(idx)).toString();
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
//		// t 요소도 문자열 포함
//		if (isTElement) {
//			String value = lastContents.trim();
//			rowlist.add(curCol, value);
//			curCol++;
//			isTElement = false;
//			// v => 셀 값, 셀이 문자열이면 v 태그 값은 SST에서 해당 문자열의 인덱스
//			// 셀 내용을 rowlist에 추가, 그 전에 문자열 앞뒤 공백 제거
//		} else if ("v".equals(name)) {
//			String value = lastContents.trim();
//			value = value.equals("") ? " " : value;
//			// 날짜 형식 처리
//			if (dateFlag) {
//				try {
//					Date date = DateUtil.getJavaDate(Double.valueOf(value));
//				    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
//					value = dateFormat.format(date);
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//			// 숫자 타입 처리
//			if (numberFlag) {
//				try {
//					BigDecimal bd = new BigDecimal(value);
//					value = bd.setScale(3, BigDecimal.ROUND_UP).toString();
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//			rowlist.add(curCol, value);
//			curCol++;
//		} else {
//			// 태그 이름이 row이면 행 끝에 도달한 것이므로 optRows() 메서드 호출
//			if (name.equals("row")) {
//
//				rowReader.getRows(sheetIndex, curRow, rowlist, this.fileKey, this.startRow);
//				rowlist.clear();
//				curRow++;
//				curCol = 0;
//			}
//		}
//
//	}
//
//	public void characters(char[] ch, int start, int length) throws SAXException {
//		// 셀 내용 값 구하기
//		lastContents += new String(ch, start, length);
//	}
//}
