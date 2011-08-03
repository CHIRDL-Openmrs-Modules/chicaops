package org.openmrs.module.chicaops.xmlBeans.dashboard;

/**
 * Bean representing the Dashboard configuration file.
 * 
 *	The possible unit values for the time measurements below are as follows:
 *		second
 *		minute
 *		hour
 *		day
 *		week
 *		quarter
 *		year
 *
 *	The possible values for severity are as follows:
 *		error
 *		warning
 *  <br /><br />
 *	Sample config XML:
 *  <pre>
 *  	{@code
 *			<dashboardConfig>
 *				<statesToMonitor>
 *					<stateToMonitor name="PSF_printed">
 *						<elapsedTime unit="second">30</elapsedTime>
 *						<timePeriod unit="hour">8</timePeriod>
 *						<numErrors>1</numErrors>
 *						<severity>error</severity>
 *						<fixTips> <!-- fix tips are optional -->
 *							<tip>Is there power to the printer?</tip>
 *							.........
 *						</fixTips>
 *						<notification>
 *							<email>Y</email>
 *							<emailAddress>bob@iupui.edu</emailAddress>
 *							<page>N</page>
 *							<pageNumber>5555555</pageNumber>
 *						</notification>
 *					</stateToMonitor>
 *					.........
 *				</statesToMonitor>
 *				<hl7ExportChecks>
 *					<check>ACK_not_received</check>
 *					.........
 *					<timePeriod unit="day">8</timePeriod>
 *					<numErrors>1</numErrors>
 *					<severity>error</severity>
 *					<fixTips> <!-- fix tips are optional -->
 *							<tip>Tip 1.</tip>
 *							..........
 *					</fixTips>
 *					<notification>
 *						<email>Y</email>
 *						<emailAddress>bob@iupui.edu</emailAddress>
 *						<page>N</page>
 *						<pageNumber>5555555</pageNumber>
 *					</notification>
 *				</hl7ExportChecks>
 *				<forcedOutPWSCheck>
 *					<timePeriod unit="day">6</timePeriod>
 *					<severity>error</severity>
 *					<fixTips> <!-- fix tips are optional -->
 *							<tip>Tip 1.</tip>
 *							..........
 *					</fixTips>
 *					<notification>
 *						<email>Y</email>
 *						<emailAddress>bob@iupui.edu</emailAddress>
 *						<page>N</page>
 *						<pageNumber>5555555</pageNumber>
 *					</notification>
 *				</forcedOutPWSCheck>
 *				<scanChecks>
 *		 			<scanCheck>
 *		 				<formName>PSF</formName>
 *		 				<timePeriod unit="day">3</timePeriod>	
 *		 				<severity>error</severity>
 *		 				<fixTips> //fix tips are optional
 *		 					<tip>tip 1.</tip>
 *		 					...........
 *		 				</fixTips>
 *		 				<notification>
 *		 					<email>Y</email>
 *		 					<emailAddress>bob@iupui.edu</emailAddress>
 *		 					<page>N</page>
 *		 					<pageNumber>5555555</pageNumber>
 *		 				</notification>
 *		 			</scanCheck>
 *		 		</scanChecks>
 *				<serverChecks>
 *					<memoryCheck>
 *						<percentileToNotify>1</percentileToNotify>
 *						<severity>warning</severity>
 *						<fixTips> <!-- fix tips are optional -->
 *							<tip>Tip 1.</tip>
 *							..........
 *						</fixTips>
 *						<notification>
 *							<email>Y</email>
 *							<emailAddress>bob@iupui.edu</emailAddress>
 *							<page>N</page>
 *							<pageNumber>5555555</pageNumber>
 *						</notification>
 *					</memoryCheck>
 *					.........
 *					<directoryCheck>
 *						<imageDir>C:\chica\images\BBPS\PSF</imageDir>
 *						<scanDir>C:\chica\scan\BBPS\PSF_SCAN</scanDir>
 *						<timePeriod unit="day">1</timePeriod>
 *						<numErrors>1</numErrors>
 *						<severity>error</severity>
 *						<fixTips> <!-- fix tips are optional -->
 *							<tip>Tip 1.</tip>
 *							..........
 *						</fixTips>
 *						<notification>
 *							<email>Y</email>
 *							<emailAddress>bob@iupui.edu</emailAddress>
 *							<page>N</page>
 *							<pageNumber>5555555</pageNumber>
 *						</notification>
 *					</directoryCheck>
 *					.........
 *				</serverChecks>
 *				<ruleChecks>
 *					<neverFiredCheck>
 *						<severity>warning</severity>
 *						<fixTips> <!-- fix tips are optional -->
 *							<tip>Check 1</tip>
 *							.........
 *						</fixTips>
 *						<notification>
 *							<email>Y</email>
 *							<emailAddress>bob@iupui.edu</emailAddress>
 *							<page>N</page>
 *							<pageNumber>5555555</pageNumber>
 *						</notification>
 *					</neverFiredCheck>
 *					<unFiredCheck>
 *						<timePeriod unit="month">6</timePeriod>
 *						<severity>warning</severity>
 *						<fixTips> <!-- fix tips are optional -->
 *							<tip>Check 1</tip>
 *							.........
 *						</fixTips>
 *						<notification>
 *							<email>Y</email>
 *							<emailAddress>bob@iupui.edu</emailAddress>
 *							<page>N</page>
 *							<pageNumber>5555555</pageNumber>
 *						</notification>
 *					</unFiredCheck>
 *				</ruleChecks>
 *			</dashboardConfig>
 *  	}
 *  </pre>
 * 
 * @author Steve McKee
 */
public class DashboardConfig {
	
	public static final String SEVERITY_WARNING = "warning";
	public static final String SEVERITY_ERROR = "error";
	public static final String CHECKIN_STATE = "CHECKIN";
	public static final String SECOND = "second";
	public static final String MINUTE = "minute";
	public static final String HOUR = "hour";
	public static final String DAY = "day";
	public static final String WEEK = "week";
	public static final String QUARTER = "quarter";
	public static final String YEAR = "year";
	public static final String YES_INDICATOR = "Y";
	public static final String NO_INDICATOR = "N";
	
	private StatesToMonitor statesToMonitor;
	private ServerChecks serverChecks;
	private ForcedOutPWSCheck forcedOutPWSCheck;
	private HL7ExportChecks hl7ExportChecks;
	private ScanChecks scanChecks;
	private RuleChecks ruleChecks;

	/**
	 * Default Constructor
	 */
	public DashboardConfig() {
	}
	
	/**
	 * Constructor
	 * 
	 * @param statesToMonitor The states to be monitored by the dashboard.
	 * @param serverChecks The server checks to be performed by the dashboard.
	 * @param forcedOutPWSCheck Specific check to see if PWSs are being forced out.
	 * @param hl7ExportChecks The possible states to alert if they exist in the chica_hl7_export table.
	 * @param ruleChecks Checks to be performed against the rules.
	 */
	public DashboardConfig(StatesToMonitor statesToMonitor, 
	                       ServerChecks serverChecks, 
	                       ForcedOutPWSCheck forcedOutPWSCheck,
	                       HL7ExportChecks hl7ExportChecks,
	                       ScanChecks scanChecks,
	                       RuleChecks ruleChecks) {
		this.statesToMonitor = statesToMonitor;
		this.serverChecks = serverChecks;
		this.forcedOutPWSCheck = forcedOutPWSCheck;
		this.hl7ExportChecks = hl7ExportChecks;
		this.scanChecks = scanChecks;
		this.ruleChecks = ruleChecks;
	}
	
    /**
     * @return the statesToMonitor
     */
    public StatesToMonitor getStatesToMonitor() {
    	return statesToMonitor;
    }
    
    /**
     * @param statesToMonitor the statesToMonitor to set
     */
    public void setStatesToMonitor(StatesToMonitor statesToMonitor) {
    	this.statesToMonitor = statesToMonitor;
    }
    
    /**
     * @return the serverChecks
     */
    public ServerChecks getServerChecks() {
    	return serverChecks;
    }
	
    /**
     * @param serverChecks the serverChecks to set
     */
    public void setServerChecks(ServerChecks serverChecks) {
    	this.serverChecks = serverChecks;
    }

    /**
     * @return the forcedOutPWSCheck
     */
    public ForcedOutPWSCheck getForcedOutPWSCheck() {
    	return forcedOutPWSCheck;
    }
	
    /**
     * @param forcedOutPWSCheck the forcedOutPWSCheck to set
     */
    public void setForcedOutPWSCheck(ForcedOutPWSCheck forcedOutPWSCheck) {
    	this.forcedOutPWSCheck = forcedOutPWSCheck;
    }

    /**
     * @return the hl7ExportChecks
     */
    public HL7ExportChecks getHl7ExportChecks() {
    	return hl7ExportChecks;
    }
	
    /**
     * @param hl7ExportChecks the hl7ExportChecks to set
     */
    public void setHl7ExportChecks(HL7ExportChecks hl7ExportChecks) {
    	this.hl7ExportChecks = hl7ExportChecks;
    }
    
    /**
     * @return the scanChecks
     */
    public ScanChecks getScanChecks() {
    	return scanChecks;
    }
	
    /**
     * @param scanChecks the scanChecks to set
     */
    public void setScanChecks(ScanChecks scanChecks) {
    	this.scanChecks = scanChecks;
    }
	
    /**
     * @return the ruleChecks
     */
    public RuleChecks getRuleChecks() {
    	return ruleChecks;
    }
	
    /**
     * @param ruleChecks the ruleChecks to set
     */
    public void setRuleChecks(RuleChecks ruleChecks) {
    	this.ruleChecks = ruleChecks;
    }
}
