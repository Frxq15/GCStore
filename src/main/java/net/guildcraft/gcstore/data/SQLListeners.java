package net.guildcraft.gcstore.data;

import net.guildcraft.gcstore.GCStore;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class SQLListeners implements Listener {
    private GCStore plugin = GCStore.getInstance();

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onAsyncPlayerPreLoginEvent(AsyncPlayerPreLoginEvent event) {
        UUID uuid = event.getUniqueId();
        plugin.getSQLUtils().createPlayer(uuid);
        GPlayer playerData = GPlayer.getPlayerData(plugin, uuid);
    }
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerQuitEvent(PlayerQuitEvent event) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            UUID uuid = event.getPlayer().getUniqueId();
            GPlayer playerData = GPlayer.getPlayerData(plugin, uuid);
            playerData.uploadPlayerData(plugin);
            GPlayer.removePlayerData(uuid);
        });
    }
}
