package org.theospi.portfolio.security.impl;

import org.theospi.portfolio.security.PasswordGenerator;

import java.util.Random;

public class PasswordGeneratorImpl implements PasswordGenerator{
   private int length;
   public String generate() {
      return generate(getLength());
   }

   public String generate(int length) {
      Random rand = new Random();
      char[] pass = new char[length];
      for (int i = 0; i < length; i++) {
         int val = rand.nextInt(52);
         // need to add appropriate values to get to the ascii values
         if (val < 26)
            val += 65;
         else
            val += 71;
         pass[i] = (char) val;
      }
      return new String(pass);
   }

   public int getLength() {
      return length;
   }

   public void setLength(int length) {
      this.length = length;
   }
}
