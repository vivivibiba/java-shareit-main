package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> add(@RequestHeader(ShareItHeaders.USER_ID) Long ownerId,
                                      @RequestBody ItemDto itemDto) {
        validateItemForCreate(itemDto);
        return itemClient.add(ownerId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(@RequestHeader(ShareItHeaders.USER_ID) Long ownerId,
                                         @PathVariable Long itemId,
                                         @RequestBody ItemDto itemDto) {
        validateItemForUpdate(itemDto);
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
                                             @RequestBody CommentCreateDto commentDto) {
        if (commentDto == null || isBlank(commentDto.getText())) {
            throw new BadRequestException("Comment text must not be blank");
        }
        return itemClient.addComment(userId, itemId, commentDto);
    }

    private void validateItemForCreate(ItemDto itemDto) {
        if (itemDto == null) {
            throw new BadRequestException("Item body is empty");
        }
        validateName(itemDto.getName());
        validateDescription(itemDto.getDescription());
        if (itemDto.getAvailable() == null) {
            throw new BadRequestException("Item availability must be specified");
        }
    }

    private void validateItemForUpdate(ItemDto itemDto) {
        if (itemDto == null) {
            throw new BadRequestException("Item body is empty");
        }
        if (itemDto.getName() != null) {
            validateName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            validateDescription(itemDto.getDescription());
        }
    }

    private void validateName(String name) {
        if (isBlank(name)) {
            throw new BadRequestException("Item name must not be blank");
        }
    }

    private void validateDescription(String description) {
        if (isBlank(description)) {
            throw new BadRequestException("Item description must not be blank");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
