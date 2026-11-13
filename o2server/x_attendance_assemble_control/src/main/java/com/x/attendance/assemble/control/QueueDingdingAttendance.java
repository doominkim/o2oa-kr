package com.x.attendance.assemble.control;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

import com.x.attendance.entity.AttendanceDingtalkDetail;
import com.x.attendance.entity.AttendanceDingtalkDetail_;
import com.x.attendance.entity.DingdingQywxSyncRecord;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.Application;
import com.x.base.core.project.Applications;
import com.x.base.core.project.x_organization_assemble_control;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.connection.HttpConnection;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.organization.Person;
import com.x.base.core.project.organization.Unit;
import com.x.base.core.project.queue.AbstractQueue;
import com.x.base.core.project.tools.DateTools;
import com.x.base.core.project.tools.ListTools;

public class QueueDingdingAttendance extends AbstractQueue<DingdingQywxSyncRecord> {

    private static final Logger logger = LoggerFactory.getLogger(QueueDingdingAttendance.class);

    @Override
    protected void execute(DingdingQywxSyncRecord record) throws Exception {
        logger.info("DingTalk 출퇴근 데이터 동기화 시작, from:" + record.getDateFrom() + ", to:"+record.getDateTo());
        if (DingdingQywxSyncRecord.syncType_dingding.equals(record.getType())) {
            try {
                dingdingSync(record);
            }catch (Exception e) {
                logger.error(e);
                updateSyncRecord(record, e.getMessage());
            }
        }
    }

    private boolean isSameDay(Date startDate, Date endDate) {
        Calendar start = Calendar.getInstance();
        start.setTime( startDate );
        Calendar end = Calendar.getInstance();
        end.setTime(endDate);
        return (start.get(Calendar.YEAR) == end.get(Calendar.YEAR) && start.get(Calendar.DAY_OF_YEAR) == end.get(Calendar.DAY_OF_YEAR));
    }
    private void dingdingSync(DingdingQywxSyncRecord record) throws Exception {
        Application app = ThisApplication.context().applications().randomWithWeight(x_organization_assemble_control.class.getName());
        //사용자 페이지 조회 시작
        boolean hasNextPerson = true;
        int personPageSize = 50;
        //시작 시간과 종료 시간
        Date fromDate = new Date();
        fromDate.setTime(record.getDateFrom());
        Date toDate =new Date();
        toDate.setTime(record.getDateTo());
        //먼저 삭제
        deleteDingdingAttendance(fromDate, toDate);
        //사용자 조회 주소
        String uri = "person/list/(0)/next/50";
        //DingTalk 근태 동기화 API 주소
        String dingdingUrl = Config.dingding().getOapiAddress() + "/attendance/list?access_token=" + Config.dingding().corpAccessToken();
        int saveNumber = 0;
        while (hasNextPerson) {
            logger.info("uri:"+ uri);
            List<Person> list = ThisApplication.context().applications().getQuery(false, app, uri).getDataAsList(Person.class);
            if (list != null && list.size() > 0) {
                //DingTalk 사용자 id
                List<String> ddUsers = list.stream().filter(person -> StringUtils.isNotEmpty(person.getDingdingId()))
                        .map(Person::getDingdingId).collect(Collectors.toList());
                if (ListTools.isNotEmpty(ddUsers)) {
                    //페이지 조회
                    int page = 0;
                    boolean hasMoreResult = true;
                    while (hasMoreResult) {
                        DingdingAttendancePost post = new DingdingAttendancePost();
                        //post 전달 시간(간격은 7일 이하) 사용자(인원은 50명 이하)
                        post.setLimit(50L);
                        post.setOffset(page * 50L);//0부터 페이지 넘김
                        //여기서 시작 시간과 종료 시간은 DingTalk 반환 결과의 workDate를 조회
                        //근태 출퇴근 기록 조회 시작 근무일
                        post.setWorkDateFrom(DateTools.format(fromDate));
                        //근태 출퇴근 기록 조회 종료 근무일
                        post.setWorkDateTo(DateTools.format(toDate));
                        post.setUserIdList(ddUsers);
                        DingdingAttendanceResult result = HttpConnection.postAsObject(dingdingUrl, null, post.toString(), DingdingAttendanceResult.class);
                        if (result.errcode != null && result.errcode == 0) {
                            List<DingdingAttendanceResultItem> resultList = result.getRecordresult();
                            saveDingdingAttendance(resultList, list);
                            saveNumber += resultList.size();
                            if (result.hasMore) {
                                page++;
                            } else {
                                logger.info("DingTalk 근태 동기화 종료!");
                                hasMoreResult = false;
                            }
                        } else {
                            //요청 결과 이상, 종료
                            throw new ExceptionDingDingRequest(result.errmsg);
                        }
                    }
                }
                //사용자가 더 있는지 여부
                if (list.size() < personPageSize) {
                    logger.info("DingTalk 근태 동기화: 더 이상 사용자가 없어 종료!");
                    hasNextPerson = false;
                    updateSyncRecord(record, null);
                } else {
                    //사용자가 더 있어 계속 조회
                    uri = Applications.joinQueryUri("person","list", list.get(list.size() - 1).getDistinguishedName(), "next", "50");
//                    uri = "person/list/" + list.get(list.size() - 1).getDistinguishedName() + "/next/50";
                }
            } else {
                //조회된 사용자가 없어 종료
                logger.info("DingTalk 근태 동기화: 조회되는 사용자가 없어 종료!");
                hasNextPerson = false;
                updateSyncRecord(record, null);
            }
        }
        logger.info("종료, 삽입:"+saveNumber+" 건");
        //데이터 삽입 성공, 통계 프로그램 시작

        boolean hasNextDate = true;
        Date statisticDate = fromDate;
        while (hasNextDate) {
            logger.info("DingTalk 근태 데이터 통계 시작, date:"+ DateTools.format(statisticDate));
            // ThisApplication.personStatisticQueue.send(statisticDate);
            // ThisApplication.unitStatisticQueue.send(statisticDate);
            if (!isSameDay(statisticDate, toDate)) {
                statisticDate = DateTools.addDay(statisticDate, 1);
            }else {
                hasNextDate = false;
            }
        }
        logger.info("데이터 통계 프로그램 시작 완료!");
    }
    private void deleteDingdingAttendance(Date fromDate, Date toDate) throws Exception{
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            //먼저 삭제 후 동기화
            EntityManager em = emc.get(AttendanceDingtalkDetail.class);
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<AttendanceDingtalkDetail> query = cb.createQuery(AttendanceDingtalkDetail.class);
            Root<AttendanceDingtalkDetail> root = query.from(AttendanceDingtalkDetail.class);
            long start = fromDate.getTime();
            long end = toDate.getTime();
            Predicate p = cb.between(root.get(AttendanceDingtalkDetail_.workDate), start, end);
            query.select(root).where(p);
            List<AttendanceDingtalkDetail> detailList = em.createQuery(query).getResultList();
            //먼저 삭제
            logger.info("삭제");
            if (detailList != null) {
                logger.info("삭제 list:"+detailList.size());
                emc.beginTransaction(AttendanceDingtalkDetail.class);
                for (int i = 0; i < detailList.size(); i++) {
                    emc.remove(detailList.get(i));
                }
                emc.commit();
            }
            logger.info("삭제 종료");
        }
    }

    private void saveDingdingAttendance(List<DingdingAttendanceResultItem> list, List<Person> personList) throws Exception {
        if (list != null && !list.isEmpty()) {
            try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
                Business business = new Business(emc);
                emc.beginTransaction(AttendanceDingtalkDetail.class);
                for (int i = 0; i < list.size(); i++) {
                    DingdingAttendanceResultItem item = list.get(i);
                    AttendanceDingtalkDetail detail = DingdingAttendanceResultItem.copier.copy(item);
                    detail.setDdId(item.getId());
                    if (detail.getUserCheckTime() > 0) {
                        Date date = new Date(detail.getUserCheckTime());
                        detail.setUserCheckTimeDate(date);
                    }
                    //o2 조직과 사용자 추가
                    Optional<Person> first = personList.stream().filter(p -> item.userId.equals(p.getDingdingId())).findFirst();
                    if (first.isPresent()) {
                        Person person = first.get();
                        String unit = getUnitWithPerson(person.getDistinguishedName(), business);
                        detail.setO2Unit(unit);
                        detail.setO2User(person.getDistinguishedName());
                    }
                    emc.persist(detail);
                }
                emc.commit();
            }
        }
    }
    private String getUnitWithPerson(String person, Business business) throws Exception {
        String result = null;
        Integer level = 0;
        Unit unit = null;
        List<String> unitNames = business.organization().unit().listWithPerson( person );
        if( ListTools.isNotEmpty( unitNames ) ) {
            for( String unitName : unitNames ) {
                if( StringUtils.isNotEmpty( unitName ) && !"null".equals( unitName ) ) {
                    unit = business.organization().unit().getObject( unitName );
                    if( level < unit.getLevel() ) {
                        level = unit.getLevel();
                        result = unitName;
                    }
                }
            }
        }
        return result;
    }

    private void updateSyncRecord(DingdingQywxSyncRecord record, String errMsg) throws Exception {
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            emc.beginTransaction(DingdingQywxSyncRecord.class);
            DingdingQywxSyncRecord entity = emc.find(record.getId(), DingdingQywxSyncRecord.class);
            entity.setEndTime(new Date());
            if (errMsg == null || errMsg.isEmpty()) {
                entity.setStatus(DingdingQywxSyncRecord.status_end);
            } else {
                entity.setStatus(DingdingQywxSyncRecord.status_error);
                entity.setExceptionMessage(errMsg);
            }
            emc.commit();
        }
    }


    public static class DingdingAttendancePost extends GsonPropertyObject {
        //        {
//            "workDateFrom": "yyyy-MM-dd HH:mm:ss",
//                "workDateTo": "yyyy-MM-dd HH:mm:ss",
//                "userIdList":["직원 UserId 목록"],    // 필수, offset·limit과 함께 사용
//            "offset":0,    // 필수, 처음에는 0, 남은 데이터가 있으면 다음에는 이전 offset에 limit 값을 더해 전달
//                "limit":1,     // 필수, 데이터 건수, 최대 50건을 넘을 수 없음
//        }
        private String workDateFrom;
        private String workDateTo;
        private List<String> userIdList;
        private Long offset;
        private Long limit;

        public String getWorkDateFrom() {
            return workDateFrom;
        }

        public void setWorkDateFrom(String workDateFrom) {
            this.workDateFrom = workDateFrom;
        }

        public String getWorkDateTo() {
            return workDateTo;
        }

        public void setWorkDateTo(String workDateTo) {
            this.workDateTo = workDateTo;
        }

        public List<String> getUserIdList() {
            return userIdList;
        }

        public void setUserIdList(List<String> userIdList) {
            this.userIdList = userIdList;
        }

        public Long getOffset() {
            return offset;
        }

        public void setOffset(Long offset) {
            this.offset = offset;
        }

        public Long getLimit() {
            return limit;
        }

        public void setLimit(Long limit) {
            this.limit = limit;
        }
    }

    public static class DingdingAttendanceResult extends GsonPropertyObject {
        private Integer errcode;
        private String errmsg;
        private Boolean hasMore;
        private List<DingdingAttendanceResultItem> recordresult;

        public Integer getErrcode() {
            return errcode;
        }

        public void setErrcode(Integer errcode) {
            this.errcode = errcode;
        }

        public String getErrmsg() {
            return errmsg;
        }

        public void setErrmsg(String errmsg) {
            this.errmsg = errmsg;
        }

        public Boolean getHasMore() {
            return hasMore;
        }

        public void setHasMore(Boolean hasMore) {
            this.hasMore = hasMore;
        }

        public List<DingdingAttendanceResultItem> getRecordresult() {
            return recordresult;
        }

        public void setRecordresult(List<DingdingAttendanceResultItem> recordresult) {
            this.recordresult = recordresult;
        }
    }

    public static class DingdingAttendanceResultItem extends GsonPropertyObject {

        private static final long serialVersionUID = 1618421987561110713L;

        static WrapCopier<DingdingAttendanceResultItem, AttendanceDingtalkDetail> copier = WrapCopierFactory.wi(DingdingAttendanceResultItem.class,
                AttendanceDingtalkDetail.class, null, JpaObject.FieldsUnmodify);

        private long id;
        private long ddId;
        private String userId;
        private long baseCheckTime;
        private long userCheckTime;
        private long workDate;
        private String timeResult;
        private String checkType;
        private String locationResult;
        private String sourceType;
        private long groupId;
        private long planId;
        private long recordId;

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public long getDdId() {
            return ddId;
        }

        public void setDdId(long ddId) {
            this.ddId = ddId;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public long getBaseCheckTime() {
            return baseCheckTime;
        }

        public void setBaseCheckTime(long baseCheckTime) {
            this.baseCheckTime = baseCheckTime;
        }

        public long getUserCheckTime() {
            return userCheckTime;
        }

        public void setUserCheckTime(long userCheckTime) {
            this.userCheckTime = userCheckTime;
        }

        public long getWorkDate() {
            return workDate;
        }

        public void setWorkDate(long workDate) {
            this.workDate = workDate;
        }

        public String getTimeResult() {
            return timeResult;
        }

        public void setTimeResult(String timeResult) {
            this.timeResult = timeResult;
        }

        public String getCheckType() {
            return checkType;
        }

        public void setCheckType(String checkType) {
            this.checkType = checkType;
        }

        public String getLocationResult() {
            return locationResult;
        }

        public void setLocationResult(String locationResult) {
            this.locationResult = locationResult;
        }

        public String getSourceType() {
            return sourceType;
        }

        public void setSourceType(String sourceType) {
            this.sourceType = sourceType;
        }

        public long getGroupId() {
            return groupId;
        }

        public void setGroupId(long groupId) {
            this.groupId = groupId;
        }

        public long getPlanId() {
            return planId;
        }

        public void setPlanId(long planId) {
            this.planId = planId;
        }

        public long getRecordId() {
            return recordId;
        }

        public void setRecordId(long recordId) {
            this.recordId = recordId;
        }
    }
}
