package ru.practicum.shareit.unit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.ValidationException;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

public class UserServiceUnitTest {

    private UserRepository mockRepository;
    private UserService userService;
    private UserDto userDto;
    private User user;

    @BeforeEach
    void setUp() {
        mockRepository = Mockito.mock(UserRepository.class);
        userService = new UserService(mockRepository);

        userDto = new UserDto();
        userDto.setName("name");
        userDto.setEmail("email@email.com");

        user = new User();
        user.setId(1L);
        user.setName("name");
        user.setEmail("email@email.com");
    }

    @Test
    void createTest() {
        Mockito
                .when(mockRepository.save(Mockito.any()))
                .thenReturn(user);

        UserDto result = userService.create(userDto);

        assertThat(result.getId(), equalTo(user.getId()));
        assertThat(result.getName(), equalTo(userDto.getName()));
        assertThat(result.getEmail(), equalTo(userDto.getEmail()));
    }

    @Test
    void getAllTest() {
        Mockito
                .when(mockRepository.findAll())
                .thenReturn(List.of(user));

        List<UserDto> result = userService.getAll();

        assertThat(result, hasSize(1));
        assertThat(result.get(0).getId(), equalTo(user.getId()));
        assertThat(result.get(0).getName(), equalTo(userDto.getName()));
        assertThat(result.get(0).getEmail(), equalTo(userDto.getEmail()));
    }

    @Test
    void updateTest() {
        UserDto update = new UserDto();
        update.setName("new name");
        update.setEmail("newEmail@email.com");

        Mockito
                .when(mockRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));

        UserDto result = userService.update(user.getId(), update);

        assertThat(result.getId(), equalTo(user.getId()));
        assertThat(result.getName(), equalTo(update.getName()));
        assertThat(result.getEmail(), equalTo(update.getEmail()));
    }

    @Test
    void updateUserNotFoundTest() {
        UserDto update = new UserDto();
        update.setName("new name");
        update.setEmail("newEmail@email.com");

        Mockito
                .when(mockRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        assertThatExceptionOfType(ValidationException.class)
                .isThrownBy(() -> {
                    UserDto result = userService.update(user.getId(), update);
                }).withMessage("Неверный ID пользователя.");
    }

    @Test
    void getByIdTest() {
        Mockito
                .when(mockRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));

        UserDto result = userService.getById(user.getId());

        assertThat(result.getId(), equalTo(user.getId()));
        assertThat(result.getName(), equalTo(user.getName()));
        assertThat(result.getEmail(), equalTo(user.getEmail()));
    }

    @Test
    void getByIdUserNotFoundTest() {
        Mockito
                .when(mockRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        assertThatExceptionOfType(UserNotFoundException.class)
                .isThrownBy(() -> {
                    UserDto result = userService.getById(user.getId());
                }).withMessage("Неверный ID пользователя.");
    }
}
