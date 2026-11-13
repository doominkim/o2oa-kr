import {component as content} from '@o2oa/oovm';
import {lp, o2} from '@o2oa/component';
import { isPositiveInt, isEmpty, convertMinutesToHoursAndMinutes } from '../../../utils/common';
import { attendanceShiftAction } from "../../../utils/actions";
import template from './template.html';
import style from "./style.scope.css";
import oInput from '../../../components/o-input';
import oTimePicker from '../../../components/o-time-picker';
import oTimeMinutesSelector from '../../../components/o-time-minutes-selector';
import { setJSONValue } from '../../../utils/common';


export default content({
    template,
    style,
    components: {oInput, oTimePicker, oTimeMinutesSelector},
    autoUpdate: true,
    bind(){
        return {
            lp,
            fTitle: lp.addShift,
            form: {
                shiftName: "", //근무조 이름
                seriousTardinessLateMinutes: "0", // 심각한 지각 분 수
                absenteeismLateMinutes: "0", // 결근 처리 지각 분 수
                lateAndEarlyOnTime: "", // 출근 최대 허용 지각 시간
                lateAndEarlyOffTime: "", // 퇴근 최대 허용 조기 퇴근 시간
                workTime: 0, // 근무 시간(분).
                needLimitWorkTime: true, // 근무 시간 부족 시 조퇴 처리 여부.
            },
            timeType: 1,  // 출퇴근 체크 횟수 1:하루 2회 2:하루 4회 3:하루 6회
            time1: {
                onDutyTime: "",
                onDutyTimeBeforeLimit: "",
                onDutyTimeAfterLimit: "",
                offDutyTime: "",
                offDutyTimeBeforeLimit: "",
                offDutyTimeAfterLimit: "",
                offDutyNextDay: false,
                restPeriod: "", // 휴식 시간대, 형식은 "HH:mm-HH:mm", 예: "12:00-13:00"은 점심 휴식 시간이 12:00부터 13:00까지임을 의미
                restPeriodStart: "",
                restPeriodEnd: ""
            },
            time2: {
                onDutyTime: "",
                onDutyTimeBeforeLimit: "",
                onDutyTimeAfterLimit: "",
                offDutyTime: "",
                offDutyTimeBeforeLimit: "",
                offDutyTimeAfterLimit: "",
                offDutyNextDay: false,
                restPeriod: "", // 휴식 시간대, 형식은 "HH:mm-HH:mm", 예: "12:00-13:00"은 점심 휴식 시간이 12:00부터 13:00까지임을 의미
                restPeriodStart: "",
                restPeriodEnd: ""
            },
            time3: {
                onDutyTime: "",
                onDutyTimeBeforeLimit: "",
                onDutyTimeAfterLimit: "",
                offDutyTime: "",
                offDutyTimeBeforeLimit: "",
                offDutyTimeAfterLimit: "",
                offDutyNextDay: false,
                restPeriod: "", // 휴식 시간대, 형식은 "HH:mm-HH:mm", 예: "12:00-13:00"은 점심 휴식 시간이 12:00부터 13:00까지임을 의미
                restPeriodStart: "",
                restPeriodEnd: ""
            },
            typeList: [
                {
                    name: lp.shiftForm.oneTimeDayLabel,
                    value: 1
                },
                {
                    name: lp.shiftForm.secondTimeDayLabel,
                    value: 2
                }, {
                    name: lp.shiftForm.thirdTimeDayLabel,
                    value: 3
                }
            ]
        };
    },
    afterRender() {
        if (this.bind.form && this.bind.form.id && this.bind.form.id !== '') {
            this.bind.fTitle = lp.editShift;
        }
        this.initRestPeriodFields(this.bind.time1);
        this.initRestPeriodFields(this.bind.time2);
        this.initRestPeriodFields(this.bind.time3);
        this.calWorkTime();
        console.log(this.bind)
    },
    clickChangeLimitWorkTime(){
        this.bind.form.needLimitWorkTime = !this.bind.form.needLimitWorkTime;
    },
    clickChangeOffDutyNextDay(timeType) {
        if (timeType === 1) {
            this.bind.time1.offDutyNextDay = !this.bind.time1.offDutyNextDay;
        } else if (timeType === 2) {
            this.bind.time2.offDutyNextDay = !this.bind.time2.offDutyNextDay;
        }  else if (timeType === 3) {
            this.bind.time3.offDutyNextDay = !this.bind.time3.offDutyNextDay;
        }
        this.calWorkTime();
    },
    // 프론트엔드 표시
    formatWorkTime(workTime) {
        return convertMinutesToHoursAndMinutes(workTime);
    },
    // workTime 계산
    calWorkTime() {
        let workTime = 0;
        if (this.bind.timeType === 1) {
            workTime = this._calTimeMinute(this.bind.time1);
        } else if (this.bind.timeType === 2) {
            this.bind.time1.offDutyNextDay = false;// 마지막 퇴근만 날짜를 넘길 수 있음
            workTime += this._calTimeMinute(this.bind.time1);
            workTime += this._calTimeMinute(this.bind.time2);
        } else if (this.bind.timeType === 3) {
            this.bind.time1.offDutyNextDay = false;// 마지막 퇴근만 날짜를 넘길 수 있음
            this.bind.time2.offDutyNextDay = false;// 마지막 퇴근만 날짜를 넘길 수 있음
            workTime += this._calTimeMinute(this.bind.time1);
            workTime += this._calTimeMinute(this.bind.time2);
            workTime += this._calTimeMinute(this.bind.time3);
        }
        this.bind.form.workTime = workTime;
    },
    _calTimeMinute(time) {
        let workTime = 0;
        const onDutyTime = time.onDutyTime.split(":");
        const offDutyTime = time.offDutyTime.split(":");
        let onDutyDate = new Date();
        let offDutyDate = new Date();
        if (onDutyTime && onDutyTime.length > 1 && offDutyTime && offDutyTime.length > 1) {
            const onDutyTimehour = parseInt(onDutyTime[0]);
            const onDutyTimeminute = parseInt(onDutyTime[1]);
            onDutyDate.setHours(onDutyTimehour);
            onDutyDate.setMinutes(onDutyTimeminute);
            const offDutyTimehour = parseInt(offDutyTime[0]);
            const offDutyTimeminute = parseInt(offDutyTime[1]);
            offDutyDate.setHours(offDutyTimehour);
            offDutyDate.setMinutes(offDutyTimeminute);
            if (time.offDutyNextDay) {
                // 1일 추가
                offDutyDate.setDate(offDutyDate.getDate() + 1);
            }
            workTime = (offDutyDate.getTime() - onDutyDate.getTime()) / 1000 / 60; // 분 수
        }
        return workTime;
    },
    initRestPeriodFields(time) {
        if (!time) {
            return;
        }
        const restPeriod = this.parseRestPeriod(time.restPeriod);
        time.restPeriodStart = time.restPeriodStart || restPeriod.start;
        time.restPeriodEnd = time.restPeriodEnd || restPeriod.end;
    },
    parseRestPeriod(restPeriod) {
        if (!restPeriod || !restPeriod.includes("-")) {
            return {start: "", end: ""};
        }
        const list = restPeriod.split("-");
        return {
            start: list[0] || "",
            end: list[1] || ""
        };
    },
    setRestPeriodValue(key, value) {
        setJSONValue(key, value, this.bind);
        const keyList = key.split(".");
        const time = this.bind[keyList[0]];
        if (!time) {
            return;
        }
        time.restPeriod = time.restPeriodStart && time.restPeriodEnd ? `${time.restPeriodStart}-${time.restPeriodEnd}` : "";
    },
    restPeriodNeedComplete(time) {
        return time && ((time.restPeriodStart && !time.restPeriodEnd) || (!time.restPeriodStart && time.restPeriodEnd));
    },
    buildTimeForSubmit(time) {
        const result = Object.assign({}, time);
        result.restPeriod = result.restPeriodStart && result.restPeriodEnd ? `${result.restPeriodStart}-${result.restPeriodEnd}` : "";
        delete result.restPeriodStart;
        delete result.restPeriodEnd;
        return result;
    },
    // o time picker 컨트롤 사용
    setTimeValue(key, value) {
        if (key && (key.endsWith(".restPeriodStart") || key.endsWith(".restPeriodEnd"))) {
            this.setRestPeriodValue(key, value);
        } else {
            setJSONValue(key, value, this.bind);
        }
        this.calWorkTime();
    },
    // o time minute selector 컨트롤 반환 결과 사용
    setSelectorValue(key, value) {
        setJSONValue(key, value, this.bind);
    },
    closeShift() {
        this.$topParent.publishEvent('shift', {});
        this.$parent.closeFormVm();
    },
    async submitAdd() {
        if (isEmpty(this.bind.form.shiftName)) {
            o2.api.page.notice(lp.shiftForm.shiftNameNotEmpty, 'error');
            return ;
        }
        let myForm = this.bind.form;
        myForm.properties = {
            timeList: []
        };
        if (this.bind.timeType === 1) {
            if (isEmpty(this.bind.time1.onDutyTime)) {
                o2.api.page.notice(lp.shiftForm.onDutyTimeNotEmpty, 'error');
                return ;
            }
            if (isEmpty(this.bind.time1.offDutyTime)) {
                o2.api.page.notice(lp.shiftForm.offDutyTimeNotEmpty, 'error');
                return ;
            }
            if (this.restPeriodNeedComplete(this.bind.time1)) {
                o2.api.page.notice(lp.shiftForm.restPeriodNeedComplete, 'error');
                return ;
            }
            myForm.properties.timeList.push(this.buildTimeForSubmit(this.bind.time1));
        } else if (this.bind.timeType === 2) {
            if (isEmpty(this.bind.time1.onDutyTime) || isEmpty(this.bind.time2.onDutyTime)) {
                o2.api.page.notice(lp.shiftForm.onDutyTimeNotEmpty, 'error');
                return ;
            }
            if (isEmpty(this.bind.time1.offDutyTime) || isEmpty(this.bind.time2.offDutyTime)) {
                o2.api.page.notice(lp.shiftForm.offDutyTimeNotEmpty, 'error');
                return ;
            }
            if (this.restPeriodNeedComplete(this.bind.time1) || this.restPeriodNeedComplete(this.bind.time2)) {
                o2.api.page.notice(lp.shiftForm.restPeriodNeedComplete, 'error');
                return ;
            }
            this.bind.time1.offDutyNextDay = false; // 마지막 퇴근만 날짜를 넘길 수 있음
            myForm.properties.timeList.push(this.buildTimeForSubmit(this.bind.time1));
            myForm.properties.timeList.push(this.buildTimeForSubmit(this.bind.time2));
        } else if (this.bind.timeType === 3) {
            if (isEmpty(this.bind.time1.onDutyTime) || isEmpty(this.bind.time2.onDutyTime) || isEmpty(this.bind.time3.onDutyTime)) {
                o2.api.page.notice(lp.shiftForm.onDutyTimeNotEmpty, 'error');
                return ;
            }
            if (isEmpty(this.bind.time1.offDutyTime ) || isEmpty(this.bind.time2.offDutyTime) || isEmpty(this.bind.time3.offDutyTime)) {
                o2.api.page.notice(lp.shiftForm.offDutyTimeNotEmpty, 'error');
                return ;
            }
            if (this.restPeriodNeedComplete(this.bind.time1) || this.restPeriodNeedComplete(this.bind.time2) || this.restPeriodNeedComplete(this.bind.time3)) {
                o2.api.page.notice(lp.shiftForm.restPeriodNeedComplete, 'error');
                return ;
            }
            this.bind.time1.offDutyNextDay = false;// 마지막 퇴근만 날짜를 넘길 수 있음
            this.bind.time2.offDutyNextDay = false;// 마지막 퇴근만 날짜를 넘길 수 있음
            myForm.properties.timeList.push(this.buildTimeForSubmit(this.bind.time1));
            myForm.properties.timeList.push(this.buildTimeForSubmit(this.bind.time2));
            myForm.properties.timeList.push(this.buildTimeForSubmit(this.bind.time3));
        }
        debugger
        if (myForm.seriousTardinessLateMinutes !== "0" && !isPositiveInt(myForm.seriousTardinessLateMinutes)) {
            o2.api.page.notice(lp.shiftForm.seriousTardinessLateMinutesNeedNumber, 'error');
            return ;
        }
        if (myForm.absenteeismLateMinutes !== "0" && !isPositiveInt(myForm.absenteeismLateMinutes)) {
            o2.api.page.notice(lp.shiftForm.absenteeismLateMinutesNeedNumber, 'error');
            return ;
        }
        if (myForm.id && myForm.id !== '') {
            const json = await attendanceShiftAction("shiftUpdate", myForm);
            console.debug('업데이트했습니다', json);
        } else {
            const json = await attendanceShiftAction("shiftCreate", myForm);
            console.debug('추가했습니다', json);
        }
        o2.api.page.notice(lp.shiftForm.success, 'success');
        this.closeShift();
    },
   
});
