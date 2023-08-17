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
@Repeatable(PositiveTimeRange.List.class)
@Constraint(validatedBy = PositiveTimeRangeValidator.class)
@Documented
public @interface PositiveTimeRange {

    String message() default "Error! End timestamp is before start.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    @Target({PARAMETER, FIELD, TYPE})
    @Retention(RUNTIME)
    @Documented
    @interface List {

        PositiveTimeRange[] value();
    }
}
