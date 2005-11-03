/**********************************************************************************
 *
 * $Header: /opt/CVS/osp2.x/common/api/src/java/org/theospi/portfolio/shared/intf/DownloadableManager.java,v 1.2 2005/08/05 18:57:35 jellis Exp $
 *
 ***********************************************************************************
 * Copyright (c) 2005 the r-smart group, inc.
 **********************************************************************************/
package org.theospi.portfolio.shared.intf;

import java.util.Map;
import java.io.OutputStream;
import java.io.IOException;

public interface DownloadableManager {

   public void packageForDownload(Map params, OutputStream out) throws IOException;
}
