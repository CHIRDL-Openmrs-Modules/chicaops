package org.openmrs.module.chicaops.db;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.openmrs.module.chicaops.dashboard.MonitorResult;
import org.openmrs.module.chicaops.xmlBeans.dashboard.ForcedOutPWSCheck;
import org.openmrs.module.chicaops.xmlBeans.dashboard.HL7ExportChecks;
import org.openmrs.module.chicaops.xmlBeans.dashboard.ImmunizationChecks;
import org.openmrs.module.chicaops.xmlBeans.dashboard.StateToMonitor;
import org.openmrs.module.chicaops.xmlBeans.dashboard.UnFiredRuleCheck;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.PatientState;
import org.openmrs.module.dss.hibernateBeans.Rule;

/**
 * Interface to define the data layer methods for the Operations Dashboard.
 * 
 * @author Steve McKee
 */
public interface ChicaopsDAO {

	/**
	 * Checks the supplied state and returns any errors/warnings resulting 
	 * from the check.
	 * 
	 * @param state Contains the information needed to perform checks on 
	 * a state.
	 * 
	 * @return ArrayList of MonitorResult objects containing the result of the 
	 * checks.  This will not return null.
	 */
	public ArrayList<MonitorResult> checkState(StateToMonitor state) throws Exception;
	
	/**
	 * Retrieves all the PatientState objects where a patient has had PSF_wait_to_scan 
	 * state with a null end time and a PWS_create state exists for the same session.
	 * 
	 * @param forcedOutPWSCheck Contains the parameter for checking for forced out PWSs.
	 */
	public List<PatientState> getForcedOutPWSs(ForcedOutPWSCheck forcedOutPWSCheck);
	
	/**
	 * Finds status in the HL7 Export table occurring based on the specified parameter in 
	 * the provided HL7ExportAlerts object. 
	 * 
	 * @param alerts HL7ExportAlerts object containing the parameters used to find issues 
	 * in the HL7 Export table.
	 * @return List of Object arrays with a status name at the first index and the 
	 * location id at the second index.
	 */
	public List<Object[]> getHL7ExportAlerts(HL7ExportChecks alerts);
	
	/**
	 * Retrieves all the rules that have not been fired.
	 * 
	 * @return List of Rule objects.
	 */
	public List<Rule> getNeverFiredRules();
	
	/**
	 * Retrieves all the rules that have not been fired within a specified period of time.  
	 * This does not include rules that have never fired at all.  This only includes rules 
	 * that have fired at least one time.
	 * 
	 * @param check UnFiredRuleCheck containing the information needing to be checked.
	 * @return List of Rule objects.
	 */
	public List<Rule> getUnFiredRules(UnFiredRuleCheck check);
	
	/**
	 * Retrieves all the PatientState objects for a particular form that have been recorded 
	 * since the provided Date.
	 * 
	 * @param formId The form ID used for limiting the query.
	 * @param locationId The location ID used for limiting the query.
	 * @param sinceDate Only include entries since this date.
	 * @return List of PatientState objects for a particular form that have been recored 
	 * since the provided Date.
	 */
	public List<PatientState> getPatientsStates(Integer formId, Integer locationId, Date sinceDate);
	
	/**
	 * Retrieves all errors from chirdlutilbackports_error table based on immunization error
	 * message.
	 * @param alerts ImmunizationCheck contains information needing to be checked.
	 * @return
	 */
	public List<String> getImmunizationAlerts(ImmunizationChecks alerts);
}
