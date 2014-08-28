package me.truekenny.MyLineagePvpSystem;

import org.bukkit.ChatColor;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class MyLineagePvpSystem extends JavaPlugin {
    private Logger log = Logger.getLogger("Minecraft");

    public void onEnable() {
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new PlayerListener(this), this);

        log("MyLineagePvpSystem has been enabled!");
    }

    public void onDisable() {
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
        this.log.info(ANSI_PURPLE + text + ANSI_RESET);
    }

}
