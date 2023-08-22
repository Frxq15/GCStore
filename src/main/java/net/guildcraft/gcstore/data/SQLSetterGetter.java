package net.guildcraft.gcstore.data;

import net.guildcraft.gcstore.GCStore;
import org.bukkit.Bukkit;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SQLSetterGetter {
    private GCStore plugin = GCStore.getInstance();
    private SQLManager sqlManager = plugin.getSQLManager();
    private final static Map<UUID, GPlayer> players = new HashMap<>();

    public boolean playerExists(UUID uuid) {
        try {
            PreparedStatement statement = sqlManager.getConnection().prepareStatement("SELECT * FROM users WHERE uuid=?");
            statement.setString(1, uuid.toString());
            ResultSet results = statement.executeQuery();
            if (results.next()) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    public void createTable(String table) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin.getInstance(), () -> {
            try {
                PreparedStatement statement = sqlManager.getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS `" + table + "` (uuid VARCHAR(36) PRIMARY KEY, credits INT(11), email VARCHAR(255));");
                statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }
    public void createPlayer(final UUID uuid) {
        if (!sqlManager.isConnected() && !sqlManager.connect()) {
            plugin.getLogger().severe("Can't establish a database connection!");
            return;
        }
        if(playerExists(uuid)) {
            return;
        }
        try {
                PreparedStatement insert = sqlManager.getConnection()
                        .prepareStatement("INSERT INTO users (uuid,credits,email) VALUES (?,?,?)");
                insert.setString(1, uuid.toString());
                insert.setInt(2, 0);
                insert.setString(3, null);
                insert.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void updateCredits(UUID uuid, int credits) {
        if (!sqlManager.isConnected() && !sqlManager.connect()) {
            plugin.getLogger().severe("Can't establish a database connection!");
            return;
        }
        if(!playerExists(uuid)) {
            plugin.log("An error whilst updating data for uuid "+uuid+", please contact the developer about this error.");
            return;
        }
        try {
            PreparedStatement statement = sqlManager.getConnection().prepareStatement("UPDATE users SET credits=? WHERE uuid=?");
            statement.setInt(1, credits);
            statement.setString(2, uuid.toString());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void updateGPlayerData(GPlayer gPlayer) {
        if (!sqlManager.isConnected() && !sqlManager.connect()) {
            plugin.getLogger().severe("Can't establish a database connection!");
            return;
        }
        if(!playerExists(gPlayer.getUUID())) {
            plugin.log("An error whilst updating data for uuid "+gPlayer.getUUID().toString()+", please contact the developer about this error.");
            return;
        }
        try {
            PreparedStatement statement = sqlManager.getConnection().prepareStatement("UPDATE users SET credits=? WHERE uuid=?");
            statement.setInt(1, gPlayer.getCredits());
            statement.setString(2, gPlayer.getUUID().toString());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public int getCredits(UUID uuid) {
        if (!sqlManager.isConnected() && !sqlManager.connect()) {
            plugin.getLogger().severe("Can't establish a database connection!");
            return 0;
        }
        if(!playerExists(uuid)) {
            plugin.log("An error whilst updating data for uuid "+uuid+", please contact the developer about this error.");
            return 0;
        }
        try {
            PreparedStatement statement = sqlManager.getConnection().prepareStatement("SELECT credits FROM users WHERE uuid=?");
            statement.setString(1, uuid.toString());
            ResultSet results = statement.executeQuery();
            results.next();
            return results.getInt("credits");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
