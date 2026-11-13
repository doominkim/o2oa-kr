package com.x.attendance.assemble.control.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.x.attendance.entity.AttendanceDetail;
import com.x.attendance.entity.AttendanceStatisticalCycle;
import com.x.attendance.entity.AttendanceWorkDayConfig;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;

/**
 * 근태 출퇴근 기록 분석 서비스 클래스
 * @author LIYI
 *
 */
public class AttendanceDetailAnalyseServiceAdv {
	private AttendanceDetailAnalyseService detailAnalyseService = new AttendanceDetailAnalyseService();

	/**
	 * 직원 이름\시작 날짜\종료 날짜로 날짜 범위 안의 모든 출퇴근 기록 정보 ID 목록 조회<br/>
	 * 시간은 시작 날짜 0시부터 종료 날짜 23시 59분까지
	 * @param empName
	 * @param startDate
	 * @param endDate
	 * @return
	 * @throws Exception
	 */
	public List<String> listAnalyseAttendanceDetailIds( String empName, Date startDate, Date endDate, Boolean debugger )throws Exception{
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return detailAnalyseService.listAnalyseAttendanceDetailIds( emc, empName, startDate, endDate );
		} catch ( Exception e ) {
			throw e;
		}
	}

	/**
	 * 체크 정보 데이터 묶음 분석
	 * @param detailIds
	 * @param debugger
	 * @return
	 * @throws Exception
	 */
	public boolean analyseAttendanceDetailWithIds( List<String> detailIds, Boolean debugger ) throws Exception{
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return detailAnalyseService.analyseAttendanceDetailWithIds( emc, detailIds, debugger );
		} catch ( Exception e ) {
			throw e;
		}
	}

	/**
	 * 체크 정보 데이터 묶음 분석
	 * @param detailList
	 * @param debugger
	 * @return
	 * @throws Exception
	 */
	public boolean analyseAttendanceDetails( List<AttendanceDetail> detailList, Boolean debugger ) throws Exception{
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return detailAnalyseService.analyseAttendanceDetails( emc, detailList, debugger );
		} catch ( Exception e ) {
			throw e;
		}
	}

	/**
	 * 체크 정보 데이터 묶음 분석
	 * @param detailList
	 * @param statisticalCycleMap
	 * @param debugger
	 * @return
	 * @throws Exception
	 */
	public boolean analyseAttendanceDetails( List<AttendanceDetail> detailList, Map<String, Map<String, List<AttendanceStatisticalCycle>>> statisticalCycleMap, Boolean debugger ) throws Exception{
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return detailAnalyseService.analyseAttendanceDetails( emc, detailList, statisticalCycleMap, debugger );
		} catch ( Exception e ) {
			throw e;
		}
	}

	/**
	 * 직원 이름, 시작 날짜, 종료 날짜로 모든 출퇴근 기록을 재분석. 보통 사용자가 휴가 기록을 보충했을 때 사용하며, 휴가 날짜 범위 안의 모든 출퇴근 기록을 재분석하고 해당 월 통계를 실행해야 함
	 * @param empName
	 * @param startDate
	 * @param endDate
	 * @param debugger
	 * @return
	 * @throws Exception
	 */
	public Boolean analyseAttendanceDetails( String empName, Date startDate, Date endDate, Boolean debugger ) throws Exception{
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return detailAnalyseService.analyseAttendanceDetails( emc, empName, startDate, endDate, debugger );
		} catch ( Exception e ) {
			throw e;
		}
	}

	/**
	 * 체크 데이터 한 건 분석
	 * @param detailId
	 * @param debugger
	 * @return
	 * @throws Exception
	 */
	public boolean analyseAttendanceDetail( String detailId, Boolean debugger ) throws Exception{
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return detailAnalyseService.analyseAttendanceDetail( emc, detailId, debugger );
		} catch ( Exception e ) {
			throw e;
		}
	}

	/**
	 * 체크 데이터 한 건 분석
	 * @param detail
	 * @param debugger
	 * @return
	 * @throws Exception
	 */
	public boolean analyseAttendanceDetail( AttendanceDetail detail, Boolean debugger ) throws Exception{
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return detailAnalyseService.analyseAttendanceDetail( emc, detail, debugger );
		} catch ( Exception e ) {
			throw e;
		}
	}

	/**
	 * 체크 데이터 한 건 상세 분석
	 * @param detail
	 * @param workDayConfigList
	 * @param statisticalCycleMap
	 * @param debugger
	 * @return
	 * @throws Exception
	 */
	public Boolean analyseAttendanceDetail( AttendanceDetail detail, List<AttendanceWorkDayConfig> workDayConfigList, Map<String, Map<String, List<AttendanceStatisticalCycle>>> statisticalCycleMap, Boolean debugger ) throws Exception{
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return detailAnalyseService.analyseAttendanceDetail( emc, detail, workDayConfigList, statisticalCycleMap, debugger );
		} catch ( Exception e ) {
			throw e;
		}
	}

}
