-- 근태(ATT) 저장값 중국어 → 한국어 이전. 반복 실행해도 안전하다.
-- 휴가 유형(attend_v2_leave_type)은 20261113-ko-defaults.sql 이 맡는다.
BEGIN;

-- v2 출퇴근 기록 출처 기기
UPDATE attend_v2_checkin_record SET xsourcedevice = '기타' WHERE xsourcedevice = '其他';

-- v2 외출 기록 유형
UPDATE attend_v2_leave_data SET xleavetype = '출장' WHERE xleavetype = '出差';
UPDATE attend_v2_leave_data SET xleavetype = '교육' WHERE xleavetype = '培训';
UPDATE attend_v2_leave_data SET xleavetype = '기타' WHERE xleavetype = '其他';

-- v2 설정: 근태 통계 관리자 직무 이름(JSON 문자열 원소)
UPDATE attend_v2_config
   SET xproperties = replace(xproperties, '"考勤管理员"', '"근태 관리자"')
 WHERE xproperties LIKE '%"考勤管理员"%';

-- v1 테이블(atdc_*)은 설치본에 따라 없을 수 있어 존재할 때만 바꾼다.
DO $$
BEGIN
  IF to_regclass('atdc_attendance_detail') IS NOT NULL THEN
    UPDATE atdc_attendance_detail SET xselfholidaydaytime = CASE xselfholidaydaytime
        WHEN '无' THEN '없음' WHEN '上午' THEN '오전' WHEN '下午' THEN '오후' WHEN '全天' THEN '종일' END
      WHERE xselfholidaydaytime IN ('无', '上午', '下午', '全天');
    UPDATE atdc_attendance_detail SET xabsentdaytime = CASE xabsentdaytime
        WHEN '无' THEN '없음' WHEN '上午' THEN '오전' WHEN '下午' THEN '오후' WHEN '全天' THEN '종일' END
      WHERE xabsentdaytime IN ('无', '上午', '下午', '全天');
    UPDATE atdc_attendance_detail SET xabnormaldutydaytime = CASE xabnormaldutydaytime
        WHEN '无' THEN '없음' WHEN '上午' THEN '오전' WHEN '下午' THEN '오후' WHEN '中午' THEN '점심' WHEN '全天' THEN '종일'
        WHEN '上午|下午' THEN '오전|오후' WHEN '上午|中午' THEN '오전|점심'
        WHEN '中午|下午' THEN '점심|오후' WHEN '上午|中午|下午' THEN '오전|점심|오후' END
      WHERE xabnormaldutydaytime IN ('无', '上午', '下午', '中午', '全天', '上午|下午', '上午|中午', '中午|下午', '上午|中午|下午');
  END IF;

  IF to_regclass('atdc_attendance_detail_mobile') IS NOT NULL THEN
    UPDATE atdc_attendance_detail_mobile SET xcheckin_type = CASE xcheckin_type
        WHEN '上午上班打卡' THEN '오전 출근 체크' WHEN '上午下班打卡' THEN '오전 퇴근 체크'
        WHEN '下午上班打卡' THEN '오후 출근 체크' WHEN '下午下班打卡' THEN '오후 퇴근 체크'
        WHEN '午间打卡' THEN '점심 체크' WHEN '上班打卡' THEN '출근 체크' END
      WHERE xcheckin_type IN ('上午上班打卡', '上午下班打卡', '下午上班打卡', '下午下班打卡', '午间打卡', '上班打卡');
    UPDATE atdc_attendance_detail_mobile SET xsigndescription = CASE xsigndescription
        WHEN '上午上班打卡' THEN '오전 출근 체크' WHEN '上午下班打卡' THEN '오전 퇴근 체크'
        WHEN '下午上班打卡' THEN '오후 출근 체크' WHEN '下午下班打卡' THEN '오후 퇴근 체크'
        WHEN '午间打卡' THEN '점심 체크' WHEN '上班打卡' THEN '출근 체크' END
      WHERE xsigndescription IN ('上午上班打卡', '上午下班打卡', '下午上班打卡', '下午下班打卡', '午间打卡', '上班打卡');
    UPDATE atdc_attendance_detail_mobile SET xoptmachinetype = '기타' WHERE xoptmachinetype = '其他';
    UPDATE atdc_attendance_detail_mobile SET xoptsystemname = '기타' WHERE xoptsystemname = '其他';
    UPDATE atdc_attendance_detail_mobile SET xrecordaddress = '알 수 없음' WHERE xrecordaddress = '未知';
  END IF;

  IF to_regclass('atdc_attendance_qywx') IS NOT NULL THEN
    UPDATE atdc_attendance_qywx SET xexception_type = '정상' WHERE xexception_type = '正常';
  END IF;

  IF to_regclass('atdc_attendance_setting') IS NOT NULL THEN
    -- 설정값(단일 값)
    UPDATE atdc_attendance_setting SET xconfigvalue = CASE xconfigvalue
        WHEN '无' THEN '없음' WHEN '指定人' THEN '지정 사용자' WHEN '个人属性' THEN '사용자 속성'
        WHEN '人员属性' THEN '사용자 속성' WHEN '汇报对象' THEN '보고 대상'
        WHEN '所属部门职务' THEN '소속 부서 직무' WHEN '所属部门职位' THEN '소속 부서 직무'
        WHEN '部门经理' THEN '부서장' WHEN '周六' THEN '토요일' WHEN '周日' THEN '일요일' END
      WHERE xconfigvalue IN ('无', '指定人', '个人属性', '人员属性', '汇报对象', '所属部门职务', '所属部门职位', '部门经理', '周六', '周日');
    -- 주말 설정 등 '|' 구분 목록: 원소 경계를 지켜 바꾼다.
    UPDATE atdc_attendance_setting SET xconfigvalue = array_to_string(ARRAY(
        SELECT CASE e WHEN '无' THEN '없음' WHEN '周六' THEN '토요일' WHEN '周日' THEN '일요일' ELSE e END
          FROM unnest(string_to_array(xconfigvalue, '|')) WITH ORDINALITY AS u(e, i) ORDER BY i), '|')
      WHERE xconfigvalue LIKE '%|%' AND string_to_array(xconfigvalue, '|') && ARRAY['无', '周六', '周日'];
    -- 선택지 목록
    UPDATE atdc_attendance_setting SET xselectcontent = '--없음--' WHERE xselectcontent = '--无--';
    UPDATE atdc_attendance_setting SET xselectcontent = array_to_string(ARRAY(
        SELECT CASE e WHEN '无' THEN '없음' WHEN '指定人' THEN '지정 사용자' WHEN '个人属性' THEN '사용자 속성'
                      WHEN '人员属性' THEN '사용자 속성' WHEN '汇报对象' THEN '보고 대상'
                      WHEN '所属部门职务' THEN '소속 부서 직무' WHEN '所属部门职位' THEN '소속 부서 직무'
                      WHEN '部门经理' THEN '부서장' WHEN '周六' THEN '토요일' WHEN '周日' THEN '일요일' ELSE e END
          FROM unnest(string_to_array(xselectcontent, '|')) WITH ORDINALITY AS u(e, i) ORDER BY i), '|')
      WHERE string_to_array(xselectcontent, '|') && ARRAY['无', '指定人', '个人属性', '人员属性', '汇报对象', '所属部门职务', '所属部门职位', '部门经理', '周六', '周日'];
  END IF;
END $$;

COMMIT;
