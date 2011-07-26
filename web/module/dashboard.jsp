<%@ include file="/WEB-INF/template/include.jsp"%>

<%@ include file="/WEB-INF/template/header.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
   "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<openmrs:require allPrivileges="View Encounters, View Patients, View Concept Classes" otherwise="/login.htm" redirect="/module/chicaops/dashboard.form" />

<html>
<head>
<title><c:out value="${appName} Operations Dashboard"/></title>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<META HTTP-EQUIV="Refresh"  CONTENT="${refreshPeriod}">

<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/moduleResources/chicaops/chicaops.css"/>
<script language="JavaScript" src="${pageContext.request.contextPath}/moduleResources/chicaops/chicaops.js"></script>
</head>     
<body>
<br/>
<center><h2 style="color:#7d98b3"><c:out value="Welcome to the ${appName} Operations Dashboard"/></h2></center>
 <table class="pretty-table" align="center">
	<c:forEach items="${careCenters}" var="center">
      <tr>
        <c:choose>
            <c:when test="${center.hasErrors == 'true'}">
	            <th scope="row"><img src="/openmrs/images/error.gif"/></th><th scope="col" colspan="3"><c:out value="${center.careCenterName}"/> (<c:out value="${center.careCenterDescription}"/>)</th>
	        </c:when>
	        <c:when test="${center.hasWarnings == 'true'}">
                <th scope="row"]]><img src="/openmrs/images/alert.gif"/></th><th scope="col" colspan="3"><c:out value="${center.careCenterName}"/> (<c:out value="${center.careCenterDescription}"/>)</th>
            </c:when>
            <c:otherwise>
                <th scope="row"><img src="/openmrs/images/play.gif"/></th><th scope="col" colspan="3"><c:out value="${center.careCenterName}"/> (<c:out value="${center.careCenterDescription}"/>)</th>
            </c:otherwise>
        </c:choose>
      </tr>	
      <c:forEach items="${center.stateResults}" var="monitor" varStatus="status">
        <tr>
            <c:choose>
                <c:when test="${monitor.stateToMonitor.severity == 'error'}">
                    <td></td><td></td>
                    <td>
                        <img src="/openmrs/images/error.gif"/>
                        <c:if test="${monitor.stateToMonitor.notification.page == 'Y'}">
                            <img src="${pageContext.request.contextPath}/moduleResources/chicaops/pager.jpg"/>
                        </c:if>
                        <c:if test="${monitor.stateToMonitor.notification.email == 'Y'}">
                            <img src="${pageContext.request.contextPath}/moduleResources/chicaops/email.jpg"/>
                        </c:if>
                    </td>
                </c:when>
                <c:when test="${monitor.stateToMonitor.severity == 'warning'}">
                    <td></td><td></td>
                    <td>
                        <img src="/openmrs/images/alert.gif"/>
                        <c:if test="${monitor.stateToMonitor.notification.page == 'Y'}">
                            <img src="${pageContext.request.contextPath}/moduleResources/chicaops/pager.jpg"/>
                        </c:if>
                        <c:if test="${monitor.stateToMonitor.notification.email == 'Y'}">
                            <img src="${pageContext.request.contextPath}/moduleResources/chicaops/email.jpg"/>
                        </c:if>
                    </td>
                </c:when>
                <c:otherwise>
                    <td></td><td></td><td></td>
                </c:otherwise>
            </c:choose>
            <td>
                <c:out value="${monitor.stateToMonitor.name}"/> 
                <c:if test="${fn:length(monitor.stateToMonitor.fixTips.tips) > 0}">
                    <c:forEach items="${monitor.stateToMonitor.fixTips.tips}" var="tip">
                        <c:set var="tiphtml" value="${tiphtml}<li>${tip}</li>"/>
                    </c:forEach>
                    <span class="hotspot" onmouseover="tooltip.show('<ol>${tiphtml}</ol>');" onmouseout="tooltip.hide();"><i>(<u>tips</u>)</i></span>
                    <c:set var="tiphtml" value=""/>
                </c:if> 
            </td>            
        </tr>
        <tr>    
            <td></td><td></td><td></td>
            <td>        
		        <div>
		          <c:choose>
		              <c:when test="${monitor.stateToMonitor.name == 'CHECKIN'}">
		                  There have been no checkins in the last <c:out value="${monitor.stateToMonitor.elapsedTime}"/> 
		                  <c:out value="${monitor.stateToMonitor.elapsedTimeUnit}"/>(s) at this clinic.
		              </c:when>
		              <c:otherwise>
				            There have been <c:out value="${monitor.numOccurrences}"/> occurrences of items in this state 
				            taking over <c:out value="${monitor.stateToMonitor.elapsedTime}"/> <c:out value="${monitor.stateToMonitor.elapsedTimeUnit}"/>(s) 
				            to complete in the last <c:out value="${monitor.stateToMonitor.timePeriod}"/> 
				            <c:out value="${monitor.stateToMonitor.timePeriodUnit}"/> period.<br />
		              </c:otherwise>
		          </c:choose>
		        </div>
	        </td>
        </tr>            
      </c:forEach>
      <c:if test="${!empty (center.forcedOutPWSs)}">
        <tr>
            <c:choose>
                <c:when test="${center.forcedOutPWSs[0].forcedOutPWSCheck.severity == 'error'}">
                    <td></td><td></td>
                    <td>
                        <img src="/openmrs/images/error.gif"/>
                        <c:if test="${center.forcedOutPWSs[0].forcedOutPWSCheck.notification.page == 'Y'}">
                            <img src="${pageContext.request.contextPath}/moduleResources/chicaops/pager.jpg"/>
                        </c:if>
                        <c:if test="${center.forcedOutPWSs[0].forcedOutPWSCheck.notification.email == 'Y'}">
                            <img src="${pageContext.request.contextPath}/moduleResources/chicaops/email.jpg"/>
                        </c:if>
                    </td>
                </c:when>
                <c:when test="${center.forcedOutPWSs[0].forcedOutPWSCheck.severity == 'warning'}">
                    <td></td><td></td>
                    <td>
                        <img src="/openmrs/images/alert.gif"/>
                        <c:if test="${center.forcedOutPWSs[0].forcedOutPWSCheck.notification.page == 'Y'}">
                            <img src="${pageContext.request.contextPath}/moduleResources/chicaops/pager.jpg"/>
                        </c:if>
                        <c:if test="${center.forcedOutPWSs[0].forcedOutPWSCheck.notification.email == 'Y'}">
                            <img src="${pageContext.request.contextPath}/moduleResources/chicaops/email.jpg"/>
                        </c:if>
                    </td>
                </c:when>
                <c:otherwise>
                    <td></td><td></td><td></td>
                </c:otherwise>
            </c:choose>
            <td>Forced Out PWSs
                <c:if test="${fn:length(center.forcedOutPWSs[0].forcedOutPWSCheck.fixTips.tips) > 0}">
                    <c:forEach items="${center.forcedOutPWSs[0].forcedOutPWSCheck.fixTips.tips}" var="tip">
                        <c:set var="foTip" value="${foTip}<li>${tip}</li>"/>
                    </c:forEach>
                    <span class="hotspot" onmouseover="tooltip.show('<ol>${foTip}</ol>');" onmouseout="tooltip.hide();"><i>(<u>tips</u>)</i></span>
                    <c:set var="foTip" value=""/>
                </c:if>
            </td>
        </tr>
        <tr>
            <td></td><td></td><td></td>
            <td>
                There have been <c:out value="${fn:length(center.forcedOutPWSs)}"/> PWSs forced out in the last 
                <c:out value="${center.forcedOutPWSs[0].forcedOutPWSCheck.timePeriod}"/> 
                <c:out value="${center.forcedOutPWSs[0].forcedOutPWSCheck.timePeriodUnit}"/> period.
            </td>
        </tr>
      </c:if>
      <c:if test="${!empty (center.hl7ExportProblems)}">
        <tr>
            <c:choose>
                <c:when test="${center.hl7ExportChecks.severity == 'error'}">
                    <td></td><td></td>
                    <td>
                        <img src="/openmrs/images/error.gif"/>
                        <c:if test="${center.hl7ExportChecks.notification.page == 'Y'}">
                            <img src="${pageContext.request.contextPath}/moduleResources/chicaops/pager.jpg"/>
                        </c:if>
                        <c:if test="${center.hl7ExportChecks.notification.email == 'Y'}">
                            <img src="${pageContext.request.contextPath}/moduleResources/chicaops/email.jpg"/>
                        </c:if>
                    </td>
                </c:when>
                <c:when test="${center.hl7ExportChecks.severity == 'warning'}">
                    <td></td><td></td>
                    <td>
                        <img src="/openmrs/images/alert.gif"/>
                        <c:if test="${center.hl7ExportChecks.notification.page == 'Y'}">
                            <img src="${pageContext.request.contextPath}/moduleResources/chicaops/pager.jpg"/>
                        </c:if>
                        <c:if test="${center.hl7ExportChecks.notification.email == 'Y'}">
                            <img src="${pageContext.request.contextPath}/moduleResources/chicaops/email.jpg"/>
                        </c:if>
                    </td>
                </c:when>
                <c:otherwise>
                    <td></td><td></td><td></td>
                </c:otherwise>
            </c:choose>
            <td>HL7 Export Issues
	            <c:if test="${fn:length(center.hl7ExportChecks.fixTips.tips) > 0}">
	                <c:forEach items="${center.hl7ExportChecks.fixTips.tips}" var="tip">
	                    <c:set var="hl7Tip" value="${hl7Tip}<li>${tip}</li>"/>
	                </c:forEach>
	                <span class="hotspot" onmouseover="tooltip.show('<ol>${hl7Tip}</ol>');" onmouseout="tooltip.hide();"><i>(<u>tips</u>)</i></span>
	                <c:set var="hl7Tip" value=""/>
	            </c:if>
            </td>
        </tr>
        <tr>
            <td></td><td></td><td></td>
            <td>
                There have been some issues with the HL7 Exporter over the last <c:out value="${center.hl7ExportChecks.timePeriod}"/> 
                <c:out value="${center.hl7ExportChecks.timePeriodUnit}"/>(s):
            </td>
        </tr>
        <c:forEach items="${center.hl7ExportProblems}" var="exportIssue">
            <tr>
                <td></td><td></td><td></td>
	            <td>
	                <c:out value="${exportIssue.key}"/>: 
	                <c:out value="${exportIssue.value}"/> occurrences
	            </td>
            </tr>
        </c:forEach>
      </c:if>
  </c:forEach>
  <tr>
    <c:choose>
        <c:when test="${serverResult.hasErrors == 'true'}">
	        <th scope="row"><img src="/openmrs/images/error.gif"/></th><th scope="col" colspan="3"><c:out value="Server"/></th>
	    </c:when>
	    <c:when test="${serverResult.hasWarnings == 'true'}">
	        <th scope="row"]]><img src="/openmrs/images/alert.gif"/></th><th scope="col" colspan="3"><c:out value="Server"/></th>
	    </c:when>
	    <c:otherwise>
	        <th scope="row"><img src="/openmrs/images/play.gif"/></th><th scope="col" colspan="3"><c:out value="Server"/></th>
	    </c:otherwise>
    </c:choose>
  </tr>
  <c:forEach items="${serverResult.memProblems}" var="memProblem">
    <tr>
        <c:choose>
            <c:when test="${memProblem.memCheck.severity == 'error'}">
	            <td></td><td></td>
	            <td>
	               <img src="/openmrs/images/error.gif"/>
	               <c:if test="${memProblem.memCheck.notification.page == 'Y'}">
                       <img src="${pageContext.request.contextPath}/moduleResources/chicaops/pager.jpg"/>
                   </c:if>
                   <c:if test="${memProblem.memCheck.notification.email == 'Y'}">
                       <img src="${pageContext.request.contextPath}/moduleResources/chicaops/email.jpg"/>
                   </c:if>
	            </td>
	        </c:when>
	        <c:when test="${memProblem.memCheck.severity == 'warning'}">
	            <td></td><td></td>
	            <td>
	               <img src="/openmrs/images/alert.gif"/>
	               <c:if test="${memProblem.memCheck.notification.page == 'Y'}">
                       <img src="${pageContext.request.contextPath}/moduleResources/chicaops/pager.jpg"/>
                   </c:if>
                   <c:if test="${memProblem.memCheck.notification.email == 'Y'}">
                       <img src="${pageContext.request.contextPath}/moduleResources/chicaops/email.jpg"/>
                   </c:if>
	            </td>
	        </c:when>
	        <c:otherwise>
	            <td></td><td></td><td></td>
	        </c:otherwise>
        </c:choose>
        <td>
            Memory Issue 
            <c:if test="${fn:length(memProblem.memCheck.fixTips.tips) > 0}">
                    <c:forEach items="${memProblem.memCheck.fixTips.tips}" var="tip">
                        <c:set var="memTip" value="${memTip}<li>${tip}</li>"/>
                    </c:forEach>
                    <span class="hotspot" onmouseover="tooltip.show('<ol>${memTip}</ol>');" onmouseout="tooltip.hide();"><i>(<u>tips</u>)</i></span>
                    <c:set var="memTip" value=""/>
            </c:if>
            <i>(</i><a href="JavaScript:newPopup('${pageContext.request.contextPath}/module/chirdlutil/memoryLeakMonitor.form');"><i><u>memory leak monitor</u></i></a><i>)</i>
        </td>
    </tr>
    <tr>
        <td></td><td></td><td></td>
        <td>        
            <div>
                The VM is currently using <c:out value="${memProblem.percentageUsed}"/>% of its allocated <c:out value="${memProblem.memType}"/> memory.<br />
            </div>
        </td>
    </tr>
  </c:forEach>
  <c:forEach items="${serverResult.imageDirectoryProblems}" var="dirProblem">
    <tr>
       <c:choose>
            <c:when test="${dirProblem.directoryCheck.severity == 'error'}">
                <td></td><td></td>
                <td>
                    <img src="/openmrs/images/error.gif"/>
                    <c:if test="${dirProblem.directoryCheck.notification.page == 'Y'}">
                        <img src="${pageContext.request.contextPath}/moduleResources/chicaops/pager.jpg"/>
                    </c:if>
                    <c:if test="${dirProblem.directoryCheck.notification.email == 'Y'}">
                        <img src="${pageContext.request.contextPath}/moduleResources/chicaops/email.jpg"/>
                    </c:if>
                </td>
            </c:when>
            <c:when test="${dirProblem.directoryCheck.severity == 'warning'}">
                <td></td><td></td>
                <td>
                    <img src="/openmrs/images/alert.gif"/>
                    <c:if test="${dirProblem.directoryCheck.notification.page == 'Y'}">
                        <img src="${pageContext.request.contextPath}/moduleResources/chicaops/pager.jpg"/>
                    </c:if>
                    <c:if test="${dirProblem.directoryCheck.notification.email == 'Y'}">
                        <img src="${pageContext.request.contextPath}/moduleResources/chicaops/email.jpg"/>
                    </c:if>
                </td>
            </c:when>
            <c:otherwise>
                <td></td><td></td><td></td>
            </c:otherwise>
        </c:choose>
        <td>Directory Check 
        <c:if test="${fn:length(dirProblem.directoryCheck.fixTips.tips) > 0}">
                    <c:forEach items="${dirProblem.directoryCheck.fixTips.tips}" var="tip">
                        <c:set var="dirTip" value="${dirTip}<li>${tip}</li>"/>
                    </c:forEach>
                    <span class="hotspot" onmouseover="tooltip.show('<ol>${dirTip}</ol>');" onmouseout="tooltip.hide();"><i>(<u>tips</u>)</i></span>
                    <c:set var="dirTip" value=""/>
        </c:if>
        (<c:out value="${dirProblem.successRate}% success rate in the past "/>
        <c:out value="${dirProblem.directoryCheck.timePeriod}"/> 
        <c:out value="${dirProblem.directoryCheck.timePeriodUnit}"/>(s))</td>
     </tr>
     <tr>
         <td></td><td></td><td></td>
         <td>        
             <div>
                 The following files exist in the <c:out value="${dirProblem.directoryCheck.imageDir}"/> directory but not 
                 in the <c:out value="${dirProblem.directoryCheck.scanDir}"/> directory:
             </div>
         </td>
     </tr>
     <tr>
         <td></td><td></td><td></td>
         <td>
	       <c:forEach items="${dirProblem.fileNames}" var="fileName" varStatus="status">
	        <c:choose>
	          <c:when test="${status.count != 1}">
	              <c:out value=", ${fileName}"/>
	          </c:when>
	          <c:otherwise>
	              <c:out value="${fileName}"/>
	          </c:otherwise>
	        </c:choose>
	       </c:forEach>
        </td>
     </tr>
     <br />
  </c:forEach>
  <c:forEach items="${serverResult.scanDirectoryProblems}" var="dirProblem">
    <tr>
       <c:choose>
            <c:when test="${dirProblem.directoryCheck.severity == 'error'}">
                <td></td><td></td>
                <td>
                    <img src="/openmrs/images/error.gif"/>
                    <c:if test="${dirProblem.directoryCheck.notification.page == 'Y'}">
                        <img src="${pageContext.request.contextPath}/moduleResources/chicaops/pager.jpg"/>
                    </c:if>
                    <c:if test="${dirProblem.directoryCheck.notification.email == 'Y'}">
                        <img src="${pageContext.request.contextPath}/moduleResources/chicaops/email.jpg"/>
                    </c:if>
                </td>
            </c:when>
            <c:when test="${dirProblem.directoryCheck.severity == 'warning'}">
                <td></td><td></td>
                <td>
                    <img src="/openmrs/images/alert.gif"/>
                    <c:if test="${dirProblem.directoryCheck.notification.page == 'Y'}">
                        <img src="${pageContext.request.contextPath}/moduleResources/chicaops/pager.jpg"/>
                    </c:if>
                    <c:if test="${dirProblem.directoryCheck.notification.email == 'Y'}">
                        <img src="${pageContext.request.contextPath}/moduleResources/chicaops/email.jpg"/>
                    </c:if>
                </td>
            </c:when>
            <c:otherwise>
                <td></td><td></td><td></td>
            </c:otherwise>
        </c:choose>
        <td>Directory Check (<c:out value="${dirProblem.successRate}% success rate in the past "/>
        <c:out value="${dirProblem.directoryCheck.timePeriod}"/> 
        <c:out value="${dirProblem.directoryCheck.timePeriodUnit}"/>(s))</td>
     </tr>
     <tr>
         <td></td><td></td><td></td>
         <td>        
             <div>
                 The following files exist in the <c:out value="${dirProblem.directoryCheck.scanDir}"/> directory but not 
                 in the <c:out value="${dirProblem.directoryCheck.imageDir}"/> directory:
             </div>
         </td>
     </tr>
     <tr>
         <td></td><td></td><td></td>
         <td>
           <c:forEach items="${dirProblem.fileNames}" var="fileName" varStatus="status">
            <c:choose>
              <c:when test="${status.count != 1}">
                  <c:out value=", ${fileName}"/>
              </c:when>
              <c:otherwise>
                  <c:out value="${fileName}"/>
              </c:otherwise>
            </c:choose>
           </c:forEach>
        </td>
     </tr>
     <br />
  </c:forEach>
  <tr>
    <c:choose>
        <c:when test="${ruleResult.hasErrors == 'true'}">
            <th scope="row"><img src="/openmrs/images/error.gif"/></th><th scope="col" colspan="3"><c:out value="Rules"/></th>
        </c:when>
        <c:when test="${ruleResult.hasWarnings == 'true'}">
            <th scope="row"]]><img src="/openmrs/images/alert.gif"/></th><th scope="col" colspan="3"><c:out value="Rules"/></th>
        </c:when>
        <c:otherwise>
            <th scope="row"><img src="/openmrs/images/play.gif"/></th><th scope="col" colspan="3"><c:out value="Rules"/></th>
        </c:otherwise>
    </c:choose>
  </tr>
  <c:if test="${!empty (ruleResult.neverFiredRules)}">
    <tr>
       <c:choose>
            <c:when test="${ruleResult.ruleChecks.neverFiredCheck.severity == 'error'}">
                <td></td><td></td>
                <td>
                    <img src="/openmrs/images/error.gif"/>
                    <c:if test="${ruleResult.ruleChecks.neverFiredCheck.notification.page == 'Y'}">
                        <img src="${pageContext.request.contextPath}/moduleResources/chicaops/pager.jpg"/>
                    </c:if>
                    <c:if test="${ruleResult.ruleChecks.neverFiredCheck.notification.email == 'Y'}">
                        <img src="${pageContext.request.contextPath}/moduleResources/chicaops/email.jpg"/>
                    </c:if>
                </td>
            </c:when>
            <c:when test="${ruleResult.ruleChecks.neverFiredCheck.severity == 'warning'}">
                <td></td><td></td>
                <td>
                    <img src="/openmrs/images/alert.gif"/>
                    <c:if test="${ruleResult.ruleChecks.neverFiredCheck.notification.page == 'Y'}">
                        <img src="${pageContext.request.contextPath}/moduleResources/chicaops/pager.jpg"/>
                    </c:if>
                    <c:if test="${ruleResult.ruleChecks.neverFiredCheck.notification.email == 'Y'}">
                        <img src="${pageContext.request.contextPath}/moduleResources/chicaops/email.jpg"/>
                    </c:if>
                </td>
            </c:when>
            <c:otherwise>
                <td></td><td></td><td></td>
            </c:otherwise>
        </c:choose>
        <td>
            <c:out value="RULES NEVER FIRED"/>
            <c:if test="${fn:length(ruleResult.ruleChecks.neverFiredCheck.fixTips.tips) > 0}">
	            <c:forEach items="${ruleResult.ruleChecks.neverFiredCheck.fixTips.tips}" var="tip">
	                <c:set var="neverFiredTip" value="${neverFiredTip}<li>${tip}</li>"/>
	            </c:forEach>
	            <span class="hotspot" onmouseover="tooltip.show('<ol>${neverFiredTip}</ol>');" onmouseout="tooltip.hide();"><i>(<u>tips</u>)</i></span>
	            <c:set var="neverFiredTip" value=""/>
            </c:if> 
        </td>
     </tr>
     <tr>
        <td></td><td></td><td></td><td>The following rules have never fired:</td>
     </tr>
     <tr>
        <td></td><td></td><td></td>
        <td>
            <table cellspacing="0" cellpadding="0">
                <c:set var="counter" value="0"/>
                <c:forEach items="${ruleResult.neverFiredRules}" var="neverFiredRule" varStatus="status">
                    <c:set var="counter" value="${status.count}"/>
                    <c:if test="${(counter mod 2) == 1}">
                        <tr>
                    </c:if>
                        <td>${neverFiredRule}</td>
                    <c:if test="${(counter mod 2) == 0}">
                        </tr>
                    </c:if>
                </c:forEach>
                <c:if test="${counter mod 2 == 1}">
                    </tr>
                </c:if>
             </table>
        </td>
     </tr>
  </c:if>
  <c:if test="${!empty (ruleResult.unFiredRules)}">
    <tr>
       <c:choose>
            <c:when test="${ruleResult.ruleChecks.unFiredCheck.severity == 'error'}">
                <td></td><td></td>
                <td>
                    <img src="/openmrs/images/error.gif"/>
                    <c:if test="${ruleResult.ruleChecks.unFiredCheck.notification.page == 'Y'}">
                        <img src="${pageContext.request.contextPath}/moduleResources/chicaops/pager.jpg"/>
                    </c:if>
                    <c:if test="${ruleResult.ruleChecks.unFiredCheck.notification.email == 'Y'}">
                        <img src="${pageContext.request.contextPath}/moduleResources/chicaops/email.jpg"/>
                    </c:if>
                </td>
            </c:when>
            <c:when test="${ruleResult.ruleChecks.unFiredCheck.severity == 'warning'}">
                <td></td><td></td>
                <td>
                    <img src="/openmrs/images/alert.gif"/>
                    <c:if test="${ruleResult.ruleChecks.unFiredCheck.notification.page == 'Y'}">
                        <img src="${pageContext.request.contextPath}/moduleResources/chicaops/pager.jpg"/>
                    </c:if>
                    <c:if test="${ruleResult.ruleChecks.unFiredCheck.notification.email == 'Y'}">
                        <img src="${pageContext.request.contextPath}/moduleResources/chicaops/email.jpg"/>
                    </c:if>
                </td>
            </c:when>
            <c:otherwise>
                <td></td><td></td><td></td>
            </c:otherwise>
        </c:choose>
        <td>
            <c:out value="DORMANT RULES"/>
            <c:if test="${fn:length(ruleResult.ruleChecks.unFiredCheck.fixTips.tips) > 0}">
                <c:forEach items="${ruleResult.ruleChecks.unFiredCheck.fixTips.tips}" var="tip">
                    <c:set var="unFiredTip" value="${unFiredTip}<li>${tip}</li>"/>
                </c:forEach>
                <span class="hotspot" onmouseover="tooltip.show('<ol>${unFiredTip}</ol>');" onmouseout="tooltip.hide();"><i>(<u>tips</u>)</i></span>
                <c:set var="unFiredTip" value=""/>
            </c:if> 
        </td>
     </tr>
     <tr>
        <td></td><td></td><td></td><td>The following rules have not fired in the past 
        <c:out value="${ruleResult.ruleChecks.unFiredCheck.timePeriod}"/> 
        <c:out value="${ruleResult.ruleChecks.unFiredCheck.timePeriodUnit}"/>(s):</td>
     </tr>
     <tr>
        <td></td><td></td><td></td>
        <td>
            <table cellspacing="0" cellpadding="0">
                <c:set var="counter" value="0"/>
                <c:forEach items="${ruleResult.unFiredRules}" var="unFiredRule" varStatus="status">
                    <c:set var="counter" value="${status.count}"/>
                    <c:if test="${(counter mod 2) == 1}">
                        <tr>
                    </c:if>
                        <td>${unFiredRule}</td>
                    <c:if test="${(counter mod 2) == 0}">
                        </tr>
                    </c:if>
                </c:forEach>
                <c:if test="${counter mod 2 == 1}">
                    </tr>
                </c:if>
             </table>
        </td>
     </tr>
  </c:if>
</table>
<br></br><br></br>
<table class="pretty-table" align="center">
    <caption>Statuses</caption>
    <tr>
        <th scope="row"><img src="/openmrs/images/play.gif"/></th>
        <td>No issues</td>
    </tr>
    <tr>
        <th scope="row"><img src="/openmrs/images/alert.gif"/></th>
        <td>Warning</td>
    </tr>
    <tr>
        <th scope="row"><img src="/openmrs/images/error.gif"/></th>
        <td>Error</td>
    </tr>
    <tr>
        <th scope="row"><img src="${pageContext.request.contextPath}/moduleResources/chicaops/email.jpg"/></th>
        <td>Email</td>
    </tr>
    <tr>
        <th scope="row"><img src="${pageContext.request.contextPath}/moduleResources/chicaops/pager.jpg"/></th>
        <td>Pager</td>
    </tr>
</table>
</body>
</html>