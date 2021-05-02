package org.padaiyal.utilities.aayvalar.parameters.units;

/**
 * Abstracts the volume parameter units.
 */
public enum VolumeUnitEnum implements UnitTypeEnumInterface<Double> {
  LITER,
  US_CUP,
  US_GALLON,
  US_PINT,
  US_QUART,
  US_TABLESPOON,
  US_TEASPOON,
  UNKNOWN;

  /**
   * SI unit of volume parameter.
   */
  private static final VolumeUnitEnum siUnit = LITER;

  /**
   * Value type of volume parameter.
   */
  private final Class<Double> valueType = Double.class;

  @Override
  public UnitTypeEnumInterface<Double> getSiUnit() {
    return siUnit;
  }

  @Override
  public Class<Double> getValueType() {
    return valueType;
  }
}
