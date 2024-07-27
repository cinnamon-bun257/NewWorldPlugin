package com.simonsplugin.plugin;

import org.bukkit.*;
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

    public String getLoginWorld(JavaPlugin plugin, Player player) {
        File locationPlayerDataFile = new File(plugin.getDataFolder(), "locationPlayerdata.yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(locationPlayerDataFile);
        return config.getString(player.getName() + ".loginLocation.baseWorldName");
    }

    public void setLocation(JavaPlugin plugin, Player player, String baseWorldName, boolean isLogin) {
        File locationPlayerDataFile = new File(plugin.getDataFolder(), "locationPlayerdata.yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(locationPlayerDataFile);
        String path;
        if(!isLogin) path = player.getUniqueId() + "." + baseWorldName;
        else path= player.getUniqueId() + ".loginLocation";
        if (config.contains(path)) {
            plugin.getLogger().info("playerlocationdata found");
            String worldName = config.getString(path + ".world");
            if (worldName == null) {
                plugin.getLogger().warning("world not found");
                return;
            }
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
            player.setRespawnLocation((Location) config.get(path + ".respawnPoint"));

            Location location = new Location(world, x, y, z, yaw, pitch);
            player.teleport(location);
            plugin.getLogger().info("player put in right world");

        } else plugin.getLogger().warning("Base World not found");
    }

    public void saveLocation(JavaPlugin plugin, Player player, boolean isLoginLocation) {
        File playerDataFile = new File(plugin.getDataFolder(), "locationPlayerdata.yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(playerDataFile);
        String path;
        String baseWorldName = Main.getBaseWorldName(player.getWorld().getName());
        if (isLoginLocation) {
            path = player.getUniqueId() + ".loginLocation";
            config.set(path + ".baseWorldName", baseWorldName);
        } else {
            path = player.getUniqueId() + "." + baseWorldName;
        }

        config.set(path + ".world", player.getWorld().getName());
        config.set(path + ".x", player.getLocation().getX());
        config.set(path + ".y", player.getLocation().getY());
        config.set(path + ".z", player.getLocation().getZ());
        config.set(path + ".yaw", player.getLocation().getYaw());
        config.set(path + ".pitch", player.getLocation().getPitch());
        if(player.getRespawnLocation()!= null) config.set(path + ".respawnPoint", player.getWorld().getSpawnLocation());
        else config.set(path + ".respawnPoint", player.getRespawnLocation());

        try {
            config.save(playerDataFile);
            plugin.getLogger().warning("saved player data");
        } catch (IOException e) {
            plugin.getLogger().warning("Could not save player data for " + player.getName());
        }
    }

    public void teleportPlayersToNewWorld(JavaPlugin plugin, Location location) {
        Bukkit.getScheduler().runTask(plugin, () -> {
            for (Player p : Bukkit.getServer().getOnlinePlayers()) {
                p.setHealth(20.0); // 20 health points is equivalent to 10 hearts
                p.setFoodLevel(20); // 20 is the max food level, fully satiated
                p.setLevel(0);
                p.setExp(0.0f);
                p.getInventory().clear();
                p.getActivePotionEffects().forEach(potionEffect ->
                        p.removePotionEffect(potionEffect.getType())
                );
                p.setGameMode(GameMode.SURVIVAL);
                p.setRespawnLocation(location, true);
                p.teleport(location);
            }
        });
    }
}
