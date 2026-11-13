package com.x.program.center.jaxrs.market;

import com.x.base.core.project.exception.PromptException;

class ExceptionErrorInstallPackage extends PromptException {

	private static final long serialVersionUID = -9050250987376957535L;

	ExceptionErrorInstallPackage() {
		super("잘못된 설치 패키지입니다.");
	}
}
