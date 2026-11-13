import { component as content } from "@o2oa/oovm";
import { lp, o2 } from "@o2oa/component";
import { isEmpty } from "../../../utils/common";
import { groupAction, attendanceWorkPlaceV2Action, attendanceShiftAction } from "../../../utils/actions";
import style from "./style.scope.css";
import template from "./temp.html";
import oInput from "../../../components/o-input";
import oOrgPersonSelector from "../../../components/o-org-person-selector";
import selectShift from "../../shiftManager/selectShift";
import addressSelector from "../../addressManager/addressSelector";

export default content({
  style,
  template,
  components: { oInput, oOrgPersonSelector, selectShift, addressSelector },
  autoUpdate: true,
  bind() {
    return {
      lp,
      fTitle: lp.groupAdd,
      form: {
        groupName: "",
        checkType: "1", // 1 고정 근무제 2 자율 출퇴근 3 교대 근무제
        participateList: [],
        assistAdminList: [], // 보조 관리자
        unParticipateList: [],
        workPlaceIdList: [],
        allowFieldWork: false,  // 외근 체크 허용 여부.
        requiredFieldWorkRemarks: false, // 외근 체크 비고 필수 여부.
        requiredFieldWorkPhoto: false, // 외근 출퇴근 체크 시 사진 필요 여부.
        fieldWorkMarkError: false, // 외근 체크를 비정상 데이터로 처리할지 여부.
        requiredCheckInDateList: [], // 필수 출퇴근 체크 날짜, 예: 2023-01-01.
        noNeedCheckInDateList: [], // 출퇴근 체크 불필요 날짜, 예: 2023-01-01.
      },
      // 교대조 선택기 사용
      shiftSelectorOpen: false,
      shiftSelector: {
        key: "all", // 현재 교대조를 선택할 위치
        shiftSelected: {},
      },
      // 근무지 선택기 사용
      workPlaceSelectorOpen: false,
      workPlaceSelector: {
        workAddressSelected: []
      },
      // 고정 근무제: 교대조와 출퇴근 체크 요일 선택
      workDay: {
        monday: {
          checked: true,
          shiftId: "",
          shiftSelected: {},
        },
        tuesday: {
          checked: true,
          shiftId: "",
          shiftSelected: {},
        },
        wednesday: {
          checked: true,
          shiftId: "",
          shiftSelected: {},
        },
        thursday: {
          checked: true,
          shiftId: "",
          shiftSelected: {},
        },
        friday: {
          checked: true,
          shiftId: "",
          shiftSelected: {},
        },
        saturday: {
          checked: false,
          shiftId: "",
          shiftSelected: {},
        },
        sunday: {
          checked: false,
          shiftId: "",
          shiftSelected: {},
        },
      },
      // 자율 근무제: 출퇴근 체크 요일 선택
      workDateList: [], // 선택된 days
      days: [
        {
          name: lp.day.Monday,
          value: "1",
        },
        {
          name: lp.day.Tuesday,
          value: "2",
        },
        {
          name: lp.day.Wednesday,
          value: "3",
        },
        {
          name: lp.day.Thursday,
          value: "4",
        },
        {
          name: lp.day.Friday,
          value: "5",
        },
        {
          name: lp.day.Saturday,
          value: "6",
        },
        {
          name: lp.day.Sunday,
          value: "0",
        },
      ],
      dateCycleList: [
        {name: lp.dateCycle.none, value: "none"},
        {name: lp.dateCycle.week, value: "week"}, 
        {name: lp.dateCycle.twoWeek, value: "twoWeek"}, 
        {name: lp.dateCycle.month, value: "month"}, 
      ],
      requiredCheckInDateForTableList: [], // 필수 출퇴근 체크 날짜, 객체 date, shift, cycle 저장
      noNeedCheckInDateForTableList: [], // 출퇴근 체크 불필요 날짜, 예: 객체 date, cycle 저장
    };
  },
  // 먼저 데이터 조회
  async beforeRender() {
    if (this.bind.updateId) {
      const group = await groupAction("get", this.bind.updateId);
      if (group) {
        this.bind.form = group;
        if (group.workDateProperties) {
          // 프론트엔드용 객체로 처리 
          const dayList = ["monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday"];
          for (let key of dayList) {
            if (group.workDateProperties[key]) {
              let day = group.workDateProperties[key];
              if (day && day.shift) {
                day.shiftSelected = day.shift;
                group.workDateProperties[key] = day;
              }
            } else {
              group.workDateProperties[key] = {
                checked: false
              }
            }
          }
          this.bind.workDay = group.workDateProperties;
        }
      }
    }
  },
  // 
  afterRender() {
    if (this.bind.form && this.bind.form.id && this.bind.form.id !== "") { // 수정
      this.bind.fTitle = lp.groupUpdate;
      // 근무지 객체 역조회
      if (this.bind.form.workPlaceIdList && this.bind.form.workPlaceIdList.length > 0) {
        this.loadWorkPlaceObjectsByIds(this.bind.form.workPlaceIdList);
      }
      // 날짜 목록 분리
      if (this.bind.form.workDateList) {
        const dateList = this.bind.form.workDateList.split(",");
        this.bind.workDateList= dateList || [];
      }
      // 필수 출퇴근 체크 날짜 프론트엔드 데이터 변환
      this.dealRequiredCheckInDate();
      // 출퇴근 체크 불필요 날짜 프론트엔드 데이터 변환
      this.dealNoNeedCheckInDate();
    }
    // 시간 선택기
    this.loadRequiredDateSelector();
    this.loadNoNeedDateSelector();
  },
  async dealRequiredCheckInDate() {
    let list = [];
    for (let index = 0; index < this.bind.form.requiredCheckInDateList.length; index++) {
      const element = this.bind.form.requiredCheckInDateList[index];
      const dArray = element.split("|");
      const dateObj = {
        date: dArray[0]
      };
      if (dArray[1]) {
        const shift = await attendanceShiftAction("get", dArray[1]);
        dateObj.shift = shift;
      }
      if(dArray[2]) {
        const cycle = this.bind.dateCycleList.filter( c => c.value === dArray[2]);
        dateObj.cycle = cycle[0];
      }
      list.push(dateObj);
    }
    this.bind.requiredCheckInDateForTableList = list;
  },
  dealNoNeedCheckInDate() {
    this.bind.noNeedCheckInDateForTableList = this.bind.form.noNeedCheckInDateList.map(d => {
      const dArray = d.split("|");
      if (dArray[1]) {
        const cycle = this.bind.dateCycleList.filter( c => c.value === dArray[1]);
        return {
          date: dArray[0],
          cycle: cycle[0]
        };
      }
      return {
        date: dArray[0]
      };
    });
  },
  // 근무일 선택
  changeWorkDayChecked(key) {
    if (key && this.bind.workDay[key]) {
      this.bind.workDay[key].checked = !this.bind.workDay[key].checked;
      if (!this.bind.workDay[key].checked) {
        this.bind.workDay[key].shiftSelected = {}; 
      }
    }
  },
  // 교대조 선택기 열기, 이미 선택된 항목 일괄 설정
  openShiftSelect(key) {
    this.bind.shiftSelector.key = key || "all";
    this.bind.shiftSelectorOpen = true;
  },
  // 교대조 선택기 호출, 선택된 교대조 객체 반환
  setShiftData(shift) {
    if (!shift) {
      return;
    }
    // 새 객체, 바인딩 정보 초기화
    const newShift =  {
      id: shift.id,
      shiftName: shift.shiftName,
    };
    // requiredCheckIn- 로 시작하는 key는 필수 출퇴근 체크 날짜 설정을 의미
    if (this.bind.shiftSelector.key.startsWith('requiredCheckIn-')) {
      const date = this.bind.shiftSelector.key.substring('requiredCheckIn-'.length);
      for (let index = 0; index < this.bind.requiredCheckInDateForTableList.length; index++) {
        const element = this.bind.requiredCheckInDateForTableList[index];
        if (element.date === date) {
          element.shift = newShift;
          this.bind.requiredCheckInDateForTableList[index] = element;
        }
      }
    } else if (this.bind.shiftSelector.key  === "all") {
      this.bind.shiftSelector.shiftSelected = newShift;
      // 기타 교대조
      for (let prop in this.bind.workDay) {
        if (this.bind.workDay[prop] && this.bind.workDay[prop].checked) {
          this.bind.workDay[prop].shiftSelected = newShift;
        }
      }
    } else {
      if (this.bind.workDay[this.bind.shiftSelector.key]) {
        this.bind.workDay[this.bind.shiftSelector.key].shiftSelected = newShift;
      }
    }
  },
  // 교대조 선택기 닫기
  closeShiftSelect() {
    this.bind.shiftSelectorOpen = false;
  },
  // days의 checkbox 클릭
  selectDay(value) {
    if (value.value) {
      const index = this.bind.workDateList.indexOf(value.value);
      if (index > -1) {
        this.bind.workDateList.splice(index, 1);
      } else {
        this.bind.workDateList.push(value.value);
      }
    }
  },
  async loadWorkPlaceObjectsByIds(ids) {
    const list = await attendanceWorkPlaceV2Action("listWithWorkPlaceObject", {"idList": ids});
    this.bind.workPlaceSelector.workAddressSelected = list || [];
    
  },
  // 근태 유형 전환
  clickChangeType(type) {
    this.bind.form.checkType = type;
  },
  // 근무지 선택기 열기
  async openWorkPlaceSelector() {
    this.bind.workPlaceSelectorOpen = true;
  },
  // 근무지 선택기 닫기
  closeSelectWorkPlace() {
    this.bind.workPlaceSelectorOpen = false;
  },
  // 근무지
  deleteWorkPlace(value) {
    let i = -1;
    for (let index = 0; index < this.bind.workPlaceSelector.workAddressSelected.length; index++) {
      const element = this.bind.workPlaceSelector.workAddressSelected[index];
      if (value.id === element.id) {
        i = index;
        break
      }
    }
    if (i > -1) {
      this.bind.workPlaceSelector.workAddressSelected.splice(i, 1);
    }
  },
  // 외근 허용 여부
  clickAllowFieldWork() {
    this.bind.form.allowFieldWork = !this.bind.form.allowFieldWork;
  },
  // 외근 설명 필수 여부
  clickRequiredFieldWorkRemarks() {
    this.bind.form.requiredFieldWorkRemarks = !this.bind.form.requiredFieldWorkRemarks;
  },
  clickRequiredFieldWorkPhoto() {
    this.bind.form.requiredFieldWorkPhoto = !this.bind.form.requiredFieldWorkPhoto;
  },
  // 외근을 이상 데이터로 처리할지 여부
  clickFieldWorkMarkError() {
    this.bind.form.fieldWorkMarkError = !this.bind.form.fieldWorkMarkError;
  },
  // 필수 출퇴근 체크 날짜
  loadRequiredDateSelector() {
    MWF.require("MWF.widget.Calendar", function(){
      var defaultView = "day";
      var options = {
          "style": "xform",
          "secondEnable" : false,
          "timeSelectType" : "select",
          "clearEnable": false,
          "isTime": false,
          "timeOnly": false,
          "monthOnly" : false,
          "yearOnly" : false,
          "defaultDate": null,
          "defaultView" : defaultView,
          "target":  this.dom,
          "baseDate": new Date(),
          "onComplate": function(formateDate, date){
            let year = date.getFullYear();
            let month = (date.getMonth() + 1) > 9 ? `${date.getMonth() + 1}`:`0${(date.getMonth() + 1)}`;
            let day = (date.getDate()) > 9 ? `${date.getDate()}`:`0${date.getDate()}`;
            const chooseDate = `${year}-${month}-${day}`;
            let isIn = false;
            for (let index = 0; index < this.bind.requiredCheckInDateForTableList.length; index++) {
              const element = this.bind.requiredCheckInDateForTableList[index];
              if (element.date === chooseDate) {
                isIn = true;
                break;
              }
            }
            if (!isIn) {
              const c = this.bind.dateCycleList[0]
              this.bind.requiredCheckInDateForTableList.push({
                date: chooseDate,
                cycle: c
              });
            }
          }.bind(this),
      };
      let bindDom = this.dom.querySelector("#requiredDateSelector");
      new MWF.widget.Calendar(bindDom, options);
    }.bind(this));
  },
  // 필수 출퇴근 체크 날짜 삭제
  deleteRequiredDate(date) {
    let i = -1;
    for (let index = 0; index < this.bind.requiredCheckInDateForTableList.length; index++) {
      const element = this.bind.requiredCheckInDateForTableList[index];
      if (date === element.date) {
        i = index;
        break;
      }
    }
    if (i > -1) {
      this.bind.requiredCheckInDateForTableList.splice(i, 1);
    }
  },
  showRequiredDateCycleSelector(id) {
    const selector = this.dom.querySelector("#o2-selector-"+id);
    const brother = this.dom.querySelector("#o2-required-date-"+id);
    const x = brother.getBoundingClientRect().left + document.documentElement.scrollLeft;
    const y = brother.getBoundingClientRect().top + document.documentElement.scrollTop;
    selector.style.left = x + "px";
    selector.style.top = (y+40)+"px";
    selector.style.position = "fixed";
    selector.show();
  },
  chooseDateCycleItem(date, cycle) {
    for (let index = 0; index < this.bind.requiredCheckInDateForTableList.length; index++) {
      const element = this.bind.requiredCheckInDateForTableList[index];
      if (element.date === date) {
        element.cycle = cycle;
        this.bind.requiredCheckInDateForTableList[index] = element;
      }
    }
    this.dom.querySelector("#o2-selector-"+date).hide();
  },
  // 필수 출퇴근 체크 날짜 주기 수정
  chooseDateCycleItem2(e) {
    const id = e.target.id;
    const value = e.target.value;
    const date = id.substring('o2-selector-'.length);
    const cycle = this.bind.dateCycleList.filter(c => c.value === value);
    for (let index = 0; index < this.bind.requiredCheckInDateForTableList.length; index++) {
      const element = this.bind.requiredCheckInDateForTableList[index];
      if (element.date === date) {
        element.cycle = cycle[0];
        this.bind.requiredCheckInDateForTableList[index] = element;
      }
    }
  },
  loadNoNeedDateSelector() {
    MWF.require("MWF.widget.Calendar", function(){
      var defaultView = "day";
      var options = {
          "style": "xform",
          "secondEnable" : false,
          "timeSelectType" : "select",
          "clearEnable": false,
          "isTime": false,
          "timeOnly": false,
          "monthOnly" : false,
          "yearOnly" : false,
          "defaultDate": null,
          "defaultView" : defaultView,
          "target":  this.dom,
          "baseDate": new Date(),
          "onComplate": function(formateDate, date){
            let year = date.getFullYear();
            let month = (date.getMonth() + 1) > 9 ? `${date.getMonth() + 1}`:`0${(date.getMonth() + 1)}`;
            let day = (date.getDate()) > 9 ? `${date.getDate()}`:`0${date.getDate()}`;
            const chooseDate = `${year}-${month}-${day}`;

            let isIn = false;
            for (let index = 0; index < this.bind.noNeedCheckInDateForTableList.length; index++) {
              const element = this.bind.noNeedCheckInDateForTableList[index];
              if (element.date === chooseDate) {
                isIn = true;
                break;
              }
            }
            if (!isIn) {
              const c = this.bind.dateCycleList[0]
              this.bind.noNeedCheckInDateForTableList.push({
                date: chooseDate,
                cycle: c
              });
            }
          }.bind(this),
      };
      let bindDom = this.dom.querySelector("#noNeedDateSelector");
      new MWF.widget.Calendar(bindDom, options);
    }.bind(this));
  },
   // 출퇴근 체크 불필요 날짜 주기 수정
   noNeedDateChangeCycle(e) {
    const id = e.target.id;
    const value = e.target.value;
    const date = id.substring('o2-noNeed-'.length);
    const cycle = this.bind.dateCycleList.filter(c => c.value === value);
    for (let index = 0; index < this.bind.noNeedCheckInDateForTableList.length; index++) {
      const element = this.bind.noNeedCheckInDateForTableList[index];
      if (element.date === date) {
        element.cycle = cycle[0];
        this.bind.noNeedCheckInDateForTableList[index] = element;
      }
    }
  },
  // 출퇴근 체크 불필요 날짜 삭제
  deleteNoNeedDateSelector(value) {
    let i = -1;
    for (let index = 0; index <  this.bind.noNeedCheckInDateForTableList.length; index++) {
      const element =  this.bind.noNeedCheckInDateForTableList[index];
      if (value === element.date) {
        i = index;
        break;
      }
    }
    if (i > -1) {
      this.bind.noNeedCheckInDateForTableList.splice(i, 1);
    }
  },
  // 교대 편성 전 먼저 데이터 저장
  startSchedule() {
    this.submit((result)=>{
      console.log(result);
      this.openScheduleWindow({bind: {trueParticipantList: result.trueParticipantList || [], groupId: result.id}})
    })
    
  },
  // 교대 편성 창 열기
  async openScheduleWindow(data) {
    const bind = data || {};
    console.log(bind);
    const c = (await import('../schedule/index.js')).default;
    this.scheduleVm = await c.generate("#scheduleFom", bind, this);
  },
  // 현재 창 닫기
  close() {
    this.$topParent.publishEvent('group', {});
    this.$parent.closeFormVm();
  },
  async submit(callback) {
    debugger;
    const myForm = this.bind.form;
    // 근태 그룹 이름
    if (isEmpty(myForm.groupName)) {
      o2.api.page.notice(lp.groupForm.titleErrorNotEmpty, 'error');
      return ;
    }
    // 근태 사용자
    if (myForm.participateList.length < 1) {
      o2.api.page.notice(lp.groupForm.participatesErrorNotEmtpy, 'error');
      return ;
    }
    // 근태 유형에 따라 판단
    if (myForm.checkType === "1") {
        let flag = false;
        let isBreak = false;
        let workDateProperties = {};
        let baseShiftId = "";
        for (let key in this.bind.workDay) {
          const day = this.bind.workDay[key];
          if (day.checked && (!day.shiftSelected || isEmpty(day.shiftSelected.id))) {
            isBreak = true;
            break;
          }
          if (day.checked && day.shiftSelected && !isEmpty(day.shiftSelected.id)) {
            flag = true;
            workDateProperties[key] = {
              shiftId: day.shiftSelected.id,
              checked: day.checked,
            };
            if (baseShiftId === "") {
              baseShiftId = day.shiftSelected.id;
            }
          }
          if (!day.checked) {
            workDateProperties[key] = {
              checked: false,
            };
          }
        }
        if (isBreak) {
          o2.api.page.notice(lp.groupForm.shiftErrorNotEmpty, 'error');
          return;
        }
        if (!flag) {
          o2.api.page.notice(lp.groupForm.timeErrorNotEmpty, 'error');
          return ;
        }
        myForm.workDateProperties = workDateProperties;
        myForm.shiftId = baseShiftId;
    } else if (myForm.checkType === "2") {
        // 근무 날짜 day
        if (this.bind.workDateList.length < 1) {
          o2.api.page.notice(lp.groupForm.timeErrorNotEmpty, 'error');
          return ;
        }
        myForm.workDateList = this.bind.workDateList.join(",");
    } else if (myForm.checkType === "3"){
      // 교대 편성 검증
    } else {
      o2.api.page.notice(lp.groupForm.checkTypeError, 'error');
      return;
    }
    
    // 근무 주소
    // if ( this.bind.workPlaceSelector.workAddressSelected.length < 1) {
    //   o2.api.page.notice(lp.groupForm.workPlaceErrorNotEmpty, 'error');
    //   return ;
    // }
    let workPlaceIdList = [];
    for (let index = 0; index < this.bind.workPlaceSelector.workAddressSelected.length; index++) {
      const element = this.bind.workPlaceSelector.workAddressSelected[index];
      workPlaceIdList.push(element.id);
    }
    myForm.workPlaceIdList = workPlaceIdList;
    // 필수 출퇴근 체크 날짜
    if (myForm.checkType === "1") { // 고정 근무제는 반드시 교대조 설정 필요
      const rCheckInDateList = this.bind.requiredCheckInDateForTableList.map((d) => {
        const date = d.date;
        const shiftId = (d.shift && d.shift.id) ? d.shift.id : "";
        const cycle = (d.cycle && d.cycle.value) ?  d.cycle.value :  "";
        if (shiftId === "") {
          return "";
        }
        return date+"|"+shiftId+"|"+cycle;
      }).filter(str => !!str);
      
      if (this.bind.requiredCheckInDateForTableList.length > 0 && rCheckInDateList.length < 1) {
        o2.api.page.notice("필수 체크 날짜에는 근무조를 설정해야 합니다!", 'error');
        return;
      }
      myForm.requiredCheckInDateList = rCheckInDateList;
    } else if (myForm.checkType === "2") { // 자율 근무제는 교대조 불필요
      const rCheckInDateList = this.bind.requiredCheckInDateForTableList.map((d) => {
        const date = d.date;
        const shiftId =  "";
        const cycle = (d.cycle && d.cycle.value) ?  d.cycle.value :  "";
        return date+"|"+shiftId+"|"+cycle;
      });
      myForm.requiredCheckInDateList = rCheckInDateList;
     }

    // 출퇴근 체크 불필요 날짜
    const noNeedCheckInDateList = this.bind.noNeedCheckInDateForTableList.map((d) => {
      const date = d.date;
      const cycle = (d.cycle && d.cycle.value) ?  d.cycle.value :  "";
      return date+"|"+cycle;
    });
    myForm.noNeedCheckInDateList = noNeedCheckInDateList;
    if (callback && callback instanceof Function && myForm.status  !== 1) {
      myForm.status = 2;
    } else {
      myForm.status = 1;
    }

    const result = await groupAction("createOrUpdate", myForm);
    console.log(result);
    if (callback && callback instanceof Function) {
      this.bind.form.id = result.id;
      callback(result);
    } else {
      o2.api.page.notice(lp.saveSuccess, 'success');
      this.close();
    }
    
  },
});
