package org.theospi.portfolio.security.app;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Jul 21, 2005
 * Time: 11:02:59 AM
 * To change this template use File | Settings | File Templates.
 */
public class OrderedAuthorizer implements Comparable {

   private ApplicationAuthorizer authorizer;
   private int order = Integer.MAX_VALUE;

   public ApplicationAuthorizer getAuthorizer() {
      return authorizer;
   }

   public void setAuthorizer(ApplicationAuthorizer authorizer) {
      this.authorizer = authorizer;
   }

   public int getOrder() {
      return order;
   }

   public void setOrder(int order) {
      this.order = order;
   }

   public int compareTo(Object o) {
      OrderedAuthorizer other = (OrderedAuthorizer) o;

      return getOrder() - other.getOrder();
   }
}
