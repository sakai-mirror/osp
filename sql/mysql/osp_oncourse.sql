ALTER TABLE osp_wizard_page_def ADD COLUMN (defaultCustomForm boolean, defaultReflectionForm boolean, defaultFeedbackForm boolean, defaultReviewers boolean, defaultEvaluationForm boolean, defaultEvaluators boolean);
UPDATE osp_wizard_page_def SET defaultCustomForm = false, defaultReflectionForm = false, defaultFeedbackForm = false, defaultReviewers = false, defaultEvaluationForm = false, defaultEvaluators = false;

ALTER TABLE osp_scaffolding ADD COLUMN (allowRequestFeedback boolean);
UPDATE osp_scaffolding SET allowRequestFeedback = false

ALTER TABLE osp_scaffolding ADD COLUMN (hideEvaluations boolean);
UPDATE osp_scaffolding SET hideEvaluations = false

ALTER TABLE osp_wizard_page_def ADD COLUMN (hideEvaluations boolean);
UPDATE osp_wizard_page_def SET hideEvaluations = false

ALTER TABLE osp_wizard_page_def ADD COLUMN (allowRequestFeedback boolean);
UPDATE osp_wizard_page_def SET allowRequestFeedback = false

ALTER TABLE osp_scaffolding Drop COLUMN reviewerGroupAccess;

ALTER TABLE osp_scaffolding ADD COLUMN (defaultFormsMatrixVersion boolean);
UPDATE osp_scaffolding SET defaultFormsMatrixVersion = true

alter table osp_wizard_page_def add column (type varchar(1) default '0');
--update osp_wizard_page_def as wpd set wpd.type = '1';
update osp_wizard_page_def as wpd set wpd.type = '0' where wpd.id in (
select distinct s.wiz_page_def_id From osp_scaffolding_cell s );

update osp_wizard_page_def as wpd set wpd.type = '1' where wpd.id in (
select distinct wps.wiz_page_def_id
From osp_wizard w
join osp_wizard_category wc on wc.wizard_id = w.id
join osp_wizard_page_sequence wps on wps.category_id = wc.id
where w.wizard_type = 'org.theospi.portfolio.wizard.model.Wizard.hierarchical'
);

update osp_wizard_page_def as wpd set wpd.type = '2' where wpd.id in (
select distinct wps.wiz_page_def_id 
From osp_wizard w
join osp_wizard_category wc on wc.wizard_id = w.id
join osp_wizard_page_sequence wps on wps.category_id = wc.id
where w.wizard_type = 'org.theospi.portfolio.wizard.model.Wizard.sequential'
);

update TAGGABLE_LINK set TAG_CRITERIA_REF = concat(TAG_CRITERIA_REF,  '/0') where TAG_CRITERIA_REF like '/ospWizPageDef/%';

--since scaffolding are now extending osp_workflow_parent
insert into OSP_WORKFLOW_PARENT select s.id, null, null, null, null, null, null from osp_scaffolding s where s.id not in (select wp.id from osp_workflow_parent wp);




-- Move the use permission from site to each newly created scaffolding realms and delete the old osp.matrix.scaffolding.use permissions --


INSERT INTO SAKAI_REALM (REALM_ID, PROVIDER_ID, MAINTAIN_ROLE, CREATEDBY, MODIFIEDBY, CREATEDON, MODIFIEDON) 
(select concat('/scaffolding/', concat(worksiteId, concat('/', id))) as new_realm_id, '', NULL, 'admin', 'admin', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP from osp_scaffolding);


insert into SAKAI_REALM_RL_FN (REALM_KEY, ROLE_KEY, FUNCTION_KEY)
select distinct sr.REALM_KEY, srrf.ROLE_KEY, (select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'osp.matrix.scaffoldingSpecific.use')
from sakai_realm sr, osp_scaffolding os, SAKAI_REALM_RL_FN srrf 
where sr.REALM_ID like concat('%', concat(os.id, '%')) 
and srrf.FUNCTION_KEY = (select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'osp.matrix.scaffolding.use')
and srrf.REALM_KEY = (select REALM_KEY from SAKAI_REALM Where REALM_ID = concat('/site/', os.worksiteid));

delete from SAKAI_REALM_RL_FN where function_key = (select function_key From SAKAI_REALM_FUNCTION where function_name like 'osp.matrix.scaffolding.use');

delete From SAKAI_REALM_FUNCTION where function_name like 'osp.matrix.scaffolding.use';


----- END ------


---- This needs to be run towards the end b/c it converts evaluate permission to a new permission ----
INSERT INTO sakai_25x.SAKAI_REALM_FUNCTION VALUES (DEFAULT, 'osp.portfolio.evaluation.use');

Update sakai_25x.SAKAI_REALM_RL_FN set FUNCTION_KEY = (select function_key From sakai_25x.SAKAI_REALM_FUNCTION where function_name like 'osp.portfolio.evaluation.use')
where function_key = (select function_key From sakai_25x.SAKAI_REALM_FUNCTION where function_name like 'osp.matrix.evaluate')
--- END ---


-------   These are the lines I removed to get rid of matrix evaluate and review permissions: ---------------

--INSERT INTO SAKAI_REALM_FUNCTION VALUES (DEFAULT, 'osp.matrix.evaluate');

--INSERT INTO SAKAI_REALM_FUNCTION VALUES (DEFAULT, 'osp.matrix.review');

--INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template'), (select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'), (select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'osp.matrix.review'));
--INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template'), (select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'), (select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'osp.matrix.review'));
--INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template.course'), (select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'Instructor'), (select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'osp.matrix.review'));
--INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template.course'), (select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'Instructor'), (select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'osp.matrix.review'));
--INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template.portfolio'), (select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'CIG Coordinator'), (select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'osp.matrix.review'));
--INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template.portfolio'), (select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'CIG Coordinator'), (select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'osp.matrix.review'));
--INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template.portfolio'), (select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'Reviewer'), (select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'osp.matrix.review'));
--INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template.portfolioAdmin'), (select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'Program Admin'), (select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'osp.matrix.review'));
--INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template.portfolioAdmin'), (select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'Program Admin'), (select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'osp.matrix.review'));
--INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template.portfolioAdmin'), (select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'Program Coordinator'), (select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'osp.matrix.review'));
--INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template.portfolioAdmin'), (select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'Program Coordinator'), (select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'osp.matrix.review'));
--INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!group.template'), (select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'), (select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'osp.matrix.review'));
--INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!group.template'), (select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'), (select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'osp.matrix.review'));
--INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!group.template.course'), (select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'Instructor'), (select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'osp.matrix.review'));
--INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!group.template.course'), (select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'Instructor'), (select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'osp.matrix.review'));
--INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!group.template.portfolio'), (select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'CIG Coordinator'), (select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'osp.matrix.review'));
--INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!group.template.portfolio'), (select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'CIG Coordinator'), (select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'osp.matrix.review'));
--INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!group.template.portfolio'), (select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'Reviewer'), (select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'osp.matrix.review'));
--INSERT INTO SAKAI_REALM_FUNCTION VALUES (DEFAULT, 'osp.matrix.review');





--INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template'), (select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'), (select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'osp.matrix.evaluate'));
--INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template.course'), (select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'Instructor'), (select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'osp.matrix.evaluate'));
--INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template.portfolio'), (select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'CIG Coordinator'), (select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'osp.matrix.evaluate'));
--INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template.portfolio'), (select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'Evaluator'), (select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'osp.matrix.evaluate'));
--INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template.portfolioAdmin'), (select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'Program Admin'), (select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'osp.matrix.evaluate'));
--INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template.portfolioAdmin'), (select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'Program Coordinator'), (select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'osp.matrix.evaluate'));
--INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!group.template'), (select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'), (select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'osp.matrix.evaluate'));
--INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!group.template.course'), (select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'Instructor'), (select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'osp.matrix.evaluate'));
--INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!group.template.portfolio'), (select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'CIG Coordinator'), (select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'osp.matrix.evaluate'));
--INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!group.template.portfolio'), (select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'Evaluator'), (select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'osp.matrix.evaluate'));

-------------   END PERMISSION CHANGES ---------------