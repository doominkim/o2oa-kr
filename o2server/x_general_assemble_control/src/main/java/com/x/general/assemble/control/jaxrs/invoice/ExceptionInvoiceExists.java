package com.x.general.assemble.control.jaxrs.invoice;

import com.x.base.core.project.exception.PromptException;

class ExceptionInvoiceExists extends PromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionInvoiceExists(String number) {
		super("이 계산서 번호가 이미 있습니다: {}.", number);
	}
}
