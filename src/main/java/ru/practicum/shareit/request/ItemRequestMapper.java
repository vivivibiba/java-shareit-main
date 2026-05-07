package ru.practicum.shareit.request;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemRequestMapper {
    public static ItemRequest toEntity(ItemRequestDto dto, User requestor) {
        return new ItemRequest(null, dto.getDescription(), requestor, LocalDateTime.now());
    }

    public static ItemRequestDto toDto(ItemRequest request, List<Item> items) {
        List<ItemDto> itemDtos = items.stream()
                .map(ItemMapper::toDto)
                .toList();
        return new ItemRequestDto(
                request.getId(),
                request.getDescription(),
                request.getCreated(),
                itemDtos
        );
    }
}
