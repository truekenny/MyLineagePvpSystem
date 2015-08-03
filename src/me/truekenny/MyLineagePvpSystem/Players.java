package me.truekenny.MyLineagePvpSystem;

import org.bukkit.*;
import org.bukkit.entity.Player;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

//import org.kitteh.tag.TagAPI;

public class Players {
    final public String FILENAME = "plugins/MyLineagePvpSystem/pvpplayers.data";
    public MyLineagePvpSystem plugin;
    private Hashtable<String, PlayerData> playerDataHashtable = new Hashtable<String, PlayerData>();
    private Location spawn;

    /**
     * Конструктор
     *
     * @param plugin
     */
    public Players(MyLineagePvpSystem plugin) {
        this.plugin = plugin;

        calcSpawn();
        load();
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
                    //TagAPI.refreshPlayer(player);
                    plugin.colorListener.updateColor(player);

                    sendColorMessage(playerData.getColor(), player);
                }
            }
        }
    }

    /**
     * Проверяет игроков, которые используют свиток возврата
     */
    public void updateTick() {
        Enumeration<String> e = playerDataHashtable.keys();
        Player player;

        if(spawn == null) {

            return;
        }

        while (e.hasMoreElements()) {
            String nick = e.nextElement();
            player = plugin.getServer().getPlayer(nick);

            if (player != null) {
                PlayerData playerData = playerDataHashtable.get(nick);

                if (playerData.tickSoe()) {
                    player.teleport(spawn);
                    player.playSound(player.getLocation(), Sound.ENDERMAN_TELEPORT, 1, 1);
                }

                if (playerData.tickCall()) {
                    if(playerData.target != null) {
                        player.sendMessage(ChatColor.GREEN + plugin.config.getString("local.call.use.finish.player"));
                        playerData.target.sendMessage(ChatColor.GREEN + plugin.config.getString("local.call.use.finish.target").replaceFirst("_PLAYER_", player.getDisplayName()));

                        PlayerData targetData = this.getPlayerData(playerData.target);
                        targetData.locationCaller = player.getLocation();

                        playerData.target = null;
                        player.playSound(player.getLocation(), Sound.ENDERMAN_TELEPORT, 1, 1);
                    }
                    else if(playerData.locationCaller != null) {
                        player.sendMessage(ChatColor.GREEN + plugin.config.getString("local.call.use.finish.player"));

                        player.teleport(playerData.locationCaller);
                        playerData.locationCaller = null;

                        player.playSound(player.getLocation(), Sound.ENDERMAN_TELEPORT, 1, 1);
                    }
                }

                if (playerData.tickHome()) {
                    if(playerData.set) {
                        int[] home = {player.getLocation().getBlockX(), player.getLocation().getBlockY(), player.getLocation().getBlockZ()};
                        playerData.setHome(home);
                        player.sendMessage(ChatColor.GREEN + plugin.config.getString("local.home.finish"));
                    }
                    else {
                        if(spawn == null) {

                            return;
                        }
                        int[] home = playerData.getHome();
                        Location location = new Location(plugin.getServer().getWorld("world"),
                                home[0] + 0.5 + Helper.rand(-2, 2),
                                home[1],
                                home[2] + 0.5 + + Helper.rand(-2, 2));
                        player.teleport(location);
                    }
                    player.playSound(player.getLocation(), Sound.ENDERMAN_TELEPORT, 1, 1);
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
            player.sendMessage(ChatColor.GREEN + plugin.config.getString("local.statusPeace"));
        }
        if (chatColor.equals(ChatColor.DARK_PURPLE)) {
            player.sendMessage(ChatColor.LIGHT_PURPLE + plugin.config.getString("local.statusPVP"));
        }
        if (chatColor.equals(ChatColor.RED)) {
            player.sendMessage(ChatColor.RED + plugin.config.getString("local.statusPK"));
            plugin.playerListener.setKillerEffects(player);
        }
    }

    /**
     * Отправлляет общую информацию
     *
     * @param player
     */
    public void sendStatusMessage(Player player) {
        PlayerData playerData = getPlayerData(player);

        player.sendMessage(ChatColor.GOLD + plugin.config.getString("local.statisticPK") + ": " + playerData.getPk());
        player.sendMessage(ChatColor.GOLD + plugin.config.getString("local.statisticPVP") + ": " + playerData.getPvp());
        player.sendMessage(ChatColor.GOLD + plugin.config.getString("local.statisticKarma") + ": " + playerData.getKarma());
        player.sendMessage(ChatColor.GOLD + plugin.config.getString("local.statisticDeaths") + ": " + playerData.getDeath() + " " + plugin.config.getString("local.statisticDeathsMore"));

        ChatColor chatColor = playerData.getColor();
        if (chatColor.equals(ChatColor.WHITE)) {
            player.sendMessage(ChatColor.GREEN + plugin.config.getString("local.statisticModePeace"));
        }
        if (chatColor.equals(ChatColor.DARK_PURPLE) || chatColor.equals(ChatColor.LIGHT_PURPLE)) {
            player.sendMessage(ChatColor.LIGHT_PURPLE + plugin.config.getString("local.statisticModePVP"));
        }
        if (chatColor.equals(ChatColor.RED)) {
            player.sendMessage(ChatColor.RED + plugin.config.getString("local.statisticModePK"));
        }
    }

    /**
     * Деструктор
     */
    public void destroy() {
        save();
    }

    /**
     * Загрузка данных
     *
     * @return
     */
    private boolean load() {
        BufferedReader br = null;

        try {

            String sCurrentLine;

            br = new BufferedReader(new FileReader(FILENAME));

            while ((sCurrentLine = br.readLine()) != null) {
                // plugin.log(sCurrentLine, plugin.ANSI_BLUE);

                StringTokenizer st = new StringTokenizer(sCurrentLine);
                String nick = st.nextToken();
                String pk = st.nextToken();
                String pvp = st.nextToken();
                String karma = st.nextToken();
                String death = st.nextToken();

                String x = "-1";
                String y = "-1";
                String z = "-1";

                if(st.hasMoreTokens()) {
                    x = st.nextToken();
                    y = st.nextToken();
                    z = st.nextToken();
                }

                PlayerData playerData = new PlayerData(this);
                playerData.setPk(Integer.parseInt(pk));
                playerData.setPvp(Integer.parseInt(pvp));
                playerData.setKarma(Integer.parseInt(karma));
                playerData.setDeath(Integer.parseInt(death));

                int[] home = {Integer.parseInt(x), Integer.parseInt(y), Integer.parseInt(z)};
                playerData.setHome(home);
                playerDataHashtable.put(nick, playerData);

            }

        } catch (IOException e) {
            plugin.log("File " + FILENAME + " not found");
        } finally {
            try {
                if (br != null) br.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        return true;
    }

    /**
     * Сохранение данных
     *
     * @return
     */
    private boolean save() {
        Enumeration<String> e = playerDataHashtable.keys();
        PrintWriter out;
        try {
            out = new PrintWriter(FILENAME);
        } catch (Exception ex) {
            return false;
        }

        int[] defaultHome = {-1,-1,-1};

        while (e.hasMoreElements()) {
            String nick = e.nextElement();
            PlayerData playerData = playerDataHashtable.get(nick);

            int[] home = playerData.getHome();

            if (
                    playerData.getPk() == 0
                    && playerData.getPvp() == 0
                    && playerData.getKarma() == 0
                    && playerData.getDeath() == 0
                    && Arrays.equals(home, defaultHome)
            ) {
                continue;
            }

            out.printf("%s %d %d %d %d %d %d %d\n", nick, playerData.getPk(), playerData.getPvp(), playerData.getKarma(), playerData.getDeath(), home[0], home[1], home[2]);
        }

        out.close();

        return true;
    }

    /**
     * Устанавливает локацию для SOE
     */
    private void calcSpawn() {
        World world = plugin.getServer().getWorld("world");

        if (world == null) {
            plugin.log("World «world» not found (/pvpsoe not work).");

            return;
        }

        Location location = world.getSpawnLocation();
        Location location1 = location.clone();
        for (int y = 0; y <= 10; y++) {

            location1 = location.clone();
            location.setY(location.getY() + 1);

            plugin.log("Block1: " + location1.getBlock().toString());
            plugin.log("Block: " + location.getBlock().toString());

            if (location.getBlock().getType() == Material.AIR && location1.getBlock().getType() == Material.AIR) {
                break;
            }

        }

        spawn = location1;
    }

    /**
     * Возвращает спавн
     * @return
     */
    public Location getSpawn() {
        return spawn;
    }
}
