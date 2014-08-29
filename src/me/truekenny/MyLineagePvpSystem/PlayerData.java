package me.truekenny.MyLineagePvpSystem;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class PlayerData {
    private int karma = 0;
    private long unixTime = 0; // System.currentTimeMillis() / 1000L;
    private Players players;
    private ChatColor lastColor = ChatColor.WHITE;
    private int pvp = 0;
    private int pk = 0;
    private int death = 0;

    public PlayerData(Players players) {
        this.players = players;
    }

    /**
     * Убивает player
     *
     * @param player
     * @return
     */
    public boolean murder(Player player) {
        int _karma = karma;
        if (players.getPlayerData(player).getColor().equals(ChatColor.WHITE)) {
            karma -= 10;
            pk++;

            if (_karma == 0 && karma < 0) return true;
        }

        pvp++;
        return false;
    }

    /**
     * Умирает
     *
     * @return
     */
    public boolean died() {
        int _karma = karma;
        karma += 10;
        if (karma > 0) karma = 0;

        death++;
        if (_karma < 0 && karma == 0) return true;

        return false;
    }

    /**
     * Убивает моба
     *
     * @return
     */
    public boolean cleansing() {
        int _karma = karma;

        karma += 1;
        if (karma > 0) karma = 0;

        if (_karma < 0 && karma == 0) return true;

        return false;
    }

    /**
     * Ударяет игрока player
     *
     * @param player
     * @return
     */
    public boolean hit(Player player) {
        if (players.getPlayerData(player).getColor().equals(ChatColor.RED)) {

            return false;
        }

        // Был белым до удара
        boolean result = getColor().equals(ChatColor.WHITE);

        unixTime = System.currentTimeMillis() / 1000L;

        return result;
    }

    /**
     * Возвращает цвет игрока
     *
     * @return
     */
    public ChatColor getColor() {
        if (karma < 0) {

            return ChatColor.RED;
        }

        if (System.currentTimeMillis() / 1000L < unixTime + 25) {

            return ChatColor.DARK_PURPLE;
        }


        if (System.currentTimeMillis() / 1000L < unixTime + 30) {

            return ChatColor.LIGHT_PURPLE;
        }

        return ChatColor.WHITE;
    }

    /**
     * Возвращает статус того, что цвет был изменён
     *
     * @return
     */
    public boolean colorChanged() {
        ChatColor _lastColor = getColor();

        if (_lastColor.equals(lastColor)) {

            return false;
        }

        lastColor = _lastColor;

        return true;
    }

    public int getPk() {
        return pk;
    }

    public int getPvp() {
        return pvp;
    }

    public int getKarma() {
        return karma;
    }

    public int getDeath() {
        return death;
    }

    public void setPk(int pk) {
        this.pk = pk;
    }

    public void setPvp(int pvp) {
        this.pvp = pvp;
    }

    public void setKarma(int karma) {
        this.karma = karma;
    }

    public void setDeath(int death) {
        this.death = death;
    }
}
