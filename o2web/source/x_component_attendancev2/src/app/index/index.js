import { component as content } from "@o2oa/oovm";
import { lp, o2 } from "@o2oa/component";
import { myAction } from "../../utils/actions";
import { EventBus } from "../../utils/eventBus";
import template from "./template.html";
import style from "./style.scope.css";

export default content({
  style,
  template,
  autoUpdate: true,
  components: {
    appContainer: {
      watch: ["menu.currentMenu"],
      async load() {
        const name = this.bind.menu.currentMenu.action;
        return (await import(`../${name}/index.js`)).default;
      },
    },
  },
  bind() {
    return {
      lp,
      admin: "", // admin readAdmin
      menu: {
        currentMenu: {}, // 현재 메뉴
        menuData: [],
      },
    };
  },
  async beforeRender() {
    await this.loadCurrentPersonInfo();
    const menu = this.getCurrentPersonMenu();
    this.bind.menu.menuData = menu;
    if (menu.length) {
      this.bind.menu.currentMenu = menu[0].children[0];
    }
    this._initEventBus()
    console.debug('attendancev2 beforeRender .......');
  },
  async afterRender() {
    this._initOONav()
  },
  startOpenMenu(action) {
    if (!action) return;
    for (let index = 0; index < this.bind.menu.menuData.length; index++) {
      const element = this.bind.menu.menuData[index];
      for (let j = 0; j < element.children.length; j++) {
        const child = element.children[j];
        if (child.action === action) {
          this.bind.menu.currentMenu = child;
          break;
        }
      }
    }
    setTimeout(() => {
      const ooNav = document.querySelector('#app-attendance-v2-menu');
      const item = ooNav.getItem(this.bind.menu.currentMenu.name);
      item.select();
    });
    console.debug('attendancev2 startOpenMenu .......');
  },
  // EventBus 초기화
  _initEventBus() {
    this.eventBus = new EventBus();
  },
  _initOONav() {
    const ooNav = document.querySelector('#app-attendance-v2-menu')
    ooNav.addEventListener('select', (e) => {
      this.bind.menu.currentMenu = e.detail.data;
      this.closeFormVm(); // form 창 닫기
    })
    ooNav.setMenu(this.bind.menu.menuData)

  },
  // 리스너 추가
  listenEventBus(eventName, callback) {
    this.eventBus.subscribe(eventName, callback);
  },
  // 기존 휴가 데이터 조회 페이지
  openOldLeaveManager() {
    const menu = {
      "name": "3-4",
      "text": lp.menu.leavemanager,
      "action": "leaveManager",
      "icon": "ooicon-clock"
    };
    this.bind.menu.currentMenu = menu;
  },
  openLeaveManagerV2() {
    const menu = {
      "name": "3-42",
      "text": lp.menu.leavemanagerv2,
      "action": "leaveManagerV2",
      "icon": "ooicon-clock"
    };
    this.bind.menu.currentMenu = menu;
  },
  // 이벤트 발송
  publishEvent(eventName, data) {
    this.eventBus.publish(eventName, data);
  },
  // 출퇴근 체크 기록 상세 열기
  async openRecordDetailVm(bind) {
    this.closeFormVm();
    const bindData = bind || {};
    const c = (await import('../recordManager/detail/index.js')).default;
    this.openFomVm(c, bindData);
  },
  // 원본 기록 열기
  async openRecordListVm(bind) {
    this.closeFormVm();
    const bindData = bind || {};
    const c = (await import('../detailManager/recordList/index.js')).default;
    this.openFomVm(c, bindData);
  },
  // 출퇴근 체크 장소 양식 열기
  async openBDMapConfigForm(bind) {
    this.closeFormVm();
    const bindData = bind || {};
    const c = (await import('../addressManager/bdAkConfig/index.js')).default;
    this.openFomVm(c, bindData);
  },
  // 출퇴근 체크 장소 양식 열기
  async openAddressForm(bind) {
    this.closeFormVm();
    const bindData = bind || {};
    const c = (await import('../addressManager/addAddress/index.js')).default;
    this.openFomVm(c, bindData);
  },
  // 교대조 양식 열기
  async openShiftForm(bind) {
    this.closeFormVm();
    const bindData = bind || {};
    const c = (await import('../shiftManager/addShift/index.js')).default;
    this.openFomVm(c, bindData);
  },
  // 근태 그룹 양식 열기
  async openGroupForm(bind) {
    this.closeFormVm();
    const bindData = bind || {};
    const c = (await import('../groupManager/editGroup/index.js')).default;
    this.openFomVm(c, bindData);
  },
  // 편집 양식 페이지 열기
  async openFomVm(c, bindData) {
    this.formVm = await c.generate("#form", bindData, this);
    this.dom.querySelector("#form").classList.add("index_page_form_container");
  },
  // 휴가 유형 양식 열기
  async openLeaveTypeForm(bind) {
    this.closeFormVm();
    const bindData = bind || {};
    const c = (await import('../leaveManagerV2/editLeaveType/index.js')).default;
    this.openFomVm(c, bindData);
  },
  // 휴가 유형 규칙 양식 열기
  async openLeaveTypePolicyForm(bind) {
    this.closeFormVm();
    const bindData = bind || {};
    const c = (await import('../leaveManagerV2/editLeaveTypePolicy/index.js')).default;
    this.openFomVm(c, bindData);
  },
  // 양식 닫기
  closeFormVm() {
    if (this.formVm) {
      this.formVm.destroy();
    }
    this.dom.querySelector("#form").classList.remove("index_page_form_container");
  },

  async loadCurrentPersonInfo() {
    content.myDutyList = [];
    this.bind.admin = "";
    const controls = await myAction("controls");
    if (controls) {
      if (controls.admin) { // 관리자
        this.bind.admin = "admin";
      } else {
        if (controls.readAdmin) { // 근태 관리자 직책이 있으면 통계 조회 권한
          this.bind.admin = "readAdmin";
          content.myDutyList = controls.unitDutyList || [];
        }
        if (controls.assistAdmin) { // 근태 그룹 보조 관리자
          this.bind.assistAdmin = true;
        }
      }
    }
  },
  // 일반 메뉴 데이터
  getCurrentPersonMenu() {
    const menus = this.navDataAll();
    let access = 0;
    if (this.bind.admin === "readAdmin") {
      access = 1;
    } else if (this.bind.admin === "admin") {
      access = 2;
    }
    let accessMenus = menus.filter((menu) => menu.access <= access);
    if (this.bind.assistAdmin) { // 보조 관리자: 근태 그룹 관리 메뉴 추가
      if (!accessMenus.some((m) => m.access === 2)) {
        accessMenus.push(
          {
            "title": lp.menu.config,
            "icon": "ooicon-systemconfig",
            "text": lp.menu.config,
            "name": '3',
            "access": 2,
            "children": [
              {
                "name": "3-2",
                "text": lp.menu.groupmanager,
                "action": "groupManager",
                icon: "ooicon-group"
              }
            ],
          }
        );
      }
    }
    return accessMenus;
  },
  navDataAll() {
    return [
      {
        "name": "1",
        "icon": "ooicon-person_clock",
        "title": lp.menu.myAttendance,
        "text": lp.menu.myAttendance,
        "access": 0,
        "children": [
          {
            "name": "1-1",
            "text": lp.menu.myStatistic,
            "action": "myAttendance",
            "icon": "ooicon-data-center",
            "selected": true,
          },
          {
            "name": "1-2",
            "text": lp.menu.myAppealList,
            "action": "appealManager",
            "icon": "ooicon-emoji-prompt"
          },
          {
            "name": "1-3",
            "text": lp.menu.leave1,
            "action": "leaveManager",
            "icon": "ooicon-zaotui"
          },
          {
            "name": "1-4",
            "text": lp.menu.leave2,
            "action": "leaveManagerV2/requestList",
            "icon": "ooicon-working_hours"
          }
        ]
      },
      {
        "title": lp.menu.statistic,
        "icon": "ooicon-reports",
        "text": lp.menu.statistic,
        "name": '2',
        "access": 1,
        "children": [
          {
            "name": "2-1",
            "text": lp.menu.detailStatisticFilter,
            "action": "detailStatisticManager",
            "icon": "ooicon-work-management"
          },
          {
            "name": "2-2",
            "text": lp.menu.detailFilter,
            "action": "detailManager",
            "icon": "ooicon-work-management2"
          },
          {
            "name": "2-3",
            "text": lp.menu.recordList,
            "action": "recordManager",
            "icon": "ooicon-list-alt"
          },
          {
            "name": "2-4",
            "text": lp.menu.appealList,
            "action": "appealAdminManager",
            "icon": "ooicon-emoji-prompt"
          },
        ],
      },
      {
        "title": lp.menu.config,
        "icon": "ooicon-systemconfig",
        "text": lp.menu.config,
        "name": '3',
        "access": 2,
        "children": [
          {
            "name": "3-1",
            "text": lp.menu.shiftManager,
            "action": "shiftManager",
            "icon": "ooicon-onduty"
          },
          {
            "name": "3-2",
            "text": lp.menu.groupmanager,
            "action": "groupManager",
            icon: "ooicon-group"
          },
          {
            "name": "3-3",
            "text": lp.menu.addressmanger,
            "action": "addressManager",
            "icon": "ooicon-workcenter"
          },
          {
            "name": "3-42",
            "text": lp.menu.leavemanagerv2,
            "action": "leaveManagerV2",
            "icon": "ooicon-clock"
          },
          {
            "name": "3-5",
            "text": lp.menu.configmanager,
            "action": "configManager",
            "icon": "ooicon-config"
          },
        ],
      }
    ]
  },

});
