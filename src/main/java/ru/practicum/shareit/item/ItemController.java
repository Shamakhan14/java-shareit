package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.ValidationException;
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ItemDto create(@RequestHeader("X-Sharer-User-Id") int userId, @RequestBody ItemDto itemDto) {
        if (!isValidItemDto(itemDto)) throw new ValidationException("Неверные данные вещи.");
        ItemDto response = itemService.create(userId, itemDto);
        log.info("Вещь {} успешно добавлена.", itemDto.getName());
        return response;
    }

    @GetMapping
    public List<ItemDto> getAll(@RequestHeader("X-Sharer-User-Id") int userId) {
        List<ItemDto> response = itemService.getAll(userId);
        log.info("Запрошен список вещей.");
        return response;
    }

    @GetMapping("/{itemId}")
    public ItemDto getById(@RequestHeader("X-Sharer-User-Id") int userId, @PathVariable int itemId) {
        ItemDto response = itemService.getById(userId, itemId);
        log.info("Выведена информация по вещи {}.", response.getName());
        return response;
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader("X-Sharer-User-Id") int userId, @PathVariable int itemId,
                            @RequestBody ItemDto itemDto) {
        ItemDto response = itemService.update(userId, itemId, itemDto);
        log.info("Информация о вещи {} обновлена.", response.getName());
        return response;
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestHeader("X-Sharer-User-Id") int userId, @RequestParam String text) {
        if (text.isEmpty()) return List.of();
        List<ItemDto> items = itemService.search(userId, text);
        log.info("Выполнен поиск по описанию: {}", text);
        return items;
    }

    private boolean isValidItemDto(ItemDto itemDto) {
        if (itemDto.getName() == null || itemDto.getDescription() == null ||
            itemDto.getName().isEmpty() || itemDto.getDescription().isEmpty() ||
            itemDto.getAvailable() == null) return false;
        return true;
    }
}
