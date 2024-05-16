package com.simonsplugin.plugin;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;
import java.io.File;
import java.io.IOException;

public class QuitWorldHandler implements Listener {
    private static JavaPlugin plugin;

    public QuitWorldHandler(JavaPlugin plugin) {
        this.plugin = plugin;
    }
    @EventHandler
    public void OnPlayerQuit(PlayerQuitEvent event){
        LocationUtils.saveLocation(plugin, event.getPlayer(), true);
    }

    public static void saveData(boolean loginLocation, Player player){
        File playerDataFile = new File(plugin.getDataFolder(), "locationPlayerdata.yml");
        YamlConfiguration configLocation = YamlConfiguration.loadConfiguration(playerDataFile);
        String path;
        if(loginLocation){
            path = player.getUniqueId().toString() + ".loginLocation";
        }
        else{
            path = player.getUniqueId().toString() + ".otherWorlds." + player.getWorld().getName();
        }

        configLocation.set(path + ".world", player.getWorld().getName());
        configLocation.set(path + ".x", player.getLocation().getX());
        configLocation.set(path + ".y", player.getLocation().getY());
        configLocation.set(path + ".z", player.getLocation().getZ());
        configLocation.set(path + ".yaw", player.getLocation().getYaw());
        configLocation.set(path + ".pitch", player.getLocation().getPitch());

        try {
            configLocation.save(playerDataFile);
            plugin.getLogger().warning("saved player data");
        } catch (IOException e) {
            plugin.getLogger().warning("Could not save player data for " + player.getName());
        }


        File inventoryFile = new File(plugin.getDataFolder(), player.getUniqueId().toString() + "_inventory.yml");
        YamlConfiguration configInv = YamlConfiguration.loadConfiguration(inventoryFile);

        PlayerInventory inventory = player.getInventory();
        path = player.getWorld().getName();
        configInv.set(path + ".inventory", inventory.getContents());
        configInv.set(path + ".armor", inventory.getArmorContents());

        try {
            configInv.save(inventoryFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
