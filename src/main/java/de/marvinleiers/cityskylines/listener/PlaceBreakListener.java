package de.marvinleiers.cityskylines.listener;

import de.marvinleiers.cityskylines.city.City;
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
            City.addIncome(player, 1000, 50);
            City.addPerson(player);
            City.addHouse(player);
        }
        else if (block.getType() == Material.POLISHED_DIORITE)
        {
            City.addIncome(player, 500, 10);
        }
        else if (block.getType() == Material.CYAN_TERRACOTTA)
        {
            City.addIncome(player, 2500, 250);
        }
        else if (block.getType() == Material.WHITE_CONCRETE)
        {
            City.addIncome(player, 10000, 2000);
        }
        else if (block.getType() == Material.BLACK_TERRACOTTA)
        {
            City.addIncome(player, 250, -10);
        }
        else if (block.getType() == Material.STONE_BRICKS)
        {
            City.addIncome(player, 100, -1);
        }
        else if (block.getType() == Material.QUARTZ_SLAB)
        {
            City.addIncome(player, 1000, 50);
            City.addEnergySrc(player);
        }
        else if (block.getType() == Material.COBBLESTONE_WALL)
        {
            City.addIncome(player, 1000, -50);
            City.addWaterSrc(player);
        }
        else if (block.getType() == Material.MAGENTA_TERRACOTTA)
        {
            City.addIncome(player, 5000, -500);
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
            City.addIncome(player, 0, -50);
            City.removePerson(player);
            City.removeHouse(player);
        }
        else if (block.getType() == Material.POLISHED_DIORITE)
        {
            City.addIncome(player, 0, -10);
        }
        else if (block.getType() == Material.CYAN_TERRACOTTA)
        {
            City.addIncome(player, 0, -250);
        }
        else if (block.getType() == Material.WHITE_CONCRETE)
        {
            City.addIncome(player, 0, -2000);
        }
        else if (block.getType() == Material.BLACK_TERRACOTTA)
        {
            City.addIncome(player, 0, 10);
        }
        else if (block.getType() == Material.STONE_BRICKS)
        {
            City.addIncome(player, 0, 1);
        }
        else if (block.getType() == Material.QUARTZ_SLAB)
        {
            City.addIncome(player, 0, -50);
            City.removeEnergySrc(player);
        }
        else if (block.getType() == Material.COBBLESTONE_WALL)
        {
            City.addIncome(player, 0, 50);
            City.removeWaterSrc(player);
        }
        else if (block.getType() == Material.MAGENTA_TERRACOTTA)
        {
            City.addIncome(player, 0, 500);
            City.removeHealthSrc(player);
        }
    }
}
