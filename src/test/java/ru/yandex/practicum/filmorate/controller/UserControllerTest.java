package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {
    UserController userController = new UserController();
    User user;

    @BeforeEach
    void beforeEach() {
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
        assertEquals(user,testUserObj);
    }


    @Test
    public void testCreateMethodWithEmptyEmail() {
        user.setEmail(" ");
        try {
            User testUserObj = userController.create(user);
        } catch (ValidationException e) {
            assertEquals("Электронная почта не может быть пустой и должна содержать символ @", e.getMessage());
        }
    }

    @Test
    public void testCreateMethodWithEmptyLogin() {
        user.setLogin(" ");
        try {
            User testUserObj = userController.create(user);
        } catch (ValidationException e) {
            assertEquals("Логин не может быть пустым и содержать пробелы", e.getMessage());
        }
    }

    @Test
    public void testCreateMethodWithEmptyBirthday() {
        user.setBirthday(null);
        try {
            User testUserObj = userController.create(user);
        } catch (ValidationException e) {
            assertEquals("Дата рождения не может быть в будущем", e.getMessage());
        }
    }

    @Test
    public void testCreateMethodWithBirthdayInFuture() {
        user.setBirthday(LocalDate.of(2111, 12, 12));
        try {
            User testUserObj = userController.create(user);
        } catch (ValidationException e) {
            assertEquals("Дата рождения не может быть в будущем", e.getMessage());
        }
    }

    @Test
    public void testUpdateMethodWithNullId() {
        user.setId(null);
        try {
            userController.update(user);
        } catch (ValidationException e) {
            assertEquals("Id не может быть пустым", e.getMessage());
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
            assertEquals("Этот имейл уже используется", e.getMessage());
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
            assertEquals("Пользователь с таким id не найден", e.getMessage());
        }
    }


}