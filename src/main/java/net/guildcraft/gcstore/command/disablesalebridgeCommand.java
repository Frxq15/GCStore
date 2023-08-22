package net.guildcraft.gcstore.command;

import net.guildcraft.gcstore.GCStore;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class disablesalebridgeCommand implements CommandExecutor {
    private GCStore plugin = GCStore.getInstance();
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if(!sender.hasPermission("gcstore.disablesale")) {
            sender.sendMessage(plugin.formatMsg("NO_PERMISSION"));
            return true;
        }
        if(sender instanceof Player) {
            sender.sendMessage(plugin.formatMsg("NO_PERMISSION"));
            return true;
        }
        if(args.length == 0) {
            sender.sendMessage(plugin.formatMsg("CONSOLE_SALE_DISABLED"));
            for(String line : plugin.getConfig().getStringList("MESSAGES.SALE_DISABLED")) {
                Bukkit.broadcastMessage(plugin.colourize(line).replace("%sale%", plugin.getStoreUtils().getSalePercentage()+""));
            }
            plugin.getStoreUtils().setSalePercentage(0);
            plugin.getStoreUtils().setSaleConfigPercentage(0);
            plugin.getStoreUtils().setSaleStatus(false);
            return true;
        }
        sender.sendMessage(plugin.colourize("&cUsage: /disablesalebridge"));
        return true;
    }
}
