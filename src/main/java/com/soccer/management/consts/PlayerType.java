package com.soccer.management.consts;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author enes.boyaci
 */
@Getter
@AllArgsConstructor
public enum PlayerType {
                        GOALKEEPER(0), DEFENDER(1), MIDFIELDER(2), ATTACKER(3);

    private int type;
}
