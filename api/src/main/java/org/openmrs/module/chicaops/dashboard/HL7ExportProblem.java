package org.openmrs.module.chicaops.dashboard;

/**
 * Representation of the existence of a status occurring in the 
 * HL7 Export table.
 *
 * @author Steve McKee
 */
public class HL7ExportProblem {
	
	private String statusName;

	/**
	 * Constructor method
	 * 
	 * @param statusName The status found in the HL7 Export table.
	 */
	public HL7ExportProblem(String statusName) {
		this.statusName = statusName;
	}
	
    /**
     * @return the statusName
     */
    public String getStatusName() {
    	return statusName;
    }
}
