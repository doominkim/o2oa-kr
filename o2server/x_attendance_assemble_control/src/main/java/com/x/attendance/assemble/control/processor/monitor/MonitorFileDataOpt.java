package com.x.attendance.assemble.control.processor.monitor;

import java.io.File;
import java.util.Iterator;
import java.util.Set;

import com.google.gson.Gson;
import com.x.attendance.assemble.control.processor.ImportOptDefine;
import com.x.attendance.assemble.control.processor.sender.SenderForSaveData;
import com.x.attendance.assemble.control.service.AttendanceImportFileInfoServiceAdv;
import com.x.attendance.entity.AttendanceImportFileInfo;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public class MonitorFileDataOpt {

	private static  Logger logger = LoggerFactory.getLogger(MonitorFileDataOpt.class);
	private AttendanceImportFileInfoServiceAdv importFileInfoService = new AttendanceImportFileInfoServiceAdv();
	private Gson gson = XGsonBuilder.instance();
	private MonitorFileDataOptThread optThread = null;

	// 스레드 종료 여부 플래그
	volatile static boolean flag = true;

	private static MonitorFileDataOpt single = null;

	public static MonitorFileDataOpt getInstance() {
		if (single == null) {
			single = new MonitorFileDataOpt();
		}
		return single;
	}

	public void start() {
		if (optThread == null) {
			optThread = new MonitorFileDataOptThread();
			optThread.start();
		}
	}

	public static void stop() {
		flag = false;
	}

	class MonitorFileDataOptThread extends Thread {
		
		public void run() {

			while (flag) {
				// 시스템 작업 상태에서 각 파일의 작업 상황 검사
				StatusSystemImportOpt systemImportOptStatus = null;
				Set<String> keySet = null;
				Iterator<String> iterator = null;
				StatusImportFileDetail cacheImportFileStatus = null;
				String key = null;

				systemImportOptStatus = StatusSystemImportOpt.getInstance();

				if (systemImportOptStatus != null) {
					keySet = systemImportOptStatus.getCheckMapKeySet();
				}

				if (keySet != null) {
					iterator = keySet.iterator();
				}

				while (iterator.hasNext()) {

					key = iterator.next();

					cacheImportFileStatus = systemImportOptStatus.getCacheImportFileStatus(key);

					cacheImportFileStatus.increaseMonitor_checkCount(1);

					cacheImportFileStatus.setFileId(key);

					if (cacheImportFileStatus.getMonitor_checkCount() > 10
							&& "NONE".equals(cacheImportFileStatus.getCurrentProcessName())) {
						// 10번 검사한 뒤에도 작업이 시작되지 않았다면 삭제해야 할 무효 캐시 객체이며, 대개 프런트엔드 조회 시 fileId를 잘못 입력해 생긴 것임.
						StatusSystemImportOpt.getInstance().cleanCacheImportFileStatus(key);
						StatusSystemImportOpt.getInstance().removeCacheImportFileStatus(key);
					}

					if (cacheImportFileStatus != null
							&& !"NONE".equals(cacheImportFileStatus.getCurrentProcessName())) {
						if (flag) {
							if (!ImportOptDefine.COMPLETED.equalsIgnoreCase(cacheImportFileStatus.getCurrentProcessName())) {
								process(cacheImportFileStatus, systemImportOptStatus.getDebugger() );
							}
						}
					}
				}

				// 모든 처리 과정이 완료되었는지 다시 확인
				if (!systemImportOptStatus.isChildrenProcessing()) {
					long total = 0L;
					long count = 0L;

					// 데이터 분석 중인지 확인
					total = systemImportOptStatus.getProcess_analysis_total();
					count = systemImportOptStatus.getProcess_analysis_count()
							+ systemImportOptStatus.getProcess_analysis_error();
					if (total == count) {
						systemImportOptStatus.setProcessing_analysis(false);
					}

					// 데이터 보충 중인지 확인
					total = systemImportOptStatus.getProcess_supplement_total();
					count = systemImportOptStatus.getProcess_supplement_count()
							+ systemImportOptStatus.getProcess_supplement_error();
					if (total == count) {
						systemImportOptStatus.setProcessing_supplement(false);
					}

					if (!systemImportOptStatus.getProcessing_analysis()
							&& !systemImportOptStatus.getProcessing_statistic()
							&& !systemImportOptStatus.getProcessing_supplement()) {
						systemImportOptStatus.setProcessing(false);
					}
				}

				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		/**
		 * 각 파일 처리 상태를 검사하고 데이터베이스에 저장
		 * 
		 * @param cacheImportFileStatus
		 */
		private void process(StatusImportFileDetail cacheImportFileStatus, Boolean debugger ) {
			if (!flag) {
				return;
			}
			String currentProcessName = cacheImportFileStatus.getCurrentProcessName();
			String file_id = cacheImportFileStatus.getFileId();
			AttendanceImportFileInfo fileInfo = null;
			// 지정 파일 정보 조회
			try {
				fileInfo = importFileInfoService.get(file_id);
			} catch (Exception e) {
				logger.warn("system get import file info from db with id got an exception.id:" + file_id);
				e.printStackTrace();
			}

			if (fileInfo != null) {
				if (ImportOptDefine.VALIDATE.equals(currentProcessName)) {
					Long process_count = 0L, process_total = 0L;
					if (cacheImportFileStatus.getProcessing_validate()) {
						process_count = cacheImportFileStatus.getProcessing_validate_count();
						process_total = cacheImportFileStatus.getProcessing_validate_total();
						fileInfo.setRecordTotle(process_count);
						fileInfo.setProcessCount(process_total);
						fileInfo.setRowCount(process_total);
						if (process_count.intValue() == process_total.intValue()) {
							if (cacheImportFileStatus != null
									&& "success".equalsIgnoreCase(cacheImportFileStatus.getCheckStatus())
									&& cacheImportFileStatus.getDetailList() != null
									&& cacheImportFileStatus.getDetailList().size() > 0) {
								logger.info("file[" + file_id + "], send before, record total:"
										+ cacheImportFileStatus.getDetailList().size());
								cacheImportFileStatus.setCurrentProcessName(ImportOptDefine.SAVEDATA);
								fileInfo.setCurrentProcessName(ImportOptDefine.SAVEDATA);
								cacheImportFileStatus.setProcessing(true);
								cacheImportFileStatus.setProcessing_validate(false);
								cacheImportFileStatus.setProcessing_save(true);
								fileInfo.setProcessing(true);
								fileInfo.setValidateOk(true);
								// 이 시점에는 임시 파일을 삭제할 수 있음
								if (cacheImportFileStatus.getFilePath() != null
										&& !cacheImportFileStatus.getFilePath().isEmpty()) {
									File file = new File(cacheImportFileStatus.getFilePath());
									if (file.exists()) {
										file.delete();
									}
								}
								// 데이터 처리가 완료되었으면 데이터 저장 시작
								logger.info("file[" + file_id
										+ "], excel validate complete, system will try to save record to database, record total:"
										+ process_total);
								new SenderForSaveData().execute(cacheImportFileStatus, debugger);
							} else {
								cacheImportFileStatus.setCurrentProcessName(ImportOptDefine.COMPLETED);
								fileInfo.setCurrentProcessName(ImportOptDefine.COMPLETED);
								cacheImportFileStatus.setProcessing(false);
								cacheImportFileStatus.setProcessing_validate(false);
								cacheImportFileStatus.setProcessing_save(false);
								fileInfo.setProcessing(false);
								fileInfo.setValidateOk(false);
								logger.warn("file[" + file_id
										+ "], excel validate complete, record can not to save, validate is not success!");
							}
						} else {
							cacheImportFileStatus.setCurrentProcessName(ImportOptDefine.VALIDATE);
							fileInfo.setCurrentProcessName(ImportOptDefine.VALIDATE);
							cacheImportFileStatus.setProcessing(true);
							cacheImportFileStatus.setProcessing_validate(true);
							cacheImportFileStatus.setProcessing_save(false);
							fileInfo.setProcessing(true);
							logger.info("file[" + file_id + "], validating:" + process_count + "/" + process_total);

							int error_count = Integer.parseInt(cacheImportFileStatus.getErrorCount() + "");
							if (error_count + process_count == process_total) {
								// 데이터 자동 처리가 완료되었음을 표시, 이 객체를 더 이상 검사하지 않음
								cacheImportFileStatus.setCurrentProcessName(ImportOptDefine.COMPLETED);
								fileInfo.setCurrentProcessName(ImportOptDefine.COMPLETED);
								cacheImportFileStatus.setProcessing(false);
								cacheImportFileStatus.setProcessing_validate(false);
								cacheImportFileStatus.setProcessing_save(false);
								fileInfo.setProcessing(false);
							}
						}
					}
				} else if (ImportOptDefine.SAVEDATA.equals(currentProcessName)) {
					Long process_count = 0L, process_total = 0L;
					if (cacheImportFileStatus.getProcessing_save()) {
						process_count = cacheImportFileStatus.getProcess_save_count();
						process_total = cacheImportFileStatus.getProcess_save_total();
						fileInfo.setRecordTotle(process_count);
						fileInfo.setProcessCount(process_total);
						if (process_count.intValue() == process_total.intValue()) {
							// 데이터 자동 처리가 완료되었음을 표시, 이 객체를 더 이상 검사하지 않음
							cacheImportFileStatus.setCurrentProcessName(ImportOptDefine.COMPLETED);
							fileInfo.setCurrentProcessName(ImportOptDefine.COMPLETED);
							cacheImportFileStatus.setProcessing(false);
							cacheImportFileStatus.setProcessing_validate(false);
							cacheImportFileStatus.setProcessing_save(false);
							fileInfo.setProcessing(false);

							logger.info("file[" + file_id + "], save data complete, save record :" + process_count);

						} else {
							cacheImportFileStatus.setCurrentProcessName(ImportOptDefine.SAVEDATA);
							fileInfo.setCurrentProcessName(ImportOptDefine.SAVEDATA);
							cacheImportFileStatus.setProcessing(true);
							cacheImportFileStatus.setProcessing_validate(false);
							cacheImportFileStatus.setProcessing_save(true);
							fileInfo.setProcessing(true);
							logger.info("file[" + file_id + "], saving:" + process_count + "/" + process_total);

							int error_count = Integer.parseInt(cacheImportFileStatus.getErrorCount() + "");
							if (error_count + process_count == process_total) {
								// 데이터 자동 처리가 완료되었음을 표시, 이 객체를 더 이상 검사하지 않음
								cacheImportFileStatus.setCurrentProcessName(ImportOptDefine.COMPLETED);
								fileInfo.setCurrentProcessName(ImportOptDefine.COMPLETED);
								cacheImportFileStatus.setProcessing(false);
								cacheImportFileStatus.setProcessing_validate(false);
								cacheImportFileStatus.setProcessing_save(false);
								fileInfo.setProcessing(false);
							}
						}
					}
				}

				// fileInfo를 데이터베이스에 저장.
				try {
					fileInfo.setStartDate(cacheImportFileStatus.getStartTime());
					fileInfo.setEndDate(cacheImportFileStatus.getEndTime());
					fileInfo.setTempFilePath(cacheImportFileStatus.getFilePath());
					if (cacheImportFileStatus.getDetailList() != null
							&& !cacheImportFileStatus.getDetailList().isEmpty()) {
						fileInfo.setDataContent(gson.toJson(cacheImportFileStatus));
					}
					if (cacheImportFileStatus.getErrorList() != null
							&& !cacheImportFileStatus.getErrorList().isEmpty()) {
						fileInfo.setErrorContent(gson.toJson(cacheImportFileStatus.getErrorList()));
					}
					importFileInfoService.save(fileInfo);
				} catch (Exception e) {
					logger.warn("system get import file info from db with id got an exception.");
					e.printStackTrace();
				}
			} else {
				logger.debug( true, ">>>>>>>>>>가져오기 파일이 없습니다: " + file_id);
			}

		}
	}

}
