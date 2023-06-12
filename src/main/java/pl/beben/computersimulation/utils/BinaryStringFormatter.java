package pl.beben.computersimulation.utils;

import lombok.NoArgsConstructor;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class BinaryStringFormatter {

  public static String format(byte aByte, byte bitCount) {
    final var nonPaddedBinaryString = Integer.toBinaryString(aByte);
    final var spacePaddedFormat = "%" + bitCount + "s";
    final var spacePaddedBinaryString = String.format(spacePaddedFormat, nonPaddedBinaryString);
    return spacePaddedBinaryString.replace(' ', '0');
  }

}
