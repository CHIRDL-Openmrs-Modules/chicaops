package org.openmrs.module.chicaops.xmlBeans.dashboard;

import java.util.ArrayList;

/**
 * Server checks set to be checked by the CHICA Dashboard.
 *
 * @author Steve McKee
 */
public class ServerChecks {
	
	private ArrayList<MemoryCheck> memoryChecks = new ArrayList<MemoryCheck>();
	private ArrayList<DirectoryCheck> directoryChecks = new ArrayList<DirectoryCheck>();

	/**
	 * Default Constructor
	 */
	public ServerChecks() {
	}
	
	/**
	 * Constructor method
	 * 
	 * @param memoryChecks ArrayList of MemoryCheck objects that need
	 * to be performed.
	 * @param directoryChecks ArrayList of DirectoryCheck objects that 
	 * need to be performed.
	 */
	public ServerChecks(ArrayList<MemoryCheck> memoryChecks, 
	                    ArrayList<DirectoryCheck> directoryChecks) {
		this.memoryChecks = memoryChecks;
		this.directoryChecks = directoryChecks;
	}
	
    /**
     * @return the memoryChecks
     */
    public ArrayList<MemoryCheck> getMemoryChecks() {
    	return memoryChecks;
    }
    
    /**
     * @param memoryChecks the memoryChecks to set
     */
    public void setMemoryChecks(ArrayList<MemoryCheck> memoryChecks) {
    	this.memoryChecks = memoryChecks;
    }
	
    /**
     * @return the directoryChecks
     */
    public ArrayList<DirectoryCheck> getDirectoryChecks() {
    	return directoryChecks;
    }
	
    /**
     * @param directoryChecks the directoryChecks to set
     */
    public void setDirectoryChecks(ArrayList<DirectoryCheck> directoryChecks) {
    	this.directoryChecks = directoryChecks;
    }
}
