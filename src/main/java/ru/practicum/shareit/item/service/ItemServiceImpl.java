package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserService userService;

    @Override
    public ItemDto add(Long ownerId, ItemDto itemDto) {
        userService.checkUserExists(ownerId);
        validateItemForCreate(itemDto);
        Item item = itemRepository.save(ItemMapper.toModel(itemDto, ownerId));
        return ItemMapper.toDto(item);
    }

    @Override
    public ItemDto update(Long ownerId, Long itemId, ItemDto itemDto) {
        userService.checkUserExists(ownerId);
        if (itemDto == null) {
            throw new ValidationException("Item body is empty");
        }

        Item item = getItemOrThrow(itemId);
        if (!item.getOwnerId().equals(ownerId)) {
            throw new NotFoundException("Only owner can update item with id=" + itemId);
        }

        if (itemDto.getName() != null) {
            validateName(itemDto.getName());
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            validateDescription(itemDto.getDescription());
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }

        return ItemMapper.toDto(itemRepository.update(item));
    }

    @Override
    public ItemDto getById(Long itemId) {
        return ItemMapper.toDto(getItemOrThrow(itemId));
    }

    @Override
    public List<ItemDto> getOwnerItems(Long ownerId) {
        userService.checkUserExists(ownerId);
        return itemRepository.findByOwnerId(ownerId).stream()
                .map(ItemMapper::toDto)
                .toList();
    }

    @Override
    public List<ItemDto> search(String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }
        return itemRepository.searchAvailableByText(text).stream()
                .map(ItemMapper::toDto)
                .toList();
    }

    private Item getItemOrThrow(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item with id=" + itemId + " was not found"));
    }

    private void validateItemForCreate(ItemDto itemDto) {
        if (itemDto == null) {
            throw new ValidationException("Item body is empty");
        }
        validateName(itemDto.getName());
        validateDescription(itemDto.getDescription());
        if (itemDto.getAvailable() == null) {
            throw new ValidationException("Item availability must be specified");
        }
    }

    private void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new ValidationException("Item name must not be blank");
        }
    }

    private void validateDescription(String description) {
        if (description == null || description.isBlank()) {
            throw new ValidationException("Item description must not be blank");
        }
    }
}
