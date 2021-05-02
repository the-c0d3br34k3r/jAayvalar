package org.padaiyal.utilities.aayvalar.parameters;

import java.util.Locale;
import java.util.Objects;
import org.padaiyal.utilities.I18nUtility;
import org.padaiyal.utilities.aayvalar.parameters.units.UnitTypeEnumInterface;

/**
 * Abstracts a measurable parameter.
 */
abstract class MeasurableParameter<ValueT, UnitT extends UnitTypeEnumInterface<ValueT>> {

  /**
   * Measurable parameter value.
   */
  protected ValueT value;
  /**
   * Measurable parameter unit.
   */
  protected UnitT unit;
  /**
   * Measurable parameter value in SI unit.
   */
  protected ValueT valueInSiUnit;

  /**
   * Abstraction for a measurable parameter including the value and unit of measurement.
   *
   * @param value ValueT of measurable parameter.
   * @param unit  UnitT of measurement.
   */
  protected MeasurableParameter(ValueT value, UnitT unit) {
    // Input validation.
    Objects.requireNonNull(value);
    Objects.requireNonNull(unit);

    this.value = value;
    this.unit = unit;
    this.valueInSiUnit = convertToSiUnit();
    I18nUtility.addResourceBundle(
        MeasurableParameter.class,
        MeasurableParameter.class.getSimpleName(),
        Locale.US
    );
  }

  /**
   * Converts the value of this measurable parameter to SI unit and returns it.
   *
   * @return ValueT in SI unit.
   */
  protected abstract ValueT convertToSiUnit();

  /**
   * Converts the value of this measurable parameter to the desired unit.
   *
   * @return ValueT in desired unit.
   */
  public abstract ValueT convertTo(UnitT outputUnit);

  /**
   * Returns the value of the measurable parameter.
   *
   * @return ValueT of the measurable parameter.
   */
  public ValueT getValue() {
    return value;
  }

  /**
   * Returns the unit of the measurable parameter.
   *
   * @return UnitT of the measurable parameter.
   */
  public UnitT getUnit() {
    return unit;
  }

}