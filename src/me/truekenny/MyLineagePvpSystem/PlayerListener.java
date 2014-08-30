package me.truekenny.MyLineagePvpSystem;

import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.kitteh.tag.AsyncPlayerReceiveNameTagEvent;
import org.kitteh.tag.TagAPI;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class PlayerListener implements Listener {
    private static final Set<String> types = new HashSet<String>(Arrays.asList(
            new String[]{"SHEEP", "COW", "ZOMBIE", "SKELETON"}
    ));
    final private int potionDuration = 20 * 30 * 24 * 60 * 60;
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

            murder(event, player, killer);
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
        plugin.log("died: " + player.getName() + " умер", plugin.ANSI_PURPLE);
        if (plugin.players.getPlayerData(player).died()) {
            TagAPI.refreshPlayer(player);
        }
        setKillerEffects(player);
    }

    /**
     * Обрабатывает убийство игрока
     *
     * @param event
     * @param player
     * @param killer
     */
    private void murder(final PlayerDeathEvent event, final Player player, Player killer) {
        if (plugin.players.getPlayerData(player).getColor().equals(ChatColor.WHITE)) {
            // Сохранить ЭКСП
            event.setKeepLevel(true);
            event.setDroppedExp(0);

            // Сохранить броню
            final ItemStack[] armor = player.getInventory().getArmorContents();
            plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                @Override
                public void run() {
                    player.getInventory().setArmorContents(armor);
                }

            });
            for (ItemStack is : armor) {
                event.getDrops().remove(is);
            }

            // Сохранить броню
            final ItemStack[] inventory = player.getInventory().getContents();
            for (int i = 0; i < inventory.length; i++) {
                ItemStack is = inventory[i];

                if (is != null && Math.random() < 0.95)
                    event.getDrops().remove(is);
                else
                    inventory[i] = null;
            }
            plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {

                @Override
                public void run() {
                    player.getInventory().setContents(inventory);
                }

            });
        }

        //
        plugin.log("murder: " + killer.getName() + " убил " + player.getName(), plugin.ANSI_RED);
        if (plugin.players.getPlayerData(killer).murder(player)) {
            TagAPI.refreshPlayer(killer);
            setKillerEffects(killer);
        }
    }

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
        plugin.log("cleansing: " + player.getName() + " чистит карму", plugin.ANSI_GREEN);
        if (plugin.players.getPlayerData(player).cleansing()) {
            TagAPI.refreshPlayer(player);
            removeKillerEffects(player);
        }
    }

    /**
     * Обрабатывает урон, полученный игроком
     *
     * @param event
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (event.isCancelled()) {
            plugin.log("onEntityDamage: isCancelled", plugin.ANSI_BLUE);

            return;
        }

        Entity entity = event.getEntity();

        if (entity == null) {
            plugin.log("onEntityDamage: entity is null", plugin.ANSI_BLUE);

            return;
        }

        Player damager = getDamager(event);

        if (damager == null) {
            // plugin.log("onEntityDamage: damager is null", plugin.ANSI_BLUE);

            return;
        }

        if (entity.getType().toString().equalsIgnoreCase("player")) {
            hit((Player) entity, damager);
        }
    }

    /**
     * Получает игрока, наносивший урон
     *
     * @param event
     * @return
     */
    private Player getDamager(EntityDamageByEntityEvent event) {

        plugin.log("getDamager: " + event.getDamager().getType().toString());
        plugin.log("getDamager: " + event.getDamager().getClass().toString());

        if (event.getDamager() instanceof Player) {
            return (Player) event.getDamager();
        }
        if (event.getDamager() instanceof Projectile) {
            Projectile projectile = (Projectile) event.getDamager();
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
        plugin.log("hit: " + damager.getName() + " ударил " + player.getName(), plugin.ANSI_YELLOW);

        if (plugin.players.getPlayerData(damager).hit(player)) {
            TagAPI.refreshPlayer(damager);
        }
    }

    /**
     * Отдать цвет имени пользователя
     *
     * @param event
     */
    @EventHandler
    public void onNameTag(AsyncPlayerReceiveNameTagEvent event) {
        Player player = event.getNamedPlayer();
        event.setTag(getPlayerColor(player) + player.getName());
        // plugin.log("onNameTag: " + player.getName());
    }

    /**
     * Возвращает цвет ника пользователя
     *
     * @param player
     * @return
     */
    private ChatColor getPlayerColor(Player player) {
        return plugin.players.getPlayerData(player).getColor();
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        //plugin.log("onPlayerMove: ");
        //plugin.players.updateNicks();
    }

    /**
     * Устанавливает на убийцу эффекты
     *
     * @param player
     */
    public void setKillerEffects(Player player) {
        if (plugin.players.getPlayerData(player).getColor().equals(ChatColor.RED)) {
            plugin.log("setKillerEffects: " + player.getName(), plugin.ANSI_RED);
            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, potionDuration, 1));
            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, potionDuration, 1));
            player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, potionDuration, 1));
        }
    }

    /**
     * Удалить эффекты с убийцы
     *
     * @param player
     */
    private void removeKillerEffects(Player player) {
        player.removePotionEffect(PotionEffectType.SLOW);
        player.removePotionEffect(PotionEffectType.SLOW_DIGGING);
        player.removePotionEffect(PotionEffectType.WEAKNESS);
    }

    /**
     * Обрабатывает воскрешение игрока
     *
     * @param event
     */
    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        final Player player = event.getPlayer();

        plugin.log("onPlayerRespawn: " + player.getName(), plugin.ANSI_BLUE);

        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {

            @Override
            public void run() {
                setKillerEffects(player);
            }

        });
    }

    /**
     * Обрабытывает вход пользователя
     *
     * @param event Событие
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        setKillerEffects(event.getPlayer());
    }
}

