package ru.practicum.shareit.booking;

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
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.common.ShareItHeaders;
import ru.practicum.shareit.exception.BadRequestException;

import java.time.LocalDateTime;
import java.util.Set;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private static final Set<String> STATES = Set.of("ALL", "CURRENT", "PAST", "FUTURE", "WAITING", "REJECTED");

    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(ShareItHeaders.USER_ID) Long userId,
                                         @RequestBody BookingCreateDto bookingDto) {
        validateBooking(bookingDto);
        return bookingClient.create(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approve(@RequestHeader(ShareItHeaders.USER_ID) Long userId,
                                          @PathVariable Long bookingId,
                                          @RequestParam Boolean approved) {
        return bookingClient.approve(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getById(@RequestHeader(ShareItHeaders.USER_ID) Long userId,
                                          @PathVariable Long bookingId) {
        return bookingClient.getById(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getByBooker(@RequestHeader(ShareItHeaders.USER_ID) Long userId,
                                              @RequestParam(defaultValue = "ALL") String state,
                                              @RequestParam(required = false) Integer from,
                                              @RequestParam(required = false) Integer size) {
        validateState(state);
        validatePagination(from, size);
        return bookingClient.getByBooker(userId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getByOwner(@RequestHeader(ShareItHeaders.USER_ID) Long userId,
                                             @RequestParam(defaultValue = "ALL") String state,
                                             @RequestParam(required = false) Integer from,
                                             @RequestParam(required = false) Integer size) {
        validateState(state);
        validatePagination(from, size);
        return bookingClient.getByOwner(userId, state, from, size);
    }

    private void validateBooking(BookingCreateDto bookingDto) {
        if (bookingDto == null) {
            throw new BadRequestException("Booking body is empty");
        }
        LocalDateTime now = LocalDateTime.now();
        if (bookingDto.getItemId() == null || bookingDto.getStart() == null || bookingDto.getEnd() == null) {
            throw new BadRequestException("Booking itemId, start and end are required");
        }
        if (!bookingDto.getEnd().isAfter(bookingDto.getStart()) || bookingDto.getStart().isBefore(now)) {
            throw new BadRequestException("Booking period must be in the future and end must be after start");
        }
    }

    private void validateState(String state) {
        if (state == null || !STATES.contains(state.toUpperCase())) {
            throw new BadRequestException("Unknown state: " + state);
        }
    }

    private void validatePagination(Integer from, Integer size) {
        if ((from != null && from < 0) || (size != null && size <= 0)) {
            throw new BadRequestException("Pagination parameters are invalid");
        }
    }
}
