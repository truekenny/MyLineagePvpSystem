package me.truekenny.MyLineagePvpSystem;

import com.google.common.collect.Lists;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;


public class ColorListener implements Listener {
    /**
     * Экземпляр главного класса плагина
     */
    private final MyLineagePvpSystem plugin;

    private Scoreboard scoreboard;
    private ArrayList<Team> teams;

    // For selecting random teams
    private Random rnd = new Random();

    public ColorListener(MyLineagePvpSystem instance) {
        plugin = instance;

        scoreboard = plugin.getServer().getScoreboardManager().getMainScoreboard();
        plugin.log("team count 1=" + scoreboard.getTeams().size());

        resetScoreboard();

        plugin.log("team count 2=" + scoreboard.getTeams().size());


        initializeTeam("RED", ChatColor.RED.toString() + "☠ ", " ☠");
        initializeTeam("DARK_PURPLE", ChatColor.DARK_PURPLE.toString() + "⚔ ", " ⚔");
        initializeTeam("LIGHT_PURPLE", ChatColor.LIGHT_PURPLE.toString(), "");
        initializeTeam("WHITE", ChatColor.WHITE.toString(), "");
        initializeTeam("OP", ChatColor.GREEN.toString(), "");
        teams = Lists.newArrayList(scoreboard.getTeams());

        updatePlayers();

        plugin.log("teams.size = " + teams.size());

        plugin.log("ColorListener has been enabled!");
    }

    /**
     * Очищает scoreboard
     */
    private void resetScoreboard() {
        for (Team team : scoreboard.getTeams()) {
            scoreboard.getTeam(team.getName()).unregister();
        }
    }

    /**
     * Инициализация группы
     *
     * @param teamName
     * @param prefix
     */
    private void initializeTeam(String teamName, String prefix, String suffix) {
        plugin.log("initializeTeam " + teamName + " = " + prefix + ".");
        if (scoreboard.getTeam(teamName) == null) {
            plugin.log("initializeTeam new:" + teamName + " = " + prefix + ".");
            Team team = scoreboard.registerNewTeam(teamName);
            team.setPrefix(prefix);
            team.setSuffix(suffix);

        }
    }

    /**
     * Устанавливает игроку новый цвет
     *
     * @param player
     * @param chatColor
     */
    public void setPlayerTeam(Player player, ChatColor chatColor) {
        plugin.log("New color: " + player.getName() + " > " + chatColor.name());

        if (chatColor == ChatColor.RED) {
            scoreboard.getTeam("RED").addPlayer(player);

            return;
        }
        if (chatColor == ChatColor.LIGHT_PURPLE) {
            scoreboard.getTeam("LIGHT_PURPLE").addPlayer(player);

            return;
        }
        if (chatColor == ChatColor.DARK_PURPLE) {
            scoreboard.getTeam("DARK_PURPLE").addPlayer(player);

            return;
        }

        if(player.isOp()) {
            scoreboard.getTeam("OP").addPlayer(player);

            return;
        }
        scoreboard.getTeam("WHITE").addPlayer(player);
    }

    /**
     * Обновить группу игрока
     *
     * @param player
     */
    public void updateColor(Player player) {
        setPlayerTeam(player, plugin.players.getPlayerData(player).getColor());
    }

    /**
     * При входе в игру устанавливаем цвет игрока
     *
     * @param e
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        updateColor(e.getPlayer());
    }

    /**
     * Обновить всех игроков
     */
    public void updatePlayers() {
        for (Player player : getOnlinePlayers()) {
            updateColor(player);
        }
    }

    /**
     * @return Возвращает список игроков
     */
    public List<Player> getOnlinePlayers() {
        List<Player> list = Lists.newArrayList();
        for (World world : Bukkit.getWorlds()) {
            list.addAll(world.getPlayers());
        }

        return Collections.unmodifiableList(list);
    }
}

