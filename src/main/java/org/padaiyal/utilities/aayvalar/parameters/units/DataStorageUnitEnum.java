package org.padaiyal.utilities.aayvalar.parameters.units;

/**
 * Abstracts the data storage parameter units.
 */
public enum DataStorageUnitEnum implements UnitTypeEnumInterface<Double> {
  BIT,
  BYTE,
  WORD,
  UNKNOWN;

  /**
   * SI unit of data storage parameter.
   */
  private static final DataStorageUnitEnum siUnit = BYTE;

  /**
   * Value type of data storage parameter.
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