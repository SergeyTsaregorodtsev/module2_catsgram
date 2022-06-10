package ru.yandex.practicum.catsgram.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.catsgram.controller.*;
import ru.yandex.practicum.catsgram.exception.*;
import ru.yandex.practicum.catsgram.model.*;

import java.util.HashMap;
import java.util.HashSet;

@Service
public class UserService {
    private final HashMap<String, User> users = new HashMap<>();
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    public HashSet<User> getUsers() {
        HashSet<User> userSet = new HashSet<>(users.values());
        log.trace("Количество пользователей в текущий момент: {}", userSet.size());
        return userSet;
    }

    public User createUser(@RequestBody User user) throws UserAlreadyExistException{
        String email = user.getEmail();
        if (email == null || email.isBlank()){
            throw new InvalidEmailException("Адрес электронной почты не может быть пустым.");
        }
        if (users.containsKey(email)) {
            throw new UserAlreadyExistException(String.format
                    ("Пользователь с электронной почтой %s уже зарегистрирован.", email));
        }
        log.trace("Добавляем пользователя: {}", user.getEmail());
        users.put(email,user);
        return user;
    }

    public User updateUser(@RequestBody User user) throws InvalidEmailException{
        String email = user.getEmail();
        if (email == null || email.isBlank()){
            throw new InvalidEmailException("Адрес электронной почты не может быть пустым.");
        }
        users.put(email,user);
        return user;
    }

    public User findUserByEmail(String author) {
        return users.get(author);
    }
}
