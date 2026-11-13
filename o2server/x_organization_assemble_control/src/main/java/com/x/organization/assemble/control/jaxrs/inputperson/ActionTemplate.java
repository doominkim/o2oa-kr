package com.x.organization.assemble.control.jaxrs.inputperson;

import java.io.ByteArrayOutputStream;
import java.util.stream.IntStream;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoFile;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public class ActionTemplate extends BaseAction {

	private static final Logger logger = LoggerFactory.getLogger(ActionTemplate.class);

	private static final String name = "input_person_template.xlsx";

	protected ActionResult<Wo> execute(EffectivePerson effectivePerson) throws Exception {
		try (XSSFWorkbook workbook = new XSSFWorkbook(); ByteArrayOutputStream os = new ByteArrayOutputStream()) {
			ActionResult<Wo> result = new ActionResult<>();

			this.templateRemark(workbook);
			this.templateUnit(workbook);
			this.templatePerson(workbook);
			this.templateIdentity(workbook);
			this.templateDuty(workbook);
			this.templateGroup(workbook);
			this.templateRole(workbook);

			workbook.write(os);
			Wo wo = new Wo(os.toByteArray(), this.contentType(true, name), this.contentDisposition(true, name));
			result.setData(wo);
			return result;
		}
	}

	private void templateRemark(XSSFWorkbook workbook) {
		XSSFSheet sheet = workbook.createSheet("주의 사항");
		Row row = sheet.createRow(0);
		// 먼저 헤더 생성
		row = sheet.createRow(2);
		row.createCell(0).setCellValue("주의 사항:");

		row = sheet.createRow(4);
		row.createCell(0).setCellValue("1. 표 안의 셀을 병합(분할)하지 마세요. 각 열의 순서를 바꾸거나 삭제하면 데이터가 꼬일 수 있습니다.");

		row = sheet.createRow(6);
		row.createCell(0).setCellValue("2. * 표시는 필수 항목입니다.");

		row = sheet.createRow(8);
		row.createCell(0).setCellValue("3. 형식에 특별한 요구 사항이 있으면 열 이름의 메모를 참고하세요.");

		row = sheet.createRow(10);
		row.createCell(0).setCellValue("4. 표의 예시 데이터는 참고용이므로 실제로 가져올 때는 삭제하세요.");

		row = sheet.createRow(12);
		row.createCell(0).setCellValue("5. 각 시트의 순서를 바꾸지 말고 이 시트를 삭제하지 마세요.");

		CellStyle cellStyle = workbook.createCellStyle();
		cellStyle.setWrapText(true);
		IntStream.rangeClosed(0, 6).forEach(i -> sheet.setDefaultColumnStyle(i, cellStyle));
	}

	private void templateUnit(XSSFWorkbook workbook) {
		XSSFSheet sheet = workbook.createSheet("조직 정보");
		sheet.setDefaultColumnWidth(25);
		Row row = sheet.createRow(0);
		Cell cell = row.createCell(0);
		cell.setCellValue("조직 이름 *");
		cell = row.createCell(1);
		cell.setCellValue("고유 코드 *");
		cell = row.createCell(2);
		cell.setCellValue("조직 레벨 이름 *");
		cell = row.createCell(3);
		cell.setCellValue("상위 조직 코드");
		cell = row.createCell(4);
		cell.setCellValue("설명");
		cell = row.createCell(5);
		cell.setCellValue("정렬 순서");
		CellStyle cellStyle = workbook.createCellStyle();
		cellStyle.setWrapText(true);
		IntStream.rangeClosed(0, 6).forEach(i -> {
			sheet.setDefaultColumnStyle(i, cellStyle);
		});
	}

	private void templatePerson(XSSFWorkbook workbook) {
		XSSFSheet sheet = workbook.createSheet("사용자 기본 정보");
		sheet.setDefaultColumnWidth(25);
		Row row = sheet.createRow(0);
		Cell cell = row.createCell(0);
		cell.setCellValue("사용자 이름 *");
		cell = row.createCell(1);
		cell.setCellValue("고유 코드 *");
		cell = row.createCell(2);
		cell.setCellValue("휴대전화 번호 *");
		cell = row.createCell(3);
		cell.setCellValue("사번");
		cell = row.createCell(4);
		cell.setCellValue("사무실 전화");
		cell = row.createCell(5);
		cell.setCellValue("성별");
		cell = row.createCell(6);
		cell.setCellValue("이메일");
		CellStyle cellStyle = workbook.createCellStyle();
		cellStyle.setWrapText(true);
		IntStream.rangeClosed(0, 6).forEach(i -> sheet.setDefaultColumnStyle(i, cellStyle));
	}

	private void templateIdentity(XSSFWorkbook workbook) {
		XSSFSheet sheet = workbook.createSheet("사용자 소속 정보");
		sheet.setDefaultColumnWidth(25);
		Row row = sheet.createRow(0);
		Cell cell = row.createCell(0);
		cell.setCellValue("사용자 고유 코드 *");
		cell = row.createCell(1);
		cell.setCellValue("조직 고유 코드 *");
		cell = row.createCell(2);
		cell.setCellValue("소속 코드");
		cell = row.createCell(3);
		cell.setCellValue("주/겸직");
		CellStyle cellStyle = workbook.createCellStyle();
		cellStyle.setWrapText(true);
		IntStream.rangeClosed(0, 6).forEach(i -> sheet.setDefaultColumnStyle(i, cellStyle));
	}

	private void templateDuty(XSSFWorkbook workbook) {
		XSSFSheet sheet = workbook.createSheet("직무 정보");
		sheet.setDefaultColumnWidth(45);
		Row row = sheet.createRow(0);
		Cell cell = row.createCell(0);
		cell.setCellValue("직무 이름 *");
		cell = row.createCell(1);
		cell.setCellValue("직무 소속 조직 고유 코드 *");
		cell = row.createCell(2);
		cell.setCellValue("직무 코드");
		cell = row.createCell(3);
		cell.setCellValue("직무 설명");
		cell = row.createCell(4);
		cell.setCellValue("직무 구성원 고유 코드");
		cell = row.createCell(5);
		cell.setCellValue("직무 구성원 소속 조직 고유 코드");
		CellStyle cellStyle = workbook.createCellStyle();
		cellStyle.setWrapText(true);
		IntStream.rangeClosed(0, 6).forEach(i -> sheet.setDefaultColumnStyle(i, cellStyle));
	}

	private void templateGroup(XSSFWorkbook workbook) {
		XSSFSheet sheet = workbook.createSheet("그룹 정보");
		sheet.setDefaultColumnWidth(25);
		Row row = sheet.createRow(0);
		Cell cell = row.createCell(0);
		cell.setCellValue("그룹 이름 *");
		cell = row.createCell(1);
		cell.setCellValue("그룹 코드 *");
		cell = row.createCell(2);
		cell.setCellValue("사용자 고유 코드");
		cell = row.createCell(3);
		cell.setCellValue("소속 고유 코드");
		cell = row.createCell(4);
		cell.setCellValue("조직 고유 코드");
		cell = row.createCell(5);
		cell.setCellValue("하위 그룹 코드");
		cell = row.createCell(6);
		cell.setCellValue("설명");
		CellStyle cellStyle = workbook.createCellStyle();
		cellStyle.setWrapText(true);
		IntStream.rangeClosed(0, 7).forEach(i -> sheet.setDefaultColumnStyle(i, cellStyle));
	}

	private void templateRole(XSSFWorkbook workbook) {
		XSSFSheet sheet = workbook.createSheet("역할 정보");
		sheet.setDefaultColumnWidth(25);
		Row row = sheet.createRow(0);
		Cell cell = row.createCell(0);
		cell.setCellValue("역할 이름 *");
		cell = row.createCell(1);
		cell.setCellValue("역할 코드 *");
		cell = row.createCell(2);
		cell.setCellValue("사용자 고유 코드");
		cell = row.createCell(3);
		cell.setCellValue("그룹 고유 코드");
		cell = row.createCell(4);
		cell.setCellValue("설명");
		CellStyle cellStyle = workbook.createCellStyle();
		cellStyle.setWrapText(true);
		IntStream.rangeClosed(0, 5).forEach(i -> sheet.setDefaultColumnStyle(i, cellStyle));
	}

	public static class Wo extends WoFile {

		public Wo(byte[] bytes, String contentType, String contentDisposition) {
			super(bytes, contentType, contentDisposition);
		}

	}

}
