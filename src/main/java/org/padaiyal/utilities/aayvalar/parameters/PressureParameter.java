package org.padaiyal.utilities.aayvalar.parameters;

import org.padaiyal.utilities.I18nUtility;
import org.padaiyal.utilities.aayvalar.parameters.units.PressureUnitEnum;

/**
 * Abstracts a pressure parameter.
 */
public class PressureParameter extends MeasurableParameter<Double, PressureUnitEnum> {

  /**
   * Abstracts a pressure parameter.
   *
   * @param value Value of pressure.
   * @param unit  Unit of pressure.
   */
  public PressureParameter(Double value, PressureUnitEnum unit) {
    super(value, unit);
    if (value < 0) {
      throw new IllegalArgumentException(
          I18nUtility.getFormattedString(
              "MeasurableParameter.error.input.value.invalid.negative",
              "pressure",
              value
          )
      );
    }
  }

  @Override
  public Double convertToSiUnit() {
    return switch (unit) {
      case ATMOSPHERIC -> value * 101_325;
      case BAR -> value * 100_000;
      case PASCAL -> value;
      case TORR -> value * 133.322;
      default -> throw new UnsupportedOperationException(
          I18nUtility.getString("MeasurableParameter.error.unknown.operation.unsupported")
      );
    };
  }

  @Override
  public Double convertTo(PressureUnitEnum outputUnit) {
    return switch (outputUnit) {
      case ATMOSPHERIC -> this.valueInSiUnit / 101_325;
      case BAR -> this.valueInSiUnit / 100_000;
      case PASCAL -> this.valueInSiUnit;
      case TORR -> this.valueInSiUnit / 133.322;
      default -> throw new UnsupportedOperationException(
          I18nUtility.getString("MeasurableParameter.error.unknown.operation.unsupported")
      );
    };
  }
}