package me.truekenny.MyLineagePvpSystem;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class PlayerData {
    private int karma = 0;
    private long unixTime = 0; // System.currentTimeMillis() / 1000L;
    private Players players;

    public PlayerData(Players players) {
        this.players = players;
    }

    public boolean murder(Player player) {
        int _karma = karma;
        if (players.getPlayerData(player).getColor().equals(ChatColor.WHITE)) {
            karma -= 10;

            if(_karma == 0 && karma < 0) return true;
        }

        return false;
    }

    public boolean died() {
        int _karma = karma;
        karma += 10;
        if (karma > 0) karma = 0;

        if(_karma < 0 && karma == 0) return true;

        return false;
    }

    public boolean cleansing() {
        int _karma = karma;

        karma += 1;
        if (karma > 0) karma = 0;

        if(_karma < 0 && karma == 0) return true;

        return false;
    }

    public boolean hit(Player player) {
        if (players.getPlayerData(player).getColor().equals(ChatColor.RED)) {

            return false;
        }

        // Был белым до удара
        boolean result = getColor().equals(ChatColor.WHITE);

        unixTime = System.currentTimeMillis() / 1000L;

        return result;
    }

    public ChatColor getColor() {
        if (karma < 0) {

            return ChatColor.RED;
        }

        if (System.currentTimeMillis() / 1000L < unixTime + 5) {

            return ChatColor.LIGHT_PURPLE;
        }


        if (System.currentTimeMillis() / 1000L < unixTime + 30) {

            return ChatColor.DARK_PURPLE;
        }

        return ChatColor.WHITE;
    }


}
