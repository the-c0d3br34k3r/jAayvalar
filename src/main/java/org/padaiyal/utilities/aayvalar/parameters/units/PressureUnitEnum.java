package org.padaiyal.utilities.aayvalar.parameters.units;

/**
 * Abstracts the pressure parameter units.
 */
public enum PressureUnitEnum implements UnitTypeEnumInterface<Double> {
  ATMOSPHERIC,
  BAR,
  PASCAL,
  TORR,
  UNKNOWN;

  /**
   * SI unit of pressure parameter.
   */
  private static final PressureUnitEnum siUnit = PASCAL;

  /**
   * Value type of pressure parameter.
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