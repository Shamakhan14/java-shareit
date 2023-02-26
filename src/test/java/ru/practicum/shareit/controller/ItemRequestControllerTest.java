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
import ru.practicum.shareit.request.ItemRequestController;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.request.Response;
import ru.practicum.shareit.request.dto.ItemRequestDtoInc;
import ru.practicum.shareit.request.dto.ItemRequestDtoOut;
import ru.practicum.shareit.user.User;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class ItemRequestControllerTest {
    @Mock
    ItemRequestService service;
    @InjectMocks
    ItemRequestController controller;
    private final ObjectMapper mapper = new ObjectMapper();
    private MockMvc mvc;
    ItemRequestDtoInc itemRequestDtoInc;
    ItemRequestDtoOut itemRequestDtoOut;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();
        itemRequestDtoInc = new ItemRequestDtoInc("description");
        User user = new User();
        user.setId(1L);
        user.setName("name");
        user.setEmail("email@email.com");
        Response response = new Response();
        response.setId(1L);
        response.setName("name");
        response.setUserId(1L);
        response.setDescription("description");
        response.setAvailable(true);
        response.setRequestId(1L);
        itemRequestDtoOut = new ItemRequestDtoOut(
                1L,
                "description",
                user,
                null,
                List.of(response)
        );
    }

    @Test
    void createTest() throws Exception {
        when(service.create(anyLong(), any()))
                .thenReturn(itemRequestDtoOut);

        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(itemRequestDtoInc))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDtoOut.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDtoOut.getDescription())))
                .andExpect(jsonPath("$.requestor.id", is(itemRequestDtoOut.getRequestor().getId()), Long.class))
                .andExpect(jsonPath("$.requestor.name", is(itemRequestDtoOut.getRequestor().getName())))
                .andExpect(jsonPath("$.requestor.email", is(itemRequestDtoOut.getRequestor().getEmail())))
                .andExpect(jsonPath("$.created", is(itemRequestDtoOut.getCreated())))
                .andExpect(jsonPath("$.items[0].id", is(itemRequestDtoOut.getItems().get(0).getId()), Long.class))
                .andExpect(jsonPath("$.items[0].name", is(itemRequestDtoOut.getItems().get(0).getName())))
                .andExpect(jsonPath("$.items[0].userId", is(itemRequestDtoOut.getItems().get(0).getUserId()), Long.class))
                .andExpect(jsonPath("$.items[0].description", is(itemRequestDtoOut.getItems().get(0).getDescription())))
                .andExpect(jsonPath("$.items[0].available", is(itemRequestDtoOut.getItems().get(0).getAvailable())))
                .andExpect(jsonPath("$.items[0].requestId", is(itemRequestDtoOut.getItems().get(0).getRequestId()), Long.class));
    }

    @Test
    void getOwnTest() throws Exception {
        when(service.getOwn(anyLong()))
                .thenReturn(List.of(itemRequestDtoOut));

        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemRequestDtoOut.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(itemRequestDtoOut.getDescription())))
                .andExpect(jsonPath("$[0].requestor.id", is(itemRequestDtoOut.getRequestor().getId()), Long.class))
                .andExpect(jsonPath("$[0].requestor.name", is(itemRequestDtoOut.getRequestor().getName())))
                .andExpect(jsonPath("$[0].requestor.email", is(itemRequestDtoOut.getRequestor().getEmail())))
                .andExpect(jsonPath("$[0].created", is(itemRequestDtoOut.getCreated())))
                .andExpect(jsonPath("$[0].items[0].id", is(itemRequestDtoOut.getItems().get(0).getId()), Long.class))
                .andExpect(jsonPath("$[0].items[0].name", is(itemRequestDtoOut.getItems().get(0).getName())))
                .andExpect(jsonPath("$[0].items[0].userId", is(itemRequestDtoOut.getItems().get(0).getUserId()), Long.class))
                .andExpect(jsonPath("$[0].items[0].description", is(itemRequestDtoOut.getItems().get(0).getDescription())))
                .andExpect(jsonPath("$[0].items[0].available", is(itemRequestDtoOut.getItems().get(0).getAvailable())))
                .andExpect(jsonPath("$[0].items[0].requestId", is(itemRequestDtoOut.getItems().get(0).getRequestId()), Long.class));
    }

    @Test
    void getAll() throws Exception {
        when(service.getAll(anyLong(), any(), any()))
                .thenReturn(List.of(itemRequestDtoOut));

        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemRequestDtoOut.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(itemRequestDtoOut.getDescription())))
                .andExpect(jsonPath("$[0].requestor.id", is(itemRequestDtoOut.getRequestor().getId()), Long.class))
                .andExpect(jsonPath("$[0].requestor.name", is(itemRequestDtoOut.getRequestor().getName())))
                .andExpect(jsonPath("$[0].requestor.email", is(itemRequestDtoOut.getRequestor().getEmail())))
                .andExpect(jsonPath("$[0].created", is(itemRequestDtoOut.getCreated())))
                .andExpect(jsonPath("$[0].items[0].id", is(itemRequestDtoOut.getItems().get(0).getId()), Long.class))
                .andExpect(jsonPath("$[0].items[0].name", is(itemRequestDtoOut.getItems().get(0).getName())))
                .andExpect(jsonPath("$[0].items[0].userId", is(itemRequestDtoOut.getItems().get(0).getUserId()), Long.class))
                .andExpect(jsonPath("$[0].items[0].description", is(itemRequestDtoOut.getItems().get(0).getDescription())))
                .andExpect(jsonPath("$[0].items[0].available", is(itemRequestDtoOut.getItems().get(0).getAvailable())))
                .andExpect(jsonPath("$[0].items[0].requestId", is(itemRequestDtoOut.getItems().get(0).getRequestId()), Long.class));
    }

    @Test
    void getByIdTest() throws Exception {
        when(service.getById(anyLong(), anyLong()))
                .thenReturn(itemRequestDtoOut);

        mvc.perform(get("/requests/1")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDtoOut.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDtoOut.getDescription())))
                .andExpect(jsonPath("$.requestor.id", is(itemRequestDtoOut.getRequestor().getId()), Long.class))
                .andExpect(jsonPath("$.requestor.name", is(itemRequestDtoOut.getRequestor().getName())))
                .andExpect(jsonPath("$.requestor.email", is(itemRequestDtoOut.getRequestor().getEmail())))
                .andExpect(jsonPath("$.created", is(itemRequestDtoOut.getCreated())))
                .andExpect(jsonPath("$.items[0].id", is(itemRequestDtoOut.getItems().get(0).getId()), Long.class))
                .andExpect(jsonPath("$.items[0].name", is(itemRequestDtoOut.getItems().get(0).getName())))
                .andExpect(jsonPath("$.items[0].userId", is(itemRequestDtoOut.getItems().get(0).getUserId()), Long.class))
                .andExpect(jsonPath("$.items[0].description", is(itemRequestDtoOut.getItems().get(0).getDescription())))
                .andExpect(jsonPath("$.items[0].available", is(itemRequestDtoOut.getItems().get(0).getAvailable())))
                .andExpect(jsonPath("$.items[0].requestId", is(itemRequestDtoOut.getItems().get(0).getRequestId()), Long.class));
    }
}
