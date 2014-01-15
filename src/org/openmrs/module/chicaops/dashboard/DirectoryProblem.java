package org.openmrs.module.chicaops.dashboard;

import java.util.ArrayList;

import org.openmrs.module.chicaops.xmlBeans.dashboard.DirectoryCheck;

/**
 * Indicates a problem was found while processing a directory check.  Contains 
 * a list of filenames that exist in the base directory but not the compare 
 * directory.
 *
 * @author Steve McKee
 */
public class DirectoryProblem {

	private DirectoryCheck directoryCheck;
	private ArrayList<String> fileNames = new ArrayList<String>();
	private double successRate;
	
	/**
	 * Constructor method
	 * 
	 * @param directoryCheck The DirectoryCheck that was performed.
	 * @param filesNames The names of the files that exist in the base 
	 * directory but not the compare directory.
	 * @param successRate The rate of success of files found vs files 
	 * comparing to.
	 */
	public DirectoryProblem(DirectoryCheck directoryCheck, ArrayList<String> filesNames, 
	                        double successRate) {
		this.directoryCheck = directoryCheck;
		this.fileNames = filesNames;
		this.successRate = successRate;
	}

    /**
     * @return the directoryCheck
     */
    public DirectoryCheck getDirectoryCheck() {
    	return directoryCheck;
    }
	
    /**
     * @return the fileNames
     */
    public ArrayList<String> getFileNames() {
    	return fileNames;
    }

    /**
     * @return the successRate
     */
    public double getSuccessRate() {
    	return successRate;
    }
}
