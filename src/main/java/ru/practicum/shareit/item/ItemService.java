package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.exceptions.ItemNotFoundException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import javax.persistence.EntityNotFoundException;
import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.*;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static org.springframework.data.domain.Sort.Direction.DESC;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Transactional
    public ItemDto create(Long userId, ItemDto itemDto) {
        if (!isValidOwner(userId)) throw new UserNotFoundException("Неверный ID пользователя.");
        Item item = ItemMapper.mapToNewItem(itemDto);
        item.setOwner(userId);
        return ItemMapper.mapToItemDto(itemRepository.save(item));
    }

    public List<ItemDtoResponse> getAll(Long userId) {
        if (!isValidOwner(userId)) throw new UserNotFoundException("Неверный ID пользователя.");
        List<Item> items = itemRepository.findByOwnerOrderById(userId);
        return mapItemsToItemDtoResponses(items);
    }

    public ItemDtoResponse getById(Long userId, Long itemId) {
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Неверный ID пользователя."));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Неверный ID вещи."));
        if (item.getOwner().equals(userId)) {
            return mapItemsToItemDtoResponses(List.of(item)).get(0);
        } else {
            ItemDtoResponse response = ItemMapper.mapToResponseWithoutBookings(item);
            List<Comment> comments = commentRepository.findByItem(itemId);
            response.setComments(CommentMapper.mapToCommentDtos(comments));
            return response;
        }
    }

    @Transactional
    public ItemDto update(Long userId, Long itemId, ItemDto itemDto) {
        if (!isValidOwner(userId)) throw new UserNotFoundException("Неверный ID пользователя.");
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException("Неверный ID вещи."));
        if (!item.getOwner().equals(userId))
            throw new UserNotFoundException("Вещь не принадлежит данному пользователю.");
        if (itemDto.getName() != null && !itemDto.getName().isBlank()) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null && !itemDto.getDescription().isBlank()) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        return ItemMapper.mapToItemDto(item);
    }

    public List<ItemDto> search(Long userId, String text) {
        if (!isValidOwner(userId)) throw new UserNotFoundException("Неверный ID пользователя.");
        Boolean available = true;
        return ItemMapper.mapToItemDto(itemRepository
                .findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAndAvailable(text, text, available));
    }

    @Transactional
    public CommentDto post(Long userId, Long itemId, CommentDtoInc commentDtoInc) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Неверный ID пользователя."));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException("Неверный ID вещи."));
        List<Booking> bookings = bookingRepository.findByItemAndValidBooker(itemId, userId, BookingStatus.APPROVED,
                LocalDateTime.now());
        if (bookings.isEmpty()) throw new ValidationException("Данный пользователь не может оставить комментарий.");
        Comment comment = CommentMapper.mapToComment(commentDtoInc, itemId, user);
        return CommentMapper.mapToCommentDto(commentRepository.save(comment));
    }

    private boolean isValidOwner(Long userId) {
        if (userRepository.findById(userId).isPresent()) {
            return true;
        } else {
            return false;
        }
    }

    private List<ItemDtoResponse> mapItemsToItemDtoResponses(List<Item> items) {
        List<ItemDtoResponse> responses = new ArrayList<>();
        List<Long> itemIds = items.stream()
                .map(Item::getId)
                .collect(toList());
        //creating map of item/bookings
        Map<Item, List<Booking>> approvedBookings = bookingRepository.findByItemInAndStatus(itemIds,
                        BookingStatus.APPROVED, Sort.by(DESC, "start")).stream()
                        .collect(groupingBy(Booking::getItem, toList()));
        //creating map of item id/comments
        Map<Long, List<Comment>> sortedComments =
                commentRepository.findByItemIn(itemIds, Sort.by(DESC, "created"))
                        .stream()
                        .collect(groupingBy(Comment::getItem, toList()));
        //transforming items into dto and filling the result list
        for (Item item: items) {
            //filling spaces
            ItemDtoResponse response = ItemMapper.mapToResponseWithoutBookings(item);
            response.setComments(CommentMapper.mapToCommentDtos(sortedComments.getOrDefault(item.getId(),
                    Collections.emptyList())));
            //filling bookings
            List<Booking> bookings = approvedBookings.getOrDefault(item, Collections.emptyList());
            LocalDateTime now = LocalDateTime.now();
            if (bookings.size() == 0) {
                response.setNextBooking(null);
                response.setLastBooking(null);
            } else {
                for (int i = 0; i < bookings.size(); i++) {
                    if (!bookings.get(i).getStart().isAfter(now)) {
                        response.setLastBooking(BookingMapper.mapToBookingDtoForItems(bookings.get(i)));
                        if (i != 0) {
                            response.setNextBooking(BookingMapper.mapToBookingDtoForItems(bookings.get(i - 1)));
                        }
                        break;
                    }
                    if (response.getLastBooking() == null) {
                        response.setNextBooking(BookingMapper
                                .mapToBookingDtoForItems(bookings.get(bookings.size() - 1)));
                    }
                }
            }
            responses.add(response);
        }
        return responses;
    }
}
