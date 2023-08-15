package ru.practicum.event.dto.customconstraint;

import ru.practicum.event.dto.query.Query;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PositiveTimeRangeValidator implements ConstraintValidator<PositiveTimeRange, Query> {

    @Override
    public boolean isValid(Query object, ConstraintValidatorContext constraintContext) {
        if (object == null || object.getRangeStart() == null || object.getRangeEnd() == null) {
            return true;
        }
        return object.getRangeStart().isBefore(object.getRangeEnd());
    }
}
