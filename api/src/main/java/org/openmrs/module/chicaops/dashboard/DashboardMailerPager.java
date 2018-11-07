package org.openmrs.module.chicaops.dashboard;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.chicaops.xmlBeans.dashboard.DashboardConfig;
import org.openmrs.module.chicaops.xmlBeans.dashboard.DirectoryCheck;
import org.openmrs.module.chicaops.xmlBeans.dashboard.ForcedOutPWSCheck;
import org.openmrs.module.chicaops.xmlBeans.dashboard.HL7ExportChecks;
import org.openmrs.module.chicaops.xmlBeans.dashboard.ManualCheckinChecks;
import org.openmrs.module.chicaops.xmlBeans.dashboard.MemoryCheck;
import org.openmrs.module.chicaops.xmlBeans.dashboard.NeverFiredRuleCheck;
import org.openmrs.module.chicaops.xmlBeans.dashboard.Notification;
import org.openmrs.module.chicaops.xmlBeans.dashboard.RuleChecks;
import org.openmrs.module.chicaops.xmlBeans.dashboard.ScanCheck;
import org.openmrs.module.chicaops.xmlBeans.dashboard.StateToMonitor;
import org.openmrs.module.chicaops.xmlBeans.dashboard.UnFiredRuleCheck;
import org.openmrs.module.chicaops.xmlBeans.dashboard.WifiIssueChecks;
import org.openmrs.module.chirdlutil.util.MailSender;

/**
 * Handles all the email/pager messaging for the CHICA Operations Dashboard.
 * 
 * @author Steve McKee
 */
public class DashboardMailerPager {
	
	private static Map<Integer, Long> messageToTimeMap = new ConcurrentHashMap<Integer, Long>(new HashMap<Integer, Long>());
	
	/** Logger for this class and subclasses */
	protected final Log log = LogFactory.getLog(getClass());
	
	private final String MAIL_SUBJECT = "CHICA Operations Dashboard Notice";
	
	private final String MAIL_SENDER = "chica.operations.dashboard@iupui.edu";
	
	private Properties mailProps = new Properties();
	
	private MailSender mailSender = null;
	
	private String baseUrl;
	
	private String idParam;
	
	private String textParam;
	
	private long thresholdTime = 3600000; // Default to 60 minutes.
	
	/**
	 * Constructor method
	 */
	public DashboardMailerPager() {
		AdministrationService adminService = Context.getAdministrationService();		
		String smtpMailHost = adminService.getGlobalProperty("chirdlutil.smtpMailHost");
		if (smtpMailHost != null) {
		    this.mailProps.put("mail.smtp.host", smtpMailHost);
		}
		
		this.mailSender = new MailSender(this.mailProps);
		this.idParam = adminService.getGlobalProperty("chica.pagerUrlNumberParam");
		this.textParam = adminService.getGlobalProperty("chica.pagerUrlMessageParam");
		this.baseUrl = adminService.getGlobalProperty("chica.pagerBaseURL");
		String threshold = adminService.getGlobalProperty("chicaops.dashboardMessageWaitThreshold");
		if (threshold != null) {
		    this.thresholdTime = Integer.parseInt(threshold);
		    this.thresholdTime = this.thresholdTime * 60000;
		}
	}
	
	/**
	 * Sends emails/pages for the Care Center Results.
	 * 
	 * @param results List of CareCenterResult objects.
	 */
	public void sendEmailsOrPages(List<CareCenterResult> results) {
		for (CareCenterResult result : results) {
			String location = result.getCareCenterName();
			String locationDescription = result.getCareCenterDescription();
			// Forced-out PWSs
			int fopwsProbSize = result.getForcedOutPWSs().size();
			if (fopwsProbSize > 0) {
				ForcedOutPWSCheck pwsCheck = result.getForcedOutPWSs().get(0).getForcedOutPWSCheck();
				Notification notification = pwsCheck.getNotification();
				if (notification != null) {
					if (DashboardConfig.YES_INDICATOR.equalsIgnoreCase(notification.getEmail()) || 
							DashboardConfig.YES_INDICATOR.equalsIgnoreCase(notification.getPage())) {
						String message = "There have been " + fopwsProbSize + " PWS's forced out in the past "
						        + pwsCheck.getTimePeriod() + " " + pwsCheck.getTimePeriodUnit() + "(s) at "
						        + result.getCareCenterName() + " (" + result.getCareCenterDescription()
						        + ").\n\nRegards,\nCHICA Operations Dashboard";
						if (canSendMessage(message,notification)) {
							if (DashboardConfig.YES_INDICATOR.equalsIgnoreCase(notification.getEmail())) {
								sendMail(message, notification.getEmailAddress(), location, locationDescription);
							} 
							if (DashboardConfig.YES_INDICATOR.equalsIgnoreCase(notification.getPage())) {
								sendPage(message, notification.getPageNumber());
							}
						}
					}
				}
			}
			
			// HL7 export issues
			Map<String, Integer> probsMap = result.getHl7ExportProblems();
			if (!probsMap.isEmpty()) {
				HL7ExportChecks checks = result.getHl7ExportChecks();
				Notification notification = checks.getNotification();
				if (notification != null) {
					if (DashboardConfig.YES_INDICATOR.equalsIgnoreCase(notification.getEmail()) || 
							DashboardConfig.YES_INDICATOR.equalsIgnoreCase(notification.getPage())) {
						Set<Entry<String, Integer>> entries = probsMap.entrySet();
						Iterator<Entry<String, Integer>> iter = entries.iterator();
						StringBuffer message = new StringBuffer("There have been some HL7 export issues over the past ");
						message.append(checks.getTimePeriod());
						message.append(" ");
						message.append(checks.getTimePeriodUnit());
						message.append("(s) at ");
						message.append(result.getCareCenterName());
						message.append(" (");
						message.append(result.getCareCenterDescription());
						message.append("):\n");
						while (iter.hasNext()) {
							Entry<String, Integer> entry = iter.next();
							message.append("\n");
							message.append(entry.getKey());
						}
						
						message.append("\n\nRegards,\nCHICA Operations Dashboard");
						if (canSendMessage(message.toString(), notification)) {
							if (DashboardConfig.YES_INDICATOR.equalsIgnoreCase(notification.getEmail())) {
								sendMail(message.toString(), notification.getEmailAddress(), location, locationDescription);
							}
							if (DashboardConfig.YES_INDICATOR.equalsIgnoreCase(notification.getPage())) {
								sendPage(message.toString(), notification.getPageNumber());
							}
						}
					}
				}
			}
			
			// State issues
			for (MonitorResult monResult : result.getStateResults()) {
				StateToMonitor stateMon = monResult.getStateToMonitor();
				Notification notification = stateMon.getNotification();
				if (notification != null) {
					if (DashboardConfig.YES_INDICATOR.equalsIgnoreCase(notification.getEmail()) || 
							DashboardConfig.YES_INDICATOR.equalsIgnoreCase(notification.getPage())) {
						String message = "The following issue with a state is occurring at " + result.getCareCenterName() + " ("
				        	+ result.getCareCenterDescription() + "):\n\n" + stateMon.getName() + ": " + "taking more than "
				        	+ stateMon.getElapsedTime() + " " + stateMon.getElapsedTimeUnit() + "(s) over the past " 
				        	+ stateMon.getTimePeriod() + " " + stateMon.getTimePeriodUnit() 
				        	+ "(s).\n\nRegards,\nCHICA Operations Dashboard";
						if (canSendMessage(message, notification)) {
							if (DashboardConfig.YES_INDICATOR.equalsIgnoreCase(notification.getEmail())) {
								sendMail(message, notification.getEmailAddress(), location, locationDescription);
							}
							if (DashboardConfig.YES_INDICATOR.equalsIgnoreCase(notification.getPage())) {
								sendPage(message, notification.getPageNumber());
							}
						}
					}
				}
			}
			
			// Scan latency
			for (ScanProblem problem : result.getScanProblems()) {
				ScanCheck check = problem.getScanCheck();
				Notification notification = check.getNotification();
				if (notification != null) {
					if (DashboardConfig.YES_INDICATOR.equalsIgnoreCase(notification.getEmail()) || 
							DashboardConfig.YES_INDICATOR.equalsIgnoreCase(notification.getPage())) {
						String message = "The following issue with a form is occurring at " + result.getCareCenterName() 
							+ " (" + result.getCareCenterDescription() + "):\n\n" + check.getFormName() + ": " 
							+ "there have been no successful scans for this form in the last " + check.getTimePeriod() 
							+ " " + check.getTimePeriodUnit() + "(s).\n\nRegards,\nCHICA Operations Dashboard";
						if (canSendMessage(message, notification)) {
							if (DashboardConfig.YES_INDICATOR.equalsIgnoreCase(notification.getEmail())) {
								sendMail(message, notification.getEmailAddress(), location, locationDescription);
							}
							if (DashboardConfig.YES_INDICATOR.equalsIgnoreCase(notification.getPage())) {
								sendPage(message, notification.getPageNumber());
							}
						}
					}
				}
			}
			
			// DWE CHICA-367 Send email for manual check-ins
			// CareCenterResult object will only have a ManualCheckinNumResult object 
			// if the number of manual check-ins is greater than the threshold
			ManualCheckinNumResult manualCheckinNumResult = result.getManualCheckinNumResult();
			if(manualCheckinNumResult != null)
			{
				ManualCheckinChecks checks = manualCheckinNumResult.getManualCheckinChecks();
				if(checks != null)
				{
					Notification notification = checks.getNotification();
					if (notification != null) {
						if(DashboardConfig.YES_INDICATOR.equalsIgnoreCase(notification.getEmail()) || DashboardConfig.YES_INDICATOR.equalsIgnoreCase(notification.getPage())) {
							StringBuilder builder = new StringBuilder();
							builder.append(manualCheckinNumResult.getMessage())
							.append(" at ")
							.append(manualCheckinNumResult.getLocation().getName())
							.append(". \n")
							.append("\n\nRegards,\nCHICA Operations Dashboard");
							if(canSendMessage(builder.toString(), notification)) 
							{
								if(DashboardConfig.YES_INDICATOR.equalsIgnoreCase(notification.getEmail()))
								{
									sendMail(builder.toString(), notification.getEmailAddress(), manualCheckinNumResult.getLocation().getName(), manualCheckinNumResult.getLocation().getDescription());
								}
								if(DashboardConfig.YES_INDICATOR.equalsIgnoreCase(notification.getPage()))
								{
									sendPage(builder.toString(), notification.getPageNumber());
								}
							}
						}
					}
				}	
			}
			// Send email for wifi issues
            // CareCenterResult object will only have a WifiIssueNumResult object 
            // if the number of wifi issues is greater than the threshold
            WifiIssueNumResult wifiIssueNumResult = result.getWifiIssueNumResult();
            if(wifiIssueNumResult != null)
            {
                WifiIssueChecks checks = wifiIssueNumResult.getWifiIssueChecks();
                if(checks != null)
                {
                    Notification notification = checks.getNotification();
                    if (notification != null) {
                        if(DashboardConfig.YES_INDICATOR.equalsIgnoreCase(notification.getEmail()) || DashboardConfig.YES_INDICATOR.equalsIgnoreCase(notification.getPage())) {
                            StringBuilder builder = new StringBuilder();
                            builder.append(wifiIssueNumResult.getMessage())
                            .append(" at ")
                            .append(wifiIssueNumResult.getLocation().getName())
                            .append(". \n")
                            .append("\n\nRegards,\nCHICA Operations Dashboard");
                            if(canSendMessage(builder.toString(), notification)) 
                            {
                                if(DashboardConfig.YES_INDICATOR.equalsIgnoreCase(notification.getEmail()))
                                {
                                    sendMail(builder.toString(), notification.getEmailAddress(), wifiIssueNumResult.getLocation().getName(), wifiIssueNumResult.getLocation().getDescription());
                                }
                                if(DashboardConfig.YES_INDICATOR.equalsIgnoreCase(notification.getPage()))
                                {
                                    sendPage(builder.toString(), notification.getPageNumber());
                                }
                            }
                        }
                    }
                }   
            }
		}
		
		reconcileMessageMap();
	}
	
	/**
	 * Sends emails/pages for issues with the server checks.
	 * 
	 * @param serverResult ServerCheckResult object containing the results from the server checks.
	 */
	public void sendEmailsOrPages(ServerCheckResult serverResult) {
		
		if (serverResult == null)
			return;
		// Memory problems
		ArrayList<MemoryProblem> memProblems = serverResult.getMemProblems();
		for (MemoryProblem memProblem : memProblems) {
			MemoryCheck memCheck = memProblem.getMemCheck();
			Notification notification = memCheck.getNotification();
			if (notification != null) {
				String message = "The following memory problem is occurring on the server:\n\n"
					+ memProblem.getPercentageUsed() + "% of the " + memProblem.getMemType()
				        + " memory is being used.\n\nRegards,\nCHICA Operations Dashboard";
				if (canSendMessage(message, notification)) {
					if (DashboardConfig.YES_INDICATOR.equalsIgnoreCase(notification.getEmail())) {
						sendMail(message, notification.getEmailAddress(), null, null);
					}
					if (DashboardConfig.YES_INDICATOR.equalsIgnoreCase(notification.getPage())) {
						sendPage(message, notification.getPageNumber());
					}
				}
			}
		}
		
		// Image/Scan directory problems
		ArrayList<DirectoryProblem> imageProbs = serverResult.getImageDirectoryProblems();
		ArrayList<DirectoryProblem> scanProbs = serverResult.getScanDirectoryProblems();
		for (DirectoryProblem imageProb : imageProbs) {
			DirectoryCheck dirCheck = imageProb.getDirectoryCheck();
			Notification notification = dirCheck.getNotification();
			if (notification != null) {
				String message = "The following directory/file problems are occurring on the server:\n\nThe following files exist in the " 
					+ imageProb.getDirectoryCheck().getImageDir() + " directory but not in the " 
					+ imageProb.getDirectoryCheck().getScanDir() + " directory:\n";
				for (String file : imageProb.getFileNames()) {
					message += "\n" + file;
				}
				
				message += "\n\nRegards,\nCHICA Operations Dashboard";
				if (canSendMessage(message, notification)) {
					if (DashboardConfig.YES_INDICATOR.equalsIgnoreCase(notification.getEmail())) {
						sendMail(message, notification.getEmailAddress(), null, null);
					}
					if (DashboardConfig.YES_INDICATOR.equalsIgnoreCase(notification.getPage())) {
						sendPage(message, notification.getPageNumber());
					}
				}
			}
		}
		
		for (DirectoryProblem scanProb : scanProbs) {
			DirectoryCheck dirCheck = scanProb.getDirectoryCheck();
			Notification notification = dirCheck.getNotification();
			if (notification != null) {
				String message = "The following directory/file problems are occurring on the server:\n\nThe following "
						+ "files exist in the " + scanProb.getDirectoryCheck().getScanDir() + " directory but not in the " 
						+ scanProb.getDirectoryCheck().getImageDir() + " directory:\n";
				for (String file : scanProb.getFileNames()) {
					message += "\n" + file;
				}
				
				message += "\n\nRegards,\nCHICA Operations Dashboard";
				if (canSendMessage(message, notification)) {
					if (DashboardConfig.YES_INDICATOR.equalsIgnoreCase(notification.getEmail())) {
						sendMail(message, notification.getEmailAddress(), null, null);
					}
					if (DashboardConfig.YES_INDICATOR.equalsIgnoreCase(notification.getPage())) {
						sendPage(message, notification.getPageNumber());
					}
				}
			}
		}
	}
	
	/**
	 * Sends emails/pages for the Rule Check Results.
	 * 
	 * @param ruleResult RuleCheckResult object containing the results of the rule checks.
	 */
	public void sendEmailsOrPages(RuleCheckResult ruleResult) {
		if (ruleResult == null)
			return;
		RuleChecks ruleChecks = ruleResult.getRuleChecks();
		if (ruleChecks != null) {
			NeverFiredRuleCheck nfRuleCheck = ruleChecks.getNeverFiredCheck();
			if (nfRuleCheck != null && nfRuleCheck.getNotification() != null) {
				Notification notification = nfRuleCheck.getNotification();
				String message = "The following rules have never fired:\n";
				boolean found = false;
				for (RuleIdentifier ruleIdentifier : ruleResult.getNeverFiredRules()) {
					message += "\n" + ruleIdentifier.getTokenName() + " (" + ruleIdentifier.getRuleType() + ")";
					found = true;
				}
				message += "\n\nRegards,\nCHICA Operations Dashboard";
				if (found && canSendMessage(message, notification)) {
					if (DashboardConfig.YES_INDICATOR.equalsIgnoreCase(notification.getEmail())) {
						sendMail(message, notification.getEmailAddress(), null, null);
					}
					if (DashboardConfig.YES_INDICATOR.equalsIgnoreCase(notification.getPage())) {
						sendPage(message, notification.getPageNumber());
					}
				}
			}
			
			UnFiredRuleCheck ufRuleCheck = ruleChecks.getUnFiredCheck();
			if (ufRuleCheck != null && ufRuleCheck.getNotification() != null) {
				Notification notification = ufRuleCheck.getNotification();
				String message = "The following rules have not fired in the past " + ufRuleCheck.getTimePeriod() + " "
				        + ufRuleCheck.getTimePeriodUnit() + "(s):\n";
				boolean found = false;
				for (RuleIdentifier ruleIdentifier : ruleResult.getUnFiredRules()) {
					message += "\n" + ruleIdentifier.getTokenName() + " (" + ruleIdentifier.getRuleType() + ")";
					found = true;
				}
				message += "\n\nRegards,\nCHICA Operations Dashboard";
				if (found && canSendMessage(message, notification)) {
					if (DashboardConfig.YES_INDICATOR.equalsIgnoreCase(notification.getEmail())) {
						sendMail(message, notification.getEmailAddress(), null, null);
					}
					if (DashboardConfig.YES_INDICATOR.equalsIgnoreCase(notification.getPage())) {
						sendPage(message, notification.getPageNumber());
					}
				}
			}
		}
	}
	
	
	private void sendMail(String message, String dashboardEmail, String location, 
	                      String locationDescription) {
		if (this.mailProps.get("mail.smtp.host") == null) {
		    this.log.error("Dashboard: SMTP host not specified.  Please specify the global property chirdlutil.smtpMailHost");
			return;
		}
		
		String[] emailList = new String[0];
		if (dashboardEmail != null) {
			StringTokenizer tokenizer = new StringTokenizer(dashboardEmail, ",", false);
			emailList = new String[tokenizer.countTokens()];
			int i = 0;
			while (tokenizer.hasMoreTokens()) {
				emailList[i++] = tokenizer.nextToken().trim();
			}
		}
		
		if (emailList.length == 0) {
		    this.log.error("Dashboard: Email list is empty.  Please specify email recipients in the Dashboard configuration file");
			return;
		}
		
		String subject = this.MAIL_SUBJECT;
		if (location != null) {
			subject += " for " + location;
			if (locationDescription != null) {
				subject += " (" + locationDescription + ")";
			}
		}
		
		this.mailSender.sendMail(this.MAIL_SENDER, emailList, subject, message);
	}
	
	/**
	 * Sends the provided message to the chicaops pager.
	 * 
	 * @param message The message to be sent to the pager.
	 * @return The response from the paging system.
	 */
	private String sendPage(String message, String pagerNumber) {
		
		if (pagerNumber == null) {
			return null;
		}
		
		if (this.baseUrl == null) {
		    this.log.error("Dashboard: Pager base URL is null.  Please specify global property chica.pagerBaseURL.");
			return null;
		}
		
		if (this.idParam == null) {
		    this.log.error("Dashboard: Pager ID param is null.  Please specify global property chica.pagerUrlNumberParam");
			return null;
		}
		
		if (this.textParam == null) {
		    this.log.error("Dashboard: Pager text param is null.  Please specify global property chica.pagerUrlMessageParam");
			return null;
		}
		
		message = message.replaceAll("\n", " ");
		if (message.length() > 160) {
			message = message.substring(0, 159);
		}
		
		String urlStr = this.baseUrl;
		BufferedReader rd = null;
		String response = null;
		
		try {
			
			urlStr += "?" + this.idParam + "=" + URLEncoder.encode(pagerNumber, "UTF-8") + "&" + textParam + "="
			        + URLEncoder.encode(message, "UTF-8");
			
			if (this.baseUrl == null || this.baseUrl.length() == 0 || pagerNumber == null || pagerNumber.length() == 0
			        || message == null || message.length() == 0 || this.idParam == null || this.idParam.length() == 0
			        || this.textParam == null || this.textParam.length() == 0) {
				this.log.warn("Page was not sent due to null url string or null parameters. " + urlStr);
				
				return null;
			}
			
			URL url = new URL(urlStr);
			URLConnection conn = url.openConnection();
			conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.2; .NET CLR 1.0.3705;)");
			rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			StringBuffer sb = new StringBuffer();
			String line;
			while ((line = rd.readLine()) != null) {
				sb.append(line);
			}
			response = sb.toString();
			
		}
		catch (Exception e) {
			this.log.error("Could not send page: " + e.getMessage());
			this.log.error(org.openmrs.module.chirdlutil.util.Util.getStackTrace(e));
		}
		finally {
			if (rd != null)
				try {
					rd.close();
				}
				catch (Exception e) {
					this.log.error("Error closing reader", e);
				}
		}
		return response;
		
	}
	
	/**
	 * Checks to see if the message has been previously sent within the specified threshold time.
	 * 
	 * @param message The message that is possibly going to be sent.
	 * @param notification object used to determine if notifications should be sent on the weekend
	 * @return true if the message can be sent, false otherwise.
	 */
	private boolean canSendMessage(String message, Notification notification) {
		long currTime = System.currentTimeMillis();
		String sendWeekendNotifications = notification.getWeekend();
		if (sendWeekendNotifications!=null&&
				sendWeekendNotifications.equalsIgnoreCase(DashboardConfig.NO_INDICATOR)) {
			/* weekend time */
			Calendar calendar = Calendar.getInstance();
			int day = calendar.get(Calendar.DAY_OF_WEEK);
			if (Calendar.SUNDAY == day || Calendar.SATURDAY == day) {
				return false;
			}
		}
		int messageHash = message.hashCode();
		Long time = messageToTimeMap.get(messageHash);
		if (time == null) {
			messageToTimeMap.put(messageHash, System.currentTimeMillis());
			return true;
		}

		if ((currTime - time) > this.thresholdTime) {
			messageToTimeMap.put(messageHash, currTime);
			return true;
		}
		
		return false;
	}
	
	/**
	 * Traces through the message map to determine what messages have last been sent over the
	 * threshold mark. If they have, they will be removed from the map so they will be allowed to
	 * be sent again.
	 */
	private void reconcileMessageMap() {
		synchronized (messageToTimeMap) {
			long currTime = System.currentTimeMillis();
			ArrayList<Integer> removeKeys = new ArrayList<Integer>();
			Set<Entry<Integer, Long>> entries = messageToTimeMap.entrySet();
			Iterator<Entry<Integer, Long>> iter = entries.iterator();
			while (iter.hasNext()) {
				Entry<Integer, Long> entry = iter.next();
				Integer messageHash = entry.getKey();
				Long time = entry.getValue();
				if ((currTime - time) > this.thresholdTime) {
					removeKeys.add(messageHash);
				}
			}
			
			for (Integer key : removeKeys) {
				messageToTimeMap.remove(key);
			}
		}
	}
}
