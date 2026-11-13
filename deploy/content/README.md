# 기본 콘텐츠 (내보내기 파일)

O2OA 앱 내보내기(export)로 받은 파일과, 이 파일을 다른 서버에 가져오는(import) 스크립트를 둔다.

| 경로 | 내용 |
|---|---|
| `approval/전자결재.xapp` | 프로세스 플랫폼 앱 "전자결재". 양식·프로세스 각 3개(기안서, 휴가 신청서, 지출결의서). 디자이너 `output/{앱}/select`로 내보낸 JSON |
| `boards/인디고 사내 게시판.xapp` | 게시판 3개(공지사항·자유게시판·자료실): 분류·글쓰기/읽기 양식·권한·설정. 앱센터 내보내기 형식 |
| `import.py` | 앱 종류별 가져오기 스크립트 |

## 가져오기

```bash
O2_ADMIN_PASSWORD=... python3 deploy/content/import.py --url http://서버주소            # 전부
O2_ADMIN_PASSWORD=... python3 deploy/content/import.py --url http://서버주소 approval   # 결재만
O2_ADMIN_PASSWORD=... python3 deploy/content/import.py --url http://서버주소 boards     # 게시판만
```

- 관리자(`--user`, 기본 `xadmin`)로 로그인한 뒤 디자이너 가져오기 API를 호출한다. 같은 id의 앱이 있으면 `PUT /x_processplatform_assemble_designer/jaxrs/input/cover`로 덮어쓰고, 없으면 `PUT .../input/create`로 새로 만든다. 요청 본문은 `.xapp` 파일 JSON 그대로다.
- 덮어쓸 때는 활동 캐시가 비워지지 않는다. 그래서 프로세스마다 `GET .../process/{id}/enabled`를 호출해 캐시를 갱신한다.
- 휴가 신청서가 쓰는 근태 휴가 유형 5개(연차·오전반차·오후반차·병가·경조사)가 없으면 `POST /x_attendance_assemble_control/jaxrs/v2/leavemanager/type`으로 만든다(UNLIMITED·DAY).
- 게시판은 `PUT /x_program_center/jaxrs/module/compare/upload`(multipart `file`)로 비교한 뒤 `PUT /x_program_center/jaxrs/module/write/{flag}`에 앱마다 `create`(없음) 또는 `cover`(있음)를 보낸다. 덮어써도 기존 글은 남는다.
- 공지사항 글쓰기 권한은 `이지원@support.lead@P`·`김대표@ceo@P`로 묶여 있어, 대상 서버에 같은 사용자가 없으면 가져온 뒤 게시자를 다시 지정한다.
- 같은 파일로 여러 번 실행해도 된다. 진행 중인 문서는 지우지 않는다.

## 전자결재 동작 요약

- 흐름: 시작 → 기안 → 합의 여부(선택 활동) → 합의(병렬, 선택) → 결재(순차 queue, 결재자 순서대로) → 종료.
- 처리자는 양식 값에서 받는다. 결재자는 `approverList`, 합의자는 `agreeList`(데이터 경로 처리자)에서 받는다. 참조자는 `refList`를 결재 단계 도착 시 열람자(readDataPathList)로 지정한다.
- 결재·합의 단계에서 "반려"(우선 경로 soleDirect, 의견 필수)를 고르면 기안 단계로 돌아가 기안자에게 다시 결재 대기가 생긴다. 다시 상신하면 같은 결재선으로 처음부터 결재한다.
- 문서번호는 상신할 때 "기안|휴가|지출-연도-0001" 형식으로 붙는다. 문서 제목은 저장·처리 시 "기안자 - 제목"으로 정해진다(양식명은 업무함·홈 카드가 "[프로세스명]"으로 따로 붙인다).
- 결재란(오른쪽 위)은 양식의 afterLoad 스크립트가 결재 기록을 읽어 그린다. 칸마다 직책(없으면 "결재"), 이름, 상신·승인·반려·결재중·대기, 날짜를 보여 준다. 합의자가 있으면 합의란이 따로 나온다.
- 휴가 신청서는 종료 활동의 '도착 전' 스크립트에서 `v2/leavemanager/request/apply`를 호출해 근태 휴가 신청 기록을 만든다. 휴가 유형은 서버마다 id가 다르므로 이름으로 찾는다. 반려된 문서나 삭제된 문서는 종료 활동에 도착하지 않으므로 기록되지 않는다. 결과는 문서 데이터 `attendanceLeaveRequestId`와 `attendanceError`에 남는다.
