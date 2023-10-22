package net.guildcraft.gcstore.gui.menus;

import net.guildcraft.gcstore.GCStore;
import net.guildcraft.gcstore.data.GPlayer;
import net.guildcraft.gcstore.gui.GUITemplate;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
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

public class CategoriesGUI extends GUITemplate {
    private final GCStore plugin;
    private final FileConfiguration categories;
    private final String target;
    private final GPlayer gPlayer;
    public CategoriesGUI(GCStore plugin, String target, Player player) {
        super(plugin,
                plugin.getFileManager().getCategoriesFile().getInt("ROWS"),
                plugin.getFileManager().getCategoriesFile().getString("TITLE"));
        this.plugin = plugin;
        this.categories = plugin.getFileManager().getCategoriesFile();
        this.target = target;
        this.gPlayer = GPlayer.getPlayerData(plugin, player.getUniqueId());
        initialize();
    }
    public void initialize() {
        setItem(categories.getInt("PURCHASING_FOR.SLOT"), createSkull(), p -> {
            p.getOpenInventory().close();
            p.sendMessage(plugin.colourize("&cUsage: /vstore <player>"));
        });
        categories.getConfigurationSection("ITEMS").getKeys(false).forEach(item -> {
            if(isEnabled(item)) {
                setItem(getItemSlot(item, false), createItem(item), p -> {
                    p.getOpenInventory().close();
                    new ServersGUI(plugin, p, target, item).open(p);
                });
            }
        });
        categories.getConfigurationSection("MISC_ITEMS").getKeys(false).forEach(item -> {
                if(item.equals("SALE")) {
                    setItem(getItemSlot(item, true), createMiscItem(item), p -> {
                        p.getOpenInventory().close();
                        p.sendMessage(plugin.formatMsg("DISCORD"));
                    });
                } else {
                    setItem(getItemSlot(item, true), createMiscItem(item));
                }
        });
    }
    public Integer getItemSlot(String item, boolean misc) {
        if(misc) {
            return categories.getInt("MISC_ITEMS."+item+".SLOT");
        }
        return categories.getInt("ITEMS."+item+".SLOT");
    }
    public boolean isEnabled(String item) {
        return categories.getBoolean("ITEMS."+item+".ENABLED");
    }
    public boolean hasGlow(String item, boolean misc) {
        if(misc) {
            return categories.getBoolean("MISC_ITEMS."+item+".GLOW");
        }
        return categories.getBoolean("ITEMS."+item+".GLOW");
    }
    public ItemStack createItem(String item) {
        //normal item creation
        List<String> lore = new ArrayList<String>();

        String material = categories.getString("ITEMS." + item + ".MATERIAL");
        Integer data = categories.getInt("ITEMS." + item + ".DATA");
        final ItemStack i = new ItemStack(Material.valueOf(material), 1, data.shortValue());
        String name = categories.getString("ITEMS." + item + ".NAME");

        final ItemMeta meta = i.getItemMeta();
        for (String lines : categories.getStringList("ITEMS." + item + ".LORE")) {
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
    public ItemStack createSkull() {
        List<String> lore = new ArrayList<String>();
        ItemStack i = new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal());
        SkullMeta meta = (SkullMeta) i.getItemMeta();
        meta.setOwner(target);
        i.setItemMeta(meta);
        String n = categories.getString("PURCHASING_FOR.NAME");
        n = n.replace("%player%", target);
        String name = plugin.colourize(n);
        for (String lines : categories.getStringList("PURCHASING_FOR.LORE")) {
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

        String material = categories.getString("MISC_ITEMS." + item + ".MATERIAL");
        Integer amount = categories.getInt("MISC_ITEMS." + item + ".AMOUNT");
        Integer data = categories.getInt("MISC_ITEMS." + item + ".DATA");
        final ItemStack i = new ItemStack(Material.valueOf(material), amount, data.shortValue());
        String name = categories.getString("MISC_ITEMS." + item + ".NAME").replace("%gbucks%", plugin.format(gPlayer.getCredits()));

        final ItemMeta meta = i.getItemMeta();
        for (String lines : categories.getStringList("MISC_ITEMS." + item + ".LORE")) {
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
