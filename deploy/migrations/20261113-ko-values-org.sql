-- ORG(조직·기반·공통) 도메인 저장값 한국어 이전. 반복 실행해도 안전하다.
-- 엔티티 테이블은 해당 기능을 처음 쓸 때 JPA가 만들기 때문에 아직 없을 수 있다. 테이블이 있을 때만 갱신한다.
BEGIN;

DO $$
BEGIN
  -- base: 서비스 호출(Invoke) 기본 분류
  IF to_regclass('public.cte_invoke') IS NOT NULL THEN
    UPDATE cte_invoke SET xcategory = '미분류' WHERE xcategory = '未分类';
  END IF;

  -- general: 영수증 정산 상태
  IF to_regclass('public.gen_invoice') IS NOT NULL THEN
    UPDATE gen_invoice SET xapplystatus = '미정산' WHERE xapplystatus = '未报销';
    UPDATE gen_invoice SET xapplystatus = '정산 완료' WHERE xapplystatus = '已报销';
  END IF;

  -- org: 명함 기본 그룹(이전 단계 번역값 '기본 분류' 포함)
  IF to_regclass('public.org_personcard') IS NOT NULL THEN
    UPDATE org_personcard SET xgrouptype = '기본 그룹' WHERE xgrouptype IN ('默认分组', '기본 분류');
  END IF;

  -- org: 조직 권한 설정 상태
  IF to_regclass('public.org_permissionsetting') IS NOT NULL THEN
    UPDATE org_permissionsetting SET xstatus = '게시' WHERE xstatus = '发布';
  END IF;

  -- org: 정무 DingTalk 동기화 사용자 속성 이름
  IF to_regclass('public.org_personattribute') IS NOT NULL THEN
    UPDATE org_personattribute SET xname = '직무' WHERE xname = '职务';
  END IF;
END $$;

-- base: 3권 분립 가상 관리자 DN·이름. 작성자·처리자 컬럼 어디에나 들어갈 수 있으므로 모든 문자열 컬럼을 훑는다.
-- DN(@...Manager@P)은 문자열 안에서 바꾸고(목록·JSON 포함), 이름 단독 값은 완전 일치만 바꾼다.
DO $$
DECLARE
  r record;
  n bigint;
BEGIN
  FOR r IN
    SELECT c.table_name AS t, c.column_name AS col
    FROM information_schema.columns c
    JOIN information_schema.tables tb
      ON tb.table_schema = c.table_schema AND tb.table_name = c.table_name AND tb.table_type = 'BASE TABLE'
    WHERE c.table_schema = 'public' AND c.data_type IN ('character varying', 'text')
  LOOP
    EXECUTE format(
      'UPDATE %I SET %I = CASE %I'
      || ' WHEN %L THEN %L WHEN %L THEN %L WHEN %L THEN %L'
      || ' ELSE replace(replace(replace(%I, %L, %L), %L, %L), %L, %L) END'
      || ' WHERE %I ~ %L',
      r.t, r.col, r.col,
      '系统管理员', '시스템 관리자',
      '安全管理员', '보안 관리자',
      '安全审计员', '보안 감사자',
      r.col,
      '系统管理员@systemManager@P', '시스템 관리자@systemManager@P',
      '安全管理员@securityManager@P', '보안 관리자@securityManager@P',
      '安全审计员@auditManager@P', '보안 감사자@auditManager@P',
      r.col,
      '^(系统管理员|安全管理员|安全审计员)$|系统管理员@systemManager@P|安全管理员@securityManager@P|安全审计员@auditManager@P');
    GET DIAGNOSTICS n = ROW_COUNT;
    IF n > 0 THEN
      RAISE NOTICE 'ternary DN %.%: % rows', r.t, r.col, n;
    END IF;
  END LOOP;
END $$;

COMMIT;
