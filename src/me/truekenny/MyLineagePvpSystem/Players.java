package me.truekenny.MyLineagePvpSystem;

import org.bukkit.entity.Player;

import java.util.Hashtable;

public class Players {
    private Hashtable<String, PlayerData> playerDataHashtable = new Hashtable<String, PlayerData>();

    public void Players() {

    }

    public PlayerData getPlayerData(String nick) {
        PlayerData playerData = playerDataHashtable.get(nick);
        if(playerData == null) {
            playerData = new PlayerData(this);
            playerDataHashtable.put(nick, playerData);
        }

        return playerData;
    }

    public PlayerData getPlayerData(Player player) {
        return getPlayerData(player.getName());
    }

    public void destroy() {

    }
}
