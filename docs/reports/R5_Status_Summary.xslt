<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    version="1.0"
    xmlns:SimpleDateFormat="http://xml.apache.org/xalan/java/java.text.SimpleDateFormat" 
    xmlns:sakaifn="org.sakaiproject.reports.utils.xml.XsltFunctions"
    xmlns:Date="http://xml.apache.org/xalan/java/java.util.Date"
    xmlns:math="http://www.ora.com/XSLTCookbook/math"
    xmlns:xalan="http://xml.apache.org/xalan"
    exclude-result-prefixes="xalan">
    
<xsl:output method="xml" indent="yes"/>
<xsl:template match="/">

<xsl:variable name="roleFilter">
	<xsl:text>participant</xsl:text>
</xsl:variable>

<html>
<head>
<style type="text/css" >
table.alternating {
}
table.alternating tr.header1 th {
	background-color: #632423;
	color: #FFFFFF;
}
table.alternating tr.header2 th {
	background-color: #E5B8B7;
}
table.alternating td {
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


	<xsl:variable name="statusColorDefs" select="//extraReportResult[@index='2']/data/datarow[@index='0']"/>
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

	
table.alternating th.matrix-COMPLETE { 
	background-color: <xsl:value-of select="$completedDef"/>;
}
table.alternating th.matrix-PENDING {
	background-color: <xsl:value-of select="$pendingDef"/>;
}
table.alternating th.matrix-READY {
	background-color: <xsl:value-of select="$readyDef"/>;
}
table.alternating th.matrix-LOCKED {
	background-color: <xsl:value-of select="$lockedDef"/>;
	color: white !important;
}
table.alternating th.matrix-RETURNED {
	background-color: <xsl:value-of select="$returnedDef"/>;
	color: white !important;
}
</style>

</head>
<body>
    <div>
    <h3><xsl:value-of select="//title" /></h3>
    
    Run on: <xsl:value-of select="//runDate" />
    

<xsl:variable name="statusStructure">
	<cellStatus>
		<key>READY</key>
		<value>Ready</value>
	</cellStatus>
	<cellStatus>
		<key>PENDING</key>
		<value>Pending</value>
	</cellStatus>
	<cellStatus>
		<key>COMPLETE</key>
		<value>Completed</value>
	</cellStatus>
	<cellStatus>
		<key>RETURNED</key>
		<value>Returned</value>
	</cellStatus>
	<cellStatus>
		<key>LOCKED</key>
		<value>Locked</value>
	</cellStatus>
</xsl:variable>

<xsl:variable name="outputRowData">
	<outputRows>	
		<xsl:for-each select="xalan:nodeset($statusStructure)/cellStatus">
			<data>
				<key>percentEachStatus<xsl:value-of select="key"/></key>
				<text>% <xsl:value-of select="value"/></text>
				<class>matrix-<xsl:value-of select="key"/></class>
			</data>
		</xsl:for-each>
		<xsl:for-each select="xalan:nodeset($statusStructure)/cellStatus">
			<data>
				<key>countEachStatus<xsl:value-of select="key"/></key>
				<text>Count <xsl:value-of select="value"/></text>
				<class>matrix-<xsl:value-of select="key"/></class>
			</data>
		</xsl:for-each>
		<data>
			<key>countParticipants</key>
			<text>Total Participants</text>
		</data>
	</outputRows>
</xsl:variable>

<xsl:variable name="roleFilteredDataRows"
	select="/reportResult/data/datarow[element[@colName='OWNER_SITEUSER']/user/role = $roleFilter]" />
 	 
	<table border="1" width="90%" cellpadding="3" class="alternating">
		<tr class="header1 exportHeaderRow">
			<th class="exportHeader"></th>
				<xsl:for-each select="//extraReportResult[@index='0']/data/datarow">
					<th class="exportHeader">
						<xsl:attribute name="colspan"><xsl:value-of select="element[@colName='NUMCOLS']" /></xsl:attribute>
						<xsl:value-of select="element[@colName='ROWNAME']" />
					</th>
				</xsl:for-each>
		</tr>
		<tr class="header2 exportHeaderRow">
			<th class="exportHeader"></th>
			<xsl:for-each select="//extraReportResult[@index='1']/data/datarow">
				<th class="exportHeader"><xsl:value-of select="element[@colName='COLNAME']" /></th>
			</xsl:for-each>
		</tr>



	<xsl:variable name="calculationResults">

		<calculations>
			<xsl:for-each select="//extraReportResult[@index='1']/data/datarow">
				<xsl:variable name="currentrowId" select="element[@colName='ROW_ID']" />
				<xsl:variable name="currentcolId" select="element[@colName='COLID']" />
 				
 				<xsl:variable name="currentDataRow"
					select="$roleFilteredDataRows[element[@colName='ROOTCRITERION_ID'] = $currentrowId and element[@colName='LEVEL_ID'] = $currentcolId]" />
 				
 				<xsl:variable name="node_statuses"
					select="xalan:nodeset($currentDataRow)/element[@colName='STATUS']" />
				<xsl:variable name="nodecount" select="count($currentDataRow)" />

				<calculation>
					<xsl:attribute name="rowId"><xsl:value-of
						select="$currentrowId" /></xsl:attribute>
					<xsl:attribute name="colId"><xsl:value-of
						select="$currentcolId" /></xsl:attribute>
					<sortedValues>
						<xsl:for-each select="$node_statuses">
							<xsl:value-of select="." />,
						</xsl:for-each>
					</sortedValues>
					
					<xsl:variable name="statusResults">

						<theStatuses>
							<xsl:for-each select="xalan:nodeset($node_statuses)">
								<status>
									<xsl:attribute name="statusValue">
										<xsl:value-of select="." />
									</xsl:attribute>
								</status>
							</xsl:for-each>
						</theStatuses>

					</xsl:variable>


					<xsl:for-each select="xalan:nodeset($statusStructure)/cellStatus">
						<xsl:variable name="keyVal" select="key"/>
						<xsl:variable name="tmpName">
							<xsl:text>percentEachStatus</xsl:text>
							<xsl:value-of select="$keyVal" />
						</xsl:variable>
						
						<data>
							<xsl:attribute name="name">
								<xsl:value-of select="$tmpName" />
							</xsl:attribute>
 							
 							<xsl:call-template name="percentFormat">
								<xsl:with-param name="inputVal"
									select="count(xalan:nodeset($statusResults)/theStatuses/status[@statusValue=$keyVal]) div $nodecount" />
 							</xsl:call-template>

						</data>
						
						<xsl:variable name="tmpName2">
							<xsl:text>countEachStatus</xsl:text>
							<xsl:value-of select="$keyVal" />
						</xsl:variable>
						
						<data>
							<xsl:attribute name="name">
								<xsl:value-of select="$tmpName2" />
							</xsl:attribute>
							
							<xsl:value-of
								select="count(xalan:nodeset($statusResults)/theStatuses/status[@statusValue=$keyVal])" />
							
						</data>
						 
					</xsl:for-each>

					<data name="countParticipants">
						<xsl:value-of select="count(xalan:distinct(xalan:nodeset($currentDataRow)/element[@colName='OWNER_SITEUSER']/user/id))"/>
					</data>

				</calculation>

			</xsl:for-each>
		</calculations>

	</xsl:variable> 


 		
		<xsl:for-each select="xalan:nodeset($outputRowData)/outputRows/data">
		<tr>
			<xsl:attribute name="class">
				<xsl:text>exportDataRow d</xsl:text>
				<xsl:value-of select="position() mod 2" />
			</xsl:attribute>
			<xsl:variable name="dataName" select="text"/>
			<xsl:variable name="dataKey" select="key"/>
			<th class="exportDataCol" style="text-align:right;">
				<xsl:attribute name="class">
					<xsl:text>exportDataCol </xsl:text>
					<xsl:value-of select="class" />
				</xsl:attribute>
				<xsl:value-of select="$dataName" />
			</th>
			<xsl:for-each select="//extraReportResult[@index='1']/data/datarow">
				<xsl:variable name="currentrowId" select="element[@colName='ROW_ID']" />
				<xsl:variable name="currentcolId" select="element[@colName='COLID']" />
				<td class="exportDataCol">
 
					<xsl:value-of select="xalan:nodeset($calculationResults)/calculations/calculation[@rowId=$currentrowId and @colId=$currentcolId]/data[@name=$dataKey]" />
					<xsl:text disable-output-escaping="yes">&amp;#160;</xsl:text>

				</td>
			</xsl:for-each>
		</tr>
		</xsl:for-each>

   </table>
  
    </div>

</body>
</html>
</xsl:template>

<xsl:template name="decimalFormat">
	<xsl:param name="inputVal" />

	<xsl:value-of select="format-number($inputVal, '#.#')" />
	
</xsl:template>

<xsl:template name="percentFormat">
	<xsl:param name="inputVal" />

	<xsl:value-of select="format-number($inputVal, '##.#%')" />
	
</xsl:template>

   <xsl:template match="datarow" mode="copy">
    <xsl:copy-of select="."/>
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
