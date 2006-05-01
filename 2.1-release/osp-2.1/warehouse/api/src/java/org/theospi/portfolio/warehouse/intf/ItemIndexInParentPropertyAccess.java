/**********************************************************************************
* $URL: https://source.sakaiproject.org/svn/trunk/osp/osp-2.1/warehouse/api/src/java/org/theospi/portfolio/warehouse/intf/ParentPropertyAccess.java $
* $Id: ParentPropertyAccess.java 5557 2006-01-26 06:02:52Z john.ellis@rsmart.com $
***********************************************************************************
*
* Copyright (c) 2005, 2006 The Sakai Foundation.
*
* Licensed under the Educational Community License, Version 1.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.opensource.org/licenses/ecl1.php
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*
**********************************************************************************/
package org.theospi.portfolio.warehouse.intf;

/**
 * a complex child is dealt with as a List.  This allows the ordering of the list to be captured.
 * When looping through the list of a complex field, the index of each element is passed to the function
 * that processes the single item.  This access puts that index as an implicit property into the database
 * 
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Dec 2, 2005
 * Time: 4:48:55 PM
 * To change this template use File | Settings | File Templates.
 */
public final class ItemIndexInParentPropertyAccess {

}
