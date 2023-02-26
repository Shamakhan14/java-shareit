package ru.practicum.shareit.request;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class Response {
    private Long id;
    private String name;
    private Long userId;
    private String description;
    private Boolean available;
    private Long requestId;
}
