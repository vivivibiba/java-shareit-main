package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserDto create(UserDto userDto) {
        validateUserForCreate(userDto);
        checkEmailIsFree(userDto.getEmail(), null);
        User user = userRepository.save(UserMapper.toModel(userDto));
        return UserMapper.toDto(user);
    }

    @Override
    public UserDto update(Long userId, UserDto userDto) {
        if (userDto == null) {
            throw new ValidationException("User body is empty");
        }

        User user = getUserOrThrow(userId);

        if (userDto.getName() != null) {
            validateName(userDto.getName());
            user.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            validateEmail(userDto.getEmail());
            checkEmailIsFree(userDto.getEmail(), userId);
            user.setEmail(userDto.getEmail());
        }

        return UserMapper.toDto(userRepository.update(user));
    }

    @Override
    public UserDto getById(Long userId) {
        return UserMapper.toDto(getUserOrThrow(userId));
    }

    @Override
    public List<UserDto> getAll() {
        return userRepository.findAll().stream()
                .map(UserMapper::toDto)
                .toList();
    }

    @Override
    public void delete(Long userId) {
        userRepository.deleteById(userId);
    }

    @Override
    public void checkUserExists(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User with id=" + userId + " was not found");
        }
    }

    private User getUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found"));
    }

    private void validateUserForCreate(UserDto userDto) {
        if (userDto == null) {
            throw new ValidationException("User body is empty");
        }
        validateName(userDto.getName());
        validateEmail(userDto.getEmail());
    }

    private void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new ValidationException("User name must not be blank");
        }
    }

    private void validateEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new ValidationException("User email must not be blank");
        }
        if (!email.contains("@")) {
            throw new ValidationException("User email must contain @");
        }
    }

    private void checkEmailIsFree(String email, Long currentUserId) {
        userRepository.findByEmail(email)
                .filter(user -> !user.getId().equals(currentUserId))
                .ifPresent(user -> {
                    throw new ConflictException("User with email=" + email + " already exists");
                });
    }
}
