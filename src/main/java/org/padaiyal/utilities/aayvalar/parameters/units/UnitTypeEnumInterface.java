package org.padaiyal.utilities.aayvalar.parameters.units;

/**
 * Abstracts the measurable parameter units.
 *
 * @param <ValueT> Type of value in parameter.
 */
public interface UnitTypeEnumInterface<ValueT> {

  /**
   * Returns the SI unit for a measurable parameter.
   *
   * @return SI unit for a measurable parameter.
   */
  UnitTypeEnumInterface<ValueT> getSiUnit();

  /**
   * Returns the type of measurable parameter.
   *
   * @return Type of measurable parameter.
   */
  Class<ValueT> getValueType();
}