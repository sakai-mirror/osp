ALTER TABLE osp_wizard_page_def ADD COLUMN (defaultUserForms boolean, defaultFeedbackEval boolean);
UPDATE osp_wizard_page_def SET defaultUserForms = false, defaultFeedbackEval = false;
