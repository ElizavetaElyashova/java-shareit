package ru.practicum.shareit;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;

import static org.assertj.core.api.Assertions.assertThat;


@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserDtoTest {
    private final JacksonTester<UserDto> userDtoJson;
    private final JacksonTester<UpdateUserRequest> updateUserRequestJson;

    @Test
    void testUserDto() throws Exception {
        UserDto userDto = new UserDto(1, "user", "user@user.com");
        JsonContent<UserDto> result = userDtoJson.write(userDto);
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("user");
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo("user@user.com");
    }

    @Test
    void testUpdateUserRequest() throws Exception {
        UpdateUserRequest updateUserRequest = new UpdateUserRequest(1, "user", "user@user.com");
        JsonContent<UpdateUserRequest> result = updateUserRequestJson.write(updateUserRequest);
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("user");
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo("user@user.com");
    }
}
