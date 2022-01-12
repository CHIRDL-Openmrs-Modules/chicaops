package org.openmrs.module.chicaops.web.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.chicaops.dashboard.CareCenterResult;
import org.openmrs.module.chicaops.dashboard.DashboardMailerPager;
import org.openmrs.module.chicaops.dashboard.RuleCheckResult;
import org.openmrs.module.chicaops.dashboard.ServerCheckResult;
import org.openmrs.module.chicaops.service.ChicaopsService;
import org.openmrs.module.chirdlutil.util.ChirdlUtilConstants;
import org.openmrs.module.chirdlutil.util.Util;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * This class is found by spring because we put
 * "org.openmrs.module.chicaops" into the spring
 * "/metadata/moduleApplicationContext.xml". <br/>
 * <br/>
 * The @Controller annotation right below this is very important.
 * 
 * @author Steve McKee
 */
@Controller
@RequestMapping(value = "module/chicaops/dashboard.form")
public class ChicaopsFormController {
    
    /** Logger for this class and subclasses */
    protected final Logger log = LoggerFactory.getLogger(getClass());
    
    /** Form view */
    private static final String FORM_VIEW = "/module/chicaops/dashboard";
    
    /** Parameters */
    private static final String PARAMETER_APP_NAME = "appName";
    private static final String PARAMETER_REFRESH_PERIOD = "refreshPeriod";
    private static final String PARAMETER_RULE_RESULT = "ruleResult";
    private static final String PARAMETER_SERVER_RESULT = "serverResult";
    private static final String PARAMETER_CARE_CENTERS = "careCenters";
    
    /** Global properties */
    private static final String GLOBAL_PROP_CHICAOPS_DASHBOARD_APPLICATION_NAME = "chicaops.dashboardApplicationName";
    private static final String GLOBAL_PROP_CHICAOPS_DASHBOARD_REFRESH = "chicaops.dashboardRefresh";
    
    /** Default values */
    private static final String DEFAULT_REFRESH_RATE = "120";
    
    /**
     * Form initialization method.
     * 
     * @param request The HTTP request information
     * @param map The map to populate for return to the client
     * @return The form view name
     */
    @RequestMapping(method = RequestMethod.GET)
    protected String initForm(ModelMap map) {
        if(Context.getUserContext().getAuthenticatedUser()== null) {
			return null;
		}
        
        try {
        	AdministrationService adminService = Context.getAdministrationService();
        	ChicaopsService dashService = Context
				.getService(ChicaopsService.class);
        	
        	// Check the states
        	List<CareCenterResult> results = dashService.checkCareCenters();        	
        	map.put(PARAMETER_CARE_CENTERS, results);
        	
        	// Check server issues
        	ServerCheckResult serverResult = dashService.performServerChecks();
        	map.put(PARAMETER_SERVER_RESULT, serverResult);
        	
        	// Check rules
        	RuleCheckResult ruleResult = dashService.performRuleChecks();
        	map.put(PARAMETER_RULE_RESULT, ruleResult);
        	
        	// Load the refresh rate
        	String refreshRate = adminService.getGlobalProperty(GLOBAL_PROP_CHICAOPS_DASHBOARD_REFRESH);        	
        	if (refreshRate == null || refreshRate.trim().length() == 0) {
        		// Default to two minutes.
        		refreshRate = DEFAULT_REFRESH_RATE;
        	}
        	
        	map.put(PARAMETER_REFRESH_PERIOD, refreshRate);
        	
        	// Load the application name
        	String appName = adminService.getGlobalProperty(GLOBAL_PROP_CHICAOPS_DASHBOARD_APPLICATION_NAME);
        	if (appName == null) {
        		appName = ChirdlUtilConstants.GENERAL_INFO_EMPTY_STRING;
        	}
        	
        	map.put(PARAMETER_APP_NAME, appName);
        	sendEmailAndPages(results, serverResult, ruleResult);
        } catch (Exception e) {
        	this.log.error(Util.getStackTrace(e));
        	throw e;
        }
       
        return FORM_VIEW;
    }
    
    /**
     * Send necessary email and pages to configured recipients.
     * 
     * @param results The clinic-specific results
     * @param serverResult The server check results
     * @param ruleResult The rules check results
     */
    private void sendEmailAndPages(List<CareCenterResult> results, ServerCheckResult serverResult, 
            RuleCheckResult ruleResult) {
        try {
            // Send emails/pages if necessary.
            DashboardMailerPager mailer = new DashboardMailerPager();
            mailer.sendEmailsOrPages(results);
            mailer.sendEmailsOrPages(serverResult);
            mailer.sendEmailsOrPages(ruleResult);
        } catch (Exception e) {
            log.error("Error creating/sending email/pages", e);
        }
    }
}
