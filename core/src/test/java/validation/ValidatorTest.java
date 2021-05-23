package validation;


import static com.arbiter.core.validation.NumberValidationFunctions.intBetween;
import static com.arbiter.core.validation.StringValidationFunctions.length;
import static com.arbiter.core.validation.StringValidationFunctions.oneOf;
import static com.arbiter.core.validation.ValidationRule.requiredRule;
import static com.arbiter.core.validation.ValidationRule.rule;
import static com.arbiter.core.validation.ValidationSchema.schema;
import static com.arbiter.core.validation.ValidationTypes.listLastRoundsValidationType;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.arbiter.core.dto.round.FindLastRoundsDto;
import com.arbiter.core.exception.ValidationException;
import com.arbiter.core.validation.ValidationType;
import com.arbiter.core.validation.Validator;
import data.ValidationTestData;
import data.ValidationTestData.Cat;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


@DisplayName("Validator tests")
class ValidatorTest {

  @Test
  @DisplayName("Validator successfully processed FindLastRoundsDto by listLastRoundsValidationType")
  void testSuccessfulValidation() {
    FindLastRoundsDto dto = new FindLastRoundsDto("S2|2020", 1000);
    Validator.validate(dto, listLastRoundsValidationType);
  }

  @Test
  @DisplayName("Validator unsuccessfully processed FindLastRoundsDto with invalid season")
  void testInvalidSeason() {
    FindLastRoundsDto dto = new FindLastRoundsDto("S22|4", 3004);
    Throwable throwable = Assertions.assertThrows(ValidationException.class,
        () -> Validator.validate(dto, listLastRoundsValidationType));
    assertEquals(1, throwable.getMessage().split("\\.").length);
  }

  @Test
  @DisplayName("Validator unsuccessfully processed FindLastRoundsDto with invalid qty")
  void testInvalidQty() {
    FindLastRoundsDto dto = new FindLastRoundsDto("S2|2020", 40000);
    Throwable throwable = Assertions.assertThrows(ValidationException.class,
        () -> Validator.validate(dto, listLastRoundsValidationType));
    assertEquals(1, throwable.getMessage().split("\\.").length);

  }

  @Test
  @DisplayName("Validator unsuccessfully processed FindLastRoundsDto with 2 validation errors")
  void testInvalidBothValues() {
    FindLastRoundsDto dto = new FindLastRoundsDto("S22|4", 40000);
    Throwable throwable = Assertions.assertThrows(ValidationException.class,
        () -> Validator.validate(dto, listLastRoundsValidationType));
    assertEquals(2, throwable.getMessage().split("\\.").length);
  }

  @Test
  @DisplayName("Validator unsuccessfully processed complex type")
  void complexValidationTest() {
    ValidationType<Cat> catValidationType = c ->
        schema()
            .withRule(rule(c.sound, "sound", oneOf("mew", "pur")))
            .withRule(rule(c.hungryPercentage, "hungryPrecentage", intBetween(0, 100)));

    ValidationType<ValidationTestData.Person> personValidationType = p ->
        schema()
            .withRule(requiredRule(p.age, "age", intBetween(0, 130)))
            .withRule(requiredRule(p.name, "name", length(2, 5), oneOf("Kate", "John")))
            .withRule(requiredRule(p.cat, "cat", catValidationType));

    ValidationTestData.Person p = new ValidationTestData.Person("Joko", 23,
        new ValidationTestData.Cat("skibidi", 100));

    Throwable throwable = Assertions.assertThrows(ValidationException.class,
        () -> Validator.validate(p, personValidationType));
    assertEquals(2, throwable.getMessage().split("\\.").length);
  }
}
