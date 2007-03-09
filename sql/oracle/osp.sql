drop table osp_authz_simple cascade constraints;
drop table osp_completed_wiz_category cascade constraints;
drop table osp_completed_wizard cascade constraints;
drop table osp_completed_wizard_page cascade constraints;
drop table osp_guidance cascade constraints;
drop table osp_guidance_item cascade constraints;
drop table osp_guidance_item_file cascade constraints;
drop table osp_help_glossary cascade constraints;
drop table osp_help_glossary_desc cascade constraints;
drop table osp_list_config cascade constraints;
drop table osp_matrix cascade constraints;
drop table osp_matrix_cell cascade constraints;
drop table osp_matrix_label cascade constraints;
drop table osp_pres_itemdef_mimetype cascade constraints;
drop table osp_presentation cascade constraints;
drop table osp_presentation_comment cascade constraints;
drop table osp_presentation_item cascade constraints;
drop table osp_presentation_item_def cascade constraints;
drop table osp_presentation_item_property cascade constraints;
drop table osp_presentation_layout cascade constraints;
drop table osp_presentation_log cascade constraints;
drop table osp_presentation_page cascade constraints;
drop table osp_presentation_page_item cascade constraints;
drop table osp_presentation_page_region cascade constraints;
drop table osp_presentation_template cascade constraints;
drop table osp_reports cascade constraints;
drop table osp_reports_params cascade constraints;
drop table osp_reports_results cascade constraints;
drop table osp_review cascade constraints;
drop table osp_scaffolding cascade constraints;
drop table osp_scaffolding_cell cascade constraints;
drop table osp_scaffolding_cell_form_defs cascade constraints;
drop table osp_scaffolding_criteria cascade constraints;
drop table osp_scaffolding_levels cascade constraints;
drop table osp_site_tool cascade constraints;
drop table osp_style cascade constraints;
drop table osp_template_file_ref cascade constraints;
drop table osp_wiz_page_attachment cascade constraints;
drop table osp_wiz_page_form cascade constraints;
drop table osp_wizard cascade constraints;
drop table osp_wizard_category cascade constraints;
drop table osp_wizard_page cascade constraints;
drop table osp_wizard_page_def cascade constraints;
drop table osp_wizard_page_sequence cascade constraints;
drop table osp_workflow cascade constraints;
drop table osp_workflow_item cascade constraints;
drop table osp_workflow_parent cascade constraints;
create table osp_authz_simple (id varchar2(36 char) not null, qualifier_id varchar2(255 char) not null, agent_id varchar2(255 char) not null, function_name varchar2(255 char) not null, primary key (id));
create table osp_completed_wiz_category (id varchar2(36 char) not null, completed_wizard_id varchar2(36 char), category_id varchar2(36 char), expanded number(1,0), seq_num number(10,0), parent_category_id varchar2(36 char), primary key (id));
create table osp_completed_wizard (id varchar2(36 char) not null, owner_id varchar2(255 char) not null, created timestamp not null, lastVisited timestamp not null, status varchar2(255 char), wizard_id varchar2(36 char), root_category varchar2(36 char) unique, primary key (id));
create table osp_completed_wizard_page (id varchar2(36 char) not null, completed_category_id varchar2(36 char), wizard_page_def_id varchar2(36 char), wizard_page_id varchar2(36 char) unique, seq_num number(10,0), created timestamp not null, lastVisited timestamp, primary key (id));
create table osp_guidance (id varchar2(36 char) not null, description varchar2(255 char), site_id varchar2(36 char) not null, securityQualifier varchar2(255 char), securityViewFunction varchar2(255 char) not null, securityEditFunction varchar2(255 char) not null, primary key (id));
create table osp_guidance_item (id varchar2(36 char) not null, type varchar2(255 char), text clob, guidance_id varchar2(36 char) not null, primary key (id));
create table osp_guidance_item_file (id varchar2(36 char) not null, baseReference varchar2(255 char), fullReference varchar2(255 char), item_id varchar2(36 char) not null, primary key (id));
create table osp_help_glossary (id varchar2(36 char) not null, worksite_id varchar2(255 char), term varchar2(255 char) not null, description varchar2(255 char) not null, primary key (id));
create table osp_help_glossary_desc (id varchar2(36 char) not null, entry_id varchar2(255 char), long_description clob, primary key (id));
create table osp_list_config (id varchar2(36 char) not null, owner_id varchar2(255 char) not null, tool_id varchar2(36 char), title varchar2(255 char), height number(10,0), numRows number(10,0), selected_columns varchar2(255 char) not null, primary key (id));
create table osp_matrix (id varchar2(36 char) not null, owner varchar2(255 char) not null, scaffolding_id varchar2(36 char) not null, primary key (id));
create table osp_matrix_cell (id varchar2(36 char) not null, matrix_id varchar2(36 char) not null, wizard_page_id varchar2(36 char) unique, scaffolding_cell_id varchar2(36 char), primary key (id));
create table osp_matrix_label (id varchar2(36 char) not null, type char(1 char) not null, description varchar2(255 char), color varchar2(7 char), textColor varchar2(7 char), primary key (id));
create table osp_pres_itemdef_mimetype (item_def_id varchar2(36 char) not null, primaryMimeType varchar2(36 char), secondaryMimeType varchar2(36 char));
create table osp_presentation (id varchar2(36 char) not null, owner_id varchar2(255 char) not null, template_id varchar2(36 char) not null, name varchar2(255 char), description varchar2(255 char), isDefault number(1,0), isPublic number(1,0), presentationType varchar2(255 char) not null, expiresOn timestamp, created timestamp not null, modified timestamp not null, allowComments number(1,0), site_id varchar2(36 char) not null, properties blob, style_id varchar2(36 char), advanced_navigation number(1,0), tool_id varchar2(36 char), primary key (id));
create table osp_presentation_comment (id varchar2(36 char) not null, title varchar2(255 char) not null, commentText varchar2(1024 char), creator_id varchar2(255 char) not null, presentation_id varchar2(36 char) not null, visibility number(3,0) not null, created timestamp not null, primary key (id));
create table osp_presentation_item (presentation_id varchar2(36 char) not null, artifact_id varchar2(36 char) not null, item_definition_id varchar2(36 char) not null, primary key (presentation_id, artifact_id, item_definition_id));
create table osp_presentation_item_def (id varchar2(36 char) not null, name varchar2(255 char), title varchar2(255 char), description varchar2(255 char), allowMultiple number(1,0), type varchar2(255 char), external_type varchar2(255 char), sequence_no number(10,0), template_id varchar2(36 char) not null, primary key (id));
create table osp_presentation_item_property (id varchar2(36 char) not null, presentation_page_item_id varchar2(36 char) not null, property_key varchar2(255 char) not null, property_value varchar2(255 char), primary key (id));
create table osp_presentation_layout (id varchar2(36 char) not null, name varchar2(255 char) not null, description varchar2(255 char), globalState number(10,0) not null, owner_id varchar2(255 char) not null, created timestamp not null, modified timestamp not null, xhtml_file_id varchar2(36 char) not null, preview_image_id varchar2(36 char), tool_id varchar2(36 char), site_id varchar2(36 char), primary key (id));
create table osp_presentation_log (id varchar2(36 char) not null, viewer_id varchar2(255 char) not null, presentation_id varchar2(36 char) not null, view_date timestamp, primary key (id));
create table osp_presentation_page (id varchar2(36 char) not null, title varchar2(255 char), description varchar2(255 char), keywords varchar2(255 char), presentation_id varchar2(36 char) not null, layout_id varchar2(36 char) not null, style_id varchar2(36 char), seq_num number(10,0), created timestamp not null, modified timestamp not null, primary key (id));
create table osp_presentation_page_item (id varchar2(36 char) not null, presentation_page_region_id varchar2(36 char) not null, type varchar2(255 char), value long, seq_num number(10,0) not null, primary key (id));
create table osp_presentation_page_region (id varchar2(36 char) not null, presentation_page_id varchar2(36 char) not null, region_id varchar2(255 char) not null, type varchar2(255 char), help_text varchar2(255 char), primary key (id));
create table osp_presentation_template (id varchar2(36 char) not null, name varchar2(255 char), description varchar2(255 char), includeHeaderAndFooter number(1,0), published number(1,0), owner_id varchar2(255 char) not null, renderer varchar2(36 char), markup varchar2(4000 char), propertyPage varchar2(36 char), documentRoot varchar2(255 char), created timestamp not null, modified timestamp not null, site_id varchar2(36 char) not null, primary key (id));
create table osp_reports (reportId varchar2(36 char) not null, reportDefIdMark varchar2(255 char), userId varchar2(255 char), title varchar2(255 char), keywords varchar2(255 char), description varchar2(255 char), isLive number(1,0), creationDate timestamp, type varchar2(255 char), display number(1,0), primary key (reportId));
create table osp_reports_params (paramId varchar2(36 char) not null, reportId varchar2(36 char), reportDefParamIdMark varchar2(255 char), value varchar2(255 char), primary key (paramId));
create table osp_reports_results (resultId varchar2(36 char) not null, reportId varchar2(36 char), userId varchar2(255 char), title varchar2(255 char), keywords varchar2(255 char), description varchar2(255 char), creationDate timestamp, xml clob, primary key (resultId));
create table osp_review (id varchar2(36 char) not null, review_content_id varchar2(36 char), site_id varchar2(36 char) not null, parent_id varchar2(36 char), review_device_id varchar2(36 char), review_item_id varchar2(36 char), review_type number(10,0) not null, primary key (id));
create table osp_scaffolding (id varchar2(36 char) not null, ownerId varchar2(255 char) not null, title varchar2(255 char), description clob, worksiteId varchar2(255 char), preview number(1,0), published number(1,0), publishedBy varchar2(255 char), publishedDate timestamp, columnLabel varchar2(255 char) not null, rowLabel varchar2(255 char) not null, readyColor varchar2(7 char) not null, pendingColor varchar2(7 char) not null, completedColor varchar2(7 char) not null, lockedColor varchar2(7 char) not null, workflowOption number(10,0) not null, exposed_page_id varchar2(36 char), style_id varchar2(36 char), primary key (id));
create table osp_scaffolding_cell (id varchar2(36 char) not null, rootcriterion_id varchar2(36 char), level_id varchar2(36 char), scaffolding_id varchar2(36 char) not null, wiz_page_def_id varchar2(36 char) unique, primary key (id));
create table osp_scaffolding_cell_form_defs (wiz_page_def_id varchar2(36 char) not null, form_def_id varchar2(255 char), seq_num number(10,0) not null, primary key (wiz_page_def_id, seq_num));
create table osp_scaffolding_criteria (scaffolding_id varchar2(36 char) not null, elt varchar2(36 char) not null, seq_num number(10,0) not null, primary key (scaffolding_id, seq_num));
create table osp_scaffolding_levels (scaffolding_id varchar2(36 char) not null, elt varchar2(36 char) not null, seq_num number(10,0) not null, primary key (scaffolding_id, seq_num));
create table osp_site_tool (id varchar2(40 char) not null, site_id varchar2(36 char), tool_id varchar2(36 char), listener_id varchar2(255 char), primary key (id));
create table osp_style (id varchar2(36 char) not null, name varchar2(255 char), description varchar2(255 char), globalState number(10,0) not null, owner_id varchar2(255 char) not null, style_file_id varchar2(36 char), site_id varchar2(36 char), created timestamp not null, modified timestamp not null, primary key (id));
create table osp_template_file_ref (id varchar2(36 char) not null, file_id varchar2(36 char), file_type_id varchar2(36 char), usage_desc varchar2(255 char), template_id varchar2(36 char) not null, primary key (id));
create table osp_wiz_page_attachment (id varchar2(36 char) not null, artifactId varchar2(36 char), page_id varchar2(36 char) not null, primary key (id));
create table osp_wiz_page_form (id varchar2(36 char) not null, artifactId varchar2(36 char), page_id varchar2(36 char) not null, formType varchar2(36 char), primary key (id));
create table osp_wizard (id varchar2(36 char) not null, owner_id varchar2(255 char) not null, name varchar2(255 char), description varchar2(1024 char), keywords varchar2(1024 char), created timestamp not null, modified timestamp not null, site_id varchar2(36 char) not null, guidance_id varchar2(36 char), published number(1,0), preview number (1,0), wizard_type varchar2(255 char), style_id varchar2(36 char), exposed_page_id varchar2(36 char), root_category varchar2(36 char) unique, seq_num number(10,0), primary key (id));
create table osp_wizard_category (id varchar2(36 char) not null, name varchar2(255 char), description varchar2(255 char), keywords varchar2(255 char), created timestamp not null, modified timestamp not null, wizard_id varchar2(36 char), parent_category_id varchar2(36 char), seq_num number(10,0), primary key (id));
create table osp_wizard_page (id varchar2(36 char) not null, owner varchar2(255 char) not null, status varchar2(255 char), wiz_page_def_id varchar2(36 char), modified timestamp, primary key (id));
create table osp_wizard_page_def (id varchar2(36 char) not null, initialStatus varchar2(255 char), name varchar2(255 char), description clob, site_id varchar2(255 char), guidance_id varchar2(255 char), style_id varchar2(36 char), primary key (id));
create table osp_wizard_page_sequence (id varchar2(36 char) not null, seq_num number(10,0), category_id varchar2(36 char) not null, wiz_page_def_id varchar2(36 char) unique, primary key (id));
create table osp_workflow (id varchar2(36 char) not null, title varchar2(255 char), parent_id varchar2(36 char) not null, primary key (id));
create table osp_workflow_item (id varchar2(36 char) not null, actionType number(10,0) not null, action_object_id varchar2(255 char) not null, action_value varchar2(255 char) not null, workflow_id varchar2(36 char) not null, primary key (id));
create table osp_workflow_parent (id varchar2(36 char) not null, reflection_device_id varchar2(36 char), reflection_device_type varchar2(255 char), evaluation_device_id varchar2(36 char), evaluation_device_type varchar2(255 char), review_device_id varchar2(36 char), review_device_type varchar2(255 char), primary key (id));
alter table osp_completed_wiz_category add constraint FK4EC54F7C6EA23D5D foreign key (category_id) references osp_wizard_category;
alter table osp_completed_wiz_category add constraint FK4EC54F7C21B27839 foreign key (completed_wizard_id) references osp_completed_wizard;
alter table osp_completed_wiz_category add constraint FK4EC54F7CF992DFC3 foreign key (parent_category_id) references osp_completed_wiz_category;
alter table osp_completed_wizard add constraint FKABC9DEB2D4C797 foreign key (root_category) references osp_completed_wiz_category;
alter table osp_completed_wizard add constraint FKABC9DEB2D62513B2 foreign key (wizard_id) references osp_wizard;
alter table osp_completed_wizard_page add constraint FK52DE9BFCE4E7E6D3 foreign key (wizard_page_id) references osp_wizard_page;
alter table osp_completed_wizard_page add constraint FK52DE9BFC2E24C4 foreign key (wizard_page_def_id) references osp_wizard_page_sequence;
alter table osp_completed_wizard_page add constraint FK52DE9BFC473463E4 foreign key (completed_category_id) references osp_completed_wiz_category;
alter table osp_guidance_item add constraint FK605DDBA737209105 foreign key (guidance_id) references osp_guidance;
alter table osp_guidance_item_file add constraint FK29770314DB93091D foreign key (item_id) references osp_guidance_item;
alter table osp_matrix add constraint FK5A172054A6286438 foreign key (scaffolding_id) references osp_scaffolding;
alter table osp_matrix_cell add constraint FK8C1D366DE4E7E6D3 foreign key (wizard_page_id) references osp_wizard_page;
alter table osp_matrix_cell add constraint FK8C1D366D2D955C foreign key (matrix_id) references osp_matrix;
alter table osp_matrix_cell add constraint FK8C1D366DCD99D2B1 foreign key (scaffolding_cell_id) references osp_scaffolding_cell;
create index IDX_MATRIX_LABEL on osp_matrix_label (type);
alter table osp_pres_itemdef_mimetype add constraint FK9EA59837650346CA foreign key (item_def_id) references osp_presentation_item_def;
alter table osp_presentation add constraint FKA9028D6DFAEA67E8 foreign key (style_id) references osp_style;
alter table osp_presentation add constraint FKA9028D6D6FE1417D foreign key (template_id) references osp_presentation_template;
alter table osp_presentation_comment add constraint FK1E7E658D7658ED43 foreign key (presentation_id) references osp_presentation;
alter table osp_presentation_item add constraint FK2FA02A59165E3E4 foreign key (item_definition_id) references osp_presentation_item_def;
alter table osp_presentation_item add constraint FK2FA02A57658ED43 foreign key (presentation_id) references osp_presentation;
alter table osp_presentation_item_def add constraint FK1B6ADB6B6FE1417D foreign key (template_id) references osp_presentation_template;
alter table osp_presentation_item_property add constraint FK86B1362FA9B15561 foreign key (presentation_page_item_id) references osp_presentation_page_item;
alter table osp_presentation_log add constraint FK2120E1727658ED43 foreign key (presentation_id) references osp_presentation;
alter table osp_presentation_page add constraint FK2FCEA217658ED43 foreign key (presentation_id) references osp_presentation;
alter table osp_presentation_page add constraint FK2FCEA21FAEA67E8 foreign key (style_id) references osp_style;
alter table osp_presentation_page add constraint FK2FCEA21533F283D foreign key (layout_id) references osp_presentation_layout;
alter table osp_presentation_page_item add constraint FK6417671954DB801 foreign key (presentation_page_region_id) references osp_presentation_page_region;
alter table osp_presentation_page_region add constraint FK8A46C2D215C572B8 foreign key (presentation_page_id) references osp_presentation_page;
alter table osp_reports_params add constraint FK231D4599C8A69327 foreign key (reportId) references osp_reports;
alter table osp_reports_results add constraint FKB1427243C8A69327 foreign key (reportId) references osp_reports;
alter table osp_scaffolding add constraint FK65135779FAEA67E8 foreign key (style_id) references osp_style;
alter table osp_scaffolding_cell add constraint FK184EAE68A6286438 foreign key (scaffolding_id) references osp_scaffolding;
alter table osp_scaffolding_cell add constraint FK184EAE689FECDBB8 foreign key (level_id) references osp_matrix_label;
alter table osp_scaffolding_cell add constraint FK184EAE68754F20BD foreign key (wiz_page_def_id) references osp_wizard_page_def;
alter table osp_scaffolding_cell add constraint FK184EAE6870EDF97A foreign key (rootcriterion_id) references osp_matrix_label;
alter table osp_scaffolding_cell_form_defs add constraint FK904DCA92754F20BD foreign key (wiz_page_def_id) references osp_wizard_page_def;
alter table osp_scaffolding_criteria add constraint FK8634116518C870CC foreign key (elt) references osp_matrix_label;
alter table osp_scaffolding_criteria add constraint FK86341165A6286438 foreign key (scaffolding_id) references osp_scaffolding;
alter table osp_scaffolding_levels add constraint FK4EBCD0F51EFC6CAF foreign key (elt) references osp_matrix_label;
alter table osp_scaffolding_levels add constraint FK4EBCD0F5A6286438 foreign key (scaffolding_id) references osp_scaffolding;
alter table osp_template_file_ref add constraint FK4B70FB026FE1417D foreign key (template_id) references osp_presentation_template;
alter table osp_wiz_page_attachment add constraint FK2257FCC9BDC195A7 foreign key (page_id) references osp_wizard_page;
alter table osp_wiz_page_form add constraint FK4725E4EABDC195A7 foreign key (page_id) references osp_wizard_page;
alter table osp_wizard add constraint FK6B9ACDFEE831DD1C foreign key (root_category) references osp_wizard_category;
alter table osp_wizard add constraint FK6B9ACDFEFAEA67E8 foreign key (style_id) references osp_style;
alter table osp_wizard add constraint FK6B9ACDFEC73F84BD foreign key (id) references osp_workflow_parent;
alter table osp_wizard_category add constraint FK3A81FE1FD62513B2 foreign key (wizard_id) references osp_wizard;
alter table osp_wizard_category add constraint FK3A81FE1FE0EFF548 foreign key (parent_category_id) references osp_wizard_category;
alter table osp_wizard_page add constraint FK4CFB5C30754F20BD foreign key (wiz_page_def_id) references osp_wizard_page_def;
alter table osp_wizard_page_def add constraint FK6ABE7776FAEA67E8 foreign key (style_id) references osp_style;
alter table osp_wizard_page_def add constraint FK6ABE7776C73F84BD foreign key (id) references osp_workflow_parent;
alter table osp_wizard_page_sequence add constraint FKA5A702F06EA23D5D foreign key (category_id) references osp_wizard_category;
alter table osp_wizard_page_sequence add constraint FKA5A702F0754F20BD foreign key (wiz_page_def_id) references osp_wizard_page_def;
alter table osp_workflow add constraint FK2065879242A62872 foreign key (parent_id) references osp_workflow_parent;
alter table osp_workflow_item add constraint FKB38697A091A4BC5E foreign key (workflow_id) references osp_workflow;
