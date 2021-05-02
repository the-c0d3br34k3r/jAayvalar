package org.padaiyal.utilities.aayvalar.parameters;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.padaiyal.utilities.I18nUtility;
import org.padaiyal.utilities.PropertyUtility;

/**
 * Abstracts a currency parameter.
 */
public class CurrencyParameter {

  /**
   * Logger.
   */
  private static final Logger logger = LogManager.getLogger(CurrencyParameter.class);
  /**
   * Currency value.
   */
  private final Double value;
  /**
   * Currency unit.
   */
  private final String unit;
  /**
   * API key.
   * A free API key can be obtained by registering here - https://www.currencyconverterapi.com/.
   */
  private final String apiKey;
  /**
   * API URL.
   */
  private final String apiUrl;

  /**
   * Abstracts a currency parameter.
   *
   * @param value Value of currency.
   * @param unit  Unit of currency.
   */
  public CurrencyParameter(Double value, String unit, String apiKey) throws IOException {
    I18nUtility.addResourceBundle(
        MeasurableParameter.class,
        MeasurableParameter.class.getSimpleName(),
        Locale.US
    );
    I18nUtility.addResourceBundle(
        CurrencyParameter.class,
        CurrencyParameter.class.getSimpleName(),
        Locale.US
    );

    PropertyUtility.addPropertyFile(
        CurrencyParameter.class,
        CurrencyParameter.class.getSimpleName() + ".properties"
    );

    // Input validation
    if (value < 0) {
      throw new IllegalArgumentException(
          I18nUtility.getFormattedString(
              "MeasurableParameter.error.input.value.invalid.negative",
              "currency",
              value
          )
      );
    } else if (unit.length() != 3) {
      throw new IllegalArgumentException(
          I18nUtility.getFormattedString(
              "CurrencyParameter.input.unit.invalid",
              unit
          )
      );
    }

    this.value = value;
    this.unit = unit;
    this.apiKey = apiKey;
    this.apiUrl = PropertyUtility.getProperty("CurrencyParameter.api.url");
  }

  /**
   * Converts the currency parameter into the desired currency unit.
   *
   * @param desiredCurrencyUnit Desired output currency unit.
   * @return The currency value in the desired unit.
   * @throws IOException If there is an issue while invoking the API.
   */
  public double convertTo(String desiredCurrencyUnit)
      throws IOException {
    // Input validation
    if (desiredCurrencyUnit.length() != 3) {
      throw new IllegalArgumentException(
          I18nUtility.getFormattedString(
              "CurrencyParameter.input.unit.invalid",
              desiredCurrencyUnit
          )
      );
    }

    URL url = new URL(
        String.format(
            apiUrl,
            unit,
            desiredCurrencyUnit,
            apiKey
        )
    );
    HttpURLConnection con = (HttpURLConnection) url.openConnection();
    con.setRequestMethod("GET");
    String response = new String(
        new BufferedInputStream(
            con.getInputStream()
        ).readNBytes(
            PropertyUtility.getTypedProperty(
                Integer.class,
                "CurrencyParameter.api.limit.maxBytesToRead"
            )
        )
    );
    ObjectMapper mapper = new ObjectMapper();
    JsonNode actualObj = mapper.readTree(response);
    logger.debug(response);
    return actualObj.get(
        String.format(
            PropertyUtility.getProperty("CurrencyParameter.api.response.keyFormat"),
            unit,
            desiredCurrencyUnit
        )
    ).asDouble() * value;
  }

  /**
   * Returns the value of the currency parameter.
   *
   * @return Value of the currency parameter.
   */
  public Double getValue() {
    return value;
  }

  /**
   * Returns the unit of the currency parameter.
   *
   * @return Unit of the currency parameter.
   */
  public String getUnit() {
    return unit;
  }
}
