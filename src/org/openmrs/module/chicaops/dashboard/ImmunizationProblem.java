package org.openmrs.module.chicaops.dashboard;

import org.openmrs.module.chicaops.xmlBeans.dashboard.ImmunizationChecks;


public class ImmunizationProblem {
	
	private ImmunizationChecks immunChecks;
	private Integer count;
	/**
	 * @param immunCheck
	 */
	public ImmunizationProblem(ImmunizationChecks immunChecks) {
		this.immunChecks = immunChecks;
	}
	
	public ImmunizationProblem() {
	}
 

	public ImmunizationChecks getImmunCheck() {
		return immunChecks;
	}

	public void setImmunCheck(ImmunizationChecks immunCheck) {
		this.immunChecks = immunCheck;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	
}