package ru.practicum.shareit.unit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.exceptions.ItemNotFoundException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.item.CommentRepository;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentDtoInc;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import javax.persistence.EntityNotFoundException;
import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class ItemServiceUnitTest {

    private ItemRepository mockItemRepository;
    private UserRepository mockUserRepository;
    private BookingRepository mockBookingRepository;
    private CommentRepository mockCommentRepository;
    private ItemService service;
    User user;
    ItemDto itemDto;
    Item item;
    Booking lastBooking;
    Booking nextBooking;
    Comment comment;
    CommentDtoInc commentDtoInc;

    @BeforeEach
    void setUp() {
        mockItemRepository = Mockito.mock(ItemRepository.class);
        mockUserRepository = Mockito.mock(UserRepository.class);
        mockBookingRepository = Mockito.mock(BookingRepository.class);
        mockCommentRepository = Mockito.mock(CommentRepository.class);
        service = new ItemService(mockItemRepository, mockUserRepository, mockBookingRepository, mockCommentRepository);

        user = new User();
        user.setId(1L);
        user.setName("name");
        user.setEmail("email@email.com");

        itemDto = new ItemDto();
        itemDto.setName("name");
        itemDto.setDescription("description");
        itemDto.setAvailable(true);
        itemDto.setRequestId(1L);

        item = new Item();
        item.setId(1L);
        item.setName("name");
        item.setDescription("description");
        item.setAvailable(true);
        item.setOwner(1L);
        item.setRequest(1L);

        lastBooking = new Booking();
        lastBooking.setId(1L);
        lastBooking.setStart(LocalDateTime.of(2020, 1, 1, 1, 1));
        lastBooking.setEnd(LocalDateTime.of(2021, 1, 1, 1, 1));
        lastBooking.setItem(item);
        lastBooking.setBooker(user);
        lastBooking.setStatus(BookingStatus.APPROVED);

        nextBooking = new Booking();
        nextBooking.setId(2L);
        nextBooking.setStart(LocalDateTime.of(2024, 1, 1, 1, 1));
        nextBooking.setEnd(LocalDateTime.of(2025, 1, 1, 1, 1));
        nextBooking.setItem(item);
        nextBooking.setBooker(user);
        nextBooking.setStatus(BookingStatus.APPROVED);

        comment = new Comment();
        comment.setId(1L);
        comment.setText("text");
        comment.setItem(1L);
        comment.setAuthor(user);
        comment.setCreated(LocalDateTime.now());

        commentDtoInc = new CommentDtoInc("text");
    }

    @Test
    void createTest() {
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));
        Mockito
                .when(mockItemRepository.save(Mockito.any()))
                .thenReturn(item);

        ItemDto result = service.create(user.getId(), itemDto);

        assertThat(result.getId(), equalTo(item.getId()));
        assertThat(result.getDescription(), equalTo(itemDto.getDescription()));
        assertThat(result.getAvailable(), equalTo(itemDto.getAvailable()));
        assertThat(result.getRequestId(), equalTo(itemDto.getRequestId()));
    }

    @Test
    void createUserNotFoundTest() {
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        assertThatExceptionOfType(UserNotFoundException.class)
                .isThrownBy(() -> {
                    ItemDto result = service.create(user.getId(), itemDto);
                }).withMessage("Неверный ID пользователя.");
    }

    @Test
    void getAllPageableTest() {
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));
        Mockito
                .when(mockItemRepository.findByOwnerOrderByIdPageable(Mockito.anyLong(), Mockito.any()))
                .thenReturn(List.of(item));
        Mockito
                .when(mockBookingRepository.findByItemInAndStatus(Mockito.anyList(), Mockito.any(), Mockito.any()))
                .thenReturn(List.of(nextBooking, lastBooking));
        Mockito
                .when(mockCommentRepository.findByItemIn(Mockito.anyList(), Mockito.any()))
                .thenReturn(List.of(comment));

        List<ItemDtoResponse> responses = service.getAll(user.getId(), 0, 10);

        assertThat(responses, hasSize(1));
        assertThat(responses.get(0).getId(), equalTo(item.getId()));
        assertThat(responses.get(0).getName(), equalTo(item.getName()));
        assertThat(responses.get(0).getDescription(), equalTo(item.getDescription()));
        assertThat(responses.get(0).getAvailable(), equalTo(item.getAvailable()));
        assertThat(responses.get(0).getLastBooking().getId(), equalTo(lastBooking.getId()));
        assertThat(responses.get(0).getLastBooking().getStart(), equalTo(lastBooking.getStart()));
        assertThat(responses.get(0).getLastBooking().getEnd(), equalTo(lastBooking.getEnd()));
        assertThat(responses.get(0).getLastBooking().getStatus(), equalTo(lastBooking.getStatus()));
        assertThat(responses.get(0).getLastBooking().getBookerId(), equalTo(lastBooking.getBooker().getId()));
        assertThat(responses.get(0).getNextBooking().getId(), equalTo(nextBooking.getId()));
        assertThat(responses.get(0).getNextBooking().getStart(), equalTo(nextBooking.getStart()));
        assertThat(responses.get(0).getNextBooking().getEnd(), equalTo(nextBooking.getEnd()));
        assertThat(responses.get(0).getNextBooking().getStatus(), equalTo(nextBooking.getStatus()));
        assertThat(responses.get(0).getNextBooking().getBookerId(), equalTo(nextBooking.getBooker().getId()));
        assertThat(responses.get(0).getComments(), hasSize(1));
        assertThat(responses.get(0).getComments().get(0).getId(), equalTo(comment.getId()));
        assertThat(responses.get(0).getComments().get(0).getText(), equalTo(comment.getText()));
        assertThat(responses.get(0).getComments().get(0).getAuthorName(), equalTo(comment.getAuthor().getName()));
        assertThat(responses.get(0).getComments().get(0).getCreated(), equalTo(comment.getCreated()));
    }

    @Test
    void getAllUserNotFoundTest() {
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        assertThatExceptionOfType(UserNotFoundException.class)
                .isThrownBy(() -> {
                    List<ItemDtoResponse> responses = service.getAll(user.getId(), 0, 10);
                }).withMessage("Неверный ID пользователя.");
    }

    @Test
    void getByIdForOwnerTest() {
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));
        Mockito
                .when(mockItemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(item));
        Mockito
                .when(mockBookingRepository.findByItemInAndStatus(Mockito.anyList(), Mockito.any(), Mockito.any()))
                .thenReturn(List.of(nextBooking, lastBooking));
        Mockito
                .when(mockCommentRepository.findByItemIn(Mockito.anyList(), Mockito.any()))
                .thenReturn(List.of(comment));

        ItemDtoResponse response = service.getById(user.getId(), item.getId());

        assertThat(response.getId(), equalTo(item.getId()));
        assertThat(response.getName(), equalTo(item.getName()));
        assertThat(response.getDescription(), equalTo(item.getDescription()));
        assertThat(response.getAvailable(), equalTo(item.getAvailable()));
        assertThat(response.getLastBooking().getId(), equalTo(lastBooking.getId()));
        assertThat(response.getLastBooking().getStart(), equalTo(lastBooking.getStart()));
        assertThat(response.getLastBooking().getEnd(), equalTo(lastBooking.getEnd()));
        assertThat(response.getLastBooking().getStatus(), equalTo(lastBooking.getStatus()));
        assertThat(response.getLastBooking().getBookerId(), equalTo(lastBooking.getBooker().getId()));
        assertThat(response.getNextBooking().getId(), equalTo(nextBooking.getId()));
        assertThat(response.getNextBooking().getStart(), equalTo(nextBooking.getStart()));
        assertThat(response.getNextBooking().getEnd(), equalTo(nextBooking.getEnd()));
        assertThat(response.getNextBooking().getStatus(), equalTo(nextBooking.getStatus()));
        assertThat(response.getNextBooking().getBookerId(), equalTo(nextBooking.getBooker().getId()));
        assertThat(response.getComments(), hasSize(1));
        assertThat(response.getComments().get(0).getId(), equalTo(comment.getId()));
        assertThat(response.getComments().get(0).getText(), equalTo(comment.getText()));
        assertThat(response.getComments().get(0).getAuthorName(), equalTo(comment.getAuthor().getName()));
        assertThat(response.getComments().get(0).getCreated(), equalTo(comment.getCreated()));
    }

    @Test
    void getByIdForOtherUserTest() {
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));
        Mockito
                .when(mockItemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(item));
        Mockito
                .when(mockBookingRepository.findByItemInAndStatus(Mockito.anyList(), Mockito.any(), Mockito.any()))
                .thenReturn(List.of(nextBooking, lastBooking));
        Mockito
                .when(mockCommentRepository.findByItem(Mockito.anyLong()))
                .thenReturn(List.of(comment));

        ItemDtoResponse response = service.getById(2L, item.getId());

        assertThat(response.getId(), equalTo(item.getId()));
        assertThat(response.getName(), equalTo(item.getName()));
        assertThat(response.getDescription(), equalTo(item.getDescription()));
        assertThat(response.getAvailable(), equalTo(item.getAvailable()));
        assertThat(response.getLastBooking(), equalTo(null));
        assertThat(response.getNextBooking(), equalTo(null));
        assertThat(response.getComments(), hasSize(1));
        assertThat(response.getComments().get(0).getId(), equalTo(comment.getId()));
        assertThat(response.getComments().get(0).getText(), equalTo(comment.getText()));
        assertThat(response.getComments().get(0).getAuthorName(), equalTo(comment.getAuthor().getName()));
        assertThat(response.getComments().get(0).getCreated(), equalTo(comment.getCreated()));
    }

    @Test
    void getByIdUserNotFoundTest() {
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        assertThatExceptionOfType(UserNotFoundException.class)
                .isThrownBy(() -> {
                    ItemDtoResponse response = service.getById(user.getId(), item.getId());
                }).withMessage("Неверный ID пользователя.");
    }

    @Test
    void getByIdItemNotFound() {
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));
        Mockito
                .when(mockItemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        assertThatExceptionOfType(EntityNotFoundException.class)
                .isThrownBy(() -> {
                    ItemDtoResponse response = service.getById(user.getId(), item.getId());
                }).withMessage("Неверный ID вещи.");
    }

    @Test
    void updateTest() {
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));
        Mockito
                .when(mockItemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(item));

        ItemDto result = service.update(user.getId(), item.getId(), itemDto);

        assertThat(result.getId(), equalTo(item.getId()));
        assertThat(result.getDescription(), equalTo(itemDto.getDescription()));
        assertThat(result.getAvailable(), equalTo(itemDto.getAvailable()));
        assertThat(result.getRequestId(), equalTo(itemDto.getRequestId()));
    }

    @Test
    void updateUserNotFoundTest() {
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        assertThatExceptionOfType(UserNotFoundException.class)
                .isThrownBy(() -> {
                    ItemDto response = service.update(user.getId(), item.getId(), itemDto);
                }).withMessage("Неверный ID пользователя.");
    }

    @Test
    void updateItemNotFoundTest() {
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));
        Mockito
                .when(mockItemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        assertThatExceptionOfType(ItemNotFoundException.class)
                .isThrownBy(() -> {
                    ItemDto response = service.update(user.getId(), item.getId(), itemDto);
                }).withMessage("Неверный ID вещи.");
    }

    @Test
    void updateByNotOwnerTest() {
        user.setId(2L);
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));
        Mockito
                .when(mockItemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(item));

        assertThatExceptionOfType(UserNotFoundException.class)
                .isThrownBy(() -> {
                    ItemDto response = service.update(user.getId(), item.getId(), itemDto);
                }).withMessage("Вещь не принадлежит данному пользователю.");
    }

    @Test
    void searchPageableTest() {
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));
        Mockito
                .when(mockItemRepository
                        .findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailable(
                                Mockito.anyString(),
                                Mockito.anyString(),
                                Mockito.any(),
                                Mockito.any()))
                .thenReturn(List.of(item));

        List<ItemDto> result = service.search(user.getId(), "text", 0, 10);

        assertThat(result, hasSize(1));
        assertThat(result.get(0).getId(), equalTo(item.getId()));
        assertThat(result.get(0).getDescription(), equalTo(item.getDescription()));
        assertThat(result.get(0).getAvailable(), equalTo(item.getAvailable()));
        assertThat(result.get(0).getRequestId(), equalTo(item.getRequest()));
    }

    @Test
    void searchUserNotFoundTest() {
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        assertThatExceptionOfType(UserNotFoundException.class)
                .isThrownBy(() -> {
                    List<ItemDto> response = service.search(user.getId(), "text", 0, 10);
                }).withMessage("Неверный ID пользователя.");
    }

    @Test
    void postTest() {
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));
        Mockito
                .when(mockItemRepository.findById(item.getId()))
                .thenReturn(Optional.of(item));
        Mockito
                .when(mockBookingRepository.findByItemAndValidBooker(Mockito.anyLong(), Mockito.anyLong(),
                        Mockito.any(), Mockito.any()))
                .thenReturn(List.of(lastBooking));
        Mockito
                .when(mockCommentRepository.save(Mockito.any()))
                .thenReturn(comment);

        CommentDto result = service.post(user.getId(), item.getId(), commentDtoInc);

        assertThat(result.getId(), equalTo(comment.getId()));
        assertThat(result.getText(), equalTo(commentDtoInc.getText()));
        assertThat(result.getAuthorName(), equalTo(comment.getAuthor().getName()));
        assertThat(result.getCreated(), equalTo(comment.getCreated()));
    }

    @Test
    void postUserNotFoundTest() {
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        assertThatExceptionOfType(UserNotFoundException.class)
                .isThrownBy(() -> {
                    CommentDto result = service.post(user.getId(), item.getId(), commentDtoInc);
                }).withMessage("Неверный ID пользователя.");
    }

    @Test
    void postItemNotFoundTest() {
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));
        Mockito
                .when(mockItemRepository.findById(item.getId()))
                .thenReturn(Optional.empty());

        assertThatExceptionOfType(ItemNotFoundException.class)
                .isThrownBy(() -> {
                    CommentDto result = service.post(user.getId(), item.getId(), commentDtoInc);
                }).withMessage("Неверный ID вещи.");
    }

    @Test
    void postByNotValidUserTest() {
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));
        Mockito
                .when(mockItemRepository.findById(item.getId()))
                .thenReturn(Optional.of(item));
        Mockito
                .when(mockBookingRepository.findByItemAndValidBooker(Mockito.anyLong(), Mockito.anyLong(),
                        Mockito.any(), Mockito.any()))
                .thenReturn(List.of());

        assertThatExceptionOfType(ValidationException.class)
                .isThrownBy(() -> {
                    CommentDto result = service.post(user.getId(), item.getId(), commentDtoInc);
                }).withMessage("Данный пользователь не может оставить комментарий.");
    }
}
