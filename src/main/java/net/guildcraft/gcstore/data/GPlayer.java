package net.guildcraft.gcstore.data;

import net.guildcraft.gcstore.GCStore;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GPlayer {

    private GCStore plugin = GCStore.getInstance();
    private final static Map<UUID, GPlayer> players = new HashMap<>();

    private UUID uuid;
    private int credits;


    public GPlayer(UUID uuid) {
        this.uuid = uuid;
        players.put(uuid, this);
    }
    public static GPlayer getPlayerData(GCStore plugin, UUID uuid) {
        if (!players.containsKey(uuid)) {
            GPlayer gPlayer = new GPlayer(uuid);
            gPlayer.setCredits(plugin.getSQLUtils().getCredits(uuid));
        }
        return players.get(uuid);
    }
    public void setCredits(int amount) {
        this.credits = amount;
    }
    public void addCredits(int amount) {
        this.credits += amount;
    }
    public void removeCredits(int amount) {
        this.credits -= amount;
    }
    public int getCredits() {
        return credits;
    }
    public String getPlayerName() {
        return Bukkit.getOfflinePlayer(uuid).getName();
    }
    public void uploadPlayerData(GCStore plugin) {
       Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> plugin.getSQLUtils().updateCredits(uuid, credits));
    }

    public static Map<UUID, GPlayer> getAllPlayerData() {
        return players;
    }
    public static void removePlayerData(UUID uuid) { players.remove(uuid); }
    public UUID getUUID() { return uuid;}

}

