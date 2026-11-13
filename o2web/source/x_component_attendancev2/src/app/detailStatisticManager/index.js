import { component as content } from "@o2oa/oovm";
import { lp, o2 } from "@o2oa/component";
import ExcelJS from "exceljs";
import { formatDate, isEmpty , convertMinutesToHoursAndMinutes, showLoading, hideLoading, fieldWorkFormat} from "../../utils/common";
import { detailAction } from "../../utils/actions";
import oOrgPersonSelector from "../../components/o-org-person-selector";
import oDatePicker from "../../components/o-date-picker";
import template from "./template.html";

export default content({
  template,
  components: { oOrgPersonSelector, oDatePicker },
  autoUpdate: true,
  bind() {
    return {
      lp,
      // 검색 양식
      form: {
        filterList: [],
        startDate: '',
        endDate: '',
        recursive: true
      },
      units: [], // 조직 선택 범위 제어
      filterList: [],
      statisticList: [],
      tableHeaderList:[] // detail 

    };
  },
  beforeRender() {
    const today = new Date();
    // 이번 주 첫째 날 날짜 계산
    const firstDayOfWeek = new Date(today.setDate(today.getDate() - today.getDay() + 1));
    // 이번 주 마지막 날 날짜 계산
    const lastDayOfWeek = new Date(today.setDate(firstDayOfWeek.getDate() + 6));
    this.bind.form.startDate = formatDate(firstDayOfWeek);
    this.bind.form.endDate = formatDate(lastDayOfWeek);
    // 현재 사용자의 선택 범위
    if (content.myDutyList && content.myDutyList.length > 0) {
      let units = [];
      for (let i = 0; i < content.myDutyList.length; i++) {
        const duty = content.myDutyList[i];
        if (duty.woUnit && duty.woUnit.distinguishedName) {
          units.push(duty.woUnit.distinguishedName);
        }
      }
      this.bind.units = units;
    }
  },
  // yyyy-MM-dd 
  _toDate(dateString) {
    var dateParts = dateString.split("-"); // 문자열을 연, 월, 일로 분리
    var year = parseInt(dateParts[0], 10); // 연도 부분을 정수로 변환
    var month = parseInt(dateParts[1], 10) - 1; // 월 부분을 정수로 변환(월은 0부터 시작)
    var day = parseInt(dateParts[2], 10); // 일 부분을 정수로 변환
    return new Date(year, month, day); // Date 객체 생성
  },
  // 간격 일수
  _dateDiffInDays(date1, date2) {
    const timeDiff = date2.getTime() - date1.getTime();
    const daysDiff = Math.floor(timeDiff / (1000 * 3600 * 24));
    return Math.abs(daysDiff); // 날짜 순서를 고려해야 하면 Math.abs 제거
  },
  // 날짜 목록
  _dateRangeToStringList(startDate, endDate) {
    const dateList = [];
    let currentDate = new Date(startDate);
    while (currentDate <= endDate) {
      dateList.push(formatDate(currentDate));
      currentDate.setDate(currentDate.getDate() + 1); // 하루 추가
    }
    return dateList;
  },
  validateForm() {
    if (this.bind.filterList.length < 1) {
      o2.api.page.notice(lp.detailStatisticList.filterEmptyPlaceholder, 'error');
      return false;
    }
    if (isEmpty(this.bind.form.startDate)) {
      o2.api.page.notice(lp.detailStatisticList.startDateEmptyPlaceholder, 'error');
      return false;
    }
    if (isEmpty(this.bind.form.endDate)) {
      o2.api.page.notice(lp.detailStatisticList.endDateEmptyPlaceholder, 'error');
      return false;
    }
    const start = this._toDate(this.bind.form.startDate);
    const end = this._toDate(this.bind.form.endDate);
    if (end < start) {
      o2.api.page.notice(lp.detailStatisticList.endDateCannotSmaller, 'error');
      return false;
    }
    if (this._dateDiffInDays(start, end) > 31) {
      o2.api.page.notice(lp.detailStatisticList.startDateAndEndDateMoreThan, 'error');
      return false;
    }
    return true;
  },
  search() {
    if (this.validateForm()) {this.loadDetailList();}
  },
  toggleRecursive() {
    this.bind.form.recursive = !this.bind.form.recursive;
  },
  async loadDetailList() {
    await showLoading(this);
    this._showTableHeader();
    const form = this.bind.form;
    form.filterList = this.bind.filterList;
    try {
      const json = await detailAction("statistic", form);
      const list =  json || [];
      this.bind.statisticList = list;
    } catch (e) {
      console.error(e);
    }
    await hideLoading(this);
  },
  _showTableHeader() {
    const start = this._toDate(this.bind.form.startDate);
    const end = this._toDate(this.bind.form.endDate);
    this.bind.tableHeaderList = this._dateRangeToStringList(start, end);
  },
  async openRecordList(date, staticItem) {
    const list = staticItem.detailList || [];
    let detail = null;
    for (let index = 0; index < list.length; index++) {
      const element = list[index];
      if (element.recordDateString && element.recordDateString == date) {
        detail = element;
        break;
      }
    }
    if (detail) {
      const recordList = detail.recordList || [];
      this.$topParent.openRecordListVm({bind: { recordList:  recordList }})
    }
  },
  // detail 존재 여부
  checkDateDetail(date, staticItem) {
    const list = staticItem.detailList || [];
    for (let index = 0; index < list.length; index++) {
      const element = list[index];
      if (element.recordDateString && element.recordDateString == date) {
        return true;
      }
    }
    return false;
  },
  formatRecordList(date, staticItem){
    let result = "";
    const list = staticItem.detailList || [];
    let detail = null;
    for (let index = 0; index < list.length; index++) {
      const element = list[index];
      if (element.recordDateString && element.recordDateString == date) {
        detail = element;
        break;
      }
    }
    if (detail && detail.workDay) {
      const recordList = detail.recordList || [];
      for (let index = 0; index < recordList.length; index++) {
        const element = recordList[index];
        result +=  (element.checkInType === 'OnDuty' ? lp.onDuty : lp.offDuty) + ": "+ this._formatRecordResult(element);
        if (index != recordList.length-1) {
          result += "\n";
        }
      }
    }
    return result;
  },

  _formatRecordResult(record) {
    let span = "";
    if (record.fieldWork) {
      span = fieldWorkFormat(record);
    } else if(record.leaveData || record.leaveRequest) {
      span = lp.appeal.leave;
    }  else {
      const result = record.checkInResult;
      if (result === "PreCheckIn") {
        span = "";
      } else if (result === "NotSigned") {
        span = lp.appeal.notSigned;
      } else if (result === "Normal") {
        span = lp.appeal.normal;
      } else if (result === "Early") {
        span = lp.appeal.early;
      } else if (result === "Late") {
        span = lp.appeal.late;
      } else if (result === "SeriousLate") {
        span = lp.appeal.seriousLate;
      } else {
        span = "";
      }
    }
    return span;
  },
  // 사용자 이름 포맷
  formatName(person) {
    if (person && person.indexOf("@") > -1) {
      return person.split("@")[0];
    }
    return person;
  },
  // 근무 시간 포맷
  formatWorkTimeDuration(workTime) {
    return convertMinutesToHoursAndMinutes(workTime);
  },
  // 내보내기 
  statisticExport() {
    if (this.validateExportData()) {
      this.exportExcel();
    }
  },
  validateExportData() {
    if (!this.bind.statisticList || this.bind.statisticList.length < 1) {
      o2.api.page.notice(lp.detailStatisticList.exportDataEmptyPlaceholder, 'error');
      return false;
    }
    if (!this.bind.tableHeaderList || this.bind.tableHeaderList.length < 1) {
      o2.api.page.notice(lp.detailStatisticList.exportDataEmptyPlaceholder, 'error');
      return false;
    }
    return true;
  },
  async exportExcel() {
    await showLoading(this, lp.detailExportConfirmMsg);
    try {
      const workbook = new ExcelJS.Workbook();
      const worksheet = workbook.addWorksheet("sheet1");
      const columns = this.getExportColumns();

      worksheet.addRow(columns.map((column) => column.header));
      this.bind.statisticList.forEach((item) => {
        worksheet.addRow(columns.map((column) => column.value(item)));
      });

      this.setExportSheetStyle(worksheet, columns);

      const buffer = await workbook.xlsx.writeBuffer();
      const excelLink = document.createElement('a');
      const objectUrl = window.URL.createObjectURL(new Blob([buffer]));
      excelLink.href = objectUrl;
      excelLink.download = this.getExportFileName();
      excelLink.click();
      setTimeout(() => window.URL.revokeObjectURL(objectUrl), 0);
    } catch (err) {
      console.error(err);
      o2.api.page.notice(lp.dataError, 'error');
    } finally {
      await hideLoading(this);
    }
  },
  getExportColumns() {
    const fixedColumns = [
      { header: lp.detailTable.person, width: 20, value: (item) => this.formatName(item.userId) },
      { header: lp.detailTable.averageWorkTimeDuration, width: 16, value: (item) => item.averageWorkTimeDuration },
      { header: lp.detailTable.workTimeDuration, width: 16, value: (item) => this.formatWorkTimeDuration(item.workTimeDuration) },
      { header: lp.detailTable.attendance, width: 12, value: (item) => item.attendance },
      { header: lp.detailTable.rest, width: 12, value: (item) => item.rest },
      { header: lp.detailTable.absenteeismDays, width: 12, value: (item) => item.absenteeismDays },
      { header: lp.detailTable.lateTimes, width: 12, value: (item) => item.lateTimes },
      { header: lp.detailTable.leaveEarlierTimes, width: 12, value: (item) => item.leaveEarlierTimes },
      { header: lp.detailTable.absenceTimes, width: 12, value: (item) => item.absenceTimes },
      { header: lp.detailTable.fieldWorkTimes, width: 12, value: (item) => item.fieldWorkTimes },
    ];
    const dateColumns = this.bind.tableHeaderList.map((date) => ({
      header: date,
      width: 24,
      value: (item) => this.formatRecordList(date, item),
    }));
    return fixedColumns.concat(dateColumns);
  },
  setExportSheetStyle(worksheet, columns) {
    worksheet.columns = columns.map((column) => ({ width: column.width }));
    worksheet.views = [{ state: 'frozen', ySplit: 1 }];
    const border = {
      top: { style: 'thin' },
      left: { style: 'thin' },
      bottom: { style: 'thin' },
      right: { style: 'thin' },
    };
    worksheet.eachRow((row, rowNumber) => {
      row.height = rowNumber === 1 ? 24 : 36;
      row.eachCell({ includeEmpty: true }, (cell) => {
        cell.alignment = { vertical: 'middle', horizontal: rowNumber === 1 ? 'center' : 'left', wrapText: true };
        cell.border = border;
        if (rowNumber === 1) {
          cell.font = { bold: true };
          cell.fill = {
            type: 'pattern',
            pattern: 'solid',
            fgColor: { argb: 'FFE8F4FF' },
          };
        }
      });
      row.commit();
    });
  },
  getExportFileName() {
    const dates = this.bind.tableHeaderList || [];
    const startDate = dates[0] || this.bind.form.startDate;
    const endDate = dates[dates.length - 1] || this.bind.form.endDate;
    return `${lp.menu.detailStatisticFilter}_${startDate}_${endDate}.xlsx`;
  },
  
});
