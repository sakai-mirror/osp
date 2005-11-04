package org.theospi.portfolio.security;

/**
 * Created by IntelliJ IDEA.
 * User: jbush
 * Date: Aug 3, 2004
 * Time: 11:17:14 PM
 * To change this template use File | Settings | File Templates.
 */
public interface PasswordGenerator {
   public String generate();
   public String generate(int length);
}
