package ru.practicum.shareit.user;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UpdateUserRequest {
    private long id;
    private String name;
    @Email
    private String email;
}
