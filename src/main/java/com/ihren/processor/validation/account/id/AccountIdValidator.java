package com.ihren.processor.validation.account.id;

import com.ihren.processor.constant.Account;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class AccountIdValidator implements ConstraintValidator<AccountId, CharSequence> {

    private Set<String> allowedValues;

    @Override
    public void initialize(AccountId annotation) {
        this.allowedValues = Arrays.stream(Account.values())
                .map(Account::getId)
                .collect(Collectors.toSet());
    }

    @Override
    public boolean isValid(CharSequence value, ConstraintValidatorContext context) {
        return Optional.ofNullable(value)
                .map(CharSequence::toString)
                .filter(allowedValues::contains)
                .map(ifContains -> true)
                .orElseGet(() -> {
                    context.disableDefaultConstraintViolation();
                    context.buildConstraintViolationWithTemplate(
                                    String.format(
                                            "inputItem.loyaltyAccountId value '%s' is not allowed. Allowed values are: %s",
                                            value,
                                            String.join(", ", allowedValues)
                                    )
                            )
                            .addConstraintViolation();
                    return false;
                });
    }
}
