package com.soccer.management.dto;

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
public class TeamDTO {

    private long id;

    @NotNull(message = "Name cannot be empty!")
    @Size(min = 1, max = 50, message = "Name can be between 1 and 50 characters!")
    private String name;

    @NotNull(message = "Country cannot be empty!")
    @Size(min = 1, max = 50, message = "Country can be between 1 and 50 characters!")
    private String country;

    private double balance;

    private double playerValue;

    private long userId;

}
