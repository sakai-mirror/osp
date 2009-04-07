delete from ONC.osp_workflow_item where workflow_id in (
select distinct id From ONC.OSP_WORKFLOW where title = 'Returned Workflow');

delete From ONC.OSP_WORKFLOW where title = 'Returned Workflow';


drop table ONC.osp_scaffolding_attachments;

drop table ONC.osp_scaffolding_form_defs;
    
drop table ONC.SITEASSOC_CONTEXT_ASSOCIATION;

drop table ONC.TAGGABLE_LINK;


ALTER TABLE ONC.osp_wizard_page_def drop column defaultCustomForm;
ALTER TABLE ONC.osp_wizard_page_def drop column defaultReflectionForm;
ALTER TABLE ONC.osp_wizard_page_def drop column defaultFeedbackForm;
ALTER TABLE ONC.osp_wizard_page_def drop column defaultReviewers;
ALTER TABLE ONC.osp_wizard_page_def drop column defaultEvaluationForm;
ALTER TABLE ONC.osp_wizard_page_def drop column defaultEvaluators;
ALTER TABLE ONC.osp_wizard_page_def drop column allowRequestFeedback;
ALTER TABLE ONC.osp_wizard_page_def drop column hideEvaluations;
ALTER TABLE ONC.osp_wizard_page_def drop column type;

ALTER TABLE ONC.osp_scaffolding drop column allowRequestFeedback;
ALTER TABLE ONC.osp_scaffolding drop column hideEvaluations;
ALTER TABLE ONC.osp_scaffolding drop column defaultFormsMatrixVersion;
ALTER TABLE ONC.osp_scaffolding drop column returnedColor;
ALTER TABLE ONC.osp_scaffolding drop column modifiedDate;

ALTER TABLE ONC.osp_scaffolding add (reviewerGroupAccess number(10,0));
UPDATE ONC.osp_scaffolding SET reviewerGroupAccess = 0;

delete from ONC.OSP_WORKFLOW_ITEM where workflow_id in (select id from ONC.osp_workflow where parent_id in (select id from ONC.osp_scaffolding));
delete from ONC.osp_workflow where parent_id in (select id from ONC.osp_scaffolding);
delete from ONC.osp_workflow_parent where id in (select id from ONC.osp_scaffolding);


create table ONC.tmp_realm_deletes (realm_id varchar2(99), realm_key INTEGER);

insert into ONC.tmp_realm_deletes (select realm_id, realm_key from ONC.sakai_realm where realm_id = '!matrix.template.portfolio');
insert into ONC.tmp_realm_deletes (select realm_id, realm_key from ONC.sakai_realm where realm_id = '!matrix.template.project');
insert into ONC.tmp_realm_deletes (select realm_id, realm_key from ONC.sakai_realm where realm_id = '!matrix.template.course');
insert into ONC.tmp_realm_deletes (select realm_id, realm_key from ONC.sakai_realm where realm_id like '/scaffolding/%');

delete from ONC.sakai_realm_rl_fn where realm_key in (select realm_key from ONC.tmp_realm_deletes);
delete from ONC.SAKAI_REALM_RL_GR where realm_key in (select realm_key from ONC.tmp_realm_deletes);
delete from ONC.SAKAI_REALM_ROLE_DESC where realm_key in (select realm_key from ONC.tmp_realm_deletes);
delete from ONC.sakai_realm where realm_key in (select realm_key from ONC.tmp_realm_deletes);

drop table ONC.tmp_realm_deletes;


update ONC.SAKAI_REALM_RL_FN set FUNCTION_KEY = (select function_key from ONC.SAKAI_REALM_FUNCTION where function_name = 'osp.matrix.evaluate')
where function_key = (select function_key From ONC.SAKAI_REALM_FUNCTION where function_name = 'osp.portfolio.evaluation.use');


create table ONC.tmp_perm_deletes (function_key INTEGER, function_name varchar2(99));

insert into ONC.tmp_perm_deletes (select function_key, function_name from ONC.sakai_realm_function where function_name = 'osp.matrix.scaffolding.revise.any');
insert into ONC.tmp_perm_deletes (select function_key, function_name from ONC.sakai_realm_function where function_name = 'osp.matrix.scaffolding.revise.own');
insert into ONC.tmp_perm_deletes (select function_key, function_name from ONC.sakai_realm_function where function_name = 'osp.matrix.scaffolding.delete.any');
insert into ONC.tmp_perm_deletes (select function_key, function_name from ONC.sakai_realm_function where function_name = 'osp.matrix.scaffolding.delete.own');
insert into ONC.tmp_perm_deletes (select function_key, function_name from ONC.sakai_realm_function where function_name = 'osp.matrix.scaffolding.publish.any');
insert into ONC.tmp_perm_deletes (select function_key, function_name from ONC.sakai_realm_function where function_name = 'osp.matrix.scaffolding.publish.own');
insert into ONC.tmp_perm_deletes (select function_key, function_name from ONC.sakai_realm_function where function_name = 'osp.matrix.scaffolding.export.any');
insert into ONC.tmp_perm_deletes (select function_key, function_name from ONC.sakai_realm_function where function_name = 'osp.matrix.scaffolding.export.own');

insert into ONC.tmp_perm_deletes (select function_key, function_name from ONC.sakai_realm_function where function_name = 'osp.matrix.scaffoldingSpecific.accessAll');
insert into ONC.tmp_perm_deletes (select function_key, function_name from ONC.sakai_realm_function where function_name = 'osp.matrix.scaffoldingSpecific.viewEvalOther');
insert into ONC.tmp_perm_deletes (select function_key, function_name from ONC.sakai_realm_function where function_name = 'osp.matrix.scaffoldingSpecific.viewFeedbackOther');
insert into ONC.tmp_perm_deletes (select function_key, function_name from ONC.sakai_realm_function where function_name = 'osp.matrix.scaffoldingSpecific.manageStatus');
insert into ONC.tmp_perm_deletes (select function_key, function_name from ONC.sakai_realm_function where function_name = 'osp.matrix.scaffoldingSpecific.accessUserList');
insert into ONC.tmp_perm_deletes (select function_key, function_name from ONC.sakai_realm_function where function_name = 'osp.matrix.scaffoldingSpecific.viewAllGroups');
insert into ONC.tmp_perm_deletes (select function_key, function_name from ONC.sakai_realm_function where function_name = 'osp.matrix.scaffoldingSpecific.use');

insert into ONC.tmp_perm_deletes (select function_key, function_name from ONC.sakai_realm_function where function_name = 'osp.portfolio.evaluation.use');

delete from ONC.sakai_realm_rl_fn where function_key in (select function_key from ONC.tmp_perm_deletes);
delete from ONC.sakai_realm_function where function_key in (select function_key from ONC.tmp_perm_deletes);

drop table ONC.tmp_perm_deletes;

delete from ONC.SAKAI_REALM_RL_FN where ROLE_KEY = (select role_key from ONC.SAKAI_REALM_ROLE where ROLE_NAME = 'reviewer');
delete from ONC.SAKAI_REALM_ROLE_DESC where ROLE_KEY = (select role_key from ONC.SAKAI_REALM_ROLE where ROLE_NAME = 'reviewer');
delete from ONC.SAKAI_REALM_ROLE where role_name = 'reviewer';


