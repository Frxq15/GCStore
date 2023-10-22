package net.guildcraft.gcstore.command;

import net.guildcraft.gcstore.GCStore;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.Date;

public class logstoreCommand implements CommandExecutor {
    private GCStore plugin = GCStore.getInstance();
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if(!sender.hasPermission("gcstore.logstore")) {
            sender.sendMessage(plugin.formatMsg("NO_PERMISSION"));
            return true;
        }
        if(args.length == 3) {
            String user = args[0];
            String id = args[1];
            String packageName = args[2];
            createLog(user, id, packageName);
            return true;
        }
        return true;
    }
    public void createLog(String user, String id, String packageName) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat formatter2 = new SimpleDateFormat("HH:mm:ss");
        Date time = new Date(System.currentTimeMillis());
        plugin.getFileManager().getConfirmationsFile().set(user+"."+formatter.format(date)+"."+formatter2.format(time)+".PACKAGE_ID", id);
        plugin.getFileManager().getConfirmationsFile().set(user+"."+formatter.format(date)+"."+formatter2.format(time)+".BOUGHT_FOR", user);
        plugin.getFileManager().getConfirmationsFile().set(user+"."+formatter.format(date)+"."+formatter2.format(time)+".PACKAGE_NAME", packageName);
        plugin.getFileManager().saveConfirmationsFile();
    }
}
