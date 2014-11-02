package me.truekenny.MyLineagePvpSystem;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class PvpSoeCommand implements CommandExecutor {
    /**
     * Экземпляр плагина
     */
    private final MyLineagePvpSystem plugin;

    public PvpSoeCommand(MyLineagePvpSystem plugin) {
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

        player.sendMessage(ChatColor.GREEN + plugin.config.getString("local.soe.use"));
        plugin.players.getPlayerData(player).startSoe();

        return true;
    }
}
