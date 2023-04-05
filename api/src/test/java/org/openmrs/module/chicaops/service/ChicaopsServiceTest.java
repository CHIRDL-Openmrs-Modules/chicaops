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

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openmrs.annotation.Authorized;
import org.openmrs.api.context.Context;
import org.openmrs.module.chicaops.dashboard.RuleCheckResult;
import org.openmrs.module.chicaops.dashboard.RuleIdentifier;
import org.openmrs.test.jupiter.BaseModuleContextSensitiveTest;
import org.openmrs.test.SkipBaseSetup;


/**
 * Test class for testing the ChicaopsService
 */
public class ChicaopsServiceTest extends BaseModuleContextSensitiveTest {
	
	public static final String DBUNIT_SETUP_FILE = "dbunitFiles/tableSetup.xml";
	
	@BeforeEach
	public void runBeforeEachTest() throws Exception {
		executeDataSet(DBUNIT_SETUP_FILE);
	}
	
	@Test
	public void testPerformRuleChecks() throws Exception {
		ChicaopsService service = Context.getService(ChicaopsService.class);
		RuleCheckResult result = service.performRuleChecks();
		Assertions.assertNotNull(result);
		List<RuleIdentifier> neverFiredRules = result.getNeverFiredRules();
		List<RuleIdentifier> unfiredRules = result.getUnFiredRules();
		
		Assertions.assertNotNull(neverFiredRules);
		Assertions.assertNotNull(unfiredRules);
		Assertions.assertEquals(3, neverFiredRules.size());
		Assertions.assertEquals(6, unfiredRules.size());
		
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
		Assertions.assertEquals(1, formCountPWS);
		Assertions.assertEquals(2, formCountPWS2);
		
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
		Assertions.assertEquals(3, formCountPWS);
		Assertions.assertEquals(3, formCountPWS2);
	}
	
	@Test
	@SkipBaseSetup
	public void checkAuthorizationAnnotations() throws Exception {
		Method[] allMethods = ChicaopsService.class.getDeclaredMethods();
		for (Method method : allMethods) {
		    if (Modifier.isPublic(method.getModifiers())) {
		        Authorized authorized = method.getAnnotation(Authorized.class);
		        Assertions.assertNotNull(authorized,"Authorized annotation not found on method " + method.getName());
		    }
		}
	}
}
