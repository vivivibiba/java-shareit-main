package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;
    private final ItemRequestRepository requestRepository;
    private final UserService userService;

    @Override
    @Transactional
    public ItemDto add(Long ownerId, ItemDto itemDto) {
        validateItemForCreate(itemDto);
        User owner = userService.getEntity(ownerId);
        ItemRequest request = null;
        if (itemDto.getRequestId() != null) {
            request = requestRepository.findById(itemDto.getRequestId())
                    .orElseThrow(() -> new NotFoundException("Request not found: " + itemDto.getRequestId()));
        }
        Item item = ItemMapper.toModel(itemDto, owner, request);
        return ItemMapper.toDto(itemRepository.save(item));
    }

    @Override
    @Transactional
    public ItemDto update(Long ownerId, Long itemId, ItemDto itemDto) {
        if (itemDto == null) {
            throw new BadRequestException("Item body is empty");
        }
        userService.getEntity(ownerId);
        Item item = getEntity(itemId);
        if (!item.getOwner().getId().equals(ownerId)) {
            throw new NotFoundException("Only owner can update item");
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
        return toDto(itemRepository.save(item), ownerId);
    }

    @Override
    public ItemDto getById(Long userId, Long itemId) {
        userService.getEntity(userId);
        return toDto(getEntity(itemId), userId);
    }

    @Override
    public List<ItemDto> getOwnerItems(Long ownerId) {
        userService.getEntity(ownerId);
        return itemRepository.findByOwner_IdOrderByIdAsc(ownerId)
                .stream()
                .map(item -> toDto(item, ownerId))
                .toList();
    }

    @Override
    public List<ItemDto> search(String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }
        return itemRepository.searchAvailableByText(text)
                .stream()
                .map(ItemMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public CommentDto addComment(Long userId, Long itemId, CommentCreateDto commentDto) {
        if (commentDto == null || commentDto.getText() == null || commentDto.getText().isBlank()) {
            throw new BadRequestException("Comment text must not be blank");
        }
        User author = userService.getEntity(userId);
        Item item = getEntity(itemId);
        boolean booked = bookingRepository.existsByItem_IdAndBooker_IdAndStatusAndEndBefore(
                itemId,
                userId,
                BookingStatus.APPROVED,
                LocalDateTime.now()
        );
        if (!booked) {
            throw new BadRequestException("User has not completed approved booking for this item");
        }
        Comment comment = new Comment(null, commentDto.getText(), item, author, LocalDateTime.now());
        return ItemMapper.toCommentDto(commentRepository.save(comment));
    }

    @Override
    public Item getEntity(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found: " + itemId));
    }

    private ItemDto toDto(Item item, Long userId) {
        List<Comment> comments = commentRepository.findByItem_IdOrderByCreatedAsc(item.getId());
        if (!item.getOwner().getId().equals(userId)) {
            return ItemMapper.toDto(item, null, null, comments);
        }
        LocalDateTime now = LocalDateTime.now();
        Booking lastBooking = bookingRepository
                .findFirstByItem_IdAndStatusAndEndBeforeOrderByEndDesc(item.getId(), BookingStatus.APPROVED, now)
                .orElse(null);
        Booking nextBooking = bookingRepository
                .findFirstByItem_IdAndStatusAndStartAfterOrderByStartAsc(item.getId(), BookingStatus.APPROVED, now)
                .orElse(null);
        return ItemMapper.toDto(item, lastBooking, nextBooking, comments);
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

    private void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new BadRequestException("Item name must not be blank");
        }
    }

    private void validateDescription(String description) {
        if (description == null || description.isBlank()) {
            throw new BadRequestException("Item description must not be blank");
        }
    }
}
