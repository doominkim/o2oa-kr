package com.x.base.core.project.logger;

public class LoggerFactory {

	private LoggerFactory() {
	}

	public static Logger getLogger(Class<?> clz) {
		return new Logger(clz.getName());
	}

	@Deprecated
	/**
	 * 
	 * 이 메서드는 폐기되어 아무 용도가 없음, 일부 custom 모듈 작성 시 이 메서드를 잘못 사용하여 custom 모듈 호환용 빈 메서드로 둠.
	 */
	public static void setLevel(Object o) {
		// nothing
	}

}