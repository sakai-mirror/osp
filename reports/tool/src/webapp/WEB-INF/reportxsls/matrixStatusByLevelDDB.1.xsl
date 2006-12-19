<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    version="1.0">
    
<xsl:template match="/">
    <div>
    <h5>Matrix Cell Completion Status</h5>
    
    Matrix: <xsl:value-of select="//datarow[1]/element[@colName='title']/." />
   
    <div class="instruction">This data is only up to the date of the last data warehouse synchronization.</div>
    
    <h5>Users</h5>
    <table>

       <xsl:for-each select="//group[@by='userId']/datarow">
          <xsl:variable name = "varUserName" select = "element[@colName='userId']/." />
          
             <xsl:variable name = "varLevel" select = "element[@colName='level_sequence']" />
             
                   <tr class="exclude">
                      <td>
                         <xsl:if test="//parameters/parameter[@name='anonymize'] = '1'" >
                            Anonymous User
                         </xsl:if>
                         <xsl:if test="//parameters/parameter[@name='anonymize'] = '0'" >
                            <B><xsl:value-of select="$varUserName"/></B>
                         </xsl:if>
                      </td>

                      <xsl:for-each select="//group[@by='level_sequence']/datarow">
                            <td>
                               <xsl:value-of select="element[@colName='level_description']"/>
                            </td>
                      </xsl:for-each>
                   </tr>



                   <xsl:for-each select="//group[@by='criterion_sequence']/datarow">

                      <xsl:variable name = "varUserCriterion" select = "element[@colName='criterion_sequence']" />
                     
                         <tr>
                            <td>
                               <xsl:value-of select="element[@colName='criterion_description']"/>
                            </td>






						   <xsl:for-each select="//group[@by='level_sequence']/datarow">
							  <xsl:variable name = "varUserLevel" select = "element[@colName='level_sequence']" />
								<td>
									<xsl:value-of select="//data/datarow[element[@colName='level_sequence'] = $varUserLevel and 
																		 element[@colName='criterion_sequence'] = $varUserCriterion and 
																		 element[@colName='userId'] = $varUserName]/element[@colName='status']"/>
								</td>
						   </xsl:for-each>




                         </tr>
                   </xsl:for-each>
           
           
       </xsl:for-each>
    </table>

    </div>
</xsl:template>
</xsl:stylesheet>