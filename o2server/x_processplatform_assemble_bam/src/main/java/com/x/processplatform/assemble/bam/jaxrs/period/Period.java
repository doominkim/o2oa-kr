package com.x.processplatform.assemble.bam.jaxrs.period;

import com.x.processplatform.assemble.bam.stub.ApplicationStubs;
import com.x.processplatform.assemble.bam.stub.UnitStubs;

public class Period {

	/** period startTask에서 필요한 ApplicationStub과 CompanyStub 선택 */
	private ApplicationStubs startTaskApplicationStubs = new ApplicationStubs();
	private UnitStubs startTaskUnitStubs = new UnitStubs();

	/** period completedTask에서 필요한 ApplicationStub과 CompanyStub 선택 */
	private ApplicationStubs completedTaskApplicationStubs = new ApplicationStubs();
	private UnitStubs completedTaskUnitStubs = new UnitStubs();

	/** period expiredTask에서 필요한 ApplicationStub과 CompanyStub 선택 */
	private ApplicationStubs expiredTaskApplicationStubs = new ApplicationStubs();
	private UnitStubs expiredTaskUnitStubs = new UnitStubs();

	/** period startWork에서 필요한 ApplicationStub과 CompanyStub 선택 */
	private ApplicationStubs startWorkApplicationStubs = new ApplicationStubs();
	private UnitStubs startWorkUnitStubs = new UnitStubs();

	/** period completedWork에서 필요한 ApplicationStub과 CompanyStub 선택 */
	private ApplicationStubs completedWorkApplicationStubs = new ApplicationStubs();
	private UnitStubs completedWorkUnitStubs = new UnitStubs();

	/** period expiredWork에서 필요한 ApplicationStub과 CompanyStub 선택 */
	private ApplicationStubs expiredWorkApplicationStubs = new ApplicationStubs();
	private UnitStubs expiredWorkUnitStubs = new UnitStubs();

	public ApplicationStubs getStartTaskApplicationStubs() {
		return startTaskApplicationStubs;
	}

	public void setStartTaskApplicationStubs(ApplicationStubs startTaskApplicationStubs) {
		this.startTaskApplicationStubs = startTaskApplicationStubs;
	}

	public UnitStubs getStartTaskUnitStubs() {
		return startTaskUnitStubs;
	}

	public void setStartTaskUnitStubs(UnitStubs startTaskUnitStubs) {
		this.startTaskUnitStubs = startTaskUnitStubs;
	}

	public ApplicationStubs getCompletedTaskApplicationStubs() {
		return completedTaskApplicationStubs;
	}

	public void setCompletedTaskApplicationStubs(ApplicationStubs completedTaskApplicationStubs) {
		this.completedTaskApplicationStubs = completedTaskApplicationStubs;
	}

	public UnitStubs getCompletedTaskUnitStubs() {
		return completedTaskUnitStubs;
	}

	public void setCompletedTaskUnitStubs(UnitStubs completedTaskUnitStubs) {
		this.completedTaskUnitStubs = completedTaskUnitStubs;
	}

	public ApplicationStubs getExpiredTaskApplicationStubs() {
		return expiredTaskApplicationStubs;
	}

	public void setExpiredTaskApplicationStubs(ApplicationStubs expiredTaskApplicationStubs) {
		this.expiredTaskApplicationStubs = expiredTaskApplicationStubs;
	}

	public UnitStubs getExpiredTaskUnitStubs() {
		return expiredTaskUnitStubs;
	}

	public void setExpiredTaskUnitStubs(UnitStubs expiredTaskUnitStubs) {
		this.expiredTaskUnitStubs = expiredTaskUnitStubs;
	}

	public ApplicationStubs getStartWorkApplicationStubs() {
		return startWorkApplicationStubs;
	}

	public void setStartWorkApplicationStubs(ApplicationStubs startWorkApplicationStubs) {
		this.startWorkApplicationStubs = startWorkApplicationStubs;
	}

	public UnitStubs getStartWorkUnitStubs() {
		return startWorkUnitStubs;
	}

	public void setStartWorkUnitStubs(UnitStubs startWorkUnitStubs) {
		this.startWorkUnitStubs = startWorkUnitStubs;
	}

	public ApplicationStubs getCompletedWorkApplicationStubs() {
		return completedWorkApplicationStubs;
	}

	public void setCompletedWorkApplicationStubs(ApplicationStubs completedWorkApplicationStubs) {
		this.completedWorkApplicationStubs = completedWorkApplicationStubs;
	}

	public UnitStubs getCompletedWorkUnitStubs() {
		return completedWorkUnitStubs;
	}

	public void setCompletedWorkUnitStubs(UnitStubs completedWorkUnitStubs) {
		this.completedWorkUnitStubs = completedWorkUnitStubs;
	}

	public ApplicationStubs getExpiredWorkApplicationStubs() {
		return expiredWorkApplicationStubs;
	}

	public void setExpiredWorkApplicationStubs(ApplicationStubs expiredWorkApplicationStubs) {
		this.expiredWorkApplicationStubs = expiredWorkApplicationStubs;
	}

	public UnitStubs getExpiredWorkUnitStubs() {
		return expiredWorkUnitStubs;
	}

	public void setExpiredWorkUnitStubs(UnitStubs expiredWorkUnitStubs) {
		this.expiredWorkUnitStubs = expiredWorkUnitStubs;
	}

}
