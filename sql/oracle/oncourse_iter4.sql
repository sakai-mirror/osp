
delete From onc.SAKAI_REALM_RL_FN fn
where FUNCTION_KEY = (select FUNCTION_KEY from onc.sakai_realm_function where function_name = 'section.role.student')
and (fn.ROLE_KEY in (select role_key from onc.sakai_realm_role where upper(role_name) = upper('Reviewer'))
    or fn.ROLE_KEY in (select role_key from onc.sakai_realm_role where upper(role_name) = upper('Evaluator')));