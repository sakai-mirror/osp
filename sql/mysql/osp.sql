alter table osp_completed_wiz_category drop foreign key FK4EC54F7C6EA23D5D;
alter table osp_completed_wiz_category drop foreign key FK4EC54F7C21B27839;
alter table osp_completed_wiz_category drop foreign key FK4EC54F7CF992DFC3;
alter table osp_completed_wizard drop foreign key FKABC9DEB2D4C797;
alter table osp_completed_wizard drop foreign key FKABC9DEB2D62513B2;
alter table osp_completed_wizard_page drop foreign key FK52DE9BFCE4E7E6D3;
alter table osp_completed_wizard_page drop foreign key FK52DE9BFC2E24C4;
alter table osp_completed_wizard_page drop foreign key FK52DE9BFC473463E4;
alter table osp_guidance_item drop foreign key FK605DDBA737209105;
alter table osp_guidance_item_file drop foreign key FK29770314DB93091D;
alter table osp_matrix drop foreign key FK5A172054A6286438;
alter table osp_matrix_cell drop foreign key FK8C1D366DE4E7E6D3;
alter table osp_matrix_cell drop foreign key FK8C1D366D2D955C;
alter table osp_matrix_cell drop foreign key FK8C1D366DCD99D2B1;
alter table osp_pres_itemdef_mimetype drop foreign key FK9EA59837650346CA;
alter table osp_presentation drop foreign key FKA9028D6DFAEA67E8;
alter table osp_presentation drop foreign key FKA9028D6D6FE1417D;
alter table osp_presentation_comment drop foreign key FK1E7E658D7658ED43;
alter table osp_presentation_item drop foreign key FK2FA02A59165E3E4;
alter table osp_presentation_item drop foreign key FK2FA02A57658ED43;
alter table osp_presentation_item_def drop foreign key FK1B6ADB6B6FE1417D;
alter table osp_presentation_item_property drop foreign key FK86B1362FA9B15561;
alter table osp_presentation_log drop foreign key FK2120E1727658ED43;
alter table osp_presentation_page drop foreign key FK2FCEA217658ED43;
alter table osp_presentation_page drop foreign key FK2FCEA21FAEA67E8;
alter table osp_presentation_page drop foreign key FK2FCEA21533F283D;
alter table osp_presentation_page_item drop foreign key FK6417671954DB801;
alter table osp_presentation_page_region drop foreign key FK8A46C2D215C572B8;
alter table osp_reports_params drop foreign key FK231D4599C8A69327;
alter table osp_reports_results drop foreign key FKB1427243C8A69327;
alter table osp_scaffolding drop foreign key FK65135779FAEA67E8;
alter table osp_scaffolding_cell drop foreign key FK184EAE68A6286438;
alter table osp_scaffolding_cell drop foreign key FK184EAE689FECDBB8;
alter table osp_scaffolding_cell drop foreign key FK184EAE68754F20BD;
alter table osp_scaffolding_cell drop foreign key FK184EAE6870EDF97A;
alter table osp_scaffolding_cell_form_defs drop foreign key FK904DCA92754F20BD;
alter table osp_scaffolding_criteria drop foreign key FK8634116518C870CC;
alter table osp_scaffolding_criteria drop foreign key FK86341165A6286438;
alter table osp_scaffolding_levels drop foreign key FK4EBCD0F51EFC6CAF;
alter table osp_scaffolding_levels drop foreign key FK4EBCD0F5A6286438;
alter table osp_template_file_ref drop foreign key FK4B70FB026FE1417D;
alter table osp_wiz_page_attachment drop foreign key FK2257FCC9BDC195A7;
alter table osp_wiz_page_form drop foreign key FK4725E4EABDC195A7;
alter table osp_wizard drop foreign key FK6B9ACDFEE831DD1C;
alter table osp_wizard drop foreign key FK6B9ACDFEFAEA67E8;
alter table osp_wizard drop foreign key FK6B9ACDFEC73F84BD;
alter table osp_wizard_category drop foreign key FK3A81FE1FD62513B2;
alter table osp_wizard_category drop foreign key FK3A81FE1FE0EFF548;
alter table osp_wizard_page drop foreign key FK4CFB5C30754F20BD;
alter table osp_wizard_page_def drop foreign key FK6ABE7776FAEA67E8;
alter table osp_wizard_page_def drop foreign key FK6ABE7776C73F84BD;
alter table osp_wizard_page_sequence drop foreign key FKA5A702F06EA23D5D;
alter table osp_wizard_page_sequence drop foreign key FKA5A702F0754F20BD;
alter table osp_workflow drop foreign key FK2065879242A62872;
alter table osp_workflow_item drop foreign key FKB38697A091A4BC5E;
drop table if exists osp_authz_simple;
drop table if exists osp_completed_wiz_category;
drop table if exists osp_completed_wizard;
drop table if exists osp_completed_wizard_page;
drop table if exists osp_guidance;
drop table if exists osp_guidance_item;
drop table if exists osp_guidance_item_file;
drop table if exists osp_help_glossary;
drop table if exists osp_help_glossary_desc;
drop table if exists osp_list_config;
drop table if exists osp_matrix;
drop table if exists osp_matrix_cell;
drop table if exists osp_matrix_label;
drop table if exists osp_pres_itemdef_mimetype;
drop table if exists osp_presentation;
drop table if exists osp_presentation_comment;
drop table if exists osp_presentation_item;
drop table if exists osp_presentation_item_def;
drop table if exists osp_presentation_item_property;
drop table if exists osp_presentation_layout;
drop table if exists osp_presentation_log;
drop table if exists osp_presentation_page;
drop table if exists osp_presentation_page_item;
drop table if exists osp_presentation_page_region;
drop table if exists osp_presentation_template;
drop table if exists osp_reports;
drop table if exists osp_reports_params;
drop table if exists osp_reports_results;
drop table if exists osp_review;
drop table if exists osp_scaffolding;
drop table if exists osp_scaffolding_cell;
drop table if exists osp_scaffolding_cell_form_defs;
drop table if exists osp_scaffolding_criteria;
drop table if exists osp_scaffolding_levels;
drop table if exists osp_site_tool;
drop table if exists osp_style;
drop table if exists osp_template_file_ref;
drop table if exists osp_wiz_page_attachment;
drop table if exists osp_wiz_page_form;
drop table if exists osp_wizard;
drop table if exists osp_wizard_category;
drop table if exists osp_wizard_page;
drop table if exists osp_wizard_page_def;
drop table if exists osp_wizard_page_sequence;
drop table if exists osp_workflow;
drop table if exists osp_workflow_item;
drop table if exists osp_workflow_parent;
create table osp_authz_simple (id varchar(36) not null, qualifier_id varchar(255) not null, agent_id varchar(255) not null, function_name varchar(255) not null, primary key (id));
create table osp_completed_wiz_category (id varchar(36) not null, completed_wizard_id varchar(36), category_id varchar(36), expanded bit, seq_num integer, parent_category_id varchar(36), primary key (id));
create table osp_completed_wizard (id varchar(36) not null, owner_id varchar(255) not null, created datetime not null, lastVisited datetime not null, status varchar(255), wizard_id varchar(36), root_category varchar(36) unique, primary key (id));
create table osp_completed_wizard_page (id varchar(36) not null, completed_category_id varchar(36), wizard_page_def_id varchar(36), wizard_page_id varchar(36) unique, seq_num integer, created datetime not null, lastVisited datetime, primary key (id));
create table osp_guidance (id varchar(36) not null, description varchar(255), site_id varchar(36) not null, securityQualifier varchar(255), securityViewFunction varchar(255) not null, securityEditFunction varchar(255) not null, primary key (id));
create table osp_guidance_item (id varchar(36) not null, type varchar(255), text text, guidance_id varchar(36) not null, primary key (id));
create table osp_guidance_item_file (id varchar(36) not null, baseReference varchar(255), fullReference varchar(255), item_id varchar(36) not null, primary key (id));
create table osp_help_glossary (id varchar(36) not null, worksite_id varchar(255), term varchar(255) not null, description varchar(255) not null, primary key (id));
create table osp_help_glossary_desc (id varchar(36) not null, entry_id varchar(255), long_description text, primary key (id));
create table osp_list_config (id varchar(36) not null, owner_id varchar(255) not null, tool_id varchar(36), title varchar(255), height integer, numRows integer, selected_columns varchar(255) not null, primary key (id));
create table osp_matrix (id varchar(36) not null, owner varchar(255) not null, scaffolding_id varchar(36) not null, primary key (id));
create table osp_matrix_cell (id varchar(36) not null, matrix_id varchar(36) not null, wizard_page_id varchar(36) unique, scaffolding_cell_id varchar(36), primary key (id));
create table osp_matrix_label (id varchar(36) not null, type char(1) not null, description varchar(255), color varchar(7), textColor varchar(7), primary key (id));
create table osp_pres_itemdef_mimetype (item_def_id varchar(36) not null, primaryMimeType varchar(36), secondaryMimeType varchar(36));
create table osp_presentation (id varchar(36) not null, owner_id varchar(255) not null, template_id varchar(36) not null, name varchar(255), description varchar(255), isDefault bit, isPublic bit, presentationType varchar(255) not null, expiresOn datetime, created datetime not null, modified datetime not null, allowComments bit, site_id varchar(36) not null, properties blob, style_id varchar(36), advanced_navigation bit, tool_id varchar(36), primary key (id));
create table osp_presentation_comment (id varchar(36) not null, title varchar(255) not null, commentText text, creator_id varchar(255) not null, presentation_id varchar(36) not null, visibility tinyint not null, created datetime not null, primary key (id));
create table osp_presentation_item (presentation_id varchar(36) not null, artifact_id varchar(36) not null, item_definition_id varchar(36) not null, primary key (presentation_id, artifact_id, item_definition_id));
create table osp_presentation_item_def (id varchar(36) not null, name varchar(255), title varchar(255), description varchar(255), allowMultiple bit, type varchar(255), external_type varchar(255), sequence_no integer, template_id varchar(36) not null, primary key (id));
create table osp_presentation_item_property (id varchar(36) not null, presentation_page_item_id varchar(36) not null, property_key varchar(255) not null, property_value varchar(255), primary key (id));
create table osp_presentation_layout (id varchar(36) not null, name varchar(255) not null, description varchar(255), globalState integer not null, owner_id varchar(255) not null, created datetime not null, modified datetime not null, xhtml_file_id varchar(36) not null, preview_image_id varchar(36), tool_id varchar(36), site_id varchar(36), primary key (id));
create table osp_presentation_log (id varchar(36) not null, viewer_id varchar(255) not null, presentation_id varchar(36) not null, view_date datetime, primary key (id));
create table osp_presentation_page (id varchar(36) not null, title varchar(255), description varchar(255), keywords varchar(255), presentation_id varchar(36) not null, layout_id varchar(36) not null, style_id varchar(36), seq_num integer, created datetime not null, modified datetime not null, primary key (id));
create table osp_presentation_page_item (id varchar(36) not null, presentation_page_region_id varchar(36) not null, type varchar(255), value longtext, seq_num integer not null, primary key (id));
create table osp_presentation_page_region (id varchar(36) not null, presentation_page_id varchar(36) not null, region_id varchar(255) not null, type varchar(255), help_text varchar(255), primary key (id));
create table osp_presentation_template (id varchar(36) not null, name varchar(255), description varchar(255), includeHeaderAndFooter bit, includeComments bit, published bit, owner_id varchar(255) not null, renderer varchar(36), markup text, propertyPage varchar(36), documentRoot varchar(255), created datetime not null, modified datetime not null, site_id varchar(36) not null, primary key (id));
create table osp_reports (reportId varchar(36) not null, reportDefIdMark varchar(255), userId varchar(255), title varchar(255), keywords varchar(255), description varchar(255), isLive bit, creationDate datetime, type varchar(255), display bit, primary key (reportId));
create table osp_reports_params (paramId varchar(36) not null, reportId varchar(36), reportDefParamIdMark varchar(255), value varchar(255), primary key (paramId));
create table osp_reports_results (resultId varchar(36) not null, reportId varchar(36), userId varchar(255), title varchar(255), keywords varchar(255), description varchar(255), creationDate datetime, xml text, primary key (resultId));
create table osp_review (id varchar(36) not null, review_content_id varchar(36), site_id varchar(36) not null, parent_id varchar(36), review_device_id varchar(36), review_type integer not null, primary key (id));
create table osp_scaffolding (id varchar(36) not null, ownerId varchar(255) not null, title varchar(255), description text, worksiteId varchar(255), published bit, publishedBy varchar(255), publishedDate datetime, columnLabel varchar(255) not null, rowLabel varchar(255) not null, readyColor varchar(7) not null, pendingColor varchar(7) not null, completedColor varchar(7) not null, lockedColor varchar(7) not null, workflowOption integer not null, exposed_page_id varchar(36), style_id varchar(36), primary key (id));
create table osp_scaffolding_cell (id varchar(36) not null, rootcriterion_id varchar(36), level_id varchar(36), scaffolding_id varchar(36) not null, wiz_page_def_id varchar(36) unique, primary key (id));
create table osp_scaffolding_cell_form_defs (wiz_page_def_id varchar(36) not null, form_def_id varchar(255), seq_num integer not null, primary key (wiz_page_def_id, seq_num));
create table osp_scaffolding_criteria (scaffolding_id varchar(36) not null, elt varchar(36) not null, seq_num integer not null, primary key (scaffolding_id, seq_num));
create table osp_scaffolding_levels (scaffolding_id varchar(36) not null, elt varchar(36) not null, seq_num integer not null, primary key (scaffolding_id, seq_num));
create table osp_site_tool (id varchar(40) not null, site_id varchar(36), tool_id varchar(36), listener_id varchar(255), primary key (id));
create table osp_style (id varchar(36) not null, name varchar(255), description varchar(255), globalState integer not null, owner_id varchar(255) not null, style_file_id varchar(36), site_id varchar(36), created datetime not null, modified datetime not null, primary key (id));
create table osp_template_file_ref (id varchar(36) not null, file_id varchar(36), file_type_id varchar(36), usage_desc varchar(255), template_id varchar(36) not null, primary key (id));
create table osp_wiz_page_attachment (id varchar(36) not null, artifactId varchar(36), page_id varchar(36) not null, primary key (id));
create table osp_wiz_page_form (id varchar(36) not null, artifactId varchar(36), page_id varchar(36) not null, formType varchar(36), primary key (id));
create table osp_wizard (id varchar(36) not null, owner_id varchar(255) not null, name varchar(255), description text, keywords text, created datetime not null, modified datetime not null, site_id varchar(36) not null, guidance_id varchar(36), published bit, wizard_type varchar(255), style_id varchar(36), exposed_page_id varchar(36), root_category varchar(36) unique, seq_num integer, primary key (id));
create table osp_wizard_category (id varchar(36) not null, name varchar(255), description varchar(255), keywords varchar(255), created datetime not null, modified datetime not null, wizard_id varchar(36), parent_category_id varchar(36), seq_num integer, primary key (id));
create table osp_wizard_page (id varchar(36) not null, owner varchar(255) not null, status varchar(255), wiz_page_def_id varchar(36), modified datetime, primary key (id));
create table osp_wizard_page_def (id varchar(36) not null, initialStatus varchar(255), name varchar(255), description text, site_id varchar(255), guidance_id varchar(255), style_id varchar(36), primary key (id));
create table osp_wizard_page_sequence (id varchar(36) not null, seq_num integer, category_id varchar(36) not null, wiz_page_def_id varchar(36) unique, primary key (id));
create table osp_workflow (id varchar(36) not null, title varchar(255), parent_id varchar(36) not null, primary key (id));
create table osp_workflow_item (id varchar(36) not null, actionType integer not null, action_object_id varchar(255) not null, action_value varchar(255) not null, workflow_id varchar(36) not null, primary key (id));
create table osp_workflow_parent (id varchar(36) not null, reflection_device_id varchar(36), reflection_device_type varchar(255), evaluation_device_id varchar(36), evaluation_device_type varchar(255), review_device_id varchar(36), review_device_type varchar(255), primary key (id));
alter table osp_completed_wiz_category add index FK4EC54F7C6EA23D5D (category_id), add constraint FK4EC54F7C6EA23D5D foreign key (category_id) references osp_wizard_category (id);
alter table osp_completed_wiz_category add index FK4EC54F7C21B27839 (completed_wizard_id), add constraint FK4EC54F7C21B27839 foreign key (completed_wizard_id) references osp_completed_wizard (id);
alter table osp_completed_wiz_category add index FK4EC54F7CF992DFC3 (parent_category_id), add constraint FK4EC54F7CF992DFC3 foreign key (parent_category_id) references osp_completed_wiz_category (id);
alter table osp_completed_wizard add index FKABC9DEB2D4C797 (root_category), add constraint FKABC9DEB2D4C797 foreign key (root_category) references osp_completed_wiz_category (id);
alter table osp_completed_wizard add index FKABC9DEB2D62513B2 (wizard_id), add constraint FKABC9DEB2D62513B2 foreign key (wizard_id) references osp_wizard (id);
alter table osp_completed_wizard_page add index FK52DE9BFCE4E7E6D3 (wizard_page_id), add constraint FK52DE9BFCE4E7E6D3 foreign key (wizard_page_id) references osp_wizard_page (id);
alter table osp_completed_wizard_page add index FK52DE9BFC2E24C4 (wizard_page_def_id), add constraint FK52DE9BFC2E24C4 foreign key (wizard_page_def_id) references osp_wizard_page_sequence (id);
alter table osp_completed_wizard_page add index FK52DE9BFC473463E4 (completed_category_id), add constraint FK52DE9BFC473463E4 foreign key (completed_category_id) references osp_completed_wiz_category (id);
alter table osp_guidance_item add index FK605DDBA737209105 (guidance_id), add constraint FK605DDBA737209105 foreign key (guidance_id) references osp_guidance (id);
alter table osp_guidance_item_file add index FK29770314DB93091D (item_id), add constraint FK29770314DB93091D foreign key (item_id) references osp_guidance_item (id);
alter table osp_matrix add index FK5A172054A6286438 (scaffolding_id), add constraint FK5A172054A6286438 foreign key (scaffolding_id) references osp_scaffolding (id);
alter table osp_matrix_cell add index FK8C1D366DE4E7E6D3 (wizard_page_id), add constraint FK8C1D366DE4E7E6D3 foreign key (wizard_page_id) references osp_wizard_page (id);
alter table osp_matrix_cell add index FK8C1D366D2D955C (matrix_id), add constraint FK8C1D366D2D955C foreign key (matrix_id) references osp_matrix (id);
alter table osp_matrix_cell add index FK8C1D366DCD99D2B1 (scaffolding_cell_id), add constraint FK8C1D366DCD99D2B1 foreign key (scaffolding_cell_id) references osp_scaffolding_cell (id);
create index IDX_MATRIX_LABEL on osp_matrix_label (type);
alter table osp_pres_itemdef_mimetype add index FK9EA59837650346CA (item_def_id), add constraint FK9EA59837650346CA foreign key (item_def_id) references osp_presentation_item_def (id);
alter table osp_presentation add index FKA9028D6DFAEA67E8 (style_id), add constraint FKA9028D6DFAEA67E8 foreign key (style_id) references osp_style (id);
alter table osp_presentation add index FKA9028D6D6FE1417D (template_id), add constraint FKA9028D6D6FE1417D foreign key (template_id) references osp_presentation_template (id);
alter table osp_presentation_comment add index FK1E7E658D7658ED43 (presentation_id), add constraint FK1E7E658D7658ED43 foreign key (presentation_id) references osp_presentation (id);
alter table osp_presentation_item add index FK2FA02A59165E3E4 (item_definition_id), add constraint FK2FA02A59165E3E4 foreign key (item_definition_id) references osp_presentation_item_def (id);
alter table osp_presentation_item add index FK2FA02A57658ED43 (presentation_id), add constraint FK2FA02A57658ED43 foreign key (presentation_id) references osp_presentation (id);
alter table osp_presentation_item_def add index FK1B6ADB6B6FE1417D (template_id), add constraint FK1B6ADB6B6FE1417D foreign key (template_id) references osp_presentation_template (id);
alter table osp_presentation_item_property add index FK86B1362FA9B15561 (presentation_page_item_id), add constraint FK86B1362FA9B15561 foreign key (presentation_page_item_id) references osp_presentation_page_item (id);
alter table osp_presentation_log add index FK2120E1727658ED43 (presentation_id), add constraint FK2120E1727658ED43 foreign key (presentation_id) references osp_presentation (id);
alter table osp_presentation_page add index FK2FCEA217658ED43 (presentation_id), add constraint FK2FCEA217658ED43 foreign key (presentation_id) references osp_presentation (id);
alter table osp_presentation_page add index FK2FCEA21FAEA67E8 (style_id), add constraint FK2FCEA21FAEA67E8 foreign key (style_id) references osp_style (id);
alter table osp_presentation_page add index FK2FCEA21533F283D (layout_id), add constraint FK2FCEA21533F283D foreign key (layout_id) references osp_presentation_layout (id);
alter table osp_presentation_page_item add index FK6417671954DB801 (presentation_page_region_id), add constraint FK6417671954DB801 foreign key (presentation_page_region_id) references osp_presentation_page_region (id);
alter table osp_presentation_page_region add index FK8A46C2D215C572B8 (presentation_page_id), add constraint FK8A46C2D215C572B8 foreign key (presentation_page_id) references osp_presentation_page (id);
alter table osp_reports_params add index FK231D4599C8A69327 (reportId), add constraint FK231D4599C8A69327 foreign key (reportId) references osp_reports (reportId);
alter table osp_reports_results add index FKB1427243C8A69327 (reportId), add constraint FKB1427243C8A69327 foreign key (reportId) references osp_reports (reportId);
alter table osp_scaffolding add index FK65135779FAEA67E8 (style_id), add constraint FK65135779FAEA67E8 foreign key (style_id) references osp_style (id);
alter table osp_scaffolding_cell add index FK184EAE68A6286438 (scaffolding_id), add constraint FK184EAE68A6286438 foreign key (scaffolding_id) references osp_scaffolding (id);
alter table osp_scaffolding_cell add index FK184EAE689FECDBB8 (level_id), add constraint FK184EAE689FECDBB8 foreign key (level_id) references osp_matrix_label (id);
alter table osp_scaffolding_cell add index FK184EAE68754F20BD (wiz_page_def_id), add constraint FK184EAE68754F20BD foreign key (wiz_page_def_id) references osp_wizard_page_def (id);
alter table osp_scaffolding_cell add index FK184EAE6870EDF97A (rootcriterion_id), add constraint FK184EAE6870EDF97A foreign key (rootcriterion_id) references osp_matrix_label (id);
alter table osp_scaffolding_cell_form_defs add index FK904DCA92754F20BD (wiz_page_def_id), add constraint FK904DCA92754F20BD foreign key (wiz_page_def_id) references osp_wizard_page_def (id);
alter table osp_scaffolding_criteria add index FK8634116518C870CC (elt), add constraint FK8634116518C870CC foreign key (elt) references osp_matrix_label (id);
alter table osp_scaffolding_criteria add index FK86341165A6286438 (scaffolding_id), add constraint FK86341165A6286438 foreign key (scaffolding_id) references osp_scaffolding (id);
alter table osp_scaffolding_levels add index FK4EBCD0F51EFC6CAF (elt), add constraint FK4EBCD0F51EFC6CAF foreign key (elt) references osp_matrix_label (id);
alter table osp_scaffolding_levels add index FK4EBCD0F5A6286438 (scaffolding_id), add constraint FK4EBCD0F5A6286438 foreign key (scaffolding_id) references osp_scaffolding (id);
alter table osp_template_file_ref add index FK4B70FB026FE1417D (template_id), add constraint FK4B70FB026FE1417D foreign key (template_id) references osp_presentation_template (id);
alter table osp_wiz_page_attachment add index FK2257FCC9BDC195A7 (page_id), add constraint FK2257FCC9BDC195A7 foreign key (page_id) references osp_wizard_page (id);
alter table osp_wiz_page_form add index FK4725E4EABDC195A7 (page_id), add constraint FK4725E4EABDC195A7 foreign key (page_id) references osp_wizard_page (id);
alter table osp_wizard add index FK6B9ACDFEE831DD1C (root_category), add constraint FK6B9ACDFEE831DD1C foreign key (root_category) references osp_wizard_category (id);
alter table osp_wizard add index FK6B9ACDFEFAEA67E8 (style_id), add constraint FK6B9ACDFEFAEA67E8 foreign key (style_id) references osp_style (id);
alter table osp_wizard add index FK6B9ACDFEC73F84BD (id), add constraint FK6B9ACDFEC73F84BD foreign key (id) references osp_workflow_parent (id);
alter table osp_wizard_category add index FK3A81FE1FD62513B2 (wizard_id), add constraint FK3A81FE1FD62513B2 foreign key (wizard_id) references osp_wizard (id);
alter table osp_wizard_category add index FK3A81FE1FE0EFF548 (parent_category_id), add constraint FK3A81FE1FE0EFF548 foreign key (parent_category_id) references osp_wizard_category (id);
alter table osp_wizard_page add index FK4CFB5C30754F20BD (wiz_page_def_id), add constraint FK4CFB5C30754F20BD foreign key (wiz_page_def_id) references osp_wizard_page_def (id);
alter table osp_wizard_page_def add index FK6ABE7776FAEA67E8 (style_id), add constraint FK6ABE7776FAEA67E8 foreign key (style_id) references osp_style (id);
alter table osp_wizard_page_def add index FK6ABE7776C73F84BD (id), add constraint FK6ABE7776C73F84BD foreign key (id) references osp_workflow_parent (id);
alter table osp_wizard_page_sequence add index FKA5A702F06EA23D5D (category_id), add constraint FKA5A702F06EA23D5D foreign key (category_id) references osp_wizard_category (id);
alter table osp_wizard_page_sequence add index FKA5A702F0754F20BD (wiz_page_def_id), add constraint FKA5A702F0754F20BD foreign key (wiz_page_def_id) references osp_wizard_page_def (id);
alter table osp_workflow add index FK2065879242A62872 (parent_id), add constraint FK2065879242A62872 foreign key (parent_id) references osp_workflow_parent (id);
alter table osp_workflow_item add index FKB38697A091A4BC5E (workflow_id), add constraint FKB38697A091A4BC5E foreign key (workflow_id) references osp_workflow (id);
