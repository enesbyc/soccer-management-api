package com.soccer.management.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author enes.boyaci
 */
@Entity
@Table(name = "team")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, insertable = false, updatable = false)
    private User user;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "country", nullable = false, length = 50)
    private String country;

    @Column(name = "balance", nullable = false, precision = 22, scale = 0)
    private double balance;

    @Column(name = "player_value", nullable = false, precision = 22, scale = 0)
    private double playerValue;

    @OneToMany(fetch = FetchType.LAZY,
               mappedBy = "team",
               targetEntity = Player.class,
               cascade = CascadeType.REMOVE)
    private Set<Player> players = new HashSet<Player>(0);

    @OneToMany(fetch = FetchType.LAZY,
               mappedBy = "team",
               targetEntity = Transfer.class,
               cascade = CascadeType.REMOVE)
    private Set<Transfer> transfers = new HashSet<Transfer>(0);

}
