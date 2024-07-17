package com.simonsplugin.plugin;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Randomizer {
    private Map<Material, ItemStack> dropMap = new HashMap<>();
    private Random random = new Random();
    private File dataFile;
    private YamlConfiguration config;
    private JavaPlugin plugin;

    public Randomizer(JavaPlugin plugin) {
        this.dataFile = new File(plugin.getDataFolder(), "drops.yml");
        this.plugin = plugin;
        loadDrops();
    }

    private void loadDrops() {
        if (!dataFile.exists()) {
            try {
                plugin.getDataFolder().mkdirs();
                dataFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        config = YamlConfiguration.loadConfiguration(dataFile);
        for (String key : config.getKeys(false)) {
            Material mat = Material.valueOf(key);
            ItemStack stack = config.getItemStack(key);
            dropMap.put(mat, stack);
        }
    }
    public ItemStack getRandomizedItem(Material blockType){
        if (dropMap.containsKey(blockType)) {
            return dropMap.get(blockType).clone(); // Clone to avoid modifying the stored ItemStack
        } else {
            // Randomize new item type (not quantity) for this block type
            Material newItemType = Material.values()[random.nextInt(Material.values().length)];
            ItemStack newItem = new ItemStack(newItemType);
            dropMap.put(blockType, newItem);
            return newItem.clone();
        }
    }
    public void saveRandomizedDrops(){
        for (Map.Entry<Material, ItemStack> entry : dropMap.entrySet()) {
            // Saving the item stack directly into the config
            config.set(entry.getKey().name(), entry.getValue());
        }

        try {
            config.save(dataFile); // Saving the config to the file
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
