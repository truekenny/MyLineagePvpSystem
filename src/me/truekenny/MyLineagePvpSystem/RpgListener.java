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
import org.bukkit.event.player.PlayerMoveEvent;

public class RpgListener implements Listener {

    private final MyLineagePvpSystem plugin;
    int moveCount = 0;

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

                boolean protectedLVL = Mobs.getMobData(entity, plugin).level >= plugin.config.getInt("rpg.stillProtectMobsLVLOfFireDamage");
                if (protectedLVL || plugin.config.getBoolean("rpg.protectMobsOfFireDamage")) {

                    return;
                }

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
        // plugin.log(event.getCause().toString(), plugin.ANSI_BLUE);


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


        if (false) {
            plugin.log("onEntityDamage: " + damager.getType() + "(" + levelDamager + ") -(" + event.getDamage() + ")> " + entity.getType() + "(" + levelEntity + ")", MyLineagePvpSystem.ANSI_BLUE);
            plugin.log("onEntityDamage: d:" + levelDamager, MyLineagePvpSystem.ANSI_BLUE);
            plugin.log("onEntityDamage: e:" + levelEntity, MyLineagePvpSystem.ANSI_BLUE);
            plugin.log("onEntityDamage: ~:" + plugin.config.getDouble("rpg.difficulty"), MyLineagePvpSystem.ANSI_BLUE);
            plugin.log("onEntityDamage: =" + Math.pow(1.0 * levelDamager / levelEntity, plugin.config.getDouble("rpg.difficulty")), MyLineagePvpSystem.ANSI_BLUE);
        }

        event.setDamage(event.getDamage() * Math.pow(1.0 * levelDamager / levelEntity, plugin.config.getDouble("rpg.difficulty")));
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

        if (Math.abs(levelEntity - levelDamager) > plugin.config.getInt("rpg.levelDifferenceForExperience")) {
            event.setDroppedExp(0);
            plugin.log("onEntityDeath: noExp", MyLineagePvpSystem.ANSI_RED);
        } else {
            long levelBetween = Math.abs(levelDamager - levelEntity);
            if (levelBetween == 0) {
                levelBetween = 1;
            }
            int dropExp = (int) Math.round(event.getDroppedExp() * Math.pow(1.0 / levelBetween, plugin.config.getDouble("rpg.expDifficulty")));
            if (dropExp == 0) {
                dropExp = 1;
            }
            plugin.log("onEntityDeath: Exp: " + event.getDroppedExp() + " -> " + dropExp, MyLineagePvpSystem.ANSI_RED);
            event.setDroppedExp(dropExp);
        }

        Mobs.remove(livingEntity.getEntityId());
    }

    /**
     * Обрабатывает перемещение игрока и обновление имен мобов
     *
     * @param event
     */
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        // plugin.log("onPlayerMove: ", MyLineagePvpSystem.ANSI_RED);
        moveCount++;

        if (moveCount % 500 != 0) {
            return;
        }

        Player player = event.getPlayer();

        for (Entity entity : player.getNearbyEntities(player.getLocation().getBlockX(), player.getLocation().getBlockY(), player.getLocation().getBlockZ())) {
            if (entity instanceof LivingEntity) {
                Mobs.getMobData((LivingEntity) entity, plugin);
            }
        }
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

        // Не живое существо
        if (!(entity instanceof LivingEntity)) {

            return null;
        }

        return (LivingEntity) entity;
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
            if (level == 0) {

                return 1;
            }

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