package com.soccer.management.dto;

import java.util.Date;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

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
public class TransferDTO {

    private long id;

    @NotNull(message = "Player id cannot be empty!")
    @Min(value = 0, message = "Player id must be greater than 0!")
    private Long playerId;

    @NotNull(message = "Transfer amount cannot be empty!")
    @Min(value = 0, message = "Transfer amount must be greater than 0!")
    private Double transferAmount;

    private PlayerDTO player;

    private Date transferStartDate;

    private TeamDTO team;

}
