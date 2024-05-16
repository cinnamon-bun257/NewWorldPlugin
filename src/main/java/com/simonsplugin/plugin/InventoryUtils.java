package com.simonsplugin.plugin;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class InventoryUtils {
    public static void saveInventory(JavaPlugin plugin, Player player){
        File inventoryFile = new File(plugin.getDataFolder(), player.getUniqueId().toString() + "_inventory.yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(inventoryFile);

        PlayerInventory inventory = player.getInventory();
        String path = player.getWorld().getName();
        config.set(path + ".inventory", inventory.getContents());
        config.set(path + ".armor", inventory.getArmorContents());

        try {
            config.save(inventoryFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void setInventory (JavaPlugin plugin, Player player){
        File inventoryFile = new File(plugin.getDataFolder(), player.getUniqueId().toString() + "_inventory.yml");
        if (!inventoryFile.exists()) {
            return; // No saved inventory to restore
        }

        YamlConfiguration config = YamlConfiguration.loadConfiguration(inventoryFile);

        // Deserialize the inventory
        ItemStack[] inventoryContents = ((List<ItemStack>) config.get("inventory")).toArray(new ItemStack[0]);
        ItemStack[] armorContents = ((List<ItemStack>) config.get("armor")).toArray(new ItemStack[0]);

        PlayerInventory inventory = player.getInventory();
        inventory.setContents(inventoryContents);
        inventory.setArmorContents(armorContents);
    }
}
