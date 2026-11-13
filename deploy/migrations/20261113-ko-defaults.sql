-- 운영 DB의 중국어 기본 데이터를 한국 기준으로 바꾼다 (O2OA-7, 이미지 d963e71b 이후).
-- 한 트랜잭션. psql -v ON_ERROR_STOP=1 로 실행한다.
BEGIN;

-- 1. AI 기본 모델(通义千问 등): 첫 기동 때 InitModel.json으로 들어간 4건. 이미지에서 시드도 제거됨.
DELETE FROM ai_model WHERE xid IN (
  'f827b000-fddd-4ba7-b6b0-35381b3f3393', '88e5062e-765c-4354-b770-f5f218eb2a99',
  'ed9afc12-7604-4ea1-858d-5d48a01792c1', '6c7fc7c1-93fe-4c92-88cf-01cf5c7c6c76');

-- 2. 중국 공휴일(holiday-cn 동기화분). 새 동기화 작업(매일 01:00)이 한국 공휴일로 다시 채운다.
DELETE FROM attend_v2_holiday WHERE xsource = 'SYNC';

-- 3. 기본 휴가 유형(ID 고정 10건)을 한국 명칭으로. 年假·病假는 content/import.py가 만든
--    연차·병가(사용 중)와 겹치므로 지운다(참조하는 신청·원장·정책 없음).
DELETE FROM attend_v2_leave_type WHERE xid IN ('49448f7d-086e-4f05-8f2c-f64121588661', 'f47ac10b-58cc-4372-a567-0e02b2c3d479');
UPDATE attend_v2_leave_type SET xname = v.name FROM (VALUES
  ('e9a3b632-1b12-4d9b-bc75-6e54c86d8a35', '유급 휴가'),
  ('2f98e6c7-3a15-4e78-9041-3b562a4d9e12', '대체 휴가'),
  ('9b1c2d3e-4f5a-6b7c-8d9e-0f1a2b3c4d5e', '출산 휴가'),
  ('782a1b9c-d3e4-4f5a-bc6d-7e8f9a0b1c2d', '육아 휴직'),
  ('a1b2c3d4-e5f6-47a8-b9c0-d1e2f3a4b5c6', '가족 돌봄 휴가'),
  ('550e8400-e29b-41d4-a716-446655440000', '결혼 휴가'),
  ('6ba7b810-9dad-11d1-80b4-00c04fd430c8', '조의 휴가'),
  ('8d61239c-4f12-4e92-bc10-72a3b1c4d5e6', '기타')) AS v(id, name)
WHERE attend_v2_leave_type.xid = v.id;

-- 4. 시스템 역할 설명(OrganizationDefinition의 *_description과 같은 문구).
UPDATE org_role SET xdescription = '시스템 관리자(시스템 역할). 모든 역할의 권한을 가지며 모든 내용을 관리할 수 있습니다.' WHERE xname = 'Manager';
UPDATE org_role SET xdescription = '근태 관리자(시스템 역할). 근태 관리 관련 설정을 관리하고 데이터 관리 기능을 사용할 수 있습니다.' WHERE xname = 'AttendanceManager';
UPDATE org_role SET xdescription = '조직 정보 관리자(시스템 역할). 조직도, 사용자, 그룹, 역할 관련 설정과 관리 기능을 사용할 수 있습니다.' WHERE xname = 'OrganizationManager';
UPDATE org_role SET xdescription = '사용자 정보 관리자(시스템 역할). 조직 관리의 사용자 관리 기능을 사용할 수 있습니다.' WHERE xname = 'PersonManager';
UPDATE org_role SET xdescription = '그룹 정보 관리자(시스템 역할). 조직 관리의 그룹 관리 기능을 사용할 수 있습니다.' WHERE xname = 'GroupManager';
UPDATE org_role SET xdescription = '조직 정보 관리자(시스템 역할). 조직 관리의 조직도 관리 기능을 사용할 수 있습니다.' WHERE xname = 'UnitManager';
UPDATE org_role SET xdescription = '역할 정보 관리자(시스템 역할). 조직 관리의 역할 관리 기능을 사용할 수 있습니다.' WHERE xname = 'RoleManager';
UPDATE org_role SET xdescription = '프로세스 플랫폼 관리자(시스템 역할). 프로세스 플랫폼과 프로세스 설계를 관리하고 프로세스 인스턴스를 조회, 단계 이동, 삭제할 수 있습니다.' WHERE xname = 'ProcessPlatformManager';
UPDATE org_role SET xdescription = '프로세스 설계 작성자(시스템 역할). 프로세스를 설계하고 프로세스 앱을 추가, 설계할 수 있습니다.' WHERE xname = 'ProcessPlatformCreator';
UPDATE org_role SET xdescription = '회의 관리자(시스템 역할). 회의 장소, 회의실, 회의 등의 정보와 회의 관리 시스템의 모든 설정을 관리할 수 있습니다.' WHERE xname = 'MeetingManager';
UPDATE org_role SET xdescription = '회의 관찰자(시스템 역할). 모든 회의 정보를 볼 수 있습니다.' WHERE xname = 'MeetingViewer';
UPDATE org_role SET xdescription = '포털 관리자(시스템 역할). 포털 앱을 설계하고 관리할 수 있습니다.' WHERE xname = 'PortalManager';
UPDATE org_role SET xdescription = '포털 작성자(시스템 역할). 포털을 설계하고 추가, 설계할 수 있습니다.' WHERE xname = 'PortalCreator';
UPDATE org_role SET xdescription = '커뮤니티 관리자(시스템 역할). 포럼 분류와 섹션 생성, 권한 설정, 게시물 관리 등을 할 수 있습니다.' WHERE xname = 'BBSManager';
UPDATE org_role SET xdescription = '게시판 시스템 관리자(시스템 역할). 게시판과 분류를 설계하고, 양식과 목록을 설계하며 게시글을 관리할 수 있습니다.' WHERE xname = 'CMSManager';
UPDATE org_role SET xdescription = '게시판 작성자(시스템 역할). 게시판을 설계하고 추가, 설계할 수 있습니다.' WHERE xname = 'CMSCreator';
UPDATE org_role SET xdescription = '실행력 관리자(시스템 역할). 실행력 관리 시스템을 설정하고 업무 내용을 관리할 수 있습니다.' WHERE xname = 'OKRManager';
UPDATE org_role SET xdescription = 'CRM 관리자(시스템 역할). CRM 시스템 관련 설정과 고객 정보, 영업 기회 등의 정보를 관리할 수 있습니다.' WHERE xname = 'CRMManager';
UPDATE org_role SET xdescription = 'TeamWork 관리자(시스템 역할). TeamWork 시스템 관련 설정과 프로젝트, 작업 등의 정보를 관리할 수 있습니다.' WHERE xname = 'TeamWorkManager';
UPDATE org_role SET xdescription = '데이터 센터 관리자(시스템 역할). 데이터 센터에서 뷰 관리, 통계 관리 등을 할 수 있습니다.' WHERE xname = 'QueryManager';
UPDATE org_role SET xdescription = '데이터 센터 작성자(시스템 역할). 데이터 센터에서 뷰 추가, 통계 관리 등을 할 수 있습니다.' WHERE xname = 'QueryCreator';
UPDATE org_role SET xdescription = '메시지 관리자(시스템 역할). 시스템에서 생성된 메시지를 관리할 수 있습니다.' WHERE xname = 'MessageManager';
UPDATE org_role SET xdescription = '핫이슈 이미지 관리 권한(시스템 역할). 시스템에서 생성된 이미지를 관리할 수 있습니다.' WHERE xname = 'HotPictureManager';
UPDATE org_role SET xdescription = '검색 관리자(시스템 역할). 권한과 관계없이 시스템 내용을 검색할 수 있습니다.' WHERE xname = 'SearchPrivilege';
UPDATE org_role SET xdescription = '웹하드 관리자(시스템 역할). 웹하드 시스템 관련 설정을 할 수 있습니다.' WHERE xname = 'FileManager';
UPDATE org_role SET xdescription = '서비스 관리자(시스템 역할). 서비스 관리의 인터페이스와 에이전트를 설정할 수 있습니다.' WHERE xname = 'ServiceManager';
UPDATE org_role SET xdescription = '3권 분리 관리의 시스템 관리자(시스템 역할). 사용자 추가와 시스템 운영 유지보수를 담당합니다.' WHERE xname = 'SystemManager';
UPDATE org_role SET xdescription = '3권 분리 관리의 보안 관리자(시스템 역할). 권한 설정과 감사 로그, 사용자 및 시스템 관리자 작업 검토 분석을 담당합니다.' WHERE xname = 'SecurityManager';
UPDATE org_role SET xdescription = '3권 분리 관리의 보안 감사자(시스템 역할). 시스템 관리자와 보안 관리자의 작업을 감사, 추적합니다.' WHERE xname = 'AuditManager';

COMMIT;
