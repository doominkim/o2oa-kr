//package com.x.calendar.common.excel.writer;
//public class Excel2007WriterImpl extends AbstractExcel2007Writer{	
//	/* 
//	 * 필요에 따라 이 메서드를 재정의 가능, 셀의 소수나 날짜 형식은 정밀도 또는 날짜 형식 변환 문제가 생길 수 있으므로 문자열 삽입 방식 사용 권장
//	 * @see com.excel.ver2.AbstractExcel2007Writer#generate()
//	 */
//	@Override
//	public void generate()throws Exception {
//        //스프레드시트 시작
//        beginSheet();
//        for (int rownum = 0; rownum < 100; rownum++) {
//        	//새 행 삽입
//            insertRow(rownum);
//            //새 셀 생성, 인덱스는 0부터 시작하며 첫째 열을 의미
//            createCell(0, "한국<" + rownum + "!" );
//            createCell(1, 34343.123456789);
//            createCell(2, "23.67%" );
//            createCell(3, "12:12:23" );
//            createCell(4, "2010-10-11 12:12:23" );
//            createCell(5, "true" );
//            createCell(6, "false" );
//          
//            //행 종료
//            endRow();
//        }
//        //스프레드시트 종료
//        endSheet();
//	}
//
//}
