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

import com.x.attendance.entity.AttendanceQywxDetail;
import com.x.attendance.entity.AttendanceQywxDetail_;
import com.x.attendance.entity.DingdingQywxSyncRecord;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.Application;
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

public class QueueQywxAttendanceSync extends AbstractQueue<DingdingQywxSyncRecord> {

    private static final Logger logger = LoggerFactory.getLogger(QueueQywxAttendanceSync.class);


    @Override
    protected void execute(DingdingQywxSyncRecord record) throws Exception {
        logger.info("WeCom 출퇴근 데이터 동기화 시작, from:" + record.getDateFrom() + ", to:"+record.getDateTo());
        if (DingdingQywxSyncRecord.syncType_qywx.equals(record.getType())) {
            try {
                qywxSync(record);
            }catch (Exception e) {
                logger.error(e);
                updateSyncRecord(record, e.getMessage());
            }
        }
    }


    private void qywxSync(DingdingQywxSyncRecord record) throws Exception{
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
        deleteQywxAttendance(fromDate, toDate);
        //사용자 조회 주소
        String uri = "person/list/(0)/next/50";
        String qywxuri = Config.qiyeweixin().getApiAddress() + "/cgi-bin/checkin/getcheckindata?access_token="+ Config.qiyeweixin().attendanceAccessToken();
        int saveNumber = 0;
        while (hasNextPerson) {
            List<Person> list = ThisApplication.context().applications().getQuery(false, app, uri).getDataAsList(Person.class);
            if (list != null && list.size() > 0) {
                //DingTalk 사용자 id
                List<String> qywxUsers = list.stream().filter(person -> StringUtils.isNotEmpty(person.getQiyeweixinId()))
                        .map(Person::getQiyeweixinId).collect(Collectors.toList());

                if(ListTools.isNotEmpty(qywxUsers)){
                    QywxPost post = new QywxPost();
                    post.setStarttime(DateTools.toUnixTimeStamp(fromDate.getTime()));
                    post.setEndtime(DateTools.toUnixTimeStamp(toDate.getTime()));
                    post.setUseridlist(qywxUsers);
                    post.setOpencheckindatatype(3);//전체 출퇴근 정보
                    logger.info("WeCom post :" + post.toString());
                    QywxResult result = HttpConnection.postAsObject(qywxuri, null, post.toString(), QywxResult.class);
                    logger.info("반환 결과:"+result.toString());
                    if (result.errcode == 0) {
                        List<QywxResultItem> resultList = result.getCheckindata();
                        saveQywxAttendance(resultList, list);
                        saveNumber += resultList.size();
                    } else {
                        //요청 결과 이상, 종료
                        throw new ExceptionDingDingRequest(result.errmsg);
                    }

                }

                //사용자가 더 있는지 여부
                if (list.size() < personPageSize) {
                    logger.info("DingTalk 근태 동기화: 더 이상 사용자가 없어 종료!");
                    hasNextPerson = false;
                    updateSyncRecord(record, null);
                } else {
                    //사용자가 더 있어 계속 조회
                    uri = "person/list/" + list.get(list.size() - 1).getDistinguishedName() + "/next/50";
                }
            } else {
                //조회된 사용자가 없어 종료
                logger.info("DingTalk 근태 동기화: 조회되는 사용자가 없어 종료!");
                hasNextPerson = false;
                updateSyncRecord(record, null);
            }
        }
        logger.info("종료, 삽입:"+saveNumber+" 건");

        boolean hasNextDate = true;
        Date statisticDate = fromDate;
        while (hasNextDate) {
            logger.info("WeCom 근태 데이터 통계 시작, date:"+ DateTools.format(statisticDate));
            // ThisApplication.personQywxStatisticQueue.send(statisticDate);
            // ThisApplication.unitQywxStatisticQueue.send(statisticDate);
            if (!isSameDay(statisticDate, toDate)) {
                statisticDate = DateTools.addDay(statisticDate, 1);
            }else {
                hasNextDate = false;
            }
        }
        logger.info("데이터 통계 프로그램 시작 완료!");

    }

    private boolean isSameDay(Date startDate, Date endDate) {
        Calendar start = Calendar.getInstance();
        start.setTime( startDate );
        Calendar end = Calendar.getInstance();
        end.setTime(endDate);
        return (start.get(Calendar.YEAR) == end.get(Calendar.YEAR) && start.get(Calendar.DAY_OF_YEAR) == end.get(Calendar.DAY_OF_YEAR));
    }

    private void saveQywxAttendance(List<QywxResultItem> resultList, List<Person> personList) throws Exception {
        if (resultList != null && !resultList.isEmpty()) {
            try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
                Business business = new Business(emc);
                emc.beginTransaction(AttendanceQywxDetail.class);
                for (int i = 0; i < resultList.size(); i++) {
                    QywxResultItem item = resultList.get(i);
                    AttendanceQywxDetail detail = QywxResultItem.copier.copy(item);
                    if (detail.getCheckin_time() > 0) {
                        long timestamp = DateTools.toTimestamp(detail.getCheckin_time());
                        detail.setCheckin_time_date(new Date(timestamp));
                    }
                    //o2 조직과 사용자 추가
                    Optional<Person> first = personList.stream().filter(p -> item.userid.equals(p.getQiyeweixinId())).findFirst();
                    if (first.isPresent()) {
                        Person person = first.get();
                        String unit = getUnitWithPerson(person.getDistinguishedName(), business);
                        detail.setO2Unit(unit);
                        detail.setO2User(person.getDistinguishedName());
                    }
                    //정상 유형 설정
                    if (StringUtils.isEmpty(detail.getException_type())){
                        detail.setException_type(AttendanceQywxDetail.EXCEPTION_TYPE_NORMAL);
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

    private void deleteQywxAttendance(Date fromDate, Date toDate) throws Exception {
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            //먼저 삭제 후 동기화
            EntityManager em = emc.get(AttendanceQywxDetail.class);
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<AttendanceQywxDetail> query = cb.createQuery(AttendanceQywxDetail.class);
            Root<AttendanceQywxDetail> root = query.from(AttendanceQywxDetail.class);
            Predicate p = cb.between(root.get(AttendanceQywxDetail_.checkin_time_date), fromDate, toDate);
            query.select(root).where(p);
            List<AttendanceQywxDetail> detailList = em.createQuery(query).getResultList();
            //먼저 삭제
            logger.info("삭제");
            if (detailList != null) {
                logger.info("삭제 list:"+detailList.size());
                emc.beginTransaction(AttendanceQywxDetail.class);
                for (int i = 0; i < detailList.size(); i++) {
                    emc.remove(detailList.get(i));
                }
                emc.commit();
            }
            logger.info("삭제 종료");
        }
    }


    private void updateSyncRecord(DingdingQywxSyncRecord record, String message) throws Exception {
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            emc.beginTransaction(DingdingQywxSyncRecord.class);
            DingdingQywxSyncRecord entity = emc.find(record.getId(), DingdingQywxSyncRecord.class);
            entity.setEndTime(new Date());
            if (message == null || message.isEmpty()) {
                entity.setStatus(DingdingQywxSyncRecord.status_end);
            } else {
                entity.setStatus(DingdingQywxSyncRecord.status_error);
                entity.setExceptionMessage(message);
            }
            emc.commit();
        }
    }


    public static class QywxPost extends GsonPropertyObject {
        //{
        //   "opencheckindatatype": 3,
        //   "starttime": 1492617600,
        //   "endtime": 1492790400,
        //   "useridlist": ["james","paul"]
        //}

        //출퇴근 유형. 1: 출퇴근 체크; 2: 외근 체크; 3: 전체 체크
        private int opencheckindatatype;
        private long starttime;
        private long endtime;
        private List<String> useridlist;

        public int getOpencheckindatatype() {
            return opencheckindatatype;
        }

        public void setOpencheckindatatype(int opencheckindatatype) {
            this.opencheckindatatype = opencheckindatatype;
        }

        public long getStarttime() {
            return starttime;
        }

        public void setStarttime(long starttime) {
            this.starttime = starttime;
        }

        public long getEndtime() {
            return endtime;
        }

        public void setEndtime(long endtime) {
            this.endtime = endtime;
        }

        public List<String> getUseridlist() {
            return useridlist;
        }

        public void setUseridlist(List<String> useridlist) {
            this.useridlist = useridlist;
        }
    }

    public static class QywxResult extends GsonPropertyObject {
        private int errcode;
        private String errmsg;
        private List<QywxResultItem> checkindata;

        public int getErrcode() {
            return errcode;
        }

        public void setErrcode(int errcode) {
            this.errcode = errcode;
        }

        public String getErrmsg() {
            return errmsg;
        }

        public void setErrmsg(String errmsg) {
            this.errmsg = errmsg;
        }

        public List<QywxResultItem> getCheckindata() {
            return checkindata;
        }

        public void setCheckindata(List<QywxResultItem> checkindata) {
            this.checkindata = checkindata;
        }
    }
    public static class QywxResultItem extends GsonPropertyObject {


        static WrapCopier<QywxResultItem, AttendanceQywxDetail> copier = WrapCopierFactory.wi(QywxResultItem.class,
                AttendanceQywxDetail.class, null, JpaObject.FieldsUnmodify);

        private String userid;
        private String groupname;
        private String checkin_type;
        private long checkin_time;
        private String exception_type;
        private String location_title;
        private String location_detail;
        private String wifiname;
        private String notes;

        public String getUserid() {
            return userid;
        }

        public void setUserid(String userid) {
            this.userid = userid;
        }

        public String getGroupname() {
            return groupname;
        }

        public void setGroupname(String groupname) {
            this.groupname = groupname;
        }

        public String getCheckin_type() {
            return checkin_type;
        }

        public void setCheckin_type(String checkin_type) {
            this.checkin_type = checkin_type;
        }

        public long getCheckin_time() {
            return checkin_time;
        }

        public void setCheckin_time(long checkin_time) {
            this.checkin_time = checkin_time;
        }

        public String getException_type() {
            return exception_type;
        }

        public void setException_type(String exception_type) {
            this.exception_type = exception_type;
        }

        public String getLocation_title() {
            return location_title;
        }

        public void setLocation_title(String location_title) {
            this.location_title = location_title;
        }

        public String getLocation_detail() {
            return location_detail;
        }

        public void setLocation_detail(String location_detail) {
            this.location_detail = location_detail;
        }

        public String getWifiname() {
            return wifiname;
        }

        public void setWifiname(String wifiname) {
            this.wifiname = wifiname;
        }

        public String getNotes() {
            return notes;
        }

        public void setNotes(String notes) {
            this.notes = notes;
        }
    }
}
