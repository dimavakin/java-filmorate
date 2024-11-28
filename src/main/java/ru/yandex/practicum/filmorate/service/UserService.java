package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;


public interface UserService {
    Collection<User> findAll();

    User create(User user);

    User update(User newUser);

    void addFriend(Long id, Long friendId);

    void removeFriend(Long id, Long friendId);

    Collection<User> findUserFriend(Long id);

    Collection<User> findCommonFriends(Long id, Long otherId);
}
