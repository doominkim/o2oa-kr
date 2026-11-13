package com.x.attendance.assemble.control.jaxrs.attendanceimportfileinfo;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;
import com.x.attendance.assemble.control.processor.monitor.StatusImportFileDetail;
import com.x.attendance.assemble.control.processor.monitor.StatusSystemImportOpt;
import com.x.attendance.entity.AttendanceImportFileInfo;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public class ActionListAll extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger(ActionListAll.class);
	private Gson gson = XGsonBuilder.instance();

	protected ActionResult<List<Wo>> execute(HttpServletRequest request, EffectivePerson effectivePerson)
			throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<>();
		List<Wo> wraps = new ArrayList<>();
		List<AttendanceImportFileInfo> attendanceSettingList = null;
		StatusImportFileDetail statusImportFileDetail = null;
		Boolean check = true;

		if (check) {
			try {
				attendanceSettingList = attendanceImportFileInfoServiceAdv.listAll();
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionAttendanceImportFileProcess(e, "전체 직원 근태 데이터 가져오기 파일 정보를 조회하는 중 오류가 발생했습니다.");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		if (check && attendanceSettingList != null) {

			for (AttendanceImportFileInfo fileInfo : attendanceSettingList) {
				if (!"COMPLETED".equals(fileInfo.getCurrentProcessName())) {
					// 완료되지 않았으면 시스템 파일 가져오기 처리 상태 객체에 해당 파일의 상세 객체 생성
					try {
						if ( StringUtils.isNotEmpty( fileInfo.getDataContent() )) {
							// 객체로 복원
							statusImportFileDetail = gson.fromJson(fileInfo.getDataContent(),
									StatusImportFileDetail.class);
							StatusSystemImportOpt.getInstance().addCacheImportFileStatus(statusImportFileDetail);
						}
					} catch (Exception e) {
						check = false;
						Exception exception = new ExceptionAttendanceEmployeeProcess(e, "파일을 메모리 처리 상태 목록으로 복원하는 중 오류가 발생했습니다.");
						result.error(exception);
						logger.error(e, effectivePerson, request, null);
					}
				}
			}
			try {
				wraps = Wo.copier.copy(attendanceSettingList);
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionAttendanceEmployeeProcess(e,
						"조회된 가져오기 파일 객체를 출력용 객체로 변환하는 중 오류가 발생했습니다.");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		result.setData(wraps);
		return result;
	}

	public static class Wo extends AttendanceImportFileInfo {

		private static final long serialVersionUID = -5076990764713538973L;

		public static List<String> Excludes = new ArrayList<String>();

		static {
			Excludes.add("errorContent");
			Excludes.add("dataContent");
			Excludes.add("fileBody");
		}

		public static WrapCopier<AttendanceImportFileInfo, Wo> copier = WrapCopierFactory
				.wo(AttendanceImportFileInfo.class, Wo.class, null, Wo.Excludes);
	}
}