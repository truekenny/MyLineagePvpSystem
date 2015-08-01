package me.truekenny.MyLineagePvpSystem;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
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
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

// import org.kitteh.tag.AsyncPlayerReceiveNameTagEvent;
//import org.kitteh.tag.TagAPI;

public class PlayerListener implements Listener {
    private static final Set<String> types = new HashSet<String>(Arrays.asList(
            new String[]{
                    "CHICKEN", "COW", "HORSE", "PIG", "RABBIT", "SHEEP", "SQUID", "WOLF",
                    "BLAZE", "CAVE_SPIDER", "CREEPER", "ENDERMAN", "MAGMA_CUBE",
                    "PIG_ZOMBIE", "SKELETON", "SLIME", "SPIDER", "WITCH", "ZOMBIE"
            }
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
        if (plugin.checkWorld(plugin.NOTWORK, player)) return;

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
        plugin.log("died: " + player.getName() + "", plugin.ANSI_PURPLE);
        if (plugin.players.getPlayerData(player).died()) {
            // TagAPI.refreshPlayer(player);
            plugin.colorListener.updateColor(player);
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
        int dropInventory = 0;
        int dropArmor = 0;
        boolean keepLevel = true;

        if (plugin.players.getPlayerData(player).getColor().equals(ChatColor.WHITE)) {
            // Peace
            dropInventory = plugin.config.getInt("drop.inventory.peace");
            dropArmor = plugin.config.getInt("drop.armor.peace");
            keepLevel = plugin.config.getBoolean("experience.keep.peace");
        } else if (plugin.players.getPlayerData(player).getColor().equals(ChatColor.RED)) {
            // PK
            dropInventory = plugin.config.getInt("drop.inventory.pk");
            dropArmor = plugin.config.getInt("drop.armor.pk");
            keepLevel = plugin.config.getBoolean("experience.keep.pk");
        } else {
            // PVP
            dropInventory = plugin.config.getInt("drop.inventory.pvp");
            dropArmor = plugin.config.getInt("drop.armor.pvp");
            keepLevel = plugin.config.getBoolean("experience.keep.pvp");
        }

        plugin.log("");

        // Begin drops

        if (keepLevel) {
            // Сохранить ЭКСП
            event.setKeepLevel(true);
            event.setDroppedExp(0);
        }

        // Сохранить броню
        final ItemStack[] armor = player.getInventory().getArmorContents();

        for (int i = 0; i < armor.length; i++) {
            ItemStack is = armor[i];

            if (is != null && Math.random() * 100 < 100 - dropArmor)
                event.getDrops().remove(is); // Сохраняется
            else
                armor[i] = null; // Падает
        }

        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
            @Override
            public void run() {
                player.getInventory().setArmorContents(armor);
            }

        });
/*
        for (ItemStack is : armor) {
            event.getDrops().remove(is);
        }
*/

        // Сохранить броню
        final ItemStack[] inventory = player.getInventory().getContents();
        for (int i = 0; i < inventory.length; i++) {
            ItemStack is = inventory[i];

            if (is != null && Math.random() * 100 < 100 - dropInventory)
                event.getDrops().remove(is); // Сохраняется
            else
                inventory[i] = null; // Падает
        }
        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {

            @Override
            public void run() {
                player.getInventory().setContents(inventory);
            }

        });

        // End drops

        //
        plugin.log("murder: " + killer.getName() + " -> " + player.getName(), plugin.ANSI_RED);
        if (plugin.players.getPlayerData(killer).murder(player)) {
            //TagAPI.refreshPlayer(killer);
            plugin.colorListener.updateColor(killer);

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
            if (plugin.checkWorld(plugin.NOTWORK, killer)) return;
            if (plugin.checkWorld(plugin.NOTCLEANKARMA, killer)) return;

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
        plugin.log("cleansing: " + player.getName() + "", plugin.ANSI_GREEN);
        if (plugin.players.getPlayerData(player).cleansing()) {

            //TagAPI.refreshPlayer(player);
            plugin.colorListener.updateColor(player);

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
        Entity entity = event.getEntity();

        if (entity == null) {
            plugin.log("onEntityDamage: entity is null", plugin.ANSI_BLUE);

            return;
        }

        if (!entity.getType().toString().equalsIgnoreCase("player")) {
            // plugin.log("onEntityDamage: ERROR: Wrong type " + entity.getType().toString(), plugin.ANSI_RED);

            return;
        }

        if (plugin.checkWorld(plugin.NOTWORK, (Player) entity)) return;

        Player damager = getDamager(event);

        if (damager == null) {
            // plugin.log("onEntityDamage: damager is null", plugin.ANSI_BLUE);

            return;
        }

        if (event.isCancelled()) {

            String enName;

            try {
                enName = ((Player) entity).getName();
            } catch (Exception e) {
                plugin.log("ERROR: " + entity.getType().toString(), plugin.ANSI_RED);

                return;
            }

            String dmName = damager.getName();
            plugin.log("onEntityDamage: isCancelled (" + dmName + " -> " + enName + ")", plugin.ANSI_BLUE);

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
        plugin.log("hit: " + damager.getName() + " -> " + player.getName(), plugin.ANSI_YELLOW);

        if (plugin.players.getPlayerData(damager).hit(player)) {
            // TagAPI.refreshPlayer(damager);
            plugin.colorListener.updateColor(damager);

        }
    }

    /**
     * Отдать цвет имени пользователя
     *
     * @param event
     */
/*
    @EventHandler
    public void onNameTag(AsyncPlayerReceiveNameTagEvent event) {
        Player player = event.getNamedPlayer();

        if(plugin.checkWorld(plugin.NOTWORK, player)) return;

        event.setTag(getPlayerColor(player) + player.getName());
    }
*/

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

        Player player = event.getPlayer();
        if (plugin.players.getPlayerData(player).inSoe()) {
            player.sendMessage(ChatColor.RED + plugin.config.getString("local.soe.cancel"));
        }
        if (plugin.players.getPlayerData(player).inCall()) {
            player.sendMessage(ChatColor.RED + plugin.config.getString("local.call.cancel"));
        }
    }

    /**
     * Устанавливает на убийцу эффекты
     *
     * @param player
     */
    public void setKillerEffects(Player player) {
        if (plugin.checkWorld(plugin.NOTWORK, player)) {
            removeKillerEffects(player);

            return;
        }

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

        if (plugin.checkWorld(plugin.NOTWORK, player)) return;

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

    /**
     * Защита от ведра с лавой на спавне
     * @param event
     */
    @EventHandler
    public void onClick(PlayerBucketEmptyEvent event) {
        if (event.getBucket().getId() == 327) {

            Location spawn = plugin.players.getSpawn();
            Location playerLocation = event.getPlayer().getLocation();
            if(spawn == null) {

                return;
            }

            double distance = spawn.distance(playerLocation);

            if (distance < plugin.config.getDouble("spawn.protect.lava.bucket.radius")) {
                event.setCancelled(true);
                event.getPlayer().sendMessage(plugin.config.getString("spawn.protect.lava.bucket.message"));
                plugin.log("Distance lava: " + distance);
            }
        }
    }

    /**
     * Защита от огнива на спавне
     * @param event
     */
    @EventHandler
    public void onPlayerUse(PlayerInteractEvent event){
        Player p = event.getPlayer();

        if(p.getItemInHand().getType() == Material.FLINT_AND_STEEL){
            Location spawn = plugin.players.getSpawn();
            Location playerLocation = event.getPlayer().getLocation();
            if(spawn == null) {

                return;
            }

            double distance = spawn.distance(playerLocation);

            if (distance < plugin.config.getDouble("spawn.protect.flint.radius")) {
                event.setCancelled(true);
                event.getPlayer().sendMessage(plugin.config.getString("spawn.protect.flint.message"));
                plugin.log("Distance flint: " + distance);
            }

        }
    }
}

