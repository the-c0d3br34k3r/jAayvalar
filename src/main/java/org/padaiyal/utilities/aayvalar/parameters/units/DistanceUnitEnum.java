package org.padaiyal.utilities.aayvalar.parameters.units;

/**
 * Abstracts the distance parameter units.
 */
public enum DistanceUnitEnum implements UnitTypeEnumInterface<Double> {
  ANGSTROM,
  FERMI,
  FOOT,
  INCH,
  METRE,
  MICRON,
  MILE,
  YARD,
  UNKNOWN;

  /**
   * SI unit of distance parameter.
   */
  private static final DistanceUnitEnum siUnit = METRE;

  /**
   * Value type of distance storage parameter.
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