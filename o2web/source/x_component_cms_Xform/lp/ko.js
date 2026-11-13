MWF.xApplication.process = MWF.xApplication.process || {};
MWF.xApplication.process.Xform = MWF.xApplication.process.Xform || {};
MWF.xDesktop.requireApp("process.Xform", "lp."+MWF.language, null, false);
MWF.xApplication.cms = MWF.xApplication.cms || {};
MWF.xApplication.cms.Xform = MWF.xApplication.cms.Xform || {};
MWF.xApplication.cms.Xform.LP = Object.merge({}, MWF.xApplication.process.Xform.LP, {
    "dataSaved": "데이터를 저장했습니다",
    "documentPublished" : "게시했습니다" ,
    "documentDelayedPublished": "예약 게시를 설정했습니다",

    "noSelectRange": "선택 범위를 확인할 수 없습니다",

    "begin": "시작",
    "end": "종료",
    "none": "없음",

    "person": "사용자 이름",
    "department": "부서",
    "firstDate": "처음 읽은 시간",
    "readDate": "최근 읽은 시간",
    "readCount" : "읽은 횟수",
    "startTime": "받은 시간",
    "completedTime": "처리 시간",
    "opinion": "의견",

    "systemProcess": "시스템 처리",

    "deleteAttachmentTitle":"첨부파일 삭제 확인",
    "deleteAttachment": "선택한 첨부파일을 삭제하시겠습니까?",

    "replaceAttachmentTitle":"첨부파일 교체 확인",
    "replaceAttachment": "선택한 첨부파일을 교체하시겠습니까?",
    "uploadMore": "첨부파일은 최대 {n}개까지 업로드할 수 있습니다",
    "notValidation": "데이터 검증을 통과하지 못했습니다",

    "deleteDocumentTitle": "게시글 삭제 확인",
    "deleteDocumentText": {"html": "<div style='color: red;'>주의: 이 게시글을 삭제하려고 합니다. 삭제하면 게시글을 복구할 수 없습니다. 삭제하시겠습니까?</div>"},
    "documentDelete": "게시글을 삭제했습니다",

    "readerFieldNotice" : "선택하지 않으면 모든 사용자가 볼 수 있습니다",

    "readedLogTitle" : "읽기 기록",
    "readedCountText" : "총 {person}명, {count}회 읽음",
    "defaultReadedLogText" : "<font style='color:#00F;'>{person}</font>({department}) <font style='color:#00F'>{date}</font>에 읽음, 총 <font style='color:#00F'>{count}</font>회",

    "commendLogTitle" : "좋아요 기록",
    "commendLogPerson" : "좋아요 누른 사용자",
    "commendLogTime" : "좋아요 시간",
    "commendCountText" : "총 {count}회 좋아요",
    "defaultCommendLogText" : "<font style='color:#00F;'>{person}</font> <font style='color:#00F'>{date}</font>에 좋아요",


    "reply" : "댓글",
    "commentTitle" : "댓글 영역",
    "commentCountText" : "총 {count}개 댓글",

    "saveComment" : "댓글 등록",
    "saveCommentSuccess" : "댓글을 등록했습니다",
    "deleteCommentTitle" : "댓글 삭제 확인",
    "deleteCommentText" : "삭제한 댓글은 복구할 수 없습니다. 이 댓글을 삭제하시겠습니까?",
    "deleteCommentSuccess" : "댓글을 삭제했습니다",
    "commentFormTitle" : "댓글 편집",
    "createCommentSuccess" : "댓글을 작성했습니다",
    "updateSuccess" : "수정했습니다",
    "save" : "저장",
    "noText" : "댓글 내용은 비워 둘 수 없습니다",

    "setTopTitle": "상단 고정 확인",
    "setTopText": "현재 게시글을 상단에 고정하시겠습니까?",
    "setTopSuccess": "상단에 고정했습니다",
    "cancelTopTitle": "상단 고정 해제 확인",
    "cancelTopText": "현재 게시글의 상단 고정을 해제하시겠습니까?",
    "cancelTopSuccess": "상단 고정을 해제했습니다",

    "attachmentArea" : "첨부파일 영역",
    "selectAttachment" : "첨부파일 선택",

    "yesterday" : "어제",
    "theDayBeforeYesterday" : "그저께",
    "severalWeekAgo" : "{count}주 전",
    "severalDayAgo" : "{count}일 전",
    "severalHourAgo" : "{count}시간 전",
    "severalMintuesAgo" : "{count}분 전",
    "justNow" : "방금",
    "commend": {
        "do": "좋아요",
        "undo": "좋아요 취소"
    },
    "noPermissionOrDocumentNotExisted": "권한이 없거나 게시글이 존재하지 않습니다",
    "form": {
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
        "popular": "인기글 설정",
        "popularTitle": "인기글 설정",
        "delete": "삭제",
        "deleteTitle": "게시글 삭제",
        "print": "인쇄",
        "printTitle": "게시글 인쇄",
        "setTop": "상단 고정",
        "setTopTitle": "게시글 상단 고정",
        "cancelTop": "상단 고정 해제",
        "cancelTopTitle": "게시글 상단 고정 해제"
    }

    //"at" : "읽은 시간",
    //"readdDocument" : ", ",
    //"historyRead" : "총",
    //"times" : "회"
});
MWF.xApplication.cms.Xform["lp."+o2.language] = MWF.xApplication.cms.Xform.LP;
