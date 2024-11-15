package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FilmControllerTest {
    FilmController filmController = new FilmController();
    Film film;

    @BeforeEach
    void BeforeEach() {
        film = Film.builder()
                .id(0L)
                .name("film")
                .description("description")
                .releaseDate(LocalDate.of(2000, 2, 2))
                .duration(130)
                .build();
    }

    @Test
    public void testCreateMethodWithValidObject() {
        Film testFilmObj = filmController.create(film);
        assertEquals(testFilmObj, film);
    }

    @Test
    public void testCreateMethodWhenDescMoreThan200Chars() {
        String desc = "1".repeat(201);
        ;
        film.setDescription(desc);
        try {
            filmController.create(film);
        } catch (ValidationException e) {
            assertEquals(e.getMessage(), "Максимальная длина описания — 200 символов");
        }
    }

    @Test
    public void testCreateMethodWithNegativeDuration() {
        film.setDuration(-12);
        try {
            filmController.create(film);
        } catch (ValidationException e) {
            assertEquals(e.getMessage(), "Продолжительность фильма должна быть положительным числом.");
        }
    }

    @Test
    public void testCreateMethodWhenNameNull() {
        film.setName(null);
        try {
            filmController.create(film);
        } catch (ValidationException e) {
            assertEquals(e.getMessage(), "Название не может быть пустым");
        }
    }

    @Test
    public void testCreateMethodWhenReleaseDateBefore18951228() {
        film.setReleaseDate(LocalDate.of(1894, 1, 28));
        try {
            filmController.create(film);
        } catch (ValidationException e) {
            assertEquals(e.getMessage(), "Дата релиза — не раньше 28 декабря 1895 года");
        }
    }

    @Test
    public void testUpdateMethodWithNullId() {
        filmController.create(film);
        film.setId(null);
        try {
            filmController.update(film);
        } catch (ValidationException e) {
            assertEquals(e.getMessage(), "Id не может быть пустым");
        }

    }

    @Test
    public void testUpdateMethodWithEmptyName() {
        filmController.create(film);
        Film newFilm = filmController.update(film.toBuilder().name("").build());
        assertEquals("film", newFilm.getName());
    }

    @Test
    public void testUpdateMethodWithOtherReleaseDate() {
        filmController.create(film);

        Film newFilm = filmController.update(film.toBuilder().releaseDate(LocalDate.of(2022, 12, 21)).build());
        assertEquals(LocalDate.of(2022, 12, 21), newFilm.getReleaseDate());
    }


    @Test
    public void testUpdateMethodWithValidRequest() {
        filmController.create(film);
        Film film1 = (film);
        film1.setDescription("other desc");
        filmController.update(film1);
        assertEquals("other desc", film1.getDescription());
    }

    @Test
    public void testUpdateMethodWithWrongId() {
        filmController.create(film);
        Film film1 = (film);
        film1.setId(1L);
        try {
            filmController.update(film1);
        } catch (ValidationException e) {
            assertEquals(e.getMessage(), "Фильм с таким id не найден");
        }
    }
}