package de.marvinleiers.cityskylines.utils;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;

public class Text
{
    static FileConfiguration config;
    static Plugin plugin;

    public static String get(String path)
    {
        return path.equalsIgnoreCase("prefix") ? get(path, false) : get(path, true);
    }

    public static String get(String path, boolean prefix)
    {
        return ChatColor.translateAlternateColorCodes('&', prefix ? config.getString("prefix") + " " + config.getString(path) : config.getString(path));
    }

    public static void setUp(Plugin plugin)
    {
        Text.plugin = plugin;
        Text.config = plugin.getConfig();

        config.options().copyDefaults(true);
        config.addDefault("prefix", "&b[CitySkylines&8]&f");
        config.addDefault("claimed", "&7Chunk has been claimed!");
        config.addDefault("alreadyclaimed", "&7This chunk has already been claimed!");
        config.addDefault("cantbuild", "&7You can't build or break blocks here!");
        config.addDefault("notenoughmoney", "&7You don't have enough money for that!");
        config.addDefault("claimprice", 25000);
        config.addDefault("hospital-needed", "&7Your citizen need hospitals, build more!");
        config.addDefault("energy-needed", "&7Your citizen need energy, produce more!");
        config.addDefault("water-needed", "&7Your citizen need water, build more pumps!");
        config.addDefault("received-income", "&7You received your income of &b%money%€");
        config.addDefault("income", "&7Your income is: &b%income%€");
        config.addDefault("persons-per-house", 3);
        config.addDefault("cost-diorite", 1000);
        config.addDefault("rate-diorite", 50);
        config.addDefault("cost-polished-diorite", 500);
        config.addDefault("rate-polished-diorite", 10);
        config.addDefault("cost-cyan-terracotta", 2500);
        config.addDefault("rate-cyan-terracotta", 250);
        config.addDefault("cost-white-concrete", 10000);
        config.addDefault("rate-white-concrete", 2000);
        config.addDefault("cost-black-terracotta", 250);
        config.addDefault("rate-black-terracotta", -10);
        config.addDefault("cost-stone-bricks", 100);
        config.addDefault("rate-stone-bricks", -1);
        config.addDefault("cost-quartz-slab", 1000);
        config.addDefault("rate-quartz-slab", -50);
        config.addDefault("cost-cobblestone-wall", 1000);
        config.addDefault("rate-cobblestone-wall", -50);
        config.addDefault("cost-magenta-terracotta", 5000);
        config.addDefault("rate-magenta-terracotta", -500);

        saveConfig();
    }

    private static void saveConfig()
    {
        plugin.saveConfig();
    }
}
