package de.marvinleiers.cityskylines.commands;

import de.marvinleiers.cityskylines.CitySkylines;
import de.marvinleiers.cityskylines.users.User;
import de.marvinleiers.cityskylines.utils.Text;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.util.List;

public class LoanCommand implements CommandExecutor
{
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if (!(sender instanceof Player))
        {
            sender.sendMessage("§cOnly for players!");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0)
        {
            player.sendMessage("§c/loan <amount> - Get a loan");
            player.sendMessage("§c/loan pay <amount> - Pay back your loan");
            return true;
        }

        User user = CitySkylines.getUser(player);

        if (args.length == 1)
        {
            boolean allowed = user.getConfig().isSet("loan.allowed") ? user.getConfig().getBoolean("loan.allowed") : true;

            if (!allowed)
            {
                player.sendMessage(Text.get("not-allowed"));
                return true;
            }

            try
            {
                double amount = Double.parseDouble(args[0]);

                if (amount > Double.parseDouble(Text.get("loan-limit", false)))
                {
                    player.sendMessage("§cThe limit is §7" + Double.parseDouble(Text.get("loan-limit", false)));
                    return true;
                }

                CitySkylines.getEconomy().depositPlayer(player, amount);

                DecimalFormat format = new DecimalFormat("#######.##");

                user.getConfig().set("loan.allowed", false);
                user.getConfig().set("loan.amount", format.format(amount * 1.1));
                user.saveConfig();

                player.sendMessage(Text.get("loan").replace("%amount%", amount + ""));
            }
            catch (NumberFormatException e)
            {
                player.sendMessage("§cPlease enter numbers only! §4" + args[0] + " §cis not a valid input!");
            }

            return true;
        }
        else if (args.length == 2 && args[0].equalsIgnoreCase("pay"))
        {
            if (!user.getConfig().isSet("loan.amount") || user.getConfig().getBoolean("loan.allowed"))
            {
                player.sendMessage(Text.get("no-loan"));
                return true;
            }

            double amount = Double.parseDouble(user.getConfig().getString("loan.amount"));

            try
            {
                double payoff = Double.parseDouble(args[1]);

                if (CitySkylines.getEconomy().getBalance(player) >= payoff)
                {
                    double left = amount - payoff;

                    if (payoff >= amount)
                    {
                        CitySkylines.getEconomy().withdrawPlayer(player, amount);
                    }
                    else
                    {
                        CitySkylines.getEconomy().withdrawPlayer(player, payoff);
                    }

                    if (left <= 0)
                    {
                        user.getConfig().set("loan.allowed", true);
                        user.saveConfig();
                        left = 0;
                    }

                    user.getConfig().set("loan.amount", left < 0 ? 0 : left);
                    user.saveConfig();

                    player.sendMessage(Text.get("payed-loan").replace("%left%", left + ""));
                    return true;
                }
                else
                {
                    player.sendMessage("§cYou don't have that much money!");
                    return true;
                }
            }
            catch (NumberFormatException e)
            {
                player.sendMessage("§cPlease enter numbers only! §4" + args[0] + " §cis not a valid input!");
            }
        }

        return true;
    }
}
