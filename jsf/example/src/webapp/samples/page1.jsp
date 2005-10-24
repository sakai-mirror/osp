<f:view>
<sakai:view title="Demo Page">
<h:form>
<table>
<tr>
<td align="right">

    
<ospx:wizardSteps currentStep="2">
   <ospx:wizardStep label="Begin Design" />
   <ospx:wizardStep label="Design" />
   <ospx:wizardStep label="Support" />
   <ospx:wizardStep label="Workflow" />
   <ospx:wizardStep label="Properties" />
</ospx:wizardSteps>

</td>
</tr>
</table>
<ospx:splitarea direction="horizontal" height="600" width="100%">
    <ospx:splitsection cssclass="headclass" size="*" id="firstID" valign="top">
    
    
        <f:verbatim><style>
        .drawerBorder{ border-left: 5px solid #0000FF; border-bottom: 5px solid #0000FF; border-right: 5px solid #0000FF;}
        .selectWidth{width:100%;}
        .scrollStyle{background-color:#FFFFFF; border:1px solid #7777AA;}
        .tabSizeSmall{width:60px;}
        .tabSizeMid{width:120px;}
        </style></f:verbatim>
        <ospx:xheader>
            <ospx:xheadertitle id="mainTitleDIV">
                <h:outputText value="Portfolio: A Portfolio for something" />
                
                <h:commandButton value="Preview" alt="Preview" />
            </ospx:xheadertitle>
            <ospx:xheaderdrawer cssclass="drawerBorder">
                 <h:outputText value="This inner text is what goes into the under area." />
                 <h:panelGrid columns="3" styleClass="indnt2">
                    <h:outputText escape="false" value="<strong>Description</strong>
                       This Portfolio contains various assignments from
                       English 311 - Transcendetal American Literature.
                       <br/><strong>Keywords</strong>
                       Writing, Assignments, English 311, Transcendentalism,
                       American Literature"/>
                    <h:outputText escape="false" value="<strong>Layout:</strong> Portfolio Default
                       <br/><strong>Style:</strong> Portfolio Default
                       <br/><strong>Pages:</strong> 4
                       <br/><strong>Size:</strong> 4.5 mb
                       <br/><strong>Modified Date:</strong> August 10, 2005"/>
                    <h:commandButton value="Edit Info" type="button" style="act" />
                 </h:panelGrid>
            </ospx:xheaderdrawer>
        </ospx:xheader>
        <f:verbatim>
            <br />
            <span class="instruction">Customize the look of your portfolio<span>
        </f:verbatim>
        
        
        
        <ospx:tabArea direction="horizontal" width="100%">
           <ospx:tab title="Style" cssclass="tabSizeSmall">
              <h:outputText value="Here's some content for the first tab."/>
           </ospx:tab>
           <ospx:tab title="Layout" cssclass="tabSizeSmall" selected="true">
                <ospx:splitarea direction="vertical" width="100%">
                    <ospx:splitsection size="30">
                        <h:outputText value="Select Style from List for Preview" />
                    </ospx:splitsection>
                    <ospx:splitsection size="120" valign="top">
                    
                        <ospx:tabArea direction="vertical" width="100%">
                           <ospx:tab title="Recent Styles" cssclass="tabSizeMid">
                              <h:outputText value="Here's some content for the first tab."/>
                           </ospx:tab>
                           <ospx:tab title="My Styles" selected="true" cssclass="tabSizeMid">
                              <ospx:splitarea direction="vertical" width="100%">
                                  <ospx:splitsection valign="top" size="170">
                                      <h:outputText value="Item Name"/>
                                  </ospx:splitsection>
                                  <ospx:splitsection valign="top">
                                    <h:selectOneListbox id="pickStyle" size="5" styleClass="selectWidth">
                                        <f:selectItem itemValue="1" itemLabel="Course Information" />
                                        <f:selectItem itemValue="2" itemLabel="Paper" />
                                        <f:selectItem itemValue="3" itemLabel="Goals & Reflections" />
                                        <f:selectItem itemValue="4" itemLabel="Contact Info" />
                                        <f:selectItem itemValue="5" itemLabel="Emersonian-Nature" />
                                    </h:selectOneListbox>
                                  </ospx:splitsection>
                              </ospx:splitarea>
                              
                           </ospx:tab>
                           <ospx:tab title="Library Styles" cssclass="tabSizeMid">
                              <h:outputText value="Here's some content for the third tab."/>
                           </ospx:tab>
                        </ospx:tabArea>
                    
                    </ospx:splitsection>
                    <ospx:splitsection size="16">
                        <h:outputText value="Selected Style Preview" />
                    </ospx:splitsection>
                    <ospx:splitsection size="*">
                        
                        <ospx:scrollablearea height="330px" cssclass="scrollStyle">
                            <f:verbatim>
                            step 1<br>
                            step 2<br>
                            step 2<br>
                            step 2<br>
                            step 2<br>
                            step 2<br>
                            step 2<br>
                            step 2<br>
                            step 2<br>
                            step 2<br>
                            step 2<br>
                            step 2<br>
                            </f:verbatim>
                        </ospx:scrollablearea>

                    </ospx:splitsection>
                </ospx:splitarea>
           </ospx:tab>
           <ospx:tab title="Arrange" cssclass="tabSizeSmall">
              <h:outputText value="Here's some content for the third tab."/>
           </ospx:tab>
        </ospx:tabArea>
                
        
        
        
    </ospx:splitsection>
    <ospx:splitsection cssclass="tailclass" size="200" id="columnDataDIV" valign="top">
        
        
        <ospx:xheader>
            <ospx:xheadertitle id="portContentsDIV">
                <h:outputText value="Portfolio Contents" />
            </ospx:xheadertitle>
        </ospx:xheader>
        
        <ospx:splitarea direction="horizontal" width="100%">
            <ospx:splitsection>
                <h:outputText value="Items" />
            </ospx:splitsection>
            <ospx:splitsection align="right">
                <h:commandButton value="add" alt="add" styleClass="" />
                <h:commandButton value="remove" alt="remove" styleClass="" />
            </ospx:splitsection>
        </ospx:splitarea>
        
        <h:selectOneListbox id="pickItems" size="15" styleClass="selectWidth">
            <f:selectItem itemValue="coursesyllabus" itemLabel="Course Syllabus" />
            <f:selectItem itemValue="coursegoals" itemLabel="Course Goals" />
            <f:selectItem itemValue="whitman" itemLabel="Whitman" />
            <f:selectItem itemValue="coursereflection" itemLabel="Course Reflection" />
            <f:selectItem itemValue="enature" itemLabel="Emersonian-Nature" />
        </h:selectOneListbox> 
        <f:verbatim><br /></f:verbatim>
        
        <ospx:splitarea direction="horizontal" width="100%">
            <ospx:splitsection>
                <h:outputText value="Pages" />
            </ospx:splitsection>
            <ospx:splitsection align="right">
                <h:commandButton value="add" alt="add" styleClass="" />
                <h:commandButton value="remove" alt="remove" styleClass="" />
            </ospx:splitsection>
        </ospx:splitarea>
        
        <h:selectOneListbox id="pickPages" size="15" styleClass="selectWidth">
            <f:selectItem itemValue="1" itemLabel="Course Information" />
            <f:selectItem itemValue="2" itemLabel="Paper" />
            <f:selectItem itemValue="3" itemLabel="Goals & Reflections" />
            <f:selectItem itemValue="4" itemLabel="Contact Info" />
            <f:selectItem itemValue="5" itemLabel="Emersonian-Nature" />
        </h:selectOneListbox> 
        
    </ospx:splitsection>
</ospx:splitarea>
</h:form>
</sakai:view>
</f:view>