package net.guildcraft.gcstore.gui.menus;

import net.guildcraft.gcstore.GCStore;
import net.guildcraft.gcstore.data.GPlayer;
import net.guildcraft.gcstore.gui.GUITemplate;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ConfirmGUI extends GUITemplate {
    private final GCStore plugin;
    private final FileConfiguration packages;
    private final String target;
    private final String category;
    private final String server;
    private final String server_formatted;
    private final ItemStack item;

    private final String type;
    private final GPlayer gPlayer;
    private final int cost;
    private final int packageid;
    public ConfirmGUI(GCStore plugin, Player player, String target, String category, String server, String type, ItemStack item, int cost, int packageid) {
        super(plugin,
                plugin.getFileManager().getConfirmFile().getInt("ROWS"),
                plugin.getFileManager().getConfirmFile().getString("TITLE"));
        this.plugin = plugin;
        this.packages = plugin.getFileManager().getConfirmFile();
        //"PRISON-RANKS"
        this.target = target;
        this.category = category;
        this.server = server;
        this.item = item;
        this.type = type;
        this.cost = cost;
        this.packageid = packageid;
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
                    new PackagesGUI(plugin, p, target, category, server, type).open(p);
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
        setItem(packages.getInt("PACKAGE.SLOT"), item, p -> {
            if(Bukkit.getPlayer(target) == null) {
                p.sendMessage(plugin.formatMsg("PLAYER_WENT_OFFLINE"));
                p.getOpenInventory().close();
                return;
            }
            p.getOpenInventory().close();
            runConfirm(p);
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
            if(item.equalsIgnoreCase("CONFIRM")) {
                for(String slot : packages.getStringList("ITEMS."+item+".SLOTS")) {
                    setItem(Integer.parseInt(slot), createItem(item), confirm -> {
                        if(Bukkit.getPlayer(target) == null) {
                            confirm.sendMessage(plugin.formatMsg("PLAYER_WENT_OFFLINE"));
                            confirm.getOpenInventory().close();
                            return;
                        }
                        confirm.getOpenInventory().close();
                        runConfirm(confirm);
                        return;
                    });
                }
            }
            if(item.equalsIgnoreCase("CANCEL")) {
                for(String slot : packages.getStringList("ITEMS."+item+".SLOTS")) {
                    setItem(Integer.parseInt(slot), createItem(item), cancel -> {
                        cancel.getOpenInventory().close();
                    });
                }
            }
        });
    }

    public void runConfirm(Player p) {
        if(gPlayer.getCredits() < cost) {
            p.sendMessage(plugin.formatMsg("INSUFFICIENT_FUNDS"));
            return;
        }
        gPlayer.removeCredits(cost);
        p.sendMessage(plugin.formatMsg("CREDITS_REMOVED_PLAYER").replace("%credits%", plugin.format(cost)));
        String one = server.toLowerCase();
        String two = one.substring(0, 1).toUpperCase() +one.substring(1);
        plugin.getConfig().getStringList("MESSAGES.PACKAGE_PURCHASED").forEach(line -> {
            line = line
                    .replace("%cost%", plugin.format(cost))
                    .replace("%player%", target)
                    .replace("%server%", two)
                    .replace("%package%", item.getItemMeta().getDisplayName());
            p.sendMessage(plugin.colourize(line));
        });
            String command = "applytotebex "+packageid+" "+target;
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
            createLog(p);
        return;
    }
    public void createLog(Player p) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat formatter2 = new SimpleDateFormat("HH:mm:ss");
        Date time = new Date(System.currentTimeMillis());
        plugin.getFileManager().getLogsFile().set(p.getName()+"."+formatter.format(date)+"."+formatter2.format(time)+".PACKAGE_ID", packageid);
        plugin.getFileManager().getLogsFile().set(p.getName()+"."+formatter.format(date)+"."+formatter2.format(time)+".BOUGHT_FOR", target);
        plugin.getFileManager().getLogsFile().set(p.getName()+"."+formatter.format(date)+"."+formatter2.format(time)+".COST", cost+" GBucks");
        plugin.getFileManager().getLogsFile().set(p.getName()+"."+formatter.format(date)+"."+formatter2.format(time)+".PACKAGE_NAME", item.getItemMeta().getDisplayName());
        plugin.getFileManager().saveLogsFile();
    }


    public Integer getItemSlot(String item, boolean misc) {
        if(misc) {
            return packages.getInt("MISC_ITEMS."+item+".SLOT");
        }
        return packages.getInt("ITEMS."+item+".SLOT");
    }
    public int getCost(String item) {
        return packages.getInt("ITEMS."+item+".COST");
    }
    public int getCostWithSale(String item) {
        return plugin.getStoreUtils().getNewCost(getCost(item));
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
            for (String lines : packages.getStringList("ITEMS." + item + ".LORE")) {
                lore.add(plugin.colourize(lines));
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
