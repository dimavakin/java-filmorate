package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    Map<Long, Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> findAll() {
        log.debug("Пользователь получил список фильмов");
        return films.values();
    }


    @PostMapping
    public Film create(@RequestBody Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            log.error("Ошибка при добавлении фильма");
            throw new ValidationException("Название не может быть пустым");
        }
        if (film.getDescription().length() > 200) {
            log.error("Ошибка при добавлении фильма");
            throw new ValidationException("Максимальная длина описания — 200 символов");
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.error("Ошибка при добавлении фильма");
            throw new ValidationException("Дата релиза — не раньше 28 декабря 1895 года");
        }
        if (film.getDuration() <= 0) {
            log.error("Ошибка при добавлении фильма");
            throw new ValidationException("Продолжительность фильма должна быть положительным числом.");
        }
        film.setId(getNextId());
        films.put(film.getId(), film);
        log.debug("Фильм добавлен");
        return film;
    }

    @PutMapping
    public Film update(@RequestBody Film newFilm) {
        if (newFilm.getId() == null) {
            log.error("Ошибка при обновлении фильма");
            throw new ValidationException("Id не может быть пустым");
        }
        if (newFilm.getDescription().length() > 200) {
            log.error("Ошибка при обновлении фильма");
            throw new ValidationException("Максимальная длина описания — 200 символов");
        }
        if (newFilm.getReleaseDate() != null && newFilm.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.error("Ошибка при обновлении фильма");
            throw new ValidationException("Дата релиза — не раньше 28 декабря 1895 года");
        }
        if (newFilm.getDuration() <= 0) {
            log.error("Ошибка при обновлении фильма");
            throw new ValidationException("Продолжительность фильма должна быть положительным числом.");
        }
        if (films.containsKey(newFilm.getId())) {
            Film oldFilm = films.get(newFilm.getId());
            System.out.println(newFilm.getReleaseDate());
            oldFilm = oldFilm.toBuilder()
                    .name(newFilm.getName().isBlank() ? oldFilm.getName() : newFilm.getName())
                    .description(newFilm.getDescription() == null ? oldFilm.getDescription() : newFilm.getDescription())
                    .releaseDate(newFilm.getReleaseDate() == null ? oldFilm.getReleaseDate() : newFilm.getReleaseDate())
                    .duration(newFilm.getDuration() == null ? oldFilm.getDuration() : newFilm.getDuration())
                    .build();
            log.info("Фильм обновлен");
            return oldFilm;
        }
        log.error("Ошибка при обновлении фильма");
        throw new ValidationException("Фильм с таким id не найден");
    }


    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
