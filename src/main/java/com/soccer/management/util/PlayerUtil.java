package com.soccer.management.util;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.json.Json;
import javax.json.JsonReader;
import javax.json.JsonStructure;

import org.json.JSONArray;

import com.soccer.management.consts.SoccerConst;
import com.soccer.management.model.Player;

/**
 * @author enes.boyaci
 */
public class PlayerUtil {

    public static List<String> playerFirstNames = new ArrayList<String>();
    public static List<String> playerLastNames = new ArrayList<String>();

    private static int maxAge = 40;
    private static int minAge = 18;

    public static Player generatePlayer(long teamId, int type) {

        Random rnd = new Random();
        String firstName = playerFirstNames.get(rnd.nextInt(playerFirstNames.size() - 1));
        String lastName = playerLastNames.get(rnd.nextInt(playerLastNames.size() - 1));
        int age = (int) (rnd.nextDouble() * (maxAge - minAge + 1) + minAge);
        Player player = Player.builder().firstName(firstName).lastName(lastName)
                        .country(SoccerConst.defaultCountry)
                        .marketValue(SoccerConst.singlePlayerInitialBalance).age(age).type(type)
                        .teamId(teamId).build();
        return player;
    }

    public static void initializePlayerValues() {
        initializePlayerFirstNames();
        initializePlayerLastNames();
    }

    public static void initializePlayerFirstNames() {
        try {
            JsonReader reader = Json.createReader(new FileReader(
                            new File("src/main/resources/static/first-names.json")));
            JsonStructure jsonst = reader.read();
            JSONArray arr = new JSONArray(jsonst.toString());
            for (int i = 0; i < arr.length(); i++) {
                playerFirstNames.add(arr.get(i).toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void initializePlayerLastNames() {
        try {
            JsonReader reader = Json.createReader(new FileReader(
                            new File("src/main/resources/static/middle-names.json")));
            JsonStructure jsonst = reader.read();
            JSONArray arr = new JSONArray(jsonst.toString());
            for (int i = 0; i < arr.length(); i++) {
                playerLastNames.add(arr.get(i).toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
