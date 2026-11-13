-- CMS(게시판·문서) 저장값 중국어 → 한국어 정본 이전. 반복 실행해도 안전하다.
BEGIN;

-- documentType: 信息 → 정보, 数据 → 데이터
UPDATE cms_document     SET xdocumenttype = '정보'   WHERE xdocumenttype = '信息';
UPDATE cms_document     SET xdocumenttype = '데이터' WHERE xdocumenttype = '数据';
UPDATE cms_appinfo      SET xdocumenttype = '정보'   WHERE xdocumenttype = '信息';
UPDATE cms_appinfo      SET xdocumenttype = '데이터' WHERE xdocumenttype = '数据';
UPDATE cms_categoryinfo SET xdocumenttype = '정보'   WHERE xdocumenttype = '信息';
UPDATE cms_categoryinfo SET xdocumenttype = '데이터' WHERE xdocumenttype = '数据';
UPDATE cms_review       SET xdocumenttype = '정보'   WHERE xdocumenttype = '信息';
UPDATE cms_review       SET xdocumenttype = '데이터' WHERE xdocumenttype = '数据';

-- appType / 템플릿 분류: 未分类 → 미분류
UPDATE cms_appinfo      SET xapptype = '미분류' WHERE xapptype = '未分类';
UPDATE cms_templateform SET xcategory = '미분류' WHERE xcategory = '未分类';

-- workflowType: 禁用审批流 → 결재 프로세스 사용 안 함
UPDATE cms_categoryinfo SET xworkflowtype = '결재 프로세스 사용 안 함' WHERE xworkflowtype = '禁用审批流';

-- 댓글 검토 상태: 无审核 → 검토 없음, 通过 → 통과
UPDATE cms_document_commentinfo SET xcommentauditstatus = '검토 없음' WHERE xcommentauditstatus = '无审核';
UPDATE cms_document_commentinfo SET xcommentauditstatus = '통과'     WHERE xcommentauditstatus = '通过';

-- 권한 목록 컨테이너 테이블: 所有人 → 모든 사용자
UPDATE cms_document_readpersonlist       SET xreadpersonlist       = '모든 사용자' WHERE xreadpersonlist       = '所有人';
UPDATE cms_document_authorpersonlist     SET xauthorpersonlist     = '모든 사용자' WHERE xauthorpersonlist     = '所有人';
UPDATE cms_document_readunitlist         SET xreadunitlist         = '모든 사용자' WHERE xreadunitlist         = '所有人';
UPDATE cms_document_readgrouplist        SET xreadgrouplist        = '모든 사용자' WHERE xreadgrouplist        = '所有人';
UPDATE cms_appinfo_viewablepersonlist    SET xviewablepersonlist   = '모든 사용자' WHERE xviewablepersonlist   = '所有人';
UPDATE cms_categoryinfo_viewablepersonlist SET xviewablepersonlist = '모든 사용자' WHERE xviewablepersonlist   = '所有人';

-- CMS 문서 데이터 스냅샷($document)에 복사된 같은 값
UPDATE qry_item SET xstringshortvalue = '정보'
 WHERE xitemcategory = 'cms' AND xpath0 = '$document' AND xpath1 = 'documentType' AND xstringshortvalue = '信息';
UPDATE qry_item SET xstringshortvalue = '데이터'
 WHERE xitemcategory = 'cms' AND xpath0 = '$document' AND xpath1 = 'documentType' AND xstringshortvalue = '数据';
UPDATE qry_item SET xstringshortvalue = '모든 사용자'
 WHERE xitemcategory = 'cms' AND xpath0 = '$document'
   AND xpath1 IN ('readPersonList', 'authorPersonList', 'readUnitList', 'readGroupList')
   AND xstringshortvalue = '所有人';

-- 가져오기 모델 설정 JSON의 documentType (테이블이 없는 환경도 있다)
DO $$
BEGIN
  IF to_regclass('qry_import_model') IS NOT NULL THEN
    UPDATE qry_import_model
       SET xdata = regexp_replace(xdata, '("documentType"\s*:\s*")信息(")', '\1정보\2', 'g')
     WHERE xdata ~ '"documentType"\s*:\s*"信息"';
    UPDATE qry_import_model
       SET xdata = regexp_replace(xdata, '("documentType"\s*:\s*")数据(")', '\1데이터\2', 'g')
     WHERE xdata ~ '"documentType"\s*:\s*"数据"';
  END IF;
END $$;

COMMIT;
