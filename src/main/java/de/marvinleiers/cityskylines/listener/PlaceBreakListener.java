package de.marvinleiers.cityskylines.listener;

import de.marvinleiers.cityskylines.city.City;
import de.marvinleiers.cityskylines.utils.Text;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class PlaceBreakListener implements Listener
{
    @EventHandler
    public void onPlace(BlockPlaceEvent event)
    {
        if (event.isCancelled())
            return;

        Player player = event.getPlayer();

        Block block = event.getBlock();

        if (block.getType() == Material.DIORITE)
        {
            City.addIncome(event, player, Double.parseDouble(Text.get("cost-diorite", false)), Double.parseDouble(Text.get("rate-diorite", false)));
            City.addPerson(player);
            City.addHouse(player);
        }
        else if (block.getType() == Material.POLISHED_DIORITE)
        {
            City.addIncome(event, player, Double.parseDouble(Text.get("cost-polished-diorite", false)), Double.parseDouble(Text.get("rate-polished-diorite", false)));
        }
        else if (block.getType() == Material.CYAN_TERRACOTTA)
        {
            City.addIncome(event, player, Double.parseDouble(Text.get("cost-cyan-terracotta", false)), Double.parseDouble(Text.get("rate-cyan-terracotta", false)));
        }
        else if (block.getType() == Material.WHITE_CONCRETE)
        {
            City.addIncome(event, player, Double.parseDouble(Text.get("cost-white-concrete", false)), Double.parseDouble(Text.get("rate-white-concrete", false)));
        }
        else if (block.getType() == Material.BLACK_TERRACOTTA)
        {
            City.addIncome(event, player, Double.parseDouble(Text.get("cost-black-terracotta", false)), Double.parseDouble(Text.get("rate-black-terracotta", false)));
        }
        else if (block.getType() == Material.STONE_BRICKS)
        {
            City.addIncome(event, player, Double.parseDouble(Text.get("cost-stone-bricks", false)), Double.parseDouble(Text.get("cost-stone-bricks", false)));
        }
        else if (block.getType() == Material.QUARTZ_SLAB)
        {
            City.addIncome(event, player, Double.parseDouble(Text.get("cost-quartz-slab", false)), Double.parseDouble(Text.get("rate-quartz-slab", false)));
            City.addEnergySrc(player);
        }
        else if (block.getType() == Material.COBBLESTONE_WALL)
        {
            City.addIncome(event, player, Double.parseDouble(Text.get("cost-cobblestone-wall", false)), Double.parseDouble(Text.get("rate-cobblestone-wall", false)));
            City.addWaterSrc(player);
        }
        else if (block.getType() == Material.MAGENTA_TERRACOTTA)
        {
            City.addIncome(event, player, Double.parseDouble(Text.get("cost-magenta-terracotta", false)), Double.parseDouble(Text.get("rate-magenta-terracotta", false)));
            City.addHealthSrc(player);
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event)
    {
        if (event.isCancelled())
            return;

        Player player = event.getPlayer();

        Block block = event.getBlock();

        if (block.getType() == Material.DIORITE)
        {
            City.addIncome(player, 0, -Double.parseDouble(Text.get("rate-diorite", false)));
            City.removePerson(player);
            City.removeHouse(player);
        }
        else if (block.getType() == Material.POLISHED_DIORITE)
        {
            City.addIncome(player, 0, -Double.parseDouble(Text.get("rate-polished-diorite", false)));
        }
        else if (block.getType() == Material.CYAN_TERRACOTTA)
        {
            City.addIncome(player, 0, -Double.parseDouble(Text.get("rate-cyan-terracotta", false)));
        }
        else if (block.getType() == Material.WHITE_CONCRETE)
        {
            City.addIncome(player, 0, -Double.parseDouble(Text.get("rate-white-concrete", false)));
        }
        else if (block.getType() == Material.BLACK_TERRACOTTA)
        {
            City.addIncome(player, 0, -Double.parseDouble(Text.get("rate-black-terracotta", false)));
        }
        else if (block.getType() == Material.STONE_BRICKS)
        {
            City.addIncome(player, 0, -Double.parseDouble(Text.get("rate-stone-bricks", false)));
        }
        else if (block.getType() == Material.QUARTZ_SLAB)
        {
            City.addIncome(player, 0, -Double.parseDouble(Text.get("rate-quartz-slab", false)));
            City.removeEnergySrc(player);
        }
        else if (block.getType() == Material.COBBLESTONE_WALL)
        {
            City.addIncome(player, 0, -Double.parseDouble(Text.get("rate-cobblestone-wall", false)));
            City.removeWaterSrc(player);
        }
        else if (block.getType() == Material.MAGENTA_TERRACOTTA)
        {
            City.addIncome(player, 0, -Double.parseDouble(Text.get("rate-magenta-terracotta", false)));
            City.removeHealthSrc(player);
        }
    }
}
