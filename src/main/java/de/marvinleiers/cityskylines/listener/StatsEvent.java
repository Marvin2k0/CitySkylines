package de.marvinleiers.cityskylines.listener;

import de.marvinleiers.cityskylines.CitySkylines;
import de.marvinleiers.cityskylines.users.User;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class StatsEvent implements Listener
{
    private static File file = new File(CitySkylines.plugin.getDataFolder().getPath() + "/city/City.yml");
    private static YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

    @EventHandler
    public void onMove(PlayerMoveEvent event)
    {
        Player player = event.getPlayer();
        Chunk chunk = player.getLocation().getChunk();

        if (!CitySkylines.plugin.getConfig().isSet("chunks." + chunk.getX() + ":" + chunk.getZ()))
        {
            return;
        }

        try
        {
            config.load(file);
        }
        catch (IOException | InvalidConfigurationException e)
        {
            e.printStackTrace();
        }

        String owner = CitySkylines.plugin.getConfig().getString("chunks." + chunk.getX() + ":" + chunk.getZ() + ".owner");
        User user = CitySkylines.getUser(Bukkit.getOfflinePlayer(UUID.fromString(owner)));
        double pop = config.getDouble(user.getConfig().getString("city") + ".people");

        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("§6Population: " + pop));
    }
}
