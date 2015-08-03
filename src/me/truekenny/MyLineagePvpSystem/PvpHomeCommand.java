package me.truekenny.MyLineagePvpSystem;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class PvpHomeCommand implements CommandExecutor {
    /**
     * Экземпляр плагина
     */
    private final MyLineagePvpSystem plugin;

    public PvpHomeCommand(MyLineagePvpSystem plugin) {
        this.plugin = plugin;
    }

    /**
     * Обрабатывает команду /pvphome
     *
     * @param sender  Отправитель команды
     * @param command ?
     * @param label   ?
     * @param split   Параметры команды
     * @return Результат
     */
    public boolean onCommand(CommandSender sender, Command command, String label, String[] split) {
        if (!(sender instanceof Player)) {
            return false;
        }

        Player player = (Player) sender;

        if (split.length == 1) {
            if (split[0].equalsIgnoreCase("set")) {

                if(!player.getLocation().getWorld().getName().equalsIgnoreCase("world")) {
                    player.sendMessage(ChatColor.RED + plugin.config.getString("local.home.bad.world"));

                    return true;
                }

                player.sendMessage(ChatColor.GREEN + plugin.config.getString("local.home.set"));
                plugin.players.getPlayerData(player).startHome(true, player);

                return true;
            }
            return false;
        }

        if (split.length == 0) {
            int[] home = plugin.players.getPlayerData(player).getHome();
            int[] defaultHome = {-1, -1, -1};

            if (Arrays.equals(home, defaultHome)) {
                player.sendMessage(ChatColor.RED + plugin.config.getString("local.home.error"));

                return true;
            }

            player.sendMessage(ChatColor.GREEN + plugin.config.getString("local.home.use"));
            plugin.players.getPlayerData(player).startHome(false, player);

            return true;
        }

        return false;
    }
}
