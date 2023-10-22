package net.guildcraft.gcstore.file;

import net.guildcraft.gcstore.GCStore;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class FileManager {
    private GCStore plugin = GCStore.getInstance();
    public File CategoriesFile;
    public FileConfiguration CategoriesConfig;

    public File ServersFile;
    public FileConfiguration ServersConfig;

    public File ConfirmFile;
    public FileConfiguration ConfirmConfig;
    public File PackagesFile;
    public FileConfiguration PackagesConfig;

    public File LogsFile;
    public FileConfiguration LogsConfig;

    public File ConfirmationsFile;
    public FileConfiguration ConfirmationsConfig;

    public void generate() {
        createCategoriesFile();
        createServersFile();
        createConfirmFile();
        createLogsFile();
        createConfirmationsFile();
        createPackagesFile("prison", "prison-ranks");
        createPackagesFile("prison", "prison-cosmetic-ranks");
        createPackagesFile("prison", "prison-rank-upgrades");
        createPackagesFile("prison", "prison-particles");
        createPackagesFile("prison", "prison-pets");
        createPackagesFile("prison", "prison-crate-keys");
        createPackagesFile("prison", "prison-companions");
        createPackagesFile("prison", "prison-titles");
        createPackagesFile("survival", "survival-ranks");
        createPackagesFile("survival", "survival-cosmetic-ranks");
        createPackagesFile("survival", "survival-rank-upgrades");
        createPackagesFile("survival", "survival-particles");
        createPackagesFile("survival", "survival-pets");
        createPackagesFile("survival", "survival-crate-keys");
        createPackagesFile("survival", "survival-companions");
        createPackagesFile("survival", "survival-titles");
    }
    public FileConfiguration getFileFromServerCategory(String type) {
        switch(type) {
            case "PRISONRANKS":
                return getPackagesFile("prison", "prison-ranks");
            case "SURVIVALRANKS":
                return getPackagesFile("survival", "survival-ranks");
            case "PRISONCOSMETIC_RANKS":
                return getPackagesFile("prison", "prison-cosmetic-ranks");
            case "SURVIVALCOSMETIC_RANKS":
                return getPackagesFile("survival", "survival-cosmetic-ranks");
            case "PRISONRANK_UPGRADES":
                return getPackagesFile("prison", "prison-rank-upgrades");
            case "SURVIVALRANK_UPGRADES":
                return getPackagesFile("survival", "survival-rank-upgrades");
            case "PRISONPARTICLES":
                return getPackagesFile("prison", "prison-particles");
            case "SURVIVALPARTICLES":
                return getPackagesFile("survival", "survival-particles");
            case "PRISONPETS":
                return getPackagesFile("prison", "prison-pets");
            case "SURVIVALPETS":
                return getPackagesFile("survival", "survival-pets");
            case "PRISONCRATE_KEYS":
                return getPackagesFile("prison", "prison-crate-keys");
            case "SURVIVALCRATE_KEYS":
                return getPackagesFile("survival", "survival-crate-keys");
            case "PRISONCOMPANIONS":
                return getPackagesFile("prison", "prison-companions");
            case "SURVIVALCOMPANIONS":
                return getPackagesFile("survival", "survival-companions");
            case "PRISONTITLES":
                return getPackagesFile("prison", "prison-titles");
            case "SURVIVALTITLES":
                return getPackagesFile("survival", "survival-titles");

            default:
                return getCategoriesFile();
        }
    }
    public void createCategoriesFile() {
        CategoriesFile = new File(plugin.getDataFolder()+"/guis", "categories.yml");
        if (!CategoriesFile.exists()) {
            CategoriesFile.getParentFile().mkdirs();
            plugin.log("categories.yml was created successfully");
            plugin.saveResource("guis/categories.yml", false);
        }
        CategoriesConfig = new YamlConfiguration();
        try {
            CategoriesConfig.load(CategoriesFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }
    public void reloadCategoriesFile() { CategoriesConfig = YamlConfiguration.loadConfiguration(CategoriesFile); }
    public void saveCategoriesFile() {
        try {
            CategoriesConfig.save(CategoriesFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void createConfirmationsFile() {
        ConfirmationsFile = new File(plugin.getDataFolder()+"/logs", "purchase-confirmations.yml");
        if (!ConfirmationsFile.exists()) {
            ConfirmationsFile.getParentFile().mkdirs();
            plugin.log("purchase-confirmations.yml was created successfully");
            plugin.saveResource("logs/purchase-confirmations.yml", false);
        }
        ConfirmationsConfig = new YamlConfiguration();
        try {
            ConfirmationsConfig.load(ConfirmationsFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }
    public void reloadConfirmationsFile() { ConfirmationsConfig = YamlConfiguration.loadConfiguration(ConfirmationsFile); }
    public void saveConfirmationsFile() {
        try {
            ConfirmationsConfig.save(ConfirmationsFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public FileConfiguration getConfirmationsFile() { return ConfirmationsConfig; }
    public void createLogsFile() {
        LogsFile = new File(plugin.getDataFolder()+"/logs", "logs.yml");
        if (!LogsFile.exists()) {
            LogsFile.getParentFile().mkdirs();
            plugin.log("logs.yml was created successfully");
            plugin.saveResource("logs/logs.yml", false);
        }
        LogsConfig = new YamlConfiguration();
        try {
            LogsConfig.load(LogsFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }
    public void reloadLogsFile() { LogsConfig = YamlConfiguration.loadConfiguration(LogsFile); }
    public void saveLogsFile() {
        try {
            LogsConfig.save(LogsFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public FileConfiguration getLogsFile() { return LogsConfig; }
    public FileConfiguration getCategoriesFile() { return CategoriesConfig; }

    public void createServersFile() {
        ServersFile = new File(plugin.getDataFolder()+"/guis", "servers.yml");
        if (!ServersFile.exists()) {
            ServersFile.getParentFile().mkdirs();
            plugin.log("servers.yml was created successfully");
            plugin.saveResource("guis/servers.yml", false);
        }
        ServersConfig = new YamlConfiguration();
        try {
            ServersConfig.load(ServersFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }
    public void reloadServersFile() { ServersConfig = YamlConfiguration.loadConfiguration(ServersFile); }
    public void saveServersFile() {
        try {
            ServersConfig.save(ServersFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public FileConfiguration getServersFile() { return ServersConfig; }

    public void createPackagesFile(String server, String file) {
        PackagesFile = new File(plugin.getDataFolder()+"/guis/packages/"+server.toLowerCase(), file.toLowerCase()+".yml");
        if (!PackagesFile.exists()) {
            PackagesFile.getParentFile().mkdirs();
            plugin.log(file.toLowerCase()+".yml was created successfully");
            plugin.saveResource("guis/packages/"+server.toLowerCase()+"/"+file.toLowerCase()+".yml", false);
        }
        PackagesConfig = new YamlConfiguration();
        try {
            PackagesConfig.load(PackagesFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }
    public void reloadPackagesFile(String server, String file) {
        PackagesFile = new File(plugin.getDataFolder()+"/guis/packages/"+server.toLowerCase(), file.toLowerCase()+".yml");
        PackagesConfig = YamlConfiguration.loadConfiguration(PackagesFile);
    }
    public void savePackagesFile(String server, String file) {
        PackagesFile = new File(plugin.getDataFolder()+"/guis/packages/"+server.toLowerCase(), file.toLowerCase()+".yml");
        try {
            PackagesConfig.save(PackagesFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public FileConfiguration getPackagesFile(String server, String file) {
        PackagesFile = new File(plugin.getDataFolder()+"/guis/packages/"+server.toLowerCase(), file.toLowerCase()+".yml");
        try {
            PackagesConfig.load(PackagesFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
        return PackagesConfig;
        }
    public void createConfirmFile() {
        ConfirmFile = new File(plugin.getDataFolder()+"/guis", "confirmation.yml");
        if (!ConfirmFile.exists()) {
            ConfirmFile.getParentFile().mkdirs();
            plugin.log("confirmation.yml was created successfully");
            plugin.saveResource("guis/confirmation.yml", false);
        }
        ConfirmConfig = new YamlConfiguration();
        try {
            ConfirmConfig.load(ConfirmFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }
    public void reloadConfirmFile() { ConfirmConfig = YamlConfiguration.loadConfiguration(ConfirmFile); }
    public void saveConfirmFile() {
        try {
            ConfirmConfig.save(ConfirmFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public FileConfiguration getConfirmFile() { return ConfirmConfig; }
    }
