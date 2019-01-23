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
import org.hibernate.type.StandardBasicTypes;
import org.openmrs.Location;
import org.openmrs.api.LocationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.chicaops.dashboard.MonitorResult;
import org.openmrs.module.chicaops.db.ChicaopsDAO;
import org.openmrs.module.chicaops.xmlBeans.dashboard.ForcedOutPWSCheck;
import org.openmrs.module.chicaops.xmlBeans.dashboard.HL7ExportChecks;
import org.openmrs.module.chicaops.xmlBeans.dashboard.StateToMonitor;
import org.openmrs.module.chicaops.xmlBeans.dashboard.UnFiredRuleCheck;
import org.openmrs.module.chicaops.xmlBeans.dashboard.WifiIssueChecks;
import org.openmrs.module.chirdlutil.util.ChirdlUtilConstants;
import org.openmrs.module.chirdlutil.util.Util;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.PatientState;
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
	
	@Override
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
	
	@Override
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
	
	@Override
    public Integer getWifiIssues(WifiIssueChecks wifiIssueChecks, Location location) {
        
        Set<String> formNames = org.openmrs.module.atd.util.Util.getPrimaryFormNameByLocation(
            ChirdlUtilConstants.LOC_TAG_ATTR_PRIMARY_PATIENT_FORM, location.getLocationId());
        
        if (!formNames.isEmpty()) {
            StringBuilder sql = new StringBuilder("select count(distinct form_instance_id) as num_issues from "
                    + "(select a.form_instance_id from atd_statistics a " + "inner join obs d on a.obsv_id=d.obs_id "
                    + "inner join concept_name b on d.concept_id=b.concept_id "
                    + "inner join concept_name c on d.value_coded = c.concept_id " + "where TIMESTAMPDIFF("
                    + wifiIssueChecks.getTimePeriodUnit() + ", printed_timestamp, NOW()) <= ? and form_name in (");
            
            for (int i = 0; i < formNames.size(); i++) {
                if (i == 0) {
                    sql.append("?");
                    continue;
                }
                
                sql.append(", ?");
            }
            
            sql.append(") " + "and a.location_id=? " + "group by a.form_instance_id,a.location_id,a.rule_id,b.name,c.name "
                    + "having count(*)>1)a");
            
            SQLQuery qry = this.sessionFactory.getCurrentSession().createSQLQuery(sql.toString());
            qry.setInteger(0, wifiIssueChecks.getTimePeriod());
            
            int count = 1;
            for (String alert : formNames) {
                qry.setString(count++, alert);
            }
            qry.setInteger(2, location.getLocationId());
            qry.addScalar("num_issues", StandardBasicTypes.LONG);
            List results = qry.list();
            return Integer.valueOf(results.get(0).toString());
        }
        return 0;
    }
	
	@Override
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
    @Override
    public List<RuleEntry> getNeverFiredRules() {
    	StringBuilder sql = new StringBuilder();
    	sql.append("SELECT *\n");
    	sql.append("FROM dss_rule_entry ruleEntry\n");
    	sql.append("	 INNER JOIN dss_rule rule ON ruleEntry.rule_id = rule.rule_id\n");
    	sql.append("	 INNER JOIN dss_rule_type ruleType\n");
    	sql.append("		ON     ruleEntry.rule_type_id = ruleType.rule_type_id\n");
    	sql.append("		   AND ruleType.retired = FALSE\n");
    	sql.append("		   AND ruleEntry.retired = FALSE\n");
    	sql.append("		   AND ruleEntry.priority IS NOT NULL\n");
    	sql.append("		   AND ruleEntry.priority < ?\n");
    	sql.append("		   AND rule.token_name <> ruleType.name\n");
    	sql.append("		   AND ruleEntry.rule_entry_id NOT IN\n");
    	sql.append("				  (SELECT ruleEntry2.rule_entry_id\n");
    	sql.append("					 FROM dss_rule_entry ruleEntry2\n");
    	sql.append("						  INNER JOIN dss_rule_type ruleType2\n");
    	sql.append("							 ON ruleEntry2.rule_type_id =\n");
    	sql.append("								ruleType2.rule_type_id\n");
    	sql.append("						  INNER JOIN dss_rule rule2\n");
    	sql.append("							 ON ruleEntry2.rule_id = rule2.rule_id\n");
    	sql.append("						  INNER JOIN atd_patient_atd_element atd\n");
    	sql.append("							 ON rule2.rule_id = atd.rule_id\n");
    	sql.append("						  INNER JOIN form form\n");
    	sql.append("							 ON atd.form_id = form.form_id\n");
    	sql.append("					WHERE ruleType2.name = form.name)\n");
    	sql.append("ORDER BY ruleType.name, rule.token_name\n");

		SQLQuery qry = this.sessionFactory.getCurrentSession().createSQLQuery(sql.toString());
		qry.addEntity(RuleEntry.class);
		qry.setInteger(0, RuleEntry.RULE_PRIORITY_RETIRE);
		return qry.list();
    }

    /**
     * @see org.openmrs.module.chicaops.db.ChicaopsDAO#getUnFiredRules(
     * org.openmrs.module.chicaops.xmlBeans.dashboard.UnFiredRuleCheck)
     */
    @Override
    public List<RuleEntry> getUnFiredRules(UnFiredRuleCheck check) {
    	StringBuilder sql = new StringBuilder();
    	sql.append("SELECT *\n");
    	sql.append("    FROM dss_rule_entry ruleEntry\n");
    	sql.append("         INNER JOIN dss_rule rule ON ruleEntry.rule_id = rule.rule_id\n");
    	sql.append("         INNER JOIN dss_rule_type ruleType\n");
    	sql.append("            ON     ruleEntry.rule_type_id = ruleType.rule_type_id\n");
    	sql.append("               AND ruleType.retired = FALSE\n");
    	sql.append("               AND ruleEntry.retired = FALSE\n");
    	sql.append("               AND ruleEntry.priority IS NOT NULL\n");
    	sql.append("               AND ruleEntry.priority < ?\n");
    	sql.append("               AND rule.token_name <> ruleType.name\n");
    	sql.append("               AND ruleEntry.rule_entry_id NOT IN\n");
    	sql.append("                      (SELECT ruleEntry2.rule_entry_id\n");
    	sql.append("					   FROM dss_rule_entry ruleEntry2\n");
    	sql.append("						  INNER JOIN dss_rule_type ruleType2\n");
    	sql.append("							 ON ruleEntry2.rule_type_id =\n");
    	sql.append("								ruleType2.rule_type_id\n");
    	sql.append("						  INNER JOIN dss_rule rule2\n");
    	sql.append("							 ON ruleEntry2.rule_id = rule2.rule_id\n");
    	sql.append("						  INNER JOIN atd_patient_atd_element atd\n");
    	sql.append("							 ON rule2.rule_id = atd.rule_id\n");
    	sql.append("						  INNER JOIN form form\n");
    	sql.append("							 ON atd.form_id = form.form_id\n");
    	sql.append("					   WHERE ruleType2.name = form.name\n");
    	sql.append("                       AND TIMESTAMPADD(");
    	sql.append(check.getTimePeriodUnit());
    	sql.append(", ?, atd.creation_time) >= NOW())\n");
    	sql.append("ORDER BY ruleType.name, rule.token_name\n");

		SQLQuery qry = this.sessionFactory.getCurrentSession().createSQLQuery(sql.toString());
		qry.setInteger(0, RuleEntry.RULE_PRIORITY_RETIRE);
		qry.setInteger(1, check.getTimePeriod());
		qry.addEntity(RuleEntry.class);
		return qry.list();
    }

    @Override
    public List<PatientState> getPatientsStates(Integer formId, Integer locationId, Date sinceDate, String reprintStateName) {
    	StringBuilder sql = new StringBuilder("select * ");
		sql.append("from chirdlutilbackports_patient_state ");
		sql.append("where form_id = ? and location_id = ? and end_time is not NULL and timestampdiff(second, ?, end_time) >= 0 ");
		sql.append("and state in (");
		sql.append("select state_id ");
		sql.append("from chirdlutilbackports_state ");
		sql.append("where name in ('"+reprintStateName+"', 'JIT_printed', ");
		sql.append("'JIT_reprint'))");
    	
		SQLQuery qry = this.sessionFactory.getCurrentSession().createSQLQuery(sql.toString());
		qry.setInteger(0, formId);
		qry.setInteger(1, locationId);
		qry.setTimestamp(2, new Timestamp(sinceDate.getTime()));
		qry.addEntity(PatientState.class);
		return qry.list();
    }
   
}
