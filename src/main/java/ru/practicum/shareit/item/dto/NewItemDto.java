package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NewItemDto {
    private long id;
    @NotBlank
    private String name;
    @Size(message = "Максимальная длина описания 500 символов", max = 500)
    private String description;
    private boolean available;
    private long ownerId;
}
