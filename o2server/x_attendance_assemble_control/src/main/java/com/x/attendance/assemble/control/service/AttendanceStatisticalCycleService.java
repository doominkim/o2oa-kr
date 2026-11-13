package com.x.attendance.assemble.control.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import com.x.attendance.assemble.common.date.DateOperation;
import com.x.attendance.assemble.control.Business;
import com.x.attendance.assemble.control.jaxrs.attendancestatisticalcycle.UnitCycleInfoEntity;
import com.x.attendance.entity.AttendanceStatisticRequireLog;
import com.x.attendance.entity.AttendanceStatisticalCycle;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.cache.Cache.CacheCategory;
import com.x.base.core.project.cache.Cache.CacheKey;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public class AttendanceStatisticalCycleService {

    private CacheCategory cache_AttendanceStatisticalCycle = new CacheCategory(AttendanceStatisticalCycle.class);

    private static Logger logger = LoggerFactory.getLogger(AttendanceStatisticalCycleService.class);
    private UserManagerService userManagerService = new UserManagerService();
    private DateOperation dateOperation = new DateOperation();

    /**
     * 캐시에서 모든 근태 주기 설정 조회
     * 
     * @return
     * @throws Exception
     */
    public Map<String, Map<String, List<AttendanceStatisticalCycle>>> getAllStatisticalCycleMapWithCache(
            Boolean debugger) throws Exception {
        CacheKey cacheKey = new CacheKey(this.getClass(), "map", "all");

        Map<String, Map<String, List<AttendanceStatisticalCycle>>> statisticalCycleMap = null;
        Optional<?> optional = CacheManager.get(cache_AttendanceStatisticalCycle, cacheKey);
        if (optional.isPresent()) {
            return ((Map<String, Map<String, List<AttendanceStatisticalCycle>>>) optional.get());
        } else {
            return getCycleMapFormAllCycles(false);
        }
    }

    public List<AttendanceStatisticalCycle> listAll(EntityManagerContainer emc) throws Exception {
        Business business = new Business(emc);
        return business.getAttendanceStatisticalCycleFactory().listAll();
    }

    public List<AttendanceStatisticalCycle> list(EntityManagerContainer emc, List<String> ids) throws Exception {
        Business business = new Business(emc);
        return business.getAttendanceStatisticalCycleFactory().list(ids);
    }

    public Map<String, Map<String, List<AttendanceStatisticalCycle>>> getCycleMapFormAllCycles(Boolean debugger)
            throws Exception {
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            return getCycleMapFormAllCycles(emc, debugger);
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * 모든 통계 주기 설정 정보를 하나의 큰 Map으로 구성
     * 
     * @param emc
     * @param debugger
     * @return
     * @throws Exception
     */
    public Map<String, Map<String, List<AttendanceStatisticalCycle>>> getCycleMapFormAllCycles(
            EntityManagerContainer emc, Boolean debugger) throws Exception {
        return getCycleMapFormAllCycles(emc, listAll(emc), debugger);
    }

    /**
     * 지정한 통계 주기 설정 정보 목록을 하나의 큰 Map으로 구성
     * 
     * @param emc
     * @param cycles
     * @param debugger
     * @return
     * @throws Exception
     */
    public Map<String, Map<String, List<AttendanceStatisticalCycle>>> getCycleMapFormAllCycles(
            EntityManagerContainer emc, List<AttendanceStatisticalCycle> cycles, Boolean debugger) throws Exception {
        Map<String, Map<String, List<AttendanceStatisticalCycle>>> topUnitAttendanceStatisticalCycleMap = new ConcurrentHashMap<String, Map<String, List<AttendanceStatisticalCycle>>>();
        Map<String, List<AttendanceStatisticalCycle>> unitAttendanceStatisticalCycleMap = null;
        List<AttendanceStatisticalCycle> unitCycles = null;
        if (cycles != null && cycles.size() > 0) {
            for (AttendanceStatisticalCycle cycle : cycles) {
                unitAttendanceStatisticalCycleMap = topUnitAttendanceStatisticalCycleMap.get(cycle.getTopUnitName());
                if (unitAttendanceStatisticalCycleMap == null) {
                    unitAttendanceStatisticalCycleMap = new ConcurrentHashMap<String, List<AttendanceStatisticalCycle>>();
                    topUnitAttendanceStatisticalCycleMap.put(cycle.getTopUnitName(), unitAttendanceStatisticalCycleMap);
                }
                unitCycles = unitAttendanceStatisticalCycleMap.get(cycle.getUnitName());
                if (unitCycles == null) {
                    unitCycles = new ArrayList<AttendanceStatisticalCycle>();
                    unitAttendanceStatisticalCycleMap.put(cycle.getUnitName(), unitCycles);
                }
                putDistinctCycleInList(cycle,
                        new UnitCycleInfoEntity(cycle.getUnitName(), cycle.getTopUnitName(), unitCycles),
                        topUnitAttendanceStatisticalCycleMap);
            }
        } else {
            logger.debug(debugger, ">>>>>>>>>>모든 통계 주기 설정을 조회했으나 설정이 없음.");
        }
        return topUnitAttendanceStatisticalCycleMap;
    }

    /**
     * 최상위 조직, 조직, 연월로 통계 주기 설정 조회, 없으면 새 주기 설정 생성
     * 
     * @param q_topUnitName
     * @param q_unitName
     * @param recordDate
     * @param topUnitAttendanceStatisticalCycleMap
     * @param debugger
     * @return
     * @throws Exception
     */
    public synchronized AttendanceStatisticalCycle getStatisticCycleWithStartAndEnd(String q_topUnitName,
            String q_unitName, Date recordDate,
            Map<String, Map<String, List<AttendanceStatisticalCycle>>> topUnitAttendanceStatisticalCycleMap,
            Boolean debugger) throws Exception {
        AttendanceStatisticalCycle attendanceStatisticalCycle = null;
        UnitCycleInfoEntity unitCycleInfoEntity = null;
        Date cycleStartDate = null;
        Date cycleEndDate = null;
        Boolean hasConfig = false;

        if (topUnitAttendanceStatisticalCycleMap == null) {
            topUnitAttendanceStatisticalCycleMap = getCycleMapFormAllCycles(debugger);
        }

        // Map에서 최상위 조직과 조직에 해당하는 주기 설정 정보 조회
        if (topUnitAttendanceStatisticalCycleMap != null) {
            unitCycleInfoEntity = analyseStatisticCycleWithUnitName(q_topUnitName, q_unitName,
                    topUnitAttendanceStatisticalCycleMap, debugger);
        } else {
            logger.debug(debugger, "통계 주기 설정 없음, 최상위 조직 *, 조직 *, 시스템에 설정이 전혀 없음");
            unitCycleInfoEntity = new UnitCycleInfoEntity();
            unitCycleInfoEntity.setTopUnitName("*");
            unitCycleInfoEntity.setUnitName("*");
        }
        if (unitCycleInfoEntity.getUnitCycles() != null && unitCycleInfoEntity.getUnitCycles().size() > 0) {
            // 설정 정보가 있으므로 설정 데이터가 출퇴근 기록에 필요한 주기를 충족하는지 확인
            for (AttendanceStatisticalCycle temp : unitCycleInfoEntity.getUnitCycles()) {
                // 연도가*
                cycleStartDate = temp.getCycleStartDate();
                cycleEndDate = temp.getCycleEndDate();
                if (recordDate.getTime() >= cycleStartDate.getTime()
                        && recordDate.getTime() <= cycleEndDate.getTime()) {
                    logger.debug(debugger,
                            "시간 비교로 찾은 적합한 주기:" + recordDate + "," + cycleStartDate + " ~ " + cycleEndDate);
                    return temp;
                }
            }
        }
        if (!hasConfig) {
            logger.debug(debugger, "적합한 주기를 찾지 못해 출퇴근 기록 정보로 자연월 주기 생성");
            // 관련 설정을 찾지 못했으므로 새 설정 생성
            // 통계 주기 설정 생성 및 저장
            attendanceStatisticalCycle = createNewCycleInfo(recordDate, q_topUnitName, q_unitName);
            putDistinctCycleInList(attendanceStatisticalCycle, unitCycleInfoEntity,
                    topUnitAttendanceStatisticalCycleMap);
            return attendanceStatisticalCycle;
        }
        return null;
    }

    /**
     * TODO 최상위 조직, 조직, 연월로 통계 주기 설정 조회
     * 없으면 새 주기 설정 생성
     * 
     * @param q_topUnitName
     * @param q_unitName
     * @param cycleYear
     * @param cycleMonth
     * @param topUnitAttendanceStatisticalCycleMap
     * @return
     * @throws Exception
     */
    public synchronized AttendanceStatisticalCycle getStatisticCycle(String q_topUnitName, String q_unitName,
            String cycleYear, String cycleMonth,
            Map<String, Map<String, List<AttendanceStatisticalCycle>>> topUnitAttendanceStatisticalCycleMap,
            Boolean debugger) throws Exception {
        AttendanceStatisticalCycle attendanceStatisticalCycle = null;
        UnitCycleInfoEntity unitCycleInfoEntity = null;
        Boolean hasConfig = false;
        String topUnitName = null, unitName = null;

        if (Integer.parseInt(cycleMonth) < 10) {
            cycleMonth = "0" + Integer.parseInt(cycleMonth);
        }

        // Map에서 최상위 조직과 조직에 해당하는 주기 설정 정보 조회
        if (topUnitAttendanceStatisticalCycleMap != null) {
            unitCycleInfoEntity = analyseStatisticCycleWithUnitName(q_topUnitName, q_unitName,
                    topUnitAttendanceStatisticalCycleMap, debugger);
        } else {
            logger.debug(debugger, "통계 주기 설정 없음, 최상위 조직 *, 조직 *, 시스템에 설정이 전혀 없음");
            unitCycleInfoEntity = new UnitCycleInfoEntity();
            unitCycleInfoEntity.setTopUnitName("*");
            unitCycleInfoEntity.setUnitName("*");
        }

        if (unitCycleInfoEntity.getUnitCycles() != null && unitCycleInfoEntity.getUnitCycles().size() > 0) {
            // 설정 정보가 있으므로 설정 데이터가 출퇴근 기록에 필요한 주기를 충족하는지 확인
            for (AttendanceStatisticalCycle temp : unitCycleInfoEntity.getUnitCycles()) {
                if (temp.getCycleYear().equals(cycleYear) && temp.getCycleMonth().equals(cycleMonth)) {
                    hasConfig = true;
                    return temp;
                }
            }
        } else {
            logger.debug(debugger, "최상위 조직 [" + topUnitName + "]과 조직 [" + unitName + "]의 주기 데이터를 찾지 못해 새 통계 주기 데이터를 생성해야 합니다......");
        }

        if (!hasConfig) {
            logger.debug(debugger, "적합한 주기를 찾지 못해 출퇴근 기록 정보로 자연월 주기 생성");
            // 관련 설정을 찾지 못했으므로 새 설정 생성
            Date day = dateOperation.getDateFromString(cycleYear + "-" + cycleMonth + "-01");
            // 통계 주기 설정 생성 및 저장
            attendanceStatisticalCycle = createNewCycleInfo(day, topUnitName, unitName);
            putDistinctCycleInList(attendanceStatisticalCycle, unitCycleInfoEntity,
                    topUnitAttendanceStatisticalCycleMap);
            return attendanceStatisticalCycle;
        }
        return null;
    }

    /**
     * 최상위 조직, 조직, 연월로 통계 주기 설정 조회, 없으면 새 주기 설정 생성
     * 
     * @param attendanceStatisticRequireLog
     * @param topUnitAttendanceStatisticalCycleMap
     * @param debugger
     * @return
     * @throws Exception
     */
    public AttendanceStatisticalCycle getStatisticCycleByEmployee(
            AttendanceStatisticRequireLog attendanceStatisticRequireLog,
            Map<String, Map<String, List<AttendanceStatisticalCycle>>> topUnitAttendanceStatisticalCycleMap,
            Boolean debugger) throws Exception {
        if (attendanceStatisticRequireLog == null) {
            return null;
        }
        return getStatisticCycleByEmployee(attendanceStatisticRequireLog.getStatisticKey(),
                attendanceStatisticRequireLog.getStatisticYear(), attendanceStatisticRequireLog.getStatisticMonth(),
                topUnitAttendanceStatisticalCycleMap, debugger);
    }

    public synchronized AttendanceStatisticalCycle getStatisticCycleByEmployee(String employeeName, String cycleYear,
            String cycleMonth,
            Map<String, Map<String, List<AttendanceStatisticalCycle>>> topUnitAttendanceStatisticalCycleMap,
            Boolean debugger) throws Exception {
        AttendanceStatisticalCycle attendanceStatisticalCycle = null;
        Boolean hasConfig = false;
        List<String> identities = null;
        UnitCycleInfoEntity unitCycleInfoEntity = null;
        String unitName = null;

        if (Integer.parseInt(cycleMonth) < 10) {
            cycleMonth = "0" + Integer.parseInt(cycleMonth);
        }

        // Map에서 최상위 조직과 조직에 해당하는 주기 설정 정보 조회
        if (topUnitAttendanceStatisticalCycleMap != null) {
            // 사용자 신분 조회 필요
            try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
                identities = userManagerService.listIdentitiesWithPerson(employeeName);
                if (identities == null || identities.size() == 0) {// 해당 직원에게 현재 할당된 신분 없음
                    throw new Exception("can not get identity of person:" + employeeName + ".");
                }
            } catch (Exception e) {
                logger.warn("시스템이 직원 [" + employeeName + "]의 시스템 내 신분을 조회하는 중 예외가 발생했습니다!");
                // modify by ray 20221101 이 예외로 인해 이후 프로그램이 실행되지 않음
                // throw e;
            }

            if (identities != null && identities.size() > 0) {
                for (String identity : identities) {
                    unitName = userManagerService.getUnitNameWithIdentity(identity);
                    unitCycleInfoEntity = analyseStatisticCycleWithUnitName(unitName,
                            topUnitAttendanceStatisticalCycleMap, debugger);

                    if (unitCycleInfoEntity.getUnitCycles() != null && unitCycleInfoEntity.getUnitCycles().size() > 0) {
                        // 설정 정보가 있으므로 설정 데이터가 출퇴근 기록에 필요한 주기를 충족하는지 확인
                        for (AttendanceStatisticalCycle temp : unitCycleInfoEntity.getUnitCycles()) {
                            if (temp.getCycleYear().equals(cycleYear) && temp.getCycleMonth().equals(cycleMonth)) {
                                hasConfig = true;
                                return temp;
                            }
                        }
                    }
                }
            }
        } else {
            unitCycleInfoEntity = new UnitCycleInfoEntity();
            unitCycleInfoEntity.setTopUnitName("*");
            unitCycleInfoEntity.setUnitName("*");
        }

        if (!hasConfig) {
            logger.debug(debugger, ">>>>>>>>>>적합한 주기를 찾지 못해 출퇴근 기록 정보로 자연월 주기 생성");
            // 관련 설정을 찾지 못했으므로 새 설정 생성
            Date day = dateOperation.getDateFromString(cycleYear + "-" + cycleMonth + "-01");

            // 통계 주기 설정 생성 및 저장
            attendanceStatisticalCycle = createNewCycleInfo(day, unitCycleInfoEntity.getTopUnitName(),
                    unitCycleInfoEntity.getUnitName());

            putDistinctCycleInList(attendanceStatisticalCycle, unitCycleInfoEntity,
                    topUnitAttendanceStatisticalCycleMap);

            return attendanceStatisticalCycle;
        }
        return null;
    }

    private UnitCycleInfoEntity analyseStatisticCycleWithUnitName(String unitName,
            Map<String, Map<String, List<AttendanceStatisticalCycle>>> topUnitAttendanceStatisticalCycleMap,
            Boolean debugger) throws Exception {
        String topUnitName = null;
        if (unitName == null) {
            throw new Exception("unitName is null!");
        }
        topUnitName = userManagerService.getTopUnitNameWithUnitName(unitName);
        if (topUnitName == null || topUnitName.isEmpty()) {
            topUnitName = unitName;
        }

        return analyseStatisticCycleWithUnitName(topUnitName, unitName, topUnitAttendanceStatisticalCycleMap, debugger);
    }

    private UnitCycleInfoEntity analyseStatisticCycleWithUnitName(String topUnitName, String unitName,
            Map<String, Map<String, List<AttendanceStatisticalCycle>>> topUnitAttendanceStatisticalCycleMap,
            Boolean debugger) throws Exception {
        Map<String, List<AttendanceStatisticalCycle>> unitAttendanceStatisticalCycleMap = null;
        List<AttendanceStatisticalCycle> unitCycles = null;

        if (unitName == null) {
            throw new Exception("unitName is null!");
        }
        if (topUnitName == null) {
            throw new Exception("topUnitName is null!");
        }

        unitAttendanceStatisticalCycleMap = topUnitAttendanceStatisticalCycleMap.get(topUnitName);

        if (unitAttendanceStatisticalCycleMap != null) {
            logger.debug(debugger, ">>>>>>>>>>최상위 조직 [" + topUnitName + "]의 통계 주기 설정");
            // 현재 최상위 조직의 설정 정보가 있으므로 조직으로 조직 설정 목록 존재 여부 재조회 [topUnit - unit]
            unitCycles = unitAttendanceStatisticalCycleMap.get(topUnitName);
            if (unitCycles == null) {
                logger.debug(debugger, ">>>>>>>>>>최상위 조직 [" + topUnitName + "]의 통계 주기 설정, 조직은 *로 설정");
                unitName = "*";
                unitCycles = unitAttendanceStatisticalCycleMap.get("*");
            }
        } else {
            logger.debug(debugger, ">>>>>>>>>>최상위 조직 [" + topUnitName + "]의 통계 주기 설정 없음, 최상위 조직은 *로 설정");
            // 최상위 조직이 *인 Map이 있는지 확인
            unitAttendanceStatisticalCycleMap = topUnitAttendanceStatisticalCycleMap.get("*");
            topUnitName = "*";
            if (unitAttendanceStatisticalCycleMap != null) {
                logger.debug(debugger, ">>>>>>>>>>최상위 조직 [*]의 통계 주기 설정을 찾아 조직이 [*]인 설정 목록을 재조회");
                // 현재 최상위 조직의 설정 정보가 있으므로 조직으로 조직 설정 목록 존재 여부 재조회 [topUnit - unit]
                unitCycles = unitAttendanceStatisticalCycleMap.get(unitName);
                if (unitCycles == null) {
                    logger.debug(debugger, ">>>>>>>>>>최상위 조직 [*] 조직 [" + unitName + "]의 통계 주기 설정, 조직은 *로 설정, 조직 [*]의 설정 조회");
                    unitName = "*";
                    unitCycles = unitAttendanceStatisticalCycleMap.get("*");
                }
            } else {
                logger.debug(debugger, ">>>>>>>>>>최상위 조직 [*]의 통계 주기 설정 없음, 최상위 조직은 *로 설정, 시스템에 설정이 전혀 없음");
                unitName = "*";
            }
        }

        return new UnitCycleInfoEntity(unitName, topUnitName, unitCycles);
    }

    /**
     * TODO 일자와 조직 정보로 새 통계 주기 정보 객체 생성
     * 
     * @param date
     * @param topUnitName
     * @param unitName
     * @return
     * @throws Exception
     */
    private AttendanceStatisticalCycle createNewCycleInfo(Date date, String topUnitName, String unitName)
            throws Exception {
        if (date == null) {
            throw new Exception("date is null!");
        }
        if (topUnitName == null) {
            throw new Exception("topUnitName is null!");
        }
        if (unitName == null) {
            throw new Exception("unitName is null!");
        }

        String cycleMonth = dateOperation.getMonth(date);
        AttendanceStatisticalCycle attendanceStatisticalCycle = null;

        if (Integer.parseInt(cycleMonth) < 10) {
            cycleMonth = "0" + Integer.parseInt(cycleMonth);
        }
        attendanceStatisticalCycle = new AttendanceStatisticalCycle();
        attendanceStatisticalCycle.setTopUnitName(topUnitName);
        attendanceStatisticalCycle.setUnitName(unitName);
        attendanceStatisticalCycle.setCycleMonth(cycleMonth);
        attendanceStatisticalCycle.setCycleYear(dateOperation.getYear(date));
        attendanceStatisticalCycle.setCycleStartDate(dateOperation.getFirstDateInMonth(date));
        attendanceStatisticalCycle.setCycleStartDateString(dateOperation.getFirstDateStringInMonth(date));
        attendanceStatisticalCycle.setCycleEndDate(dateOperation.getLastDateInMonth(date));
        attendanceStatisticalCycle.setCycleEndDateString(dateOperation.getLastDateStringInMonth(date));
        attendanceStatisticalCycle.setDescription("시스템 자동 생성");
        try {
            attendanceStatisticalCycle = saveCycleInfo(attendanceStatisticalCycle);
        } catch (Exception e) {
            logger.warn("시스템이 새 통계 주기 정보를 저장하는 중 예외가 발생했습니다!");
            throw e;
        }
        return attendanceStatisticalCycle;
    }

    /**
     * TODO 통계 주기 정보를 데이터베이스에 저장(synchronized)
     * 
     * @param attendanceStatisticalCycle
     * @return
     * @throws Exception
     */
    private synchronized AttendanceStatisticalCycle saveCycleInfo(AttendanceStatisticalCycle attendanceStatisticalCycle)
            throws Exception {
        if (attendanceStatisticalCycle == null) {
            throw new Exception("attendanceStatisticalCycle is null!");
        }
        AttendanceStatisticalCycle temp = null;
        Business business = null;
        List<String> ids = null;
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            business = new Business(emc);
            ids = business.getAttendanceStatisticalCycleFactory().listByParameter(
                    attendanceStatisticalCycle.getTopUnitName(),
                    attendanceStatisticalCycle.getUnitName(),
                    attendanceStatisticalCycle.getCycleYear(),
                    attendanceStatisticalCycle.getCycleMonth());
            if (ids != null && !ids.isEmpty()) {
                for (int i = 0; i < ids.size(); i++) {
                    temp = emc.find(ids.get(i), AttendanceStatisticalCycle.class);
                    if (i > 0) {
                        emc.beginTransaction(AttendanceStatisticalCycle.class);
                        emc.remove(temp, CheckRemoveType.all);
                        emc.commit();
                    }
                }
            } else {
                emc.beginTransaction(AttendanceStatisticalCycle.class);
                emc.persist(attendanceStatisticalCycle, CheckPersistType.all);
                emc.commit();
            }
        } catch (Exception e) {
            logger.warn("시스템이 새 통계 주기 정보를 저장하는 중 예외가 발생했습니다!");
            throw e;
        }
        return attendanceStatisticalCycle;
    }

    /**
     * TODO 단일 객체를 List에 넣고 중복 제거(synchronized)
     * 
     * @param cycle
     * @param topUnitCycles
     * @return
     */
    public synchronized List<AttendanceStatisticalCycle> putDistinctCycleInList(AttendanceStatisticalCycle cycle,
            UnitCycleInfoEntity unitCycleInfoEntity,
            Map<String, Map<String, List<AttendanceStatisticalCycle>>> topUnitAttendanceStatisticalCycleMap) {
        List<AttendanceStatisticalCycle> unitCycles = null;
        Map<String, List<AttendanceStatisticalCycle>> unitAttendanceStatisticalCycleMap = null;
        // 객체를 Map에 넣어 반환
        if (topUnitAttendanceStatisticalCycleMap == null) {
            topUnitAttendanceStatisticalCycleMap = new ConcurrentHashMap<String, Map<String, List<AttendanceStatisticalCycle>>>();
        }

        unitAttendanceStatisticalCycleMap = topUnitAttendanceStatisticalCycleMap
                .get(unitCycleInfoEntity.getTopUnitName());
        if (unitAttendanceStatisticalCycleMap == null) {
            unitAttendanceStatisticalCycleMap = new ConcurrentHashMap<String, List<AttendanceStatisticalCycle>>();
            topUnitAttendanceStatisticalCycleMap.put(unitCycleInfoEntity.getTopUnitName(),
                    unitAttendanceStatisticalCycleMap);
        }

        unitCycles = unitAttendanceStatisticalCycleMap.get(unitCycleInfoEntity.getUnitName());

        if (unitCycles == null) {
            unitCycles = new ArrayList<AttendanceStatisticalCycle>();
            unitAttendanceStatisticalCycleMap.put(unitCycleInfoEntity.getUnitName(), unitCycles);
        }

        for (AttendanceStatisticalCycle attendanceStatisticalCycle : unitCycles) {
            if (attendanceStatisticalCycle.getTopUnitName() != null
                    && attendanceStatisticalCycle.getUnitName() != null
                    && attendanceStatisticalCycle.getCycleYear() != null
                    && attendanceStatisticalCycle.getCycleMonth() != null) {
                if (attendanceStatisticalCycle.getTopUnitName().equals(cycle.getTopUnitName())
                        && attendanceStatisticalCycle.getUnitName().equals(cycle.getUnitName())
                        && attendanceStatisticalCycle.getCycleYear().equals(cycle.getCycleYear())
                        && attendanceStatisticalCycle.getCycleMonth().equals(cycle.getCycleMonth())

                ) {
                    return unitCycles;
                }
            }
        }
        unitCycles.add(cycle);
        return unitCycles;
    }
}
