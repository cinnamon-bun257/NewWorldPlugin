package com.simonsplugin.plugin;

import org.bukkit.WorldCreator;
import org.bukkit.event.Listener;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class PlayerJoinHandler implements Listener{
    private JavaPlugin plugin;

    public PlayerJoinHandler(JavaPlugin plugin) {
        this.plugin = plugin;
    }
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        plugin.getLogger().info("player join recognised");
        Player player = event.getPlayer();
        File playerDataFile = new File(plugin.getDataFolder(), "playerdata.yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(playerDataFile);

        String path = player.getUniqueId().toString();
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
}

