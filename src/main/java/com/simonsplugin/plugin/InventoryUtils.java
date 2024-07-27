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
    public void saveInventory(JavaPlugin plugin, Player player){
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
    public void setInventory (JavaPlugin plugin, Player player, String worldName){
        File inventoryFile = new File(plugin.getDataFolder(), player.getUniqueId().toString() + "_inventory.yml");
        if (!inventoryFile.exists()) {
            return; // No saved inventory to restore
        }

        YamlConfiguration config = YamlConfiguration.loadConfiguration(inventoryFile);
        String path = worldName;
        // Deserialize the inventory
        List<?> inventoryList = config.getList(path + ".inventory");
        List<?> armorList = config.getList(path + ".armor");
        if (inventoryList == null || armorList == null) {
            player.sendMessage("inventory or armor haven't been found");
            return;
        }
        ItemStack[] inventoryContents = inventoryList.toArray(new ItemStack[0]);
        ItemStack[] armorContents = armorList.toArray(new ItemStack[0]);
        PlayerInventory inventory = player.getInventory();
        inventory.setContents(inventoryContents);
        inventory.setArmorContents(armorContents);
    }
}
