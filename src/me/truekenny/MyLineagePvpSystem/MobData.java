package me.truekenny.MyLineagePvpSystem;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

public class MobData {
    public long level = 1;

    private MyLineagePvpSystem plugin;

    /**
     * Вычисляет уровень существа, устанавливет имя
     *
     * @param entity
     */
    public MobData(LivingEntity entity, MyLineagePvpSystem plugin) {
        this.plugin = plugin;
        Location spawn = entity.getWorld().getSpawnLocation();
        Location mob = entity.getLocation();

        // System.out.println("Loc: " + entity.getWorld().getName());

        level = Math.round(Helper.betweenPoints(spawn, mob) / 100) + 1; // 100 блоков = 1 уровень

        if (entity.getWorld().getName().equalsIgnoreCase("world_nether")) {
            level += 25;
        }

        if (entity.getWorld().getName().equalsIgnoreCase("world_the_end")) {
            level += 50;
        }

        entity.setCustomName(getName(entity.getType()) + " " + level);
        entity.setCustomNameVisible(true);
    }

    /**
     * Устанавливает уровень
     *
     * @param level
     */
    public MobData(String level, MyLineagePvpSystem plugin) {
        this.plugin = plugin;
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

        entity.setCustomName(getName(entity.getType()) + " " + this.level);
    }

    private String getName(EntityType entityType) {

        String name = plugin.config.getString("rpg.name." + entityType.toString());
        if (name != null) {

            return name;
        }

        return plugin.config.getString("rpg.name.default");
    }
}
