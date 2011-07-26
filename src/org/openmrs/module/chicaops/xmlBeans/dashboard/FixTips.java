package org.openmrs.module.chicaops.xmlBeans.dashboard;

/**
 * Bean containing any tips for fixing issues with states.
 * 
 * @author Steve McKee
 */
public class FixTips {

	private String[] tips = new String[0];

	/**
	 * Default Constructor
	 */
	public FixTips() {
	}
	
	/**
	 * Constructor method
	 * 
	 * @param tips Array of Strings containing tips on how to fix 
	 * any issues for a specific state.
	 */
	public FixTips(String[] tips) {
		this.tips = tips;
	}
	
    /**
     * @return the tips
     */
    public String[] getTips() {
    	return tips;
    }

	
    /**
     * @param tips the tips to set
     */
    public void setTips(String[] tips) {
    	this.tips = tips;
    }
}
