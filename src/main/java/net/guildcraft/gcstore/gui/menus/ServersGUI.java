package net.guildcraft.gcstore.gui.menus;

import net.guildcraft.gcstore.GCStore;
import net.guildcraft.gcstore.data.GPlayer;
import net.guildcraft.gcstore.gui.GUITemplate;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;

public class ServersGUI extends GUITemplate {
    private final GCStore plugin;
    private final FileConfiguration servers;
    private final String target;
    private final String category;
    private final GPlayer gPlayer;
    public ServersGUI(GCStore plugin, Player player, String target, String category) {
        super(plugin,
                plugin.getFileManager().getServersFile().getInt("ROWS"),
                plugin.getFileManager().getServersFile().getString("TITLE"));
        this.plugin = plugin;
        this.servers = plugin.getFileManager().getServersFile();
        this.target = target;
        this.category = category;
        this.gPlayer = GPlayer.getPlayerData(plugin, player.getUniqueId());
        initialize();
    }
    public void initialize() {
        setItem(servers.getInt("PURCHASING_FOR.SLOT"), createSkull(), p -> {
            p.getOpenInventory().close();
            p.sendMessage(plugin.colourize("&cUsage: /vstore <player>"));
        });
        servers.getConfigurationSection("ITEMS").getKeys(false).forEach(item -> {
            if(isEnabled(item)) {
                if(getApplicableServers().contains(item)) {
                    setItem(getItemSlot(item, false), createItem(item), p -> {
                        if(Bukkit.getPlayer(target) == null) {
                            p.sendMessage(plugin.formatMsg("PLAYER_WENT_OFFLINE"));
                            p.getOpenInventory().close();
                            return;
                        }

                        p.getOpenInventory().close();
                        String type = item.toUpperCase()+category.toUpperCase();
                        new PackagesGUI(plugin, p, target, category, item, type).open(p);
                    });
                    }
                }
        });
        servers.getConfigurationSection("MISC_ITEMS").getKeys(false).forEach(item -> {
            if(item.equals("SALE")) {
                setItem(getItemSlot(item, true), createMiscItem(item), p -> {
                    p.getOpenInventory().close();
                    p.sendMessage(plugin.formatMsg("DISCORD"));
                });
            } if(item.equals("CLOSE")) {
                    setItem(getItemSlot(item, true), createMiscItem(item), p -> {
                    p.getOpenInventory().close();
                    new CategoriesGUI(plugin, target, p).open(p);
                });
            }
            else {
                setItem(getItemSlot(item, true), createMiscItem(item));
            }
        });
        setItem(servers.getInt("CATEGORY.SLOT"), createCategoryItem(category), p -> {
            if(Bukkit.getPlayer(target) == null) {
                p.sendMessage(plugin.formatMsg("PLAYER_WENT_OFFLINE"));
                p.getOpenInventory().close();
                return;
            }
            p.getOpenInventory().close();
            new CategoriesGUI(plugin, target, p).open(p);
        });

    }
    public List<String> getApplicableServers() {
        return plugin.getFileManager().getCategoriesFile().getStringList("ITEMS."+category+".SERVERS");
    }
    public Integer getItemSlot(String item, boolean misc) {
        if(misc) {
            return servers.getInt("MISC_ITEMS."+item+".SLOT");
        }
        return servers.getInt("ITEMS."+item+".SLOT");
    }
    public boolean isEnabled(String item) {
        return servers.getBoolean("ITEMS."+item+".ENABLED");
    }
    public boolean hasGlow(String item, boolean misc) {
        if(misc) {
            return servers.getBoolean("MISC_ITEMS."+item+".GLOW");
        }
        return servers.getBoolean("ITEMS."+item+".GLOW");
    }
    public boolean hasGlowCategoryItem(String item) {
        return plugin.getFileManager().getCategoriesFile().getBoolean("ITEMS."+item+".GLOW");
    }
    public ItemStack createItem(String item) {
        //normal item creation
        List<String> lore = new ArrayList<String>();

        String material = servers.getString("ITEMS." + item + ".MATERIAL");
        Integer data = servers.getInt("ITEMS." + item + ".DATA");
        final ItemStack i = new ItemStack(Material.valueOf(material), 1, data.shortValue());
        String name = servers.getString("ITEMS." + item + ".NAME");

        final ItemMeta meta = i.getItemMeta();
        for (String lines : servers.getStringList("ITEMS." + item + ".LORE")) {
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
        Integer data = plugin.getFileManager().getCategoriesFile().getInt("ITEMS." + item + ".DATA");
        final ItemStack i = new ItemStack(Material.valueOf(material), 1, data.shortValue());
        String name = plugin.getFileManager().getCategoriesFile().getString("ITEMS." + item + ".NAME");

        final ItemMeta meta = i.getItemMeta();
        for (String lines : servers.getStringList("CATEGORY.LORE")) {
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
        ItemStack i = new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal());
        SkullMeta meta = (SkullMeta) i.getItemMeta();
        meta.setOwner(target);
        i.setItemMeta(meta);
        String n = servers.getString("PURCHASING_FOR.NAME");
        n = n.replace("%player%", target);
        String name = plugin.colourize(n);
        for (String lines : servers.getStringList("PURCHASING_FOR.LORE")) {
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

        String material = servers.getString("MISC_ITEMS." + item + ".MATERIAL");
        Integer amount = servers.getInt("MISC_ITEMS." + item + ".AMOUNT");
        final ItemStack i = new ItemStack(Material.valueOf(material), amount);
        String name = servers.getString("MISC_ITEMS." + item + ".NAME").replace("%gbucks%", plugin.format(gPlayer.getCredits()));

        final ItemMeta meta = i.getItemMeta();
        for (String lines : servers.getStringList("MISC_ITEMS." + item + ".LORE")) {
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
