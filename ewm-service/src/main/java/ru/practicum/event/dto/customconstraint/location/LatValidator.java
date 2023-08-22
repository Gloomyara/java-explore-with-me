package ru.practicum.event.dto.customconstraint.location;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class LatValidator implements ConstraintValidator<LatValidation, Double> {

    private double min = -90.0;
    private double max = 90.0;

    @Override
    public boolean isValid(Double object, ConstraintValidatorContext constraintContext) {
        if (object == null) {
            return true;
        }
        if (object < min) {
            return false;
        }
        return object <= max;
    }
}
