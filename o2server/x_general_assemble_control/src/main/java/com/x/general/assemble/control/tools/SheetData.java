package com.x.general.assemble.control.tools;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import java.util.List;

/**
 * @author chengjian
 * @date 2026/08/28 13:48
 **/
public class SheetData extends GsonPropertyObject {
    @FieldDescribe("sheet 이름")
    private String sheetName;
    @FieldDescribe("시작 행(0행부터 시작)")
    private Integer startRow;
    @FieldDescribe("데이터 목록")
    private List<List<String>> dataList;

    public String getSheetName() {
        return sheetName;
    }

    public void setSheetName(String sheetName) {
        this.sheetName = sheetName;
    }

    public Integer getStartRow() {
        return startRow;
    }

    public void setStartRow(Integer startRow) {
        this.startRow = startRow;
    }

    public List<List<String>> getDataList() {
        return dataList;
    }

    public void setDataList(List<List<String>> dataList) {
        this.dataList = dataList;
    }
}
