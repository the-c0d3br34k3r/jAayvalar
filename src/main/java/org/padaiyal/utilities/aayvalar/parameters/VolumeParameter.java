package org.padaiyal.utilities.aayvalar.parameters;

import org.padaiyal.utilities.I18nUtility;
import org.padaiyal.utilities.aayvalar.parameters.units.VolumeUnitEnum;

/**
 * Abstracts a volume parameter.
 */
public class VolumeParameter extends MeasurableParameter<Double, VolumeUnitEnum> {

  /**
   * Abstracts a volume parameter.
   *
   * @param value Value of volume.
   * @param unit  Unit of volume.
   */
  public VolumeParameter(Double value, VolumeUnitEnum unit) {
    super(value, unit);
    if (value < 0) {
      throw new IllegalArgumentException(
          I18nUtility.getFormattedString(
              "MeasurableParameter.error.input.value.invalid.negative",
              "volume",
              value
          )
      );
    }
  }

  @Override
  protected Double convertToSiUnit() {
    return switch (unit) {
      case LITER -> value;
      case US_CUP -> value * 0.2400005716272;
      case US_GALLON -> value * 3.7854208000180791238;
      case US_PINT -> value * 0.47317760000225989048;
      case US_QUART -> value * 0.94635520000451978095;
      case US_TABLESPOON -> value * 0.0147868;
      case US_TEASPOON -> value * 0.00492892;
      default -> throw new UnsupportedOperationException(
          I18nUtility.getString("MeasurableParameter.error.unknown.operation.unsupported")
      );
    };
  }

  @Override
  public Double convertTo(VolumeUnitEnum outputUnit) {
    return switch (outputUnit) {
      case LITER -> valueInSiUnit;
      case US_CUP -> valueInSiUnit / 0.2400005716272;
      case US_GALLON -> valueInSiUnit / 3.7854208000180791238;
      case US_PINT -> valueInSiUnit / 0.47317760000225989048;
      case US_QUART -> valueInSiUnit / 0.94635520000451978095;
      case US_TABLESPOON -> valueInSiUnit / 0.0147868;
      case US_TEASPOON -> valueInSiUnit / 0.00492892;
      default -> throw new UnsupportedOperationException(
          I18nUtility.getString("MeasurableParameter.error.unknown.operation.unsupported")
      );
    };
  }
}
