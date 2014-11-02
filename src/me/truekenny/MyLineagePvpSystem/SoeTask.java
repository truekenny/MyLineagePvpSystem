package me.truekenny.MyLineagePvpSystem;

public class SoeTask implements Runnable {

    public static MyLineagePvpSystem plugin;

    public SoeTask(MyLineagePvpSystem instance) {
        plugin = instance;
    }

    public void run() {
        plugin.players.updateSoe();
    }

}