package org.padaiyal.utilities.aayvalar.parameters.units;

/**
 * Abstracts the temperature parameter units.
 */
public enum TemperatureUnitEnum implements UnitTypeEnumInterface<Double> {
  CELSIUS,
  FAHRENHEIT,
  KELVIN,
  UNKNOWN;

  /**
   * SI unit of temperature parameter.
   */
  private static final TemperatureUnitEnum siUnit = CELSIUS;

  /**
   * Value type of temperature parameter.
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