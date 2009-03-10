<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	version="1.0"
	xmlns:SimpleDateFormat="http://xml.apache.org/xalan/java/java.text.SimpleDateFormat"
	xmlns:Date="http://xml.apache.org/xalan/java/java.util.Date"
	xmlns:xalan="http://xml.apache.org/xalan"
	xmlns:sakaifn="org.sakaiproject.reports.utils.xml.XsltFunctions"
	exclude-result-prefixes="xalan">
	
	<xsl:output method="xml" indent="yes"/>

	<xsl:template match="/">
		<xsl:variable name="roleFilter">
			<xsl:text>participant</xsl:text>
		</xsl:variable>
		<xsl:variable name="debug">
			<xsl:text>false</xsl:text>
		</xsl:variable>
		<html>
			<head>
				<style type="text/css" >
					table.alternating {
						cellspacing: 0;
					}
					table.alternating tr {
						cellspacing: 0;
						border: 1px solid black;
						padding: 0.2em 2ex 0.2em 2ex;
					}
					table.alternating tr.header1 th {
						background-color: #632423;
						color: #FFFFFF;
					}
					table.alternating tr.header2 th {
						background-color: #E5B8B7;
					}
					table.alternating td {
						cellspacing: 0;
						border: 1px solid black;
						padding: 0.2em 2ex 0.2em 2ex;
						color: black;
					}
					table.alternating tr.d0 {
						background-color: #F2F2F2;
					}
					table.alternating tr.d1 {
						background-color: #FFFFFF;
					}
					
					<xsl:variable name="statusColorDefs" select="//extraReportResult[@index='3']/data/datarow[@index='0']"/>
					<xsl:variable name="readyDef">
						<xsl:call-template name="valueOrDefault">
							<xsl:with-param name="inputVal" select="$statusColorDefs/element[@colName='READYCOLOR']" />
							<xsl:with-param name="defaultVal"><xsl:text>#86f283</xsl:text></xsl:with-param>
						</xsl:call-template>
					</xsl:variable>
					<xsl:variable name="pendingDef">
						<xsl:call-template name="valueOrDefault">
							<xsl:with-param name="inputVal" select="$statusColorDefs/element[@colName='PENDINGCOLOR']" />
							<xsl:with-param name="defaultVal"><xsl:text>#f7ef84</xsl:text></xsl:with-param>
						</xsl:call-template>		
					</xsl:variable>
					<xsl:variable name="completedDef">
						<xsl:call-template name="valueOrDefault">
							<xsl:with-param name="inputVal" select="$statusColorDefs/element[@colName='COMPLETEDCOLOR']" />
							<xsl:with-param name="defaultVal"><xsl:text>#a6c7ea</xsl:text></xsl:with-param>
						</xsl:call-template>
					</xsl:variable>
					<xsl:variable name="lockedDef">
						<xsl:call-template name="valueOrDefault">
							<xsl:with-param name="inputVal" select="$statusColorDefs/element[@colName='LOCKEDCOLOR']" />
							<xsl:with-param name="defaultVal"><xsl:text>#ac326b</xsl:text></xsl:with-param>
						</xsl:call-template>
					</xsl:variable>
					<xsl:variable name="returnedDef">
						<xsl:call-template name="valueOrDefault">
							<xsl:with-param name="inputVal" select="$statusColorDefs/element[@colName='RETURNEDCOLOR']" />
							<xsl:with-param name="defaultVal"><xsl:text>#6633CC</xsl:text></xsl:with-param>
						</xsl:call-template>
					</xsl:variable>
					
					table.alternating td.matrix-COMPLETE { 
						background-color: <xsl:value-of select="$completedDef"/>;
					}
					table.alternating td.matrix-PENDING {
						background-color: <xsl:value-of select="$pendingDef"/>;
					}
					table.alternating td.matrix-READY {
						background-color: <xsl:value-of select="$readyDef"/>;
					}
					table.alternating td.matrix-LOCKED {
						background-color: <xsl:value-of select="$lockedDef"/>;
						color: white !important;
					}
					table.alternating td.matrix-RETURNED {
						background-color: <xsl:value-of select="$returnedDef"/>;
						color: white !important;
					}
				</style>
			</head>
			<body>
				<div>
					<h3>
						<xsl:value-of select="//title" />
					</h3>

					Run on:
					<xsl:value-of select="//runDate" />
					
<xsl:variable name="statusStructure">
	<cellStatus key="READY" value="Ready"/>
	<cellStatus key="PENDING" value="Pending"/>
	<cellStatus key="COMPLETE" value="Completed"/>
	<cellStatus key="RETURNED" value="Returned"/>
	<cellStatus key="LOCKED" value="Locked"/>
</xsl:variable>

					<table border="1" width="90%" align="center" class="alternating">
						<tr class="header1 exportHeaderRow">
							<th class="exportHeader"></th>
							<th class="exportHeader"></th>
							<th class="exportHeader"></th>
							<th class="exportHeader"></th>
							<xsl:for-each
								select="//extraReportResult[@index='0']/data/datarow">
								<th class="exportHeader">
									<xsl:attribute name="colspan"><xsl:value-of
											select="element[@colName='NUMCOLS']" />
									</xsl:attribute>
									<xsl:value-of
										select="element[@colName='ROWNAME']" />
								</th>
							</xsl:for-each>
						</tr>
						<tr class="header2 exportHeaderRow">
							<th class="exportHeader">Username</th>
							<th class="exportHeader">Lastname</th>
							<th class="exportHeader">Firstname</th>
							<th class="exportHeader">Groups</th>
							<xsl:for-each
								select="//extraReportResult[@index='1']/data/datarow">
								<th class="exportHeader">
									<xsl:value-of
										select="element[@colName='COLNAME']" />
								</th>
							</xsl:for-each>
						</tr>

						<xsl:variable name="roleFilteredDataRows"
							select="/reportResult/data/datarow[element[@colName='OWNER_SITEUSER']/user/role = $roleFilter]" />

						<xsl:for-each
							select="//extraReportResult[@index='2']/data/datarow">
							<xsl:variable name="currentUserId"
								select="element[@colName='OWNER']" />
							
							<xsl:variable name="currentUserElement"
									select="$roleFilteredDataRows/element[@colName='OWNER_SITEUSER']/user[id=$currentUserId]" />

								<xsl:if test="$currentUserElement">
							
							
							<tr>
								<xsl:attribute name="class">
									<xsl:text>exportDataRow d</xsl:text>
									<xsl:value-of select="position() mod 2" />
								</xsl:attribute>

								<td class="exportDataCol">
									<xsl:value-of
										select="xalan:nodeset($currentUserElement)/eid" />
								</td>
								<td class="exportDataCol">
									<xsl:value-of
										select="xalan:nodeset($currentUserElement)/lastName" />
									<xsl:text disable-output-escaping="yes">&amp;#160;</xsl:text>
								</td>
								<td class="exportDataCol">
									<xsl:value-of
										select="xalan:nodeset($currentUserElement)/firstName" />
									<xsl:text disable-output-escaping="yes">&amp;#160;</xsl:text>
								</td>
								<td class="exportDataCol">
									<xsl:value-of
										select="xalan:nodeset($currentUserElement)/groups" />
									<xsl:text disable-output-escaping="yes">&amp;#160;</xsl:text>
								</td>

								<xsl:for-each
									select="//extraReportResult[@index='1']/data/datarow">
									
									<xsl:variable name="currentrowId"
										select="element[@colName='ROW_ID']" />
									<xsl:variable name="currentcolId"
									select="element[@colName='COLID']" />
									
									<xsl:variable
										name="currentDataRow"
										select="$roleFilteredDataRows[element[@colName='ROOTCRITERION_ID'] = $currentrowId and element[@colName='LEVEL_ID'] = $currentcolId and element[@colName='OWNER_SITEUSER']/user/id = $currentUserId]" />
									
									<xsl:variable name="currentStatus" 
										select="$currentDataRow/element[@colName='STATUS']"/>
									<td>
										<xsl:attribute name="class">
											<xsl:text>exportDataCol matrix-</xsl:text>
											<xsl:value-of select="$currentStatus" />
										</xsl:attribute>
										
											
										<xsl:value-of select="xalan:nodeset($statusStructure)/cellStatus[@key = $currentStatus]/@value"/>

										<xsl:text disable-output-escaping="yes">&amp;#160;</xsl:text>
									</td>
								</xsl:for-each>


							</tr>
							
							</xsl:if>
						</xsl:for-each>

					</table>

				</div>

			</body>
		</html>
	</xsl:template>
	
	<xsl:template name="valueOrDefault">
		<xsl:param name="inputVal"/>
		<xsl:param name="defaultVal"/>
		
		<xsl:choose>
			<xsl:when test="$inputVal = ''"><xsl:value-of select="$defaultVal"/></xsl:when>
			<xsl:otherwise><xsl:value-of select="$inputVal"/></xsl:otherwise>
		</xsl:choose>
		
		
	</xsl:template>


</xsl:stylesheet>
