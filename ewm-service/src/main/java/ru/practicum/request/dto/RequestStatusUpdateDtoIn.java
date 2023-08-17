package ru.practicum.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestStatusUpdateDtoIn {
    private Set<Long> requestIds;
    private RequestStatus status;

    public enum RequestStatus {
        CONFIRMED,
        REJECTED
    }
}
