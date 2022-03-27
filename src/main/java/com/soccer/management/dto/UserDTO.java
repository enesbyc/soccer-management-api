package com.soccer.management.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author enes.boyaci
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDTO {

    @NotNull(message = "Email cannot be empty!")
    @Email(message = "Email format is not suitable!")
    private String email;

    @NotNull(message = "Password cannot be empty!")
    @Size(min = 4, max = 15, message = "Password can be between 1 and 50 characters!")
    private String password;

}
