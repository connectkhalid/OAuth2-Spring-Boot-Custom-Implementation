/**
 * Created by Mohammad Khalid Hasan|| BJIT-R&D
 * Since: 5/2/2024
 * Version: 1.0
 */

package org.bjit.oauth.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.SneakyThrows;
import org.passay.*;

import java.util.Arrays;
import java.util.List;

public class PasswordConstraintValidator implements ConstraintValidator<ValidPassword, String> {
    @Override
    public void initialize(ValidPassword constraintAnnotation) {
    }

    @SneakyThrows
    @Override
    public boolean isValid(String Password, ConstraintValidatorContext context) {
//        Properties props = new Properties();
//        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("passay.properties");
//        props.load(inputStream);
//        MessageResolver resolver = new PropertiesMessageResolver(props);

        PasswordValidator validator = new PasswordValidator(Arrays.asList(
                // length between 8 and 16 characters
                new LengthRule(8,16),
                // at least one upper-case character
                new UppercaseCharacterRule(1),
                // at least one lower-case character
                new LowercaseCharacterRule(1),
                // at least one digit character
                new DigitCharacterRule(1),
                // at least one symbol (special character)
                new SpecialCharacterRule(1),
                // no sequential number like-6789
                new NumericalSequenceRule(4, false),
                // no sequential number like-abc
                new AlphabeticalSequenceRule(3, false),
                // no quarty key sequence allowed-asdg
                new QwertySequenceRule(4, false),
                // no whitespace
                new WhitespaceRule(),
                // rejects passwords that contain a sequence
                // of >= 5 characters alphabetical  (e.g. abcdef)
                new AlphabeticalSequenceRule(5, false)

        ));
        RuleResult result = validator.validate(new PasswordData(Password));

        if (result.isValid()){
            return true;
        } else {
            List<String> messages = validator.getMessages(result);
            String messageTemplate = String.join(",", messages);
            context.buildConstraintViolationWithTemplate(messageTemplate)
                    .addConstraintViolation()
                    .disableDefaultConstraintViolation();
            return false;
        }
    }
}
