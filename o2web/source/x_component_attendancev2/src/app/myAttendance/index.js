import { component as content } from "@o2oa/oovm";
import { lp } from "@o2oa/component";
import { myAction } from "../../utils/actions";
import { convertTo2DArray, fieldWorkFormat } from "../../utils/common";
import template from "./temp.html";
import style from "./style.scope.css";

export default content({
  template,
  style,
  autoUpdate: true,
  bind() {
    return {
      lp,
      startFromMonday: true, // 월요일부터 시작 여부 
      dayList: [], // 요일 목록
      dateWithData: [], // 업무 데이터를 포함한 달력 데이터
      statistic: {
        userId: "",
        workTimeDuration: 0,
        averageWorkTimeDuration: "0.0",
        attendance: 0,
        rest: 0,
        absenteeismDays: 0,
        lateTimes: 0,
        leaveEarlierTimes: 0,
        absenceTimes: 0,
        fieldWorkTimes: 0,
        leaveDays: 0,
        appealNums: 0
      } // 통계 데이터
    };
  },
  afterRender() {
    this.loadDayList();
    this.loadDate();
  },
  // 요일 목록 
  loadDayList() {
    let dayList = [];
    if (this.bind.startFromMonday) {
      for (let index = 1; index < 8; index++) {
        if (index === 7) {
          dayList.push({
            name: this.dayName(0),
            day: 0
          });
        } else {
          dayList.push({
            name: this.dayName(index),
            day: index
          });
        }
      }
    } else {
      for (let index = 0; index < 7; index++) {
        dayList.push({
          name: this.dayName(index),
          day: index
        });
      }
    }
    this.bind.dayList = dayList;
  },
  // 요일의 한국어 이름 조회
  dayName(index) {
    switch(index) {
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
  // 현재 월의 모든 날짜를 가져오고 앞뒤 날짜를 채워 완전한 달력 구성
  // 기본은 일요일부터 시작, startFromMonday = true 이면 월요일부터 시작
  loadDate() {
    let start = 0
    if (this.bind.startFromMonday) {
      start = 1;
    }
    // 현재 날짜 조회
    const today = new Date();
    this.bind.currentDateTime = today.getTime();
    // 현재 월 조회
    const currentMonth = today.getMonth();
    // 현재 연도 조회
    const currentYear = today.getFullYear();
    const currentDate = today.getDate();
    this.bind.todayString = `${currentYear}-${(currentMonth + 1) < 10 ? "0" + (currentMonth + 1) : (currentMonth + 1)}-${(currentDate) < 10 ? "0" + (currentDate) : (currentDate)}`
    
    this.loadDataByDate(start, currentYear, currentMonth);
  },
  clickToNextMonth() {
    let start = 0
    if (this.bind.startFromMonday) {
      start = 1;
    }
    const preDateTime = this.bind.currentDateTime || new Date().getTime();
    const preDate = new Date(preDateTime);
    // 현재 월 조회
    const preMonth = preDate.getMonth();
    // 현재 연도 조회
    let currentYear = preDate.getFullYear();
    let currentMonth = preMonth + 1;
    if (currentMonth == 12) {
      currentMonth = 0;
      currentYear = currentYear + 1;
    }
    this.bind.currentDateTime = new Date(currentYear, currentMonth, 1).getTime();// 다음 달 1일
    this.loadDataByDate(start, currentYear, currentMonth);
  },
  clickToPreMonth() {
    let start = 0
    if (this.bind.startFromMonday) {
      start = 1;
    }
    const lastDateTime = this.bind.currentDateTime || new Date().getTime();
    const lastDate = new Date(lastDateTime);
    // 현재 월 조회
    const lastMonth = lastDate.getMonth();
     // 현재 연도 조회
     let currentYear = lastDate.getFullYear();
     let currentMonth = lastMonth - 1;
     if (currentMonth == -1) {
       currentMonth = 11;
       currentYear = currentYear - 1;
     }
     this.bind.currentDateTime = new Date(currentYear, currentMonth, 1).getTime();// 다음 달 1일
     this.loadDataByDate(start, currentYear, currentMonth);
  },
  // 데이터 로드
  // @param start 1 월요일부터 시작
  async loadDataByDate( start, currentYear, currentMonth) {
    // 해당 월의 일수 조회
    const daysInMonth = new Date(currentYear, currentMonth + 1, 0).getDate(); // 10-31
    // 해당 월 첫째 날의 요일 조회
    const firstDayOfMonth =  new Date(currentYear, currentMonth, 1).getDay(); // 일요일  0
    // 해당 월 마지막 날의 요일 조회
    // 채워야 할 일수 계산
    const daysToPad = (7 - ((daysInMonth + firstDayOfMonth) % 7) + start) % 7; //  4
    // 채워야 할 이전 달 일수 계산
    const lastMonthEndDate = new Date(currentYear, currentMonth, 0);
    const daysInPrevMonth = lastMonthEndDate.getDate(); // 9-30  
    // 날짜를 저장할 배열 생성
    let dates = [];
    // 해당 월 출퇴근 체크 데이터 조회
    const startDate = `${currentYear}-${(currentMonth + 1) < 10 ? "0" + (currentMonth + 1) : (currentMonth + 1)}-01`;
    const endDate = `${currentYear}-${(currentMonth + 1) < 10 ? "0" + (currentMonth + 1) : (currentMonth + 1)}-${daysInMonth}`;
    const reqBody = {
      startDate: startDate,
      endDate: endDate,
    };
    let list = [];
    try {
      list = (await myAction("listDetailWithDate", reqBody));
    } catch (error) {
     console.error(error);  
    }
    // 채워야 할 이전 달 날짜 추가
    const preDay = firstDayOfMonth == 0 ? daysInPrevMonth - 6 + start : daysInPrevMonth - firstDayOfMonth + 1 + start 
    for (
      let i = preDay;
      i <= daysInPrevMonth;
      i++
    ) {
      dates.push({
        date: i,
        notCurrent: 1, // 현재 월 여부
        dateYmd: `${lastMonthEndDate.getFullYear()}-${(lastMonthEndDate.getMonth() + 1)  < 10 ? "0" + (lastMonthEndDate.getMonth() + 1) : (lastMonthEndDate.getMonth() + 1) }-${i}`
      });
    }
    // 현재 월의 모든 날짜 추가
    for (let i = 1; i <= daysInMonth; i++) {
      const dateYmd = `${currentYear}-${(currentMonth + 1) < 10 ? "0" + (currentMonth + 1) : (currentMonth + 1)}-${i < 10 ? "0"+i : i}`;
      dates.push({
        date: i,
        notCurrent: 0, 
        dateYmd: dateYmd,
      });
    }
    // 채워야 할 다음 달 날짜 추가
    const nextMonthFirstDate = new Date(currentYear, currentMonth + 1, 1);
    for (let i = 1; i <= daysToPad; i++) {
      dates.push({
        date: i,
        notCurrent: 1,
        dateYmd: `${nextMonthFirstDate.getFullYear()}-${ (nextMonthFirstDate.getMonth() + 1) < 10 ? "0" + (nextMonthFirstDate.getMonth() + 1) : (nextMonthFirstDate.getMonth() + 1) }-0${i}`
      });
    }
    // 데이터를 달력에 병합
    const newDates = dates.map(date => {
      const detail = list.find(element => element.recordDateString === date.dateYmd) || null;
      date.detail = detail;
      date.workDay = true; // 기본 흰색 배경
      return date;
    });
    const checkWorkDayList = dates.map(date => {
      return date.dateYmd;
    });
    // 날짜 배열 출력
    this.bind.dateWithData = convertTo2DArray(newDates, 7);
    // 현재 통계 주기 월과 날짜
    this.bind.cycleMonth = `${currentYear}${lp.year}${(currentMonth + 1)}${lp.month}`;
    const showMonth = (currentMonth + 1) < 10 ? "0" + (currentMonth + 1) : (currentMonth + 1);
    this.bind.cycleDate = `${showMonth}${lp.month}01 - ${showMonth}${lp.month}${daysInMonth}`;
    // 통계 정보
    this.loadMyStatistic(reqBody);
    // 근무일 여부 조회
    this.loadIsWorkDay(checkWorkDayList);
  },
  async loadMyStatistic(body) {
    const statistic = (await myAction("statistic", body));
    if (statistic) {
      this.bind.statistic = statistic;
    }
  },
  async loadIsWorkDay(checkIsMyRestDay) {
    const old = this.bind.dateWithData;
    let list = [];
    try {
      const result = (await myAction("checkIsMyRestDay", { dateList : checkIsMyRestDay}));
      if (result && result.restDateList) {
        list = result.restDateList;
      }
    } catch (error) {
     console.error(error);  
    }
    for (let i = 0; i < old.length; i++) {
      let line = old[i];
      for (let index = 0; index < line.length; index++) {
        const element = line[index];
        if (list.indexOf(element.dateYmd) > -1) {
            element.workDay = false;
            line[index] = element;
        }
      }
    }
  },
  // 달력 칸의 class
  formatCalItemClass(item) {
    let className = 'item';
    if (item.notCurrent === 1) {
      className += ' out';
    }
    if (item.workDay === false) {
      className += ' rest';
    }
    return className;
  },
  // 출퇴근 체크
  formatRecord(record) {
    let time = "";
    let type = lp.offDutySimple;
    if (record && record.recordDate) {
      time = record.recordDate.substring(11, 16);
    }
    if (record && record.checkInType === 'OnDuty') {
      type = lp.onDutySimple;
    }
    return `${time}`;
  },
  // 날짜의 class
  formatDateClass(date) {
    if (date === this.bind.todayString) {
      return 'item-cal-date-text item-cal-date-today';
    }
    return 'item-cal-date-text'
  },
  formatRecordResultTagClass(record) {
    let statusClassName = '';
    if (record && record.checkInResult) {
      switch (record.checkInResult) {
        case 'Early':
          statusClassName =  "item-record-status-tag record-status-early";
          break;
        case 'Late':
        case 'SeriousLate':
            statusClassName =  "item-record-status-tag record-status-late";
          break;
        case 'NotSigned':
            statusClassName =  "item-record-status-tag record-status-nosign";
          break;
        default:
          statusClassName =  "";
      }
      if (record.fieldWork == true) {
        statusClassName =  "item-record-status-tag record-status-fieldwork";
      } else if (record.leaveData || record.leaveRequest) {
        statusClassName =  "item-record-status-tag record-status-leave";
      }  else if (record.appealId && statusClassName === "") {
        // 관리자 처리 시 정상으로 간주
        if (record.appealData && record.appealData.status === 4) {
          statusClassName =  "";
        } else {
          statusClassName =  "item-record-status-tag record-status-appeal";
        }
      }
    }
    return statusClassName;
  },
  formatRecordResultTagName(record) {
    let tagName = '';
    if (record && record.checkInResult) {
      switch (record.checkInResult) {
        case 'Early':
          tagName =  lp.appeal.early;
          break;
        case 'Late':
        case 'SeriousLate':
          tagName =  lp.appeal.late;
          break;
        case 'NotSigned':
          tagName =  lp.appeal.notSigned;
          break;
        default:
          tagName =  "";
      }
      if (record.fieldWork == true) {
        tagName =  fieldWorkFormat(record);
      } else if (record.leaveData || record.leaveRequest) {
        tagName =  lp.appeal.leave;
      } else if (record.appealId && tagName === "") {
        // 관리자 처리 시 정상으로 간주
        if (record.appealData && record.appealData.status === 4) {
          tagName =  "";
        } else {
          tagName =  lp.appeal.appeal;
        }
      }
    }
    return tagName;
  },
  
});
