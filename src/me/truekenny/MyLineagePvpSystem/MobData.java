package me.truekenny.MyLineagePvpSystem;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

public class MobData {
    public long level = 1;

    /**
     * Вычисляет уровень существа, устанавливет имя
     *
     * @param entity
     */
    public MobData(LivingEntity entity) {
        Location spawn = entity.getWorld().getSpawnLocation();
        Location mob = entity.getLocation();

        // System.out.println("Loc: " + entity.getWorld().getName());

        level = Math.round(Math.sqrt(
                Math.pow(spawn.getBlockX() - mob.getBlockX(), 2) +
                        Math.pow(spawn.getBlockY() - mob.getBlockY(), 2) +
                        Math.pow(spawn.getBlockZ() - mob.getBlockZ(), 2)

        ) / 100) + 1; // 100 блоков = 1 уровень

        if (entity.getWorld().getName().equalsIgnoreCase("world_nether")) {
            level += 25;
        }

        if (entity.getWorld().getName().equalsIgnoreCase("world_the_end")) {
            level += 50;
        }

        entity.setCustomName("Level " + level);
        entity.setCustomNameVisible(true);
    }

    /**
     * Устанавливает уровень
     *
     * @param level
     */
    public MobData(String level) {
        this.level = Long.parseLong(level);
    }

    /**
     * Устанвливает новый уровень
     *
     * @param entity
     * @param level
     */
    public void setLevel(LivingEntity entity, long level) {
        this.level = level;

        entity.setCustomName("Level " + this.level);
    }
}
