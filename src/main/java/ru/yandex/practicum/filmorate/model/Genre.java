package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Data
public class Genre {
    @Min(1)
    @Max(6)
    private int id;
    private String name;
}
