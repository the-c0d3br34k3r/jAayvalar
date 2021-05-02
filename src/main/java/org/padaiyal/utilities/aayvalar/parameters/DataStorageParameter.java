package org.padaiyal.utilities.aayvalar.parameters;

import org.padaiyal.utilities.I18nUtility;
import org.padaiyal.utilities.aayvalar.parameters.units.DataStorageUnitEnum;

/**
 * Abstracts a data storage parameter.
 */
public class DataStorageParameter extends MeasurableParameter<Double, DataStorageUnitEnum> {

  /**
   * Abstracts a data storage parameter.
   *
   * @param value Value of data storage.
   * @param unit  Unit of data storage.
   */
  public DataStorageParameter(Double value, DataStorageUnitEnum unit) {
    super(value, unit);
    if (value < 0) {
      throw new IllegalArgumentException(
          I18nUtility.getFormattedString(
              "MeasurableParameter.error.input.value.invalid.negative",
              "data storage",
              value
          )
      );
    }
  }

  @Override
  protected Double convertToSiUnit() {
    return switch (unit) {
      case BIT -> value / 8.0;
      case BYTE -> value;
      case WORD -> value * 2;
      default -> throw new UnsupportedOperationException(
          I18nUtility.getString("MeasurableParameter.error.unknown.operation.unsupported")
      );
    };
  }

  @Override
  public Double convertTo(DataStorageUnitEnum outputUnit) {
    return switch (outputUnit) {
      case BIT -> valueInSiUnit * 8.0;
      case BYTE -> valueInSiUnit;
      case WORD -> valueInSiUnit / 2;
      default -> throw new UnsupportedOperationException(
          I18nUtility.getString("MeasurableParameter.error.unknown.operation.unsupported")
      );
    };
  }
}
