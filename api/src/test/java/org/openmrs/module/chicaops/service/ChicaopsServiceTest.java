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
package org.openmrs.module.chicaops.service;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.chicaops.dashboard.RuleCheckResult;
import org.openmrs.module.chicaops.dashboard.RuleIdentifier;
import org.openmrs.test.BaseModuleContextSensitiveTest;


/**
 * Test class for testing the ChicaopsService
 */
public class ChicaopsServiceTest extends BaseModuleContextSensitiveTest {
	
	public static final String DBUNIT_SETUP_FILE = "dbunitFiles/tableSetup.xml";
	
	@Before
	public void runBeforeEachTest() throws Exception {
		executeDataSet(DBUNIT_SETUP_FILE);
	}
	
	@Test
	public void testPerformRuleChecks() throws Exception {
		ChicaopsService service = Context.getService(ChicaopsService.class);
		RuleCheckResult result = service.performRuleChecks();
		Assert.assertNotNull(result);
		List<RuleIdentifier> neverFiredRules = result.getNeverFiredRules();
		List<RuleIdentifier> unfiredRules = result.getUnFiredRules();
		
		Assert.assertNotNull(neverFiredRules);
		Assert.assertNotNull(unfiredRules);
		Assert.assertEquals(3, neverFiredRules.size());
		Assert.assertEquals(6, unfiredRules.size());
		
		int formCountPWS = 0;
		int formCountPWS2 = 0;
		for (RuleIdentifier ruleIdentifier : neverFiredRules) {
			if ("PWS".equals(ruleIdentifier.getRuleType())) {
				formCountPWS++;
			} else if ("PWS_2".equals(ruleIdentifier.getRuleType())) {
				formCountPWS2++;
			}
		}
		
		// There are a total of three rules available.  Each rule is referenced by the PWS and PWS_2.
		// Two rules fired for the PWS and one for the PWS_2. 
		Assert.assertEquals(1, formCountPWS);
		Assert.assertEquals(2, formCountPWS2);
		
		formCountPWS = 0;
		formCountPWS2 = 0;
		for (RuleIdentifier ruleIdentifier : unfiredRules) {
			if ("PWS".equals(ruleIdentifier.getRuleType())) {
				formCountPWS++;
			} else if ("PWS_2".equals(ruleIdentifier.getRuleType())) {
				formCountPWS2++;
			}
		}
		
		// The Dashboard Configuration file is only set to look back one minute to exclude any rule references.  
		// This means all rules should not have fired in the last minute.
		Assert.assertEquals(3, formCountPWS);
		Assert.assertEquals(3, formCountPWS2);
	}
}
