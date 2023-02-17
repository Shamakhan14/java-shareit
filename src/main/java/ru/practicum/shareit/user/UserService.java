package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.ValidationException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public UserDto create(UserDto userDto) {
        User user = userRepository.save(UserMapper.mapToNewUser(userDto));
        return UserMapper.mapToUserDto(user);
    }

    public List<UserDto> getAll() {
        List<User> users = userRepository.findAll();
        return UserMapper.mapToUserDto(users);
    }

    @Transactional
    public UserDto update(Long userId, UserDto userDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ValidationException("Неверный ID пользователя."));
        if (userDto.getName() != null && !userDto.getName().isBlank()) {
            user.setName(userDto.getName());
        }
        if (userDto.getEmail() != null && !userDto.getEmail().isBlank()) {
            user.setEmail(userDto.getEmail());
        }
        return UserMapper.mapToUserDto(user);
    }

    public UserDto getById(Long userId) {
        if (!isValidId(userId)) throw new UserNotFoundException("Неверный ID пользователя.");
        return UserMapper.mapToUserDto(userRepository.getById(userId));
    }

    @Transactional
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
