package ru.yandex.practicum.filmorate.service;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Service
@Slf4j
public class UserService {
    private final UserStorage userStorage;

    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public Collection<User> findAll() {
        return userStorage.findAll();
    }

    public User create(User user) {
        return userStorage.create(user);
    }

    public User update(User newUser) {
        return userStorage.update(newUser);
    }

    public void addFriend(Long id, Long friendId) {
        userStorage.findById(id).getFriends().add(friendId);
        log.info("Друг добавлен");

        userStorage.findById(friendId).getFriends().add(id);
        log.info("Пользователь добавлен к другу");
    }

    public void removeFriend(Long id, Long friendId) {
        userStorage.findById(id).getFriends().remove(friendId);
        log.info("Друг удален");

        userStorage.findById(friendId).getFriends().remove(id);
        log.info("Пользователь удален у друга");
    }

    public Collection<User> findUserFriend(Long id) {
        Set<Long> friendsOfUser = new HashSet<>(userStorage.findById(id).getFriends());
        return userStorage.findAll().stream()
                .filter(user -> friendsOfUser.contains(user.getId()))
                .toList();
    }

    public Collection<User> findCommonFriends(Long id, Long otherId) {
        Collection<User> friendsOfFirstUser = findUserFriend(id);
        Collection<User> friendsOfSecondUser = findUserFriend(otherId);

        if (friendsOfFirstUser == null || friendsOfSecondUser == null) {
            return Collections.emptySet();
        }

        Collection<User> commonFriends = new HashSet<>(friendsOfFirstUser);
        commonFriends.retainAll(new HashSet<>(friendsOfSecondUser));
        return commonFriends;
    }
}
