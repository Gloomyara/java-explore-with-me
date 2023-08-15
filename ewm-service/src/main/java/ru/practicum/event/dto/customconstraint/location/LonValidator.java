package ru.practicum.event.dto.customconstraint.location;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class LonValidator implements ConstraintValidator<LonValidation, Double> {

    private double min = -180.0;
    private double max = 180.0;

    @Override
    public boolean isValid(Double object, ConstraintValidatorContext constraintContext) {
        if (object == null) {
            return false;
        }
        if (object < min) {
            return false;
        }
        return object <= max;
    }
}
