package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.request.ItemRequestResponse;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@Builder
@Data
public class ItemRequestWithResponsesDto {
    private long id;
    private String description;
    private LocalDateTime created;
    private List<ItemRequestResponse> items;
}
