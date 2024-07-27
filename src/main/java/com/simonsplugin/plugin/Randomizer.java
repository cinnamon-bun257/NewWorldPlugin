package com.simonsplugin.plugin;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Randomizer {
    private final Map<String, Map<Material, ItemStack>> worldDropMap = new HashMap<>();
    private Random random = new Random();
    private File dataFile;
    private YamlConfiguration config;
    private JavaPlugin plugin;

    public Randomizer(JavaPlugin plugin) {
        this.dataFile = new File(plugin.getDataFolder(), "drops.yml");
        this.plugin = plugin;
        this.config = YamlConfiguration.loadConfiguration(dataFile);
        loadDrops();
    }

    private void loadDrops() {
        if (!dataFile.exists()) {
            try {
                dataFile.getParentFile().mkdirs();
                dataFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        config = YamlConfiguration.loadConfiguration(dataFile);
        // Iterate over all keys in the configuration which are base world names
        for (String baseWorldName : config.getKeys(false)) {
            ConfigurationSection worldSection = config.getConfigurationSection(baseWorldName);
            if (worldSection == null) continue;  // Skip if for some reason the section is null

            Map<Material, ItemStack> dropMap = new HashMap<>();
            for (String key : worldSection.getKeys(false)) {
                Material mat = Material.valueOf(key);
                ItemStack stack = worldSection.getItemStack(key);
                if (stack != null) {
                    dropMap.put(mat, stack);
                }
            }
            worldDropMap.put(baseWorldName, dropMap);
        }
    }

    public ItemStack getRandomizedItem(Material blockType, World world) {
        String baseWorldName = Main.getBaseWorldName(world.getName());
        Map<Material, ItemStack> dropMap = worldDropMap.computeIfAbsent(baseWorldName, k -> new HashMap<>());
        if (dropMap.containsKey(blockType)) {
            return dropMap.get(blockType).clone();
        } else {
            Material newItemType = Material.values()[random.nextInt(Material.values().length)];
            while (!newItemType.isItem()){
                newItemType = Material.values()[random.nextInt(Material.values().length)];
            }
            ItemStack newItem = new ItemStack(newItemType);
            dropMap.put(blockType, newItem);
            return newItem.clone();
        }
    }

    public void saveRandomizedDrops() {
        for (Map.Entry<String, Map<Material, ItemStack>> worldEntry : worldDropMap.entrySet()) {
            String worldKey = worldEntry.getKey();
            for (Map.Entry<Material, ItemStack> entry : worldEntry.getValue().entrySet()) {
                config.set(worldKey + "." + entry.getKey().name(), entry.getValue());
            }
        }
        try {
            config.save(dataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteWorldData(String worldName){
        String baseWorldName = Main.getBaseWorldName(worldName);
        // Remove the drop data from the in-memory map
        if (worldDropMap.containsKey(baseWorldName)) {
            worldDropMap.remove(baseWorldName);
            plugin.getLogger().info("Removed drop data for world: " + baseWorldName);
        }

        // Remove the data from the YAML configuration file
        if (config.contains(baseWorldName)) {
            config.set(baseWorldName, null); // This effectively deletes the section from the file

            // Save the modified configuration back to the file
            try {
                config.save(dataFile);
                plugin.getLogger().info("Updated drops.yml to remove data for world: " + baseWorldName);
            } catch (IOException e) {
                plugin.getLogger().severe("Could not save drops.yml after deleting data for world: " + baseWorldName);
                e.printStackTrace();
            }
        }
    }
}
