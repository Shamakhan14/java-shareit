package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.exceptions.ItemNotFoundException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;

import javax.persistence.EntityNotFoundException;
import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    public ItemDto create(Long userId, ItemDto itemDto) {
        if (!isValidOwner(userId)) throw new UserNotFoundException("Неверный ID пользователя.");
        Item item = ItemMapper.mapToNewItem(itemDto);
        item.setOwner(userId);
        return ItemMapper.mapToItemDto(itemRepository.save(item));
    }

    public List<ItemDtoResponse> getAll(Long userId) {
        if (!isValidOwner(userId)) throw new UserNotFoundException("Неверный ID пользователя.");
        List<Item> items = itemRepository.findByOwner(userId);
        return mapItemsToItemDtoResponses(items);
    }

    public ItemDtoResponse getById(Long userId, Long itemId) {
        if (!isValidOwner(userId)) throw new UserNotFoundException("Неверный ID пользователя.");
        if (!isValidItemId(itemId)) throw new EntityNotFoundException("Неверный ID вещи.");
        Item item = itemRepository.findById(itemId).get();
        if (item.getOwner().equals(userId)) {
            return mapItemsToItemDtoResponses(List.of(itemRepository.getById(itemId))).get(0);
        } else {
            ItemDtoResponse response = new ItemDtoResponse();
            response.setId(item.getId());
            response.setName(item.getName());
            response.setDescription(item.getDescription());
            response.setAvailable(item.getAvailable());
            List<Comment> comments = commentRepository.findByItem(itemId);
            response.setComments(mapToCommentDtos(comments));
            return response;
        }
    }

    public ItemDto update(Long userId, Long itemId, ItemDto itemDto) {
        if (!isValidOwner(userId)) throw new UserNotFoundException("Неверный ID пользователя.");
        if (!isValidItemId(itemId)) throw new ItemNotFoundException("Неверный ID вещи.");
        if (!itemRepository.getById(itemId).getOwner().equals(userId))
            throw new UserNotFoundException("Вещь не принадлежит данному пользователю.");
        Item item = itemRepository.getById(itemId);
        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        return ItemMapper.mapToItemDto(itemRepository.save(item));
    }

    public List<ItemDto> search(Long userId, String text) {
        if (!isValidOwner(userId)) throw new UserNotFoundException("Неверный ID пользователя.");
        Boolean available = true;
        return ItemMapper.mapToItemDto(itemRepository
                .findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAndAvailable(text, text, available));
    }

    public CommentDto post(Long userId, Long itemId, CommentDtoInc commentDtoInc) {
        if (!isValidOwner(userId)) throw new UserNotFoundException("Неверный ID пользователя.");
        if (!isValidItemId(itemId)) throw new ItemNotFoundException("Неверный ID вещи.");
        if (commentDtoInc.getText().isBlank() || commentDtoInc.getText() == null) {
            throw new ValidationException("Комментарий не может быть пустым.");
        }
        List<Booking> bookings = bookingRepository.findByItem(itemId);
        boolean isValidCommentator = false;
        for (Booking booking: bookings) {
            if (booking.getBooker().equals(userId) && booking.getStatus().equals(BookingStatus.APPROVED) &&
                booking.getEnd().isBefore(LocalDateTime.now())) {
                isValidCommentator = true;
            }
        }
        if (!isValidCommentator) throw new ValidationException("Данный пользователь не может оставить комментарий.");
        Comment comment = new Comment();
        comment.setText(commentDtoInc.getText());
        comment.setItem(itemId);
        comment.setAuthor(userId);
        comment.setCreated(LocalDateTime.now());
        return CommentMapper.mapToCommentDto(commentRepository.save(comment),
                userRepository.findById(userId).get().getName());
    }

    private boolean isValidOwner(Long userId) {
        if (userRepository.findById(userId).isPresent()) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isValidItemId(Long itemId) {
        if (itemRepository.findById(itemId).isPresent()) {
            return true;
        } else {
            return false;
        }
    }

    private List<ItemDtoResponse> mapItemsToItemDtoResponses(List<Item> items) {
        List<ItemDtoResponse> responses = new ArrayList<>();
        for (Item item: items) {
            LocalDateTime lastEnd = LocalDateTime.MIN;
            LocalDateTime nextStart = LocalDateTime.MAX;
            BookingDtoFotItems lastBooking = new BookingDtoFotItems();
            BookingDtoFotItems nextBooking = new BookingDtoFotItems();
            LocalDateTime now = LocalDateTime.now();
            List<Booking> bookings = bookingRepository.findByItem(item.getId());
            for (Booking booking: bookings) {
                if (booking.getEnd().isAfter(lastEnd) && booking.getEnd().isBefore(now)) {
                    lastEnd = booking.getEnd();
                    lastBooking = BookingMapper.mapToBookingDtoForItems(booking);
                }
                if (booking.getStart().isBefore(nextStart) && booking.getStart().isAfter(now)) {
                    nextStart = booking.getStart();
                    nextBooking = BookingMapper.mapToBookingDtoForItems(booking);
                }
            }
            ItemDtoResponse response = new ItemDtoResponse();
            response.setId(item.getId());
            response.setName(item.getName());
            response.setDescription(item.getDescription());
            response.setAvailable(item.getAvailable());
            if (!lastEnd.equals(LocalDateTime.MIN)) {
                response.setLastBooking(lastBooking);
            }
            if (!nextStart.equals(LocalDateTime.MAX)) {
                response.setNextBooking(nextBooking);
            }
            List<Comment> comments = commentRepository.findByItem(item.getId());
            response.setComments(mapToCommentDtos(comments));
            responses.add(response);
        }
        Collections.sort(responses, Comparator.comparing(ItemDtoResponse::getId));
        return responses;
    }

    private List<CommentDto> mapToCommentDtos(List<Comment> comments) {
        List<CommentDto> commentDtos = new ArrayList<>();
        for (Comment comment: comments) {
            commentDtos.add(CommentMapper.mapToCommentDto(comment,
                    userRepository.findById(comment.getAuthor()).get().getName()));
        }
        return commentDtos;
    }
}
