package org.padaiyal.utilities.aayvalar.parameters;

import org.padaiyal.utilities.I18nUtility;
import org.padaiyal.utilities.aayvalar.parameters.units.DistanceUnitEnum;

/**
 * Abstracts a distance parameter.
 */
public class DistanceParameter extends MeasurableParameter<Double, DistanceUnitEnum> {

  /**
   * Abstracts a distance parameter.
   *
   * @param value Value of distance.
   * @param unit  Unit of distance.
   */
  public DistanceParameter(Double value, DistanceUnitEnum unit) {
    super(value, unit);
    if (value < 0) {
      throw new IllegalArgumentException(
          I18nUtility.getFormattedString(
              "MeasurableParameter.error.input.value.invalid.negative",
              "distance",
              value
          )
      );
    }
  }

  @Override
  public Double convertToSiUnit() {
    return switch (unit) {
      case METRE -> value;
      case FERMI -> value * Math.pow(10, -15);
      case ANGSTROM -> value * Math.pow(10, -10);
      case MICRON -> value * Math.pow(10, -6);
      case INCH -> value * 0.0254;
      case FOOT -> value * 0.3048;
      case YARD -> value * 0.9144;
      case MILE -> value * 1609.34;
      default -> throw new UnsupportedOperationException(
          I18nUtility.getString("MeasurableParameter.error.unknown.operation.unsupported")
      );
    };
  }

  @Override
  public Double convertTo(DistanceUnitEnum outputUnit) {
    return switch (outputUnit) {
      case ANGSTROM -> valueInSiUnit * Math.pow(10, 10);
      case FERMI -> valueInSiUnit * Math.pow(10, 15);
      case FOOT -> valueInSiUnit / 0.3048;
      case INCH -> valueInSiUnit / 0.0254;
      case METRE -> valueInSiUnit;
      case MICRON -> valueInSiUnit * Math.pow(10, 6);
      case MILE -> valueInSiUnit / 1609.34;
      case YARD -> valueInSiUnit / 0.9144;
      default -> throw new UnsupportedOperationException(
          I18nUtility.getString("MeasurableParameter.error.unknown.operation.unsupported")
      );
    };
  }
}