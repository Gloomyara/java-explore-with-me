package ru.practicum.event.dto.customconstraint;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({PARAMETER, FIELD, TYPE})
@Retention(RUNTIME)
@Repeatable(EventDateConstraint.List.class)
@Constraint(validatedBy = EventDateConstraintValidator.class)
@Documented
public @interface EventDateConstraint {

    String message() default "Error! EventDate must be at least few hours after the current time.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String value();

    @Target({PARAMETER, FIELD, TYPE})
    @Retention(RUNTIME)
    @Documented
    @interface List {

        EventDateConstraint[] value();
    }
}
