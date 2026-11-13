/**
 * 포럼 역할 정보 관리
 *
 * 포럼 역할>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
 * 포럼 방문자(FORUM_GUEST): 포럼 방문자
 * 		0. 포럼 표시(FORUM_VIEW): 사용자가 BBS 시스템에서 이 포럼에 접근할 수 있음
 *
 * 포럼 관리자(FORUM_SUPER_MANAGER): 포럼 관리자, 이 포럼 관리의 최대 권한 보유
 *      0. 포럼 표시(FORUM_VIEW): 사용자가 BBS 시스템에서 이 포럼에 접근할 수 있음
 * 		1. 포럼 정보 관리(FORUM_INFO_MANAGEMENT): 포럼 게시판 추가, 삭제, 수정 권한 보유
 * 		1. 포럼 주제 게시(FORUM_SUBJECT_PUBLISH): 포럼의 모든 게시판에 주제 게시 가능
 * 		2. 포럼 답글 작성(FORUM_REPLY_PUBLISH): 포럼의 모든 주제에 답글 가능
 * 		3. 포럼 주제 추천(FORUM_SUBJECT_RECOMMEND): 지정 포럼의 모든 게시판에서 지정 주제를 포럼 첫 화면에 추천 가능
 * 		4. 포럼 주제 상단 고정(FORUM_SUBJECT_STICK): 포럼의 모든 주제에 대한 상단 고정 권한 보유
 * 		5. 포럼 주제 추천글 지정(FORUM_SUBJECT_CREAM): 포럼의 모든 주제에 대한 추천글 설정 권한 보유
 * 		6. 포럼 주제 관리(FORUM_SUBJECT_MANAGEMENT): 포럼의 모든 주제에 대한 잠금·삭제 권한 보유
 * 		7. 포럼 답글 관리(FORUM_REPLY_MANAGEMENT): 포럼의 모든 답글에 대한 삭제 권한 보유
 * 		8. 포럼 권한 관리(FORUM_PERMISSION_MANAGEMENT): 포럼 사용자에 대한 포럼 권한 설정 권한 보유
 * 		9. 포럼 설정 관리(FORUM_CONFIG_MANAGEMENT): 포럼 매개변수 설정 권한 보유
 *
 *
 * 게시판 역할>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
 * 게시판 방문자(SECTION_GUEST): 게시판 방문자
 * 		0. 포럼 열람(SECTION_VIEW): BBS 시스템에서 해당 게시판 접근 가능
 *
 * 게시판 관리자(SECTION_MANAGER): 게시판 및 게시판 콘텐츠 관리의 최고 권한 보유
 *      0. 포럼 열람(SECTION_VIEW): BBS 시스템에서 해당 게시판 접근 가능
 * 		1. 주제 게시(SECTION_SUBJECT_PUBLISH): 지정 게시판에 주제 게시 가능
 *		2. 주제 검토(SECTION_SUBJECT_AUDIT): 주제 검토가 필요한 경우 지정 게시판에 게시된 주제 검토 가능
 * 		3. 주제 관리(SECTION_SUBJECT_MANAGEMENT): 지정 게시판에서 게시된 주제 조회·삭제 가능
 * 		4. 답글 작성(SECTION_REPLY_PUBLISH): 지정 게시판의 모든 주제에 답글 가능
 *		5. 답글 검토(SECTION_REPLY_AUDIT): 답글 검토가 필요한 경우 지정 게시판의 모든 답글 내용 검토 가능
 *		6. 답글 관리(SECTION_REPLY_MANAGEMENT): 사용자가 지정 게시판에서 답글을 조회하거나 삭제할 수 있음
 * 		7. 게시판 주제 추천(SECTION_SUBJECT_RECOMMEND): 사용자가 지정 게시판에서 지정 주제를 추천할 수 있음
 * 		8. 게시판 주제 상단 고정(SECTION_SUBJECT_STICK): 사용자가 지정 게시판에서 지정 주제를 상단 고정할 수 있음
 * 		9. 게시판 주제 우수글 지정(SECTION_SUBJECT_CREAM): 사용자가 지정 게시판에서 지정 주제를 우수 주제로 설정할 수 있음
 * 		10. 게시판 권한 관리(SECTION_PERMISSION_MANAGEMENT): 사용자가 포럼 사용자의 지정 게시판 권한을 관리할 수 있음
 * 		11. 게시판 설정 관리(SECTION_CONFIG_MANAGEMENT): 사용자가 지정 게시판의 시스템 매개변수 설정을 수정할 수 있음
 *
 * 주제 게시자(SECTION_SUBJECT_PUBLISHER): 지정 게시판 내 주제 게시 허용
 * 		0. 포럼 열람(SECTION_VIEW): BBS 시스템에서 해당 게시판 접근 가능
 * 		1. 주제 게시(SECTION_SUBJECT_PUBLISH): 지정 게시판에 주제 게시 가능
 *
 * 주제 답글 작성자(SECTION_REPLY_PUBLISHER): 지정 게시판 내 주제 게시 허용
 * 		0. 포럼 열람(SECTION_VIEW): BBS 시스템에서 해당 게시판 접근 가능
 * 		1. 답글 작성(SECTION_REPLY_PUBLISH): 사용자가 지정 게시판의 모든 주제에 답글을 달 수 있음
 *
 * 주제 추천자(SECTION_RECOMMENDER): 지정 게시판 내 주제 게시 허용
 * 		0. 포럼 열람(SECTION_VIEW): BBS 시스템에서 해당 게시판 접근 가능
 * 		1. 게시판 주제 추천(SECTION_SUBJECT_RECOMMEND): 사용자가 지정 게시판에서 지정 주제를 추천할 수 있음
 *
 * 주제 검토자(SECTION_SUBJECT_AUDITOR): 지정 게시판 내 주제 게시 허용
 * 		0. 포럼 열람(SECTION_VIEW): BBS 시스템에서 해당 게시판 접근 가능
 * 		1. 주제 검토(SECTION_SUBJECT_AUDIT): 주제에 검토가 필요한 경우 사용자가 지정 게시판에 게시된 주제를 검토할 수 있음
 *
 * 답글 검토자(SECTION_REPLY_AUDITOR): 지정 게시판 내 주제 게시 허용
 * 		0. 포럼 열람(SECTION_VIEW): BBS 시스템에서 해당 게시판 접근 가능
 * 		1. 답글 검토(SECTION_REPLY_AUDIT): 답글에 검토가 필요한 경우 사용자가 지정 게시판의 모든 답글 내용을 검토할 수 있음 *
 *
 * @author LIYI
 *
 */