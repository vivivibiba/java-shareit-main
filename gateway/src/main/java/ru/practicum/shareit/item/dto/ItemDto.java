package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.validation.Create;
import ru.practicum.shareit.validation.Update;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ItemDto {
    private Long id;

    @NotBlank(groups = Create.class)
    @Pattern(regexp = ".*\\S.*", groups = Update.class)
    private String name;

    @NotBlank(groups = Create.class)
    @Pattern(regexp = ".*\\S.*", groups = Update.class)
    private String description;

    @NotNull(groups = Create.class)
    private Boolean available;

    private Long requestId;
    private Long ownerId;
    private BookingShortDto lastBooking;
    private BookingShortDto nextBooking;
    private List<CommentDto> comments;
}
