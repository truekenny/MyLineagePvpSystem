package me.truekenny.MyLineagePvpSystem;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.kitteh.tag.TagAPI;

import java.util.Enumeration;
import java.util.Hashtable;

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

                    sendColorMessage(playerData.getColor(), player);
                }
            }
        }
    }

    /**
     * Отправляет сообщение о смене режима
     *
     * @param chatColor
     * @param player
     */
    public void sendColorMessage(ChatColor chatColor, Player player) {
        if (chatColor.equals(ChatColor.WHITE)) {
            player.sendMessage(ChatColor.GREEN + "Вы перешли в Мирный режим");
        }
        if (chatColor.equals(ChatColor.DARK_PURPLE)) {
            player.sendMessage(ChatColor.LIGHT_PURPLE + "Вы перешли в режим PVP");
        }
        if (chatColor.equals(ChatColor.RED)) {
            player.sendMessage(ChatColor.RED + "Вы стали Убийцей");
        }
    }

    /**
     * Отправлляет общую информацию
     *
     * @param nick
     */
    public void sendStatusMessage(Player player) {
        PlayerData playerData = getPlayerData(player);

        player.sendMessage(ChatColor.GOLD + "PK: " + playerData.getPk());
        player.sendMessage(ChatColor.GOLD + "PVP: " + playerData.getPvp());
        player.sendMessage(ChatColor.GOLD + "Karma: " + playerData.getKarma());
        player.sendMessage(ChatColor.GOLD + "Смертей: " + playerData.getDeath() + " (от других игроков)");

        ChatColor chatColor = playerData.getColor();
        if (chatColor.equals(ChatColor.WHITE)) {
            player.sendMessage(ChatColor.GREEN + "Вы в Мирном режиме");
        }
        if (chatColor.equals(ChatColor.DARK_PURPLE) || chatColor.equals(ChatColor.LIGHT_PURPLE)) {
            player.sendMessage(ChatColor.LIGHT_PURPLE + "Вы в режиме PVP");
        }
        if (chatColor.equals(ChatColor.RED)) {
            player.sendMessage(ChatColor.RED + "Вы Убийца");
        }
    }

    /**
     * Деструктор
     */
    public void destroy() {
    }
}
