package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public void addLike(Long filmId, Long userId) {
        filmStorage.findOne(filmId).getLikes().add(userId);
        log.info("У фильма добавился лайк");
        userStorage.findOne(userId).getLikedMovies().add(filmId);
        log.info("У пользователя добавился лайк на фильм");
    }

    public void removeLike(Long filmId, Long userId) {
        filmStorage.findOne(filmId).getLikes().remove(userId);
        log.info("Лайк удален");
        userStorage.findOne(userId).getLikedMovies().remove(filmId);
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
