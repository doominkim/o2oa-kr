import { component as content } from "@o2oa/oovm";
import { lp, o2 } from "@o2oa/component";
import { lpFormat } from "../../utils/common";
import { groupActionListByPaging, groupAction } from "../../utils/actions";
import oPager from "../../components/o-pager";
import template from "./template.html";

export default content({
  template,
  components: { oPager },
  autoUpdate: true,
  bind() {
    return {
      lp,
      groupList: [],
      pagerData: {
        page: 1,
        totalCount: 0,
        totalPage: 1,
        size: 15, // 페이지당 항목 수
      },
    };
  },
  afterRender() {
    this.loadGroupList();
    this.listenEventBus();
  },
  listenEventBus() {
    this.$topParent.listenEventBus("group", (data) => {
      console.log("group 메시지 수신", data);
      this.loadGroupList();
    });
  },
  // 교대조 정보 표시
  formatAttendanceTime(group) {
    if (!group) {
      return "";
    }
    if (group.checkType === "1" && group.workDateProperties) {
      // 고정 근무제면 월요일 또는 화요일 교대조 정보 표시
      let map = {};
      let restList = [];
      for (let key in group.workDateProperties) {
        const day = group.workDateProperties[key];
        const dayLp = this.day2Lang(key);
        if (dayLp === "") {
          continue;
        }
        if (day.checked) {
          if (map[day.shiftId]) {
            map[day.shiftId]["dayList"].push(dayLp);
          } else {
            map[day.shiftId] = {
              dayList: [dayLp],
              shift: day.shift,
            };
          }
        } else {
          restList.push(dayLp);
        }
      }
      let retStr = "";
      for (let id in map) {
        retStr +=
          map[id].dayList.join("、") +
          " " +
          map[id].shift.shiftName +
          " (" +
          map[id].shift.properties.timeList[0].onDutyTime +
          " - " +
          map[id].shift.properties.timeList[
            map[id].shift.properties.timeList.length - 1
          ].offDutyTime +
          ") | ";
      }
      if (restList.length > 0) {
        retStr += restList.join("、") + " " + lp.groupForm.shiftEmpty;
      } else {
        retStr = retStr.substring(0, retStr.length - 2);
      }
      return retStr;
    } else if (group.checkType === "2") {
      // 자율 근무제면 바로 표시
      const dayList = group.workDateList.split(",").map((d) => {
        switch (d) {
          case "1":
            return lp.day.Monday;
          case "2":
            return lp.day.Tuesday;
          case "3":
            return lp.day.Wednesday;
          case "4":
            return lp.day.Thursday;
          case "5":
            return lp.day.Friday;
          case "6":
            return lp.day.Saturday;
          case "0":
            return lp.day.Sunday;
        }
        return "";
      });
      return dayList.join("、") + " " + lp.groupForm.checkTypeFree;
    } else if (group.checkType === "3") {
      return lp.groupForm.checkTypeArrangement;
    }
    return "";
  },
  day2Lang(day) {
    switch (day) {
      case "monday":
        return lp.day.Monday;
      case "tuesday":
        return lp.day.Tuesday;
      case "wednesday":
        return lp.day.Wednesday;
      case "thursday":
        return lp.day.Thursday;
      case "friday":
        return lp.day.Friday;
      case "saturday":
        return lp.day.Saturday;
      case "sunday":
        return lp.day.Sunday;
    }
    return "";
  },
  loadData(e) {
    if (e && e.detail && e.detail.module && e.detail.module.bind) {
      this.bind.pagerData.page = e.detail.module.bind.page || 1;
      this.loadGroupList();
    }
  },
  async loadGroupList() {
    const json = await groupActionListByPaging(
      this.bind.pagerData.page,
      this.bind.pagerData.size,
      {}
    );
    if (json) {
      this.bind.groupList = json.data || [];
      const count = json.count || 0;
      this.bind.pagerData.totalCount = count;
    }
  },
  // 추가
  async addGroup() {
    // const content = (await import(`./editGroup/index.js`)).default;
    // this.addGroupVm = await content.generate(".form", {}, this);
    this.$parent.openGroupForm({});
  },
  // 수정
  async clickEditGroup(id) {
    const group = this.bind.groupList.find((g) => g.id === id);
    if (group) {
      // const content = (await import(`./editGroup/index.js`)).default;
      // this.addGroupVm = await content.generate(".form", {bind: {updateId: group.id}}, this);
      this.$parent.openGroupForm({ bind: { updateId: group.id } });
    }
  },
  // 삭제
  async clickDeleteGroup(id, name) {
    var _self = this;
    const c = lpFormat(lp, "groupForm.confirmDelete", { name: name });
    o2.api.page.confirm(
      "warn",
      lp.alert,
      c,
      300,
      100,
      function () {
        _self.deleteGroup(id);
        this.close();
      },
      function () {
        this.close();
      }
    );
  },
  async deleteGroup(id) {
    this.$topParent.closeFormVm(); // 양식 닫기
    const json = await groupAction("delete", id);
    console.debug(json);
    this.loadGroupList();
  },
  // 클릭 이벤트: 교대 편성 편집 열기
  async clickEditSchedule(group) {
    if (!group || !group.id) {
      console.log('데이터에 이상이 있습니다!');
      return ;
    }
    this.$topParent.closeFormVm(); // 양식 닫기
    const result = await groupAction("get", group.id);
    if (result) {
      const data = {
        bind: {
          trueParticipantList: result.trueParticipantList || [],
          groupId: result.id,
        },
      };
      this.openScheduleWindow(data);
    }
  },
  // 교대 편성 화면 열기
  async openScheduleWindow(bind) {
    if (!bind || !bind.bind || !bind.bind.trueParticipantList || !bind.bind.groupId) {
      console.log('데이터에 이상이 있습니다!');
      return ;
    }
    const c = (await import("./schedule/index.js")).default;
    this.scheduleVm = await c.generate("#scheduleFom", bind, this);
  },

  // 양식 페이지 닫기
  closeGroup() {
    if (this.addGroupVm) {
      this.addGroupVm.destroy();
    }
    this.loadGroupList();
  },
});
