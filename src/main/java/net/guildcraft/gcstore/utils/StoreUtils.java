package net.guildcraft.gcstore.utils;

import net.guildcraft.gcstore.GCStore;

public class StoreUtils {
    private GCStore plugin = GCStore.getInstance();
    private int salePercentage;


    public void initialize() {
        setSalePercentage(getSalePercentageFromConfig());
    }

    public int getSalePercentage() {
        return salePercentage;
    }
    public void setSalePercentage(int salePercentage) {
        this.salePercentage = salePercentage;
    }

    public void setSaleConfigPercentage(int saleConfigPercentage) {
        plugin.getConfig().set("options.sale.sale_discount_percentage", saleConfigPercentage);
        plugin.saveConfig();
        plugin.reloadConfig();
    }
    public int getSalePercentageFromConfig() {
        if(!plugin.getConfig().getBoolean("options.sale.enabled")) {
            return 0;
        }
        return plugin.getConfig().getInt("options.sale.sale_discount_percentage");
    }
    public int getNewCost(int original) {
        if(getSalePercentage() == 0) {
            return original;
        }
        int getPercentage = (original/100) * getSalePercentage();
        int newPrice = (original-getPercentage);
        return newPrice;
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
