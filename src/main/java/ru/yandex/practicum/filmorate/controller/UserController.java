package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    Map<Long, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> findAll() {
        log.debug("Получен список всех пользователей");
        return users.values();
    }

    @PostMapping
    public User create(@RequestBody User user) {
        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            log.error("Ошибка при создании пользователя");
            throw new ValidationException("Электронная почта не может быть пустой и должна содержать символ @");
        }
        if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            log.error("Ошибка при создании пользователя");
            throw new ValidationException("Логин не может быть пустым и содержать пробелы");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            log.error("Ошибка при создании пользователя");
            user.setName(user.getLogin());
        }
        if (user.getBirthday() == null || user.getBirthday().isAfter(LocalDate.now())) {
            log.error("Ошибка при создании пользователя");
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
        user.setId(getNextId());
        users.put(user.getId(), user);
        log.error("Пользователь создан");
        return user;
    }

    @PutMapping
    public User update(@RequestBody User newUser) {
        if (newUser.getId() == null) {
            log.error("Ошибка при обновлении пользователя");
            throw new ValidationException("Id не может быть пустым");
        }
        if (newUser.getName() == null || newUser.getEmail().isBlank() && newUser.getEmail().contains("@")) {
            log.error("Ошибка при обновлении пользователя");
            throw new ValidationException("Электронная почта не может быть пустой и должна содержать символ @");
        }
        if (newUser.getName() == null || newUser.getLogin().isBlank() && newUser.getLogin().contains(" ")) {
            log.error("Ошибка при обновлении пользователя");
            throw new ValidationException("логин не может быть пустым и содержать пробелы");
        }
        if (newUser.getName() == null || newUser.getName().isBlank()) {
            newUser.setName(newUser.getLogin());
        }
        if (newUser.getName() == null || newUser.getBirthday().isAfter(LocalDate.now())) {
            log.error("Ошибка при обновлении пользователя");
            throw new ValidationException("дата рождения не может быть в будущем");
        }
        users.values().stream()
                .filter(user -> user.getEmail().equals(newUser.getEmail())
                        && !Objects.equals(user.getId(), newUser.getId()))
                .findFirst()
                .ifPresent(user -> {
                    throw new DuplicatedDataException("Этот имейл уже используется");
                });
        if (users.containsKey(newUser.getId())) {
            User oldUser = users.get(newUser.getId());
            oldUser = oldUser.toBuilder()
                    .name(newUser.getName())
                    .email(newUser.getEmail())
                    .login(newUser.getLogin())
                    .birthday(newUser.getBirthday())
                    .build();
            log.error("Поьзователь добавлен");
            return oldUser;
        }
        log.error("Ошибка при обновлении пользователя");
        throw new ValidationException("Пользователь с таким id не найден");
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
