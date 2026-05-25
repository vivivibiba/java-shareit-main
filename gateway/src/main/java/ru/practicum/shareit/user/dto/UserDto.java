package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.validation.Create;
import ru.practicum.shareit.validation.Update;

@Getter
@Setter
@NoArgsConstructor
public class UserDto {
    private Long id;

    @NotBlank(groups = Create.class)
    @Pattern(regexp = ".*\\S.*", groups = Update.class)
    private String name;

    @NotBlank(groups = Create.class)
    @Pattern(regexp = ".*\\S.*", groups = Update.class)
    @Email(groups = {Create.class, Update.class})
    private String email;
}
