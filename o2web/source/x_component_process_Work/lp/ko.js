MWF.xApplication = MWF.xApplication || {};
MWF.xApplication.process = MWF.xApplication.process || {};
MWF.xApplication.process.Work = MWF.xApplication.process.Work || {};
MWF.xApplication.process.Work.LP = {
	"title": "문서",
    "selectRoute": "처리 선택",
    "inputOpinion": "의견 작성",
    "selectPerson" : "사용자 선택",
    "cancel": "취소",
    "ok": "제출",
    "close": "닫기",
    "saveWrite": "저장",
    "inputText": "여기에 결재 의견을 작성하세요",

    "mustSelectRoute": "먼저 처리 방법을 선택하세요",
    "mustSelectRouteGroup" : "먼저 처리 그룹을 선택하세요",
    "opinionRequired" : "의견을 작성하세요",

    "searchKey": "키워드를 입력하세요",

    "task": "결재 대기함",
    "done": "결재 완료함",
    "draft": "임시저장함",
    "myfile": "내 문서",
    "reset": "처리자 재지정",
    "reroute": "단계 이동",
    "addSplit": "분기 추가",
    "rollback": "결재 되돌리기",
    "goBack": "반송",

    "phone": "휴대폰",
    "mail": "메일",
    "save": "저장",
    "process": "다음 단계로",
    "flowWork": "다음 단계로",
    "handwriting": "손글씨",
    "audioRecord": "녹음",
    "collect": "즐겨찾기",

    "noOpinionNote": "자주 쓰는 의견이 아직 없습니다. 클릭해 설정하세요.",

    "noAppendTaskIdentityConfig" : "전달 대상자가 설정되지 않았습니다. 관리자에게 문의하세요",
    "selectAppendTaskIdentityNotice" : "전달 대상자를 선택하세요",
    "routeValidFailure" : "처리 선택 검증에 실패했습니다",
    "loadedOrgCountUnexpected" : "사용자 선택 화면을 아직 불러오지 못했습니다. 잠시 기다려 주세요...",

    "taskCompletedPerson": "처리자",
    "readPerson": "열람자",
    "systemFlow": "시스템 자동 처리",

    "openWorkError": "이 문서를 볼 권한이 없거나 문서가 삭제되었습니다.",

    "rollbackConfirmTitle": "결재 되돌리기 확인",
    "rollbackConfirmContent": "결재를 \"{log}\" 상태로 되돌리시겠습니까? (되돌리면 이 상태 이후의 모든 정보가 삭제됩니다)",
    "rollbackSuccess" : "되돌렸습니다",

    "recoverFileConfirmTitle": "본문 복원 확인",
    "recoverFileConfirmContent": "본문을 \"{att}\" 버전으로 복원하시겠습니까? (복원하면 저장된 임시 파일이 삭제되어 다시 복원할 수 없습니다)",

    "notRecoverFileConfirmTitle": "본문 복원 취소 확인",
    "notRecoverFileConfirmContent": "본문 복원을 취소하시겠습니까? (취소하면 저장된 임시 파일이 삭제되어 다시 복원할 수 없습니다)",

    "closePageCountDownText" : "\"{second}\"초 후에 페이지를 닫습니다!",
    "closePage" : "페이지 닫기",

    "selectRouteGroup" : "처리 그룹 선택",
    "defaultDecisionOpinionName" : "기타",
    "routeGroupOrderList" : ["승인","반려","기타","기타"],

    "selectWork": "열려는 문서가 여러 분기로 나뉘었습니다. 확인할 분기를 선택하세요: ",
    "currentActivity": "현재 단계: ",
    "currentUsers": "현재 처리자: ",
    "completedWork": "결재가 완료된 문서입니다",

    "managerProcessNotice" : "참고: 빠른 처리 기능은 다음 경우에만 사용하세요. 그렇지 않으면 오류가 발생할 수 있습니다.<br\>1. 양식의 필수 항목이 모두 입력되어 있음.<br\>2. 제출할 때 사용자를 선택할 필요가 없음.<br\>3. 사용자 소속에 따라 계산되는 내용이 없음.<br\>관리자는 결재 대기 처리자로 대리 로그인해 양식에서 제출할 수 있습니다. 아래 링크를 클릭해 실행하세요.",
    "managerLogin" : "대리 로그인 후 문서 열기",
    "managerLoginConfirmTitle" : "대리 로그인",
    "managerLoginConfirmContent" : "{user}(으)로 로그인해 문서를 여시겠습니까? 확인을 누르면 현재 사용자로 돌아가려면 로그아웃 후 다시 로그인해야 합니다.",
    "managerLoginSuccess" : "{user}(으)로 전환했습니다",

    "selectIdentity": "이 결재 대기를 처리할 소속 선택",
    "selectIdentityInfo": "현재 문서에 여러 소속의 결재 대기가 있습니다. 이 문서를 처리할 소속을 선택하세요",

    "org": "조직",
    "duty": "직무",

    "flowActions": {
        "addTask": "결재자 추가",
        "reset": "재지정",
        "process": "제출",
        "goBack": "반송"
    },
    "modeType": "처리 방식",
    "single": "단독",
    "queue": "순차",
    "parallel": "병렬",
    "addTaskType": "결재자 추가 방식",
    "addTaskBefore": "앞에 추가",
    "addTaskAfter": "뒤에 추가",
    "opinion": "의견",
    "addTaskPerson": "추가 결재자",
    "inputOpinionNote": "여기에 의견을 작성하세요",
    "inputAddTaskPeople": "추가 결재자를 선택하세요",
    "inputResetPeople": "재지정할 처리자를 선택하세요",
    "inputAddTaskType": "결재자 추가 방식을 선택하세요",
    "inputModeType": "처리 방식을 선택하세요",
    "resetTo": "재지정 대상",
    "keepTask": "내 결재 대기 유지",
    "quickSelect": "빠른 선택",
    "empowerTo": "위임 대상",
    "selectAll": "전체 선택",
    "ok1": "확인",
    "not": "안 함",
    "selectPerson1": "사용자를 선택하세요",
    "noQuickSelectDataNote": "현재 단계에서 선택한 데이터 기록이 아직 없습니다.",
    "submitQuickText": "[{route}] 선택, 의견: {opinion}{org}.",
    "addTaskQuickText": "[{route}{mode}] 선택, 의견: {opinion}, 추가 결재자: {org}.",
    "resetQuickText": "의견: {opinion}, 재지정 대상: {org}.",
    "deleteQuickItemTitle": "빠른 선택 기록 삭제",
    "deleteQuickItemContent": "이 빠른 선택 기록을 삭제하시겠습니까?",

    "users": "처리자",
    "goBackActivity": "반송할 단계",
    "goBackActivityWay": "반송 후 처리: ",
    "goBackActivityWayStep": "결재 순서대로 진행",
    "goBackActivityWayJump": "반송한 사람에게 바로 돌아가기",
    "goBackTo": "반송 대상: ",
    "selectGoBackActivity": "반송할 단계를 선택하세요"
};
MWF.xApplication.process.Work["lp."+o2.language] = MWF.xApplication.process.Work.LP;
