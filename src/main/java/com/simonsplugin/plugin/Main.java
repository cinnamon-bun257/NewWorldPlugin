package com.simonsplugin.plugin;

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
    private PluginConfig pluginConfig;
    private LocationUtils locationUtils;
    private  AdvancementUtils advancementUtils;
    private HealthUtils healthUtils;
    private InventoryUtils inventoryUtils;


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

        this.pluginConfig = new PluginConfig(this);
        this.advancementUtils = new AdvancementUtils();
        this.healthUtils = new HealthUtils(this);
        this.locationUtils = new LocationUtils();
        this.inventoryUtils = new InventoryUtils();

        getLogger().info("New World Plugin enabled!");
        Objects.requireNonNull(this.getCommand("newworld")).setExecutor(new NewworldCommandExecutor(this, pluginConfig, locationUtils, advancementUtils, healthUtils, inventoryUtils ));
        Objects.requireNonNull(this.getCommand("changeworld")).setExecutor(new ChangeWorldCommandExecutor(this, locationUtils, advancementUtils, healthUtils, inventoryUtils ));
        Objects.requireNonNull(this.getCommand("sethardcore")).setExecutor(new PluginConfigCommandExecutor(pluginConfig));
        Objects.requireNonNull(this.getCommand("bp")).setExecutor((new BackpackCommandExecutor(backpack)));
        Objects.requireNonNull(this.getCommand("setminerandomizer")).setExecutor(new PluginConfigCommandExecutor(pluginConfig));


        getServer().getPluginManager().registerEvents(new QuitWorldHandler(this, locationUtils), this);
        getServer().getPluginManager().registerEvents(new PlayerJoinHandler(this, locationUtils), this);
        getServer().getPluginManager().registerEvents(new CustomPortalListener(this), this);
        //getServer().getPluginManager().registerEvents(new AdvancementListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerDeathListener(this), this);
        getServer().getPluginManager().registerEvents(new BackpackListener(backpack), this);
        getServer().getPluginManager().registerEvents(new RandomizerListener(randomizer, pluginConfig), this);

    }

    @Override
    public void onDisable(){
        getLogger().info("NewWorldPlugin is being disabled...");

        for (Player p: this.getServer().getOnlinePlayers()){
            locationUtils.saveLocation(this, p, true);
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
        pluginConfig.savePluginConfig();
        getLogger().info("New World Plugin disabled");

    }
    public static String getBaseWorldName(String worldName) {
        if (worldName.contains("_nether")) {
            return worldName.replace("_nether", "");
        } else if (worldName.contains("_the_end")) {
            return worldName.replace("_the_end", "");
        }
        return worldName;  // Return base world name
    }
}
