package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();

    public Collection<User> findAll() {
        log.debug("Получен список всех пользователей");
        return users.values();
    }

    public User findById(Long id) {
        if (!users.containsKey(id)) {
            throw new NotFoundException("Пользователь не найден");
        }
        return users.get(id);
    }

    public User create(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        user.setId(getNextId());
        user.setLikedMovies(new HashSet<>());
        user.setFriends(new HashSet<>());
        users.put(user.getId(), user);
        log.info("Пользователь создан");
        return user;
    }

    public User update(User newUser) {
        if (newUser.getId() == null) {
            log.error("Ошибка при обновлении фильма");
            throw new ValidationException("Id не может быть пустым");
        }
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
            log.info("Пользователь добавлен");
            return oldUser;
        }
        log.error("Ошибка при обновлении пользователя");
        throw new NotFoundException("Пользователь с таким id не найден");
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
