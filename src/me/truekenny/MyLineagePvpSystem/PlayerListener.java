package me.truekenny.MyLineagePvpSystem;

import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class PlayerListener implements Listener {
    /**
     * Экземпляр главного класса плагина
     */
    private final MyLineagePvpSystem plugin;

    public PlayerListener(MyLineagePvpSystem instance) {
        plugin = instance;
        plugin.log("PlayerListener has been enabled!");
    }

    /**
     * Обрабатывает событие Смерть игрока
     *
     * @param event
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerDeath(final PlayerDeathEvent event) {
        final Player player = event.getEntity();
        Player killer = player.getKiller();

        plugin.log("onPlayerDeath: entity: " + player.getName());

        if (killer != null && killer.getType().toString().equalsIgnoreCase("player")) {
            plugin.log("onPlayerDeath: killer: " + ((Player) killer).getName());

            murder(player, killer);
        }

        died(player);
    }

    /**
     * Обрабатывает смерить игрока
     *
     * @param player
     */
    private void died(Player player) {
        // Уменьшить карму
        plugin.log("died: " + player.getName() + " умер");
    }

    /**
     * Обрабатывает убийство игрока
     *
     * @param player
     * @param killer
     */
    private void murder(Player player, Player killer) {
        //
        plugin.log("murder: " + killer.getName() + " убил " + player.getName());
    }

    private static final Set<String> types = new HashSet<String>(Arrays.asList(
            new String[]{"SHEEP", "COW", "ZOMBIE", "SKELETON"}
    ));

    /**
     * Обрабатывает событие Смерть моба
     *
     * @param event
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDeath(EntityDeathEvent event) {


        LivingEntity livingEntity = event.getEntity();
        plugin.log("onEntityDeath: entity: " + livingEntity.getType().toString());

        if (!types.contains(livingEntity.getType().toString())) {
            plugin.log("onEntityDeath: killer: Плохой тип");

            return;
        }

        Player killer = livingEntity.getKiller();

        if (killer != null && killer.getType().toString().equalsIgnoreCase("player")) {
            plugin.log("onEntityDeath: killer: " + ((Player) killer).getName());

            cleansing(killer);
        }
    }

    /**
     * Обрабытывает убийство моба
     *
     * @param player
     */
    private void cleansing(Player player) {
        plugin.log("cleansing: " + player.getName() + " чистит карму");
    }

    /**
     * Обрабатывает урон, полученный игроком
     *
     * @param entityDamageByEntityEvent
     */
    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent entityDamageByEntityEvent) {
        Entity entity = entityDamageByEntityEvent.getEntity();
        plugin.log("onEntityDamage: type: " + (entity == null ? "null" : entity.getType().toString()));

        if (entity != null && entity.getType().toString().equalsIgnoreCase("player")) {
            plugin.log("onEntityDamage: entity: " + ((Player) entity).getName());
        }

        Player damager = getDamager(entityDamageByEntityEvent);

        if (damager != null) {
            plugin.log("onEntityDamage: damager: " + damager.getName());
        }

        if (entity != null && entity.getType().toString().equalsIgnoreCase("player") && damager != null) {
            hit((Player) entity, damager);
        }
    }

    /**
     * Получает игрока, наносивший урон
     *
     * @param entityDamageByEntityEvent
     * @return
     */
    private Player getDamager(EntityDamageByEntityEvent entityDamageByEntityEvent) {

        plugin.log("getDamager: " + entityDamageByEntityEvent.getDamager().getType().toString());
        plugin.log("getDamager: " + entityDamageByEntityEvent.getDamager().getClass().toString());

        if (entityDamageByEntityEvent.getDamager() instanceof Player) {
            return (Player) entityDamageByEntityEvent.getDamager();
        }
        if (entityDamageByEntityEvent.getDamager() instanceof Projectile) {
            Projectile projectile = (Projectile) entityDamageByEntityEvent.getDamager();
            if (projectile.getShooter() instanceof Player) {
                return (Player) projectile.getShooter();
            }
        }

        return null;
    }

    /**
     * Игрок ударяет другого игрока
     *
     * @param player
     * @param damager
     */
    private void hit(Player player, Player damager) {
        plugin.log("hit: " + damager.getName() + " ударил " + player.getName());
    }
}

