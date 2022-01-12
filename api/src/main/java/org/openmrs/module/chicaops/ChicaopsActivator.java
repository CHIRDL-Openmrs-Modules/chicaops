package org.openmrs.module.chicaops;

import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.openmrs.GlobalProperty;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.BaseModuleActivator;
import org.openmrs.module.chirdlutil.util.Util;

/**
 * Checks that all global properties for this module have been set.
 * 
 * @author Steve McKee
 *
 */
public class ChicaopsActivator extends BaseModuleActivator {
	
	private Logger log = LoggerFactory.getLogger(this.getClass());
	
	/**
	 * @see org.openmrs.module.BaseModuleActivator#stopped()
	 */
	@Override
    public void stopped() {
		this.log.info("Shutting down Chica Ops Module");
	}

	/**
	 * @see org.openmrs.module.BaseModuleActivator#started()
	 */
	@Override
    public void started() {
		this.log.info("Starting Chica Ops Module");
		
		//check that all the required global properties are set
		checkGlobalProperties();
	}
	
	private void checkGlobalProperties()
	{
		try
		{
			AdministrationService adminService = Context.getAdministrationService();
			Iterator<GlobalProperty> properties = adminService
					.getAllGlobalProperties().iterator();
			GlobalProperty currProperty = null;
			String currValue = null;
			String currName = null;

			while (properties.hasNext())
			{
				currProperty = properties.next();
				currName = currProperty.getProperty();
				if (currName.equals("chicaops.dashboardConfigFile")  || currName.equals("chicaops.dashboardRefresh"))
				{
					currValue = currProperty.getPropertyValue();
					if (currValue == null || currValue.length() == 0)
					{
						this.log.error("You must set a value for global property: "
								+ currName);
					}
				}
			}
		} catch (Exception e)
		{
			this.log.error("Error checking global properties for chica ops module");
			this.log.error(e.getMessage());
			this.log.error(Util.getStackTrace(e));
		}
	}
}
