package com.simonsplugin.plugin;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class PluginConfig {
    private File pluginConfigFile;
    private FileConfiguration pluginConfigFileConfiguration;
    public static boolean isHardcore;
    public PluginConfig(JavaPlugin plugin){
        this.pluginConfigFile = new File(plugin.getDataFolder(), "pluginConfig.yml");
        if(!pluginConfigFile.exists()){
            pluginConfigFile.getParentFile().mkdirs();
            plugin.saveResource("pluginConfig.yml", false);
        }
        this.pluginConfigFileConfiguration = YamlConfiguration.loadConfiguration(pluginConfigFile);
    }
    public void loadPluginConfig(){
        this.isHardcore = this.pluginConfigFileConfiguration.getBoolean(".isHardcore");
    }
    public void savePluginConfig(){
        if(pluginConfigFile!= null && pluginConfigFileConfiguration != null){
            pluginConfigFileConfiguration.set(".isHardcore", this.isHardcore);

        }
    }
}
