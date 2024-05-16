package com.simonsplugin.plugin;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
public class PlayerDataSaver implements Listener {
    private JavaPlugin plugin;

    public PlayerDataSaver(JavaPlugin plugin) {
        this.plugin = plugin;
    }
    @EventHandler
    public void OnPlayerQuit(PlayerQuitEvent event){
        plugin.getLogger().warning("saved player data");
        Player player = event.getPlayer();
        File playerDataFile = new File(plugin.getDataFolder(), "playerdata.yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(playerDataFile);


        String path = player.getUniqueId().toString();
        config.set(path + ".world", player.getWorld().getName());
        config.set(path + ".x", player.getLocation().getX());
        config.set(path + ".y", player.getLocation().getY());
        config.set(path + ".z", player.getLocation().getZ());
        config.set(path + ".yaw", player.getLocation().getYaw());
        config.set(path + ".pitch", player.getLocation().getPitch());


        try {
            config.save(playerDataFile);
        } catch (IOException e) {
            plugin.getLogger().warning("Could not save player data for " + player.getName());
        }
    }
}
