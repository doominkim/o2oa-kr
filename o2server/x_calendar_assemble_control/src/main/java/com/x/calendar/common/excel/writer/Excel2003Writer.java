//package com.x.calendar.common.excel.writer;
//
//import java.io.FileOutputStream;
//import java.io.IOException;
//
//import org.apache.poi.hssf.usermodel.HSSFWorkbook;
//import org.apache.poi.ss.usermodel.Cell;
//import org.apache.poi.ss.usermodel.Row;
//import org.apache.poi.ss.usermodel.Sheet;
//import org.apache.poi.ss.usermodel.Workbook;
//
//public class Excel2003Writer {
//
//	/**
//	 * @param args
//	 */
//	public static void main(String[] args) {
//		try{	
//			//System.out.println( "excel2003 쓰기 시작...." );
//			writeExcel( "tes2003.xls" );
//			//System.out.println( "xcel2003 쓰기 완료" );
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
//	
//	
//	/**
//	 * excel을 쓰고 내용 채우기, sheet 하나에 65536행 미만만 가능하며 초과 시 예외 발생, 쓰기 시 AbstractExcel2007Writer 사용 권장
//	 * @param fileName
//	 * @throws IOException
//	 */
//	@SuppressWarnings("resource")
//	public static void writeExcel(String fileName) throws IOException{
//			
//			// excel2003 객체 생성
//			Workbook wb = new HSSFWorkbook();
//			
//			// 파일 저장 경로와 파일명 설정
//		    FileOutputStream fileOut = new FileOutputStream(fileName);
//		    // 새 시트 생성
//		    Sheet sheet = wb.createSheet( "newsheet" );
//		    // 새 행 생성
//		    for(int i=0;i<20000;i++){
//			    Row row = sheet.createRow(i);
//			    // 셀 생성
//			    Cell cell = row.createCell(0);
//			    // 셀 값 설정
//			    cell.setCellValue(1);
//			    row.createCell(1).setCellValue(1+i);
//			    row.createCell(2).setCellValue(true);
//			    row.createCell(3).setCellValue(0.43d);
//			    row.createCell(4).setCellValue('d');
//			    row.createCell(5).setCellValue( "" );
//			    row.createCell(6).setCellValue( "일곱째 열"+i);
//			    row.createCell(7).setCellValue( "여덟째 열"+i);
//		    }
//		    wb.write(fileOut);
//		    fileOut.close();
//	}
//
//
//}
