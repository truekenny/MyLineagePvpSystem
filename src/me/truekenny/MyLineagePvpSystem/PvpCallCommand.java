package me.truekenny.MyLineagePvpSystem;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PvpCallCommand implements CommandExecutor {
    /**
     * Экземпляр плагина
     */
    private final MyLineagePvpSystem plugin;

    public PvpCallCommand(MyLineagePvpSystem plugin) {
        this.plugin = plugin;
    }

    /**
     * Обрабатывает команду /pvpsoe
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

        Player target;
        if (split.length == 1) {
            if(player.getDisplayName().equalsIgnoreCase(split[0])) {
                player.sendMessage(ChatColor.RED + plugin.config.getString("local.call.use.self"));

                return true;
            }

            target = plugin.getServer().getPlayer(split[0]);

            if (target != null) {
                player.sendMessage(ChatColor.GREEN + plugin.config.getString("local.call.use.start").replaceFirst("_TARGET_", target.getDisplayName()));
                plugin.players.getPlayerData(player).startCall(target);

                return true;
            }

            return false;
        }

        if (split.length == 0) {
            Location location = plugin.players.getPlayerData(player).locationCaller;

            if(location != null) {
                player.sendMessage(ChatColor.GREEN + plugin.config.getString("local.call.use.accept"));
                plugin.players.getPlayerData(player).startCall(null);

                return true;
            }

            player.sendMessage(ChatColor.RED + plugin.config.getString("local.call.use.notarget"));

            return true;
        }

        return false;
    }
}
