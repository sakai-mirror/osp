<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xs="http://www.w3.org/2001/XMLSchema" 
	version="1.0"
	xmlns:SimpleDateFormat="http://xml.apache.org/xalan/java/java.text.SimpleDateFormat"
	xmlns:sakaifn="org.sakaiproject.reports.utils.xml.XsltFunctions"
	xmlns:Date="http://xml.apache.org/xalan/java/java.util.Date"
	xmlns:math="http://www.ora.com/XSLTCookbook/math" 
	xmlns:xalan="http://xml.apache.org/xalan"
	exclude-result-prefixes="xalan">

	<xsl:key name="userGroupKey" match="datarow"
			use="element[@colName='OWNER_SITEUSER']/user/id"/>

	<xsl:output method="xml" indent="yes"/>
	
	<xsl:variable name="roleFilter">
		<xsl:text>participant</xsl:text>
	</xsl:variable>
	
	<xsl:template match="/">

		<html>
			<head>
				<style type="text/css">
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
						<xsl:value-of select="//title"/>
					</h3> Run on: <xsl:value-of select="//runDate"/>
					<xsl:variable name="ratingKeyValueStructure">

						<xsl:for-each
							select="//datarow[1]/element[@colName='REVIEW_CONTENT_ARTIFACT']/artifact/schema/element[@name='EvaluationForm']/children/element[@name='rating']/xs:simpleType/xs:restriction/xs:enumeration[not(@value = '')]">
							<rating>
								<key>
									<xsl:value-of select="@value"/>
								</key>
								<value>
									<xsl:value-of
										select="xs:annotation/xs:documentation[@xml:lang='en']"/>
								</value>
							</rating>
						</xsl:for-each>
					</xsl:variable>
					<!-- 
<rating>
<key>1</key>
<value>does not meet expectations</value>
</rating>
<rating>

<key>2</key>
<value>meets expectations</value>
</rating>
<rating>
<key>3</key>
<value>exceeds expectations</value>
</rating>
 -->
					<xsl:variable name="outputRowData">
						<outputRows>
							<data>
								<key>mean</key>
								<text>mean</text>
							</data>
							<data>
								<key>median0</key>
								<text>median0</text>
							</data>
							<data>
								<key>median1</key>
								<text>median1</text>
							</data>
							<data>
								<key>median2</key>
								<text>median2</text>
							</data>
							<data>
								<key>mode</key>
								<text>mode</text>
							</data>
							<data>
								<key>stdDev</key>
								<text>standard deviation</text>
							</data>
							<xsl:for-each select="xalan:nodeset($ratingKeyValueStructure)/rating">
								<data>
									<key>percentEachRatings<xsl:value-of select="key"/></key>
									<text>% <xsl:value-of select="value"/> (<xsl:value-of
											select="key"/>)</text>
								</data>
							</xsl:for-each>
							<xsl:for-each select="xalan:nodeset($ratingKeyValueStructure)/rating">
								<data>
									<key>countEachRating<xsl:value-of select="key"/></key>
									<text>count: <xsl:value-of select="value"/> (<xsl:value-of
											select="key"/>)</text>
								</data>
							</xsl:for-each>
							<data>
								<key>countRatings</key>
								<text>count: total ratings</text>
							</data>
							<data>
								<key>countParticipants</key>
								<text>count: total participants</text>
							</data>
						</outputRows>
					</xsl:variable>
					<xsl:variable name="roleFilteredDataRows"
						select="/reportResult/data/datarow[element[@colName='OWNER_SITEUSER']/user/role = $roleFilter]"/>
					<xsl:variable name="extraReportResultCopy">

						<xsl:apply-templates select="//extraReportResult[@index='1']/data/datarow"
							mode="copy"/>

					</xsl:variable>
					<table border="1" width="90%" cellpadding="3" class="alternating">
						<tr class="header1 exportHeaderRow">
							<th class="exportHeader"/>
							<xsl:for-each select="//extraReportResult[@index='0']/data/datarow">
								<th class="exportHeader">
									<xsl:attribute name="colspan">
										<xsl:value-of select="element[@colName='NUMCOLS']"/>
									</xsl:attribute>
									<xsl:value-of select="element[@colName='ROWNAME']"/>
								</th>
							</xsl:for-each>
						</tr>
						<tr class="header2 exportHeaderRow">
							<th class="exportHeader"/>
							<xsl:for-each select="//extraReportResult[@index='1']/data/datarow">
								<th class="exportHeader">
									<xsl:value-of select="element[@colName='COLNAME']"/>
								</th>
							</xsl:for-each>
						</tr>



						<xsl:variable name="calculationResults">

							<calculations>
								<xsl:for-each select="//extraReportResult[@index='1']/data/datarow">
									<xsl:variable name="currentrowId"
										select="element[@colName='ROW_ID']"/>
									<xsl:variable name="currentcolId"
										select="element[@colName='COLID']"/>


									<xsl:variable name="currentDataRow"
										select="$roleFilteredDataRows[element[@colName='ROOTCRITERION_ID'] = $currentrowId and element[@colName='LEVEL_ID'] = $currentcolId]"/>


									<xsl:variable name="dateFilteredDataRow">
										<xsl:call-template name="dateFilter">
											<xsl:with-param name="inputData" select="$currentDataRow"/>
											<xsl:with-param name="rowId" select="$currentrowId"/>
											<xsl:with-param name="colId" select="$currentcolId"/>
										</xsl:call-template>
									</xsl:variable>
									
									<xsl:variable name="sortedDataRow">
										<xsl:apply-templates select="xalan:nodeset($dateFilteredDataRow)/datarow" mode="copy">
											<xsl:sort
												select="element[@colName='REVIEW_CONTENT_ARTIFACT']/artifact/structuredData/EvaluationForm/rating"
												data-type="number"/>
										</xsl:apply-templates>
									</xsl:variable>


									<xsl:variable name="node_ratings"
										select="xalan:nodeset($sortedDataRow)/datarow/element[@colName='REVIEW_CONTENT_ARTIFACT']/artifact/structuredData/EvaluationForm/rating"/>
									<xsl:variable name="nodecount" select="count($node_ratings)"/>
									
									<xsl:variable name="node_ratings_filtered"
										select="xalan:nodeset($node_ratings)[. != 'n/a']"/>
									<xsl:variable name="nodecount_filtered" select="count($node_ratings_filtered)"/>



									<calculation>
										<xsl:attribute name="rowId">
											<xsl:value-of select="$currentrowId"/>
										</xsl:attribute>
										<xsl:attribute name="colId">
											<xsl:value-of select="$currentcolId"/>
										</xsl:attribute>
										<sortedValues>
											<xsl:for-each select="$node_ratings">
												<xsl:value-of select="."/>, </xsl:for-each>
										</sortedValues>
										<data name="mean">
											<xsl:call-template name="decimalFormat">
												<xsl:with-param name="inputVal"
													select="sum($node_ratings_filtered) div $nodecount_filtered"
												/>
											</xsl:call-template>
										</data>
										<data name="median0">
											<xsl:call-template name="decimalFormat">
												<xsl:with-param name="inputVal"
												select="xalan:nodeset($sortedDataRow)/datarow[ceiling(last() div 2)]/element[@colName='REVIEW_CONTENT_ARTIFACT']/artifact/structuredData/EvaluationForm/rating"
												/>
											</xsl:call-template>
										</data>
										<data name="median1">
											<xsl:call-template name="math:median1">
												<xsl:with-param name="nodes" select="$node_ratings_filtered"/>
												<xsl:with-param name="count" select="$nodecount_filtered"/>
											</xsl:call-template>
										</data>
										<data name="median2">
											<xsl:call-template name="math:median2">
												<xsl:with-param name="nodes" select="$node_ratings_filtered"/>
												<xsl:with-param name="count" select="$nodecount_filtered"/>
											</xsl:call-template>
										</data>
										
										<data name="mode">
											<xsl:call-template name="math:mode">
												<xsl:with-param name="nodes" select="$node_ratings_filtered" />
											</xsl:call-template>
										</data>

										<data name="stdDev">
											<xsl:call-template name="math:stdDev">
												<xsl:with-param name="nodes" select="$node_ratings_filtered"/>
												<xsl:with-param name="nodeCount" select="$nodecount_filtered"
												/>
											</xsl:call-template>
										</data>

										<xsl:variable name="ratingResults">

											<theRatings>
												<xsl:for-each select="xalan:nodeset($node_ratings)">
												<rating>
												<xsl:attribute name="ratingValue">
												<xsl:value-of select="."/>
												</xsl:attribute>
												</rating>
												</xsl:for-each>
											</theRatings>

										</xsl:variable>


										<xsl:for-each
											select="xalan:nodeset($ratingKeyValueStructure)/rating">
											<xsl:variable name="keyVal" select="key"/>
											<xsl:variable name="tmpName">
												<xsl:text>percentEachRatings</xsl:text>
												<xsl:value-of select="$keyVal"/>
											</xsl:variable>

											<data>
												<xsl:attribute name="name">
												<xsl:value-of select="$tmpName"/>
												</xsl:attribute>

												<xsl:call-template name="percentFormat">
												<xsl:with-param name="inputVal"
												select="count(xalan:nodeset($ratingResults)/theRatings/rating[@ratingValue=$keyVal]) div $nodecount"
												/>
												</xsl:call-template>

											</data>


											<xsl:variable name="tmpName2">
												<xsl:text>countEachRating</xsl:text>
												<xsl:value-of select="$keyVal"/>
											</xsl:variable>
											
											<data>
												<xsl:attribute name="name">
													<xsl:value-of select="$tmpName2"/>
												</xsl:attribute>
												
												<xsl:value-of
													select="count(xalan:nodeset($ratingResults)/theRatings/rating[@ratingValue=$keyVal])"/>
											</data>

										</xsl:for-each>

										<data name="countRatings">
											<xsl:value-of select="$nodecount"/>
										</data>
										<data name="countParticipants">
											<xsl:value-of
												select="count(xalan:distinct(xalan:nodeset($currentDataRow)/element[@colName='OWNER_SITEUSER']/user/id))"/>
										</data>

									</calculation>

								</xsl:for-each>
							</calculations>

						</xsl:variable>



						<xsl:for-each select="xalan:nodeset($outputRowData)/outputRows/data">
							<tr>
								<xsl:attribute name="class">
									<xsl:text>exportDataRow d</xsl:text>
									<xsl:value-of select="position() mod 2"/>
								</xsl:attribute>
								<xsl:variable name="dataName" select="text"/>
								<xsl:variable name="dataKey" select="key"/>
								<th class="exportDataCol" style="text-align:right;">
									<xsl:value-of select="$dataName"/>
								</th>
								<xsl:for-each select="xalan:nodeset($extraReportResultCopy)/datarow">
									<xsl:variable name="currentrowId"
										select="element[@colName='ROW_ID']"/>
									<xsl:variable name="currentcolId"
										select="element[@colName='COLID']"/>
									<td class="exportDataCol">

										<xsl:value-of
										select="xalan:nodeset($calculationResults)/calculations/calculation[@rowId=$currentrowId and @colId=$currentcolId]/data[@name=$dataKey]"/>
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


	<xsl:template name="dateFilter">
		<xsl:param name="inputData"/>
		<xsl:param name="rowId"/>
		<xsl:param name="colId"/>   


		<xsl:variable name="filteredResults">
			<xsl:choose>
				<xsl:when test="/reportResult/parameters/parameter[@name='mostRecentOnly'] = 'false'">
					<xsl:copy-of select="$inputData"/>
	
				</xsl:when>
				<xsl:otherwise>			
					<xsl:for-each select="/reportResult/data/datarow[element[@colName='ROOTCRITERION_ID'] = $rowId and element[@colName='LEVEL_ID'] = $colId and element[@colName='OWNER_SITEUSER']/user/role = $roleFilter and generate-id(.)=generate-id(key('userGroupKey', element[@colName='OWNER_SITEUSER']/user/id)[1])]">
									
						<xsl:variable name="outerLoopResults" select="key('userGroupKey', element[@colName='OWNER_SITEUSER']/user/id)[element[@colName='ROOTCRITERION_ID'] = $rowId and element[@colName='LEVEL_ID'] = $colId and element[@colName='OWNER_SITEUSER']/user/role = $roleFilter]"/>
							
							<xsl:for-each select="$outerLoopResults">
								<xsl:sort select="sakaifn:getSortableDate(element[@colName='REVIEW_CONTENT_ARTIFACT']/artifact/metaData/repositoryNode/modified)" order="descending"/>
								
								<xsl:if test="position()=1"><xsl:copy-of select="." /></xsl:if>
								
							</xsl:for-each>					
						
						</xsl:for-each>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<xsl:copy-of select="xalan:nodeset($filteredResults)/datarow"/>
	</xsl:template>

	<xsl:template name="decimalFormat">
		<xsl:param name="inputVal"/>
		<xsl:if test="string(number($inputVal)) != 'NaN'">
			<xsl:value-of select="format-number($inputVal, '#.#')"/>
		</xsl:if>		
	</xsl:template>
	
	<xsl:template name="percentFormat">
		<xsl:param name="inputVal"/>
		<xsl:if test="string(number($inputVal)) != 'NaN'">
			<xsl:value-of select="format-number($inputVal, '##.#%')"/>
		</xsl:if>
	</xsl:template>

	<xsl:template match="datarow" mode="copy">
		<xsl:copy-of select="."/>
	</xsl:template>
<!--
	<xsl:template match="extraReportResults" mode="copy">
		<xsl:copy-of select="."/>
	</xsl:template>
-->
	<xsl:template name="math:median1">
		<xsl:param name="nodes"/>
		<xsl:param name="count"/>
		<xsl:variable name="middle1" select="floor(($count + 1) div 2)"/>
		<xsl:variable name="middle2" select="ceiling(($count + 1) div 2)"/>

		<xsl:variable name="m1">
			<xsl:for-each select="$nodes">
				<xsl:if test="position() = $middle1">
					<xsl:value-of select="."/>
				</xsl:if>
			</xsl:for-each>
		</xsl:variable>

		<xsl:variable name="m2">
			<xsl:choose>
				<xsl:when test="$middle1 = $middle2">
					<xsl:value-of select="$m1"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:for-each select="$nodes">
						<xsl:if test="position() = $middle2">
							<xsl:value-of select="."/>
						</xsl:if>
					</xsl:for-each>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<!-- The median -->

		<xsl:call-template name="decimalFormat">
			<xsl:with-param name="inputVal" select="($m1 + $m2) div 2"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="math:median2">
		<xsl:param name="nodes"/>
		<xsl:param name="count"/>
		<xsl:variable name="middle" select="ceiling($count div 2)"/>
		<xsl:variable name="even" select="not($count mod 2)"/>


		<xsl:variable name="m1">
			<xsl:for-each select="$nodes">
				<xsl:if test="position() = $middle">
					<xsl:value-of select=". + ($even * ./following-sibling::*[1])"/>
				</xsl:if>
			</xsl:for-each>
		</xsl:variable>

		<!-- The median -->
		<xsl:call-template name="decimalFormat">
			<xsl:with-param name="inputVal" select="$m1 div ($even + 1)"/>
		</xsl:call-template>
	</xsl:template>


	<xsl:template name="math:mode">
		<xsl:param name="nodes"/>
		<xsl:param name="max" select="0"/>
		<xsl:param name="mode" select="/.."/>

		<xsl:choose>
			<xsl:when test="not($nodes)">
				<xsl:copy-of select="$mode"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:variable name="first" select="$nodes[1]"/>
				<xsl:variable name="try" select="$nodes[. = $first]"/>
				<xsl:variable name="count" select="count($try)"/>
				<!-- Recurse with nodes not equal to first -->
				<xsl:call-template name="math:mode">
					<xsl:with-param name="nodes" select="$nodes[not(. = $first)]"/>
					<!-- If we have found a node that is more frequent then 
		pass the count otherwise pass the old max count -->
					<xsl:with-param name="max"
						select="($count > $max) * $count + not($count > $max) * $max"/>
					<!-- Compute the new mode as ... -->
					<xsl:with-param name="mode">
						<xsl:choose>
							<!-- the first element in try if we found a new max -->
							<xsl:when test="$count > $max">
								<xsl:copy-of select="$try[1]"/>
							</xsl:when>
							<!-- the old mode union the first element in try if we 
			found an equivalent count to current max -->
							<xsl:when test="$count = $max">
								<xsl:message>trouble?</xsl:message>
								<xsl:copy-of select="$mode | $try[1]"/>
							</xsl:when>
							<!-- othewise the old mode stays the same -->
							<xsl:otherwise>
								<xsl:copy-of select="$mode"/>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:with-param>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>


	<xsl:template name="math:stdDev">
		<xsl:param name="nodes"/>
		<xsl:param name="nodeCount" select="0"/>

		<xsl:if test="not($nodeCount > 1)">
			<xsl:value-of select="n/a"/>
		</xsl:if>

		<xsl:variable name="nodeSum" select="sum($nodes)"/>

		<xsl:variable name="sqrNode">
			<sqrs>
				<xsl:for-each select="$nodes">
					<sqr>
						<xsl:value-of select=". * ."/>
					</sqr>
				</xsl:for-each>
			</sqrs>
		</xsl:variable>

		<xsl:variable name="nodeSquaresSum" select="sum(xalan:nodeset($sqrNode)/sqrs/sqr)"/>

		<xsl:call-template name="decimalFormat">
			<xsl:with-param name="inputVal"
				select="sakaifn:sqrt(($nodeSquaresSum - ($nodeSum * $nodeSum div $nodeCount)) div ($nodeCount - 1))"
			/>
		</xsl:call-template>

	</xsl:template>



</xsl:stylesheet>
