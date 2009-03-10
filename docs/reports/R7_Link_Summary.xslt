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

	<xsl:variable name="linkData">
		<cells>
			<xsl:for-each
				select="//extraReportResult[@index='1']/data/datarow">
				<xsl:variable name="currentrowId"
					select="element[@colName='ROW_ID']" />
				<xsl:variable name="currentcolId"
					select="element[@colName='COLID']" />
				<cell>
					<xsl:attribute name="row">
						<xsl:value-of select="$currentrowId"/>
					</xsl:attribute>
					<xsl:attribute name="col">
						<xsl:value-of select="$currentcolId"/>
					</xsl:attribute>
				<xsl:variable
					name="currentDataRow"
					select="/reportResult/data/datarow[element[@colName='TAG_CRITERIAREF']/criteriaRef/row = $currentrowId and element[@colName='TAG_CRITERIAREF']/criteriaRef/col = $currentcolId]" />
				
					<xsl:variable name="linkCountVar" select="count(xalan:nodeset($currentDataRow))"/>
					<xsl:attribute name="linkCount">
						<xsl:value-of select="$linkCountVar"/>
					</xsl:attribute>
				<xsl:for-each select="xalan:nodeset($currentDataRow)/element[@colName='ACTIVITYREF_ACTIVITY']/activity">
					<link>						
						<xsl:attribute name="index">
							<xsl:value-of select="position()"/>
						</xsl:attribute>
						<xsl:value-of select="siteTitle" />:<xsl:value-of select="type" />:<xsl:value-of select="title" />
					</link>
				</xsl:for-each>
				</cell>
			</xsl:for-each>
			
		</cells>
	</xsl:variable>

					<table border="1" width="90%" align="center" class="alternating">
						<tr class="header1 exportHeaderRow">
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
							<th class="exportHeader" />
							<xsl:for-each
								select="//extraReportResult[@index='1']/data/datarow">
								<th class="exportHeader">
									<xsl:value-of
										select="element[@colName='COLNAME']" />
								</th>
							</xsl:for-each>
						</tr>
						
						<xsl:variable name="maxLinks">
							<xsl:call-template name="max">
								<xsl:with-param name="list">
									<xsl:apply-templates select="xalan:nodeset($linkData)/cells/cell" mode="copy">
										<xsl:sort select="@linkCount"/>
									</xsl:apply-templates>
								</xsl:with-param> 
							</xsl:call-template>
							
						</xsl:variable>

						<xsl:call-template name="tr-recursive">
							<xsl:with-param name="maxIndex" select="$maxLinks" />
							<xsl:with-param name="data" select="$linkData" />
						</xsl:call-template>
							
					</table>

				</div>

			</body>
		</html>
	</xsl:template>
	
	<xsl:template name ="max">
		<xsl:param name ="list" />
		<xsl:variable name="nodeList" select="xalan:nodeset($list)/cell"/>
		<xsl:variable name="listCount" select="count($nodeList)"/>
		<xsl:variable name="cellWithMax" select="$nodeList[$listCount]"/>
		<xsl:value-of select="$cellWithMax/@linkCount" />
	</xsl:template>
	
	<xsl:template match="cell" mode="copy">
		<xsl:copy-of select="."/>
	</xsl:template>
	
	<xsl:template name="tr-recursive">
		<xsl:param name="index" select="1"/>
		<xsl:param name="maxIndex" />
		<xsl:param name="data" />
		<tr>
			<xsl:attribute name="class">
				<xsl:text>exportDataRow d</xsl:text>
				<xsl:value-of select="position() mod 2" />
			</xsl:attribute>
			
			<th class="exportDataCol">Link <xsl:value-of select="$index"/></th>
			<xsl:for-each
				select="//extraReportResult[@index='1']/data/datarow">
				<xsl:variable name="currentrowId"
					select="element[@colName='ROW_ID']" />
				<xsl:variable name="currentcolId"
					select="element[@colName='COLID']" />
				
				<td class="exportDataCol">
					<xsl:variable name="currentLink" select="xalan:nodeset($data)/cells/cell[@row=$currentrowId and @col=$currentcolId]/link[@index=$index]"/>
					<xsl:value-of select="$currentLink"/>
					<xsl:text disable-output-escaping="yes">&amp;#160;</xsl:text>
				</td>
			</xsl:for-each>
			
		</tr>
		<xsl:if test="$index &lt; $maxIndex">
			<xsl:call-template name="tr-recursive">
				<xsl:with-param name="index" select="$index + 1"/>
				<xsl:with-param name="maxIndex" select="$maxIndex" />
				<xsl:with-param name="data" select="$data" />
			</xsl:call-template>
		</xsl:if>
		<xsl:if test="$index = $maxIndex">
			<tr class="exportDataRow">
				<th class="exportDataCol">Total links</th>
				<xsl:for-each
					select="//extraReportResult[@index='1']/data/datarow">
					<xsl:variable name="currentrowId"
						select="element[@colName='ROW_ID']" />
					<xsl:variable name="currentcolId"
						select="element[@colName='COLID']" />
					
					<td class="exportDataCol">
						<xsl:variable name="currentCell" select="xalan:nodeset($data)/cells/cell[@row=$currentrowId and @col=$currentcolId]"/>
						<xsl:value-of select="$currentCell/@linkCount"/>
						<xsl:text disable-output-escaping="yes">&amp;#160;</xsl:text>
					</td>
				</xsl:for-each>
			</tr>
		</xsl:if>
	</xsl:template>
	

</xsl:stylesheet>
