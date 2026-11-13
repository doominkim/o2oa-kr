import {component as content} from '@o2oa/oovm';
import {lp, component, component as app} from '@o2oa/component';
import template from './template.html';
import {
    appealInfoAction,
    appealInfoActionListByPaging, configAction,
    jobAction,
    myAction,
    processAction
} from "../../../utils/actions";
import {fieldWorkFormat} from "../../../utils/common";


export default content({
    template,
    autoUpdate: true,
    components: {
    },
    beforeRender() {
        this.loadConfig()
    },
    bind(){
        return {
            lp,
            defaultDate: new Date(),
            appealEnable: false, // 설정 파일 읽기: 이의 신청 기능 사용 여부
            statistic: {},
            detailList: [],
            recordList: []
        };
    },
    async loadConfig() {
        // 쿼리 설정
        this.imConfig = await configAction("get");
        this.bind.appealEnable = (this.imConfig && this.imConfig.appealEnable && this.imConfig.processId)
    },
    // 달력 날짜 클릭
    handleDateChange(e) {
        this.loadRecordListWithAppealData(e.detail.value)
    },
    async loadRecordListWithAppealData(clickDate) {
        this.bind.recordList = []
        this.clickDate = clickDate;
        const reqBody = {
            startDate: clickDate,
            endDate: clickDate,
        };
        const detailList = (await myAction("listDetailWithDate", reqBody));
        if (detailList && detailList.length > 0) {
            const detail = detailList[0];
            if (detail && detail.recordList && detail.recordList.length > 0) {
                const recordList = detail.recordList;
                const newRecordList = []
                for (let i = 0, len = recordList.length; i < len; i++) {
                    const record = recordList[i];
                    const json = await appealInfoActionListByPaging(
                        1,
                        1,
                        {recordId: record.id}
                    );
                    if (json) {
                        const appealList = json.data || [];
                        if (appealList && appealList.length > 0) {
                            record.appealData = appealList[0];
                        }
                    }
                    newRecordList.push(record);
                }
                this.bind.recordList = newRecordList;
            }
        }
    },

    // 캘린더 표시 전환
    handleViewChange( e) {
        if (e && e.detail && e.detail.view === 'date') {
            const calendar = this.dom.querySelector('#mobileAttendanceCalendar');
            // 빨간 점 지우기
            const els = calendar.querySelectorAll('.calendar-point');
            els.forEach(el=>el.destroy());
            this.bind.detailList = [];
            this.loadAttendanceData(`${e.detail.range[0]}`, `${e.detail.range[1]}`);
        }
    },
    async loadAttendanceStatistics(reqBody) {
        const statistic = (await myAction("statistic", reqBody));
        if (statistic) {
            this.bind.statistic = statistic;
        }
    },
    async loadAttendanceData(startDate, endDate) {
        const reqBody = {
            startDate: startDate,
            endDate: endDate,
        };
        // 통계 데이터
        this.loadAttendanceStatistics(reqBody);
        // 캘린더 데이터
        const detailList = (await myAction("listDetailWithDate", reqBody));
        if (detailList && detailList.length > 0) {
            this.bind.detailList = detailList;
            const calendar = this.dom.querySelector('#mobileAttendanceCalendar');
            for (let i = 0; i < detailList.length; i++) {
                const detail = detailList[i];
                if (detail.recordList && detail.recordList.length > 0) {
                    const flat = calendar.querySelector('div[slot="' + detail.recordDateString + '"]');
                    if (!flat) {
                        const point = document.createElement('div');
                        point.classList.add('ooicon-pentagram_fill', 'calendar-point');
                        point.setAttribute('slot', detail.recordDateString);
                        calendar.appendChild(point);
                    }
                }
            }
        }
    },
    formatRecordResultClass(record) {
        let classname = "calendar-item-flag";
        if (record.fieldWork) {
            classname = "calendar-item-flag bgcolor-fieldWork";
        } else {
            const result = record.checkInResult;
            if (result === "PreCheckIn") {
                classname = "calendar-item-flag ";
            } else if (result === "NotSigned") {
                classname = "calendar-item-flag bgcolor-nosign";
            } else if (result === "Normal") {
                classname = "calendar-item-flag bgcolor-normal";
            } else if (result === "Early") {
                classname = "calendar-item-flag bgcolor-early";
            } else if (result === "Late") {
                classname = "calendar-item-flag bgcolor-late";
            } else if (result === "SeriousLate") {
                classname = "calendar-item-flag bgcolor-serilate";
            } else {
                classname = "calendar-item-flag ";
            }
        }
        return classname;
    },
    formatRecordResult(record) {
        let span = "";
        if (record.fieldWork) {
            span = fieldWorkFormat(record);
        } else {
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
    formatAppealStatus(appeal) {
        if (appeal) {
            if (appeal.status === 1) {
                return  `(${lp.appeal.status1})`;
            } else if (appeal.status === 2) {
                return `(${lp.appeal.status2})`;
            } else if (appeal.status === 3) {
                return `(${lp.appeal.status3})`;
            } else if (appeal.status === 4) {
                let name = appeal.updateStatusAdminPerson;
                if (name && name.indexOf("@") > -1) {
                    name =  name.split("@")[0];
                }
                return "(" + lp.appeal.status4 + " ["+ name + "])";
            }
        }
        return ''
    },
    // 프로세스 시작
    async startProcess(appeal) {
        if (this.bind.appealEnable ) {
            // 이의 신청 가능 여부 확인(제한 횟수 초과 가능성)
            const checkResult = await appealInfoAction("startCheck", appeal.id);
            if (checkResult && this.imConfig.processId) {
                const process = await processAction("get", this.imConfig.processId);
                MWF.xDesktop.requireApp("process.TaskCenter", "ProcessStarter", function(){
                    const starter = new MWF.xApplication.process.TaskCenter.ProcessStarter(process, app, {
                        "latest" : false,
                        "workData" : { "appealId": appeal.id, "record": appeal.record }, // id와 출퇴근 기록을 프로세스로 전달
                        "onStarted": function(data, title, processName){
                            this._afterStartProcess(data, appeal.id);
                        }.bind(this)
                    });
                    starter.load();
                }.bind(this));
            }
        } else {
            o2.api.page.notice(lp.appeal.startProcessNoConfigError, 'error');
        }
    },
    // 프로세스 시작 후 업무 문서를 열고 데이터 업데이트
    async _afterStartProcess(data, id){
        const currentTask = [];
        const jobIds = [];
        data.each(function(work){
            if (work.currentTaskIndex > -1) {
                jobIds.push( work.job || "" );
                currentTask.push(work.taskList[work.currentTaskIndex].work);
            }
        });
        // 데이터 업데이트
        const body = {
            job: (jobIds[0] || "")
        };
        const json = await appealInfoAction("startProcess", id, body);
        console.debug('업데이트했습니다', json);
        this.refreshClickDateRecordList();
        // 프로세스 업무 문서 열기, 모바일 페이지는 이동하므로 마지막에 열기
        if (currentTask.length > 0){
            o2.api.page.openWork(currentTask[0]);
        }
    },
    // 프로세스 업무 문서 열기
    async openJob(jobId, id) {
        if (jobId) {
            // 먼저 업무 문서 존재 여부 조회(생성 확인 후 결재 대기 업무 문서가 이미 삭제됐을 수 있음)
            const result = await jobAction("findWorkWorkCompleted", jobId);
            const workList = result.workList || [];
            const workCompletedList =  result.workCompletedList || [];
            if ((workList.length + workCompletedList.length) > 0 ) {
                o2.api.page.openJob(jobId);
            } else { // work 없음, 이미 삭제되었는지?
                var _self = this;
                o2.api.page.confirm(
                    "warn",
                    lp.alert,
                    lp.appeal.notfoundJobError,
                    300,
                    100,
                    function () {
                        _self.resetStatus(id);
                        this.close();
                    },
                    function () {
                        this.close();
                    }
                );
            }
        }
    },
    // 데이터 상태 복원
    async resetStatus(id) {
        const json = await appealInfoAction("resetStatus", id);
        console.debug('상태를 복원했습니다', json);
        this.refreshClickDateRecordList();
    },
    refreshClickDateRecordList() {
        if (this.clickDate) {
            this.loadRecordListWithAppealData(this.clickDate)
        }
    }

});
