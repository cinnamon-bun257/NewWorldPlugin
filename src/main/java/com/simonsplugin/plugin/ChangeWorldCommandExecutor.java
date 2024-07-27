package com.simonsplugin.plugin;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class ChangeWorldCommandExecutor implements CommandExecutor {

    private JavaPlugin plugin;
    private LocationUtils locationUtils;
    private  AdvancementUtils advancementUtils;
    private HealthUtils healthUtils;
    private InventoryUtils inventoryUtils;


    public ChangeWorldCommandExecutor(JavaPlugin plugin, LocationUtils locationUtils, AdvancementUtils advancementUtils, HealthUtils healthUtils, InventoryUtils inventoryUtils ) {
        this.plugin = plugin;
        this.advancementUtils = advancementUtils;
        this.healthUtils = healthUtils;
        this.inventoryUtils = inventoryUtils;
        this.locationUtils = locationUtils;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be run by a player.");
            return true;
        }

        Player player = (Player) sender;
        if (!player.hasPermission("newworld.use")) {
            player.sendMessage("You do not have permission to perform this command.");
            return true;
        }

        if (args.length == 0) {
            player.sendMessage("Usage: /changeworld <name of the world>");
            return true;
        }

        String baseWorldName = args[0];
        sender.sendMessage("worldName:" + baseWorldName + "|");
        for(Player p: Bukkit.getServer().getOnlinePlayers()){
            // Ensuring all save operations are complete before starting load operations
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                savePlayerData(p);

                // Ensuring all load operations run on the main thread after saving
                Bukkit.getScheduler().runTask(plugin, () -> {
                    loadPlayerData(p, baseWorldName);
                });
            });
        }
        return true;
    }

    private void savePlayerData(Player player) {
        try {
            locationUtils.saveLocation(plugin, player, false);
            inventoryUtils.saveInventory(plugin, player);
            advancementUtils.saveAdvancements(plugin, player);
            healthUtils.saveHealthData( player);
            player.sendMessage("Data saved successfully.");
        } catch (Exception e) {
            player.sendMessage("Error saving data.");
            e.printStackTrace();
        }
    }

    private void loadPlayerData(Player player, String worldName) {
        try {
            locationUtils.setLocation(plugin, player, worldName, false);
            inventoryUtils.setInventory(plugin, player, worldName);
            advancementUtils.loadAdvancements(plugin, player, worldName);
            healthUtils.loadHealthData( player, worldName);
            player.sendMessage("Data loaded for world: " + worldName);
        } catch (Exception e) {
            player.sendMessage("Error loading data for world: " + worldName);
            e.printStackTrace();
        }
    }
}
