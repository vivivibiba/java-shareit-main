package ru.practicum.shareit.request.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.validation.Create;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ItemRequestDto {
    private Long id;

    @NotBlank(groups = Create.class)
    private String description;

    private LocalDateTime created;
    private List<ItemDto> items;
}
