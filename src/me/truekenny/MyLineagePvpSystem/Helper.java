package me.truekenny.MyLineagePvpSystem;

import org.bukkit.Location;

public class Helper {

    /**
     * Расстояние между точками
     *
     * @param a
     * @param b
     * @return
     */
    public static double betweenPoints(Location a, Location b) {
        return Math.sqrt(
                Math.pow(a.getBlockX() - b.getBlockX(), 2) +
                        Math.pow(a.getBlockY() - b.getBlockY(), 2) +
                        Math.pow(a.getBlockZ() - b.getBlockZ(), 2)

        );
    }
}
