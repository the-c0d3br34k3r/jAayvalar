package org.padaiyal.utilities.aayvalar.parameters;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.converter.ConvertWith;
import org.junit.jupiter.params.provider.CsvSource;
import org.padaiyal.utilities.aayvalar.parameters.units.DataStorageUnitEnum;

/**
 * Tests the DataStorageParameter.
 */
public class DataStorageParameterTest
    extends MeasurableParameterTest<Double, DataStorageUnitEnum, DataStorageParameter> {

  @Override
  DataStorageParameter instantiateParameter(Double value, DataStorageUnitEnum unit) {
    return new DataStorageParameter(value, unit);
  }

  @Override
  DataStorageUnitEnum getExpectedSiUnit() {
    return DataStorageUnitEnum.BYTE;
  }

  @Override
  Class<Double> getExpectedValueType() {
    return Double.class;
  }

  @ParameterizedTest
  @CsvSource({
      "1, BYTE, 8, BIT",
      "1, BYTE, 0.5, WORD",

      "1, BYTE, 1, BYTE",

      "1, BIT, 0.125, BYTE",
      "1, WORD, 2, BYTE"
  })
  @Override
  void testConvertToWithValidInputs(
      Double currentValue,
      DataStorageUnitEnum currentUnit,
      Double resultValue,
      DataStorageUnitEnum resultUnit
  ) {
    super.testConvertToWithValidInputs(
        currentValue,
        currentUnit,
        resultValue,
        resultUnit
    );
  }

  @ParameterizedTest
  @CsvSource({
      "-1, BYTE, BIT, IllegalArgumentException.class",
      ", BYTE, BIT, NullPointerException.class",
      "1,, BIT, NullPointerException.class",
      "1, BYTE,, NullPointerException.class",
      "1, BYTE, UNKNOWN, UnsupportedOperationException.class",
      "1, UNKNOWN, BYTE, UnsupportedOperationException.class"
  })
  @Override
  void testConvertToWithInvalidInputs(
      Double currentValue,
      DataStorageUnitEnum currentUnit,
      DataStorageUnitEnum resultUnit,
      @ConvertWith(ExceptionClassConverter.class)
          Class<? extends Exception> expectedExceptionClass
  ) {
    super.testConvertToWithInvalidInputs(
        currentValue,
        currentUnit,
        resultUnit,
        expectedExceptionClass
    );
  }
}