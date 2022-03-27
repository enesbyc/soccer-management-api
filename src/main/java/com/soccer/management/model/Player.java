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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author enes.boyaci
 */
@Entity
@Table(name = "player")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false, insertable = false, updatable = false)
    private Team team;

    @Column(name = "team_id", nullable = false)
    private Long teamId;

    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    @Column(name = "country", nullable = false, length = 50)
    private String country;

    @Column(name = "age", nullable = false)
    private int age;

    @Column(name = "type", nullable = false)
    private int type;

    @Column(name = "market_value", nullable = false, precision = 22, scale = 0)
    private double marketValue;

    @OneToMany(fetch = FetchType.LAZY,
               mappedBy = "player",
               targetEntity = Transfer.class,
               cascade = CascadeType.REMOVE)
    private Set<Transfer> transfers = new HashSet<Transfer>(0);

}
