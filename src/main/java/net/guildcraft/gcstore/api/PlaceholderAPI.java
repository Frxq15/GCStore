package net.guildcraft.gcstore.api;

import net.guildcraft.gcstore.GCStore;
import org.bukkit.Bukkit;

public class PlaceholderAPI {
    private final GCStore instance = GCStore.getInstance();

    public void setupPapi() {
        if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") == null) {
            instance.getInstance().log("PlaceholderAPI not found, disabling plugin.");
            Bukkit.getPluginManager().disablePlugin(instance);
            return;
        }
        new Placeholders().register();
        instance.getInstance().log("Hooked into PlaceholderAPI successfully");
    }
}
