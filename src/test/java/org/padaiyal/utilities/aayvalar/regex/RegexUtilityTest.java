package org.padaiyal.utilities.aayvalar.regex;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.converter.ConvertWith;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.padaiyal.utilities.I18nUtility;
import org.padaiyal.utilities.PropertyUtility;
import org.padaiyal.utilities.aayvalar.regex.abstractions.Comparison;
import org.padaiyal.utilities.aayvalar.regex.exceptions.InvalidRegexException;
import org.padaiyal.utilities.aayvalar.testutils.ExceptionClassConverter;
import org.padaiyal.utilities.aayvalar.testutils.StringArrayConverter;

/**
 * Tests RegexUtility.
 */
class RegexUtilityTest {

  static {
    I18nUtility.addResourceBundle(
        RegexUtilityTest.class,
        RegexUtilityTest.class.getSimpleName(),
        Locale.US);
  }

  /**
   * Gets the capture group expressions from the regex expression.
   *
   * @param regexPattern The regex expression to get the capture group expressions from.
   * @return A list of all the capture group expressions.
   * @throws InvalidRegexException If an invalid regex expression is provided.
   */
  private static List<String> getCaptureGroupsRegex(String regexPattern)
      throws InvalidRegexException {
    return RegexUtility.getAllMatches("\\([^\\(]*\\)", 0, regexPattern)
        .parallelStream()
        .map(matches -> {
          Assertions.assertEquals(1, matches.size());
          return matches.get(0);
        }).collect(Collectors.toList());
  }

  /**
   * Test if a string matches a regex pattern.
   *
   * @param regexPattern The regex pattern to match the string.
   * @param inputString  The string to match the regex pattern.
   */
  @ParameterizedTest
  @CsvSource({
      "\\d+\\s+\\S+, 1290 TestString"
  })
  void testMatches(String regexPattern, String inputString) {
    Assertions.assertTrue(
        RegexUtility.matches(
            regexPattern,
            0,
            inputString
        ),
        I18nUtility.getFormattedString(
            "RegexUtilityTest.noMatchesFoundForStringMessage",
            inputString,
            regexPattern
        )
    );
  }


  /**
   * Test get all matches with regex expression that do not have capturing groups.
   *
   * @param regexPattern       The regex pattern to test.
   * @param inputString        The input string to test.
   * @param expectedMatchArray The expected matches.
   * @throws InvalidRegexException If an invalid regex pattern is provided.
   */
  @ParameterizedTest
  @CsvSource({
      "\\d+\\.\\d+, 99.99999999, '99.99999999'",
      "\\d, 99, '9,9'",
      "\\w+, 'hello world', 'hello,world'",
      "\\w, 'hello world', 'h,e,l,l,o,w,o,r,l,d'"
  })
  void testGetAllMatchesWithoutCaptureGroups(
      String regexPattern,
      String inputString,
      @ConvertWith(StringArrayConverter.class) String[] expectedMatchArray
  ) throws InvalidRegexException {

    List<String> expectedMatches = Arrays.asList(expectedMatchArray);

    List<String> actualMatches = RegexUtility.getAllMatches(
        regexPattern,
        0,
        inputString).parallelStream()
        .map(matches -> {
          Assertions.assertEquals(1, matches.size());
          return matches.get(0);
        })
        .collect(Collectors.toList());
    Assertions.assertEquals(expectedMatches, actualMatches);
  }

  /**
   * Test get all matches with regex expression that do not have capturing groups.
   *
   * @param regexPattern                 The regex pattern to test.
   * @param inputString                  The input string to test.
   * @param expectedNumberOfTotalMatches The expected number of full matches. It doesn't consider
   *                                     the number of capturing groups.
   * @throws InvalidRegexException If an invalid regex pattern is provided.
   */
  @ParameterizedTest
  @CsvSource({
      "([A-Za-z0-9][A-Za-z0-9-]+)\\.([A-Za-z0-9][A-Za-z0-9-]+), "
          + "some.domain hello.com, 2",
      "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\"
          + ".(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\"
          + ".(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\"
          + ".(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?), 192.125.1.1 127.0.0.0 125.1.1.1, 3"
  })
  void testGetAllMatchesWithCapturingGroups(
      String regexPattern,
      String inputString,
      int expectedNumberOfTotalMatches
  ) throws InvalidRegexException {

    List<String> regexPatternAndGroups = getCaptureGroupsRegex(regexPattern);
    regexPatternAndGroups.add(0, regexPattern);
    List<List<String>> actualMatches =
        RegexUtility.getAllMatches(regexPattern, 0, inputString);

    Assertions.assertEquals(expectedNumberOfTotalMatches, actualMatches.size());

    actualMatches.parallelStream()
        .forEach(matches ->
            IntStream.range(0, matches.size())
                .forEach(index -> {
                  String match = matches.get(index);
                  String regexExpression = regexPatternAndGroups.get(index);

                  Assertions.assertTrue(
                      RegexUtility.matches(regexExpression, 0, match)
                  );
                }));
  }

  /**
   * Test getting the top first numbers of matches without capturing groups in the regex
   * expression.
   *
   * @param regexPattern    The regex pattern to test.
   * @param input           The input string.
   * @param topNumbers      The top first numbers of matches to retrieve.
   * @param expectedMatches The expected matches.
   * @throws InvalidRegexException If an invalid regex exception is provided.
   */
  @ParameterizedTest
  @CsvSource({
      "\\w, abcdedf, 3, 'a,b,c'",
      "\\w, abcdedf, 0, ''",
      "\\w, abc, 4, 'a,b,c'",
      "\\w, 23, 2, '2,3'",
      "\\d, 23, 2, '2,3'",
      "\\w+, Hello World, 1, 'Hello'",
      "\\d+, 23, 2, '23'",
      "\\w*, Hello. World stuff, 4, 'Hello,,,World'",
  })
  void testGetFirstNumbersMatchesWithoutCapturingGroups(
      String regexPattern,
      String input,
      int topNumbers,
      @ConvertWith(StringArrayConverter.class) String[] expectedMatches
  ) throws InvalidRegexException {
    List<String> expectedTopNumberMatches = Arrays.asList(expectedMatches);

    List<String> actualTopNumberMatches =
        RegexUtility.getFirstNumbersMatches(regexPattern, 0, topNumbers, input)
            .parallelStream()
            .map((matches) -> {
              Assertions.assertEquals(1, matches.size());
              return matches.get(0);
            })
            .collect(Collectors.toList());
    Assertions.assertEquals(expectedTopNumberMatches, actualTopNumberMatches);
  }

  /**
   * Test getting the top first numbers of matches with capturing groups in the regex expression.
   *
   * @param regexPattern The regex pattern to test.
   * @param input        The input string.
   * @param topNumbers   The top first numbers of matches to retrieve.
   * @throws InvalidRegexException If an invalid regex exception is provided.
   */
  @ParameterizedTest
  @CsvSource({
      "([A-Za-z0-9][A-Za-z0-9-]+)\\.([A-Za-z0-9][A-Za-z0-9-]+), "
          + "some.domain hello.com, 3",
      "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\"
          + ".(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\"
          + ".(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\"
          + ".(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?), 192.125.1.1 127.0.0 125.1.1.1, 2"
  })
  void testGetFirstNumbersMatchesWithCapturingGroups(
      String regexPattern,
      String input,
      int topNumbers
  ) throws InvalidRegexException {

    List<String> regexPatternAndGroups = getCaptureGroupsRegex(regexPattern);
    regexPatternAndGroups.add(0, regexPattern);
    List<List<String>> actualMatches =
        RegexUtility.getFirstNumbersMatches(regexPattern, 0, topNumbers, input);

    Assertions.assertTrue(topNumbers >= actualMatches.size());

    actualMatches.parallelStream()
        .forEach(matches ->
            IntStream.range(0, matches.size())
                .forEach(index -> {
                  String match = matches.get(index);
                  String regexExpression = regexPatternAndGroups.get(index);

                  Assertions.assertTrue(
                      RegexUtility.matches(regexExpression, 0, match)
                  );
                }));
  }

  @ParameterizedTest
  @CsvSource({
      "\\w",
      "\\w+",
      "\\w*",
      "\\s",
      "\\s+",
      "\\s*",
      "\\d",
      "\\d+",
      "\\d*",
      "[abz]",
      "[abz]*",
      "[abz]+",
      "hello.*",
      ".*",
      "hello.+",
      ".+",
      "hello.",
      "."
  })
  void testFillRandomValues(String regexToTest) throws InvalidRegexException {
    String generatedString = RegexUtility.fillRandomValues(regexToTest, 100);
    Assertions.assertTrue(
        Pattern.matches(regexToTest, generatedString),
        I18nUtility.getFormattedString(
            "RegexUtilityTest.failedToGenerateStringFromRegexMessage",
            regexToTest,
            generatedString
        )
    );

  }

  // TODO: Add more test cases.
  /**
   * Test getting personal identifiable information from input string.
   *
   * @param stringToMatch The string to find the personal identifiable information from.
   * @param isResultEmpty Flag used to test the actual number of personal identifiable information
   *                      fields found.
   */
  @ParameterizedTest
  @CsvSource({
      "192.168.1.1, false",
      "1.1.1.1, false",
      "192.168.1.1 1.1.1.1, false"
  })
  void testGetPersonalIdentifiableInformationFromInputString(
      String stringToMatch,
      boolean isResultEmpty
  ) {

    Map<String, List<String>> identifiedPersonalInformation = RegexUtility
        .findIdentifiablePersonalInformation(stringToMatch);

    Assertions.assertEquals(isResultEmpty, identifiedPersonalInformation.size() == 0);
    identifiedPersonalInformation.entrySet()
        .parallelStream()
        .forEach(entry -> {
          String regexExpression = PropertyUtility.getProperty(entry.getKey());
          entry.getValue()
              .parallelStream()
              .forEach(match ->
                  Assertions.assertTrue(
                      RegexUtility.matches(regexExpression, 0, match)
                  ));
        });

  }

  /**
   * Test getting regex expressions for numbers are less or equal to the provided number.
   *
   * @param number     Number to use in the comparison regex
   * @param comparison Comparison operation to apply
   * @param sqlRegex   If true, it generates a SQL compatible regex, Else a regular regex.
   */
  @ParameterizedTest
  @CsvSource(
      {
          "14000, EQUAL, false",
          "1125, LESSER, true",
          "1125, LESSER_OR_EQUAL, false",
          "14000, LESSER_OR_EQUAL, false",
      }
  )
  void testGenerateRegexExpressionsForPositiveNumbersLesserThan(
      int number,
      Comparison comparison,
      boolean sqlRegex
  ) {
    int rangeStart = 0;
    int rangeEnd = number;

    String[] regexExpressions =
        RegexUtility.generateRegexExpressionsForPositiveNumbersLesserThan(
            number,
            comparison,
            sqlRegex
        );

    if (comparison == Comparison.EQUAL) {
      rangeStart = number;
      rangeEnd = number + 1;
    } else if (comparison == Comparison.LESSER_OR_EQUAL) {
      rangeEnd++;
    }
    IntStream.range(rangeStart, rangeEnd)
        .forEach(testNumber -> {
          boolean match = Arrays.stream(regexExpressions)
              .anyMatch(regexp -> RegexUtility.matches(regexp, 0, Integer.toString(testNumber)));
          Assertions.assertEquals(match, comparison.evaluateComparison(testNumber, number));
        });
  }


  /**
   * Test conditions that are not supported for equivalent regex generation.
   *
   * @param number     Number to use in the comparison regex
   * @param comparison Comparison operation to apply
   * @param sqlRegex   If true, it generates a SQL compatible regex, Else a regular regex.
   */
  @ParameterizedTest
  @CsvSource(
      {
          "14000, GREATER, false",
          "14000, GREATER_OR_EQUAL, false",
          "-14000, EQUAL, false",
          "-14000, LESSER, false",
          "-14000, LESSER_OR_EQUAL, false",
          "-14000, GREATER, false",
          "-14000, GREATER_OR_EQUAL, false",
          "-14000, UNEQUAL, false",
          "14000, UNEQUAL, false",
          "-14000, , false",
          "-14000, , true"
      }
  )
  void testGenerateRegexExpressionsForNumbersLesserOrEqualWithInvalidInputs(
      int number,
      Comparison comparison,
      boolean sqlRegex
  ) {
    Assertions.assertThrows(
        IllegalArgumentException.class,
        () -> RegexUtility.generateRegexExpressionsForPositiveNumbersLesserThan(
            number,
            comparison,
            sqlRegex
        )
    );
  }

  /**
   * Test if the provided regex pattern is valid.
   *
   * @param regexPattern       The regex expression to test.
   * @param expectedValidation The expected validation of the regex expression.
   */
  @ParameterizedTest
  @CsvSource({
      "\\w, true",
      "[, false"
  })
  public void testIsValidRegex(String regexPattern, boolean expectedValidation) {
    Assertions.assertEquals(expectedValidation, RegexUtility.isValidRegex(regexPattern));
  }

  /**
   * Test get the first number of matches with invalid inputs.
   *
   * @param regexPattern The regex pattern to test.
   * @param patternOptions The pattern options to use.
   * @param matchNumbers The firs numbers of matches.
   * @param inputString The input to test.
   * @param expectedException The expected exception to be thrown.
   */
  @ParameterizedTest
  @CsvSource({
      ", 1, 1, Something, NullPointerException.class",
      "(, 1, 1, Something, InvalidRegexException.class",
      "\\w, -1, 1, Something, IllegalArgumentException.class",
      "\\w, 1, -199, Something, IllegalArgumentException.class",
  })
  public void testGetFirstNumbersOfMatchesWithInvalidInputs(
      String regexPattern,
      int patternOptions,
      int matchNumbers,
      String inputString,
      @ConvertWith(ExceptionClassConverter.class) Class<? extends Exception> expectedException
  ) {
    Assertions.assertThrows(
        expectedException,
        () -> RegexUtility.getFirstNumbersMatches(
            regexPattern,
            patternOptions,
            matchNumbers,
            inputString
        ));
  }

  /**
   * Test get the fill random values from a regex expressions with invalid inputs.
   *
   * @param regexPattern The regex pattern to test.
   * @param randomValueLength The random value length to test.
   * @param expectedException The expected exception to be thrown.
   */
  @ParameterizedTest
  @CsvSource({
      ", 1, NullPointerException.class",
      "(, 1, InvalidRegexException.class",
      "\\w, -1, IllegalArgumentException.class",
  })
  public void testFillRandomValuesWithInvalidInputs(
      String regexPattern,
      int randomValueLength,
      @ConvertWith(ExceptionClassConverter.class) Class<? extends Exception> expectedException
  ) {
    Assertions.assertThrows(
        expectedException,
        () -> RegexUtility.fillRandomValues(
            regexPattern, randomValueLength
        ));
  }

  /**
   * Test initialize dependant values when there is an IOException.
   */
  @Test
  public void testInitializeDependantValuesWithInputAndOutputException() {
    try (
        MockedStatic<PropertyUtility> mockedProperties = Mockito.mockStatic(PropertyUtility.class)
    ) {
      mockedProperties.when(
          () -> PropertyUtility.addPropertyFile(Mockito.any(), Mockito.anyString())
      ).thenThrow(IOException.class);

      Assertions.assertDoesNotThrow(() -> RegexUtility.initializeDependantValue.run());
    }
    RegexUtility.initializeDependantValue.run();
  }

}
