package ru.practicum.shareit.item.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.model.User;

/**
 * TODO Sprint add-controllers.
 */

@Data
@AllArgsConstructor
public class Item {
    private long id;
    @NotBlank
    private String name;
    @Size(message = "Максимальная длина описания 500 символов", max = 500)
    private String description;
    private User owner;
    private Boolean available;
    private ItemRequest request;
}
