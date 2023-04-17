package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Getter
@Setter
public class User {
    private Long id;
    @Email @NotEmpty
    private String email;
    @NotBlank
    private String login;
    private String name;
    @NotNull
    private LocalDate birthday;
    private Set<Long> friends = new HashSet<>();
}
