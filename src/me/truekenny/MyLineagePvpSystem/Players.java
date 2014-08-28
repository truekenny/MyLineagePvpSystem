package me.truekenny.MyLineagePvpSystem;

import org.bukkit.entity.Player;
import org.kitteh.tag.TagAPI;

import java.util.*;

public class Players {
    private Hashtable<String, PlayerData> playerDataHashtable = new Hashtable<String, PlayerData>();
    private MyLineagePvpSystem plugin;
    private ColorThread colorThread;
    Set<String> nicksToUpdate = new HashSet<String>();

    /**
     * Конструктор
     *
     * @param plugin
     */
    public Players(MyLineagePvpSystem plugin) {
        this.plugin = plugin;
        colorThread = new ColorThread(this, "THREAD" + String.valueOf(System.currentTimeMillis()));
        colorThread.start();
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
    public void lookingForUpdateColor() {
        Enumeration<String> e = playerDataHashtable.keys();
        while (e.hasMoreElements()) {
            String nick = e.nextElement();
            PlayerData playerData = playerDataHashtable.get(nick);

            if (playerData.colorChanged()) {
                if(!nicksToUpdate.contains(nick)) {
                    plugin.log("lookingForUpdateColor: add: " + nick);
                    nicksToUpdate.add(nick);
                }
            }
        }
    }

    /**
     * Обновляет цвета игроков
     */
    public void updateNicks() {
        if(nicksToUpdate.size() == 0) {

            return;
        }

        Player player;

        for(String nick: nicksToUpdate) {
            plugin.log("lookingForUpdateColor: remove: " + nick);
            nicksToUpdate.remove(nick);

            player = plugin.getServer().getPlayer(nick);

            if (player != null) {
                TagAPI.refreshPlayer(player);
            }
        }

        nicksToUpdate.clear();
    }

    /**
     * Деструктор
     */
    public void destroy() {
        colorThread.setDestroyTrue();
    }
}
