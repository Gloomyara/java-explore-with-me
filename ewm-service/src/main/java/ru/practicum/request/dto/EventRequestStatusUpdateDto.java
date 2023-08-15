package ru.practicum.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventRequestStatusUpdateDto {
    private Set<Long> requestIds;
    private Status status;

    public enum Status {
        CONFIRMED,
        PENDING,
        REJECTED,
        CANCELED
    }
}
