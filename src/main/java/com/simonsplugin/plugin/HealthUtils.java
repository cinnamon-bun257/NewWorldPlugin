package com.simonsplugin.plugin;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

class HealthUtils {
  private JavaPlugin plugin;
  
  public HealthUtils(JavaPlugin plugin){
    this.plugin = plugin;
  }
  
  public void saveHealthData (Player player){
    File healthFile = new File(plugin.getDataFolder(), "healthData_" + player.getName() + ".yml");
    YamlConfiguration config = YamlConfiguration.loadConfiguration(healthFile);
    String path = Main.getBaseWorldName(player.getWorld().getName());

    config.set(path + ".health", player.getHealth());
    config.set(path + ".food", player.getFoodLevel());
    config.set(path + ".saturation", player.getSaturation());
    config.set(path + ".xpProgress", player.getExp());
    config.set(path + ".xpLevel", player.getLevel());
    config.set(path + ".gamemode", player.getGameMode().toString());
    config.set(path + ".isHardcore", player.getWorld().isHardcore());

    try{
      config.save(healthFile);

    }
    catch(IOException e){
      plugin.getLogger().warning("Could not save player Health data for player: " + player.getName());

    }

  }
  public void loadHealthData (Player player, String worldName){
    File healthFile = new File(plugin.getDataFolder(), "healthData_" + player.getName() + ".yml");
    YamlConfiguration config = YamlConfiguration.loadConfiguration(healthFile);
      if(config.contains(worldName)){
      double health = config.getDouble(worldName + ".health");
      int food = config.getInt(worldName + ".food");
      int saturation = config.getInt(worldName + ".saturation");
      float xpProgress = (float) config.getDouble(worldName + ".xpProgress");
      int xpLevel = config.getInt(worldName + ".xpLevel");
      String gamemodeStr = config.getString(worldName + ".gamemode");
      GameMode gamemode = GameMode.valueOf(gamemodeStr);
      boolean isHardcore = config.getBoolean(worldName + ".isHardcore");
      player.setHealth(health);
      player.setFoodLevel(food);
      player.setSaturation(saturation);
      player.setExp(xpProgress);
      player.setLevel(xpLevel);
      player.setGameMode(gamemode);
      player.getWorld().setHardcore(isHardcore);

    }
    else plugin.getLogger().warning("could not load Health data of player: " + player.getName());
  }
  
  
  
  
}