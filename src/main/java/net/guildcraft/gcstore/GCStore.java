package net.guildcraft.gcstore;

import net.guildcraft.gcstore.command.*;
import net.guildcraft.gcstore.data.GPlayer;
import net.guildcraft.gcstore.data.SQLListeners;
import net.guildcraft.gcstore.data.SQLManager;
import net.guildcraft.gcstore.data.SQLSetterGetter;
import net.guildcraft.gcstore.file.FileManager;
import net.guildcraft.gcstore.gui.GUIListeners;
import net.guildcraft.gcstore.utils.StoreUtils;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.text.NumberFormat;
import java.util.Locale;

public final class GCStore extends JavaPlugin {
    private static GCStore instance;
    private FileManager fileManager;
    private StoreUtils storeUtils;
    private SQLManager sqlManager;

    private Permission perms = null;
    private SQLSetterGetter sqlUtils;
    private int savingTask;

    private final NumberFormat NUMBER_FORMAT = NumberFormat.getNumberInstance(Locale.ENGLISH);

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        initialize();
        // Plugin startup logic
        log("Plugin enabled.");
    }

    @Override
    public void onDisable() {
        Bukkit.getScheduler().cancelTask(savingTask);
        savingTask = 0;
        GPlayer.getAllPlayerData().forEach((uuid, playerData) -> getSQLUtils().updateCredits(uuid, playerData.getCredits()));
        // Plugin shutdown logic
        log("Plugin disabled.");
    }
    public static GCStore getInstance() { return instance; }

    public void initialize() {
        sqlManager = new SQLManager(this);
        if (!sqlManager.connect()) {
            log("Failed to connect to MySQL, disabling plugin.");
            Bukkit.getPluginManager().disablePlugin(this);
        }
        fileManager = new FileManager();
        getFileManager().generate();
        storeUtils = new StoreUtils();
        getStoreUtils().initialize();
        sqlUtils = new SQLSetterGetter();
        Bukkit.getPluginManager().registerEvents(new GUIListeners(), this);
        Bukkit.getPluginManager().registerEvents(new SQLListeners(), this);
        getCommand("vstore").setExecutor(new vstoreCommand());
        getCommand("credits").setExecutor(new creditsCommand());
        getCommand("activatesale").setExecutor(new activatesaleCommand());
        getCommand("activatesalebridge").setExecutor(new activatesalebridgeCommand());
        getCommand("disablesale").setExecutor(new disablesaleCommand());
        getCommand("disablesalebridge").setExecutor(new disablesalebridgeCommand());
        savingTask = startSavingTask();
        setupVault();
        }

    public FileManager getFileManager() {
        return fileManager;
    }
    public StoreUtils getStoreUtils() { return storeUtils; }

    public SQLManager getSQLManager() { return sqlManager; }
    public SQLSetterGetter getSQLUtils() { return sqlUtils; }

    public void log(String msg) { Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA+"[GCStore] "+msg); }
    public String colourize(String msg) { return ChatColor.translateAlternateColorCodes('&', msg); }
    public String formatMsg(String msg) { return ChatColor.translateAlternateColorCodes('&', getInstance().getConfig().getString("MESSAGES."+msg)); }

    private int startSavingTask() {
        return Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> {
            GPlayer.getAllPlayerData().forEach((uuid, player) -> {
                player.uploadPlayerData(this);
            });
        }, 20L * 60L * 5, 20L * 60L * 10).getTaskId();
    }
    public String format(int number) {
        return NUMBER_FORMAT.format(number);
    }

    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        perms = rsp.getProvider();
        return perms != null;
    }
    public Permission getPermissions() {
        return perms;
    }
    public void setupVault() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            log("Vault depedency not found, disabling plugin.");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        setupPermissions();
    }
}
