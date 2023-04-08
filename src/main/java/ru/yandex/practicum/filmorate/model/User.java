package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
@AllArgsConstructor
public class User {
    private Integer id;
    @Email @NotEmpty
    private String email;
    @NotEmpty
    @NotBlank
    @Pattern(regexp = "\\S+")
    private String login;
    private String name;
    @NotNull
    private LocalDate birthday;
}
