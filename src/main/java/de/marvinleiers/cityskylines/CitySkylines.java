package de.marvinleiers.cityskylines;

import de.marvinleiers.cityskylines.city.City;
import de.marvinleiers.cityskylines.listener.PlaceBreakListener;
import de.marvinleiers.cityskylines.users.User;
import de.marvinleiers.cityskylines.utils.Text;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.*;

public final class CitySkylines extends JavaPlugin implements Listener
{
    private File file;
    private YamlConfiguration config;

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
        file = new File(CitySkylines.plugin.getDataFolder().getPath() + "/city/City.yml");
        config = YamlConfiguration.loadConfiguration(file);
        Text.setUp(this);

        setUpChunks();
        setUpPlayers();

        this.getCommand("city").setExecutor(this);
        this.getCommand("city").setTabCompleter(this);

        this.getCommand("income").setExecutor(this);

        this.getServer().getPluginManager().registerEvents(this, this);
        this.getServer().getPluginManager().registerEvents(new PlaceBreakListener(), this);

        payIncomes();
        check();
    }

    private void check()
    {
        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                for (Player player : Bukkit.getOnlinePlayers())
                {
                    User user = getUser(player);
                    double people = config.getDouble(user.getConfig().getString("city") + ".people");

                    /* Has to build hospitals */
                    double healt = config.getDouble(user.getConfig().getString("city") + ".health");

                    if ((people - healt * 10000) > 0)
                    {
                        player.sendMessage(Text.get("hospital-needed"));
                    }

                    /* Has to build water */
                    double water = 0;

                    if (config.isSet(user.getConfig().getString("city") + ".water"))
                        water = config.getDouble(user.getConfig().getString("city") + ".water");

                    if ((people - water * 5000) > 0)
                    {
                        player.sendMessage(Text.get("water-needed"));
                    }

                    /* Has to build energy */
                    double energy = 0;

                    if (config.isSet(user.getConfig().getString("city") + ".energy"))
                        energy = config.getDouble(user.getConfig().getString("city") + ".energy");

                    if ((people - energy * 10000) > 0)
                    {
                        player.sendMessage(Text.get("energy-needed"));
                    }
                }
            }
        }.runTaskTimer(this, 0, 5 * 20 * 60);
    }

    private void payIncomes()
    {
        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                for (OfflinePlayer player : Bukkit.getOfflinePlayers())
                {
                    File file = new File(getDataFolder().getPath() + "/users/" + player.getUniqueId() + ".yml");
                    File cityfile = new File(getDataFolder().getPath() + "/city/City.yml");

                    if (cityfile.exists())
                    {
                        if (file.exists())
                        {
                            YamlConfiguration playerconfig = YamlConfiguration.loadConfiguration(file);
                            YamlConfiguration cityconfig = YamlConfiguration.loadConfiguration(cityfile);

                            double income = cityconfig.getDouble(playerconfig.getString("city") + ".income");
                            double healt = 0;
                            boolean flag = false;

                            double people = 0;

                            if (cityconfig.isSet(playerconfig.getString("city") + ".people"))
                                people = cityconfig.getDouble(playerconfig.getString("city") + ".people");

                            if (cityconfig.isSet(playerconfig.getString("city") + ".health"))
                                healt = cityconfig.getDouble(playerconfig.getString("city") + ".health");

                            if ((people - healt * 10000) > 0)
                            {
                                flag = true;
                            }

                            double water = 0;

                            if (cityconfig.isSet(playerconfig.getString("city") + ".water"))
                                water = cityconfig.getDouble(playerconfig.getString("city") + ".water");

                            if ((people - water * 5000) > 0)
                            {
                                flag = true;
                            }

                            getEconomy().depositPlayer(player, flag ? income * 0.75 : income);

                            if (player.isOnline())
                                ((Player) player).sendMessage(Text.get("received-income").replace("%money%", (flag ? income * 0.75 : income) + ""));
                        }
                    }

                }
            }
        }.runTaskTimer(this, 0, 30 * 60 * 20);
    }

    private void setUpPlayers()
    {
        for (Player player : Bukkit.getOnlinePlayers())
        {
            if (!users.containsKey(player))
                users.put(player, new User(player));
        }
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
            ArrayList<String> list = new ArrayList<>();
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

        if (label.equalsIgnoreCase("income"))
        {
            File file = new File(getDataFolder().getPath() + "/users/" + player.getUniqueId() + ".yml");
            File cityfile = new File(getDataFolder().getPath() + "/city/City.yml");

            if (file.exists())
            {
                YamlConfiguration playerconfig = YamlConfiguration.loadConfiguration(file);
                YamlConfiguration cityconfig = YamlConfiguration.loadConfiguration(cityfile);

                double income = cityconfig.getDouble(playerconfig.getString("city") + ".income");
                double healt = 0;
                boolean flag = false;

                double people = 0;

                if (cityconfig.isSet(playerconfig.getString("city") + ".people"))
                    people = cityconfig.getDouble(playerconfig.getString("city") + ".people");

                if (cityconfig.isSet(playerconfig.getString("city") + ".health"))
                    healt = cityconfig.getDouble(playerconfig.getString("city") + ".health");

                if ((people - healt * 10000) > 0)
                {
                    flag = true;
                }

                double water = 0;

                if (cityconfig.isSet(playerconfig.getString("city") + ".water"))
                    water = cityconfig.getDouble(playerconfig.getString("city") + ".water");

                if ((people - water * 5000) > 0)
                {
                    flag = true;
                }

                player.sendMessage(Text.get("income").replace("%income%", (flag ? income * 0.75 : income) + ""));
                return true;
            }
            else
            {
                player.sendMessage("§cYou don't have a city!");
                return true;
            }
        }

        if (args.length == 0)
        {
            player.sendMessage("§9/city claim §7- Claim a chunk for your city");
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

                User user = getUser(player);

                chunks.put(chunk, player.getUniqueId());
                List<String> chunks = new ArrayList<>();

                if (user.getConfig().isSet("chunks"))
                    chunks = getUser(player).getConfig().getStringList("chunks");

                chunks.add(chunkX + ":" + chunkZ);
                user.getConfig().set("chunks", chunks);
                user.saveConfig();

                if (!user.getConfig().isSet("city"))
                {
                    String uuid = UUID.randomUUID().toString();

                    user.getConfig().set("city", uuid);
                    user.saveConfig();

                    City.create(uuid);
                }
            }
            else
            {
                player.sendMessage(Text.get("alreadyclaimed"));
                return true;
            }

            if (getEconomy().getBalance(player) < Double.parseDouble(Text.get("claimprice", false)))
            {
                player.sendMessage(Text.get("notenoughmoney"));
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

            getEconomy().withdrawPlayer(player, Double.parseDouble(Text.get("claimprice", false)));
            player.sendMessage(Text.get("claimed"));
            return true;
        }

        return false;
    }

    @EventHandler
    public void onExplode(BlockExplodeEvent event)
    {
        for (Block block : event.blockList())
        {
            if (getConfig().isSet("chunks." + block.getChunk().getX() + ":" + block.getChunk().getZ()))
                event.setCancelled(true);
        }
    }

    @EventHandler
    public void onExplode(EntityExplodeEvent event)
    {
        event.blockList().removeIf(block -> getConfig().isSet("chunks." + block.getChunk().getX() + ":" + block.getChunk().getZ()));
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
        users.put(event.getPlayer(), new User(event.getPlayer()));
    }

    public static User getUser(Player player)
    {
        return users.get(player);
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
