ALTER TABLE osp_wizard_page_def 
  ADD (defaultCustomForm number(1,0), defaultReflectionForm number(1,0), defaultFeedbackForm number(1,0), 
  defaultReviewers number(1,0), defaultEvaluationForm number(1,0), defaultEvaluators number(1,0));
UPDATE osp_wizard_page_def 
  SET defaultCustomForm = 0, defaultReflectionForm = 0, defaultFeedbackForm = 0, 
  defaultReviewers = 0, defaultEvaluationForm = 0, defaultEvaluators = 0;

ALTER TABLE osp_scaffolding ADD (allowRequestFeedback number(1,0));
UPDATE osp_scaffolding SET allowRequestFeedback = 0;

ALTER TABLE osp_scaffolding ADD (hideEvaluations number(1,0));
UPDATE osp_scaffolding SET hideEvaluations = 0;

ALTER TABLE osp_wizard_page_def ADD (allowRequestFeedback number(1,0));
UPDATE osp_wizard_page_def SET allowRequestFeedback = 0;

ALTER TABLE osp_wizard_page_def ADD (hideEvaluations number(1,0));
UPDATE osp_wizard_page_def SET hideEvaluations = 0;

ALTER TABLE osp_scaffolding Drop column reviewerGroupAccess;

ALTER TABLE osp_scaffolding ADD (defaultFormsMatrixVersion number(1,0));
UPDATE osp_scaffolding SET defaultFormsMatrixVersion = 1;

alter table osp_scaffolding add (returnedColor varchar2(7), modifiedDate timestamp);
update osp_scaffolding set returnedColor = '';

create table osp_scaffolding_attachments (
        id varchar2(36) not null,
        artifact_id varchar2(255),
        seq_num number(10,0) not null,
        primary key (id, seq_num)
    );

create table osp_scaffolding_form_defs (
        id varchar2(36) not null,
        form_def_id varchar2(255),
        seq_num number(10,0) not null,
        primary key (id, seq_num)
    );
    
create table SITEASSOC_CONTEXT_ASSOCIATION (
		FROM_CONTEXT varchar2(99 char) not null, 
		TO_CONTEXT varchar2(99 char) not null, 
		VERSION number(10,0) not null, 
		primary key (FROM_CONTEXT, TO_CONTEXT)
	);

alter table osp_wizard_page_def add (type varchar2(1) default '0');
--update osp_wizard_page_def as wpd set wpd.type = '1';
update osp_wizard_page_def set type = '0' where id in (
select distinct s.wiz_page_def_id From osp_scaffolding_cell s );

update osp_wizard_page_def set type = '1' where id in (
select distinct wps.wiz_page_def_id
From osp_wizard w
join osp_wizard_category wc on wc.wizard_id = w.id
join osp_wizard_page_sequence wps on wps.category_id = wc.id
where w.wizard_type = 'org.theospi.portfolio.wizard.model.Wizard.hierarchical'
);

update osp_wizard_page_def set type = '2' where id in (
select distinct wps.wiz_page_def_id 
From osp_wizard w
join osp_wizard_category wc on wc.wizard_id = w.id
join osp_wizard_page_sequence wps on wps.category_id = wc.id
where w.wizard_type = 'org.theospi.portfolio.wizard.model.Wizard.sequential'
);


CREATE TABLE TAGGABLE_LINK ( 
    LINK_ID           	VARCHAR2(36) NOT NULL,
    VERSION      	NUMBER(10,0) NOT NULL,
    ACTIVITY_REF 	VARCHAR2(255) NOT NULL,
    TAG_CRITERIA_REF      	VARCHAR2(255) NOT NULL,
    RUBRIC       	CLOB NULL,
    RATIONALE    	CLOB NULL,
    EXPORT_STRING	NUMBER(10,0) NOT NULL,
    VISIBLE      	NUMBER(1,0) NOT NULL,
    LOCKED       	NUMBER(1,0) DEFAULT 0 NULL,
    CONSTRAINT TAGGABLE_LINK_PK PRIMARY KEY(LINK_ID)
);


--since scaffolding are now extending osp_workflow_parent
insert into OSP_WORKFLOW_PARENT select s.id, null, null, null, null, null, null from osp_scaffolding s where s.id not in (select wp.id from osp_workflow_parent wp);

INSERT INTO SAKAI_REALM_ROLE VALUES (SAKAI_REALM_ROLE_SEQ.NEXTVAL, 'reviewer');
insert into SAKAI_REALM_ROLE_DESC values ((select realm_key from sakai_realm where REALM_ID = '!site.template.portfolio'), (select role_key from sakai_realm_role where role_name = 'reviewer'), 'A person who is a designated reviewer of matrices and wizards.', null);
insert into SAKAI_REALM_ROLE_DESC values ((select realm_key from sakai_realm where REALM_ID = '!group.template.portfolio'), (select role_key from sakai_realm_role where role_name = 'reviewer'), 'A person who is a designated reviewer of matrices and wizards.', null);


-- Move the use permission from site to each newly created scaffolding realms and delete the old osp.matrix.scaffolding.use permissions --

INSERT INTO SAKAI_REALM_FUNCTION VALUES (SAKAI_REALM_FUNCTION_SEQ.NEXTVAL, 'osp.matrix.scaffolding.revise.any');
INSERT INTO SAKAI_REALM_FUNCTION VALUES (SAKAI_REALM_FUNCTION_SEQ.NEXTVAL, 'osp.matrix.scaffolding.revise.own');
INSERT INTO SAKAI_REALM_FUNCTION VALUES (SAKAI_REALM_FUNCTION_SEQ.NEXTVAL, 'osp.matrix.scaffolding.delete.any');
INSERT INTO SAKAI_REALM_FUNCTION VALUES (SAKAI_REALM_FUNCTION_SEQ.NEXTVAL, 'osp.matrix.scaffolding.delete.own');
INSERT INTO SAKAI_REALM_FUNCTION VALUES (SAKAI_REALM_FUNCTION_SEQ.NEXTVAL, 'osp.matrix.scaffolding.publish.any');
INSERT INTO SAKAI_REALM_FUNCTION VALUES (SAKAI_REALM_FUNCTION_SEQ.NEXTVAL, 'osp.matrix.scaffolding.publish.own');
INSERT INTO SAKAI_REALM_FUNCTION VALUES (SAKAI_REALM_FUNCTION_SEQ.NEXTVAL, 'osp.matrix.scaffolding.export.any');
INSERT INTO SAKAI_REALM_FUNCTION VALUES (SAKAI_REALM_FUNCTION_SEQ.NEXTVAL, 'osp.matrix.scaffolding.export.own');

INSERT INTO SAKAI_REALM_FUNCTION VALUES (SAKAI_REALM_FUNCTION_SEQ.NEXTVAL, 'osp.matrix.scaffoldingSpecific.accessAll');
INSERT INTO SAKAI_REALM_FUNCTION VALUES (SAKAI_REALM_FUNCTION_SEQ.NEXTVAL, 'osp.matrix.scaffoldingSpecific.viewEvalOther');
INSERT INTO SAKAI_REALM_FUNCTION VALUES (SAKAI_REALM_FUNCTION_SEQ.NEXTVAL, 'osp.matrix.scaffoldingSpecific.viewFeedbackOther');
INSERT INTO SAKAI_REALM_FUNCTION VALUES (SAKAI_REALM_FUNCTION_SEQ.NEXTVAL, 'osp.matrix.scaffoldingSpecific.manageStatus');
INSERT INTO SAKAI_REALM_FUNCTION VALUES (SAKAI_REALM_FUNCTION_SEQ.NEXTVAL, 'osp.matrix.scaffoldingSpecific.accessUserList');
INSERT INTO SAKAI_REALM_FUNCTION VALUES (SAKAI_REALM_FUNCTION_SEQ.NEXTVAL, 'osp.matrix.scaffoldingSpecific.viewAllGroups');
INSERT INTO SAKAI_REALM_FUNCTION VALUES (SAKAI_REALM_FUNCTION_SEQ.NEXTVAL, 'osp.matrix.scaffoldingSpecific.use');

INSERT INTO SAKAI_REALM VALUES (SAKAI_REALM_SEQ.NEXTVAL, '!matrix.template.portfolio', '', NULL, 'admin', 'admin', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!matrix.template.portfolio'), (select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'coordinator'), (select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'osp.matrix.scaffoldingSpecific.accessAll'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!matrix.template.portfolio'), (select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'coordinator'), (select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'osp.matrix.scaffoldingSpecific.viewEvalOther'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!matrix.template.portfolio'), (select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'coordinator'), (select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'osp.matrix.scaffoldingSpecific.viewFeedbackOther'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!matrix.template.portfolio'), (select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'coordinator'), (select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'osp.matrix.scaffoldingSpecific.manageStatus'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!matrix.template.portfolio'), (select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'coordinator'), (select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'osp.matrix.scaffoldingSpecific.accessUserList'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!matrix.template.portfolio'), (select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'coordinator'), (select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'osp.matrix.scaffoldingSpecific.viewAllGroups'));

INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!matrix.template.portfolio'), (select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'assistant'), (select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'osp.matrix.scaffoldingSpecific.accessAll'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!matrix.template.portfolio'), (select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'assistant'), (select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'osp.matrix.scaffoldingSpecific.viewEvalOther'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!matrix.template.portfolio'), (select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'assistant'), (select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'osp.matrix.scaffoldingSpecific.viewFeedbackOther'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!matrix.template.portfolio'), (select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'assistant'), (select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'osp.matrix.scaffoldingSpecific.manageStatus'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!matrix.template.portfolio'), (select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'assistant'), (select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'osp.matrix.scaffoldingSpecific.accessUserList'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!matrix.template.portfolio'), (select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'assistant'), (select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'osp.matrix.scaffoldingSpecific.viewAllGroups'));

INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!matrix.template.portfolio'), (select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'reviewer'), (select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'osp.matrix.scaffoldingSpecific.accessUserList'));

INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!matrix.template.portfolio'), (select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'evaluator'), (select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'osp.matrix.scaffoldingSpecific.accessUserList'));

INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!matrix.template.portfolio'), (select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'participant'), (select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'osp.matrix.scaffoldingSpecific.use'));


INSERT INTO SAKAI_REALM VALUES (SAKAI_REALM_SEQ.NEXTVAL, '!matrix.template.course', '', NULL, 'admin', 'admin', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!matrix.template.course'), (select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'instructor'), (select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'osp.matrix.scaffoldingSpecific.accessAll'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!matrix.template.course'), (select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'instructor'), (select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'osp.matrix.scaffoldingSpecific.viewEvalOther'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!matrix.template.course'), (select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'instructor'), (select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'osp.matrix.scaffoldingSpecific.viewFeedbackOther'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!matrix.template.course'), (select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'instructor'), (select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'osp.matrix.scaffoldingSpecific.manageStatus'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!matrix.template.course'), (select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'instructor'), (select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'osp.matrix.scaffoldingSpecific.accessUserList'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!matrix.template.course'), (select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'instructor'), (select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'osp.matrix.scaffoldingSpecific.viewAllGroups'));

INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!matrix.template.course'), (select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'assistant'), (select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'osp.matrix.scaffoldingSpecific.accessAll'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!matrix.template.course'), (select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'assistant'), (select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'osp.matrix.scaffoldingSpecific.viewEvalOther'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!matrix.template.course'), (select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'assistant'), (select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'osp.matrix.scaffoldingSpecific.viewFeedbackOther'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!matrix.template.course'), (select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'assistant'), (select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'osp.matrix.scaffoldingSpecific.manageStatus'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!matrix.template.course'), (select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'assistant'), (select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'osp.matrix.scaffoldingSpecific.accessUserList'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!matrix.template.course'), (select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'assistant'), (select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'osp.matrix.scaffoldingSpecific.viewAllGroups'));

INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!matrix.template.course'), (select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'student'), (select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'osp.matrix.scaffoldingSpecific.use'));


INSERT INTO SAKAI_REALM VALUES (SAKAI_REALM_SEQ.NEXTVAL, '!matrix.template.project', '', NULL, 'admin', 'admin', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!matrix.template.project'), (select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'), (select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'osp.matrix.scaffoldingSpecific.accessAll'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!matrix.template.project'), (select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'), (select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'osp.matrix.scaffoldingSpecific.viewEvalOther'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!matrix.template.project'), (select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'), (select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'osp.matrix.scaffoldingSpecific.viewFeedbackOther'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!matrix.template.project'), (select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'), (select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'osp.matrix.scaffoldingSpecific.manageStatus'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!matrix.template.project'), (select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'), (select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'osp.matrix.scaffoldingSpecific.accessUserList'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!matrix.template.project'), (select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'), (select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'osp.matrix.scaffoldingSpecific.viewAllGroups'));

INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!matrix.template.project'), (select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'access'), (select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'osp.matrix.scaffoldingSpecific.use'));


INSERT INTO SAKAI_REALM (REALM_KEY, REALM_ID, PROVIDER_ID, MAINTAIN_ROLE, CREATEDBY, MODIFIEDBY, CREATEDON, MODIFIEDON) 
(select SAKAI_REALM_SEQ.NEXTVAL, concat('/scaffolding/', concat(worksiteId, concat('/', id))) as new_realm_id, '', NULL, 'admin', 'admin', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP from osp_scaffolding);


insert into SAKAI_REALM_RL_FN (REALM_KEY, ROLE_KEY, FUNCTION_KEY)
select distinct sr.REALM_KEY, srrf.ROLE_KEY, (select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'osp.matrix.scaffoldingSpecific.use')
from sakai_realm sr, osp_scaffolding os, SAKAI_REALM_RL_FN srrf 
where sr.REALM_ID = concat('/scaffolding/', concat(os.WORKSITEID, concat('/', os.id)))  
and srrf.FUNCTION_KEY = (select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'osp.matrix.scaffolding.use')
and srrf.REALM_KEY = (select REALM_KEY from SAKAI_REALM Where REALM_ID = concat('/site/', os.worksiteid));

--delete from SAKAI_REALM_RL_FN where function_key = (select function_key From SAKAI_REALM_FUNCTION where function_name = 'osp.matrix.scaffolding.use');

--delete From SAKAI_REALM_FUNCTION where function_name = 'osp.matrix.scaffolding.use';


create table permissions_backfill_src_temp (function_name varchar2(99), TYPE INTEGER);
CREATE TABLE permissions_backfill_temp (FUNCTION_KEY INTEGER, TYPE INTEGER);

INSERT INTO permissions_backfill_src_temp values ('osp.matrix.scaffoldingSpecific.accessAll', 1);
INSERT INTO permissions_backfill_src_temp values ('osp.matrix.scaffoldingSpecific.viewEvalOther', 1);
INSERT INTO permissions_backfill_src_temp values ('osp.matrix.scaffoldingSpecific.viewFeedbackOther', 1);
INSERT INTO permissions_backfill_src_temp values ('osp.matrix.scaffoldingSpecific.accessUserList', 1);
INSERT INTO permissions_backfill_src_temp values ('osp.matrix.scaffoldingSpecific.accessAll', 2);
INSERT INTO permissions_backfill_src_temp values ('osp.matrix.scaffoldingSpecific.viewFeedbackOther', 2);
INSERT INTO permissions_backfill_src_temp values ('osp.matrix.scaffoldingSpecific.accessUserList', 2);

insert into permissions_backfill_temp
select rf.function_key, pbst.type 
from SAKAI_REALM_FUNCTION rf
join permissions_backfill_src_temp pbst on (pbst.function_name = rf.FUNCTION_NAME);


insert into SAKAI_REALM_RL_FN (REALM_KEY, ROLE_KEY, FUNCTION_KEY)
select distinct sr.REALM_KEY, srrf.ROLE_KEY, pbt.FUNCTION_KEY
from sakai_realm sr, osp_scaffolding os, SAKAI_REALM_RL_FN srrf, permissions_backfill_temp pbt
where sr.REALM_ID = concat('/scaffolding/', concat(os.WORKSITEID, concat('/', os.id))) 
and srrf.FUNCTION_KEY = (select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'osp.matrix.evaluate')
and srrf.REALM_KEY = (select REALM_KEY from SAKAI_REALM Where REALM_ID = concat('/site/', os.worksiteid))
and pbt.TYPE = 1
and not exists (select 1 from SAKAI_REALM_RL_FN rrf_tmp where rrf_tmp.REALM_KEY = sr.REALM_KEY and rrf_tmp.ROLE_KEY = srrf.ROLE_KEY and rrf_tmp.FUNCTION_KEY = pbt.FUNCTION_KEY);

insert into SAKAI_REALM_RL_FN (REALM_KEY, ROLE_KEY, FUNCTION_KEY)
select distinct sr.REALM_KEY, srrf.ROLE_KEY, pbt.FUNCTION_KEY
from sakai_realm sr, osp_scaffolding os, SAKAI_REALM_RL_FN srrf, permissions_backfill_temp pbt
where sr.REALM_ID = concat('/scaffolding/', concat(os.WORKSITEID, concat('/', os.id))) 
and srrf.FUNCTION_KEY = (select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'osp.matrix.review')
and srrf.REALM_KEY = (select REALM_KEY from SAKAI_REALM Where REALM_ID = concat('/site/', os.worksiteid))
and pbt.TYPE = 2
and not exists (select 1 from SAKAI_REALM_RL_FN rrf_tmp where rrf_tmp.REALM_KEY = sr.REALM_KEY and rrf_tmp.ROLE_KEY = srrf.ROLE_KEY and rrf_tmp.FUNCTION_KEY = pbt.FUNCTION_KEY);

drop table permissions_backfill_src_temp;
drop table permissions_backfill_temp;


----- END ------



CREATE TABLE PERMISSIONS_SRC_TEMP (ROLE_NAME VARCHAR(99), FUNCTION_NAME VARCHAR(99));
CREATE TABLE PERMISSIONS_TEMP (ROLE_KEY INTEGER, FUNCTION_KEY INTEGER);

-- Backfill sites

INSERT INTO PERMISSIONS_SRC_TEMP values ('instructor','osp.matrix.scaffoldingSpecific.manageStatus');
INSERT INTO PERMISSIONS_SRC_TEMP values ('instructor','osp.matrix.scaffoldingSpecific.viewAllGroups');

INSERT INTO PERMISSIONS_SRC_TEMP values ('coordinator','osp.matrix.scaffoldingSpecific.manageStatus');
INSERT INTO PERMISSIONS_SRC_TEMP values ('coordinator','osp.matrix.scaffoldingSpecific.viewAllGroups');
INSERT INTO PERMISSIONS_SRC_TEMP values ('Coordinator','osp.matrix.scaffoldingSpecific.manageStatus');
INSERT INTO PERMISSIONS_SRC_TEMP values ('Coordinator','osp.matrix.scaffoldingSpecific.viewAllGroups');
INSERT INTO PERMISSIONS_SRC_TEMP values ('CIG Coordinator','osp.matrix.scaffoldingSpecific.manageStatus');
INSERT INTO PERMISSIONS_SRC_TEMP values ('CIG Coordinator','osp.matrix.scaffoldingSpecific.viewAllGroups');

INSERT INTO PERMISSIONS_SRC_TEMP values ('project owner','osp.matrix.scaffoldingSpecific.manageStatus');
INSERT INTO PERMISSIONS_SRC_TEMP values ('project owner','osp.matrix.scaffoldingSpecific.viewAllGroups');

INSERT INTO PERMISSIONS_SRC_TEMP values ('assistant','osp.matrix.scaffoldingSpecific.manageStatus');
INSERT INTO PERMISSIONS_SRC_TEMP values ('assistant','osp.matrix.scaffoldingSpecific.viewAllGroups');
INSERT INTO PERMISSIONS_SRC_TEMP values ('Assistant','osp.matrix.scaffoldingSpecific.manageStatus');
INSERT INTO PERMISSIONS_SRC_TEMP values ('Assistant','osp.matrix.scaffoldingSpecific.viewAllGroups');

INSERT INTO PERMISSIONS_SRC_TEMP values ('maintain','osp.matrix.scaffoldingSpecific.manageStatus');
INSERT INTO PERMISSIONS_SRC_TEMP values ('maintain','osp.matrix.scaffoldingSpecific.viewAllGroups');

INSERT INTO PERMISSIONS_SRC_TEMP values ('AI/TA','osp.matrix.scaffoldingSpecific.manageStatus');
INSERT INTO PERMISSIONS_SRC_TEMP values ('AI/TA','osp.matrix.scaffoldingSpecific.viewAllGroups');


-- Lookup the role and function keys
insert into PERMISSIONS_TEMP (ROLE_KEY, FUNCTION_KEY)
select SRR.ROLE_KEY, SRF.FUNCTION_KEY
from PERMISSIONS_SRC_TEMP TMPSRC
join SAKAI_REALM_ROLE SRR on (TMPSRC.ROLE_NAME = SRR.ROLE_NAME)
join SAKAI_REALM_FUNCTION SRF on (TMPSRC.FUNCTION_NAME = SRF.FUNCTION_NAME);


-- Insert the new functions into the roles of any existing realm that has the role (don't convert the "!site.helper" or any group realms)
insert into SAKAI_REALM_RL_FN (REALM_KEY, ROLE_KEY, FUNCTION_KEY)
select
    SRRFD.REALM_KEY, SRRFD.ROLE_KEY, TMP.FUNCTION_KEY
from
    (select distinct SRRF.REALM_KEY, SRRF.ROLE_KEY from SAKAI_REALM_RL_FN SRRF) SRRFD
    join PERMISSIONS_TEMP TMP on (SRRFD.ROLE_KEY = TMP.ROLE_KEY)
    join SAKAI_REALM SR on (SRRFD.REALM_KEY = SR.REALM_KEY)
    where SR.REALM_ID like '/scaffolding/%'
   and not exists (
        select 1
            from SAKAI_REALM_RL_FN SRRFI
            where SRRFI.REALM_KEY=SRRFD.REALM_KEY and SRRFI.ROLE_KEY=SRRFD.ROLE_KEY and  SRRFI.FUNCTION_KEY=TMP.FUNCTION_KEY
    );


-- clean up the temp tables to use again for group permissions
drop table PERMISSIONS_TEMP;
drop table PERMISSIONS_SRC_TEMP;


CREATE TABLE permissions_convertl_temp (OLD_FUNCTION_KEY INTEGER, OLD_FUNCTION_NAME VARCHAR2(99), FUNCTION_KEY INTEGER, FUNCTION_NAME VARCHAR2(99));

INSERT INTO permissions_convertl_temp
select rf.FUNCTION_KEY, rf.FUNCTION_NAME, rf2.FUNCTION_KEY, rf2.FUNCTION_NAME 
from SAKAI_REALM_FUNCTION rf, SAKAI_REALM_FUNCTION rf2 
where rf.FUNCTION_NAME = 'osp.matrix.scaffolding.edit' and (rf2.function_name = 'osp.matrix.scaffolding.revise.any' or rf2.function_name = 'osp.matrix.scaffolding.revise.own');

INSERT INTO permissions_convertl_temp
select rf.FUNCTION_KEY, rf.FUNCTION_NAME, rf2.FUNCTION_KEY, rf2.FUNCTION_NAME 
from SAKAI_REALM_FUNCTION rf, SAKAI_REALM_FUNCTION rf2 
where rf.FUNCTION_NAME = 'osp.matrix.scaffolding.delete' and (rf2.function_name = 'osp.matrix.scaffolding.delete.any' or rf2.function_name = 'osp.matrix.scaffolding.delete.own');

INSERT INTO permissions_convertl_temp
select rf.FUNCTION_KEY, rf.FUNCTION_NAME, rf2.FUNCTION_KEY, rf2.FUNCTION_NAME 
from SAKAI_REALM_FUNCTION rf, SAKAI_REALM_FUNCTION rf2 
where rf.FUNCTION_NAME = 'osp.matrix.scaffolding.export' and (rf2.function_name = 'osp.matrix.scaffolding.export.any' or rf2.function_name = 'osp.matrix.scaffolding.export.own');

INSERT INTO permissions_convertl_temp
select rf.FUNCTION_KEY, rf.FUNCTION_NAME, rf2.FUNCTION_KEY, rf2.FUNCTION_NAME 
from SAKAI_REALM_FUNCTION rf, SAKAI_REALM_FUNCTION rf2 
where rf.FUNCTION_NAME = 'osp.matrix.scaffolding.publish' and (rf2.function_name = 'osp.matrix.scaffolding.publish.any' or rf2.function_name = 'osp.matrix.scaffolding.publish.own');



insert into SAKAI_REALM_RL_FN (REALM_KEY, ROLE_KEY, FUNCTION_KEY)
select distinct srrf.REALM_KEY, srrf.ROLE_KEY, pct.function_key
from SAKAI_REALM_RL_FN srrf
join permissions_convertl_temp pct on (srrf.FUNCTION_KEY = pct.old_function_key);

drop table permissions_convertl_temp;

INSERT INTO SAKAI_REALM_FUNCTION VALUES (SAKAI_REALM_FUNCTION_SEQ.NEXTVAL, 'osp.portfolio.evaluation.use');

update SAKAI_REALM_RL_FN set FUNCTION_KEY = (select function_key from SAKAI_REALM_FUNCTION where function_name = 'osp.portfolio.evaluation.use')
where function_key = (select function_key From SAKAI_REALM_FUNCTION where function_name = 'osp.matrix.evaluate');


--default perms for reviewer role
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template.portfolio'), (select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'reviewer'), (select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'annc.read'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template.portfolio'), (select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'reviewer'), (select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'asn.read'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template.portfolio'), (select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'reviewer'), (select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'asn.submit'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template.portfolio'), (select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'reviewer'), (select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'assessment.submitAssessmentForGrade'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template.portfolio'), (select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'reviewer'), (select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'assessment.takeAssessment'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template.portfolio'), (select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'reviewer'), (select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'calendar.read'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template.portfolio'), (select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'reviewer'), (select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'chat.new'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template.portfolio'), (select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'reviewer'), (select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'chat.read'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template.portfolio'), (select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'reviewer'), (select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'content.read'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template.portfolio'), (select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'reviewer'), (select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'disc.new'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template.portfolio'), (select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'reviewer'), (select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'disc.read'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template.portfolio'), (select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'reviewer'), (select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'disc.revise.own'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template.portfolio'), (select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'reviewer'), (select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'dropbox.own'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template.portfolio'), (select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'reviewer'), (select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'mail.read'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template.portfolio'), (select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'reviewer'), (select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'reports.run'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template.portfolio'), (select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'reviewer'), (select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'reports.view'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template.portfolio'), (select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'reviewer'), (select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'rwiki.create'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template.portfolio'), (select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'reviewer'), (select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'rwiki.read'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template.portfolio'), (select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'reviewer'), (select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'rwiki.update'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template.portfolio'), (select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'reviewer'), (select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'section.role.student'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template.portfolio'), (select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'reviewer'), (select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'site.visit'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template.portfolio'), (select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'reviewer'), (select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'osp.presentation.copy'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template.portfolio'), (select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'reviewer'), (select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'osp.presentation.comment'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template.portfolio'), (select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'reviewer'), (select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'osp.wizard.review'));

INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!group.template.portfolio'), (select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'reviewer'), (select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'annc.read'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!group.template.portfolio'), (select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'reviewer'), (select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'asn.read'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!group.template.portfolio'), (select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'reviewer'), (select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'asn.submit'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!group.template.portfolio'), (select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'reviewer'), (select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'assessment.submitAssessmentForGrade'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!group.template.portfolio'), (select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'reviewer'), (select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'assessment.takeAssessment'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!group.template.portfolio'), (select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'reviewer'), (select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'calendar.read'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!group.template.portfolio'), (select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'reviewer'), (select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'chat.new'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!group.template.portfolio'), (select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'reviewer'), (select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'chat.read'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!group.template.portfolio'), (select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'reviewer'), (select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'content.read'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!group.template.portfolio'), (select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'reviewer'), (select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'disc.new'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!group.template.portfolio'), (select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'reviewer'), (select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'disc.read'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!group.template.portfolio'), (select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'reviewer'), (select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'disc.revise.own'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!group.template.portfolio'), (select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'reviewer'), (select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'dropbox.own'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!group.template.portfolio'), (select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'reviewer'), (select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'mail.read'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!group.template.portfolio'), (select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'reviewer'), (select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'reports.run'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!group.template.portfolio'), (select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'reviewer'), (select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'reports.view'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!group.template.portfolio'), (select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'reviewer'), (select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'rwiki.create'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!group.template.portfolio'), (select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'reviewer'), (select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'rwiki.read'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!group.template.portfolio'), (select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'reviewer'), (select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'rwiki.update'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!group.template.portfolio'), (select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'reviewer'), (select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'section.role.student'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!group.template.portfolio'), (select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'reviewer'), (select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'site.visit'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!group.template.portfolio'), (select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'reviewer'), (select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'osp.presentation.copy'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!group.template.portfolio'), (select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'reviewer'), (select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'osp.presentation.comment'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!group.template.portfolio'), (select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'reviewer'), (select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'osp.wizard.review'));

--update TAGGABLE_LINK set TAG_CRITERIA_REF = concat(TAG_CRITERIA_REF,  '/0') where TAG_CRITERIA_REF like '/ospWizPageDef/%'



create public synonym osp_scaffolding_attachments for osp_scaffolding_attachments;
grant select,update,insert,delete on osp_scaffolding_attachments to onc_proxy;
grant select on osp_scaffolding_attachments to onc_select;

create public synonym osp_scaffolding_form_defs for osp_scaffolding_form_defs;
grant select,update,insert,delete on osp_scaffolding_form_defs to onc_proxy;
grant select on osp_scaffolding_form_defs to onc_select;

create public synonym SITEASSOC_CONTEXT_ASSOCIATION for SITEASSOC_CONTEXT_ASSOCIATION;
grant select,update,insert,delete on SITEASSOC_CONTEXT_ASSOCIATION to onc_proxy;
grant select on SITEASSOC_CONTEXT_ASSOCIATION to onc_select;

create public synonym TAGGABLE_LINK for TAGGABLE_LINK;
grant select,update,insert,delete on TAGGABLE_LINK to onc_proxy;
grant select on TAGGABLE_LINK to onc_select;


--****** backfill for new returned status
create table tmp_workflow_guid_map (old_id varchar2(99), new_id varchar2(99), 
  parent_id varchar2(99), old_status varchar2(99), new_status varchar2(99));

insert into tmp_workflow_guid_map select id, sys_guid(), parent_id, 'READY', 'RETURNED' from OSP_WORKFLOW where title = 'Return Workflow';

delete From tmp_workflow_guid_map where PARENT_ID in (
select distinct parent_id from osp_workflow where title = 'Returned Workflow');

insert into osp_workflow
select NEW_ID, 'Returned Workflow', parent_id from tmp_workflow_guid_map;

insert into osp_workflow_item
select sys_guid(), owi.actiontype, owi.action_object_id, t.new_status, t.new_id 
from osp_workflow_item owi
join tmp_workflow_guid_map t on (t.old_id = owi.WORKFLOW_ID and t.old_status = owi.ACTION_VALUE);

insert into osp_workflow_item
select sys_guid(), owi.actiontype, owi.action_object_id, owi.action_value, t.new_id 
from osp_workflow_item owi
join tmp_workflow_guid_map t on (t.old_id = owi.WORKFLOW_ID)
where t.old_status <> owi.ACTION_VALUE;

drop table tmp_workflow_guid_map;
--****** end backfill for returned status


--******* Backfill for reviewer role
CREATE TABLE reviewer_backfill_src (ROLE_NAME VARCHAR(99), FUNCTION_NAME VARCHAR(99));
CREATE TABLE reviewer_backfill (ROLE_KEY INTEGER, FUNCTION_KEY INTEGER);


INSERT INTO reviewer_backfill_src values ('reviewer','annc.read');
INSERT INTO reviewer_backfill_src values ('reviewer','asn.read');
INSERT INTO reviewer_backfill_src values ('reviewer','asn.submit');
INSERT INTO reviewer_backfill_src values ('reviewer','assessment.submitAssessmentForGrade');
INSERT INTO reviewer_backfill_src values ('reviewer','assessment.takeAssessment');
INSERT INTO reviewer_backfill_src values ('reviewer','calendar.read');
INSERT INTO reviewer_backfill_src values ('reviewer','chat.new');
INSERT INTO reviewer_backfill_src values ('reviewer','chat.read');
INSERT INTO reviewer_backfill_src values ('reviewer','content.read');
INSERT INTO reviewer_backfill_src values ('reviewer','disc.new');
INSERT INTO reviewer_backfill_src values ('reviewer','disc.read');
INSERT INTO reviewer_backfill_src values ('reviewer','disc.revise.own');
INSERT INTO reviewer_backfill_src values ('reviewer','dropbox.own');
INSERT INTO reviewer_backfill_src values ('reviewer','mail.read');
INSERT INTO reviewer_backfill_src values ('reviewer','reports.run');
INSERT INTO reviewer_backfill_src values ('reviewer','reports.view');
INSERT INTO reviewer_backfill_src values ('reviewer','rwiki.create');
INSERT INTO reviewer_backfill_src values ('reviewer','rwiki.read');
INSERT INTO reviewer_backfill_src values ('reviewer','rwiki.update');
INSERT INTO reviewer_backfill_src values ('reviewer','section.role.student');
INSERT INTO reviewer_backfill_src values ('reviewer','site.visit');
INSERT INTO reviewer_backfill_src values ('reviewer','osp.presentation.copy');
INSERT INTO reviewer_backfill_src values ('reviewer','osp.presentation.comment');
INSERT INTO reviewer_backfill_src values ('reviewer','osp.wizard.review');

-- Lookup the role and function keys
insert into reviewer_backfill (ROLE_KEY, FUNCTION_KEY)
select SRR.ROLE_KEY, SRF.FUNCTION_KEY
from reviewer_backfill_src TMPSRC
join SAKAI_REALM_ROLE SRR on (TMPSRC.ROLE_NAME = SRR.ROLE_NAME)
join SAKAI_REALM_FUNCTION SRF on (TMPSRC.FUNCTION_NAME = SRF.FUNCTION_NAME);

-- Insert the new functions into the roles of any existing realm that has the role (don't convert the "!site.helper" or any group realms)
insert into SAKAI_REALM_RL_FN (REALM_KEY, ROLE_KEY, FUNCTION_KEY)
select
    SR.REALM_KEY, TMP.ROLE_KEY, TMP.FUNCTION_KEY
from reviewer_backfill TMP,
    SAKAI_REALM SR, sakai_site SS 
where instr(SR.realm_id, '/site/' || SS.SITE_ID) > 0
and SS.type = 'portfolio'
   and not exists (
        select 1
            from SAKAI_REALM_RL_FN SRRFI
            where SRRFI.REALM_KEY=SR.REALM_KEY and SRRFI.ROLE_KEY=TMP.ROLE_KEY and  SRRFI.FUNCTION_KEY=TMP.FUNCTION_KEY
    );

drop table reviewer_backfill_src;
drop table reviewer_backfill;

