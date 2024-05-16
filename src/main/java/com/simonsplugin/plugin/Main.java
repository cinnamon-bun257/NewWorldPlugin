package com.simonsplugin.plugin;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import java.io.File;
import java.io.IOException;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
public class Main extends JavaPlugin{

    @Override
    public void onEnable(){
        //plugin startup
        getLogger().info("New World Plugin enabled!");
        this.getCommand("newworld").setExecutor(new NewworldCommandExecutor(this));
        getServer().getPluginManager().registerEvents(new WorldChangeListener(), this);
        getServer().getPluginManager().registerEvents(new QuitWorldHandler(this), this);
        getServer().getPluginManager().registerEvents(new PlayerJoinHandler(this), this);


    }

    @Override
    public void onDisable(){
        File playerDataFile = new File(getDataFolder(), "playerdata.yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(playerDataFile);

        for (Player player : getServer().getOnlinePlayers()) {

            String path = player.getUniqueId().toString();
            config.set(path + ".world", player.getWorld().getName());
            config.set(path + ".x", player.getLocation().getX());
            config.set(path + ".y", player.getLocation().getY());
            config.set(path + ".z", player.getLocation().getZ());
            config.set(path + ".yaw", player.getLocation().getYaw());
            config.set(path + ".pitch", player.getLocation().getPitch());
        }

        try {
            config.save(playerDataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        getLogger().info("New World Plugin disabled");

    }
}