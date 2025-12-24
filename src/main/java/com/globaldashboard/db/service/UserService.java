package com.globaldashboard.db.service;


import com.globaldashboard.db.entity.User;
import com.globaldashboard.db.event.user.UserCreateRequest;
import com.globaldashboard.db.event.user.UserEvent;
import com.globaldashboard.db.event.user.UserFindRequest;
import com.globaldashboard.db.mapper.UserMapper;
import com.globaldashboard.db.producer.UserProducer;
import com.globaldashboard.db.repository.UserRepository;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Singleton
public class UserService {

    private final UserRepository userRepository;
    private final UserProducer userProducer;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, UserProducer userProducer,
            UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userProducer = userProducer;
        this.userMapper = userMapper;
    }

    @Transactional
    public void createUser(UserCreateRequest request) {
        log.info("Processing creation request for user: {}", request.username());

        if (userRepository.existsByEmail(request.email())) {
            log.warn("User email already exists: {}", request.email());
            userProducer.sendEvent(request.email(), new UserEvent(null, request.username(),
                    request.email(), null, UserEvent.EventType.ERROR, "Email already exists"));
            return;
        }

        User newUser = userMapper.toEntity(request);
        User savedUser = userRepository.save(newUser);

        log.info("User created successfully with ID: {}", savedUser.getId());
        UserEvent event = new UserEvent(savedUser.getId(), savedUser.getUsername(),
                savedUser.getEmail(), null, UserEvent.EventType.CREATED, "User created successfully");
        userProducer.sendEvent(savedUser.getUsername(), event);
    }

    @Transactional
    public void findUser(UserFindRequest request) {
        log.info("Processing find request for user: {}", request.username());
        Optional<User> userOpt = userRepository.findByUsername(request.username());

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            // We expose the password hash ONLY for internal services (like Auth) listening to this event.
            UserEvent event = new UserEvent(user.getId(), user.getUsername(), user.getEmail(),
                    user.getPasswordHash(), UserEvent.EventType.FOUND, "User found");
            userProducer.sendEvent(user.getUsername(), event);
        } else {
            log.warn("User not found: {}", request.username());
            userProducer.sendEvent(request.username(), new UserEvent(null, request.username(), null, null,
                    UserEvent.EventType.NOT_FOUND, "User not found"));
        }
    }
}
