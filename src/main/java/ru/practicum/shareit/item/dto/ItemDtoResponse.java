package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ItemDtoResponse {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private BookingDtoFotItems lastBooking;
    private BookingDtoFotItems nextBooking;
    private List<CommentDto> comments;
}
