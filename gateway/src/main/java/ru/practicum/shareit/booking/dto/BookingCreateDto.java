package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class BookingCreateDto {
    @NotNull
    private Long itemId;

    @NotNull
    @Future
    private LocalDateTime start;

    @NotNull
    @Future
    private LocalDateTime end;

    @AssertTrue(message = "Booking end must be after start")
    public boolean isEndAfterStart() {
        return start == null || end == null || end.isAfter(start);
    }
}
