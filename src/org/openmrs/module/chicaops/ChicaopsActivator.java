package org.openmrs.module.chicaops;

import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.GlobalProperty;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.api.context.ContextAuthenticationException;
import org.openmrs.module.Activator;
import org.openmrs.module.chirdlutil.util.Util;

/**
 * Checks that all global properties for this module have been set.
 * 
 * @author Steve McKee
 *
 */
public class ChicaopsActivator implements Activator {
	
	private Log log = LogFactory.getLog(this.getClass());
	
	public void shutdown() {
		this.log.info("Shutting down Chica Ops Module");
	}
	
	public void startup() {
		this.log.info("Starting Chica Ops Module");
		
		//check that all the required global properties are set
		checkGlobalProperties();
	}
	
	private void checkGlobalProperties()
	{
		try
		{
			AdministrationService adminService = Context.getAdministrationService();
			Context.authenticate(adminService
					.getGlobalProperty("scheduler.username"), adminService
					.getGlobalProperty("scheduler.password"));
			Iterator<GlobalProperty> properties = adminService
					.getAllGlobalProperties().iterator();
			GlobalProperty currProperty = null;
			String currValue = null;
			String currName = null;

			while (properties.hasNext())
			{
				currProperty = properties.next();
				currName = currProperty.getProperty();
				if (currName.equals("chicaops.dashboardConfigFile"))
				{
					currValue = currProperty.getPropertyValue();
					if (currValue == null || currValue.length() == 0)
					{
						this.log.error("You must set a value for global property: "
								+ currName);
					}
				} else if (currName.equals("chicaops.dashboardRefresh")) {
					currValue = currProperty.getPropertyValue();
					if (currValue == null || currValue.length() == 0)
					{
						this.log.error("You must set a value for global property: "
								+ currName);
					}
				}
			}
		} catch (ContextAuthenticationException e)
		{
			this.log.error("Error checking global properties for chica ops module");
			this.log.error(e.getMessage());
			this.log.error(Util.getStackTrace(e));
		}
	}
}
