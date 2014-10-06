package me.truekenny.MyLineagePvpSystem;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;

public class RpgListener implements Listener {

    private final MyLineagePvpSystem plugin;

    public RpgListener(MyLineagePvpSystem plugin) {
        this.plugin = plugin;
    }

    /**
     * Выставляет уровень новому мобу
     *
     * @param event
     */
    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        // plugin.log("onCreatureSpawn: " + event.getEntity().getType().toString(), MyLineagePvpSystem.ANSI_GREEN);

        Mobs.getMobData(event.getEntity(), plugin);
    }

    /**
     * Определяет скорость горения мобов
     *
     * @param event
     */
    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        // plugin.log(event.getCause().toString(), plugin.ANSI_RED);

        if (event.getCause().toString().equalsIgnoreCase("FIRE_TICK")) {
            LivingEntity entity = getEntity(event.getEntity());
            if (!entity.getType().toString().equalsIgnoreCase("PLAYER")) {
                if (distanceToPlayer(entity) < 15) {
                    event.setDamage(event.getDamage() / Mobs.getMobData(entity, plugin).level);
                } else {
                }
            }

        }

    }

    /**
     * Обрабатывает урон
     *
     * @param event
     */
    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        plugin.log(event.getCause().toString(), plugin.ANSI_BLUE);


        LivingEntity entity = getEntity(event.getEntity());

        if (entity == null) {

            return;
        }

        LivingEntity damager = getEntity(event.getDamager());

        if (damager == null) {

            return;
        }

        long levelEntity = getLevel(entity);
        long levelDamager = getLevel(damager);


        plugin.log("onEntityDamage: " + damager.getType() + "(" + levelDamager + ") -(" + event.getDamage() + ")> " + entity.getType() + "(" + levelEntity + ")", MyLineagePvpSystem.ANSI_BLUE);

        event.setDamage(event.getDamage() * levelDamager / levelEntity);
    }

    /**
     * Обрабытывает смерть моба
     *
     * @param event
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDeath(EntityDeathEvent event) {

        LivingEntity livingEntity = event.getEntity();
        LivingEntity killer = livingEntity.getKiller();

        plugin.log("onEntityDeath: remove: " + livingEntity.getEntityId(), MyLineagePvpSystem.ANSI_RED);

        if (killer == null) {

            Mobs.remove(livingEntity.getEntityId());
            return;
        }

        long levelEntity = getLevel(livingEntity);
        long levelDamager = getLevel(killer);

        plugin.log("onEntityDeath: " + killer.getType() + "(" + levelDamager + ") -> " + livingEntity.getType() + "(" + levelEntity + ")", MyLineagePvpSystem.ANSI_RED);

        if (Math.abs(levelEntity - levelDamager) > 5) {
            event.setDroppedExp(0);
            plugin.log("onEntityDeath: noExp", MyLineagePvpSystem.ANSI_RED);
        }

        Mobs.remove(livingEntity.getEntityId());
    }

    /**
     * Получает игрока, наносивший урон
     *
     * @param entity
     * @return
     */
    private LivingEntity getEntity(Entity entity) {
        if (entity instanceof Projectile) {
            Projectile projectile = (Projectile) entity;
            return (LivingEntity) projectile.getShooter();
        }

        LivingEntity livingEntity = null;
        try {
            livingEntity = (LivingEntity) entity;
        } catch (Exception e) {
            plugin.log("getEntity: ERROR #1", MyLineagePvpSystem.ANSI_RED);
        }

        return livingEntity;
    }

    /**
     * Возвращает уровень существа
     *
     * @param entity
     * @return
     */
    private long getLevel(LivingEntity entity) {
        if (entity instanceof Player) {

            long level = ((Player) entity).getLevel();
            level = (level == 0) ? 1 : level;

            return level;
        }

        return Mobs.getMobData(entity, plugin).level;
    }

    /**
     * Вычисляет расстояние до ближайшего пользователя
     *
     * @param entity
     * @return
     */
    private double distanceToPlayer(LivingEntity entity) {
        double min = 100, _min;

        for (World world : Bukkit.getWorlds()) {
            for (Player player : world.getPlayers()) {
                _min = Helper.betweenPoints(entity.getLocation(), player.getLocation());
                if (_min < min) {
                    min = _min;
                }
            }
        }

        return min;
    }
}