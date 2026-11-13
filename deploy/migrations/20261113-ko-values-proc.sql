-- PROC 도메인(calendar, meeting, mind, portal, process, query) 저장값 중국어 → 한국어 이전.
-- 반복 실행해도 안전하다. 아직 생성되지 않은 테이블은 건너뛴다.
BEGIN;

DO $$
BEGIN
  -- calendar
  IF to_regclass('cal_setting') IS NOT NULL THEN
    UPDATE cal_setting SET xselectcontent = '--없음--' WHERE xselectcontent = '--无--';
  END IF;
  IF to_regclass('cal_event_repm') IS NOT NULL THEN
    UPDATE cal_event_repm SET xrepeatstatus = '생성 대기' WHERE xrepeatstatus = '等待生成';
  END IF;

  -- meeting: mt_config.xproperties JSON 의 onlineProduct
  IF to_regclass('mt_config') IS NOT NULL THEN
    UPDATE mt_config
       SET xproperties = regexp_replace(xproperties, '("onlineProduct"\s*:\s*")好视通(")', '\1FSMeeting\2', 'g')
     WHERE xproperties ~ '"onlineProduct"\s*:\s*"好视通"';
    UPDATE mt_config
       SET xproperties = regexp_replace(xproperties, '("onlineProduct"\s*:\s*")其他(")', '\1기타\2', 'g')
     WHERE xproperties ~ '"onlineProduct"\s*:\s*"其他"';
  END IF;

  -- mind
  IF to_regclass('mind_share_recored') IS NOT NULL THEN
    UPDATE mind_share_recored SET xsharetype = '공유' WHERE xsharetype = '分享';
    UPDATE mind_share_recored SET xfilestatus = '정상' WHERE xfilestatus = '正常';
  END IF;

  -- portal
  IF to_regclass('ptl_portal') IS NOT NULL THEN
    UPDATE ptl_portal SET xportalcategory = '미분류' WHERE xportalcategory = '未分类';
  END IF;

  -- process
  IF to_regclass('pp_c_handover') IS NOT NULL THEN
    UPDATE pp_c_handover SET xtype = '교체' WHERE xtype = '替换';
    UPDATE pp_c_handover SET xtype = '위임' WHERE xtype = '授权';
  END IF;

  -- query: 가져오기 기록 상태
  IF to_regclass('qry_import_record') IS NOT NULL THEN
    UPDATE qry_import_record SET xstatus = CASE xstatus
        WHEN '待导入' THEN '가져오기 대기'
        WHEN '导入中' THEN '가져오는 중'
        WHEN '导入成功' THEN '가져오기 성공'
        WHEN '部分成功' THEN '일부 가져오기 성공'
        WHEN '导入失败' THEN '가져오기 실패' END
     WHERE xstatus IN ('待导入', '导入中', '导入成功', '部分成功', '导入失败');
  END IF;
  IF to_regclass('qry_import_record_item') IS NOT NULL THEN
    UPDATE qry_import_record_item SET xstatus = CASE xstatus
        WHEN '待导入' THEN '가져오기 대기'
        WHEN '导入中' THEN '가져오는 중'
        WHEN '导入成功' THEN '가져오기 성공'
        WHEN '部分成功' THEN '일부 가져오기 성공'
        WHEN '导入失败' THEN '가져오기 실패' END
     WHERE xstatus IN ('待导入', '导入中', '导入成功', '部分成功', '导入失败');
  END IF;
END
$$;

COMMIT;
