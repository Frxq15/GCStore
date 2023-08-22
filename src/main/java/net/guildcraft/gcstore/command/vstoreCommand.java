package net.guildcraft.gcstore.command;

import net.guildcraft.gcstore.GCStore;
import net.guildcraft.gcstore.gui.menus.CategoriesGUI;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class vstoreCommand implements CommandExecutor {
    private GCStore plugin = GCStore.getInstance();
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if(!(sender instanceof Player)) {
            plugin.log("This command cannot be executed from console.");
            return true;
        }
        Player p = (Player)sender;
        if(!sender.hasPermission("gcstore.vstore")) {
            sender.sendMessage(plugin.formatMsg("NO_PERMISSION"));
            return true;
        }
        if(args.length == 0) {
            new CategoriesGUI(plugin, p.getName(), p).open(p);
            return true;
        }
        if(args.length == 1) {
            String target = args[0];

            if(Bukkit.getPlayer(target) == null) {
                p.sendMessage(plugin.formatMsg("PLAYER_NOT_ONLINE"));
                return true;
            }
            new CategoriesGUI(plugin, target, p).open(p);
            return true;
        }
        p.sendMessage(plugin.colourize("&cUsage: /vstore <player>"));
        return true;
    }
}
