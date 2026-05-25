package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDto create(UserDto userDto) {
        validateForCreate(userDto);
        if (userRepository.existsByEmailIgnoreCase(userDto.getEmail())) {
            throw new ConflictException("Email already exists: " + userDto.getEmail());
        }
        return UserMapper.toDto(userRepository.save(UserMapper.toModel(userDto)));
    }

    @Override
    @Transactional
    public UserDto update(Long userId, UserDto userDto) {
        if (userDto == null) {
            throw new BadRequestException("User body is empty");
        }
        User user = getEntity(userId);
        if (userDto.getName() != null) {
            validateName(userDto.getName());
            user.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            validateEmail(userDto.getEmail());
            userRepository.findByEmailIgnoreCase(userDto.getEmail())
                    .filter(existing -> !existing.getId().equals(userId))
                    .ifPresent(existing -> {
                        throw new ConflictException("Email already exists: " + userDto.getEmail());
                    });
            user.setEmail(userDto.getEmail());
        }
        return UserMapper.toDto(userRepository.save(user));
    }

    @Override
    public UserDto getById(Long userId) {
        return UserMapper.toDto(getEntity(userId));
    }

    @Override
    public List<UserDto> getAll() {
        return userRepository.findAll()
                .stream()
                .map(UserMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public void delete(Long userId) {
        if (userRepository.existsById(userId)) {
            userRepository.deleteById(userId);
        }
    }

    @Override
    public void checkUserExists(Long userId) {
        getEntity(userId);
    }

    @Override
    public User getEntity(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found: " + userId));
    }

    private void validateForCreate(UserDto userDto) {
        if (userDto == null) {
            throw new BadRequestException("User body is empty");
        }
        validateName(userDto.getName());
        validateEmail(userDto.getEmail());
    }

    private void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new BadRequestException("User name must not be blank");
        }
    }

    private void validateEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new BadRequestException("User email must not be blank");
        }
        if (!email.contains("@")) {
            throw new BadRequestException("User email must contain @");
        }
    }
}
