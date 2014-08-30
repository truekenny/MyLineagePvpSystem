package me.truekenny.MyLineagePvpSystem;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class MyLineagePvpSystem extends JavaPlugin {
    private Logger log = Logger.getLogger("Minecraft");
    public PlayerListener playerListener;
    public Players players;
    private int taskId;

    public void onEnable() {
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
