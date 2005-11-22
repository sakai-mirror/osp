/*
 * Copyright (c) 2005 Your Corporation. All Rights Reserved.
 */
package org.theospi.jsf.intf;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Nov 21, 2005
 * Time: 4:19:36 PM
 * To change this template use File | Settings | File Templates.
 */
public interface InitObjectContainer {

   public void addInitScript(String script);

   public List getInitScripts();

}
