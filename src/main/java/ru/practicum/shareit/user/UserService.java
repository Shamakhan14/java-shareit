package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.ValidationException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserDto create(UserDto userDto) {
        User user = userRepository.save(UserMapper.mapToNewUser(userDto));
        return UserMapper.mapToUserDto(user);
    }

    public List<UserDto> getAll() {
        List<User> users = userRepository.findAll();
        return UserMapper.mapToUserDto(users);
    }

    public UserDto update(Long userId, UserDto userDto) {
        if (!isValidId(userId)) throw new ValidationException("Неверный ID пользователя.");
        User user = userRepository.findById(userId).get();
        if (userDto.getName() != null) {user.setName(userDto.getName());}
        if (userDto.getEmail() != null) {user.setEmail(userDto.getEmail());}
        return UserMapper.mapToUserDto(userRepository.save(user));
    }

    public UserDto getById(Long userId) {
        if (!isValidId(userId)) throw new UserNotFoundException("Неверный ID пользователя.");
        return UserMapper.mapToUserDto(userRepository.getById(userId));
    }

    public void deleteById(Long userId) {
        userRepository.deleteById(userId);
    }

    private boolean isValidId(Long userId) {
        if (userRepository.getById(userId) != null) {
            return true;
        } else {
            return false;
        }
    }
}
