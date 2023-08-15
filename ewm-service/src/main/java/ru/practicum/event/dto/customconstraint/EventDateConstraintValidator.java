package ru.practicum.event.dto.customconstraint;

import ru.practicum.event.dto.EventDtoIn;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;

public class EventDateConstraintValidator implements ConstraintValidator<EventDateConstraint, EventDtoIn> {

    private int timeRange;

    @Override
    public void initialize(EventDateConstraint constraintAnnotation) {
        this.timeRange = Integer.parseInt(constraintAnnotation.value());
    }

    @Override
    public boolean isValid(EventDtoIn object, ConstraintValidatorContext constraintContext) {
        if (object == null || object.getEventDate() == null) {
            return true;
        }
        if (object.getEventDate().isBefore(LocalDateTime.now())) {
            return false;
        }
        return object.getEventDate().isAfter(LocalDateTime.now().plusHours(timeRange));
    }
}
