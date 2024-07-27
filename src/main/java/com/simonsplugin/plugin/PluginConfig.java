package com.simonsplugin.plugin;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class PluginConfig {
    private File pluginConfigFile;
    private FileConfiguration pluginConfigFileConfiguration;
    public boolean isHardcore;
    public boolean doMineRandomizing;
    public PluginConfig(JavaPlugin plugin){
        this.pluginConfigFile = new File(plugin.getDataFolder(), "pluginConfig.yml");
        if(!pluginConfigFile.exists()){
            pluginConfigFile.getParentFile().mkdirs();
            plugin.saveResource("pluginConfig.yml", false);
        }
        this.pluginConfigFileConfiguration = YamlConfiguration.loadConfiguration(pluginConfigFile);
        loadPluginConfig();
    }
    private void loadPluginConfig(){

        isHardcore = this.pluginConfigFileConfiguration.getBoolean(".isHardcore");
        doMineRandomizing = this.pluginConfigFileConfiguration.getBoolean(".doMineRandomizing");
    }
    public void savePluginConfig(){
        if(pluginConfigFile!= null && pluginConfigFileConfiguration != null){
            pluginConfigFileConfiguration.set(".isHardcore", isHardcore);
            pluginConfigFileConfiguration.set(".doMineRandomizing", doMineRandomizing);
        }
    }
}
