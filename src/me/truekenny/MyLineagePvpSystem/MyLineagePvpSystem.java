package me.truekenny.MyLineagePvpSystem;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class MyLineagePvpSystem extends JavaPlugin {
    private Logger log = Logger.getLogger("Minecraft");
    public PlayerListener playerListener;
    public Players players;
    private int taskId;

    /**
     * Экземпляр конфигурации
     */
    public FileConfiguration config;

    public void onEnable() {
        defaultConfig();

        players = new Players(this);

        PluginManager pm = getServer().getPluginManager();
        playerListener = new PlayerListener(this);
        pm.registerEvents(playerListener, this);

        if(config.getBoolean("rpg.enable")) {
            Mobs.load(this);
            pm.registerEvents(new RpgListener(this), this);
        } else {
            log("MyLineagePvpSystem: Rpg game disabled by config (rpg.enable)", ANSI_RED);
        }

        getCommand("pvpstatus").setExecutor(new PvpStatusCommand(this));
        getCommand("pvpspawn").setExecutor(new PvpSpawnCommand(this));

        log("MyLineagePvpSystem has been enabled!");

        taskId = getServer().getScheduler().scheduleSyncRepeatingTask(this, new ColorTask(this), 0, 20);
    }

    public void onDisable() {
        players.destroy();
        getServer().getScheduler().cancelTask(taskId);
        if(config.getBoolean("rpg.enable")) {
            Mobs.save();
        }

        log("MyLineagePvpSystem has been disabled.");
    }

    public void defaultConfig() {
        config = getConfig();

        config.addDefault("local.statusPeace", "You went into a peaceful mode"); // Вы перешли в Мирный режим
        config.addDefault("local.statusPVP", "You went into a PVP mode"); // Вы перешли в режим PVP
        config.addDefault("local.statusPK", "You have become a murderer, are imposed on you the effect of slowing, fatigue, weakness"); // Вы стали Убийцей, на вас наложены эффекты Замедление, Усталость, Слабость
        config.addDefault("local.statisticPK", "PK"); // PK
        config.addDefault("local.statisticPVP", "PVP"); // PVP
        config.addDefault("local.statisticKarma", "Karma"); // Karma
        config.addDefault("local.statisticDeaths", "Deaths"); // Смертей
        config.addDefault("local.statisticDeathsMore", "(from the other players)"); // (от других игроков)
        config.addDefault("local.statisticModePeace", "You are in a peace mode"); // Вы в Мирном режиме
        config.addDefault("local.statisticModePVP", "You are in a PVP mode"); // Вы в режиме PVP
        config.addDefault("local.statisticModePK", "You are player killer"); // Вы Убийца

        config.addDefault("drop.inventory.peace", 0);
        config.addDefault("drop.inventory.pvp", 10);
        config.addDefault("drop.inventory.pk", 100);

        config.addDefault("drop.armor.peace", 0);
        config.addDefault("drop.armor.pvp", 9);
        config.addDefault("drop.armor.pk", 99);

        config.addDefault("experience.keep.peace", true);
        config.addDefault("experience.keep.pvp", false);
        config.addDefault("experience.keep.pk", false);

        config.addDefault("karma.kill.peace", -10);
        config.addDefault("karma.kill.self", 10);
        config.addDefault("karma.kill.mob", 1);

        config.addDefault("world.doesNotWork", "creative,world_creative");
        config.addDefault("world.doNotCleanKarma", "creative,world_creative");

        config.addDefault("time.purple", 30);

        config.addDefault("rpg.enable", false);

        config.addDefault("rpg.name.CREEPER", "Creeper");
        config.addDefault("rpg.name.SKELETON", "Skeleton");
        config.addDefault("rpg.name.SPIDER", "Spider");
        config.addDefault("rpg.name.GIANT", "Giaht");
        config.addDefault("rpg.name.ZOMBIE", "Zombie");
        config.addDefault("rpg.name.SLIME", "Slime");
        config.addDefault("rpg.name.GHAST", "Ghast");
        config.addDefault("rpg.name.PIG_ZOMBIE", "Pig Zombie");
        config.addDefault("rpg.name.ENDERMAN", "Enderman");
        config.addDefault("rpg.name.CAVE_SPIDER", "Cave Spider");
        config.addDefault("rpg.name.SILVERFISH", "Silver Fish");
        config.addDefault("rpg.name.BLAZE", "Blaze");
        config.addDefault("rpg.name.MAGMA_CUBE", "Magma Cube");
        config.addDefault("rpg.name.ENDER_DRAGON", "ENDER DRAGON");
        config.addDefault("rpg.name.WITHER", "Wither");
        config.addDefault("rpg.name.BAT", "Bat");
        config.addDefault("rpg.name.WITCH", "Witch");
        config.addDefault("rpg.name.PIG", "Pig");
        config.addDefault("rpg.name.SHEEP", "Sheep");
        config.addDefault("rpg.name.COW", "Cow");
        config.addDefault("rpg.name.CHICKEN", "Chicken");
        config.addDefault("rpg.name.SQUID", "Squid");
        config.addDefault("rpg.name.WOLF", "Wolf");
        config.addDefault("rpg.name.MUSHROOM_COW", "Mushroom Cow");
        config.addDefault("rpg.name.SNOWMAN", "Snowman");
        config.addDefault("rpg.name.OCELOT", "Ocelot");
        config.addDefault("rpg.name.IRON_GOLEM", "Golem");
        config.addDefault("rpg.name.HORSE", "Horse");
        config.addDefault("rpg.name.VILLAGER", "Villager");
        config.addDefault("rpg.name.default", "Level");


        config.options().copyDefaults(true);
        saveConfig();
    }

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    public void log(String text, String color) {
        this.log.info(color + text + ANSI_RESET);
    }

    public void log(String text) {
        // log(text, ANSI_GREEN);
    }

    public static final int NOTWORK = 0;
    public static final int NOTCLEANKARMA = 1;

    /**
     * Возвращает, что мир должен быть отключен
     * @param worldType
     * @param worldName
     * @return
     */
    public boolean checkWorld(int worldType, String worldName) {
        String worlds = "";
        switch (worldType) {
            case NOTWORK:
                worlds = config.getString("world.doesNotWork");
                break;
            case NOTCLEANKARMA:
                worlds = config.getString("world.doNotCleanKarma");
                break;
        }
        worlds = "," + worlds.toLowerCase() + ",";

        return worlds.contains("," + worldName.toLowerCase() + ",");
    }

    public boolean checkWorld(int worldType, Player player) {
        return checkWorld(worldType, player.getWorld().getName());
    }
}
