package com.globaldashboard.db.controller;

import com.globaldashboard.db.entity.User;
import com.globaldashboard.db.repository.UserRepository;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import java.util.Optional;

@Controller("/users")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Get("/{username}")
    public Optional<User> findUser(@PathVariable String username) {
        // Returns the full User entity including password hash
        // Security: Correct, this is an internal API for Auth Service only.
        return userRepository.findByUsername(username);
    }
}
