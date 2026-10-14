package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ItemRequestResponse {
    private Long id;
    private String name;
    private Long ownerId;
    private Long requestId;
}
