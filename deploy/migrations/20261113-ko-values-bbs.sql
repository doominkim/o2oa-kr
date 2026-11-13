-- BBS(포럼) 저장값 중국어 -> 한국어 이전. 반복 실행해도 안전하다.
-- '|' 구분자 목록은 원소 단위로만 바꾼다(다른 단어 일부는 바뀌지 않는다).
-- 운영 DB에 없는 테이블(포럼 미사용으로 생성되지 않은 경우)은 건너뛴다.
BEGIN;

CREATE TEMP TABLE ko_bbs_map (zh text PRIMARY KEY, ko text NOT NULL) ON COMMIT DROP;
INSERT INTO ko_bbs_map (zh, ko) VALUES
  ('--无--', '--없음--'),
  ('O2社区', 'O2 커뮤니티'),
  ('主板块', '상위 섹션'),
  ('主版块', '상위 섹션'),
  ('子版块', '하위 섹션'),
  ('人员', '사용자'),
  ('组织', '조직'),
  ('群组', '그룹'),
  ('信息', '정보'),
  ('问题', '질문'),
  ('投票', '투표'),
  ('新闻', '뉴스'),
  ('讨论', '토론'),
  ('启用', '사용'),
  ('停用', '사용 중지'),
  ('已锁定', '잠김'),
  ('其他权限', '기타 권한'),
  ('论坛权限', '포럼 권한'),
  ('版块权限', '섹션 권한'),
  ('论坛角色', '포럼 역할'),
  ('版块角色', '섹션 역할'),
  ('匿名', '익명'),
  ('审核通过', '검토 통과'),
  ('无审核', '검토 없음'),
  ('所有人', '모든 사용자'),
  ('根据权限', '권한에 따라'),
  ('文字', '텍스트'),
  ('未知', '알 수 없음'),
  ('未知类别', '알 수 없는 분류'),
  ('经典', '클래식');

DO $$
DECLARE
  tc record;
  n bigint;
BEGIN
  FOR tc IN SELECT * FROM (VALUES
    ('bbs_configsetting', 'xconfigvalue'),
    ('bbs_configsetting', 'xselectcontent'),
    ('bbs_foruminfo', 'xforumstatus'),
    ('bbs_foruminfo', 'xforumvisible'),
    ('bbs_foruminfo', 'xsubjectpublishable'),
    ('bbs_foruminfo', 'xreplypublishable'),
    ('bbs_foruminfo', 'xindexliststyle'),
    ('bbs_foruminfo', 'xforumindexstyle'),
    ('bbs_foruminfo', 'xsubjecttype'),
    ('bbs_foruminfo', 'xtypecategory'),
    ('bbs_sectioninfo', 'xsectionstatus'),
    ('bbs_sectioninfo', 'xsectionlevel'),
    ('bbs_sectioninfo', 'xsectionvisible'),
    ('bbs_sectioninfo', 'xsubjectpublishable'),
    ('bbs_sectioninfo', 'xreplypublishable'),
    ('bbs_sectioninfo', 'xsectiontype'),
    ('bbs_sectioninfo', 'xsubjecttype'),
    ('bbs_sectioninfo', 'xtypecategory'),
    ('bbs_sectioninfo_subjecttypelist', 'xsubjecttypelist'),
    ('bbs_subjectinfo', 'xsubjectstatus'),
    ('bbs_subjectinfo', 'xtype'),
    ('bbs_subjectinfo', 'xtypecategory'),
    ('bbs_subjectinfo', 'xsubjectauditstatus'),
    ('bbs_replyinfo', 'xreplyauditstatus'),
    ('bbs_userinfo', 'xuserstatus'),
    ('bbs_userrole', 'xobjecttype'),
    ('bbs_roleinfo', 'xroletype'),
    ('bbs_permissioninfo', 'xpermissiontype'),
    ('bbs_permissioninfo', 'xpermissionfunction'),
    ('bbs_permissionrole', 'xpermissiontype'),
    ('bbs_operationrecord', 'xopttype'),
    ('bbs_voteoption', 'xoptioncontenttype')
  ) AS v(t, c) LOOP
    IF to_regclass(tc.t) IS NULL THEN
      RAISE NOTICE '% 없음, 건너뜀', tc.t;
      CONTINUE;
    END IF;
    EXECUTE format(
      'UPDATE %1$I SET %2$I = (SELECT string_agg(coalesce(m.ko, e.v), ''|'' ORDER BY e.i)
                                 FROM unnest(string_to_array(%2$I, ''|'')) WITH ORDINALITY AS e(v, i)
                                 LEFT JOIN ko_bbs_map m ON m.zh = e.v)
        WHERE EXISTS (SELECT 1 FROM unnest(string_to_array(%2$I, ''|'')) AS e(v)
                       JOIN ko_bbs_map m ON m.zh = e.v)', tc.t, tc.c);
    GET DIAGNOSTICS n = ROW_COUNT;
    RAISE NOTICE '%.%: % 행', tc.t, tc.c, n;
  END LOOP;

  IF to_regclass('bbs_subjectinfo') IS NOT NULL THEN
    UPDATE bbs_subjectinfo SET xnickname = '익명' WHERE xnickname = '匿名';
    GET DIAGNOSTICS n = ROW_COUNT;
    RAISE NOTICE 'bbs_subjectinfo.xnickname: % 행', n;
  END IF;
END
$$;

COMMIT;
