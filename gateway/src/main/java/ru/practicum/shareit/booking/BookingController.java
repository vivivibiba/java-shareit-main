package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
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
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.common.ShareItHeaders;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Validated
public class BookingController {
    private static final String BOOKING_STATE_PATTERN = "(?i)ALL|CURRENT|PAST|FUTURE|WAITING|REJECTED";

    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(ShareItHeaders.USER_ID) Long userId,
                                         @Valid @RequestBody BookingCreateDto bookingDto) {
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
                                              @Pattern(regexp = BOOKING_STATE_PATTERN)
                                              @RequestParam(defaultValue = "ALL") String state,
                                              @Min(0) @RequestParam(required = false) Integer from,
                                              @Positive @RequestParam(required = false) Integer size) {
        return bookingClient.getByBooker(userId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getByOwner(@RequestHeader(ShareItHeaders.USER_ID) Long userId,
                                             @Pattern(regexp = BOOKING_STATE_PATTERN)
                                             @RequestParam(defaultValue = "ALL") String state,
                                             @Min(0) @RequestParam(required = false) Integer from,
                                             @Positive @RequestParam(required = false) Integer size) {
        return bookingClient.getByOwner(userId, state, from, size);
    }
}
