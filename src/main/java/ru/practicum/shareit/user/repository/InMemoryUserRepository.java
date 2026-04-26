package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.User;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryUserRepository implements UserRepository {
    private final Map<Long, User> users = new LinkedHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public synchronized User save(User user) {
        Long id = idGenerator.getAndIncrement();
        User savedUser = new User(id, user.getName(), user.getEmail());
        users.put(id, savedUser);
        return copy(savedUser);
    }

    @Override
    public synchronized User update(User user) {
        User updatedUser = copy(user);
        users.put(updatedUser.getId(), updatedUser);
        return copy(updatedUser);
    }

    @Override
    public synchronized Optional<User> findById(Long userId) {
        return Optional.ofNullable(users.get(userId)).map(this::copy);
    }

    @Override
    public synchronized List<User> findAll() {
        return users.values().stream()
                .map(this::copy)
                .toList();
    }

    @Override
    public synchronized void deleteById(Long userId) {
        users.remove(userId);
    }

    @Override
    public synchronized boolean existsById(Long userId) {
        return users.containsKey(userId);
    }

    @Override
    public synchronized Optional<User> findByEmail(String email) {
        return new ArrayList<>(users.values()).stream()
                .filter(user -> user.getEmail().equalsIgnoreCase(email))
                .findFirst()
                .map(this::copy);
    }

    private User copy(User user) {
        return new User(user.getId(), user.getName(), user.getEmail());
    }
}
