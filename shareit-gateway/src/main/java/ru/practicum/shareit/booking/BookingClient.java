package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.client.BaseClient;

@Component
@RequiredArgsConstructor
public class BookingClient {
    private final BaseClient baseClient;

    public ResponseEntity<Object> create(Long userId, BookingCreateDto bookingDto) {
        return baseClient.post("/bookings", userId, bookingDto);
    }

    public ResponseEntity<Object> approve(Long userId, Long bookingId, Boolean approved) {
        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
        parameters.add("approved", approved.toString());
        return baseClient.patch("/bookings/" + bookingId, userId, null, parameters);
    }

    public ResponseEntity<Object> getById(Long userId, Long bookingId) {
        return baseClient.get("/bookings/" + bookingId, userId);
    }

    public ResponseEntity<Object> getByBooker(Long userId, String state, Integer from, Integer size) {
        return baseClient.get("/bookings", userId, pagingParameters(state, from, size));
    }

    public ResponseEntity<Object> getByOwner(Long userId, String state, Integer from, Integer size) {
        return baseClient.get("/bookings/owner", userId, pagingParameters(state, from, size));
    }

    private MultiValueMap<String, String> pagingParameters(String state, Integer from, Integer size) {
        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
        parameters.add("state", state);
        if (from != null) {
            parameters.add("from", from.toString());
        }
        if (size != null) {
            parameters.add("size", size.toString());
        }
        return parameters;
    }
}
