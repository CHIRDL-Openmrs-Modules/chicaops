package org.openmrs.module.chicaops.db.hibernate;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.openmrs.Location;
import org.openmrs.api.LocationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.chicaops.dashboard.MonitorResult;
import org.openmrs.module.chicaops.db.ChicaopsDAO;
import org.openmrs.module.chicaops.xmlBeans.dashboard.ForcedOutPWSCheck;
import org.openmrs.module.chicaops.xmlBeans.dashboard.HL7ExportChecks;
import org.openmrs.module.chicaops.xmlBeans.dashboard.ImmunizationChecks;
import org.openmrs.module.chicaops.xmlBeans.dashboard.StateToMonitor;
import org.openmrs.module.chicaops.xmlBeans.dashboard.UnFiredRuleCheck;
import org.openmrs.module.chirdlutil.util.Util;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.PatientState;
import org.openmrs.module.dss.hibernateBeans.Rule;
import org.openmrs.module.dss.hibernateBeans.RuleEntry;

/**
 * Implementation of the DashboardDAO used for checking state information 
 * for errors/warnings.
 * 
 * @author Steve McKee
 */
public class HibernateChicaopsDAO implements ChicaopsDAO {
	
	private static final String CHECKIN_STATE = "CHECKIN";
	
	protected final Log log = LogFactory.getLog(getClass());

	/**
	 * Hibernate session factory
	 */
	private SessionFactory sessionFactory;
	
	/**
	 * Default constructor
	 */
	public HibernateChicaopsDAO() {
	}
	
	/**
	 * Set session factory
	 * 
	 * @param sessionFactory
	 */
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	public ArrayList<MonitorResult> checkState(StateToMonitor state) throws Exception {
		ArrayList<MonitorResult> results = new ArrayList<MonitorResult>();
		if (state == null) {
			return results;
		}
		
		try {
			List<PatientState> patientStates;
			if (CHECKIN_STATE.equals(state.getName())) {
				patientStates = getCheckinPatientStates(state);
			} else {
				patientStates = getGenericPatientStates(state);
			}
			
			Map<Integer, ArrayList<PatientState>> locationToPatientStateMap = 
				new HashMap<Integer, ArrayList<PatientState>>();
			Map<Integer, String> locIdToNameMap = new HashMap<Integer, String>();
			for (PatientState patientState : new ArrayList<PatientState>(patientStates)) {
				Integer location = patientState.getLocationId();
				ArrayList<PatientState> states = locationToPatientStateMap.get(location);
				if (states == null) {
					states = new ArrayList<PatientState>();
					locationToPatientStateMap.put(location, states);
				}
				
				states.add(patientState);
			}
			
			Set<Entry<Integer, ArrayList<PatientState>>> entrySet = 
				locationToPatientStateMap.entrySet();
			Iterator<Entry<Integer, ArrayList<PatientState>>> iter = 
				entrySet.iterator();
			while (iter.hasNext()) {
				Entry<Integer, ArrayList<PatientState>> entry = iter.next();
				int count = entry.getValue().size();
				if (count >= state.getNumErrors()) {
					Integer locId = entry.getKey();
					String locName = locIdToNameMap.get(locId);
					if (locName == null) {
						LocationService locService = Context.getLocationService();
						Location loc = locService.getLocation(locId);
						locName = loc.getName();
						locIdToNameMap.put(locId, locName);
					}
					
					results.add(new MonitorResult(state, true, locName, count));
				}
			}
			
			locationToPatientStateMap.clear();
			locIdToNameMap.clear();
		} catch (Exception e) {
			this.log.error(Util.getStackTrace(e));
			throw e;
		}
		
		return results;
	}
	
	public List<PatientState> getForcedOutPWSs(ForcedOutPWSCheck forcedOutPWSCheck) {
		String sql = "select * "
				+ "from chirdlutilbackports_patient_state a, chirdlutilbackports_patient_state b "
				+ "where a.session_id = b.session_id "
				+ "and (a.state = 6 and a.end_time is null AND TIMESTAMPDIFF(" + forcedOutPWSCheck.getTimePeriodUnit() 
				+ ", a.start_time, NOW()) <= ?) and b.state = 19";
		SQLQuery qry = this.sessionFactory.getCurrentSession().createSQLQuery(sql);
		qry.setInteger(0, forcedOutPWSCheck.getTimePeriod());
		qry.addEntity(PatientState.class);
		return qry.list();
	}
	
	public List<Object[]> getHL7ExportAlerts(HL7ExportChecks alerts) {
		StringBuffer sql = new StringBuffer("select c.name, a.location_id from encounter a, chica_hl7_export b, " +
				"chica_hl7_export_status c where a.encounter_id = b.encounter_id and b.status = c.hl7_export_status_id " +
				"and c.name IN (");
		ArrayList<String> al = alerts.getChecks();
		for (int i = 0; i < al.size(); i++) {
			if (i == 0) {
				sql.append("?");
				continue;
			}
			
			sql.append(", ?");
		}
		
		sql.append(") and TIMESTAMPDIFF(" + alerts.getTimePeriodUnit() + ", b.date_inserted, NOW())<= ?");
		SQLQuery qry = this.sessionFactory.getCurrentSession().createSQLQuery(sql.toString());
		int count = 0;
		for (String alert : al) {
			qry.setString(count++, alert);
		}
		
		qry.setInteger(count, alerts.getTimePeriod());
		
		return qry.list();
	}
	
	private List<PatientState> getGenericPatientStates(StateToMonitor state) {
		String sql = "select * from chirdlutilbackports_patient_state where state = (select state_id from chirdlutilbackports_state "
		        + "where name = ?) and (TIMESTAMPDIFF(" + state.getTimePeriodUnit() + ", start_time, NOW()) <= ?) and "
		        + "(TIMESTAMPDIFF(" + state.getElapsedTimeUnit() + ", start_time, end_time) >= ?) union "
		        + "select * from chirdlutilbackports_patient_state where state = (select state_id from chirdlutilbackports_state "
		        + "where name = ?) and end_time is null and (TIMESTAMPDIFF(" + state.getTimePeriodUnit()
		        + ", start_time, NOW()) <= ?) and " + "(TIMESTAMPDIFF(" + state.getElapsedTimeUnit()
		        + ", start_time, NOW()) >= ?)";
		SQLQuery qry = this.sessionFactory.getCurrentSession().createSQLQuery(sql);
		qry.setString(0, state.getName());
		qry.setInteger(1, state.getTimePeriod());
		qry.setInteger(2, state.getElapsedTime());
		qry.setString(3, state.getName());
		qry.setInteger(4, state.getTimePeriod());
		qry.setInteger(5, state.getElapsedTime());
		qry.addEntity(PatientState.class);
		return qry.list();
	}
	
	private List<PatientState> getCheckinPatientStates(StateToMonitor state) {
		List<PatientState> patientStates = new ArrayList<PatientState>();
		LocationService locService = Context.getLocationService();
		for (Location location : locService.getAllLocations()) {
			String sql = "select * from chirdlutilbackports_patient_state where state = (select state_id from chirdlutilbackports_state where name = ?) "
			        + "and start_time = (select MAX(start_time) from chirdlutilbackports_patient_state where state = (select state_id from "
			        + "chirdlutilbackports_state where name = ?) and location_id = ?) and (TIMESTAMPDIFF(" + state.getElapsedTimeUnit()
			        + ", start_time, NOW()) >= ?)";
			SQLQuery qry = this.sessionFactory.getCurrentSession().createSQLQuery(sql);
			qry.setString(0, state.getName());
			qry.setString(1, state.getName());
			qry.setInteger(2, location.getLocationId());
			qry.setInteger(3, state.getElapsedTime());
			qry.addEntity(PatientState.class);
			List<PatientState> states = qry.list();
			patientStates.addAll(states);
		}
		
		return patientStates;
	}

	/**
	 * @see org.openmrs.module.chicaops.db.ChicaopsDAO#getNeverFiredRules()
	 */
    public List<RuleEntry> getNeverFiredRules() {
		String sql = "SELECT *" + 
				"  FROM dss_rule_entry ruleEntry" + 
				"       INNER JOIN dss_rule rule" + 
				"          ON ruleEntry.rule_id = rule.rule_id" + 
				"       INNER JOIN dss_rule_type ruleType" + 
				"          ON ruleEntry.rule_type_id = ruleType.rule_type_id" +
				" AND ruleType.retired = false" + 
				" AND ruleEntry.retired = false" + 
				" AND ruleEntry.priority IS NOT NULL" + 
				" AND ruleEntry.priority < 1000" + 
				" AND rule.token_name <> ruleType.name" +
				" AND rule.rule_id not in (select distinct rule_id from atd_patient_atd_element)" + 
				" ORDER BY ruleType.name, rule.token_name";
		SQLQuery qry = this.sessionFactory.getCurrentSession().createSQLQuery(sql);
		qry.addEntity(RuleEntry.class);
		return qry.list();
    }

    public List<Rule> getUnFiredRules(UnFiredRuleCheck check) {
		String sql = "SELECT * FROM dss_rule "
			   + "WHERE rule_id NOT IN"
			   + "          (SELECT rule_id"
			   + "             FROM atd_patient_atd_element"
			   + "             WHERE creation_time >= (SELECT DATE_SUB(NOW(), INTERVAL ? "
			   + 			   check.getTimePeriodUnit() + ")))"
			   + "       AND PRIORITY IS NOT NULL"
			   + "       AND PRIORITY < 1000"
			   + "       AND token_name <> rule_type"
			   + "       AND version <> 0.1"
			   + "       AND rule_id NOT IN"
			   + "              (SELECT rule_id"
			   + "                FROM dss_Rule"
			   + "                WHERE rule_id NOT IN"
			   + "                         (SELECT rule_id FROM atd_patient_atd_element)"
			   + "                      AND priority IS NOT NULL"
			   + "                      AND priority < 1000"
			   + "                      AND token_name <> rule_type"
			   + "                      AND version <> 0.1)";

		SQLQuery qry = this.sessionFactory.getCurrentSession().createSQLQuery(sql);
		qry.setInteger(0, check.getTimePeriod());
		qry.addEntity(Rule.class);
		return qry.list();
    }

    public List<PatientState> getPatientsStates(Integer formId, Integer locationId, Date sinceDate, String reprintStateName) {
    	String sql = "select * " 
			+ "from chirdlutilbackports_patient_state "
			+ "where form_id = ? and location_id = ? and end_time is not NULL and timestampdiff(second, ?, end_time) >= 0 "
					+ "and state in ("
					+ "select state_id "
				    + "from chirdlutilbackports_state "
				    + "where name in ('"+reprintStateName+"', 'JIT_printed', "
				    		+ "'JIT_reprint'))";
    	
		SQLQuery qry = this.sessionFactory.getCurrentSession().createSQLQuery(sql);
		qry.setInteger(0, formId);
		qry.setInteger(1, locationId);
		qry.setTimestamp(2, new Timestamp(sinceDate.getTime()));
		qry.addEntity(PatientState.class);
		return qry.list();
    }
    
    public List<String> getImmunizationAlerts(ImmunizationChecks alerts) {
    	StringBuffer sql = new StringBuffer("select e.message from chirdlutilbackports_error e "
    			+ " join chirdlutilbackports_error_category c ON "  
    			+ " e.error_category_id = c.error_category_id"
    			+ " where c.name like 'Query Immunization List Connection' "
    			+ " and e.message in (");
    	
    	ArrayList<String> al = alerts.getChecks();
		for (int i = 0; i < al.size(); i++) {
			if (i == 0) {
				sql.append("?");
				continue;
			}
			
			sql.append(", ?");
		}
    	
		sql.append(") and TIMESTAMPDIFF(" + alerts.getTimePeriodUnit() + ", e.date_time, NOW())<= ?");
		SQLQuery qry = this.sessionFactory.getCurrentSession().createSQLQuery(sql.toString());
		int count = 0;
		for (String alert : al) {
			qry.setString(count++, alert);
		}
		
		qry.setInteger(count, alerts.getTimePeriod());
		return qry.list();
	}
}
