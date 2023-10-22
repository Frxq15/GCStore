package net.guildcraft.gcstore.api;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.guildcraft.gcstore.GCStore;
import net.guildcraft.gcstore.data.GPlayer;
import org.bukkit.entity.Player;

public class Placeholders extends PlaceholderExpansion {
    private final GCStore instance = GCStore.getInstance();

    public String getAuthor() {
        return "Frxq15";
    }

    @Override
    public String getIdentifier() {
        return "gcstore";
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }

    @Override
    public boolean persist() {
        return true; // This is required or else PlaceholderAPI will unregister the Expansion on reload
    }
    @Override
    public String onPlaceholderRequest(Player player, String placeholder) {
        GPlayer playerData = GPlayer.getPlayerData(instance, player.getUniqueId());
        switch(placeholder.toLowerCase()) {
            case "gbucks":
                return String.valueOf(playerData.getCredits());
            case "gbucks_formatted":
                return instance.format(playerData.getCredits());
            default:
                return "Invalid Placeholder";
        }
    }
}
