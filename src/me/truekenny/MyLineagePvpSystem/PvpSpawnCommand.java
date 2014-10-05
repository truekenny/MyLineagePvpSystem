package me.truekenny.MyLineagePvpSystem;

import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class PvpSpawnCommand implements CommandExecutor {
    /**
     * Экземпляр плагина
     */
    private final MyLineagePvpSystem plugin;

    public PvpSpawnCommand(MyLineagePvpSystem plugin) {
        this.plugin = plugin;
    }

    /**
     * Обрабатывает команду /pvpspawn
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

        if (!player.isOp()) {
            return false;
        }

        if (split.length == 0) {
            return false;
        }

        String mobName = split[0];
        int level = 0;
        int count = 1;

        if (split.length >= 2) {
            level = Integer.parseInt(split[1]);
        }

        if (split.length >= 3) {
            count = Integer.parseInt(split[2]);
        }

        if (level < 0) {
            level = 1;
        }

        plugin.log(mobName + ", " + level, plugin.ANSI_RED);

        EntityType entityType;
        LivingEntity entity;
        World world = player.getWorld();

        for (int i = 1; i <= count; i++) {
            try {
                entityType = EntityType.valueOf(mobName.toUpperCase());
                entity = (LivingEntity) (world.spawnEntity(player.getLocation(), entityType));
            } catch (Exception e) {
                player.sendMessage("Only: CREEPER, SKELETON, SPIDER, GIANT, ZOMBIE, SLIME, GHAST, PIG_ZOMBIE, ENDERMAN, CAVE_SPIDER, SILVERFISH, BLAZE, MAGMA_CUBE, ENDER_DRAGON, WITHER, BAT, WITCH, PIG, SHEEP, COW, CHICKEN, SQUID, WOLF, MUSHROOM_COW, SNOWMAN, OCELOT, IRON_GOLEM, HORSE, VILLAGER");

                return false;
            }

            if (level != 0) {
                Mobs.getMobData(entity).setLevel(entity, level);
            }
        }

        return true;
    }
}
