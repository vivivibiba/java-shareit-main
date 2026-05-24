package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;

@Component
@RequiredArgsConstructor
public class ItemClient {
    private final BaseClient baseClient;

    public ResponseEntity<Object> add(Long userId, ItemDto itemDto) {
        return baseClient.post("/items", userId, itemDto);
    }

    public ResponseEntity<Object> update(Long userId, Long itemId, ItemDto itemDto) {
        return baseClient.patch("/items/" + itemId, userId, itemDto);
    }

    public ResponseEntity<Object> getById(Long userId, Long itemId) {
        return baseClient.get("/items/" + itemId, userId);
    }

    public ResponseEntity<Object> getOwnerItems(Long userId) {
        return baseClient.get("/items", userId);
    }

    public ResponseEntity<Object> search(String text) {
        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
        parameters.add("text", text);
        return baseClient.get("/items/search", null, parameters);
    }

    public ResponseEntity<Object> addComment(Long userId, Long itemId, CommentCreateDto commentDto) {
        return baseClient.post("/items/" + itemId + "/comment", userId, commentDto);
    }
}
