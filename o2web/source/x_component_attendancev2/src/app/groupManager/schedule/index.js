import {component as content} from "@o2oa/oovm";
import {lp, o2} from "@o2oa/component";
import ExcelJS from "exceljs";

import {
    formatPersonName,
    getAllDatesInMonth,
    formatDate,
    formatMonth,
    isEmpty,
    showLoading,
    hideLoading,
    replaceCustomString,
    lpFormat,
    generateExcelColumnNames,
    chooseSingleFile
} from "../../../utils/common";
import {groupScheduleAction} from "../../../utils/actions";
import selectShiftMultiple from "../../shiftManager/selectShiftMultiple";
import style from "./style.scope.css";
import template from "./temp.html";


export default content({
    style,
    template,
    components: {selectShiftMultiple},
    autoUpdate: true,
    bind() {
        return {
            lp,
            fTitle: lp.scheduleForm.title,
            trueParticipantList: [], // 교대 편성 사용자
            month: "",
            groupId: "",
            dateList: [], // 날짜 객체
            //  교대조 선택기 사용
            shiftSelectorOpen: false,
            shiftSelector: {
                shiftSelected: []
            },
            // 근무 편성 데이터
            scheduleList: [],
            // 교대 편성 주기
            shiftCycleList: [], // 교대조 id
        };
    },
    // 먼저 데이터 조회
    async beforeRender() {
        // 월 날짜 데이터 초기화
        const now = new Date();
        now.setDate(1);// 매월 1일로 설정
        this.bind.month = formatMonth(now);
        this.bind.currentDate = now;
        // 교대조와 교대 편성 주기 등 설정 데이터
        await this.loadConfigData();
        // 날짜 사용자 표
        this.loadDateTable();
        // 교대 편성 데이터 월별 조회
        this.loadMonthScheduleList();

    },
    afterRender() {
        this.initUI();
        // 월 선택기 로드
        this.loadMonthPicker();
    },
    // 화면 초기화
    initUI() {
        const mask = this.dom.querySelector("#scheduleFormBox");
        mask.style["z-index"] = "1999";
        const dialog = this.dom.querySelector("#scheduleFormDialog");
        let width = mask.clientWidth;
        if (width > 1300) {
            width = 1300;
        }
        let height = mask.clientHeight - 200;
        if (height < 500) {
            height = mask.clientHeight;
        }
        let left = mask.clientWidth - width;
        if (left < 0) {
            left = 0;
        }
        left = left / 2;
        let top = mask.clientHeight - height;
        if (top < 0) {
            top = 0;
        }
        top = top / 2;
        dialog.style.width = width + "px";
        dialog.style.height = height + "px";
        dialog.style.left = left + "px";
        dialog.style.top = top + "px";
    },
    loadMonthPicker() {
        MWF.require("MWF.widget.Calendar", function () {
            var options = {
                "style": "xform",
                "secondEnable": false,
                "timeSelectType": "select",
                "clearEnable": false,
                "isTime": false,
                "timeOnly": false,
                "monthOnly": true,
                "yearOnly": false,
                "defaultDate": null,
                "defaultView": "month",
                "target": document.body,
                "onComplate": function (formateDate, date) {
                    this.changeMonthValue(date);
                }.bind(this)

            };
            if (this.bind.month && this.bind.month != "") {
                options.baseDate = new Date(this.bind.month);
            }
            let bindDom = this.dom.querySelector(".input");
            if (!bindDom) {
                bindDom = this.dom;
            }
            this.calendar = new MWF.widget.Calendar(bindDom, options);
        }.bind(this));
    },
    // 월 전환
    async changeMonthValue(date) {
        date.setDate(1);
        const newMonth = formatMonth(date);
        if (newMonth !== this.bind.month) {
            // 먼저 현재 월 데이터 저장
            await this.postMonthData(false);
            // 그다음 월을 갱신하고 페이지 새로고침
            this.bind.month = newMonth
            this.bind.currentDate = date;
            this.loadDateTable()
            // 새로고침 처리
            this.loadMonthScheduleList();
        }
    },
    // 교대조와 교대 편성 주기 등 설정 데이터
    async loadConfigData() {
        const config = await groupScheduleAction("configByGroupId", this.bind.groupId);
        if (config && config.scheduleConfigJson) {
            const jsonObj = JSON.parse(config.scheduleConfigJson);
            this.bind.shiftSelector.shiftSelected = jsonObj.shiftSelected || [];
            this.bind.shiftCycleList = jsonObj.shiftCycleList || [];
        }
    },
    // 날짜에 따라 표 표시
    loadDateTable() {
        // 사용자 날짜 표 데이터
        let dateList = [];
        const currentMonthDateList = getAllDatesInMonth(this.bind.currentDate);
        if (currentMonthDateList && currentMonthDateList.length > 0) {
            for (let index = 0; index < currentMonthDateList.length; index++) {
                const element = currentMonthDateList[index];
                const dName = this.dayName(element.getDay());
                dateList.push({
                    date: element,
                    text: `${element.getDate()}`,
                    day: dName,
                    dateString: formatDate(element)
                });
            }
        }
        this.bind.dateList = dateList;
    },
    // 월 데이터 조회
    async loadMonthScheduleList() {
        await showLoading(this);
        console.log("월 데이터 조회 시작", this.bind.month, this.bind.groupId);
        try {
            const list = await groupScheduleAction("listMonth", this.bind.groupId, this.bind.month);
            const scheduleList = list || [];
            this.loadShift(scheduleList);
            this.bind.scheduleList = scheduleList;
        } catch (e) {
            console.error(e);
        }
        await hideLoading(this);
    },
    // 로컬 캐시에 교대조 데이터가 없으면 반환된 교대 편성 결과에서 교대조 데이터 조회
    loadShift(scheduleList) {
        // 이미 교대조 데이터가 있어 처리 불필요
        if (this.bind.shiftSelector.shiftSelected.length === 0 && scheduleList.length > 0) {
            let shiftList = [];
            for (let index = 0; index < scheduleList.length; index++) {
                const element = scheduleList[index].shift;
                const existingItemIndex = shiftList.findIndex(item => item.id === element.id);
                if (existingItemIndex === -1) {
                    shiftList.push(element);
                }
            }
            this.bind.shiftSelector.shiftSelected = shiftList;
        }
    },
    // 교대조 목록 색상 스타일
    shiftIndexClassName(index) {
        return "shift_color_item shift_color_" + index;
    },
    // 요일의 한국어 이름 조회
    dayName(index) {
        switch (index) {
            case 0 :
                return lp.day.Sunday;
            case 1 :
                return lp.day.Monday;
            case 2 :
                return lp.day.Tuesday;
            case 3 :
                return lp.day.Wednesday;
            case 4 :
                return lp.day.Thursday;
            case 5 :
                return lp.day.Friday;
            case 6 :
                return lp.day.Saturday;
            default:
                return "";
        }
    },
    // 칸 안 교대조명 표시
    scheduleDateShow(date, person, list) {
        for (let index = 0; index < this.bind.scheduleList.length; index++) {
            const element = this.bind.scheduleList[index];
            if (element.scheduleDateString === date && element.userId === person) {
                return element.shift.shiftName;
            }
        }
        return "";
    },
    // 칸 안 교대조 스타일
    scheduleDateClass(date, person) {
        let className = "schedule_cell";
        let shiftId = "";
        for (let index = 0; index < this.bind.scheduleList.length; index++) {
            const element = this.bind.scheduleList[index];
            if (element.scheduleDateString === date && element.userId === person) {
                shiftId = element.shift.id;
                break;
            }
        }
        if (!isEmpty(shiftId)) {
            const index = this.bind.shiftSelector.shiftSelected.findIndex(function (e) {
                return e.id === shiftId;
            });
            if (index !== -1) {
                className = "schedule_cell shift_color_" + index;
            }
        }
        return className;
    },
    // 현재 페이지 닫기
    closeSelf() {
        this.component.destroy();
    },
    // 제출 버튼
    submit() {
        this.postMonthData(true);
    },
    // 지난달 교대 편성 데이터 복사
    copyFromLastMonth() {
        const year = this.bind.currentDate.getFullYear();
        const month = this.bind.currentDate.getMonth();
        // 지난달의 연도와 월 계산
        const lastMonthYear = month === 0 ? year - 1 : year;
        const lastMonth = month === 0 ? 11 : month - 1;
        const lastMonthDate = new Date(lastMonthYear, lastMonth, 1);
        const lastMonthString = formatMonth(lastMonthDate);
        var _self = this;
        const c = lpFormat(lp, "scheduleForm.copyConfirmInfo", {month: lastMonthString});
        o2.api.page.confirm(
            "warn",
            lp.alert,
            c,
            300,
            100,
            function () {
                this.close();
                _self.loadLastMonthDataAndCopy(lastMonthString);
            },
            function () {
                this.close();
            }
        );
    },
    // 월 데이터를 조회하고 현재 월로 데이터 교체
    async loadLastMonthDataAndCopy(month) {
        await showLoading(this);
        console.log(this.bind.month, month);
        try {
            const list = await groupScheduleAction("listMonth", this.bind.groupId, month);
            const scheduleList = list || [];
            if (scheduleList.length > 0) {
                // 데이터 처리: 월을 현재 월로 변경
                let newScheduleList = [];
                for (let index = 0; index < scheduleList.length; index++) {
                    const element = scheduleList[index];
                    element.scheduleDateString = replaceCustomString(element.scheduleDateString, month, this.bind.month);
                    newScheduleList.push(element);
                }
                this.bind.scheduleList = newScheduleList;
            }
        } catch (e) {
            console.error(e);
        }
        await hideLoading(this);
    },
    // 교대 편성 데이터 제출
    async postMonthData(close) {
        if (this.bind.scheduleList.length === 0) {
            if (close) {
                this.closeSelf();
            }
            return;
        }
        await showLoading(this);
        try {
            // 교대 편성 교대조 목록과 주기 목록 데이터를 설정으로 서버에 저장해 이후 사용
            const scheduleConfigJson = {
                shiftSelected: this.bind.shiftSelector.shiftSelected,
                shiftCycleList: this.bind.shiftCycleList
            };
            const body = {
                groupId: this.bind.groupId,
                month: this.bind.month,
                scheduleList: this.bind.scheduleList,
                scheduleConfigJson: JSON.stringify(scheduleConfigJson)
            };
            const result = await groupScheduleAction("postMonth", body);
            console.log(result);
        } catch (e) {
            console.error(e);
        }
        await hideLoading(this);
        if (close) {
            this.closeSelf();
        }
    },
    personName(person) {
        return formatPersonName(person);
    },
    // 교대조 선택기 열기
    async openShiftSelector() {
        this.bind.shiftSelectorOpen = true;
    },
    // 교대조 선택기 닫기
    closeMultipleShiftSelector() {
        this.bind.shiftSelectorOpen = false;
    },
    // 교대 편성 칸 클릭
    clickScheduleBox(date, person, cycle, e) {
        // 현재 클릭한 날짜와 사용자 바인딩
        this.bind.clickDate = date;
        this.bind.clickPerson = person;
        // 교대 주기
        this.bind.clickForCycle = cycle;
        // 선택 상자
        const target = e.currentTarget;
        const divWidth = target.offsetWidth; // div 요소의 너비 조회
        const divHeight = target.offsetHeight; // div 요소의 높이 조회
        const containerRect = this.dom.querySelector("#scheduleContainer").getBoundingClientRect(); // 상위 요소(container)의 위치 정보 조회
        const divRect = target.getBoundingClientRect(); // div 요소의 위치 정보 조회
        const offsetX = divRect.left - containerRect.left; // 상위 요소 기준 div 요소의 가로 오프셋 계산
        const offsetY = divRect.top - containerRect.top; // 상위 요소 기준 div 요소의 세로 오프셋 계산
        this._openChooseBox(divWidth, divHeight, offsetX, offsetY);
    },
    // 교대조 드롭다운 선택 상자 열기
    _openChooseBox(divWidth, divHeight, offsetX, offsetY) {
        const selectedBox = this.dom.querySelector("#selectedFrame");
        selectedBox.style.width = divWidth + "px";
        selectedBox.style.height = divHeight + "px";
        selectedBox.style.left = offsetX + "px";
        selectedBox.style.top = offsetY + "px";
        selectedBox.style.display = "";
        const tPopup = this.dom.querySelector(".t-popup");
        if (offsetX < 500) {
            tPopup.style.inset = "0px auto auto 0px ";
            tPopup.style.transform = "translate3d(" + divWidth + "px, 5px, 0px)";
        } else {
            tPopup.style.inset = "0px 0px auto auto";
            tPopup.style.transform = "translate3d(-" + divWidth + "px, 5px, 0px)";
        }
        this.hideEvent = this._chooseBoxOutHide.bind(this);
        this.dom.addEventListener('mousedown', this.hideEvent);
    },
    // 교대조 드롭다운 선택 상자 닫기
    _closeChooseBox() {
        const selectedBox = this.dom.querySelector("#selectedFrame");
        selectedBox.style.display = "none";
        this.dom.removeEventListener('mousedown', this.hideEvent);
    },
    // 외부 마우스 클릭 시 닫기
    _chooseBoxOutHide(e) {
        // 드롭다운 범위 계산, 범위 밖 클릭 시 숨김
        const targetElement = this.dom.querySelector("#selectedFrame");
        var clickedElement = e.target;
        var isInsideTarget = false;
        // 클릭한 대상 요소부터 DOM 계층을 위로 순회
        while (clickedElement) {
            if (clickedElement === targetElement) {
                isInsideTarget = true;
                break;
            }
            clickedElement = clickedElement.parentNode;
        }
        if (!isInsideTarget) {
            this._closeChooseBox();
        }
    },
    // 교대조 선택
    chooseShiftOnDate(shift) {
        debugger;
        if (this.bind.clickForCycle) { // 교대 주기
            this.bind.shiftCycleList.push(shift);
        } else {
            this.putInScheduleList(shift, this.bind.clickDate, this.bind.clickPerson);
        }
        this._closeChooseBox();
    },
    // 교대 편성 목록에 추가
    putInScheduleList(shift, date, person) {
        let exist = false;
        for (let index = 0; index < this.bind.scheduleList.length; index++) {
            const element = this.bind.scheduleList[index];
            if (element.scheduleDateString === date && element.userId === person) {
                element.shift = shift;
                this.bind.scheduleList[index] = element;
                exist = true;
            }
        }
        if (!exist) {
            this.bind.scheduleList.push({
                scheduleDateString: date,
                userId: person,
                shift: shift,
                shiftId: shift.id
            });
        }
    },
    // 교대 편성 주기에 따라 편성
    scheduleByCycle() {
        if (this.bind.clickDate && this.bind.shiftCycleList.length > 0) {
            const date = new Date(this.bind.clickDate);
            const dateList = this._getMonthDates(date);
            const cycleList = this.bind.shiftCycleList;
            let cycleIndex = 0;
            for (let index = 0; index < dateList.length; index++) {
                const element = dateList[index];
                const forDate = formatDate(element);
                const shift = cycleList[cycleIndex];
                const scheduleIndex = this.bind.scheduleList.findIndex((item) => item.scheduleDateString === forDate && item.userId === this.bind.clickPerson);
                if (shift.id === "rest") {
                    if (scheduleIndex > -1) {
                        this.bind.scheduleList.splice(scheduleIndex, 1);
                    }
                } else {
                    if (scheduleIndex > -1) {
                        this.bind.scheduleList[scheduleIndex].shift = shift;
                        this.bind.scheduleList[scheduleIndex].shiftId = shift.id;
                    } else {
                        this.bind.scheduleList.push({
                            scheduleDateString: forDate,
                            userId: this.bind.clickPerson,
                            shift: shift,
                            shiftId: shift.id
                        });
                    }
                }

                if (cycleIndex >= cycleList.length - 1) {
                    cycleIndex = 0;
                } else {
                    cycleIndex++;
                }
            }
        }
        this._closeChooseBox();
    },
    // date 날짜부터 해당 월 마지막 날까지의 모든 날짜 데이터
    _getMonthDates(date) {
        // 주어진 날짜의 연도와 월 조회
        const year = date.getFullYear();
        const month = date.getMonth();
        // 새 Date 객체를 생성하고 날짜를 다음 달 첫째 날로 설정
        const nextMonth = new Date(year, month + 1, 1);
        // 반복문으로 해당 월의 모든 날짜 데이터 생성
        const dates = [];
        let currentDate = date;
        while (currentDate < nextMonth) {
            dates.push(new Date(currentDate));
            currentDate.setDate(currentDate.getDate() + 1);
        }
        return dates;
    },
    // 교대조 또는 교대 주기 중 휴무 초기화
    clearShiftOnDate() {
        if (this.bind.clickForCycle) { // 교대 주기
            this.bind.shiftCycleList.push({
                id: "rest",
                shiftName: lp.scheduleForm.restShift
            });
        } else { // 교대조 초기화
            this.deleteScheduleListByDatePerson(this.bind.clickDate, this.bind.clickPerson)
        }
        this._closeChooseBox();
    },
    // 교대 편성 목록에서 데이터 한 건 삭제
    deleteScheduleListByDatePerson(date, person) {
        const existingItemIndex = this.bind.scheduleList.findIndex(item => item.scheduleDateString === date && item.userId === person);
        if (existingItemIndex > -1) {
            this.bind.scheduleList.splice(existingItemIndex, 1);
        }
    },
    // 교대 편성 주기에서 데이터 한 건 삭제
    deleteCycleShift(shift) {
        if (!shift) {
            return;
        }
        const existingItemIndex = this.bind.shiftCycleList.findIndex(item => item.id === shift.id);
        if (existingItemIndex > -1) {
            this.bind.shiftCycleList.splice(existingItemIndex, 1);
        }
    },
    // 교대 편성 excel 템플릿 다운로드
    async downloadScheduleTemplate() {
        if (this.bind.shiftSelector.shiftSelected.length === 0) {
            o2.api.page.notice(lp.scheduleForm.downloadExcelTempEmptyShift, 'error');
            return;
        }
        if (this.bind.trueParticipantList.length === 0) {
            console.error("참여자가 없습니다");
            return;
        }
        // excel 파일 생성 시작
        const _shifts = this.bind.shiftSelector.shiftSelected.map((item) => item.shiftName) // 교대조 목록
        const people = this.bind.trueParticipantList.map((item) => formatPersonName(item)) // 참여자 목록
        const monthDayNum = this.bind.dateList.length + 1 // 월 일수, 첫 열은 이름
        _shifts.push(lp.scheduleForm.excelEmptyShift)
        const _workbook = new ExcelJS.Workbook()
        // 교대조 선택용 워크시트 추가
        const _sheet2 = _workbook.addWorksheet('sheet2')
        _sheet2.addRow(_shifts)
        _sheet2.state = 'hidden' // 숨김, 표시 불필요
        // 교대조 수에 따라 해당 열 식별자 생성
        const _sheet2ColumnNames = generateExcelColumnNames(_shifts.length)
        // 교대조 드롭다운 선택 목록 데이터 표현식
        const formulae = 'sheet2!$' + _sheet2ColumnNames[0] + '$1:$' + _sheet2ColumnNames[_sheet2ColumnNames.length - 1] + '$1'
        console.log(formulae)
        // 사용자 조작용 워크시트
        const _sheet1 = _workbook.addWorksheet('sheet1')
        // 이번 달 날짜 수에 따라 해당 열 식별자 생성
        const columnNames = generateExcelColumnNames(monthDayNum);
        console.log(columnNames)
        //첫 행 헤더: 병합 가운데 정렬, 색 채우기
        _sheet1.addRow([lpFormat(lp, 'scheduleForm.excelTitle', {month: this.bind.month})])
        _sheet1.mergeCells('A1:' + columnNames[columnNames.length - 1] + '1')
        _sheet1.getCell('A1').alignment = {vertical: 'middle', horizontal: 'center'}
        _sheet1.getCell('A1').fill = {
            type: 'pattern',
            pattern: 'solid',
            fgColor: {argb: 'FFCCFFFF'}
        }
        // 둘째 행 날짜
        let header1 = [] // 요일 넣기
        let header2 = [] // 날짜 넣기
        for (let i = 0; i < monthDayNum; i++) {
            if (i === 0) {
                header1.push(lp.detailTable.person)
                header2.push("")
            } else {
                const _date = this.bind.dateList[i - 1]
                header1.push(_date.day)
                header2.push(_date.text)
            }
        }
        _sheet1.addRow(header1)
        _sheet1.addRow(header2)
        // 색 채우기
        const headerFill = {
            type: 'pattern',
            pattern: 'solid',
            fgColor: {argb: 'FFCCFFCC'}
        }
        for (let i = 0; i < columnNames.length; i++) {
            _sheet1.getCell(columnNames[i] + '2').fill = headerFill
            _sheet1.getCell(columnNames[i] + '3').fill = headerFill
        }
        // A2A3에 이름 필드 넣기, 병합 가운데 정렬
        _sheet1.mergeCells('A2:A3')
        _sheet1.getCell('A2').alignment = {vertical: 'middle', horizontal: 'center'}

        // 넷째 행부터 이후 행은 사용자별로 행 추가
        for (let i = 0; i < people.length; i++) {
            _sheet1.addRow([people[i]])
        }
        //첫 열은 사용자 이름, 1부터 해당 셀에 교대조 드롭다운 데이터 추가
        for (let i = 1; i < columnNames.length; i++) {
            const column = columnNames[i]
            for (let j = 0; j < people.length; j++) {
                const cellName = column + (j + 4) // 넷째 행부터 사용자 교대조 선택
                _sheet1.getCell(cellName).dataValidation = {
                    type: 'list',
                    allowBlank: true,
                    formulae: [formulae]
                }
            }
        }
        // 모든 셀에 테두리 추가
        const border = {
            top: {style: 'thin'},
            left: {style: 'thin'},
            bottom: {style: 'thin'},
            right: {style: 'thin'}
        }
        _sheet1.eachRow(function (row, rowNumber) {
            row.eachCell({includeEmpty: true}, function (cell, colNumber) {
                cell.border = border
            });
            row.commit();
        });
        // 파일 다운로드
        const buffer = await _workbook.xlsx.writeBuffer()
        const excelLink = document.createElement('a')
        excelLink.href = window.URL.createObjectURL(new Blob([buffer]))
        excelLink.download = lpFormat(lp, 'scheduleForm.excelFileName', {month: this.bind.month})
        excelLink.click()
    },
    // excel 파일에서 데이터 읽기
    async excelJsReadFile(file) {
        const workbook = new ExcelJS.Workbook();  // new 생성
        await workbook.xlsx.load(file);  // 파일 읽기
        const worksheet = workbook.getWorksheet('sheet1');  // sheet1 읽기, 위의 내보내기 템플릿과 동일
        const monthDayNum = this.bind.dateList.length + 1 // 월 일수, 첫 열은 이름
        const columnNames = generateExcelColumnNames(monthDayNum);
        this.bind.trueParticipantList.forEach((participant, j) => {
            const rowNum = 4 + j
            columnNames.forEach((columnName, i) => {
                if (i !== 0) {
                    const cellValue = worksheet.getCell(columnName + rowNum).value
                    let shift = null
                    const date = this.bind.dateList[i - 1]
                    if (cellValue && cellValue !== "") {
                        shift = this.bind.shiftSelector.shiftSelected.find(item => item.shiftName === cellValue)
                    }
                    if (shift) {
                        this.putInScheduleList(shift, date.dateString, participant)
                    } else {
                        this.deleteScheduleListByDatePerson(date.dateString, participant)
                    }
                }
            })
        })
    },
    // 편성 완료된 excel 템플릿을 업로드해 데이터를 시스템에 가져오기
    uploadScheduleTemplate() {
        if (this.bind.shiftSelector.shiftSelected.length === 0) {
            o2.api.page.notice(lp.scheduleForm.downloadExcelTempEmptyShift, 'error');
            return;
        }
        chooseSingleFile((file) => {
            // 파일 읽기
            const reader = new FileReader();
            reader.onloadend = () => {
                const arrayBuffer = reader.result
                this.excelJsReadFile(arrayBuffer)
            }
            reader.readAsArrayBuffer(file)
        })
    },
});
