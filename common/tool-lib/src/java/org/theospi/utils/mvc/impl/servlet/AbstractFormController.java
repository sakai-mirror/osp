package org.theospi.utils.mvc.impl.servlet;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.validation.Errors;
import org.sakaiproject.metaobj.utils.mvc.intf.CancelableController;
import org.sakaiproject.metaobj.utils.mvc.intf.FormController;

import javax.servlet.ServletException;
import java.util.Map;
import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: jbush
 * Date: Dec 15, 2004
 * Time: 9:52:19 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractFormController implements CancelableController, FormController {
    public static final String PARAM_CANCEL = "_cancel";
	/**
	 * Return if cancel action is specified in the request.
	 * <p>Default implementation looks for "_cancel" parameter in the request.
	 * @param request current HTTP request
	 * @see #PARAM_CANCEL
	 */
    public boolean isCancel(Map request) {
        return request.containsKey(PARAM_CANCEL);
    }

    public ModelAndView processCancel(Map request, Map session, Map application,
                                      Object command, Errors errors) throws Exception {
        throw new ServletException(
                "Wizard form controller class [" + getClass().getName() + "] does not support a cancel operation");
    }

   public Map referenceData(Map request, Object command, Errors errors){
       return new HashMap();
   }
}
