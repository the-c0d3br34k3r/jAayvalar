package org.padaiyal.utilities.aayvalar.parameters;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.converter.ConvertWith;
import org.junit.jupiter.params.provider.CsvSource;
import org.padaiyal.utilities.aayvalar.parameters.units.TemperatureUnitEnum;

/**
 * Tests the TemperatureParameter.
 */
public class TemperatureParameterTest
    extends MeasurableParameterTest<Double, TemperatureUnitEnum, TemperatureParameter> {

  @Override
  TemperatureParameter instantiateParameter(Double value, TemperatureUnitEnum unit) {
    return new TemperatureParameter(value, unit);
  }

  @Override
  TemperatureUnitEnum getExpectedSiUnit() {
    return TemperatureUnitEnum.CELSIUS;
  }

  @Override
  Class<Double> getExpectedValueType() {
    return Double.class;
  }

  @ParameterizedTest
  @CsvSource({
      "1, CELSIUS, 33.8, FAHRENHEIT",
      "1, CELSIUS, -272.0, KELVIN",

      "3.141569, CELSIUS, 3.141569, CELSIUS",

      "1, FAHRENHEIT, -17.22222222222222, CELSIUS",
      "1, KELVIN, 274.0, CELSIUS",
  })
  @Override
  void testConvertToWithValidInputs(
      Double currentValue,
      TemperatureUnitEnum currentUnit,
      Double resultValue,
      TemperatureUnitEnum resultUnit
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
      "-1, CELSIUS, FAHRENHEIT, IllegalArgumentException.class",
      ", CELSIUS, KELVIN, NullPointerException.class",
      "1,, KELVIN, NullPointerException.class",
      "1, CELSIUS,, NullPointerException.class",
      "1, CELSIUS, UNKNOWN, UnsupportedOperationException.class",
      "1, UNKNOWN, CELSIUS, UnsupportedOperationException.class"
  })
  @Override
  void testConvertToWithInvalidInputs(
      Double currentValue,
      TemperatureUnitEnum currentUnit,
      TemperatureUnitEnum resultUnit,
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
