package de.marvinleiers.cityskylines.city;

import com.sun.xml.internal.ws.addressing.WsaActionUtil;
import de.marvinleiers.cityskylines.CitySkylines;
import de.marvinleiers.cityskylines.users.User;
import de.marvinleiers.cityskylines.utils.Text;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;

import java.io.File;
import java.io.IOException;

public class City
{
    private static File file = new File(CitySkylines.plugin.getDataFolder().getPath() + "/city/City.yml");
    private static YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

    public static void addCityMoney(Player player, double add)
    {
        User user = CitySkylines.getUser(player);
        double money = getCityMoney(player);

        money += add;

        System.out.println(money);

        config.set(user.getConfig().getString("city") + ".money", money);
        saveConfig();
    }

    public static void create(String uuid)
    {
        config.set(uuid + ".money", 0);
        saveConfig();
    }

    public static void addHouse(Player player)
    {
        User user = CitySkylines.getUser(player);
        double houses = 0;

        if (config.isSet(user.getConfig().getString("city") + ".houses"))
            houses = config.getDouble(user.getConfig().getString("city") + ".houses");

        houses += 1;

        config.set(user.getConfig().getString("city") + ".houses", houses);
        saveConfig();
    }

    public static void removeHouse(Player player)
    {
        User user = CitySkylines.getUser(player);
        double houses = 0;

        if (config.isSet(user.getConfig().getString("city") + ".houses"))
            houses = config.getDouble(user.getConfig().getString("city") + ".houses");

        houses -= 1;

        config.set(user.getConfig().getString("city") + ".houses", houses);
        saveConfig();
    }

    public static void addPerson(Player player)
    {
        User user = CitySkylines.getUser(player);
        double people = 0;

        if (config.isSet(user.getConfig().getString("city") + ".people"))
            people = config.getDouble(user.getConfig().getString("city") + ".people");

        people += Double.parseDouble(Text.get("persons-per-house", false));

        config.set(user.getConfig().getString("city") + ".people", people);
        saveConfig();
    }

    public static void removePerson(Player player)
    {
        User user = CitySkylines.getUser(player);
        double people = 0;

        if (config.isSet(user.getConfig().getString("city") + ".people"))
            people = config.getDouble(user.getConfig().getString("city") + ".people");

        people -= Double.parseDouble(Text.get("persons-per-house", false));;

        config.set(user.getConfig().getString("city") + ".people", people);
        saveConfig();
    }

    public static void addEnergySrc(Player player)
    {
        User user = CitySkylines.getUser(player);
        double energy = 0;

        if (config.isSet(user.getConfig().getString("city") + ".energy"))
            energy = config.getDouble(user.getConfig().getString("city") + ".energy");

        energy += 1;

        config.set(user.getConfig().getString("city") + ".energy", energy);
        saveConfig();
    }

    public static void addWaterSrc(Player player)
    {
        User user = CitySkylines.getUser(player);
        double water = 0;

        if (config.isSet(user.getConfig().getString("city") + ".water"))
            water = config.getDouble(user.getConfig().getString("city") + ".water");

        water += 1;

        config.set(user.getConfig().getString("city") + ".water", water);
        saveConfig();
    }

    public static void addHealthSrc(Player player)
    {
        User user = CitySkylines.getUser(player);
        double healt = 0;

        if (config.isSet(user.getConfig().getString("city") + ".health"))
            healt = config.getDouble(user.getConfig().getString("city") + ".health");

        healt += 1;

        config.set(user.getConfig().getString("city") + ".health", healt);
        saveConfig();
    }

    public static void removeEnergySrc(Player player)
    {
        User user = CitySkylines.getUser(player);
        double energy = 0;

        if (config.isSet(user.getConfig().getString("city") + ".energy"))
            energy = config.getDouble(user.getConfig().getString("city") + ".energy");

        energy -= 1;

        config.set(user.getConfig().getString("city") + ".energy", energy);
        saveConfig();
    }

    public static void removeWaterSrc(Player player)
    {
        User user = CitySkylines.getUser(player);
        double water = 0;

        if (config.isSet(user.getConfig().getString("city") + ".water"))
            water = config.getDouble(user.getConfig().getString("city") + ".water");

        water -= 1;

        config.set(user.getConfig().getString("city") + ".water", water);
        saveConfig();
    }

    public static void removeHealthSrc(Player player)
    {
        User user = CitySkylines.getUser(player);
        double healt = 0;

        if (config.isSet(user.getConfig().getString("city") + ".health"))
            healt = config.getDouble(user.getConfig().getString("city") + ".health");

        healt -= 1;

        config.set(user.getConfig().getString("city") + ".health", healt);
        saveConfig();
    }

    public static void addIncome(BlockPlaceEvent event, Player player, double cost, double amount)
    {
        if (CitySkylines.getEconomy().getBalance(player) < cost)
        {
            player.sendMessage(Text.get("notenoughmoney"));

            if (event != null)
                event.setCancelled(true);

            return;
        }

        CitySkylines.getEconomy().withdrawPlayer(player, cost);

        User user = CitySkylines.getUser(player);
        double income = 0;

        if (config.isSet(user.getConfig().getString("city") + ".income"))
            income = config.getDouble(user.getConfig().getString("city") + ".income");

        income += amount;

        config.set(user.getConfig().getString("city") + ".income", income);
        saveConfig();
    }

    public static void addIncome(Player player, double cost, double amount)
    {
        addIncome(null, player, cost, amount);
    }

    public static double getCityMoney(Player player)
    {
        User user = CitySkylines.getUser(player);

        if (!user.getConfig().isSet("city"))
            return 0;

        return config.getDouble(user.getConfig().getString("city") + ".money");
    }

    private static void saveConfig()
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
