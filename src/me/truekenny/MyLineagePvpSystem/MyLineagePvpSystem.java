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

    public void log(String text) {
        this.log.info(">> " + text);
    }
}
