package org.openmrs.module.chicaops.xmlBeans.dashboard;

/**
 * Class used to hold notification information for the operations dashboard.
 *
 * @author Steve McKee
 */
public class Notification {

	private String email;
	private String emailAddress;
	private String page;
	private String pageNumber;
	private String weekend;
	
	/**
	 * Default constructor
	 */
	public Notification() {
	}
	
	/**
	 * Constructor method
	 * 
	 * @param email Whether or not to send email ("Y" or "N").
	 * @param emailAddress Comma delimited list of email addresses to notify.
	 * @param page Whether or not to send a page ("Y" or "N").
	 * @param pageNumber The phone number to page.
	 */
	public Notification(String email, String emailAddress, String page, String pageNumber) {
		this.email = email;
		this.emailAddress = emailAddress;
		this.page = page;
		this.pageNumber = pageNumber;
	}
	
	
	/**
	 * Constructor method
	 * @param email Whether or not to send email ("Y" or "N").
	 * @param emailAddress Comma delimited list of email addresses to notify.
	 * @param page Whether or not to send a page ("Y" or "N").
	 * @param pageNumber The phone number to page.
	 * @param weekend if it is a weekend day
	 */
	public Notification(String email, String emailAddress, String page, String pageNumber, String weekend) {
		this.email = email;
		this.emailAddress = emailAddress;
		this.page = page;
		this.pageNumber = pageNumber;
		this.setWeekend(weekend);
	}
	
    /**
     * @return the email
     */
    public String getEmail() {
    	return email;
    }
	
    /**
     * @param email the email to set
     */
    public void setEmail(String email) {
    	this.email = email;
    }
	
    /**
     * @return the emailAddress
     */
    public String getEmailAddress() {
    	return emailAddress;
    }
	
    /**
     * @param emailAddress the emailAddress to set
     */
    public void setEmailAddress(String emailAddress) {
    	this.emailAddress = emailAddress;
    }
	
    /**
     * @return the page
     */
    public String getPage() {
    	return page;
    }
	
    /**
     * @param page the page to set
     */
    public void setPage(String page) {
    	this.page = page;
    }
	
    /**
     * @return the pageNumber
     */
    public String getPageNumber() {
    	return pageNumber;
    }
	
    /**
     * @param pageNumber the pageNumber to set
     */
    public void setPageNumber(String pageNumber) {
    	this.pageNumber = pageNumber;
    }

	public String getWeekend() {
		return weekend;
	}

	public void setWeekend(String weekend) {
		this.weekend = weekend;
	}
}
