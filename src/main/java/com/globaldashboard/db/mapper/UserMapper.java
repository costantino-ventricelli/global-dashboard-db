package com.globaldashboard.db.mapper;

import com.globaldashboard.db.entity.User;
import com.globaldashboard.db.event.user.UserCreateRequest;
import com.globaldashboard.db.event.user.UserEvent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "jsr330")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", expression = "java(java.time.Instant.now())")
    User toEntity(UserCreateRequest request);

    @Mapping(target = "type", ignore = true) // Set manually in service
    @Mapping(target = "message", ignore = true) // Set manually in service
    UserEvent toEvent(User user);
}
