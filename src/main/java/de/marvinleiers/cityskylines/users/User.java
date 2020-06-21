package de.marvinleiers.cityskylines.users;

import de.marvinleiers.cityskylines.CitySkylines;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;

public class User
{
    private final Player player;
    private final File file;
    private final YamlConfiguration config;

    public User(Player player)
    {
        this.player = player;
        this.file = new File(CitySkylines.plugin.getDataFolder().getPath() + "/users/" + player.getUniqueId().toString() + ".yml");
        this.config = YamlConfiguration.loadConfiguration(file);
    }

    public double getMoney()
    {
        return CitySkylines.getEconomy().getBalance(player);
    }

    public void addMoney(double money)
    {
        CitySkylines.getEconomy().depositPlayer(player, money);
    }

    public Player getPlayer()
    {
        return player;
    }

    public YamlConfiguration getConfig()
    {
        return config;
    }

    public void saveConfig()
    {
        try
        {
            config.save(file);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
