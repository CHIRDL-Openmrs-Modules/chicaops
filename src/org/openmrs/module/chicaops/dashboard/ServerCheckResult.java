package org.openmrs.module.chicaops.dashboard;

import java.util.ArrayList;

import org.openmrs.module.chicaops.xmlBeans.dashboard.DashboardConfig;

/**
 * Used to hold the results of all the server checks.
 *
 * @author Steve McKee
 */
public class ServerCheckResult {

	private ArrayList<MemoryProblem> memProblems = new ArrayList<MemoryProblem>();
	private ArrayList<DirectoryProblem> imageDirectoryProblems = new ArrayList<DirectoryProblem>();
	private ArrayList<DirectoryProblem> scanDirectoryProblems = new ArrayList<DirectoryProblem>();
	boolean hasErrors = false;
	boolean hasWarnings = false;

	/**
	 * Default Constructor
	 */
	public ServerCheckResult() {
	}
	
    /**
     * @return the memProblems
     */
    public ArrayList<MemoryProblem> getMemProblems() {
    	return memProblems;
    }    
    
    /**
     * @return the imageDirectoryProblems
     */
    public ArrayList<DirectoryProblem> getImageDirectoryProblems() {
    	return imageDirectoryProblems;
    }
    
    /**
     * @return the scanDirectoryProblems
     */
    public ArrayList<DirectoryProblem> getScanDirectoryProblems() {
    	return scanDirectoryProblems;
    }

	/**
     * Adds a new MemoryProblem object.
     * 
     * @param memProblem The MemoryProblem object to add.
     */
    public void addMemProblem(MemoryProblem memProblem) {
    	memProblems.add(memProblem);
    	if (DashboardConfig.SEVERITY_ERROR.equals(memProblem.getMemCheck().getSeverity())) {
    		hasErrors = true;
    	} else if (DashboardConfig.SEVERITY_WARNING.equals(memProblem.getMemCheck().getSeverity())) {
    		hasWarnings = true;
    	}
    }
    
    /**
     * Adds a new DirectoryProblem object.
     * 
     * @param imageDirectoryProblems The DirectoryProblem object to add.
     */
    public void addImageDirectoryProblem(DirectoryProblem imageDirectoryProblem) {
    	imageDirectoryProblems.add(imageDirectoryProblem);
    	if (DashboardConfig.SEVERITY_ERROR.equals(imageDirectoryProblem.getDirectoryCheck().getSeverity())) {
    		hasErrors = true;
    	} else if (DashboardConfig.SEVERITY_WARNING.equals(imageDirectoryProblem.getDirectoryCheck().getSeverity())) {
    		hasWarnings = true;
    	}
    }
    
    /**
     * Adds a new DirectoryProblem object.
     * 
     * @param imageDirectoryProblems The DirectoryProblem object to add.
     */
    public void addScanDirectoryProblem(DirectoryProblem scanDirectoryProblem) {
    	scanDirectoryProblems.add(scanDirectoryProblem);
    	if (DashboardConfig.SEVERITY_ERROR.equals(scanDirectoryProblem.getDirectoryCheck().getSeverity())) {
    		hasErrors = true;
    	} else if (DashboardConfig.SEVERITY_WARNING.equals(scanDirectoryProblem.getDirectoryCheck().getSeverity())) {
    		hasWarnings = true;
    	}
    }
    
    /**
     * @return the hasErrors
     */
    public boolean isHasErrors() {
    	return hasErrors;
    }
	
    /**
     * @return the hasWarnings
     */
    public boolean isHasWarnings() {
    	return hasWarnings;
    }
}
