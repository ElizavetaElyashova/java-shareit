package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NewItemDto {
    private long id;
    private String name;
    private String description;
    private boolean available;
    private long ownerId;
}
