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
				</style>
			</head>
			<body>
				<div>
					<h3>
						<xsl:value-of select="//title" />
					</h3>

					Run on:
					<xsl:value-of select="//runDate" />

					<table border="1" width="90%" align="center" class="alternating">
						<tr class="header1 exportHeaderRow">
							<th class="exportHeader" />
							<th class="exportHeader" />
							<th class="exportHeader" />
							<th class="exportHeader" />
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
									<td class="exportDataCol">
										<xsl:variable
											name="currentDataRow"
											select="$roleFilteredDataRows[element[@colName='ROOTCRITERION_ID'] = $currentrowId and element[@colName='LEVEL_ID'] = $currentcolId and element[@colName='OWNER_SITEUSER']/user/id = $currentUserId]" />
										<xsl:for-each
											select="xalan:nodeset($currentDataRow)">
											
											<xsl:sort select="sakaifn:getSortableDate(element[@colName='REVIEW_CONTENT_ARTIFACT']/artifact/metaData/repositoryNode/modified)" 
												data-type="number" order="descending" />
											 
											<xsl:variable name="currentArtifact" 
												select="element[@colName='REVIEW_CONTENT_ARTIFACT']/artifact"/>
											
											<xsl:if test="/reportResult/parameters/parameter[@name='mostRecentOnly'] = 'false' or position() = 1">
												<xsl:if test="$debug = 'true'">
													<xsl:text>Debug - Date(</xsl:text>
													<xsl:value-of
														select="$currentArtifact/metaData/repositoryNode/modified" />
													<xsl:text>) -</xsl:text>
												</xsl:if>
												<xsl:value-of
													select="$currentArtifact/structuredData/EvaluationForm/rating" />
												<xsl:if test="position() != last()"><xsl:text>;</xsl:text></xsl:if>
											</xsl:if>

										</xsl:for-each>
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


</xsl:stylesheet>
