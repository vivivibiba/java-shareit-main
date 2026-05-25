package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CommentCreateDto {
    @NotBlank
    private String text;
}
