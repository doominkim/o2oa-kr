import { o2 } from "@o2oa/component";

/**
 * 공통 설정 조회
 * @param {*} name
 * @returns
 */
function getPublicData(name) {
  return new Promise((resolve) => {
    o2.UD.getPublicData(name, (dData) => resolve(dData));
  });
}
/**
 * 공통 설정 지정
 * @param {*} name
 * @param {*} value
 * @returns
 */
function putPublicData(name, value) {
  return new Promise((resolve) => {
    o2.UD.putPublicData(name, value, (dData) => resolve(dData));
  });
}

/**
 * 백엔드 api 호출, data만 반환
 * @param {*} content
 * @param {*} action
 * @param {*} method
 * @param  {...any} args
 * @returns
 */
async function doAction(content, action, method, ...args) {
  const m = o2.Actions.load(content)[action][method];
  const json = await m.apply(m, ...args);
  return json.data;
}
/**
 * 백엔드 api 호출, response 객체 전체 반환
 * @param {*} content
 * @param {*} action
 * @param {*} method
 * @param  {...any} args
 * @returns
 */
async function doActionBackResult(content, action, method, ...args) {
  const m = o2.Actions.load(content)[action][method];
  return await m.apply(m, ...args);
}
/**
 * 근태 근무조 API
 * @param {*} method
 * @param  {...any} args
 * @returns
 */
function attendanceShiftAction(method, ...args) {
  return doAction("x_attendance_assemble_control", "ShiftAction", method, args);
}
/**
 * 근태 근무조 페이지 조회
 * @param {*} method
 * @param  {...any} args
 * @returns
 */
function shiftActionListByPaging(...args) {
  return doActionBackResult(
    "x_attendance_assemble_control",
    "ShiftAction",
    "listByPaging",
    args
  );
}
/**
 * 근태 근무지 주소 API
 * @param {*} method
 * @param  {...any} args
 * @returns
 */
function attendanceWorkPlaceV2Action(method, ...args) {
  return doAction(
    "x_attendance_assemble_control",
    "WorkPlaceV2Action",
    method,
    args
  );
}

/**
 * 근태 그룹 페이지 조회
 * @param {*} method
 * @param  {...any} args
 * @returns
 */
function groupActionListByPaging(...args) {
  return doActionBackResult(
    "x_attendance_assemble_control",
    "GroupAction",
    "listByPaging",
    args
  );
}
/**
 * 근태 그룹 API
 * @param {*} method
 * @param  {...any} args
 * @returns
 */
function groupAction(method, ...args) {
  return doAction("x_attendance_assemble_control", "GroupAction", method, args);
}

/**
 * 근무 편성 API
 * @param {*} method
 * @param  {...any} args
 * @returns
 */
function  groupScheduleAction(method, ...args) {
  return doAction("x_attendance_assemble_control", "GroupScheduleAction", method, args);
}


/**
 * 근태 상세 페이지 조회
 * @param {*} method
 * @param  {...any} args
 * @returns
 */
function detailActionListByPaging(...args) {
  return doActionBackResult(
    "x_attendance_assemble_control",
    "DetailAction",
    "listByPaging",
    args
  );
}

function detailAction(method, ...args) {
  return doAction(
    "x_attendance_assemble_control",
    "DetailAction",
    method,
    args
  );
}


/**
 * 휴가 데이터 상세 페이지 조회
 * @param {*} method
 * @param  {...any} args
 * @returns
 */
function leaveActionListByPaging(...args) {
  return doActionBackResult(
    "x_attendance_assemble_control",
    "LeaveAction",
    "listByPaging",
    args
  );
}

function leaveAction(method, ...args) {
  return doAction(
    "x_attendance_assemble_control",
    "LeaveAction",
    method,
    args
  );
}

function leaveManagerAction(method, ...args) {
  return doAction(
    "x_attendance_assemble_control",
    "LeaveManagerAction",
    method,
    args
  );
}

function leaveManagerActionListByPaging(method, ...args) {
  return doActionBackResult(
    "x_attendance_assemble_control",
    "LeaveManagerAction",
    method,
    args
  );
}


/**
 * 근태 설정 API
 * @param {*} method
 * @param  {...any} args
 * @returns
 */
function configAction(method, ...args) {
  return doAction("x_attendance_assemble_control", "ConfigAction", method, args);
}



/**
 * 이의 신청 페이지 조회
 * @param {*} method
 * @param  {...any} args
 * @returns
 */
function appealInfoActionListByPaging(...args) {
  return doActionBackResult(
    "x_attendance_assemble_control",
    "AppealInfoAction",
    "listByPaging",
    args
  );
}
/**
 * 이의 신청 페이지 조회
 * @param {*} method
 * @param  {...any} args
 * @returns
 */
function appealInfoActionManagerListByPaging(...args) {
  return doActionBackResult(
    "x_attendance_assemble_control",
    "AppealInfoAction",
    "managerListByPaging",
    args
  );
}

/**
 * 원본 기록 페이지 조회
 * @param {*} method
 * @param  {...any} args
 * @returns
 */
function recordActionListByPaging(...args) {
  return doActionBackResult(
    "x_attendance_assemble_control",
    "RecordAction",
    "listByPaging",
    args
  );
}


/**
 * 이의 신청 API
 * @param {*} method
 * @param  {...any} args
 * @returns
 */
function appealInfoAction(method, ...args) {
  return doAction("x_attendance_assemble_control", "AppealInfoAction", method, args);
}
/**
 * 프로세스 API
 * @param {*} method 
 * @param  {...any} args 
 * @returns 
 */
function processAction(method, ...args) {
  return doAction("x_processplatform_assemble_surface", "ProcessAction", method, args);
}
/**
 * Job API
 * @param {*} method 
 * @param  {...any} args 
 * @returns 
 */
function jobAction(method, ...args) {
  return doAction("x_processplatform_assemble_surface", "JobAction", method, args);
}


/**
 * 내 근태 API
 * @param {*} method
 * @param  {...any} args
 * @returns
 */
function myAction(method, ...args) {
  return doAction("x_attendance_assemble_control", "MyAction", method, args);
}
/**
 * 출퇴근 체크 API
 * @param {*} method
 * @param  {...any} args
 * @returns
 */
function mobileAction(method, ...args) {
  return doAction("x_attendance_assemble_control", "MobileAction", method, args);
}

/**
 * 조직 관리 사용자 API
 * x_organization_assemble_control
 * @param {*} method
 * @param  {...any} args
 * @returns
 */
function personAction(method, ...args) {
  return doAction("x_organization_assemble_control", "PersonAction", method, args);
}

/**
 * 개인 API
 * x_organization_assemble_personal
 * @param {*} method 
 * @param  {...any} args 
 * @returns 
 */
function personalAction(method, ...args) {
  return doAction("x_organization_assemble_personal", "PersonAction", method, args);
}
/**
 * DefinitionAction
 * x_organization_assemble_personal
 * @param {*} method 
 * @param  {...any} args 
 * @returns 
 */
function definitionAction(method, ...args) {
  return doAction("x_organization_assemble_personal", "DefinitionAction", method, args);
}

/**
 * 센터 서비스 스크립트 실행
 * @param {*} method 
 * @param  {...any} args 
 * @returns 
 */
function invokeAction(method, ...args) {
    return doAction("x_program_center", "InvokeAction", method, args);
}

function qywxAuthAction(method, ...args) {
    return doAction("x_organization_assemble_authentication", "QiyeweixinAction", method, args);
}




//file 다운로드 url
const getFileDownloadUrl = (id) => {
    var action = o2.Actions.load('x_attendance_assemble_control').FileAction.action;
    var url = action.getAddress() + action.actions.download.uri;
    url = url.replace('{id}', encodeURIComponent(id));
    return url;
};


export {
  getPublicData,
  putPublicData,
  attendanceShiftAction,
  shiftActionListByPaging,
  attendanceWorkPlaceV2Action,
  groupActionListByPaging,
  groupAction,
  detailActionListByPaging,
  detailAction,
  configAction,
  appealInfoActionListByPaging,
  appealInfoActionManagerListByPaging,
  appealInfoAction,
  processAction,
  jobAction,
  myAction,
  mobileAction,
  personAction,
  personalAction,
  leaveActionListByPaging,
  leaveAction,
  recordActionListByPaging,
  groupScheduleAction,
  leaveManagerAction,
  leaveManagerActionListByPaging,
  invokeAction,
  qywxAuthAction,
  getFileDownloadUrl,
  definitionAction
};
