package me.truekenny.MyLineagePvpSystem;

import org.bukkit.Location;

import java.util.Random;

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

    public static double rand(double rangeMin, double rangeMax) {
        Random random = new Random();

        return rangeMin + (rangeMax - rangeMin) * random.nextDouble();
    }
}
