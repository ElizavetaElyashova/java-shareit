package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;


@AllArgsConstructor
@Builder
@Data
public class ItemRequestDto {
    private long id;
    private String description;
    private LocalDateTime created;
}
