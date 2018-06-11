package org.openmrs.module.chicaops.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IUnmarshallingContext;
import org.jibx.runtime.JiBXException;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Form;
import org.openmrs.Location;
import org.openmrs.LocationTag;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.FormService;
import org.openmrs.api.LocationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.chicaops.dashboard.CareCenterResult;
import org.openmrs.module.chicaops.dashboard.DirectoryProblem;
import org.openmrs.module.chicaops.dashboard.ForcedOutPWSProblem;
import org.openmrs.module.chicaops.dashboard.ImmunizationCheckResult;
import org.openmrs.module.chicaops.dashboard.ManualCheckinNumResult;
import org.openmrs.module.chicaops.dashboard.MemoryProblem;
import org.openmrs.module.chicaops.dashboard.MonitorResult;
import org.openmrs.module.chicaops.dashboard.RuleCheckResult;
import org.openmrs.module.chicaops.dashboard.RuleIdentifier;
import org.openmrs.module.chicaops.dashboard.ScanProblem;
import org.openmrs.module.chicaops.dashboard.ServerCheckResult;
import org.openmrs.module.chicaops.db.ChicaopsDAO;
import org.openmrs.module.chicaops.service.ChicaopsService;
import org.openmrs.module.chicaops.util.FileListTimeFilter;
import org.openmrs.module.chicaops.xmlBeans.dashboard.DashboardConfig;
import org.openmrs.module.chicaops.xmlBeans.dashboard.DirectoryCheck;
import org.openmrs.module.chicaops.xmlBeans.dashboard.ForcedOutPWSCheck;
import org.openmrs.module.chicaops.xmlBeans.dashboard.HL7ExportChecks;
import org.openmrs.module.chicaops.xmlBeans.dashboard.ImmunizationChecks;
import org.openmrs.module.chicaops.xmlBeans.dashboard.ManualCheckinChecks;
import org.openmrs.module.chicaops.xmlBeans.dashboard.MemoryCheck;
import org.openmrs.module.chicaops.xmlBeans.dashboard.RuleChecks;
import org.openmrs.module.chicaops.xmlBeans.dashboard.ScanCheck;
import org.openmrs.module.chicaops.xmlBeans.dashboard.ScanChecks;
import org.openmrs.module.chicaops.xmlBeans.dashboard.ServerChecks;
import org.openmrs.module.chicaops.xmlBeans.dashboard.StateToMonitor;
import org.openmrs.module.chicaops.xmlBeans.dashboard.StatesToMonitor;
import org.openmrs.module.chirdlutil.util.ChirdlUtilConstants;
import org.openmrs.module.chirdlutil.util.Util;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.FormAttributeValue;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.PatientState;
import org.openmrs.module.chirdlutilbackports.service.ChirdlUtilBackportsService;
import org.openmrs.module.dss.hibernateBeans.RuleEntry;

/**
 * Service layer implementation for the Dashboard.
 * 
 * @author Steve McKee
 */
public class ChicaopsServiceImpl implements ChicaopsService {

	private Log log = LogFactory.getLog(this.getClass());
	private ChicaopsDAO dao;
	
	/**
	 * Default Constructor
	 */
	public ChicaopsServiceImpl() {
	}
	
	/**
	 * @param dao The ChicaopsDAO to set.
	 */
	public void setChicaopsDAO(ChicaopsDAO dao) {
		this.dao = dao;
	}
	
	/**
	 * @return The ChicaopsDAO object.
	 */
	public ChicaopsDAO getChicaopsDAO() {
		return dao;
	}

    @Override
    public ArrayList<CareCenterResult> checkCareCenters() {
		Map<String, CareCenterResult> careCenterNameToResultMap = 
			new LinkedHashMap<String, CareCenterResult>();
		Map<Integer, CareCenterResult> careCenterIdToResultMap = 
			new HashMap<Integer, CareCenterResult>();
		populateLocationData(careCenterNameToResultMap, careCenterIdToResultMap);
		
		try {
			DashboardConfig config = getDashboardConfig();
			if (config == null) {
				return new ArrayList<CareCenterResult>(careCenterNameToResultMap.values());
			}
			
			// Check the states that need to be monitored.
		    StatesToMonitor states = config.getStatesToMonitor();
		    if (states != null && states.getStatesToMonitor() != null) {		    
			    for (StateToMonitor state : states.getStatesToMonitor()) {
			    	for (MonitorResult result : getChicaopsDAO().checkState(state)) {
				    	String careCenterName = result.getLocationName();
				    	CareCenterResult results = careCenterNameToResultMap.get(careCenterName);				    	
				    	results.addStateResult(result);
			    	}
		    	}
		    }
		    
		    // Check for any forced out PWSs.
		    ForcedOutPWSCheck forcedOutPWSs = config.getForcedOutPWSCheck();
		    if (forcedOutPWSs != null) {
			    for (PatientState patientState : getChicaopsDAO().getForcedOutPWSs(forcedOutPWSs)) {
			    	CareCenterResult results = careCenterIdToResultMap.get(patientState.getLocationId());
			    	results.addForcedOutPWS(new ForcedOutPWSProblem(forcedOutPWSs));
			    }
		    }
		    
		    // Check for alerts in the CHICA HL7 Export table
		    HL7ExportChecks alerts = config.getHl7ExportChecks();
		    if (alerts != null && alerts.getChecks().size() > 0) {
		    	List<Object[]> exportProbs = getChicaopsDAO().getHL7ExportAlerts(alerts);
		    	if (exportProbs.size() >= alerts.getNumErrors()) {
			    	for (Object[] arry : exportProbs) {
			    		CareCenterResult results = careCenterIdToResultMap.get(arry[1]);
			    		results.setHl7ExportChecks(alerts);
			    		results.addHl7ExportProblem(arry[0].toString());
			    	}
		    	}
		    }
		    
		
		    
		    // Check to see if anything has been scanned lately
		    ScanChecks scanChecks = config.getScanChecks();
		    if (scanChecks != null && scanChecks.getScanChecks().size() > 0) {
		    	FormService formService = Context.getFormService();
		    	LocationService locService = Context.getLocationService();
		    	List<Location> locations = locService.getAllLocations(false);
		    	for (ScanCheck check : scanChecks.getScanChecks()) {
		        	Form form = formService.getForm(check.getFormName());
		        	if (form == null) {
		        		log.error("Error performing scan check.  The form \"" + check.getFormName() + "\" does not exist.");
		        		continue;
		        	}
		        	
		    		long timePeriodMs = check.getTimePeriodInMilliseconds();
		    		Date date = new Date();
		    		date = new Date(date.getTime() - timePeriodMs);
		    		timePeriodMs = date.getTime();
		        	Calendar cal = Calendar.getInstance();
		        	cal.setTimeInMillis(timePeriodMs);
		        	// Account for Saturday and Sunday.  Go back to Friday if it is Saturday or Sunday.
		        	while (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY || cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
		        		cal.add(Calendar.DAY_OF_YEAR, -1);
		        	}
		        	
		        	if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY) {
		        		// We need to start at the beginning of Friday so we don't get over-notified on the weekends.
		        		cal.set(Calendar.HOUR_OF_DAY, 0);
		        		cal.set(Calendar.MINUTE, 0);
		        		cal.set(Calendar.SECOND, 0);
		        		cal.set(Calendar.MILLISECOND, 0);
		        	}
		        	
		        	long timePeriod = cal.getTimeInMillis();
		    		for (Location location : locations) {
		    			ScanProblem problem = performScanCheck(check, location, timePeriod, form);
		    			if (problem != null) {
		    				String locationName = location.getName();
		    				CareCenterResult results = careCenterNameToResultMap.get(locationName);					    	
					    	results.addScanProblem(problem);
		    			}
		    		}
		    	}
		    }
		    
		    // DWE 5/8/15 CHICA-367
		    // Check to see if there have been manual check-ins
		    // Adding here so that it can be added to the CareCenterResult object and displayed on the dashboard
		    // ********* This list will only contain ManualCheckinNumResult 
		    // objects for locations where the manual check-in 
		    // value is greater than the threshold specified in the config file
		    List<ManualCheckinNumResult> manualCheckinNumResultList = performManualCheckinChecks();
		    if(manualCheckinNumResultList != null)
		    {
		    	for(ManualCheckinNumResult manualCheckinNumResult : manualCheckinNumResultList)
		    	{
		    		CareCenterResult careCenterResult = careCenterIdToResultMap.get(manualCheckinNumResult.getLocation().getId());
		    		if(careCenterResult != null)
		    		{
		    			careCenterResult.setManualCheckinNumResult(manualCheckinNumResult);
		    		}
		    	}
		    }
		    
		} catch (Throwable e) {
			log.error("Error processing the dashboard configuration file.", e);
		}
	    
		ArrayList<CareCenterResult> returnList = new ArrayList<CareCenterResult>(careCenterNameToResultMap.values());
		careCenterNameToResultMap.clear();
		careCenterIdToResultMap.clear();
	    return returnList;
    }
    
    private ScanProblem performScanCheck(ScanCheck check, Location location, long timeSincedLastModDate, Form form) {    	
    	ChirdlUtilBackportsService backportService = Context.getService(ChirdlUtilBackportsService.class);
    	Set<String> checkedDirs = new HashSet<String>();    	
    	FilenameFilter filter = new FileListTimeFilter(null, "20", timeSincedLastModDate, null);
    	Date sinceDate = new Date(timeSincedLastModDate);
    	for (LocationTag tag : location.getTags()) {
    		FormAttributeValue fav = backportService.getFormAttributeValue(form.getFormId(), "defaultExportDirectory", 
    			tag.getId(), location.getLocationId());
    		if (fav != null && StringUtils.isNotBlank(fav.getValue()) && 
    				!checkedDirs.contains(fav.getValue())) {
    			String scanDirStr = fav.getValue();
    			checkedDirs.add(scanDirStr);

    			// Check to see if any forms have been printed first
    			FormAttributeValue formAttributeValueReprintStateName = backportService.getFormAttributeValue(form.getFormId(), ChirdlUtilConstants.FORM_ATTRIBUTE_REPRINT_STATE, 
    					tag.getId(), location.getLocationId());
    			
    			String reprintStateName = null;
    			if (formAttributeValueReprintStateName != null && StringUtils.isNotBlank(formAttributeValueReprintStateName.getValue())) {
    				reprintStateName = formAttributeValueReprintStateName.getValue();
    			}
    			List<PatientState> pStates = 
    				getChicaopsDAO().getPatientsStates(form.getFormId(), location.getLocationId(), sinceDate, reprintStateName);
    			if (pStates == null || pStates.size() == 0) {
    				continue;
    			}
    			
    			// Check the directory to see if any forms have been scanned
    			File scanDir = new QuickListFile(scanDirStr);
    			if (scanDir.exists()) {
    				String[] files = scanDir.list(filter);
    				if (files == null || files.length == 0) {
    					// No files were found...there's a problem.
    					checkedDirs.clear();
    					return new ScanProblem(check);
    				}
    			}
    		}
    	}
    	
    	checkedDirs.clear();
    	return null;
    }

    @Override
    public ServerCheckResult performServerChecks() {
		ServerCheckResult result = new ServerCheckResult();
		try {
			DashboardConfig config = getDashboardConfig();
			if (config == null) {
				return null;
			}
			
			ServerChecks checks = config.getServerChecks();
			if (checks == null) {
				return null;
			}
				
			performMemoryChecks(checks.getMemoryChecks(), result);
			performDirectoryChecks(checks.getDirectoryChecks(), result);
		} catch (Throwable e) {
			log.error("Error attempting to load the dashboard configuration file.", e);
		}
	    
	    return result;
    }
	
	private void performMemoryChecks(ArrayList<MemoryCheck> checks, 
	                                 ServerCheckResult result) throws Exception {
		Runtime.getRuntime().gc();
		for (MemoryCheck memCheck : checks) {
			// Check the heap memory usage
			MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
			MemoryUsage memoryUsage = memoryMXBean.getHeapMemoryUsage();
			double percentage = memCheck.getPercentileToNotify();
			double totMemory = memoryUsage.getMax();
			double usedMemory = memoryUsage.getUsed();
			double percentUsed = ((usedMemory/totMemory)*100);
			if (percentUsed >= percentage) {
				result.addMemProblem(new MemoryProblem(memCheck, (int)percentUsed, MemoryProblem.HEAP));
			}
			
			// Check the non-heap memory usage
			memoryUsage = memoryMXBean.getNonHeapMemoryUsage();
			totMemory = memoryUsage.getMax();
			usedMemory = memoryUsage.getUsed();
			percentUsed = ((usedMemory/totMemory)*100);
			if (percentUsed >= percentage) {
				result.addMemProblem(new MemoryProblem(memCheck, (int)percentUsed, MemoryProblem.NON_HEAP));
			}
		}
	}
	
	private void performDirectoryChecks(ArrayList<DirectoryCheck> checks, 
	                                    ServerCheckResult result) {
		DecimalFormat df = new DecimalFormat("##0.00");
		for (DirectoryCheck directoryCheck : checks) {
			Long timePeriodMs = directoryCheck.getTimePeriodInMilliseconds();
			Date date = new Date();
			date = new Date(date.getTime() - timePeriodMs);
			timePeriodMs = date.getTime();
			Long bufferTimeMs = directoryCheck.getBufferTimeInMilliseconds();
			Date bufferDate = new Date();
			bufferDate = new Date(bufferDate.getTime() - bufferTimeMs);
			bufferTimeMs = bufferDate.getTime();
			Map<String, String> imgNewToOldName = new HashMap<String, String>();
			Map<String, String> scanNewToOldName = new HashMap<String, String>();
			ArrayList<String> imgProblemFiles = new ArrayList<String>();
			ArrayList<String> scanProblemFiles = new ArrayList<String>();
			String imageDirStr = directoryCheck.getImageDir();
			String scanDirStr = directoryCheck.getScanDir();
			File imageDir = new File(imageDirStr);
			File scanDir = new File(scanDirStr);
			if (!imageDir.exists() || !imageDir.canRead()) {
				log.error("Error performing directory checks: The image directory (" + imageDir + ") does not exist or " +
						"cannot be read.");
				continue;
			} else if (!scanDir.exists() || !scanDir.canRead()) {
				log.error("Error performing directory checks: The scan directory (" + scanDir + ") does not exist or " +
				"cannot be read.");
				continue;
			} else if (!imageDir.isDirectory()) {
				log.error("Error performing directory checks: The image directory (" + imageDir + " is not directory.");
				continue;
			} else if (!scanDir.isDirectory()) {
				log.error("Error performing directory checks: The scan directory (" + scanDir + " is not directory.");
				continue;
			}
			
			File[] imageDirFiles = imageDir.listFiles(new FileListTimeFilter(null, "tif", timePeriodMs, bufferTimeMs));
			File[] scanDirFiles = scanDir.listFiles(new FileListTimeFilter(null, "20", timePeriodMs, bufferTimeMs));
			double imageDirSize = imageDirFiles.length;
			double scanDirSize = scanDirFiles.length;
			
			// index the compare files
			Set<String> scanSet = new HashSet<String>();
			scanSet = new HashSet<String>();
			for (File scanDirFile : scanDirFiles) {				
				String name = scanDirFile.getName();
				// need to remove the extension
				int index = name.lastIndexOf(".");
				String newName = name;
				if (index != -1) {
					newName = name.substring(0, index);
				}
				
				newName = Util.extractIntFromString(newName);
				scanSet.add(newName);
				scanNewToOldName.put(newName, name);
			}
			
			Set<String> imageSet = new HashSet<String>();
			for (File imageDirFile : imageDirFiles) {				
				String name = imageDirFile.getName();
				int index = name.lastIndexOf("_");
				String newName = name;
				if (index != -1) {
					newName = name.substring(0, index);
				}
				
				newName = Util.extractIntFromString(newName);
				imageSet.add(newName);
				imgNewToOldName.put(newName, name);
			}
			
			Iterator<String> iter = imageSet.iterator();
			while (iter.hasNext()) {
				String name = iter.next();
				if (!scanSet.contains(name)) {
					imgProblemFiles.add(imgNewToOldName.get(name));
				}
			}
			
			iter = scanSet.iterator();
			while (iter.hasNext()) {
				String name = iter.next();
				if (!imageSet.contains(name)) {
					scanProblemFiles.add(scanNewToOldName.get(name));
				}
			}
			
			if (imgProblemFiles.size() >= directoryCheck.getNumErrors()) {
				double successRate = 0;
				double rate = 100 * (imgProblemFiles.size()/(scanDirSize + imgProblemFiles.size()));
				successRate = 100 - rate;
				
				DirectoryProblem problem = new DirectoryProblem(
					directoryCheck, imgProblemFiles, new Double(df.format(successRate)));;
				result.addImageDirectoryProblem(problem);
			}
			
			if (scanProblemFiles.size() >= directoryCheck.getNumErrors()) {
				double successRate = 0;
				double rate = 100 * (scanProblemFiles.size()/(imageDirSize + scanProblemFiles.size()));
				successRate = 100 - rate;
				
				DirectoryProblem problem = new DirectoryProblem(
					directoryCheck, scanProblemFiles, new Double(df.format(successRate)));
				result.addScanDirectoryProblem(problem);
			}
			
			imgNewToOldName.clear();
			scanNewToOldName.clear();
		}
	}
	
	private void populateLocationData(Map<String, CareCenterResult> careCenterNameToResultMap, 
	                                  Map<Integer, CareCenterResult> careCenterIdToResultMap) {
		LocationService locService = Context.getLocationService();
		for (Location loc : locService.getAllLocations()) {
			String name = loc.getName();
			String description = loc.getDescription();
			Integer id = loc.getLocationId();
			CareCenterResult results = new CareCenterResult(loc.getName(), description);
			careCenterNameToResultMap.put(name, results);
			careCenterIdToResultMap.put(id, results);
		}
	}
	
	private DashboardConfig getDashboardConfig() throws JiBXException, FileNotFoundException {
		AdministrationService adminService = Context.getAdministrationService();
		String configFileStr = adminService.getGlobalProperty(
			"chicaops.dashboardConfigFile");
		if (configFileStr == null) {
			log.error("You must set a value for global property: "
				+ "chicaops.dashboardConfigFile");
			return null;
		}
		
		File configFile = new File(configFileStr);
		if (!configFile.exists()) {
			log.error("The file location specified for the global property "
				+ "chicaops.dashboardConfigFile does not exist.");
			return null;
		}
		
		IBindingFactory bfact = 
	        BindingDirectory.getFactory(DashboardConfig.class);
		
		IUnmarshallingContext uctx = bfact.createUnmarshallingContext();
		return (DashboardConfig)uctx.unmarshalDocument(
			new FileInputStream(configFile), null);
	}

    @Override
    public RuleCheckResult performRuleChecks() {
		RuleChecks ruleChecks = new RuleChecks();
		RuleCheckResult results = new RuleCheckResult(ruleChecks);
		try {
		    DashboardConfig dashboardConfig = getDashboardConfig();
		    if (dashboardConfig != null) {
    			ruleChecks = dashboardConfig.getRuleChecks();
    			if (ruleChecks != null) {
        			results = new RuleCheckResult(ruleChecks);
        			
        			// Check for rules that have never fired
        			if (ruleChecks.getNeverFiredCheck() != null) {
        				List<RuleEntry> ruleEntries = getChicaopsDAO().getNeverFiredRules();
        				if (ruleEntries != null) {
        					for (RuleEntry ruleEntry: ruleEntries) {
        						results.addNeverFiredRule(new RuleIdentifier(
        						    ruleEntry.getRule().getTokenName(), ruleEntry.getRuleType().getName()));
        					}
        				}
        			}
        			
        			// Check for rules that have not fired in the specified time period.
        			if (ruleChecks.getUnFiredCheck() != null) {
        				List<RuleEntry> ruleEntries = getChicaopsDAO().getUnFiredRules(ruleChecks.getUnFiredCheck());
        				if (ruleEntries != null) {
        					for (RuleEntry ruleEntry: ruleEntries) {
        						results.addUnFiredRule(new RuleIdentifier(
        							ruleEntry.getRule().getTokenName(), ruleEntry.getRuleType().getName()));
        					}
        				}
        			}
    			}
		    }
			return results;
		} catch (Exception e) {
			log.error("Error processing the dashboard configuration file.", e);
		}
		
		return new RuleCheckResult(ruleChecks);
    }
    
    /**
	 * Perform immunization registry (CHIRP) access checks.
	 * @return ImmunizationRegistryCheckResult object containing the results of the CHIRP registry check.
	 */
    
    
	@Override
    public ImmunizationCheckResult performImmunizationChecks(){
		ImmunizationCheckResult result = new ImmunizationCheckResult();
		try {
			DashboardConfig config = getDashboardConfig();
			if (config == null) {
				return null;
			}
			
			ImmunizationChecks immunChecks = config.getImmunizationChecks();
			if (immunChecks == null) {
			  return null;
			}
			
			//check  " Error type Query Immunization List Connection"
			//result.
			if (immunChecks != null && immunChecks.getChecks() != null 
					&&  immunChecks.getChecks().size() > 0) {
				
				//loop through the checks  which is an array of strings
				List<String> errors = getChicaopsDAO().getImmunizationAlerts(immunChecks);
		    	if (errors.size() >= immunChecks.getNumErrors()) {
			    	for (String arry : errors) {
			    		 result.addImmunizationProblem(arry.toString());
			    		
			    	}
		    	}
		    }
			result.setImmunizationChecks(immunChecks);
			
		} catch (Exception e) {
			log.error("Error attempting to load the dashboard configuration file.", e);
		}
		return result;
	}
    
    /**
     * Extension of the File object.  This basically makes the list function faster by 
     * returning an accepted file once found.  It does not continue once it finds one.  
     * This is to be used to check to see if any files exist matching the FilenameFilter.
     * 
     * @author Steve McKee
     */
    private class QuickListFile extends File {

        private static final long serialVersionUID = 1L;

        /**
         * Creates a new QuickListFile instance by converting the given pathname String into 
         * an abstract pathname.  If the given String is the empty String, then the result is 
         * the empty abstract pathname.
         * 
         * @param pathname A pathname String
         * @throws NullPointerException If the pathname argument is null.
         */
		public QuickListFile(String pathname) {
	        super(pathname);
        }

		/**
		 * Returns only one String if any are found.  This is to be used to tell if at least one 
		 * child file exists matching the FilenameFilter.
		 * 
		 * @param filter FilenameFilter used for matching child files.
		 */
		@Override
        public String[] list(FilenameFilter filter) {
			String[] names = list();
			if ((names == null) || (filter == null)) {
				return names;
			}
			
			// Only need to know if at least one file is accepted.
			for (String name : names) {
				if (filter.accept(this, name)) {
					return new String[] { name };
				}
			}
			
			return new String[0];
		}
    }

	@Override
	/**
	 * Perform monitoring clinic manual check-in times frequency. If it happens too frequently, the program will let chica team know. 
	 * @return ManualCheckinNumResult object containing the results of manual check-in monitoring result
	 */
	public List<ManualCheckinNumResult> performManualCheckinChecks() {
		DashboardConfig config=null;
		List<ManualCheckinNumResult> resultsList = new ArrayList<ManualCheckinNumResult>();
		try {
			config = getDashboardConfig();
			if (config == null) {
				return null;
			}
			ManualCheckinChecks manualCheckinChecks = config.getManualCheckinChecks();
			if (manualCheckinChecks == null) {
				  return null;
			}
			Date now = new Date();
			long diff = manualCheckinChecks.getTimePeriodInMilliseconds();
			
			Date start = new Date(now.getTime()-diff);
			EncounterService es = Context.getEncounterService();
			List<EncounterType> encounterTypes = new ArrayList<EncounterType>();
			encounterTypes.add(es.getEncounterType("ManualCheckin"));
			LocationService ls = Context.getLocationService();
			List<Location> locations = ls.getAllLocations();
			for(Location loc: locations){
				java.util.Collection<Encounter> encountersRecords = es.getEncounters(null, loc, start, now, null, encounterTypes, null, null, null, false); // CHICA-1151 Add null parameters for Collection<VisitType> and Collection<Visit>
				int manualCheckinNum = encountersRecords.size();
				
				if(manualCheckinNum>=manualCheckinChecks.getManualCheckinNum()){
					ManualCheckinNumResult result = new ManualCheckinNumResult();
					result.setManualCheckinChecks(manualCheckinChecks);
					result.setLocation(loc);
					result.setNumberOfManualCheckins(manualCheckinNum); 
					resultsList.add(result);
				}
				
			}
		} catch (Exception e) {
			log.error("Error attempting to load the dashboard configuration file.", e);
		}
		return resultsList;
	}
}
