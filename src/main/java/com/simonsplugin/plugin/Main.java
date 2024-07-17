package com.simonsplugin.plugin;


import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class Main extends JavaPlugin{
    private Backpack backpack;
    private File backpackFile;
    private FileConfiguration backpackConfig;

    private Randomizer randomizer;

    @Override
    public void onEnable() {
        // Initialize the shared backpack
        backpack = new Backpack();

        // Load backpacks from file
        backpackFile = new File(getDataFolder(), "backpacks.yml");
        if (!backpackFile.exists()) {
            backpackFile.getParentFile().mkdirs();
            saveResource("backpacks.yml", false);
        }
        backpackConfig = YamlConfiguration.loadConfiguration(backpackFile);
        backpack.loadBackpacks(backpackConfig);

        this.randomizer = new Randomizer(this);

        getLogger().info("New World Plugin enabled!");
        Objects.requireNonNull(this.getCommand("newworld")).setExecutor(new NewworldCommandExecutor(this));
        Objects.requireNonNull(this.getCommand("changeworld")).setExecutor(new ChangeWorldCommandExecutor(this));
        Objects.requireNonNull(this.getCommand("sethardcore")).setExecutor(new PluginConfigCommandExecutor());
        Objects.requireNonNull(this.getCommand("bp")).setExecutor((new BackpackCommandExecutor(backpack)));


        getServer().getPluginManager().registerEvents(new QuitWorldHandler(this), this);
        getServer().getPluginManager().registerEvents(new PlayerJoinHandler(this), this);
        getServer().getPluginManager().registerEvents(new CustomPortalListener(this), this);
        //getServer().getPluginManager().registerEvents(new AdvancementListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerDeathListener(this), this);
        getServer().getPluginManager().registerEvents(new BackpackListener(backpack), this);
        getServer().getPluginManager().registerEvents(new RandomizerListener(randomizer), this);

    }

    @Override
    public void onDisable(){
        getLogger().info("NewWorldPlugin is being disabled...");

        for (Player p: this.getServer().getOnlinePlayers()){
            LocationUtils.saveLocation(this, p, true);
        }
        if (backpackConfig != null && backpackFile != null) {
            try {
                // Save backpacks to file
                backpack.saveBackpacks(backpackConfig);
                backpackConfig.save(backpackFile);
                getLogger().info("NewWorldPlugin has been disabled successfully.");
            } catch (IOException e) {
                getLogger().severe("Failed to save backpacks: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            getLogger().warning("Backpack config or file is null, skipping save.");
        }

        randomizer.saveRandomizedDrops();
        getLogger().info("New World Plugin disabled");

    }
}
