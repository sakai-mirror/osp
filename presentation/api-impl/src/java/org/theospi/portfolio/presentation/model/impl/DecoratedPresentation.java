package org.theospi.portfolio.presentation.model.impl;

import org.sakaiproject.metaobj.shared.model.Agent;
import org.sakaiproject.metaobj.worksite.mgt.WorksiteManager;
import org.theospi.portfolio.list.intf.DecoratedListItem;
import org.theospi.portfolio.list.intf.ListItemUtils;
import org.theospi.portfolio.presentation.model.Presentation;
import org.theospi.portfolio.presentation.model.PresentationTemplate;

/**
 * Created by IntelliJ IDEA.
 * User: bbiltimier
 * Date: Mar 7, 2006
 * Time: 2:13:20 PM
 * To change this template use File | Settings | File Templates.
 */
public class DecoratedPresentation implements DecoratedListItem {

    private ListItemUtils listItemUtils;
    private Presentation presentation;
    private WorksiteManager worksiteManager;

    public DecoratedPresentation(Presentation presentation, WorksiteManager worksiteManager) {
        this.presentation = presentation;
        this.worksiteManager = worksiteManager;
    }

    public ListItemUtils getListItemUtils() {
        return listItemUtils;
    }

    public void setListItemUtils(ListItemUtils listItemUtils) {
        this.listItemUtils = listItemUtils;
    }

    public String getSiteName() {
        return worksiteManager.getSite(presentation.getSiteId()).getTitle();
    }

    public String getName() {
        return presentation.getName();
    }

    public Agent getOwner() {
        return presentation.getOwner();
    }

    public String getDescription() {
        return presentation.getDescription();
    }

    public Presentation getPresentation() {
        return presentation;
    }

    public String getModified() {
        return listItemUtils.formatMessage("date_format", new Object[]{presentation.getModified()});
    }

    public PresentationTemplate getTemplate () {
        return presentation.getTemplate();
    }

    public String getExternalUri(){
        return presentation.getExternalUri();
    }


}
