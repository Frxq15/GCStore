package net.guildcraft.gcstore.command;

import net.guildcraft.gcstore.GCStore;
import net.guildcraft.gcstore.data.GPlayer;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class creditsCommand implements CommandExecutor {
    private GCStore plugin = GCStore.getInstance();
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if(!sender.hasPermission("gcstore.credits")) {
            sender.sendMessage(plugin.formatMsg("NO_PERMISSION"));
            return true;
        }
        if(args.length == 0) {
            if(!(sender instanceof Player)) {
                plugin.log("This command cannot be executed from console.");
                return true;
            }
            Player p = (Player)sender;
            GPlayer gPlayer = GPlayer.getPlayerData(plugin, p.getUniqueId());
            p.sendMessage(plugin.formatMsg("OWN_CREDITS").replace("%credits%", plugin.format(gPlayer.getCredits())));
            return true;
        }
        if(args.length == 1) {
            if(!sender.hasPermission("gcstore.credits.others")) {
                sender.sendMessage(plugin.formatMsg("NO_PERMISSION"));
                return true;
            }
            String t = args[0];

                OfflinePlayer target = Bukkit.getOfflinePlayer(t);

                if(!plugin.getSQLUtils().playerExists(target.getUniqueId())) {
                    sender.sendMessage(plugin.formatMsg("PLAYER_NOT_FOUND"));
                }
                GPlayer gPlayer = GPlayer.getPlayerData(plugin, target.getUniqueId());
                sender.sendMessage(plugin.formatMsg("OTHER_CREDITS")
                        .replace("%credits%", plugin.format(gPlayer.getCredits()))
                        .replace("%player%", target.getName()));

                if(!target.isOnline()) {
                    GPlayer.removePlayerData(target.getUniqueId());
                }
                return true;
        }
        if(args.length == 3) {
            if(!sender.hasPermission("gcstore.credits.manage")) {
                sender.sendMessage(plugin.formatMsg("NO_PERMISSION"));
                return true;
            }
            if(sender instanceof Player) {
                sender.sendMessage(plugin.colourize("&cUsage: /gbucks"));
                return true;
            }
            String type = args[0];
            OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
            int amount = Integer.parseInt(args[2]);

            if(!plugin.getSQLUtils().playerExists(target.getUniqueId())) {
                sender.sendMessage(plugin.formatMsg("PLAYER_NOT_FOUND"));
                return true;
            }
            GPlayer gPlayer = GPlayer.getPlayerData(plugin, target.getUniqueId());

            switch (type.toLowerCase()) {
                case "add":
                    gPlayer.addCredits(amount);
                    sender.sendMessage(plugin.formatMsg("CREDITS_GIVEN")
                            .replace("%player%", target.getName())
                            .replace("%credits%", amount+""));
                    gPlayer.uploadPlayerData(plugin);
                    if(!target.isOnline()) {
                        GPlayer.removePlayerData(target.getUniqueId());
                    }
                    return true;
                case "remove":
                    gPlayer.removeCredits(amount);
                    sender.sendMessage(plugin.formatMsg("CREDITS_REMOVED")
                            .replace("%player%", target.getName())
                            .replace("%credits%", amount+""));
                    gPlayer.uploadPlayerData(plugin);
                    if(!target.isOnline()) {
                        GPlayer.removePlayerData(target.getUniqueId());
                    }
                    return true;
                case "set":
                    gPlayer.setCredits(amount);
                    sender.sendMessage(plugin.formatMsg("CREDITS_SET")
                            .replace("%player%", target.getName())
                            .replace("%credits%", amount+""));
                    gPlayer.uploadPlayerData(plugin);
                    if(!target.isOnline()) {
                        GPlayer.removePlayerData(target.getUniqueId());
                    }
                    return true;
                default:
                    sender.sendMessage(plugin.colourize("&cUsage: /gbucks <set/add/remove> <player> <amount>"));
                    return true;

            }
        }
        sender.sendMessage(plugin.colourize("&cUsage: /gbucks"));
        return true;
    }
}
