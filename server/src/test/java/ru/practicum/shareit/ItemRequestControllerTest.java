package ru.practicum.shareit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.ItemRequestController;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithResponsesDto;
import ru.practicum.shareit.request.dto.NewItemRequestDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ItemRequestController.class)
public class ItemRequestControllerTest {
    @MockBean
    ItemRequestService itemRequestService;

    @Autowired
    ObjectMapper mapper;
    @Autowired
    MockMvc mvc;

    @Test
    void create() throws Exception {
        NewItemRequestDto newItemRequestDto = new NewItemRequestDto("description");
        ItemRequestDto itemRequestDto = new ItemRequestDto(1, newItemRequestDto.getDescription(), LocalDateTime.now());
        Mockito.when(itemRequestService.create(newItemRequestDto, 1))
                .thenReturn(itemRequestDto);
        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(newItemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription())))
                .andExpect(jsonPath("$.created", is(itemRequestDto.getCreated().toString())));
    }

    @Test
    void findUserRequests() throws Exception {
        Mockito.when(itemRequestService.findUserRequests(1))
                .thenReturn(List.of());
        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void findOtherUsersRequests() throws Exception {
        Mockito.when(itemRequestService.findOtherUsersRequests(1))
                .thenReturn(List.of());
        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void findRequest() throws Exception {
        ItemRequestWithResponsesDto itemRequestWithResponsesDto = ItemRequestWithResponsesDto.builder().id(1)
                .description("desc").created(LocalDateTime.now()).items(List.of()).build();
        Mockito.when(itemRequestService.findRequest(1))
                .thenReturn(itemRequestWithResponsesDto);
        mvc.perform(get("/requests/1")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestWithResponsesDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestWithResponsesDto.getDescription())))
                .andExpect(jsonPath("$.created", is(itemRequestWithResponsesDto.getCreated().toString())))
                .andExpect(jsonPath("$.items", is(List.of())));

    }
}
