MWF.xApplication.process = MWF.xApplication.process || {};
MWF.xApplication.process.FormDesigner = MWF.xApplication.process.FormDesigner || {};
MWF.xDesktop.requireApp("process.FormDesigner", "lp."+MWF.language, null, false);
MWF.xApplication.cms.FormDesigner.LP = Object.merge( {}, MWF.xApplication.process.FormDesigner.LP, {
    "selectApplication" : "앱 선택",
    "formType": {
        "empty": "빈 양식",
        "publishEdit": "게시용 편집 양식",
        "publishRead": "게시용 읽기 양식",
        "publishEditTidy": "게시용 편집 양식(간결)",
        "publishReadTidy": "게시용 읽기 양식(간결)",
        "publishEditGreen": "게시용 편집 양식(녹색)",
        "publishReadGreen": "게시용 읽기 양식(녹색)",
        "dataInput": "데이터 입력용 양식",
        "dataInputTidy": "데이터 입력용 양식(간결)"
    },
    "validation" : {
        "publish" : "게시할 때"
    },
    "modules": {
        "reader": "독자",
        "commend": "좋아요",
        "author": "작성자",
        "log": "읽기 기록",
        "comment": "댓글",
        "logCommend" : "좋아요 기록",
        "group_cms": "게시판 구성요소"
    },
    "formStyle":{
        "noneStyle": "빈 스타일",
        "defaultStyle": "전통 스타일",
        "redSimple": "빨강 간결",
        "blueSimple": "파랑 간결",
        "greenFlat": "녹색 플랫",
        "lightBlue": "연파랑 스타일",
        "defaultMobileStyle": "모바일 스타일",
        "banner": "배너",
        "title": "제목",
        "sectionTitle": "구역 제목",
        "section": "구역",
        "cmcc": "그룹 스타일",
        "blueFlat": "파란색 플랫"
    },
    "propertyTemplate": {
        "setPopular": "인기 게시글 설정 작업",

        "commentPerPage": "페이지당 댓글 수",
        "tiao": "개",
        "allowModifyComment": "작성 후 수정 허용",
        "allowComment": "댓글 작성 허용",
        "editor": "편집기",
        "editorTitle": "CKEditor Config 스크립트",
        "editorConfigNote": "편집기 초기화에 사용할 CKEditor Config 객체를 반환합니다",
        "editorConfigLinkNote": "더 많은 속성 도움말 보기",

        "table": "표",
        "text": "텍스트",
        "format": "형식",

        "validationSave": "저장 시 유효성 검사",
        "validationPublish": "게시 시 유효성 검사",

        "notice" : "메시지",
        "noticeInfo": "참고: 메시지 발송 전체 스위치는 분류 설정에서 지정합니다.",
        "noticeRange": "범위",
        "noticeByReader": "읽기 범위 기준",
        "noticeByCustom": "사용자 지정",
        "notifyCreatePerson": "작성자에게 알림",
        "blankToAllNotify": "독자(게시 범위)가 비어 있으면 읽기 범위에 알림",
        "blankNotToAllNotify": "독자(게시 범위)가 비어 있으면 알리지 않음",
        "specificValue": "지정",
        "formField": "양식 필드",

        "draft": "임시저장",
        "published": "게시됨",
    },
    "actionBar": {
        "close":"닫기",
        "closeTitle": "게시글 닫기",
        "edit": "편집",
        "editTitle": "게시글 편집",
        "save": "저장",
        "saveTitle": "게시글 저장",
        "publish": "게시",
        "publishTitle": "게시글 게시",
        "publishDelayed": "예약 게시",
        "publishDelayedTitle": "게시글 예약 게시",
        "saveDraft": "임시저장",
        "saveDraftTitle": "임시저장",
        "popular": "인기 게시글 설정",
        "popularTitle": "인기 게시글 설정",
        "delete": "삭제",
        "deleteTitle": "게시글 삭제",
        "print": "인쇄",
        "printTitle": "게시글 인쇄",
        "setTop": "상단 고정",
        "setTopTitle": "게시글 상단 고정",
        "cancelTop": "상단 고정 해제",
        "cancelTopTitle": "게시글 상단 고정 해제",
        "downloadAll": "일괄 다운로드",
        "downloadAllTitle": "일괄 다운로드"
    }
});
