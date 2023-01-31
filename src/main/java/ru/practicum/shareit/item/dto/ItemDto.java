package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.model.Item;

/**
 * TODO Sprint add-controllers.
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ItemDto {
    private String name;
    private String description;
    private Boolean available;
    private int request;
}
