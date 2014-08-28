package me.truekenny.MyLineagePvpSystem;

import org.bukkit.entity.Player;
import org.kitteh.tag.TagAPI;

import java.util.*;

public class Players {
    private Hashtable<String, PlayerData> playerDataHashtable = new Hashtable<String, PlayerData>();
    private MyLineagePvpSystem plugin;

    /**
     * Конструктор
     *
     * @param plugin
     */
    public Players(MyLineagePvpSystem plugin) {
        this.plugin = plugin;
    }

    /**
     * Получает информацию о игроке
     *
     * @param nick
     * @return
     */
    public PlayerData getPlayerData(String nick) {
        PlayerData playerData = playerDataHashtable.get(nick);
        if (playerData == null) {
            playerData = new PlayerData(this);
            playerDataHashtable.put(nick, playerData);
        }

        return playerData;
    }

    /**
     * Получает информацию о игроке
     *
     * @param player
     * @return
     */
    public PlayerData getPlayerData(Player player) {
        return getPlayerData(player.getName());
    }

    /**
     * Отправляет инф-ию о смене цвета
     */
    public void updateColor() {
        Enumeration<String> e = playerDataHashtable.keys();
        Player player;

        while (e.hasMoreElements()) {
            String nick = e.nextElement();
            PlayerData playerData = playerDataHashtable.get(nick);

            if (playerData.colorChanged()) {

                player = plugin.getServer().getPlayer(nick);

                if (player != null) {
                    TagAPI.refreshPlayer(player);
                }
            }
        }
    }

    /**
     * Деструктор
     */
    public void destroy() {
    }
}
