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
    <ospx:splitsection cssclass="headclass" size="*" id="firstID">
    
    
        <f:verbatim><style>
        .drawerBorder{ border-left: 5px solid #0000FF; border-bottom: 5px solid #0000FF; border-right: 5px solid #0000FF;}
        .selectWidth{width:200px;}
        </style></f:verbatim>
        <ospx:xheader>
            <ospx:xheadertitle id="mainTitleDIV">
                <h:outputText value="Portfolio: A Portfolio for something" />
                
                <h:commandButton value="Preview" alt="Preview" />
            </ospx:xheadertitle>
            <ospx:xheaderdrawer id="mainDrawerDIV" cssclass="drawerBorder">
                
                
                
            </ospx:xheaderdrawer>
            
        </ospx:xheader>
    
    </ospx:splitsection>
    <ospx:splitsection cssclass="tailclass" size="200" id="columnDataDIV">
        
        
        <ospx:xheader>
            <ospx:xheadertitle id="portContentsDIV">
                <h:outputText value="Portfolio Contents" />
            </ospx:xheadertitle>
        </ospx:xheader>
        
        <h:outputText value="Items" />
        <h:commandButton value="add" alt="add" styleClass="" />
        <h:commandButton value="remove" alt="remove" styleClass="" />
        
        <f:verbatim><br /></f:verbatim>
        <h:selectOneListbox id="pickItems" size="15" styleClass="selectWidth">
            <f:selectItem itemValue="coursesyllabus" itemLabel="Course Syllabus" />
            <f:selectItem itemValue="coursegoals" itemLabel="Course Goals" />
            <f:selectItem itemValue="whitman" itemLabel="Whitman" />
            <f:selectItem itemValue="coursereflection" itemLabel="Course Reflection" />
            <f:selectItem itemValue="enature" itemLabel="Emersonian-Nature" />
        </h:selectOneListbox> 
        <f:verbatim><br /><br /></f:verbatim>
        
        <h:outputText value="Pages" />
        <h:commandButton value="add" alt="add" styleClass="" />
        <h:commandButton value="remove" alt="remove" styleClass="" />
        
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