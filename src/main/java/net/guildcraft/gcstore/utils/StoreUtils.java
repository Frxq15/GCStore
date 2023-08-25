package net.guildcraft.gcstore.utils;

import net.guildcraft.gcstore.GCStore;
import org.bukkit.Bukkit;

public class StoreUtils {
    private GCStore plugin = GCStore.getInstance();
    private int salePercentage;
    private int boostPercentage;


    public void initialize() {
        setSalePercentage(getSalePercentageFromConfig());
        setBoostPercentage(getBoostPercentageFromConfig());
    }

    public int getSalePercentage() {
        return salePercentage;
    }
    public int getBoostPercentage() { return boostPercentage; }
    public void setSalePercentage(int salePercentage) {
        this.salePercentage = salePercentage;
    }
    public void setBoostPercentage(int boostPercentage) {
        this.boostPercentage = boostPercentage;
    }

    public void setSaleConfigPercentage(int saleConfigPercentage) {
        plugin.getConfig().set("options.sale.sale_discount_percentage", saleConfigPercentage);
        plugin.saveConfig();
        plugin.reloadConfig();
    }
    public int getBoostPercentageFromConfig() {
        if(!plugin.getConfig().getBoolean("options.price_modifier.enabled")) {
            return 1;
        }
        return plugin.getConfig().getInt("options.price_modifier.boost_percentage");
    }
    public int getSalePercentageFromConfig() {
        if(!plugin.getConfig().getBoolean("options.sale.enabled")) {
            return 0;
        }
        return plugin.getConfig().getInt("options.sale.sale_discount_percentage");
    }
    public int getNewCost(int original) {
        int newCost = ((original/100) * getBoostPercentage() + original);
        if(getSalePercentage() == 0) {
            return newCost;
        }
        int getPercentage = (newCost/100) * getSalePercentage();
        int newPrice = (newCost-getPercentage);
        return newPrice;
    }
    public int getBuffedCost(int original) {
        return ((original/100) * getBoostPercentage() + original);
    }
    public boolean isSaleEnabled() {
        return plugin.getConfig().getBoolean("options.sale.enabled");
    }
    public void setSaleStatus(boolean status) {
        plugin.getConfig().set("options.sale.enabled", status);
        plugin.saveConfig();
        plugin.reloadConfig();
    }
}
