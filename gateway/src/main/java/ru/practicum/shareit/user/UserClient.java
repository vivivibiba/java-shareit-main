package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.user.dto.UserDto;

@Component
@RequiredArgsConstructor
public class UserClient {
    private final BaseClient baseClient;

    public ResponseEntity<Object> create(UserDto userDto) {
        return baseClient.post("/users", null, userDto);
    }

    public ResponseEntity<Object> update(Long userId, UserDto userDto) {
        return baseClient.patch("/users/" + userId, null, userDto);
    }

    public ResponseEntity<Object> getById(Long userId) {
        return baseClient.get("/users/" + userId, null);
    }

    public ResponseEntity<Object> getAll() {
        return baseClient.get("/users", null);
    }

    public ResponseEntity<Object> delete(Long userId) {
        return baseClient.delete("/users/" + userId, null);
    }
}
