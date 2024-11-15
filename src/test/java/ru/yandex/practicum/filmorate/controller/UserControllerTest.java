package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {
    UserController userController = new UserController();
    User user;

    @BeforeEach
    void BeforeEach() {
        user = User.builder()
                .id(0L)
                .name("user")
                .email("email@mail.com")
                .birthday(LocalDate.of(2000, 2, 2))
                .login("login")
                .build();
    }

    @Test
    public void testCreateMethodWithValidObject() {
        User testUserObj = userController.create(user);
        assertEquals(testUserObj, user);
    }


    @Test
    public void testCreateMethodWithEmptyEmail() {
        user.setEmail(" ");
        try {
            User testUserObj = userController.create(user);
        } catch (ValidationException e) {
            assertEquals(e.getMessage(), "Электронная почта не может быть пустой и должна содержать символ @");
        }
    }

    @Test
    public void testCreateMethodWithEmptyLogin() {
        user.setLogin(" ");
        try {
            User testUserObj = userController.create(user);
        } catch (ValidationException e) {
            assertEquals(e.getMessage(), "Логин не может быть пустым и содержать пробелы");
        }
    }

    @Test
    public void testCreateMethodWithEmptyBirthday() {
        user.setBirthday(null);
        try {
            User testUserObj = userController.create(user);
        } catch (ValidationException e) {
            assertEquals(e.getMessage(), "Дата рождения не может быть в будущем");
        }
    }

    @Test
    public void testCreateMethodWithBirthdayInFuture() {
        user.setBirthday(LocalDate.of(2111, 12, 12));
        try {
            User testUserObj = userController.create(user);
        } catch (ValidationException e) {
            assertEquals(e.getMessage(), "Дата рождения не может быть в будущем");
        }
    }

    @Test
    public void testUpdateMethodWithNullId() {
        user.setId(null);
        try {
            userController.update(user);
        } catch (ValidationException e) {
            assertEquals(e.getMessage(), "Id не может быть пустым");
        }
    }

    @Test
    public void testUpdateMethodWithSameEmail() {
        userController.create(user);
        User user1 = user;
        user1.setId(10L);
        userController.create(user1);
        try {
            userController.update(user1);
        } catch (ValidationException e) {
            assertEquals(e.getMessage(), "Этот имейл уже используется");
        }
    }

    @Test
    public void testUpdateMethodWithWrongId() {
        userController.create(user);
        User user1 = (user);
        user1.setId(Long.valueOf(44));
        try {
            userController.update(user1);
        } catch (ValidationException e) {
            assertEquals(e.getMessage(), "Пользователь с таким id не найден");
        }
    }


}