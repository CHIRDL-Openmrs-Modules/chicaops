/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.chicaops;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.chicaops.dashboard.CareCenterResult;
import org.openmrs.module.chicaops.dashboard.DashboardMailerPager;
import org.openmrs.module.chicaops.dashboard.RuleCheckResult;
import org.openmrs.module.chicaops.dashboard.ServerCheckResult;
import org.openmrs.module.chicaops.service.ChicaopsService;
import org.openmrs.scheduler.tasks.AbstractTask;


/**
 *
 * @author Steve McKee
 */
public class DashboardTask extends AbstractTask {
	
	 protected final Logger log = LoggerFactory.getLogger(getClass());
	
	/**
	 * @see org.openmrs.scheduler.tasks.AbstractTask#execute()
	 */
	@Override
	public void execute() {
		try {
		    ChicaopsService dashService = Context.getService(ChicaopsService.class);
        	
        	// Check the states
        	ArrayList<CareCenterResult> results = dashService.checkCareCenters();
        	
        	// Check server issues
        	ServerCheckResult serverResult = dashService.performServerChecks();
        	
        	// Check rules
        	RuleCheckResult ruleResult = dashService.performRuleChecks();
        	
        	
        	// Send emails/pages if necessary.
        	DashboardMailerPager mailer = new DashboardMailerPager();
        	mailer.sendEmailsOrPages(results);
        	mailer.sendEmailsOrPages(serverResult);
        	mailer.sendEmailsOrPages(ruleResult);
        } catch (Exception e) {
        	this.log.error("Error creating/sending email/pages", e);
        }
	}
}
