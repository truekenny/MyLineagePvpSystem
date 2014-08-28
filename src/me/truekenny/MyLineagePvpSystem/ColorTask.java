package me.truekenny.MyLineagePvpSystem;

public class ColorTask implements Runnable {

    public static MyLineagePvpSystem plugin;

    public ColorTask(MyLineagePvpSystem instance) {
        plugin = instance;
    }

    public void run() {
        plugin.players.updateColor();
    }

}