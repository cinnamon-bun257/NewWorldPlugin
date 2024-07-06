package com.simonsplugin.plugin;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Backpack {

    public boolean isOpened;
    private final Map<String, Inventory> worldInventories;
    private final Map<String, ItemStack[]> savedInventories;

    public Backpack() {
        worldInventories = new HashMap<>();
        savedInventories = new HashMap<>();
    }

    public void openBackpack(Player player) {
        String worldName = getBaseWorldName(player.getWorld().getName());
        Inventory backpackInventory = getOrCreateInventory(worldName);
        player.openInventory(backpackInventory);
        this.isOpened = true;
    }

    public void saveInventory(String worldName, ItemStack[] contents) {
        savedInventories.put(worldName, contents);
    }

    public ItemStack[] loadInventory(String worldName) {
        return savedInventories.getOrDefault(worldName, new ItemStack[27]);
    }

    public void updateInventory(String worldName) {
        Inventory backpackInventory = worldInventories.get(worldName);
        if (backpackInventory != null) {
            ItemStack[] contents = backpackInventory.getContents();
            saveInventory(worldName, contents);
        }
    }

    public Inventory getOrCreateInventory(String worldName) {
        if (!worldInventories.containsKey(worldName)) {
            Inventory backpackInventory = Bukkit.createInventory(null, 27, "Backpack (" + worldName + ")");
            ItemStack[] savedContents = loadInventory(worldName);
            backpackInventory.setContents(savedContents);
            worldInventories.put(worldName, backpackInventory);
        }
        return worldInventories.get(worldName);
    }

    public Set<String> getWorldInventories() {
        return worldInventories.keySet();
    }

    public void saveBackpacks(FileConfiguration config) {
        for (Map.Entry<String, ItemStack[]> entry : savedInventories.entrySet()) {
            String worldName = entry.getKey();
            ItemStack[] contents = entry.getValue();
            ConfigurationSection section = config.createSection(worldName);
            section.set("contents", contents);
        }
    }

    public void loadBackpacks(FileConfiguration config) {
        for (String worldName : config.getKeys(false)) {
            try {
                ConfigurationSection section = config.getConfigurationSection(worldName);
                ItemStack[] contents = section.getList("contents").toArray(new ItemStack[0]);
                savedInventories.put(worldName, contents);
            } catch (Exception e) {
                Bukkit.getLogger().severe("Failed to load backpack for world: " + worldName);
                e.printStackTrace();
            }
        }
    }

    public static String getBaseWorldName(String worldName) {
        if (worldName.endsWith("_nether")) {
            return worldName.substring(0, worldName.length() - "_nether".length());
        } else if (worldName.endsWith("_the_end")) {
            return worldName.substring(0, worldName.length() - "_the_end".length());
        } else {
            return worldName;
        }
    }
}

