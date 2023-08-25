package net.guildcraft.gcstore.gui.menus;

import net.guildcraft.gcstore.GCStore;
import net.guildcraft.gcstore.data.GPlayer;
import net.guildcraft.gcstore.gui.GUITemplate;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class PackagesGUI extends GUITemplate {

    private final GCStore plugin;
    private final FileConfiguration packages;
    private final String target;
    private final String category;
    private final String server;
    private final String server_formatted;

    private final GPlayer gPlayer;
    private final String type;
    public PackagesGUI(GCStore plugin, Player player, String target, String category, String server, String type) {
        super(plugin,
                plugin.getFileManager().getFileFromServerCategory(type).getInt("ROWS"),
                plugin.getFileManager().getFileFromServerCategory(type).getString("TITLE"));
        this.plugin = plugin;
        this.packages = plugin.getFileManager().getFileFromServerCategory(type);
        //"PRISON-RANKS"
        this.target = target;
        this.category = category;
        this.server = server;
        this.type = type;
        this.server_formatted = server.toLowerCase().substring(0, 1).toUpperCase() + server.substring(1);
        this.gPlayer = GPlayer.getPlayerData(plugin, player.getUniqueId());
        initialize();
    }
    public void initialize() {
        setItem(packages.getInt("PURCHASING_FOR.SLOT"), createSkull(), p -> {
            p.getOpenInventory().close();
            p.sendMessage(plugin.colourize("&cUsage: /vstore <player>"));
        });
        packages.getConfigurationSection("MISC_ITEMS").getKeys(false).forEach(item -> {
            if(item.equals("SALE")) {
                setItem(getItemSlot(item, true), createMiscItem(item), p -> {
                    p.getOpenInventory().close();
                    p.sendMessage(plugin.formatMsg("DISCORD"));
                });
            } if(item.equals("CLOSE")) {
                setItem(getItemSlot(item, true), createMiscItem(item), p -> {
                    p.getOpenInventory().close();
                    new ServersGUI(plugin, p, target, category).open(p);
                });
            }
            else {
                setItem(getItemSlot(item, true), createMiscItem(item));
            }
        });
        setItem(packages.getInt("CATEGORY.SLOT"), createCategoryItem(category), p -> {
            if(Bukkit.getPlayer(target) == null) {
                p.sendMessage(plugin.formatMsg("PLAYER_WENT_OFFLINE"));
                p.getOpenInventory().close();
                return;
            }
            p.getOpenInventory().close();
            new CategoriesGUI(plugin, target, p).open(p);
        });
        setItem(packages.getInt("SERVER.SLOT"), createServerItem(server), p -> {
            if(Bukkit.getPlayer(target) == null) {
                p.sendMessage(plugin.formatMsg("PLAYER_WENT_OFFLINE"));
                p.getOpenInventory().close();
                return;
            }
            p.getOpenInventory().close();
            new ServersGUI(plugin, p, target, category).open(p);
        });
        packages.getConfigurationSection("ITEMS").getKeys(false).forEach(item -> {
            if(!isEnabled(item)) {
                setItem(getItemSlot(item, false), createItem(item), p -> {
                        p.getOpenInventory().close();
                        p.sendMessage(plugin.formatMsg("PACKAGE_DISABLED"));
                        return;
                 });
                }
            if(isEnabled(item)) {
                    setItem(getItemSlot(item, false), createItem(item), p -> {
                        if(!server.equalsIgnoreCase(plugin.getConfig().getString("SERVER"))) {
                            p.getOpenInventory().close();
                            String one = server.toLowerCase();
                            String two = one.substring(0, 1).toUpperCase() +one.substring(1);
                            p.sendMessage(plugin.formatMsg("CHANGE_SERVER").replace("%server%", two));
                            return;
                        }
                        Player player;
                        if(Bukkit.getPlayer(target) == null) {
                            p.getOpenInventory().close();
                            p.sendMessage(plugin.formatMsg("PLAYER_WENT_OFFLINE"));
                            return;
                        }
                        player = Bukkit.getPlayer(target);
                        if(!player.hasPermission(packages.getString("ITEMS."+item+".PERMISSION"))) {
                            if(player.getName().equalsIgnoreCase(p.getName())) {
                                p.getOpenInventory().close();
                                p.sendMessage(plugin.formatMsg("PLAYER_INVALID_REQUIREMENTS"));
                                return;
                            }
                            p.getOpenInventory().close();
                            p.sendMessage(plugin.formatMsg("TARGET_INVALID_REQUIREMENTS"));
                            return;
                        }
                        p.getOpenInventory().close();
                        new ConfirmGUI(plugin, p, target, category, server, type, createItem(item), getCostWithSale(item), packages.getInt("ITEMS."+item+".PACKAGE_ID")).open(p);
                    });
            }
        });
    }
    public Integer getItemSlot(String item, boolean misc) {
        if(misc) {
            return packages.getInt("MISC_ITEMS."+item+".SLOT");
        }
        return packages.getInt("ITEMS."+item+".SLOT");
    }
    public int getCost(String item) {
        int cost = packages.getInt("ITEMS."+item+".COST");
        return plugin.getStoreUtils().getNewCost(cost);
    }
    public int getCostWithSale(String item) {
        return plugin.getStoreUtils().getNewCost(getCost(item));
    }
    public boolean isEnabled(String item) {
        return packages.getBoolean("ITEMS."+item+".ENABLED");
    }
    public boolean hasGlow(String item, boolean misc) {
        if(misc) {
            return packages.getBoolean("MISC_ITEMS."+item+".GLOW");
        }
        return packages.getBoolean("ITEMS."+item+".GLOW");
    }
    public boolean hasGlowCategoryItem(String item) {
        return plugin.getFileManager().getCategoriesFile().getBoolean("ITEMS."+item+".GLOW");
    }
    public ItemStack createItem(String item) {
        //normal item creation
        List<String> lore = new ArrayList<String>();

        String material = packages.getString("ITEMS." + item + ".MATERIAL");
        int amount = packages.getInt("ITEMS." + item + ".AMOUNT");
        final ItemStack i = new ItemStack(Material.valueOf(material), amount);
        String name = packages.getString("ITEMS." + item + ".NAME");

        final ItemMeta meta = i.getItemMeta();

        if(plugin.getStoreUtils().isSaleEnabled()) {
            for (String lines : packages.getStringList("ITEMS." + item + ".SALE_LORE")) {
                lines = lines.replace("%cost%", plugin.getStoreUtils().getBuffedCost(getCost(item))+"")
                        .replace("%salecost%", getCostWithSale(item)+"")
                        .replace("%salepercentage%", plugin.getStoreUtils().getSalePercentage()+"");
                lore.add(plugin.colourize(lines));
            }
        } else {
            for (String lines : packages.getStringList("ITEMS." + item + ".LORE")) {
                lines = lines.replace("%cost%", getCostWithSale(item)+"");
                lore.add(plugin.colourize(lines));
            }
        }
        meta.setDisplayName(plugin.colourize(name));
        if (hasGlow(item, false)) {
            meta.addEnchant(Enchantment.DURABILITY, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.setLore(lore);
        i.setItemMeta(meta);
        return i;
    }
    public ItemStack createCategoryItem(String item) {
        //normal item creation
        List<String> lore = new ArrayList<String>();

        String material = plugin.getFileManager().getCategoriesFile().getString("ITEMS." + item + ".MATERIAL");
        final ItemStack i = new ItemStack(Material.valueOf(material), 1);
        String name = plugin.getFileManager().getCategoriesFile().getString("ITEMS." + item + ".NAME");

        final ItemMeta meta = i.getItemMeta();
        for (String lines : packages.getStringList("CATEGORY.LORE")) {
            lore.add(plugin.colourize(lines));
        }
        meta.setDisplayName(plugin.colourize(name));
        if (hasGlowCategoryItem(item)) {
            meta.addEnchant(Enchantment.DURABILITY, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.setLore(lore);
        i.setItemMeta(meta);
        return i;
    }
    public ItemStack createServerItem(String item) {
        //normal item creation
        List<String> lore = new ArrayList<String>();

        String material = plugin.getFileManager().getServersFile().getString("ITEMS." + item + ".MATERIAL");
        final ItemStack i = new ItemStack(Material.valueOf(material), 1);
        String name = plugin.getFileManager().getServersFile().getString("ITEMS." + item + ".NAME");

        final ItemMeta meta = i.getItemMeta();
        for (String lines : packages.getStringList("SERVER.LORE")) {
            lore.add(plugin.colourize(lines));
        }
        meta.setDisplayName(plugin.colourize(name));
        if (hasGlowCategoryItem(item)) {
            meta.addEnchant(Enchantment.DURABILITY, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.setLore(lore);
        i.setItemMeta(meta);
        return i;
    }
    public ItemStack createSkull() {
        List<String> lore = new ArrayList<String>();
        ItemStack i = new ItemStack(Material.PLAYER_HEAD, 1);
        SkullMeta meta = (SkullMeta) i.getItemMeta();
        meta.setOwner(target);
        i.setItemMeta(meta);
        String n = packages.getString("PURCHASING_FOR.NAME");
        n = n.replace("%player%", target);
        String name = plugin.colourize(n);
        for (String lines : packages.getStringList("PURCHASING_FOR.LORE")) {
            lines = lines.replace("%player%", target);
            lore.add(plugin.colourize(lines));
        }
        meta.setLore(lore);
        meta.setDisplayName(name);
        i.setItemMeta(meta);

        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);

        return i;
    }
    public ItemStack createMiscItem(String item) {
        // misc item creation
        List<String> lore = new ArrayList<String>();

        String material = packages.getString("MISC_ITEMS." + item + ".MATERIAL");
        Integer amount = packages.getInt("MISC_ITEMS." + item + ".AMOUNT");
        final ItemStack i = new ItemStack(Material.valueOf(material), amount);
        String name = packages.getString("MISC_ITEMS." + item + ".NAME").replace("%gbucks%", plugin.format(gPlayer.getCredits()));;

        final ItemMeta meta = i.getItemMeta();
        for (String lines : packages.getStringList("MISC_ITEMS." + item + ".LORE")) {
            lines = lines.replace("%gbucks%", plugin.format(gPlayer.getCredits()));
            if(item.equalsIgnoreCase("SALE")) {
                if(plugin.getStoreUtils().getSalePercentage() == 0) {
                    lines = lines.replace("%sale_status%", "&cNo sale active.");
                } else {
                    lines = lines.replace("%sale_status%", "&6&l"+plugin.getStoreUtils().getSalePercentage()+"% OFF");
                }
            }
            lore.add(plugin.colourize(lines));
        }
        meta.setDisplayName(plugin.colourize(name));
        if (hasGlow(item, true)) {
            meta.addEnchant(Enchantment.DURABILITY, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        meta.setLore(lore);
        i.setItemMeta(meta);
        return i;
    }
}
