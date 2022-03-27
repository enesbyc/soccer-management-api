package com.soccer.management.model.mapper;

import java.util.Collection;
import java.util.List;

import org.mapstruct.AfterMapping;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import com.soccer.management.consts.Role;
import com.soccer.management.dto.UserDTO;
import com.soccer.management.model.User;

/**
 * @author enes.boyaci
 */
@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDTO toUserDTO(User user);

    User toUser(UserDTO userDTO);

    @IterableMapping(qualifiedByName = {"toUserDTO"})
    List<UserDTO> toUserDTO(Collection<User> user);

    @AfterMapping
    public default void setPassword(@MappingTarget UserDTO userDTO) {
        userDTO.setPassword(null);
    }

    @AfterMapping
    public default void setRole(@MappingTarget User user) {
        user.setRole(Role.USER.name());
    }

}
