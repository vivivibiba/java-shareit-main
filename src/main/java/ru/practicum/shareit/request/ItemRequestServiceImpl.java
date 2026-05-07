package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {
    private static final Sort CREATED_DESC = Sort.by(Sort.Direction.DESC, "created");

    private final ItemRequestRepository requestRepository;
    private final ItemRepository itemRepository;
    private final UserService userService;

    @Override
    @Transactional
    public ItemRequestDto create(Long userId, ItemRequestDto requestDto) {
        validateRequest(requestDto);
        User requestor = userService.getEntity(userId);
        ItemRequest request = requestRepository.save(ItemRequestMapper.toEntity(requestDto, requestor));
        return ItemRequestMapper.toDto(request, List.of());
    }

    @Override
    public List<ItemRequestDto> getOwn(Long userId) {
        userService.getEntity(userId);
        return requestRepository.findByRequestor_IdOrderByCreatedDesc(userId)
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public List<ItemRequestDto> getAll(Long userId, Integer from, Integer size) {
        userService.getEntity(userId);
        return requestRepository.findByRequestor_IdNotOrderByCreatedDesc(userId, pageable(from, size))
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public ItemRequestDto getById(Long userId, Long requestId) {
        userService.getEntity(userId);
        ItemRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Request not found: " + requestId));
        return toDto(request);
    }

    private ItemRequestDto toDto(ItemRequest request) {
        List<Item> items = itemRepository.findByRequest_IdOrderByIdAsc(request.getId());
        return ItemRequestMapper.toDto(request, items);
    }

    private void validateRequest(ItemRequestDto requestDto) {
        if (requestDto == null || requestDto.getDescription() == null || requestDto.getDescription().isBlank()) {
            throw new BadRequestException("Request description must not be blank");
        }
    }

    private Pageable pageable(Integer from, Integer size) {
        int checkedFrom = from == null ? 0 : from;
        int checkedSize = size == null ? Integer.MAX_VALUE : size;
        if (checkedFrom < 0 || checkedSize <= 0) {
            throw new BadRequestException("Pagination parameters are invalid");
        }
        return PageRequest.of(checkedFrom / checkedSize, checkedSize, CREATED_DESC);
    }
}
