package org.openmrs.module.chicaops.service;

import java.util.ArrayList;

import org.openmrs.module.chicaops.dashboard.CareCenterResult;
import org.openmrs.module.chicaops.dashboard.RuleCheckResult;
import org.openmrs.module.chicaops.dashboard.ServerCheckResult;
import org.springframework.transaction.annotation.Transactional;

/**
 * Interface to define the service layer for the Dashboard.
 * 
 * @author Steve McKee
 */
@Transactional
public interface ChicaopsService {

	/**
	 * Checks states specified in the Dashboard configuration file against 
	 * their specified conditions to determine if the condition is occurring.
	 * 
	 * @return ArrayList of CareCenterResults objects containing the results 
	 * of the checks based on care center.
	 */
	public ArrayList<CareCenterResult> monitorStates();
	
	/**
	 * Performs server-specific checks.
	 * 
	 * @return ServerCheckResult object containing the results of the server 
	 * checks.
	 */
	public ServerCheckResult performServerChecks();
	
	/**
	 * Performs rule-specific checks.
	 * 
	 * @return RuleCheckResult object containing the results of the rule checks.
	 */
	public RuleCheckResult performRuleChecks();
}
