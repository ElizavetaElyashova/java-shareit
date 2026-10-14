package ru.practicum.shareit;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.request.ItemRequestResponse;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithResponsesDto;
import ru.practicum.shareit.request.dto.NewItemRequestDto;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestDtoTest {
    private final JacksonTester<ItemRequestDto> itemRequestDtoJson;
    private final JacksonTester<ItemRequestWithResponsesDto> itemRequestWithResponsesDtoJson;
    private final JacksonTester<NewItemRequestDto> newItemRequestDtoJson;

    @Test
    void testItemRequestDto() throws Exception {
        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .id(1).description("request")
                .created(LocalDateTime.of(2026, Month.SEPTEMBER, 12, 13, 40)).build();
        JsonContent<ItemRequestDto> result = itemRequestDtoJson.write(itemRequestDto);
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("request");
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo("2026-09-12T13:40:00");
    }

    @Test
    void testItemRequestWithResponsesDto() throws Exception {
        ItemRequestResponse itemRequestResponse = new ItemRequestResponse(2L, "item", 3L, 1L);
        ItemRequestWithResponsesDto itemRequestWithResponsesDto = ItemRequestWithResponsesDto.builder()
                .id(1)
                .description("request")
                .created(LocalDateTime.of(2026, Month.SEPTEMBER, 12, 13, 40))
                .items(List.of(itemRequestResponse)).build();
        JsonContent<ItemRequestWithResponsesDto> result = itemRequestWithResponsesDtoJson.write(itemRequestWithResponsesDto);
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("request");
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo("2026-09-12T13:40:00");
        assertThat(result).extractingJsonPathNumberValue("$.items[0].id").isEqualTo(2);
        assertThat(result).extractingJsonPathStringValue("$.items[0].name").isEqualTo("item");
        assertThat(result).extractingJsonPathNumberValue("$.items[0].ownerId").isEqualTo(3);
        assertThat(result).extractingJsonPathNumberValue("$.items[0].requestId").isEqualTo(1);
    }

    @Test
    void testNewItemRequestDto() throws Exception {
        NewItemRequestDto newItemRequestDto = new NewItemRequestDto("request");
        JsonContent<NewItemRequestDto> result = newItemRequestDtoJson.write(newItemRequestDto);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("request");
    }
}
