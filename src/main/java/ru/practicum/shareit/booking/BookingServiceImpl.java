package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {
    private static final Sort START_DESC = Sort.by(Sort.Direction.DESC, "start");

    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserService userService;

    @Override
    @Transactional
    public BookingDto create(Long userId, BookingCreateDto bookingDto) {
        if (bookingDto == null) {
            throw new BadRequestException("Booking body is empty");
        }
        validatePeriod(bookingDto.getStart(), bookingDto.getEnd());
        User booker = userService.getEntity(userId);
        Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new NotFoundException("Item not found: " + bookingDto.getItemId()));
        if (item.getOwner().getId().equals(userId)) {
            throw new NotFoundException("Owner cannot book own item");
        }
        if (!Boolean.TRUE.equals(item.getAvailable())) {
            throw new BadRequestException("Item is not available");
        }
        Booking booking = new Booking(null, bookingDto.getStart(), bookingDto.getEnd(), item, booker,
                BookingStatus.WAITING);
        return BookingMapper.toDto(bookingRepository.save(booking));
    }

    @Override
    @Transactional
    public BookingDto approve(Long userId, Long bookingId, Boolean approved) {
        userService.getEntity(userId);
        if (approved == null) {
            throw new BadRequestException("Approved parameter is required");
        }
        Booking booking = getEntity(bookingId);
        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new NotFoundException("Only owner can approve booking");
        }
        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new BadRequestException("Booking is already processed");
        }
        booking.setStatus(Boolean.TRUE.equals(approved) ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        return BookingMapper.toDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto getById(Long userId, Long bookingId) {
        userService.getEntity(userId);
        Booking booking = getEntity(bookingId);
        boolean booker = booking.getBooker().getId().equals(userId);
        boolean owner = booking.getItem().getOwner().getId().equals(userId);
        if (!booker && !owner) {
            throw new NotFoundException("Booking is not available for user: " + userId);
        }
        return BookingMapper.toDto(booking);
    }

    @Override
    public List<BookingDto> getByBooker(Long userId, BookingState state, Integer from, Integer size) {
        userService.getEntity(userId);
        Pageable pageable = pageable(from, size);
        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings = switch (state) {
            case ALL -> bookingRepository.findByBooker_Id(userId, pageable);
            case CURRENT -> bookingRepository.findByBooker_IdAndStartBeforeAndEndAfter(userId, now, now, pageable);
            case PAST -> bookingRepository.findByBooker_IdAndEndBefore(userId, now, pageable);
            case FUTURE -> bookingRepository.findByBooker_IdAndStartAfter(userId, now, pageable);
            case WAITING -> bookingRepository.findByBooker_IdAndStatus(userId, BookingStatus.WAITING, pageable);
            case REJECTED -> bookingRepository.findByBooker_IdAndStatus(userId, BookingStatus.REJECTED, pageable);
        };
        return bookings.stream().map(BookingMapper::toDto).toList();
    }

    @Override
    public List<BookingDto> getByOwner(Long userId, BookingState state, Integer from, Integer size) {
        userService.getEntity(userId);
        Pageable pageable = pageable(from, size);
        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings = switch (state) {
            case ALL -> bookingRepository.findByItem_Owner_Id(userId, pageable);
            case CURRENT -> bookingRepository.findByItem_Owner_IdAndStartBeforeAndEndAfter(userId, now, now, pageable);
            case PAST -> bookingRepository.findByItem_Owner_IdAndEndBefore(userId, now, pageable);
            case FUTURE -> bookingRepository.findByItem_Owner_IdAndStartAfter(userId, now, pageable);
            case WAITING -> bookingRepository.findByItem_Owner_IdAndStatus(userId, BookingStatus.WAITING, pageable);
            case REJECTED -> bookingRepository.findByItem_Owner_IdAndStatus(userId, BookingStatus.REJECTED, pageable);
        };
        return bookings.stream().map(BookingMapper::toDto).toList();
    }

    private Booking getEntity(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found: " + bookingId));
    }

    private void validatePeriod(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null || !end.isAfter(start)) {
            throw new BadRequestException("Booking end must be after start");
        }
    }

    private Pageable pageable(Integer from, Integer size) {
        int checkedFrom = from == null ? 0 : from;
        int checkedSize = size == null ? Integer.MAX_VALUE : size;
        if (checkedFrom < 0 || checkedSize <= 0) {
            throw new BadRequestException("Pagination parameters are invalid");
        }
        return PageRequest.of(checkedFrom / checkedSize, checkedSize, START_DESC);
    }
}
