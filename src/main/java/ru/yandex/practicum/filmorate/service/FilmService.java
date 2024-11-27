package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Collection<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film create(Film film) {
        return filmStorage.create(film);
    }

    public Film update(Film newFilm) {
        return filmStorage.update(newFilm);
    }

    public void addLike(Long filmId, Long userId) {
        filmStorage.findById(filmId).getLikes().add(userId);
        log.info("У фильма добавился лайк");
        userStorage.findById(userId).getLikedMovies().add(filmId);
        log.info("У пользователя добавился лайк на фильм");
    }

    public void removeLike(Long filmId, Long userId) {
        filmStorage.findById(filmId).getLikes().remove(userId);
        log.info("Лайк удален");
        userStorage.findById(userId).getLikedMovies().remove(filmId);
        log.info("Фильм из лайков удален");
    }

    public List<Film> findMostPopularFilms(Integer count) {
        Collection<Film> films = filmStorage.findAll();
        if (films == null || films.isEmpty()) {
            return Collections.emptyList();
        }
        return films.stream()
                .sorted((f1, f2) -> Integer.compare(f2.getLikes().size(), f1.getLikes().size()))
                .limit(count)
                .toList();
    }
}
