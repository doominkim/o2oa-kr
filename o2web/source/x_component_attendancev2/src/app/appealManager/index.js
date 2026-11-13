import { component as content } from "@o2oa/oovm";
import { lp, component as app } from "@o2oa/component";
import { appealInfoActionListByPaging, configAction, appealInfoAction, processAction, jobAction } from "../../utils/actions";
import { fieldWorkFormat } from "../../utils/common";
import oPager from "../../components/o-pager";
import template from "./template.html";

export default content({
  template,
  components: { oPager },
  autoUpdate: true,
  bind() {
    return {
      lp,
      appealList: [],
      pagerData: {
        page: 1,
        totalCount: 0,
        totalPage: 1,
        size: 15, // 페이지당 항목 수
      },
      appealEnable: false, // 설정 파일 읽기: 이의 신청 기능 사용 여부
    };
  },
  afterRender() {
    this.loadImConfig();
  },
  loadData(e) {
    if (
      e &&
      e.detail &&
      e.detail.module &&
      e.detail.module.bind
    ) {
      this.bind.pagerData.page = e.detail.module.bind.page || 1;
      this.loadAppealList();
    }
  },
  async loadImConfig() {
    // 쿼리 설정
    this.imConfig = await configAction("get");
    this.bind.appealEnable = (this.imConfig && this.imConfig.appealEnable && this.imConfig.processId)
    await this.loadAppealList();
  },
  async loadAppealList() {
    const json = await appealInfoActionListByPaging(
      this.bind.pagerData.page,
      this.bind.pagerData.size,
      {}
    );
    if (json) {
      this.bind.appealList = json.data || [];
      const count = json.count || 0;
      this.bind.pagerData.totalCount = count;
    }
  },
  formatRecordResultClass(record) {
    let span = "";
    if (record.fieldWork) {
      span = "color-fieldWork";
    } else {
      const result = record.checkInResult;
      if (result === 'PreCheckIn') {
        span = "";
      } else if (result === 'NotSigned') {
        span =   "color-nosign";
      }  else if (result === 'Normal') {
        span =   "color-normal";
      } else if (result === 'Early') {
        span =    "color-early";
      } else if (result === 'Late') {
        span =    "color-late";
      } else if (result === 'SeriousLate') {
        span =    "color-serilate";
      } else { 
        span = "" ;
      }
    }
    return span;
  },
  formatRecordResult(record) {
    let span = "";
    if (record.fieldWork) {
      span = fieldWorkFormat(record);
    } else {
      const result = record.checkInResult;
      if (result === 'PreCheckIn') {
        span = "";
      } else if (result === 'NotSigned') {
        span =  lp.appeal.notSigned;
      }  else if (result === 'Normal') {
        span =   lp.appeal.normal;
      } else if (result === 'Early') {
        span =   lp.appeal.early;
      } else if (result === 'Late') {
        span =   lp.appeal.late;
      } else if (result === 'SeriousLate') {
        span =   lp.appeal.seriousLate;
      } else { 
        span = "" ;
      }
    }
    return span;
  },
  formatAppealStatus(appeal) {
    if (appeal) {
      if (appeal.status === 0) {
        return  lp.appeal.status0;
      } else if (appeal.status=== 1) {
        return  lp.appeal.status1;
      } else if (appeal.status === 2) {
        return lp.appeal.status2;
      } else if (appeal.status === 3) {
        return lp.appeal.status3;
      } else if (appeal.status === 4) {
        let name = appeal.updateStatusAdminPerson;
        if (name && name.indexOf("@") > -1) {
          name =  name.split("@")[0];
        }
        return lp.appeal.status4 + " ["+ name + "]";
      } else if (appeal.status === 5) {
        return lp.appeal.status5;
      }
    }
    return "";
  },
  async startProcess(appeal) {
    if (this.bind.appealEnable ) {
      // 이의 신청 가능 여부 확인(제한 횟수 초과 가능성)
      const checkResult = await appealInfoAction("startCheck", appeal.id);
      if (checkResult && this.imConfig.processId) {
        const process = await processAction("get", this.imConfig.processId);
        MWF.xDesktop.requireApp("process.TaskCenter", "ProcessStarter", function(){
          var starter = new MWF.xApplication.process.TaskCenter.ProcessStarter(process, app, {
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
    var currentTask = [];
    var jobIds = [];
    data.each(function(work){
        if (work.currentTaskIndex > -1) {
          jobIds.push( work.job || "" );
          currentTask.push(work.taskList[work.currentTaskIndex].work);
        }
    });
    // 프로세스 업무 문서 열기
    if (currentTask.length > 0){
        o2.api.page.openWork(currentTask[0]);
    }
    // 데이터 업데이트
    var body = {
      job: (jobIds[0] || "")
    };
    const json = await appealInfoAction("startProcess", id, body);
    console.debug('업데이트했습니다', json);
    this.loadAppealList();
  },
  // 프로세스 업무 문서 열기
  async openJob(jobId, id) {
    if (jobId) {
      // 먼저 업무 문서 존재 여부 조회(생성 확인 후 결재 대기 업무 문서가 이미 삭제됐을 수 있음)
      const result = await jobAction("findWorkWorkCompleted", jobId);
      var workList = result.workList || [];
      var workCompletedList =  result.workCompletedList || [];
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
    this.loadAppealList();
  },
  // 양식 페이지 닫기
  closeGroup() {
    // if (this.addGroupVm) {
    //   this.addGroupVm.destroy();
    // }
    this.loadAppealList();
  },
});
