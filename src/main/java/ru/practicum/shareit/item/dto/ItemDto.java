package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingDatesDto;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@Data
@AllArgsConstructor
public class ItemDto {
    private long id;
    @NotBlank
    private String name;
    @NotBlank
    @Size(message = "Максимальная длина описания 500 символов", max = 500)
    private String description;
    @NotNull
    private Boolean available;
    private BookingDatesDto lastBooking;
    private BookingDatesDto nextBooking;
    List<CommentDto> comments;
}
