package me.truekenny.MyLineagePvpSystem;

public class TickerTask implements Runnable {

    public static MyLineagePvpSystem plugin;

    public TickerTask(MyLineagePvpSystem instance) {
        plugin = instance;
    }

    public void run() {
        plugin.players.updateTick();
    }

}