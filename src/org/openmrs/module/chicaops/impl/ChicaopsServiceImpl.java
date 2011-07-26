package org.openmrs.module.chicaops.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.text.DecimalFormat;
import java.util.ArrayList;
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
import org.openmrs.Location;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.LocationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.atd.hibernateBeans.PatientState;
import org.openmrs.module.chicaops.dashboard.CareCenterResult;
import org.openmrs.module.chicaops.dashboard.DirectoryProblem;
import org.openmrs.module.chicaops.dashboard.ForcedOutPWSProblem;
import org.openmrs.module.chicaops.dashboard.MemoryProblem;
import org.openmrs.module.chicaops.dashboard.MonitorResult;
import org.openmrs.module.chicaops.dashboard.RuleCheckResult;
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

    public ArrayList<CareCenterResult> monitorStates() {
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
				    	if (results == null) {
				    		LocationService locService = Context.getLocationService();
							Location loc = locService.getLocation(result.getLocationName());
							String description = loc.getDescription();
				    		results = new CareCenterResult(result.getLocationName(), description);
				    		careCenterNameToResultMap.put(careCenterName, results);
				    	}
				    	
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
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Error processing the dashboard configuration file.", e);
		}
	    
		ArrayList<CareCenterResult> returnList = new ArrayList<CareCenterResult>(careCenterNameToResultMap.values());
		careCenterNameToResultMap.clear();
		careCenterIdToResultMap.clear();
	    return returnList;
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
}
