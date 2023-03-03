package ru.practicum.shareit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.*;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class ItemControllerTest {
    @Mock
    private ItemService service;
    @InjectMocks
    private ItemController controller;
    private final ObjectMapper mapper = new ObjectMapper();
    private MockMvc mvc;
    private ItemDto itemDto;
    private ItemDtoResponse itemDtoResponse;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();
        itemDto = new ItemDto(
                1L,
                "name",
                "description",
                true,
                1L
        );
        itemDtoResponse = new ItemDtoResponse(
                1L,
                "name",
                "description",
                true,
                new BookingDtoFotItems(
                        1L,
                        null,
                        null,
                        null,
                        1L
                ),
                new BookingDtoFotItems(
                        1L,
                        null,
                        null,
                        null,
                        1L
                ),
                List.of(new CommentDto(
                        1L,
                        "name",
                        "author",
                        null
                ))
        );
    }

    @Test
    void createTest() throws Exception {
        when(service.create(anyLong(), any()))
                .thenReturn(itemDto);

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())))
                .andExpect(jsonPath("$.requestId", is(itemDto.getRequestId()), Long.class));
    }

    @Test
    void getAllTest() throws Exception {
        when(service.getAll(anyLong(), any(), any()))
                .thenReturn(List.of(itemDtoResponse));

        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(itemDtoResponse))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemDtoResponse.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemDtoResponse.getName())))
                .andExpect(jsonPath("$[0].description", is(itemDtoResponse.getDescription())))
                .andExpect(jsonPath("$[0].available", is(itemDtoResponse.getAvailable())))
                .andExpect(jsonPath("$[0].lastBooking.id", is(itemDtoResponse.getLastBooking().getId()), Long.class))
                .andExpect(jsonPath("$[0].lastBooking.start", is(itemDtoResponse.getLastBooking().getStart())))
                .andExpect(jsonPath("$[0].lastBooking.end", is(itemDtoResponse.getLastBooking().getEnd())))
                .andExpect(jsonPath("$[0].lastBooking.status", is(itemDtoResponse.getLastBooking().getStatus())))
                .andExpect(jsonPath("$[0].lastBooking.bookerId", is(itemDtoResponse.getLastBooking().getBookerId()), Long.class))
                .andExpect(jsonPath("$[0].nextBooking.id", is(itemDtoResponse.getNextBooking().getId()), Long.class))
                .andExpect(jsonPath("$[0].nextBooking.start", is(itemDtoResponse.getNextBooking().getStart())))
                .andExpect(jsonPath("$[0].nextBooking.end", is(itemDtoResponse.getNextBooking().getEnd())))
                .andExpect(jsonPath("$[0].nextBooking.status", is(itemDtoResponse.getNextBooking().getStatus())))
                .andExpect(jsonPath("$[0].nextBooking.bookerId", is(itemDtoResponse.getNextBooking().getBookerId()), Long.class))
                .andExpect(jsonPath("$[0].comments[0].id", is(itemDtoResponse.getComments().get(0).getId()), Long.class))
                .andExpect(jsonPath("$[0].comments[0].text", is(itemDtoResponse.getComments().get(0).getText())))
                .andExpect(jsonPath("$[0].comments[0].authorName", is(itemDtoResponse.getComments().get(0).getAuthorName())))
                .andExpect(jsonPath("$[0].comments[0].created", is(itemDtoResponse.getComments().get(0).getCreated())));
    }

    @Test
    void getByIdTest() throws Exception {
        when(service.getById(anyLong(), anyLong()))
                .thenReturn(itemDtoResponse);

        mvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(itemDtoResponse))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDtoResponse.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDtoResponse.getName())))
                .andExpect(jsonPath("$.description", is(itemDtoResponse.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDtoResponse.getAvailable())))
                .andExpect(jsonPath("$.lastBooking.id", is(itemDtoResponse.getLastBooking().getId()), Long.class))
                .andExpect(jsonPath("$.lastBooking.start", is(itemDtoResponse.getLastBooking().getStart())))
                .andExpect(jsonPath("$.lastBooking.end", is(itemDtoResponse.getLastBooking().getEnd())))
                .andExpect(jsonPath("$.lastBooking.status", is(itemDtoResponse.getLastBooking().getStatus())))
                .andExpect(jsonPath("$.lastBooking.bookerId", is(itemDtoResponse.getLastBooking().getBookerId()), Long.class))
                .andExpect(jsonPath("$.nextBooking.id", is(itemDtoResponse.getNextBooking().getId()), Long.class))
                .andExpect(jsonPath("$.nextBooking.start", is(itemDtoResponse.getNextBooking().getStart())))
                .andExpect(jsonPath("$.nextBooking.end", is(itemDtoResponse.getNextBooking().getEnd())))
                .andExpect(jsonPath("$.nextBooking.status", is(itemDtoResponse.getNextBooking().getStatus())))
                .andExpect(jsonPath("$.nextBooking.bookerId", is(itemDtoResponse.getNextBooking().getBookerId()), Long.class))
                .andExpect(jsonPath("$.comments[0].id", is(itemDtoResponse.getComments().get(0).getId()), Long.class))
                .andExpect(jsonPath("$.comments[0].text", is(itemDtoResponse.getComments().get(0).getText())))
                .andExpect(jsonPath("$.comments[0].authorName", is(itemDtoResponse.getComments().get(0).getAuthorName())))
                .andExpect(jsonPath("$.comments[0].created", is(itemDtoResponse.getComments().get(0).getCreated())));
    }

    @Test
    void updateTest() throws Exception {
        when(service.update(anyLong(), anyLong(), any()))
                .thenReturn(itemDto);

        mvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())))
                .andExpect(jsonPath("$.requestId", is(itemDto.getRequestId()), Long.class));
    }

    @Test
    void searchTest() throws Exception {
        when(service.search(anyLong(), anyString(), any(), any()))
                .thenReturn(List.of(itemDto));

        mvc.perform(get("/items/search?text=text")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemDto.getName())))
                .andExpect(jsonPath("$[0].description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$[0].available", is(itemDto.getAvailable())))
                .andExpect(jsonPath("$[0].requestId", is(itemDto.getRequestId()), Long.class));
    }

    @Test
    void postTest() throws Exception {
        CommentDtoInc commentDtoInc = new CommentDtoInc("text");
        CommentDto commentDto = new CommentDto(1L, "text", "name", null);

        when(service.post(anyLong(), anyLong(), any()))
                .thenReturn(commentDto);

        mvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(commentDtoInc))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentDto.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(commentDto.getText())))
                .andExpect(jsonPath("$.authorName", is(commentDto.getAuthorName())))
                .andExpect(jsonPath("$.created", is(commentDto.getCreated())));
    }
}
