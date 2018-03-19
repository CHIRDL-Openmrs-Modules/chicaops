package org.openmrs.module.chicaops.service;

import java.util.ArrayList;
import java.util.List;

import org.openmrs.annotation.Authorized;
import org.openmrs.module.chicaops.dashboard.CareCenterResult;
import org.openmrs.module.chicaops.dashboard.ImmunizationCheckResult;
import org.openmrs.module.chicaops.dashboard.ManualCheckinNumResult;
import org.openmrs.module.chicaops.dashboard.RuleCheckResult;
import org.openmrs.module.chicaops.dashboard.ServerCheckResult;

/**
 * Interface to define the service layer for the Dashboard.
 * 
 * @author Steve McKee
 */
public interface ChicaopsService {

	/**
	 * Checks for issues directly related to care centers.
	 * 
	 * @return ArrayList of CareCenterResults objects containing the results 
	 * of the checks based on care center.
	 */
	@Authorized()
	public ArrayList<CareCenterResult> checkCareCenters();
	
	/**
	 * Performs server-specific checks.
	 * 
	 * @return ServerCheckResult object containing the results of the server 
	 * checks.
	 */
	@Authorized()
	public ServerCheckResult performServerChecks();
	
	/**
	 * Performs rule-specific checks.
	 * 
	 * @return RuleCheckResult object containing the results of the rule checks.
	 */
	@Authorized()
	public RuleCheckResult performRuleChecks();
	
	/**
	 * Perform immunization registry (CHIRP) access checks.
	 * @return ImmunizationRegistryCheckResult object containing the results of the CHIRP registry check.
	 */
	@Authorized()
	public ImmunizationCheckResult performImmunizationChecks();
	
	/**
	 * Perform monitoring clinic manual check-in times frequency. If it happens too frequently, the program will let chica team know. 
	 * @return ManualCheckinNumResult object containing the results of manual check-in monitoring result
	 */
	@Authorized()
	public List<ManualCheckinNumResult> performManualCheckinChecks();
}
