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

        saveConfig();
    }

    private static void saveConfig()
    {
        plugin.saveConfig();
    }
}
