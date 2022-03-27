package com.soccer.management.dto;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
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
public class PlayerDTO {

    private long id;

    @NotNull(message = "First name cannot be empty!")
    @Size(min = 1, max = 50, message = "First name can be between 1 and 50 characters!")
    private String firstName;

    @NotNull(message = "Last name cannot be empty!")
    @Size(min = 1, max = 50, message = "Last name can be between 1 and 50 characters!")
    private String lastName;

    @NotNull(message = "Country cannot be empty!")
    @Size(min = 1, max = 50, message = "Country can be between 1 and 50 characters!")
    private String country;

    @NotNull(message = "Age cannot be empty!")
    @Max(value = 60, message = "The age value cannot be greater than 60!")
    @Min(value = 18, message = "The age value cannot be lower than 18!")
    private Integer age;

    @NotNull(message = "Type name cannot be empty!")
    @Max(value = 3, message = "The type value cannot be greater than 3!")
    @Min(value = 0, message = "The age value cannot be lower than 0!")
    private Integer type;

    @NotNull(message = "Market value cannot be empty!")
    @Min(value = 0, message = "The type value cannot be greater than 0!")
    private Double marketValue;

    private Long teamId;

}
