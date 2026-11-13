//package com.x.calendar.common.excel.writer;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileOutputStream;
//import java.io.FileWriter;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.OutputStream;
//import java.io.Writer;
//import java.util.Calendar;
//import java.util.Enumeration;
//import java.util.zip.ZipEntry;
//import java.util.zip.ZipFile;
//import java.util.zip.ZipOutputStream;
//
//import org.apache.poi.ss.usermodel.DateUtil;
//import org.apache.poi.ss.util.CellReference;
//import org.apache.poi.xssf.usermodel.XSSFSheet;
//import org.apache.poi.xssf.usermodel.XSSFWorkbook;
//
///**
// * 추상 excel2007 라이터, 먼저 .xlsx 템플릿을 만든 뒤 템플릿의 sheet.xml을 수정하는 방식으로 .xlsx 파일 작성, 메모리가 많이 필요하지 않음
// *
// */
//public abstract class AbstractExcel2007Writer {
//
//	private SpreadsheetWriter sw;
//
//	/**
//	 * 스프레드시트 쓰기 주요 흐름
//	 * 
//	 * @param fileName
//	 * @throws Exception
//	 */
//	@SuppressWarnings("resource")
//	public void process(String fileName) throws Exception {
//		// 워크북 및 스프레드시트 객체 생성
//		XSSFWorkbook wb = new XSSFWorkbook();
//		XSSFSheet sheet = wb.createSheet("sheet1");
//		// 스프레드시트 데이터를 담는 xml 파일명 예: /xl/worksheets/sheet1.xml
//		String sheetRef = sheet.getPackagePart().getPartName().getName();
//
//		// 템플릿 저장
//		try (FileOutputStream os = new FileOutputStream("template.xlsx")) {
//			wb.write(os);
//		}
//
//		// xml 파일 생성
//		File tmp = File.createTempFile("sheet", ".xml");
//		try (Writer fw = new FileWriter(tmp)) {
//			sw = new SpreadsheetWriter(fw);
//			generate();
//		}
//
//		// 생성된 데이터로 템플릿 교체
//		File templateFile = new File("template.xlsx");
//		try (FileOutputStream out = new FileOutputStream(fileName)){
//			substitute(templateFile, tmp, sheetRef.substring(1), out);
//		}
//		// 임시 템플릿 파일 삭제
//		if (templateFile.isFile() && templateFile.exists()) {
//			templateFile.delete();
//		}
//	}
//
//	/**
//	 * 스프레드시트 쓰기 주요 흐름
//	 * 
//	 * @param fileName
//	 * @throws Exception
//	 */
//	@SuppressWarnings("resource")
//	public void process(OutputStream out) throws Exception {
//		// 워크북 및 스프레드시트 객체 생성
//		XSSFWorkbook wb = new XSSFWorkbook();
//		XSSFSheet sheet = wb.createSheet("sheet1");
//		// 스프레드시트 데이터를 담는 xml 파일명 예: /xl/worksheets/sheet1.xml
//		String sheetRef = sheet.getPackagePart().getPartName().getName();
//
//		// 템플릿 저장
//		FileOutputStream os = new FileOutputStream("template.xlsx");
//		wb.write(os);
//		os.close();
//
//		// xml 파일 생성
//		File tmp = File.createTempFile("sheet", ".xml");
//		Writer fw = new FileWriter(tmp);
//		sw = new SpreadsheetWriter(fw);
//		generate();
//		fw.close();
//
//		// 생성된 데이터로 템플릿 교체
//		File templateFile = new File("template.xlsx");
//		substitute(templateFile, tmp, sheetRef.substring(1), out);
//		out.close();
//		// 파일 삭제 전 가비지 컬렉터 호출, 그렇지 않으면 템플릿 파일 삭제 불가
//		System.gc();
//		// 임시 템플릿 파일 삭제
//		if (templateFile.isFile() && templateFile.exists()) {
//			templateFile.delete();
//		}
//	}
//
//	/**
//	 * 클래스 사용자는 이 메서드로 쓰기 작업을 수행해야 함
//	 * 
//	 * @throws Exception
//	 */
//	public abstract void generate() throws Exception;
//
//	public void beginSheet() throws IOException {
//		sw.beginSheet();
//	}
//
//	public void insertRow(int rowNum) throws IOException {
//		sw.insertRow(rowNum);
//	}
//
//	public void createCell(int columnIndex, String value) throws IOException {
//		sw.createCell(columnIndex, value, -1);
//	}
//
//	public void createCell(int columnIndex, double value) throws IOException {
//		sw.createCell(columnIndex, value, -1);
//	}
//
//	public void endRow() throws IOException {
//		sw.endRow();
//	}
//
//	public void endSheet() throws IOException {
//		sw.endSheet();
//	}
//
//	/**
//	 *
//	 * @param zipfile the template file
//	 * @param tmpfile the XML file with the sheet data
//	 * @param entry   the name of the sheet entry to substitute, e.g.
//	 *                xl/worksheets/sheet1.xml
//	 * @param out     the stream to write the result to
//	 */
//	@SuppressWarnings("resource")
//	private static void substitute(File zipfile, File tmpfile, String entry, OutputStream out) throws IOException {
//		try (ZipFile zip = new ZipFile(zipfile); ZipOutputStream zos = new ZipOutputStream(out)) {
//			@SuppressWarnings("unchecked")
//			Enumeration<ZipEntry> en = (Enumeration<ZipEntry>) zip.entries();
//			while (en.hasMoreElements()) {
//				ZipEntry ze = en.nextElement();
//				if (!ze.getName().equals(entry)) {
//					zos.putNextEntry(new ZipEntry(ze.getName()));
//					InputStream is = zip.getInputStream(ze);
//					copyStream(is, zos);
//					is.close();
//				}
//			}
//			zos.putNextEntry(new ZipEntry(entry));
//			try (InputStream is = new FileInputStream(tmpfile)) {
//				copyStream(is, zos);
//			}
//		}
//	}
//
//	private static void copyStream(InputStream in, OutputStream out) throws IOException {
//		byte[] chunk = new byte[1024];
//		int count;
//		while ((count = in.read(chunk)) >= 0) {
//			out.write(chunk, 0, count);
//		}
//	}
//
//	/**
//	 * 라이터에 스프레드시트 쓰기
//	 * 
//	 */
//	public static class SpreadsheetWriter {
//		private final Writer _out;
//		private int _rownum;
//		private static String LINE_SEPARATOR = System.getProperty("line.separator");
//
//		public SpreadsheetWriter(Writer out) {
//			_out = out;
//		}
//
//		public void beginSheet() throws IOException {
//			_out.write("<?xml version=\"1.0\" encoding=\"GB2312\"?>"
//					+ "<worksheet xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\">");
//			_out.write("<sheetData>" + LINE_SEPARATOR);
//		}
//
//		public void endSheet() throws IOException {
//			_out.write("</sheetData>");
//			_out.write("</worksheet>");
//		}
//
//		/**
//		 * 새 행 삽입
//		 *
//		 * @param rownum 0부터 시작
//		 */
//		public void insertRow(int rownum) throws IOException {
//			_out.write("<row r=\"" + (rownum + 1) + "\">" + LINE_SEPARATOR);
//			this._rownum = rownum;
//		}
//
//		/**
//		 * 행 종료 표시 삽입
//		 */
//		public void endRow() throws IOException {
//			_out.write("</row>" + LINE_SEPARATOR);
//		}
//
//		/**
//		 * 새 열 삽입
//		 * 
//		 * @param columnIndex
//		 * @param value
//		 * @param styleIndex
//		 * @throws IOException
//		 */
//		public void createCell(int columnIndex, String value, int styleIndex) throws IOException {
//			String ref = new CellReference(_rownum, columnIndex).formatAsString();
//			_out.write("<c r=\"" + ref + "\" t=\"inlineStr\"");
//			if (styleIndex != -1)
//				_out.write(" s=\"" + styleIndex + "\"");
//			_out.write(">");
//			_out.write("<is><t>" + XMLEncoder.encode(value) + "</t></is>");
//			_out.write("</c>");
//		}
//
//		public void createCell(int columnIndex, String value) throws IOException {
//			createCell(columnIndex, value, -1);
//		}
//
//		public void createCell(int columnIndex, double value, int styleIndex) throws IOException {
//			String ref = new CellReference(_rownum, columnIndex).formatAsString();
//			_out.write("<c r=\"" + ref + "\" t=\"n\"");
//			if (styleIndex != -1)
//				_out.write(" s=\"" + styleIndex + "\"");
//			_out.write(">");
//			_out.write("<v>" + value + "</v>");
//			_out.write("</c>");
//		}
//
//		public void createCell(int columnIndex, double value) throws IOException {
//			createCell(columnIndex, value, -1);
//		}
//
//		public void createCell(int columnIndex, Calendar value, int styleIndex) throws IOException {
//			createCell(columnIndex, DateUtil.getExcelDate(value, false), styleIndex);
//		}
//	}
//}