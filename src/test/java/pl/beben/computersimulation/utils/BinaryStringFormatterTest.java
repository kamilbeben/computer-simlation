package pl.beben.computersimulation.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class BinaryStringFormatterTest {

  @ParameterizedTest
  @CsvSource({
    "1,  4, 0001",
    "2,  4, 0010",
    "3,  4, 0011",
    "4,  4, 0100",
    "5,  4, 0101",
    "6,  4, 0110",
    "7,  4, 0111",
    "8,  4, 1000",
    "9,  4, 1001",
    "10, 4, 1010",
    "10, 8, 00001010"
  })
 public void formatTest(byte aByte, byte bitCount, String expectedOutput) {
    Assertions.assertEquals(expectedOutput, BinaryStringFormatter.format(aByte, bitCount));
 }

}