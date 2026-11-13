package com.x.attendance.assemble.control.schedule.v2;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.quartz.JobExecutionContext;

import com.google.gson.reflect.TypeToken;
import com.x.attendance.entity.v2.AttendanceV2Holiday;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.schedule.AbstractJob;

/**
 * 한국 공휴일을 동기화한다. Nager.Date 공개 API(대한민국, 대체공휴일 반영된 실제 쉬는 날)를 읽어
 * 해당 연도 데이터가 없을 때만 휴일로 저장한다. 한국에는 주말 대체 근무일이 없으므로 모두 휴일이다.
 */
public class AttendanceV2HolidaySyncTask extends AbstractJob {

    private static final Logger logger = LoggerFactory.getLogger(AttendanceV2HolidaySyncTask.class);
    private static final String HOLIDAY_URL = "https://date.nager.at/api/v3/PublicHolidays/%d/KR";
    private static final int CONNECT_TIMEOUT = 10000;
    private static final int READ_TIMEOUT = 10000;

    @Override
    public void schedule(JobExecutionContext jobExecutionContext) throws Exception {
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        syncYear(currentYear);
        syncYear(currentYear + 1);
    }

    private void syncYear(int year) {
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            if (existsYearData(emc, year)) {
                if (logger.isDebugEnabled()) {
                    logger.debug("{}년 공휴일 데이터가 이미 있어 동기화를 건너뜁니다.", year);
                }
                return;
            }
            List<HolidaySourceDay> days = fetchHolidays(year);
            if (days == null) {
                return;
            }
            if (days.isEmpty()) {
                logger.warn("{}년 공휴일 데이터가 비어 있어 저장하지 않습니다.", year);
                return;
            }
            int persistCount = persistDays(emc, year, days);
            logger.info("{}년 공휴일 동기화 완료: {}건 저장.", year, persistCount);
        } catch (Exception e) {
            logger.error(e);
        }
    }

    private boolean existsYearData(EntityManagerContainer emc, int year) throws Exception {
        List<AttendanceV2Holiday> list = emc.listEqualAndEqual(AttendanceV2Holiday.class,
                AttendanceV2Holiday.year_FIELDNAME, year, AttendanceV2Holiday.source_FIELDNAME,
                AttendanceV2Holiday.SOURCE_SYNC);
        return list != null && !list.isEmpty();
    }

    private List<HolidaySourceDay> fetchHolidays(int year) {
        String url = String.format(HOLIDAY_URL, year);
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setConnectTimeout(CONNECT_TIMEOUT);
            connection.setReadTimeout(READ_TIMEOUT);
            connection.setRequestMethod("GET");
            int code = connection.getResponseCode();
            if (code < 200 || code >= 300) {
                logger.warn("공휴일 데이터를 읽지 못했습니다. 연도: {}, 응답 코드: {}, 주소: {}", year, code, url);
                return null;
            }
            StringBuilder builder = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
            }
            if (builder.length() == 0) {
                logger.warn("공휴일 데이터를 읽지 못했습니다. 연도: {}, 응답이 비어 있습니다. 주소: {}", year, url);
                return null;
            }
            List<HolidaySourceDay> days = XGsonBuilder.instance().fromJson(builder.toString(),
                    new TypeToken<List<HolidaySourceDay>>() {
                    }.getType());
            return days == null ? new ArrayList<>() : days;
        } catch (Exception e) {
            logger.warn("공휴일 데이터를 읽지 못했습니다. 연도: {}, 원격 주소에 접속할 수 없습니다: {}", year, url);
            return null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private int persistDays(EntityManagerContainer emc, int year, List<HolidaySourceDay> days) throws Exception {
        int count = 0;
        Set<String> dateSet = new HashSet<>();
        List<AttendanceV2Holiday> existingList = emc.listEqual(AttendanceV2Holiday.class,
                AttendanceV2Holiday.year_FIELDNAME, year);
        if (existingList != null) {
            for (AttendanceV2Holiday holiday : existingList) {
                if (StringUtils.isNotBlank(holiday.getDateString())) {
                    dateSet.add(holiday.getDateString());
                }
            }
        }
        emc.beginTransaction(AttendanceV2Holiday.class);
        for (HolidaySourceDay day : days) {
            if (day == null || StringUtils.isBlank(day.getDate()) || !dateSet.add(day.getDate())) {
                continue;
            }
            AttendanceV2Holiday holiday = new AttendanceV2Holiday();
            holiday.setYear(year);
            holiday.setDateString(day.getDate());
            holiday.setName(StringUtils.defaultIfBlank(day.getLocalName(), day.getName()));
            holiday.setOffDay(true);
            holiday.setSource(AttendanceV2Holiday.SOURCE_SYNC);
            emc.persist(holiday, CheckPersistType.all);
            count++;
        }
        emc.commit();
        return count;
    }

    /** Nager.Date PublicHoliday 항목 중 쓰는 필드. */
    public static class HolidaySourceDay extends GsonPropertyObject {

        private static final long serialVersionUID = 7082737076206215952L;

        private String date;
        private String localName;
        private String name;

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getLocalName() {
            return localName;
        }

        public void setLocalName(String localName) {
            this.localName = localName;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
