package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.common.ShareItHeaders;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.validation.Create;
import ru.practicum.shareit.validation.Update;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> add(@RequestHeader(ShareItHeaders.USER_ID) Long ownerId,
                                      @Validated(Create.class) @RequestBody ItemDto itemDto) {
        return itemClient.add(ownerId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(@RequestHeader(ShareItHeaders.USER_ID) Long ownerId,
                                         @PathVariable Long itemId,
                                         @Validated(Update.class) @RequestBody ItemDto itemDto) {
        return itemClient.update(ownerId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getById(@RequestHeader(ShareItHeaders.USER_ID) Long userId,
                                          @PathVariable Long itemId) {
        return itemClient.getById(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getOwnerItems(@RequestHeader(ShareItHeaders.USER_ID) Long ownerId) {
        return itemClient.getOwnerItems(ownerId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(@RequestParam String text) {
        return itemClient.search(text);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader(ShareItHeaders.USER_ID) Long userId,
                                             @PathVariable Long itemId,
                                             @Valid @RequestBody CommentCreateDto commentDto) {
        return itemClient.addComment(userId, itemId, commentDto);
    }
}
