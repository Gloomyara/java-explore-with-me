package ru.practicum.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.time.LocalDateTime;

import static ru.practicum.dto.UtilConstants.DATE_TIME_PATTERN;

@Data
public class EndpointHit {
    @NotBlank
    private String app;
    @NotBlank
    private String uri;
    @NotBlank
    @Pattern(message = "Incorrect ip pattern",
            regexp = "^((25[0-5]|(2[0-4]|1\\d|[1-9]|)\\d)\\.?\\b){4}$")
    private String ip;
    @NotNull
    @JsonFormat(pattern = DATE_TIME_PATTERN)
    private LocalDateTime timestamp;
}