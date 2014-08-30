package me.truekenny.MyLineagePvpSystem;

import org.bukkit.configuration.file.FileConfiguration;
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

        getCommand("pvpstatus").setExecutor(new PvpStatusCommand(this));

        log("MyLineagePvpSystem has been enabled!");

        taskId = getServer().getScheduler().scheduleSyncRepeatingTask(this, new ColorTask(this), 0, 20);
    }

    public void onDisable() {
        players.destroy();
        getServer().getScheduler().cancelTask(taskId);

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

}
