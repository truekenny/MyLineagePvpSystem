package me.truekenny.MyLineagePvpSystem;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PlayerData {
    private int karma = 0;
    private long unixTime = 0; // System.currentTimeMillis() / 1000L;
    private Players players;
    private ChatColor lastColor = ChatColor.WHITE;
    private int pvp = 0;
    private int pk = 0;
    private int death = 0;

    private int[] home = {-1,-1,-1};

    private int soeTimeout = -1;
    private int callTimeout = -1;
    public Player target;
    public Location locationCaller;
    private int homeTimeout = -1;
    public boolean set = false; // setHome

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
        int delta = players.plugin.config.getInt("karma.kill.peace");

        int _karma = karma;
        if (players.getPlayerData(player).getColor().equals(ChatColor.WHITE)) {
            karma += delta;
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
        int delta = players.plugin.config.getInt("karma.kill.self");

        int _karma = karma;
        karma += delta;
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
        int delta = players.plugin.config.getInt("karma.kill.mob");
        int _karma = karma;

        karma += delta;
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
        int deltaTime = players.plugin.config.getInt("time.purple");

        if (karma < 0) {

            return ChatColor.RED;
        }

        if (System.currentTimeMillis() / 1000L < unixTime + deltaTime - 5) {

            return ChatColor.DARK_PURPLE;
        }


        if (System.currentTimeMillis() / 1000L < unixTime + deltaTime) {

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

    /**
     * Стартует Свиток возврата
     */
    public void startSoe(Player player) {
        soeTimeout = players.plugin.config.getInt("time.soe");
        player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, soeTimeout * 20, 1));
    }

    /**
     * Тик на использование скрола
     *
     * @return признак, что игрока надо портовать на спавн
     */
    public boolean tickSoe() {
        soeTimeout--;

        return soeTimeout == 0;
    }

    /**
     * Игрок в режиме возврата
     * @return boolean
     */
    public boolean inSoe() {
        if(soeTimeout <= 0) {

            return false;
        }

        soeTimeout = -1; // Вызывая метод, сбрасываю вызов игрока

        return true;

    }

    /**
     * Стартует Свиток призыва
     */
    public void startCall(Player target, Player player) {
        callTimeout = players.plugin.config.getInt("time.call");
        this.target = target;
        player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, callTimeout * 20, 1));
    }

    /**
     * Тик на использование скрола
     *
     * @return признак, что игрок завершил призыв
     */
    public boolean tickCall() {
        callTimeout--;

        return callTimeout == 0;
    }

    /**
     * Игрок в режиме вызова
     * @return boolean
     */
    public boolean inCall() {
        if(callTimeout <= 0) {

            return false;
        }

        callTimeout = -1; // Вызывая метод, сбрасываю вызов игрока

        return true;

    }

    /**
     * Стартует Свиток домой
     */
    public void startHome(boolean set, Player player) {
        homeTimeout = players.plugin.config.getInt("time.home");
        this.set = set;

        player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, homeTimeout * 20, 1));
    }

    /**
     * Тик на использование скрола
     *
     * @return признак, что игрок завершил призыв
     */
    public boolean tickHome() {
        homeTimeout--;

        return homeTimeout == 0;
    }

    /**
     * Игрок в режиме домой
     * @return boolean
     */
    public boolean inHome() {
        if(homeTimeout <= 0) {

            return false;
        }

        homeTimeout = -1; // Вызывая метод, сбрасываю вызов игрока

        return true;

    }

    public int getPk() {
        return pk;
    }

    public void setPk(int pk) {
        this.pk = pk;
    }

    public int getPvp() {
        return pvp;
    }

    public void setPvp(int pvp) {
        this.pvp = pvp;
    }

    public int getKarma() {
        return karma;
    }

    public void setKarma(int karma) {
        this.karma = karma;
    }

    public int getDeath() {
        return death;
    }

    public void setDeath(int death) {
        this.death = death;
    }

    public int[] getHome() {
        return home;
    }

    public void setHome(int[] home) {
        this.home = home;
    }
}
