package ru.practicum.shareit;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemDto;

import java.time.LocalDateTime;
import java.time.Month;

import static org.assertj.core.api.Assertions.assertThat;


@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemDtoTest {
    private final JacksonTester<CommentDto> commentDtoJson;
    private final JacksonTester<ItemDto> itemDtoJson;
    private final JacksonTester<NewItemDto> newItemDtoJson;

    @Test
    void testCommentDto() throws Exception {
        CommentDto commentDto = CommentDto.builder()
                .id(1).text("comm").authorName("user")
                .created(LocalDateTime.of(2026, Month.SEPTEMBER, 12, 13, 40)).build();
        JsonContent<CommentDto> result = commentDtoJson.write(commentDto);
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo("comm");
        assertThat(result).extractingJsonPathStringValue("$.authorName").isEqualTo("user");
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo("2026-09-12T13:40:00");
    }

    @Test
    void testItemDto() throws Exception {
        ItemDto itemDto = new ItemDto(1, "item", "description",
                true, null, null, null, 10L);
        JsonContent<ItemDto> result = itemDtoJson.write(itemDto);
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("item");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("description");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
        assertThat(result).extractingJsonPathNumberValue("$.requestId").isEqualTo(10);
    }

    @Test
    void testNewItemDto() throws Exception {
        NewItemDto newItemDto = new NewItemDto(1, "item", "desc", false, 10);
        JsonContent<NewItemDto> result = newItemDtoJson.write(newItemDto);
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("item");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("desc");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(false);
        assertThat(result).extractingJsonPathNumberValue("$.ownerId").isEqualTo(10);
    }
}
