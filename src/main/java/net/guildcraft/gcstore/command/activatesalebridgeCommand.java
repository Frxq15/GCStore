package net.guildcraft.gcstore.command;

import net.guildcraft.gcstore.GCStore;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class activatesalebridgeCommand implements CommandExecutor {
    private GCStore plugin = GCStore.getInstance();
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if(!sender.hasPermission("gcstore.activesale")) {
            sender.sendMessage(plugin.formatMsg("NO_PERMISSION"));
            return true;
        }
        if(sender instanceof Player) {
            sender.sendMessage(plugin.formatMsg("NO_PERMISSION"));
            return true;
        }
        if(args.length == 1) {
            int percentage = Integer.parseInt(args[0]);
            plugin.getStoreUtils().setSalePercentage(percentage);
            plugin.getStoreUtils().setSaleConfigPercentage(percentage);
            plugin.getStoreUtils().setSaleStatus(true);
            for(String line : plugin.getConfig().getStringList("MESSAGES.SALE_ACTIVATED")) {
                Bukkit.broadcastMessage(plugin.colourize(line).replace("%sale%", percentage+""));
            }
            return true;
        }
        sender.sendMessage(plugin.colourize("&cUsage: /activatesalebridge <percentage>"));
        return true;
    }
}
