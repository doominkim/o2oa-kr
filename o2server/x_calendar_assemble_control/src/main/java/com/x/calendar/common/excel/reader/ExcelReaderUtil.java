//package com.x.calendar.common.excel.reader;
//
//import java.io.InputStream;
//
//public class ExcelReaderUtil {
//	
//	//excel2003 확장자
//	public static final String EXCEL03_EXTENSION = ".xls";
//	//excel2007 확장자
//	public static final String EXCEL07_EXTENSION = ".xlsx";
//	
//	/**
//	 * Excel 파일 읽기, 03 또는 07 버전일 수 있음
//	 * @param excel03
//	 * @param excel07
//	 * @param fileName
//	 * @throws Exception 
//	 */
//	public static void readExcel(IRowReader reader, String fileName, String fileKey, int startRow ) throws Exception{
//		// excel2003 파일 처리
//		if (fileName.endsWith(EXCEL03_EXTENSION)){
//			Excel2003Reader excel03 = new Excel2003Reader();
//			excel03.setRowReader(reader, fileKey, startRow);
//			excel03.process(fileName);
//		// excel2007 파일 처리
//		} else if (fileName.endsWith(EXCEL07_EXTENSION)){
//			Excel2007Reader excel07 = new Excel2007Reader();
//			excel07.setRowReader(reader, fileKey, startRow);
//			excel07.process(fileName);
//		} else {
//			throw new  Exception( "파일 형식 오류입니다. fileName의 확장자는 xls 또는 xlsx만 가능합니다." );
//		}
//	}
//	
//	/**
//	 * Excel 파일 읽기, 03 또는 07 버전일 수 있음
//	 * @param excel03
//	 * @param excel07
//	 * @param fileName
//	 * @throws Exception 
//	 */
//	public static void readExcel(IRowReader reader,String fileName, InputStream inputStream, String fileKey, int startRow ) throws Exception{
//		// excel2003 파일 처리
//		if (fileName.endsWith( EXCEL03_EXTENSION )){
//			Excel2003Reader excel03 = new Excel2003Reader();
//			excel03.setRowReader(reader, fileKey, startRow);
//			excel03.process(inputStream);
//		// excel2007 파일 처리
//		} else if (fileName.endsWith(EXCEL07_EXTENSION)){
//			Excel2007Reader excel07 = new Excel2007Reader();
//			excel07.setRowReader(reader, fileKey, startRow);
//			excel07.process(inputStream);
//		} else {
//			throw new  Exception( "파일 형식 오류입니다. fileName의 확장자는 xls 또는 xlsx만 가능합니다." );
//		}
//	}
//}