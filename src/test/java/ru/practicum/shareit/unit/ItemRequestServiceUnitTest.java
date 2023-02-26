package ru.practicum.shareit.unit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.request.dto.ItemRequestDtoInc;
import ru.practicum.shareit.request.dto.ItemRequestDtoOut;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import javax.persistence.EntityNotFoundException;
import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.assertj.core.api.Assertions.*;

public class ItemRequestServiceUnitTest {

    private ItemRequestRepository mockRequestRepository;
    private ItemRepository mockItemRepository;
    private UserRepository mockUserRepository;
    private ItemRequestService service;
    User user;
    ItemRequestDtoInc itemRequestDtoInc;
    ItemRequest itemRequest;
    Item item;

    @BeforeEach
    void setUp() {
        mockRequestRepository = Mockito.mock(ItemRequestRepository.class);
        mockItemRepository = Mockito.mock(ItemRepository.class);
        mockUserRepository = Mockito.mock(UserRepository.class);
        service = new ItemRequestService(mockUserRepository, mockRequestRepository, mockItemRepository);

        user = new User();
        user.setId(1L);
        user.setName("name");
        user.setEmail("email@email.com");

        itemRequestDtoInc = new ItemRequestDtoInc("text");

        itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        itemRequest.setDescription("text");
        itemRequest.setRequestor(user);
        itemRequest.setCreated(LocalDateTime.now());

        item = new Item();
        item.setId(1L);
        item.setName("name");
        item.setDescription("description");
        item.setAvailable(true);
        item.setOwner(1L);
        item.setRequest(1L);
    }

    @Test
    void createTest() {
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));
        Mockito
                .when(mockRequestRepository.save(Mockito.any()))
                .thenReturn(itemRequest);

        ItemRequestDtoOut result = service.create(user.getId(), itemRequestDtoInc);

        assertThat(result.getId(), equalTo(itemRequest.getId()));
        assertThat(result.getDescription(), equalTo(itemRequestDtoInc.getDescription()));
        assertThat(result.getRequestor(), equalTo(user));
        assertThat(result.getCreated(), notNullValue());
        assertThat(result.getItems(), hasSize(0));
    }

    @Test
    void createUserNotFoundTest() {
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        assertThatExceptionOfType(UserNotFoundException.class)
                .isThrownBy(() -> {
                    ItemRequestDtoOut result = service.create(user.getId(), itemRequestDtoInc);
                }).withMessage("Неверный ID пользователя.");
    }

    @Test
    void getOwnTest() {
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));
        Mockito
                .when(mockRequestRepository.findByRequestor_IdOrderByCreatedDesc(Mockito.anyLong()))
                .thenReturn(List.of(itemRequest));
        Mockito
                .when(mockItemRepository.findByRequestInOrderByIdDesc(Mockito.anyList()))
                .thenReturn(List.of(item));

        List<ItemRequestDtoOut> result = service.getOwn(user.getId());

        assertThat(result, hasSize(1));
        assertThat(result.get(0).getId(), equalTo(itemRequest.getId()));
        assertThat(result.get(0).getDescription(), equalTo(itemRequestDtoInc.getDescription()));
        assertThat(result.get(0).getRequestor(), equalTo(user));
        assertThat(result.get(0).getCreated(), notNullValue());
        assertThat(result.get(0).getItems(), hasSize(1));
        assertThat(result.get(0).getItems().get(0).getId(), equalTo(item.getId()));
        assertThat(result.get(0).getItems().get(0).getName(), equalTo(item.getName()));
        assertThat(result.get(0).getItems().get(0).getDescription(), equalTo(item.getDescription()));
        assertThat(result.get(0).getItems().get(0).getAvailable(), equalTo(item.getAvailable()));
        assertThat(result.get(0).getItems().get(0).getRequestId(), equalTo(item.getRequest()));
    }

    @Test
    void getOwnUserNotFoundTest() {
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        assertThatExceptionOfType(UserNotFoundException.class)
                .isThrownBy(() -> {
                    List<ItemRequestDtoOut> result = service.getOwn(user.getId());
                }).withMessage("Неверный ID пользователя.");
    }

    @Test
    void getAllTest() {
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));
        Mockito
                .when(mockRequestRepository.findAllWithoutUser(Mockito.anyLong(), Mockito.any()))
                .thenReturn(List.of(itemRequest));
        Mockito
                .when(mockItemRepository.findByRequestInOrderByIdDesc(Mockito.anyList()))
                .thenReturn(List.of(item));

        List<ItemRequestDtoOut> result = service.getAll(user.getId(), Optional.empty(), Optional.empty());

        assertThat(result, hasSize(1));
        assertThat(result.get(0).getId(), equalTo(itemRequest.getId()));
        assertThat(result.get(0).getDescription(), equalTo(itemRequestDtoInc.getDescription()));
        assertThat(result.get(0).getRequestor(), equalTo(user));
        assertThat(result.get(0).getCreated(), notNullValue());
        assertThat(result.get(0).getItems(), hasSize(1));
        assertThat(result.get(0).getItems().get(0).getId(), equalTo(item.getId()));
        assertThat(result.get(0).getItems().get(0).getName(), equalTo(item.getName()));
        assertThat(result.get(0).getItems().get(0).getDescription(), equalTo(item.getDescription()));
        assertThat(result.get(0).getItems().get(0).getAvailable(), equalTo(item.getAvailable()));
        assertThat(result.get(0).getItems().get(0).getRequestId(), equalTo(item.getRequest()));
    }

    @Test
    void getAllPageableTest() {
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));
        Mockito
                .when(mockRequestRepository.findAllPageable(Mockito.anyLong(), Mockito.any()))
                .thenReturn(List.of(itemRequest));
        Mockito
                .when(mockItemRepository.findByRequestInOrderByIdDesc(Mockito.anyList()))
                .thenReturn(List.of(item));

        List<ItemRequestDtoOut> result = service.getAll(user.getId(), Optional.of(5), Optional.of(5));

        assertThat(result, hasSize(1));
        assertThat(result.get(0).getId(), equalTo(itemRequest.getId()));
        assertThat(result.get(0).getDescription(), equalTo(itemRequestDtoInc.getDescription()));
        assertThat(result.get(0).getRequestor(), equalTo(user));
        assertThat(result.get(0).getCreated(), notNullValue());
        assertThat(result.get(0).getItems(), hasSize(1));
        assertThat(result.get(0).getItems().get(0).getId(), equalTo(item.getId()));
        assertThat(result.get(0).getItems().get(0).getName(), equalTo(item.getName()));
        assertThat(result.get(0).getItems().get(0).getDescription(), equalTo(item.getDescription()));
        assertThat(result.get(0).getItems().get(0).getAvailable(), equalTo(item.getAvailable()));
        assertThat(result.get(0).getItems().get(0).getRequestId(), equalTo(item.getRequest()));
    }

    @Test
    void getAllUserNotFoundTest() {
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        assertThatExceptionOfType(UserNotFoundException.class)
                .isThrownBy(() -> {
                    List<ItemRequestDtoOut> result = service.getAll(user.getId(),
                            Optional.empty(), Optional.empty());
                }).withMessage("Неверный ID пользователя.");
    }

    @Test
    void getAllFromEmptySizePresentTest() {
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));

        assertThatExceptionOfType(ValidationException.class)
                .isThrownBy(() -> {
                    List<ItemRequestDtoOut> result = service.getAll(user.getId(),
                            Optional.empty(), Optional.of(5));
                }).withMessage("Должны присутствовать оба параметра.");
    }

    @Test
    void getAllFromPresentSizeEmptyTest() {
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));

        assertThatExceptionOfType(ValidationException.class)
                .isThrownBy(() -> {
                    List<ItemRequestDtoOut> result = service.getAll(user.getId(),
                            Optional.of(5), Optional.empty());
                }).withMessage("Должны присутствовать оба параметра.");
    }

    @Test
    void getAllFromNegativeTest() {
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));

        assertThatExceptionOfType(ValidationException.class)
                .isThrownBy(() -> {
                    List<ItemRequestDtoOut> result = service.getAll(user.getId(),
                            Optional.of(-5), Optional.of(5));
                }).withMessage("Индекс не может быть меньше 0.");
    }

    @Test
    void getAllSizeNegative() {
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));

        assertThatExceptionOfType(ValidationException.class)
                .isThrownBy(() -> {
                    List<ItemRequestDtoOut> result = service.getAll(user.getId(),
                            Optional.of(5), Optional.of(-5));
                }).withMessage("Количество элементов не может быть меньше или равно 0.");
    }

    @Test
    void getByIdTest() {
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));
        Mockito
                .when(mockRequestRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(itemRequest));
        Mockito
                .when(mockItemRepository.findByRequestOrderByIdDesc(Mockito.anyLong()))
                .thenReturn(List.of(item));

        ItemRequestDtoOut result = service.getById(user.getId(), itemRequest.getId());

        assertThat(result.getId(), equalTo(itemRequest.getId()));
        assertThat(result.getDescription(), equalTo(itemRequestDtoInc.getDescription()));
        assertThat(result.getRequestor(), equalTo(user));
        assertThat(result.getCreated(), notNullValue());
        assertThat(result.getItems(), hasSize(1));
        assertThat(result.getItems().get(0).getId(), equalTo(item.getId()));
        assertThat(result.getItems().get(0).getName(), equalTo(item.getName()));
        assertThat(result.getItems().get(0).getDescription(), equalTo(item.getDescription()));
        assertThat(result.getItems().get(0).getAvailable(), equalTo(item.getAvailable()));
        assertThat(result.getItems().get(0).getRequestId(), equalTo(item.getRequest()));
    }

    @Test
    void getByIdUserNotFoundTest() {
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        assertThatExceptionOfType(UserNotFoundException.class)
                .isThrownBy(() -> {
                    ItemRequestDtoOut result = service.getById(user.getId(), itemRequest.getId());
                }).withMessage("Неверный ID пользователя.");
    }

    @Test
    void getByIdRequestNotFoundTest() {
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));
        Mockito
                .when(mockRequestRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        assertThatExceptionOfType(EntityNotFoundException.class)
                .isThrownBy(() -> {
                    ItemRequestDtoOut result = service.getById(user.getId(), itemRequest.getId());
                }).withMessage("Неверный ID запроса.");
    }
}
