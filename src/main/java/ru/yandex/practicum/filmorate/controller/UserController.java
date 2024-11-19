package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private Map<Long, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> findAll() {
        log.debug("Получен список всех пользователей");
        return users.values();
    }

    @PostMapping
    public User create(@RequestBody @Valid User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        user.setId(getNextId());
        users.put(user.getId(), user);
        log.error("Пользователь создан");
        return user;
    }

    @PutMapping
    public User update(@RequestBody @Valid User newUser) {
        if (newUser.getName() == null || newUser.getName().isBlank()) {
            newUser.setName(newUser.getLogin());
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
