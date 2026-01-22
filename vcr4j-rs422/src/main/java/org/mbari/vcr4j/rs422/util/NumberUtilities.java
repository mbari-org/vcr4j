package org.mbari.vcr4j.rs422.util;

/*-
 * #%L
 * vcr4j-rs422
 * %%
 * Copyright (C) 2008 - 2026 Monterey Bay Aquarium Research Institute
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */



/**
 * <p>Useful static methods for manipulating numbers.</p>
 */
public class NumberUtilities {

  /**
   * No instantiation allowed
   */
  private NumberUtilities() {}


  /**
   * Reverse the ordering of a byte array
   *
   * @param  si
   * @param  i
   * @return
   */
  private final static byte[] reverseOrder(byte[] si, int i) {
      byte[] is = new byte[i];
      for (byte b = 0; b <= i - 1; b++) {
          is[b] = si[i - 1 - b];
      }

      return is;
  }

  /**
   * Converts an <b>char</b> to the corresponding byte[] array. Most significant byte is first.
   *
   * @param  c  char input
   * @return    Byte array corresponding to the input
   */
  public final static byte[] toByteArray(char c) {
      return toByteArray(c, false);
  }

  /**
   * Converts a <b>double</b> to the corresponding byte[] array. Most significant byte is first.
   *
   * @param  d  Integer input
   * @return    Byte array corresponding to the input
   */
  public final static byte[] toByteArray(double d) {
      return toByteArray(d, false);
  }

  /**
   * Converts a <b>float</b> to the corresponding byte[] array. Most significant byte is first.
   *
   * @param  f  float input
   * @return    Byte array corresponding to the input
   */
  public final static byte[] toByteArray(float f) {
      return toByteArray(f, false);
  }

  /**
   * Converts an <b>integer</b> to the corresponding byte[] array. Most significant byte is first.
   *
   * @param  i  Integer input
   * @return    Byte array corresponding to the input
   */
  public final static byte[] toByteArray(int i) {
      return toByteArray(i, false);
  }

  /**
   * Converts a <b>long</b> to the corresponding byte[] array. Most significant byte is first.
   *
   * @param  l  long input
   * @return    Byte array corresponding to the input
   */
  public final static byte[] toByteArray(long l) {
      return toByteArray(l, false);
  }

  /**
   * Converts a <b>short</b> to the corresponding byte[] array. Most significant byte is first.
   *
   * @param  i  Integer input
   * @return    Byte array corresponding to the input
   */
  public final static byte[] toByteArray(short i) {
      return toByteArray(i, false);
  }

  /**
   *  Description of the Method
   *
   * @param  c               Description of the Parameter
   * @param  isReverseOrder  Description of the Parameter
   * @return                 Description of the Return Value
   */
  public final static byte[] toByteArray(char c, boolean isReverseOrder) {
      byte[] si = new byte[2];
      for (byte i = 0; i <= 1; i++) {
          si[i] = (byte) (c >>> (1 - i) * 8);
      }

      if (isReverseOrder) {
          si = reverseOrder(si, 2);
      }

      return si;
  }

  /**
   * Converts a <b>double</b> to the corresponding byte[] array
   *
   * @param  isReverseOrder  True if the ordering is least significant byte to
   * most signifigant byte, false for most significant to least.
   * @param  d               Description of the Parameter
   * @return                 Byte array corresponding to the input
   */
  public final static byte[] toByteArray(double d, boolean isReverseOrder) {
      byte[] si = new byte[8];
      // Double var_double = new Double(d);
      long l = Double.doubleToLongBits(d);
      si = toByteArray(l, isReverseOrder);
      return si;
  }

  /**
   * Converts a <b>float</b> to the corresponding byte[] array
   *
   * @param  f               Short input
   * @param  isReverseOrder  True if the ordering is least significant byte to
   * most signifigant byte, false for most significant to least.
   * @return                 Byte array corresponding to the input
   */
  public final static byte[] toByteArray(float f, boolean isReverseOrder) {
      byte[] si = new byte[4];
      // Float var_float = new Float(f);
      int i = Float.floatToIntBits(f);
      si = toByteArray(i, isReverseOrder);
      return si;
  }

  /**
   * Converts an <b>integer</b> to the corresponding byte[] array
   *
   * @param  i               Integer input
   * @param  isReverseOrder  True if the ordering is least significant byte to
   * most signifigant byte, false for most significant to least.
   * @return                 Byte array corresponding to the input
   */
  public final static byte[] toByteArray(int i, boolean isReverseOrder) {
      byte[] si = new byte[4];
      for (byte b = 0; b <= 3; b++) {
          si[b] = (byte) (i >>> (3 - b) * 8);
      }

      if (isReverseOrder) {
          si = reverseOrder(si, 4);
      }

      return si;
  }

  /**
   * Converts a <b>long</b> to the corresponding byte[] array
   *
   * @param  isReverseOrder  True if the ordering is least significant byte to
   * most signifigant byte, false for most significant to least.
   * @param  l               Description of the Parameter
   * @return                 Byte array corresponding to the input
   */
  public final static byte[] toByteArray(long l, boolean isReverseOrder) {
      byte[] si = new byte[8];
      for (byte i = 0; i <= 7; i++) {
          si[i] = (byte) (int) (l >>> (7 - i) * 8);
      }

      if (isReverseOrder) {
          si = reverseOrder(si, 8);
      }

      return si;
  }

  /**
   * Converts a <b>short</b> to the corresponding byte[] array
   *
   * @param  i               Short input
   * @param  isReverseOrder  True if the ordering is least significant byte to
   * most signifigant byte, false for most significant to least.
   * @return                 Byte array corresponding to the input
   */
  public final static byte[] toByteArray(short i, boolean isReverseOrder) {
      byte[] si = new byte[2];
      for (byte b = 0; b <= 1; b++) {
          si[b] = (byte) (i >>> (1 - b) * 8);
      }

      if (isReverseOrder) {
          si = reverseOrder(si, 2);
      }

      return si;
  }

  /**
   * Convert a byte[] (most significant byte first) to the corresponding <b>char</b> value.
   *
   * @param  si  the input array
   * @return     The value coresponding to the byte array
   */
  public final static char toChar(byte[] si) {
      return toChar(si, false);
  }

  /**
   * Convert a byte[] to the corresponding <b>char</b> value.
   *
   * @param  si              the input array
   * @param  isReverseOrder  True if llittle-endian. False if big-endian (Most significant byte first)
   * @return                 The value coresponding to the byte array
   */
  public final static char toChar(byte[] si, boolean isReverseOrder) {
      int i = 0;
      if (isReverseOrder) {
          si = reverseOrder(si, 2);
      }

      char c = (char) ((i | (char) si[0]) << 8);
      c |= (char) si[1];
      return c;
  }

  /**
   * Convert a byte[] (most significant byte first) to the corresponding <b>double</b> value.
   *
   * @param  si  the input array
   * @return     The value coresponding to the byte array
   */
  public final static double toDouble(byte[] si) {
      return toDouble(si, false);
  }

  /**
   * Convert a byte[] to the corresponding <b>double</b> value.
   *
   * @param  si              the input array
   * @param  isReverseOrder  True if llittle-endian. False if big-endian (Most significant byte first)
   * @return                 The value coresponding to the byte array
   */
  public final static double toDouble(byte[] si, boolean isReverseOrder) {
      double d = 0.0;
      // Double var_double = new Double(d);
      long l = toLong(si, isReverseOrder);
      d = Double.longBitsToDouble(l);
      return d;
  }

  /**
   * Convert a byte[] (most significant byte first) to the corresponding <b>float</b> value.
   *
   * @param  si  the input array
   * @return     The value coresponding to the byte array
   */
  public final static float toFloat(byte[] si) {
      return toFloat(si, false);
  }

  /**
   * Convert a byte[] to the corresponding <b>float</b> value.
   *
   * @param  si              the input array
   * @param  isReverseOrder  True if llittle-endian. False if big-endian (Most significant byte first)
   * @return                 The value coresponding to the byte array
   */
  public final static float toFloat(byte[] si, boolean isReverseOrder) {
      float f = 0.0F;
      // Float var_float = new Float(f);
      int i = toInt(si, isReverseOrder);
      f = Float.intBitsToFloat(i);
      return f;
  }

  /**
   * Convert a byte[] (most significant byte first) to the corresponding <b>int</b> value.
   *
   * @param  si  the input array
   * @return     The value coresponding to the byte array
   */
  public final static int toInt(byte[] si) {
      return toInt(si, false);
  }

  /**
   * Convert a byte[] to the corresponding <b>int</b> value.
   *
   * @param  si              the input array
   * @param  isReverseOrder  True if little-endian. False if big-endian (Most significant byte first)
   * @return                 The value coresponding to the byte array
   */
  public final static int toInt(byte[] si, boolean isReverseOrder) {
      int i = 0;
      if (isReverseOrder) {
          si = reverseOrder(si, 4);
      }

      int nb = si.length - 1;
      for (byte b = 0; b <= nb; b++) {
          int j;
          if (si[b] < 0) {
              si[b] = (byte) (si[b] & 0x7f);
              j = si[b];
              j |= 0x80;
          } else {
              j = si[b];
          }

          i |= j;

          if (b < nb) {
              i <<= 8;
          }
      }

      return i;
  }

  /**
   * Convert a byte[] (most significant byte first) to the corresponding <b>long</b> value.
   *
   * @param  si  the input array
   * @return     The value coresponding to the byte array
   */
  public final static long toLong(byte[] si) {
      return toLong(si, false);
  }

  /**
   * Convert a byte[] to the corresponding <b>long</b> value.
   *
   * @param  si              the input array
   * @param  isReverseOrder  True if llittle-endian. False if big-endian (Most significant byte first)
   * @return                 The value coresponding to the byte array
   */
  public final static long toLong(byte[] si, boolean isReverseOrder) {
      long l = 0L;
      if (isReverseOrder) {
          si = reverseOrder(si, 8);
      }

      // for (byte i = 0; i <= 7; i++) {
      int nb = si.length - 1;
      for (byte i = 0; i <= nb; i++) {
          long j;
          if (si[i] < 0) {
              si[i] = (byte) (si[i] & 0x7f);
              j = (long) si[i];
              j |= 0x80L;
          } else {
              j = (long) si[i];
          }

          l |= j;

          if (i < nb) {
              l <<= 8;
          }
      }

      return l;
  }

  /**
   * Convert a byte[] (most significant byte first) to the corresponding <b>short</b> value.
   *
   * @param  si  the input array
   * @return     The value coresponding to the byte array
   */
  public final static short toShort(byte[] si) {
      return toShort(si, false);
  }

  /**
   * Convert a byte[] to the corresponding <b>short</b> value.
   *
   * @param  si              the input array
   * @param  isReverseOrder  True if llittle-endian. False if big-endian (Most significant byte first)
   * @return                 The value coresponding to the byte array
   */
  public final static short toShort(byte[] si, boolean isReverseOrder) {
      short i = 0;
      if (isReverseOrder) {
          si = reverseOrder(si, 2);
      }

      for (byte b = 0; b <= 1; b++) {
          short j;
          if (si[b] < 0) {
              si[b] = (byte) (si[b] & 0x7f);
              j = (short) si[b];
              j |= 0x80;
          } else {
              j = (short) si[b];
          }

          i |= j;

          if (b < 1) {
              i <<= 8;
          }
      }

      return i;
  }

  /**
   * Convert an unsigned byte to an integer
   *
   * @param  b  byte to convert
   * @return    integer corresponding to the unsigned byte.
   */
  public static int unsignedBytetoInt(byte b) {
      return (int) b & 0xFF;
  }
  
  /**
   * Format a byte array as a hexadecimal string. For example
   * <pre>
   * byte[] bytes = new byte[]{(byte)0x12, (byte)0x0F, (byte)0xF0};
   * String hex = NumberUtilities.toHexString(bytes); // 120ff0
   *</pre>
   *
   */
  public static String toHexString(byte[] b) {
    if (b == null) {
      return "null";
    }
    StringBuffer ret = new StringBuffer(b.length);
      for (int i = 0; i < b.length; i++) {
          String hex = Integer.toHexString(0x0100 + (b[i] & 0x00FF)).substring(1);
          ret.append((hex.length() < 2 ? "0" : "") + hex);
      }
      return ret.toString();
    
  }
}
