package ru.practicum.event.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.request.enums.Status;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventStatusUpdateDto {
    private Set<Long> requestIds;
    private Status status;
}
