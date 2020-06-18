package de.marvinleiers.cityskylines;

import de.marvinleiers.cityskylines.users.User;
import de.marvinleiers.cityskylines.utils.Text;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.*;

public final class CitySkylines extends JavaPlugin implements Listener
{
    public static HashMap<Player, User> users = new HashMap<>();
    public static HashMap<Chunk, UUID> chunks = new HashMap<>();
    public static CitySkylines plugin = null;
    private static Economy econ = null;

    @Override
    public void onEnable()
    {
        if (!setupEconomy())
        {
            System.out.println(String.format("[%s] - §4Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        plugin = this;
        Text.setUp(this);

        setUpChunks();

        this.getCommand("city").setExecutor(this);
        this.getCommand("city").setTabCompleter(this);
        this.getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable()
    {
    }

    private void setUpChunks()
    {
        if (!getConfig().isSet("chunks"))
            return;

        for (Map.Entry<String, Object> entry : getConfig().getConfigurationSection("chunks").getValues(false).entrySet())
        {
            String world = getConfig().getString("chunks." + entry.getKey() + ".world");
            int x = Integer.parseInt(entry.getKey().split(":")[0]);
            int z = Integer.parseInt(entry.getKey().split(":")[1]);

            Chunk chunk = Bukkit.getWorld(world).getChunkAt(x, z);
            UUID owner = UUID.fromString(getConfig().getString("chunks." + entry.getKey() + ".owner"));

            chunks.put(chunk, owner);
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args)
    {
        if (args.length == 1)
        {
            ArrayList<String> list = new ArrayList<String>();
            list.add("claim");
            list.add("add");
            list.add("remove");

            return list;
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("claim"))
        {
            return new ArrayList<>();
        }

        if (args.length > 3)
            return new ArrayList<>();

        return super.onTabComplete(sender, command, alias, args);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if (!(sender instanceof Player))
        {
            sender.sendMessage("§7Plugin by Marvin2k0 version: §9" + this.getDescription().getVersion());
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0)
        {
            player.sendMessage("§9/city claim §7- Claim a city if you don't own one");
            player.sendMessage("§9/city invite <player> §7- Add a player to your city");
            player.sendMessage("§9/city remove <player> §7- Remove a player from your city");
            return true;
        }

        if (args[0].equalsIgnoreCase("claim"))
        {
            Chunk chunk = player.getLocation().getChunk();
            int chunkX = chunk.getX();
            int chunkZ = chunk.getZ();

            if (!this.getConfig().isSet("chunks." + chunkX + ":" + chunkZ))
            {
                this.getConfig().set("chunks." + chunkX + ":" + chunkZ + ".owner", player.getUniqueId().toString());
                this.getConfig().set("chunks." + chunkX + ":" + chunkZ + ".world", player.getLocation().getWorld().getName());
                this.saveConfig();

                chunks.put(chunk, player.getUniqueId());
            }
            else
            {
                player.sendMessage(Text.get("alreadyclaimed"));
                return true;
            }

            for (int x = 0; x < 16; x++)
            {
                for (int z = 0; z < 16; z++)
                {
                    if (x == 0 || x == 15 || z == 0 || z == 15)
                    {
                        Block block = chunk.getBlock(x, chunk.getChunkSnapshot().getHighestBlockYAt(x, z) - 1, z);

                        player.sendBlockChange(block.getLocation(), Material.QUARTZ_BLOCK.createBlockData());
                    }
                }
            }

            player.sendMessage(Text.get("claimed"));
            return true;
        }

        return false;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event)
    {
        Chunk chunk = event.getBlock().getChunk();

        if (!chunks.containsKey(chunk))
        {
            event.getPlayer().sendMessage(Text.get("cantbuild"));
            event.setCancelled(true);
            return;
        }

        UUID owner = chunks.get(chunk);

        if (!owner.equals(event.getPlayer().getUniqueId()))
        {
            event.getPlayer().sendMessage(Text.get("cantbuild"));
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event)
    {
        Chunk chunk = event.getBlock().getChunk();

        if (!chunks.containsKey(chunk))
        {
            event.getPlayer().sendMessage(Text.get("cantbuild"));
            event.setCancelled(true);
            return;
        }

        UUID owner = chunks.get(chunk);

        if (!owner.equals(event.getPlayer().getUniqueId()))
        {
            event.getPlayer().sendMessage(Text.get("cantbuild"));
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event)
    {
        users.put(event.getPlayer(), getUser(event.getPlayer()));
    }

    public User getUser(Player player)
    {
        File file = new File(getDataFolder().getPath() + "/users/" + player.getUniqueId().toString() + ".yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

        try
        {
            config.save(file);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return new User(player);
    }

    public YamlConfiguration getPlayerConfig(Player player)
    {
        File file = new File(getDataFolder().getPath() + "/users/" + player.getUniqueId().toString() + ".yml");

        return YamlConfiguration.loadConfiguration(file);
    }

    public void savePlayerConfig(Player player)
    {
        File file = new File(getDataFolder().getPath() + "/users/" + player.getUniqueId().toString() + ".yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

        try
        {
            config.save(file);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public static Economy getEconomy()
    {
        return econ;
    }

    private boolean setupEconomy()
    {
        if (getServer().getPluginManager().getPlugin("Vault") == null)
        {
            return false;
        }

        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);

        if (rsp == null)
        {
            return false;
        }

        econ = rsp.getProvider();
        return econ != null;
    }
}
