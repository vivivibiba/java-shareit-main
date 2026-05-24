package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.request.dto.ItemRequestDto;

@Component
@RequiredArgsConstructor
public class ItemRequestClient {
    private final BaseClient baseClient;

    public ResponseEntity<Object> create(Long userId, ItemRequestDto requestDto) {
        return baseClient.post("/requests", userId, requestDto);
    }

    public ResponseEntity<Object> getOwn(Long userId) {
        return baseClient.get("/requests", userId);
    }

    public ResponseEntity<Object> getAll(Long userId, Integer from, Integer size) {
        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
        if (from != null) {
            parameters.add("from", from.toString());
        }
        if (size != null) {
            parameters.add("size", size.toString());
        }
        return baseClient.get("/requests/all", userId, parameters);
    }

    public ResponseEntity<Object> getById(Long userId, Long requestId) {
        return baseClient.get("/requests/" + requestId, userId);
    }
}
