package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();

    public Collection<Film> findAll() {
        log.debug("Пользователь получил список фильмов");
        return films.values();
    }

    public Film findOne(Long id) {
        if (!films.containsKey(id)) {
            throw new NotFoundException("Фильм не найден");
        }
        return films.get(id);
    }

    public Film create(Film film) {
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Дата релиза — не раньше 28 декабря 1895 года");
        }
        film.setId(getNextId());
        film.setLikes(new HashSet<>());
        films.put(film.getId(), film);
        log.debug("Фильм добавлен");
        return film;
    }

    public Film update(Film newFilm) {
        if (newFilm.getId() == null) {
            log.error("Ошибка при обновлении фильма");
            throw new ValidationException("Id не может быть пустым");
        }
        if (newFilm.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Дата релиза — не раньше 28 декабря 1895 года");
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
        throw new NotFoundException("Фильм с таким id не найден");
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
