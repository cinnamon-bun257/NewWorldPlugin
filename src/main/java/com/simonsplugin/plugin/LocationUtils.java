package com.simonsplugin.plugin;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class LocationUtils {
    public static void setLocation(JavaPlugin plugin, Player player) {
        File locationPlayerDataFile = new File(plugin.getDataFolder(), "locationPlayerdata.yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(locationPlayerDataFile);
        String path = player.getUniqueId().toString() + ".loginLocation";
        if (config.contains(path)) {
            plugin.getLogger().info("playerdata found");
            String worldName = config.getString(path + ".world");
            World world = Bukkit.getWorld(worldName);
            if (world == null) {
                plugin.getLogger().info("World '" + worldName + "' not found, loading...");
                world = Bukkit.createWorld(new WorldCreator(worldName));  // Load the world
                if (world == null) {
                    plugin.getLogger().warning("Failed to load world '" + worldName + "'.");
                    return;
                }
            }
            double x = config.getDouble(path + ".x");
            double y = config.getDouble(path + ".y");
            double z = config.getDouble(path + ".z");
            float yaw = (float) config.getDouble(path + ".yaw");
            float pitch = (float) config.getDouble(path + ".pitch");

            Location location = new Location(world, x, y, z, yaw, pitch);
            player.teleport(location);
            plugin.getLogger().info("player put in right world");
        }
    }
        public static void setLocation (JavaPlugin plugin, Player player, String worldName){
            File locationPlayerDataFile = new File(plugin.getDataFolder(), "locationPlayerdata.yml");
            YamlConfiguration config = YamlConfiguration.loadConfiguration(locationPlayerDataFile);
            String path = player.getUniqueId().toString() + ".otherWorlds." + worldName;
            if (config.contains(path)) {
                plugin.getLogger().info("playerdata found");
                World world = Bukkit.getWorld(worldName);
                if (world == null) {
                    plugin.getLogger().info("World '" + worldName + "' not found, loading...");
                    world = Bukkit.createWorld(new WorldCreator(worldName));  // Load the world
                    if (world == null) {
                        plugin.getLogger().warning("Failed to load world '" + worldName + "'.");
                        return;
                    }
                }
                double x = config.getDouble(path + ".x");
                double y = config.getDouble(path + ".y");
                double z = config.getDouble(path + ".z");
                float yaw = (float) config.getDouble(path + ".yaw");
                float pitch = (float) config.getDouble(path + ".pitch");

                Location location = new Location(world, x, y, z, yaw, pitch);
                player.teleport(location);
                plugin.getLogger().info("player put in right world");

            }
        }

        public static void saveLocation (JavaPlugin plugin, Player player, String worldName, boolean isLoginLocation){
            File playerDataFile = new File(plugin.getDataFolder(), "locationPlayerdata.yml");
            YamlConfiguration config = YamlConfiguration.loadConfiguration(playerDataFile);
            String path;
            if(isLoginLocation){
                path = player.getUniqueId().toString() + ".loginLocation";
            }
            else{
                path = player.getUniqueId().toString() + ".otherWorlds." + worldName;
            }

            config.set(path + ".world", player.getWorld().getName());
            config.set(path + ".x", player.getLocation().getX());
            config.set(path + ".y", player.getLocation().getY());
            config.set(path + ".z", player.getLocation().getZ());
            config.set(path + ".yaw", player.getLocation().getYaw());
            config.set(path + ".pitch", player.getLocation().getPitch());

            try {
                config.save(playerDataFile);
                plugin.getLogger().warning("saved player data");
            } catch (IOException e) {
                plugin.getLogger().warning("Could not save player data for " + player.getName());
            }
        }
    }
