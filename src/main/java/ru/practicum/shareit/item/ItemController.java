package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentDtoInc;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.user.Create;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ItemDto create(@RequestHeader("X-Sharer-User-Id") Long userId,
                          @RequestBody @Validated(Create.class) ItemDto itemDto) {
        ItemDto response = itemService.create(userId, itemDto);
        log.info("Вещь {} успешно добавлена.", itemDto.getName());
        return response;
    }

    @GetMapping
    public List<ItemDtoResponse> getAll(@RequestHeader("X-Sharer-User-Id") Long userId) {
        List<ItemDtoResponse> response = itemService.getAll(userId);
        log.info("Запрошен список вещей.");
        return response;
    }

    @GetMapping("/{itemId}")
    public ItemDtoResponse getById(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long itemId) {
        ItemDtoResponse response = itemService.getById(userId, itemId);
        log.info("Выведена информация по вещи {}.", response.getName());
        return response;
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long itemId,
                            @RequestBody ItemDto itemDto) {
        ItemDto response = itemService.update(userId, itemId, itemDto);
        log.info("Информация о вещи {} обновлена.", response.getName());
        return response;
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestHeader("X-Sharer-User-Id") Long userId, @RequestParam String text) {
        if (text.isBlank()) return List.of();
        List<ItemDto> items = itemService.search(userId, text);
        log.info("Выполнен поиск по описанию: {}", text);
        return items;
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto post(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long itemId,
                           @RequestBody @Valid CommentDtoInc commentDtoInc) {
        CommentDto commentDto = itemService.post(userId, itemId, commentDtoInc);
        log.info("Комментарий успешно загружен.");
        return commentDto;
    }
}
