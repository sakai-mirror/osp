/*
 * Copyright (c) 2005 Your Corporation. All Rights Reserved.
 */
package org.theospi.portfolio.shared.tool;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Nov 18, 2005
 * Time: 3:14:15 PM
 * To change this template use File | Settings | File Templates.
 */
public class PagingList {

   private int firstItem = 0;
   private int pageSize = 10;

   private List wholeList;

   public PagingList(List wholeList) {
      this.wholeList = wholeList;
   }

   public int getTotalItems() {
      return wholeList.size();
   }

   public boolean isRendered() {
      return getTotalItems() > 0;
   }

   public int getFirstItem() {
      return firstItem;
   }

   public void setFirstItem(int firstItem) {
      this.firstItem = firstItem;
   }

   public int getPageSize() {
      return pageSize;
   }

   public void setPageSize(int pageSize) {
      this.pageSize = pageSize;
   }

   public List getWholeList() {
      return wholeList;
   }

   public void setWholeList(List wholeList) {
      this.wholeList = wholeList;
   }

   public List getSubList() {
      if (pageSize == 0){
         return wholeList;
      }
      else {
         return wholeList.subList(getFirstItem(), getLastItem());
      }
   }

   public int getLastItem() {
      int lastItem = getFirstItem() + getPageSize();
      if (lastItem >= wholeList.size()) {
         lastItem = wholeList.size();
      }
      return lastItem;
   }
}
