package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Data
@Builder
public class Mpa {
    @Min(1)
    @Max(5)
    private int id;
    @Max(10)
    private String name;
}
