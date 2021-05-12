package org.padaiyal.utilities.aayvalar.regex;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.padaiyal.utilities.I18nUtility;
import org.padaiyal.utilities.PropertyUtility;
import org.padaiyal.utilities.aayvalar.regex.abstractions.Comparison;
import org.padaiyal.utilities.aayvalar.regex.exceptions.InvalidRegexException;


/**
 * Utility class for manipulating regex expressions.
 */
public class RegexUtility {

  /**
   * Personal Identifiable Information regex expressions file name.
   */
  private static final String regexFieldsPropertiesFileName =
      "PersonalIdentifiableInformationRegex.properties";

  /**
   * Logger object used to log error and information.
   */
  private static final Logger logger = LogManager.getLogger(RegexUtility.class);

  /**
   * Word characters in a regex expression. Equivalent to \w.
   */
  private static final String wordCharactersRegex =
      "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890_";

  /**
   * Digits characters in a regex expression. Equivalent to \d.
   */
  private static final String digitCharactersRegex = "1234567890";

  /**
   * Random object.
   */
  private static final Random randomObj = new Random();
  /**
   * Runnable for initializing properties files.
   */
  public static Runnable initializeDependantValue = () -> {
    I18nUtility.addResourceBundle(
        RegexUtility.class,
        RegexUtility.class.getSimpleName(),
        Locale.US
    );

    try {
      PropertyUtility.addPropertyFile(
          RegexUtility.class,
          RegexUtility.class.getSimpleName() + ".properties"
      );

      PropertyUtility.addPropertyFile(
          RegexUtility.class,
          regexFieldsPropertiesFileName
      );

    } catch (IOException e) {
      logger.error(e);
    }
  };

  static {
    initializeDependantValue.run();
  }

  /**
   * Private constructor.
   */
  private RegexUtility() {
  }

  /**
   * Checks if at least one occurrence of the regex expression can be found in the input.
   *
   * @param patternString  Pattern to match
   * @param patternOptions Pattern match flags
   * @param input          Input string
   * @return Returns true if the pattern matches the input, else false
   */
  public static boolean find(String patternString, int patternOptions, String input) {
    return Pattern.compile(patternString, patternOptions)
        .matcher(input)
        .find();
  }

  /**
   * Checks if the whole input matches the specified pattern.
   *
   * @param patternString  Pattern to match
   * @param patternOptions Pattern match flags
   * @param input          Input string
   * @return Returns true if the pattern matches the input, else false
   */
  public static boolean matches(String patternString, int patternOptions, String input) {
    return Pattern.compile(patternString, patternOptions)
        .matcher(input)
        .matches();
  }

  /**
   * Get the first matchNumbers occurrences of the regex in the input string.
   *
   * @param patternString  Pattern to match.
   * @param patternOptions Regex flags.
   * @param matchNumbers   -1 means get all matches, 0 means none.
   * @param input          Input string to find matches in.
   * @return List of matches where each match is a list of groups.
   */
  public static List<List<String>> getFirstNumbersMatches(
      String patternString,
      int patternOptions,
      int matchNumbers,
      String input
  ) throws InvalidRegexException {

    // Ensure that the input regex is valid
    if (!isValidRegex(patternString)) {
      throw new InvalidRegexException(patternString);
    }

    List<List<String>> matches = new ArrayList<>();
    // If no matches are to be found. return empty list.
    if (matchNumbers == 0) {
      return matches;
    } else if (matchNumbers < -1) {
      throw new IllegalArgumentException(
          I18nUtility.getFormattedString(
              "RegexUtility.exception.negativeFirstNumberOfMatches",
              matchNumbers
          )
      );
    }

    Pattern patternObj = Pattern.compile(patternString, patternOptions);
    Matcher matcher = patternObj.matcher(input);

    int i = 0;
    while (matcher.find()) {
      matches.add(IntStream.range(0, matcher.groupCount() + 1)
          .boxed()
          .map(matcher::group)
          .collect(Collectors.toList()));
      i++;
      if (i == matchNumbers) {
        break;
      }
    }
    return matches;
  }

  /**
   * Get all the occurrences of the regex in the input string.
   *
   * @param patternString  Pattern to match.
   * @param patternOptions Regex flags.
   * @param input          Input string to find matches in.
   * @return List of matches where each match is a list of groups.
   */
  public static List<List<String>> getAllMatches(String patternString, int patternOptions,
      String input) throws InvalidRegexException {
    return getFirstNumbersMatches(patternString, patternOptions, -1, input);
  }

  /**
   * Find identifiable personal information from the input data.
   *
   * @param data Input string.
   * @return A map of identifiable personal information to their corresponding matches.
   */
  public static Map<String, List<String>> findIdentifiablePersonalInformation(String data) {

    Properties regexFields = PropertyUtility.getProperties(
        RegexUtility.class,
        regexFieldsPropertiesFileName
    );
    return findAllMatchingStringsFromRegexPropertiesFile(regexFields, data);

  }

  /**
   * Finds all substrings inside the inputString that matches any of the regex in the provided
   * Properties object.
   *
   * @param regexFields The regex expressions to match.
   * @param inputString The inputString to find the substrings that match the provided regex
   *                    expressions.
   * @return A map of the regex expression keys to their matching substring.
   */
  public static Map<String, List<String>> findAllMatchingStringsFromRegexPropertiesFile(
      Properties regexFields,
      String inputString
  ) {
    return regexFields.entrySet()
        .parallelStream()
        .filter(
            entry -> {
              String regexPattern = entry.getValue().toString();
              if (!regexPattern.equals("")) {
                return find(regexPattern, 0, inputString);
              }
              return false;
            }
        ).collect(Collectors.toMap(entry -> entry.getKey().toString(), entry -> {
          // Not using getAllMatches due to test coverage.
          Pattern patternObj = Pattern.compile(entry.getValue().toString(), 0);
          Matcher matcher = patternObj.matcher(inputString);

          List<String> matches = new ArrayList<>();
          while (matcher.find()) {
            matches.add(matcher.group());
          }
          return matches;
        }));
  }

  private static String generateString(int length, String characterSet) {
    StringBuilder buffer = new StringBuilder();
    IntStream.range(0, length)
        .forEach(i -> buffer.append(
            characterSet
                .charAt(randomObj.nextInt(characterSet.length()))
            )
        );
    logger.debug(
        I18nUtility.getFormattedString(
            "RegexUtility.generatedStringFromCharacterSet",
            buffer.toString(),
            length,
            characterSet
        )
    );
    return buffer.toString();
  }

  /**
   * Checks if a regex expression is valid.
   *
   * @param regex The regex expression to evaluate.
   * @return True if the regex expression is valid, false otherwise.
   */
  public static boolean isValidRegex(String regex) {
    boolean result;
    try {
      Pattern.compile(regex);
      result = true;
    } catch (PatternSyntaxException e) {
      result = false;
      logger.debug(
          I18nUtility.getFormattedString("RegexUtility.exception.invalidRegex", regex)
      );
    }
    return result;
  }

  /**
   * Generate a partial regex string that matches the provided regex. Currently it does not support
   * the following: - Ranges (e.g. a-z, 1-9) - Special characters (e.g. $, &) except "." - \S, \W,
   * \D - Character classes (e.g. [...]) - Lazy quantifiers - Anchors and boundaries
   *
   * @param regex             Regex to match.
   * @param randomValueLength Maximum length of value to insert in the generated string.
   * @return The generated string.
   */
  public static String fillRandomValues(String regex, int randomValueLength)
      throws InvalidRegexException {

    if (!isValidRegex(regex)) {
      throw new InvalidRegexException(regex);
    }

    String result = regex;
    List<String> regexCharacterSets;

    regexCharacterSets = getAllMatches("\\[(.+)\\]\\*", 0, result)
        .stream()
        .map(matchList -> matchList.get(1))
        .distinct()
        .collect(Collectors.toList());
    logger.debug(String.format(I18nUtility.getString("RegexUtility.pattern.count"), "[]*",
        regexCharacterSets.size()));
    for (String regexCharacterSet : regexCharacterSets) {
      result = result.replaceAll(
          String.format("\\[%s\\]\\*", regexCharacterSet),
          generateString(randomObj.nextInt(randomValueLength + 1), regexCharacterSet)
      );
    }

    //[]+
    regexCharacterSets = getAllMatches("\\[(.+)\\]\\+", 0, result)
        .stream()
        .map(matchList -> matchList.get(1))
        .distinct()
        .collect(Collectors.toList());
    logger.debug(String.format(I18nUtility.getString("RegexUtility.pattern.count"), "[]+",
        regexCharacterSets.size()));
    for (String regexCharacterSet : regexCharacterSets) {
      result = result.replaceAll(
          String.format("\\[%s\\]\\+", regexCharacterSet),
          generateString(randomObj.nextInt(randomValueLength + 1), regexCharacterSet)
      );
    }

    regexCharacterSets = getAllMatches("\\[(.*)\\]", 0, result)
        .stream()
        .map(matchList -> matchList.get(1))
        .distinct()
        .collect(Collectors.toList());
    logger.debug(String.format(I18nUtility.getString("RegexUtility.pattern.count"), "[]",
        regexCharacterSets.size()));
    for (String regexCharacterSet : regexCharacterSets) {
      result = result.replaceAll(
          String.format("\\[%s\\]", regexCharacterSet),
          generateString(1, regexCharacterSet)
      );
    }

    // Scenario when there special character . is provided in the inputPattern.
    logger.debug(String.format(I18nUtility.getString("RegexUtility.pattern.count"), ".*",
        RegexUtility.getAllMatches("(^|[^\\\\])\\.\\*", 0, result).size()));
    result = result.replaceAll(
        "(^|[^\\\\])\\.\\*",
        "$1" + generateString(
            randomObj.nextInt(randomValueLength + 1),
            wordCharactersRegex)
    );

    logger.debug(String.format(I18nUtility.getString("RegexUtility.pattern.count"), ".+",
        RegexUtility.getAllMatches("(^|[^\\\\])\\.\\+", 0, result).size()));
    result = result.replaceAll(
        "(^|[^\\\\])\\.\\+",
        "$1" + generateString(randomValueLength, wordCharactersRegex
        )
    );

    logger.debug(String.format(I18nUtility.getString("RegexUtility.pattern.count"), ".",

        RegexUtility.getAllMatches("(^|[^\\\\])\\.", 0, result).size()));
    result = result.replaceAll(
        "(^|[^\\\\])\\.",
        "$1" + generateString(1, wordCharactersRegex
        )
    );

    // Case for \w in inputPattern.
    logger.debug(String
        .format(I18nUtility.getString("RegexUtility.pattern.count"), "\\w*",
            RegexUtility.getAllMatches("\\\\w\\*", 0, result).size()));
    result = result.replaceAll("\\\\w\\*",
        generateString(randomObj.nextInt(randomValueLength + 1), wordCharactersRegex));

    logger.debug(String
        .format(I18nUtility.getString("RegexUtility.pattern.count"), "\\w+",
            RegexUtility.getAllMatches("\\\\w\\+", 0, result).size()));
    result = result.replaceAll("\\\\w\\+", generateString(randomValueLength, wordCharactersRegex));

    logger.debug(String.format(I18nUtility.getString("RegexUtility.pattern.count"), "\\w",
        RegexUtility.getAllMatches("\\\\w", 0, result).size()));
    result = result.replaceAll("\\\\w", generateString(1, wordCharactersRegex));

    // Case for \d in inputPattern.
    logger.debug(String
        .format(I18nUtility.getString("RegexUtility.pattern.count"), "\\d*",
            RegexUtility.getAllMatches("\\\\d\\*", 0, result).size()));
    result = result.replaceAll("\\\\d\\*",
        generateString(randomObj.nextInt(randomValueLength + 1), digitCharactersRegex));

    logger.debug(String
        .format(I18nUtility.getString("RegexUtility.pattern.count"), "\\d+",
            RegexUtility.getAllMatches("\\\\d\\+", 0, result).size()));
    result = result.replaceAll("\\\\d\\+", generateString(randomValueLength, digitCharactersRegex));

    logger.debug(String.format(I18nUtility.getString("RegexUtility.pattern.count"), "\\d",
        RegexUtility.getAllMatches("\\\\d", 0, result).size()));
    result = result.replaceAll("\\\\d", generateString(1, digitCharactersRegex));

    // Case for \s in inputPattern.
    logger.debug(String
        .format(I18nUtility.getString("RegexUtility.pattern.count"), "\\s*",
            RegexUtility.getAllMatches("\\\\s\\*", 0, result).size()));
    result = result.replaceAll("\\\\s\\*",
        String.join("", Collections.nCopies(randomObj.nextInt(randomValueLength + 1), " ")));

    logger.debug(String
        .format(I18nUtility.getString("RegexUtility.pattern.count"), "\\s+",
            RegexUtility.getAllMatches("\\\\s\\+", 0, result).size()));
    result = result
        .replaceAll("\\\\s\\+", String.join("", Collections.nCopies(randomValueLength, " ")));

    logger.debug(String.format(I18nUtility.getString("RegexUtility.pattern.count"), "\\s",
        RegexUtility.getAllMatches("\\\\s", 0, result).size()));
    result = result.replaceAll("\\\\s", " ");

    return result;
  }

  /**
   * Generate regex expression for the following numbers.
   *
   * @param number   The number to generate a regex based on.
   * @param sqlRegex If true, we generate a regex that can be used with SQL queries, else it
   *                 generates a normal regex
   * @return The generated regexps, out of which even if one of them match a value, it is lesser
   *     than the specified number.
   */
  public static String[] generateRegexExpressionsForPositiveNumbersLesserThan(int number,
      Comparison comparison, boolean sqlRegex) {

    if (number < 0 || comparison == Comparison.GREATER
        || comparison == Comparison.GREATER_OR_EQUAL || comparison == Comparison.UNEQUAL) {
      throw new IllegalArgumentException();
    }

    String valueString = Integer.toString(number);
    int[] digits = valueString.chars().map(c -> c - '0').toArray();
    List<String> regexExpressions = new ArrayList<>();

    String allPossibleDigitValues = sqlRegex ? "[0-9]" : "\\d";

    if (comparison == Comparison.EQUAL || comparison == Comparison.LESSER_OR_EQUAL) {
      regexExpressions.add(Integer.toString(number));
    }

    // Generates regexExpressions to match all numbers with the same number of digits,
    // but lesser in value
    // Generates regex to numbers that are lesser in number of digits
    if (comparison == Comparison.LESSER || comparison == Comparison.LESSER_OR_EQUAL) {
      IntStream.range(1, digits.length - 1)
          .filter(digitIndex -> digits[digitIndex] > 0)
          .mapToObj(digitIndex -> valueString.substring(0, digitIndex)
              + "[0-" + Math.max(digits[digitIndex] - 1, 0) + "]"
              + allPossibleDigitValues.repeat(digits.length - (digitIndex + 1))
          )
          .forEach(regexExpressions::add);
      if (digits[digits.length - 1] > 0) {
        regexExpressions.add(valueString.substring(0, valueString.length() - 1)
            + "[0-" + Math.max(digits[digits.length - 1] - 1, 0) + "]");
      }
      IntStream.range(1, digits.length)
          .mapToObj(index -> String.format("^%s$", allPossibleDigitValues.repeat(index)))
          .forEach(regexExpressions::add);
    }

    return regexExpressions.toArray(new String[0]);
  }

}
