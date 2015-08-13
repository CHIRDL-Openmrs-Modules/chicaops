package org.openmrs.module.chicaops.web.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.chicaops.dashboard.CareCenterResult;
import org.openmrs.module.chicaops.dashboard.DashboardMailerPager;
import org.openmrs.module.chicaops.dashboard.ImmunizationCheckResult;
import org.openmrs.module.chicaops.dashboard.ManualCheckinNumResult;
import org.openmrs.module.chicaops.dashboard.RuleCheckResult;
import org.openmrs.module.chicaops.dashboard.ServerCheckResult;
import org.openmrs.module.chicaops.service.ChicaopsService;
import org.openmrs.module.chirdlutil.util.Util;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.mvc.SimpleFormController;

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
public class ChicaopsFormController extends SimpleFormController {
    
    /** Logger for this class and subclasses */
    protected final Log log = LogFactory.getLog(getClass());
    
    /**
     * Returns any extra data in a key-->value pair kind of way
     * 
     * @see org.springframework.web.servlet.mvc.SimpleFormController#referenceData(javax.servlet.http.HttpServletRequest,
     *      java.lang.Object, org.springframework.validation.Errors)
     */
    @Override
    protected Map<String, Object> referenceData(HttpServletRequest request) throws Exception {
        if(Context.getUserContext().getAuthenticatedUser()== null){
			return null;
		}
        Map<String, Object>  modelMap = new HashMap<String, Object> ();
        
        try {
        	AdministrationService adminService = Context.getAdministrationService();
        	ChicaopsService dashService = Context
				.getService(ChicaopsService.class);
        	
        	// Check the states
        	ArrayList<CareCenterResult> results = dashService.checkCareCenters();        	
        	modelMap.put("careCenters", results);
        	
        	// Check server issues
        	ServerCheckResult serverResult = dashService.performServerChecks();
        	modelMap.put("serverResult", serverResult);
        	
        	// Check rules
        	RuleCheckResult ruleResult = dashService.performRuleChecks();
        	modelMap.put("ruleResult", ruleResult);
        	
        	// Check rules
        	ImmunizationCheckResult immunizationResult = dashService.performImmunizationChecks();
        	modelMap.put("immunizationResult", immunizationResult);
        	
        	// Load the refresh rate
        	String refreshRate = adminService.getGlobalProperty("chicaops.dashboardRefresh");        	
        	if (refreshRate == null || refreshRate.trim().length() == 0) {
        		// Default to two minutes.
        		refreshRate = "120";
        	}
        	
        	modelMap.put("refreshPeriod", refreshRate);
        	
        	// Load the application name
        	String appName = adminService.getGlobalProperty("chicaops.dashboardApplicationName");
        	if (appName == null) {
        		appName = "";
        	}
        	
        	modelMap.put("appName", appName);
        	
        	try {
	        	// Send emails/pages if necessary.
	        	DashboardMailerPager mailer = new DashboardMailerPager();
	        	mailer.sendEmailsOrPages(results);
	        	mailer.sendEmailsOrPages(serverResult);
	        	mailer.sendEmailsOrPages(ruleResult);
	        	mailer.sendEmailsOrPages(immunizationResult);
        	} catch (Exception e) {
        		log.error("Error creating/sending email/pages", e);
        	}
        } catch (Exception e) {
        	this.log.error(Util.getStackTrace(e));
        	throw e;
        }
       
        return modelMap;
    }
    
	@Override
	protected Object formBackingObject(HttpServletRequest request)
			throws Exception {
		return "testing";
	}
}
