package me.truekenny.MyLineagePvpSystem;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

/**
 * Created by kenny on 28.08.14.
 */
public class MyLineagePvpSystem extends JavaPlugin {
    Logger log = Logger.getLogger("Minecraft");

    public void onEnable() {
        log.info("MyLineagePvpSystem has been enabled!");
    }

    public void onDisable() {
        log.info("MyLineagePvpSystem has been disabled.");
    }
}
