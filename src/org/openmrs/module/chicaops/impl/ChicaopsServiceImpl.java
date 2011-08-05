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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IUnmarshallingContext;
import org.jibx.runtime.JiBXException;
import org.openmrs.Form;
import org.openmrs.Location;
import org.openmrs.LocationTag;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.FormService;
import org.openmrs.api.LocationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.atd.hibernateBeans.FormAttributeValue;
import org.openmrs.module.atd.hibernateBeans.PatientState;
import org.openmrs.module.atd.service.ATDService;
import org.openmrs.module.chicaops.dashboard.CareCenterResult;
import org.openmrs.module.chicaops.dashboard.DirectoryProblem;
import org.openmrs.module.chicaops.dashboard.ForcedOutPWSProblem;
import org.openmrs.module.chicaops.dashboard.MemoryProblem;
import org.openmrs.module.chicaops.dashboard.MonitorResult;
import org.openmrs.module.chicaops.dashboard.RuleCheckResult;
import org.openmrs.module.chicaops.dashboard.ScanProblem;
import org.openmrs.module.chicaops.dashboard.ServerCheckResult;
import org.openmrs.module.chicaops.db.ChicaopsDAO;
import org.openmrs.module.chicaops.service.ChicaopsService;
import org.openmrs.module.chicaops.util.FileListTimeFilter;
import org.openmrs.module.chicaops.xmlBeans.dashboard.DashboardConfig;
import org.openmrs.module.chicaops.xmlBeans.dashboard.DirectoryCheck;
import org.openmrs.module.chicaops.xmlBeans.dashboard.ForcedOutPWSCheck;
import org.openmrs.module.chicaops.xmlBeans.dashboard.HL7ExportChecks;
import org.openmrs.module.chicaops.xmlBeans.dashboard.MemoryCheck;
import org.openmrs.module.chicaops.xmlBeans.dashboard.RuleChecks;
import org.openmrs.module.chicaops.xmlBeans.dashboard.ScanCheck;
import org.openmrs.module.chicaops.xmlBeans.dashboard.ScanChecks;
import org.openmrs.module.chicaops.xmlBeans.dashboard.ServerChecks;
import org.openmrs.module.chicaops.xmlBeans.dashboard.StateToMonitor;
import org.openmrs.module.chicaops.xmlBeans.dashboard.StatesToMonitor;
import org.openmrs.module.chirdlutil.util.Util;
import org.openmrs.module.dss.hibernateBeans.Rule;

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
		        	boolean adjusted = false;
		        	while (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY || cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
		        		cal.add(Calendar.DAY_OF_YEAR, -1);
		        		adjusted = true;
		        	}
		        	
		        	if (adjusted) {
		        		// We need to start at the beginning of Friday so we don't get over-notified on the weekends.
		        		cal.set(Calendar.HOUR, 0);
		        		cal.set(Calendar.MINUTE, 0);
		        		cal.set(Calendar.SECOND, 0);
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
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Error processing the dashboard configuration file.", e);
		}
	    
		ArrayList<CareCenterResult> returnList = new ArrayList<CareCenterResult>(careCenterNameToResultMap.values());
		careCenterNameToResultMap.clear();
		careCenterIdToResultMap.clear();
	    return returnList;
    }
    
    private ScanProblem performScanCheck(ScanCheck check, Location location, long timeSincedLastModDate, Form form) {
    	ATDService atdService = Context.getService(ATDService.class);
    	Set<String> checkedDirs = new HashSet<String>();    	
    	FilenameFilter filter = new FileListTimeFilter(null, "20", timeSincedLastModDate);
    	for (LocationTag tag : location.getTags()) {
    		FormAttributeValue fav = atdService.getFormAttributeValue(form.getFormId(), "defaultExportDirectory", 
    			tag.getId(), location.getLocationId());
    		if (fav != null && fav.getValue() != null && fav.getValue().trim().length() > 0 && 
    				!checkedDirs.contains(fav.getValue())) {
    			String scanDirStr = fav.getValue();
    			checkedDirs.add(scanDirStr);
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
		} catch (Exception e) {
			e.printStackTrace();
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
			long timePeriodMs = directoryCheck.getTimePeriodInMilliseconds();
			Date date = new Date();
			date = new Date(date.getTime() - timePeriodMs);
			timePeriodMs = date.getTime();
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
			
			File[] imageDirFiles = imageDir.listFiles(new FileListTimeFilter(null, "tif", timePeriodMs));
			File[] scanDirFiles = scanDir.listFiles(new FileListTimeFilter(null, "20", timePeriodMs));
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

    public RuleCheckResult performRuleChecks() {
		RuleChecks ruleChecks = new RuleChecks();
		RuleCheckResult results = new RuleCheckResult(ruleChecks);
		try {
			ruleChecks = getDashboardConfig().getRuleChecks();
			results = new RuleCheckResult(ruleChecks);
			
			// Check for rules that have never fired
			if (ruleChecks != null && ruleChecks.getNeverFiredCheck() != null) {
				List<Rule> rules = getChicaopsDAO().getNeverFiredRules();
				if (rules != null) {
					for (Rule rule: rules) {
						results.addNeverFiredRule(rule.getTitle());
					}
				}
			}
			
			// Check for rules that have not fired in the specified time period.
			if (ruleChecks != null && ruleChecks.getUnFiredCheck() != null) {
				List<Rule> rules = getChicaopsDAO().getUnFiredRules(ruleChecks.getUnFiredCheck());
				if (rules != null) {
					for (Rule rule: rules) {
						results.addUnFiredRule(rule.getTitle());
					}
				}
			}
			return results;
		} catch (Exception e) {
			log.error("Error processing the dashboard configuration file.", e);
			e.printStackTrace();
		}
		
		return new RuleCheckResult(ruleChecks);
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
}
