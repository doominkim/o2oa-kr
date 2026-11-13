package com.x.general.assemble.control.jaxrs.excel;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.general.assemble.control.Business;
import com.x.general.assemble.control.ThisApplication;
import com.x.general.core.entity.GeneralFile;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.WorkbookUtil;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.util.List;


class ActionExcelSheetExport extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionExcelSheetExport.class);
	private static final String EXCEL_EXTENSION = ".xlsx";

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String excelName, JsonElement jsonElement)
			throws Exception {

		LOGGER.debug("jsonElement:{}.", () -> jsonElement);

		ActionResult<Wo> result = new ActionResult<>();
		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);

		if (StringUtils.isEmpty(excelName)) {
			excelName = "제목 없음" + EXCEL_EXTENSION;
		}
		if (!excelName.toLowerCase().endsWith(EXCEL_EXTENSION)) {
			excelName = excelName + EXCEL_EXTENSION;
		}
		if (ListTools.isEmpty(wi.getSheetList())) {
			throw new Exception("sheetList 내용이 비어 있습니다");
		}
		byte[] bytes;
		try (XSSFWorkbook workbook = new XSSFWorkbook(); ByteArrayOutputStream os = new ByteArrayOutputStream()) {

			// 각 WiSheet를 순회하며 대응하는 sheet 생성
			for (WiSheet sheetItem : wi.getSheetList()) {
				String safeSheetName = WorkbookUtil.createSafeSheetName(
						StringUtils.defaultIfEmpty(sheetItem.getSheetName(), "Sheet")
				);
				XSSFSheet sheet = workbook.createSheet(safeSheetName); // sheet 생성

				List<List<String>> dataList = sheetItem.getDataList();
				if (ListTools.isEmpty(dataList)) {
					continue; // 이 sheet에 데이터가 없어도 빈 sheet 생성
				}

				int rowNum = 0;
				for (List<String> rowList : dataList) {
					Row row = sheet.createRow(rowNum++);
					int cellNum = 0;
					// 각 행의 셀 처리
					for (String value : rowList) {
						Cell cell = row.createCell(cellNum++);
						cell.setCellValue(StringUtils.defaultString(value)); // null 방지
					}
				}
			}

			workbook.write(os);
			bytes = os.toByteArray();
		}

		Business business;
		GeneralFile generalFile;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);

			StorageMapping gfMapping = ThisApplication.context().storageMappings().random(GeneralFile.class);
			generalFile = new GeneralFile(gfMapping.getName(), excelName,
					effectivePerson.getDistinguishedName());
			generalFile.saveContent(gfMapping, bytes, excelName);
			business.entityManagerContainer().beginTransaction(GeneralFile.class);
			business.entityManagerContainer().persist(generalFile, CheckPersistType.all);
			business.entityManagerContainer().commit();
		}


		Wo wo = new Wo();
		wo.setId(generalFile.getId());
		result.setData(wo);
		return result;
	}

	public static class Wo extends WoId {

		private static final long serialVersionUID = 1123515948467557694L;

	}

	public static class Wi extends GsonPropertyObject {

		private static final long serialVersionUID = -51802802412712709L;

		@FieldDescribe("여러 sheet의 내용, 예: { \"sheetName\": \"Sheet1\", \"dataList\": [[\"이름\", \"성별\", \"나이\"],[\"\", \"\", \"\"],[\"김영희\", \"여\", \"22\"]]}")

		public List<WiSheet> sheetList;

		public List<WiSheet> getSheetList() {
			return sheetList;
		}

		public void setSheetList(List<WiSheet> sheetList) {
			this.sheetList = sheetList;
		}

	}

	public static class WiSheet extends GsonPropertyObject {

		private static final long serialVersionUID = -51802802412712709L;

		@FieldDescribe("sheet 이름")
		public String sheetName;

		@FieldDescribe("excel로 변환할 내용, 예: ['이름','성별'],['홍길동','남']")
		public List<List<String>> dataList;

		public String getSheetName() {
			return sheetName;
		}

		public void setSheetName(String sheetName) {
			this.sheetName = sheetName;
		}

		public List<List<String>> getDataList() {
			return dataList;
		}

		public void setDataList(List<List<String>> dataList) {
			this.dataList = dataList;
		}

	}

}
