package org.padaiyal.utilities.aayvalar.parameters;

import org.padaiyal.utilities.I18nUtility;
import org.padaiyal.utilities.aayvalar.parameters.units.TemperatureUnitEnum;

/**
 * Abstracts a temperature parameter.
 */
public class TemperatureParameter extends MeasurableParameter<Double, TemperatureUnitEnum> {

  /**
   * Abstracts a temperature parameter.
   *
   * @param value Value of temperature.
   * @param unit  Unit of temperature.
   */
  public TemperatureParameter(Double value, TemperatureUnitEnum unit) {
    super(value, unit);
    if (value < 0) {
      throw new IllegalArgumentException(
          I18nUtility.getFormattedString(
              "MeasurableParameter.error.input.value.invalid.negative",
              "temperature",
              value
          )
      );
    }
  }

  @Override
  protected Double convertToSiUnit() {
    return switch (unit) {
      case CELSIUS -> value;
      case FAHRENHEIT -> (value - 32) / 1.8;
      case KELVIN -> value + 273;
      default -> throw new UnsupportedOperationException(
          I18nUtility.getString("MeasurableParameter.error.unknown.operation.unsupported")
      );
    };
  }

  @Override
  public Double convertTo(TemperatureUnitEnum outputUnit) {
    return switch (outputUnit) {
      case CELSIUS -> this.valueInSiUnit;
      case FAHRENHEIT -> 1.8 * this.valueInSiUnit + 32;
      case KELVIN -> this.valueInSiUnit - 273;
      default -> throw new UnsupportedOperationException(
          I18nUtility.getString("MeasurableParameter.error.unknown.operation.unsupported")
      );
    };
  }
}
