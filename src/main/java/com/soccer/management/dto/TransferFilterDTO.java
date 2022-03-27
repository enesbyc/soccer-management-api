package com.soccer.management.dto;

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
public class TransferFilterDTO {

    private String country;
    private String playerName;
    private String playerValue;
    private String teamName;

}
