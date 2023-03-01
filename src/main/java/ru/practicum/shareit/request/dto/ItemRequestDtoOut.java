package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.request.RequestResponse;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ItemRequestDtoOut {

    private Long id;
    private String description;
    private User requestor;
    private LocalDateTime created;
    private List<RequestResponse> items;
}
