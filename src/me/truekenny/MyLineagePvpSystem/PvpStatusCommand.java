package me.truekenny.MyLineagePvpSystem;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PvpStatusCommand implements CommandExecutor {
    /**
     * Экземпляр плагина
     */
    private final MyLineagePvpSystem plugin;

    public PvpStatusCommand(MyLineagePvpSystem plugin) {
        this.plugin = plugin;
    }

    /**
     * Обрабатывает команду /pvpstatus
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

        plugin.players.sendStatusMessage(player);

        return true;
    }
}
